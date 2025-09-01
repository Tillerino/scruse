package org.tillerino.scruse.processor.apis;

import static org.tillerino.scruse.processor.Snippet.join;
import static org.tillerino.scruse.processor.Snippet.of;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock.Builder;
import jakarta.annotation.Nullable;
import java.io.IOException;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.helpers.JakartaJsonParserHelper;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public class JakartaJsonParserGenerator extends AbstractReaderGenerator<JakartaJsonParserGenerator> {
    private final VariableElement parserVariable;

    public JakartaJsonParserGenerator(
            AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        super(utils, prototype, generatedClass);
        parserVariable = prototype.methodElement().getParameters().get(0);
    }

    public JakartaJsonParserGenerator(
            ScrusePrototype prototype,
            AnnotationProcessorUtils utils,
            Type type,
            Property property,
            Builder code,
            VariableElement parserVariable,
            LHS lhs,
            JakartaJsonParserGenerator parent,
            boolean stackRelevantType,
            AnyConfig config) {
        super(utils, parent.generatedClass, prototype, code, parent, type, stackRelevantType, property, lhs, config);
        this.parserVariable = parserVariable;
    }

    @Override
    protected void startStringCase(Branch branch) {
        branch.controlFlow(this, "$L.currentEvent() == $L", parserVariable.getSimpleName(), token("VALUE_STRING"));
    }

    @Override
    protected void startNumberCase(Branch branch) {
        branch.controlFlow(this, "$L.currentEvent() == VALUE_NUMBER", parserVariable.getSimpleName());
    }

    @Override
    protected Snippet objectCaseCondition() {
        importHelper();
        return Snippet.of("nextIfCurrentTokenIs($L, $L)", parserVariable.getSimpleName(), token("START_OBJECT"));
    }

    @Override
    protected void startArrayCase(Branch branch) {
        branch.controlFlow(this, "$L.currentEvent() == $L", parserVariable.getSimpleName(), token("START_ARRAY"));
        advance();
    }

    @Override
    protected void startBooleanCase(Branch branch) {
        branch.controlFlow(
                this,
                "$L.currentEvent() == VALUE_TRUE || $L.currentEvent() == VALUE_FALSE",
                parserVariable.getSimpleName(),
                parserVariable.getSimpleName());
    }

    @Override
    protected void startFieldCase(Branch branch) {
        branch.controlFlow(this, "$L.currentEvent() == $L", parserVariable.getSimpleName(), token("KEY_NAME"));
    }

    @Override
    protected void initializeParser() {
        beginControlFlow("if ($L.currentEvent() == null)", parserVariable.getSimpleName());
        advance();
        endControlFlow();
    }

    @Override
    protected Snippet nullCaseCondition() {
        importHelper();
        return Snippet.of("nextIfCurrentTokenIs($L, $L)", parserVariable.getSimpleName(), token("VALUE_NULL"));
    }

    @Override
    protected void readPrimitive(TypeMirror type) {
        Snippet method =
                switch (type.getKind()) {
                    case BOOLEAN -> Snippet.of("$L.currentEvent() == VALUE_TRUE", parserVariable);
                    case BYTE -> Snippet.of("(byte) $L.getInt()", parserVariable);
                    case SHORT -> Snippet.of("(short) $L.getInt()", parserVariable);
                    case INT -> Snippet.of("$L.getInt()", parserVariable);
                    case LONG -> Snippet.of("$L.getLong()", parserVariable);
                    case FLOAT -> Snippet.of(
                            "(float) (($T) $L.getValue()).doubleValue()", jsonNumber(), parserVariable);
                    case DOUBLE -> Snippet.of("(($T) $L.getValue()).doubleValue()", jsonNumber(), parserVariable);
                    default -> throw new ContextedRuntimeException(
                            type.getKind().toString());
                };
        if (lhs instanceof LHS.Return) {
            String tmp = createVariable("tmp").name();
            addStatement(Snippet.of("$T $L = $C", type, tmp, method));
            advance();
            addStatement("return $L", tmp);
        } else {
            addStatement(lhs.assign(method));
            advance();
        }
    }

    @Override
    protected void readString(StringKind stringKind) {
        String conversion =
                switch (stringKind) {
                    case STRING -> "";
                    case CHAR_ARRAY -> ".toCharArray()";
                };
        if (lhs instanceof LHS.Return) {
            String tmp = createVariable("tmp").name();
            addStatement(
                    "$T $L = $L.getString()$L",
                    stringKind == StringKind.STRING ? String.class : char[].class,
                    tmp,
                    parserVariable.getSimpleName(),
                    conversion);
            advance();
            addStatement("return $L", tmp);
        } else {
            addStatement(lhs.assign("$L.getString()$L", parserVariable.getSimpleName(), conversion));
            advance();
        }
    }

    @Override
    protected void iterateOverFields() {
        importHelper();
        // we immediately skip the END_OBJECT token once we encounter it
        beginControlFlow("while (!nextIfCurrentTokenIs($L, $L))", parserVariable.getSimpleName(), token("END_OBJECT"));
    }

    @Override
    protected void skipValue() {
        importHelper();
        addStatement("skip($L)", parserVariable.getSimpleName());
        advance();
    }

    @Override
    protected void afterObject() {}

    @Override
    protected void readFieldNameInIteration(String propertyName) {
        addStatement("String $L = $L.getString()", propertyName, parserVariable.getSimpleName());
        advance();
    }

    @Override
    protected void readDiscriminator(String propertyName) {
        importHelper();
        addStatement(lhs.assign("readDiscriminator($S, $L)", propertyName, parserVariable.getSimpleName()));
    }

    @Override
    protected void iterateOverElements() {
        importHelper();
        // we immediately skip the END_ARRAY token once we encounter it
        beginControlFlow("while (!nextIfCurrentTokenIs($L, $L))", parserVariable.getSimpleName(), token("END_ARRAY"));
    }

    @Override
    protected void afterArray() {
        // we skipped the END_ARRAY token in the head of the loop
    }

    @Override
    protected void throwUnexpected(String expected) {
        addStatement(
                "throw new $T($S + $L.currentEvent() + $S + $L.getLocation())",
                IOException.class,
                "Expected " + expected + ", got ",
                parserVariable.getSimpleName(),
                " at ",
                parserVariable.getSimpleName());
    }

    @Override
    protected void invokeDelegate(String instance, InstantiatedMethod callee) {
        addStatement(lhs.assign(of(
                "$L.$L($C)",
                instance,
                callee,
                join(utils.delegation.findArguments(prototype, callee, 0, generatedClass), ", "))));
    }

    @Override
    protected JakartaJsonParserGenerator nest(
            TypeMirror type, @Nullable Property property, LHS lhs, boolean stackRelevantType, AnyConfig config) {
        return new JakartaJsonParserGenerator(
                prototype,
                utils,
                utils.tf.getType(type),
                property,
                code,
                parserVariable,
                lhs,
                this,
                stackRelevantType,
                config);
    }

    private Class<JakartaJsonParserHelper> importHelper() {
        generatedClass.fileBuilderMods.add(builder -> builder.addStaticImport(JakartaJsonParserHelper.class, "*"));
        return JakartaJsonParserHelper.class;
    }

    private ClassName jsonNumber() {
        return ClassName.get("jakarta.json", "JsonNumber");
    }

    private String token(String t) {
        generatedClass.fileBuilderMods.add(
                builder -> builder.addStaticImport(ClassName.get("jakarta.json.stream.JsonParser", "Event"), "*"));
        return t;
    }

    private void advance() {
        addStatement("$L.next()", parserVariable.getSimpleName());
    }
}
