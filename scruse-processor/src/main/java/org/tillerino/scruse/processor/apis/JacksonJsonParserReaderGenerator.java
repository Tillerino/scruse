package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import java.io.IOException;
import javax.annotation.Nullable;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.helpers.JacksonJsonParserReaderHelper;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public class JacksonJsonParserReaderGenerator extends AbstractReaderGenerator<JacksonJsonParserReaderGenerator> {
    private final VariableElement parserVariable;

    public JacksonJsonParserReaderGenerator(
            AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        super(
                utils,
                generatedClass,
                prototype,
                CodeBlock.builder(),
                null,
                utils.tf.getType(prototype.instantiatedReturnType()),
                true,
                null,
                new LHS.Return());
        parserVariable = prototype.methodElement().getParameters().get(0);
    }

    public JacksonJsonParserReaderGenerator(
            ScrusePrototype prototype,
            AnnotationProcessorUtils utils,
            Type type,
            String propertyName,
            CodeBlock.Builder code,
            VariableElement parserVariable,
            LHS lhs,
            JacksonJsonParserReaderGenerator parent,
            boolean stackRelevantType) {
        super(utils, parent.generatedClass, prototype, code, parent, type, stackRelevantType, propertyName, lhs);
        this.parserVariable = parserVariable;
    }

    @Override
    protected void startStringCase(Branch branch) {
        branch.controlFlow(code, "$L.currentToken() == $L", parserVariable.getSimpleName(), token("VALUE_STRING"));
    }

    @Override
    protected void startNumberCase(Branch branch) {
        branch.controlFlow(code, "$L.currentToken().isNumeric()", parserVariable.getSimpleName());
    }

    @Override
    protected Snippet objectCaseCondition() {
        importHelper();
        return Snippet.of("nextIfCurrentTokenIs($L, $L)", parserVariable.getSimpleName(), token("START_OBJECT"));
    }

    @Override
    protected void startArrayCase(Branch branch) {
        branch.controlFlow(code, "$L.currentToken() == $L", parserVariable.getSimpleName(), token("START_ARRAY"));
        advance();
    }

    @Override
    protected void startBooleanCase(Branch branch) {
        branch.controlFlow(code, "$L.currentToken().isBoolean()", parserVariable.getSimpleName());
    }

    @Override
    protected void startFieldCase(Branch branch) {
        branch.controlFlow(code, "$L.currentToken() == $L", parserVariable.getSimpleName(), token("FIELD_NAME"));
    }

    @Override
    protected void initializeParser() {
        code.beginControlFlow("if (!$L.hasCurrentToken())", parserVariable.getSimpleName());
        advance();
        code.endControlFlow();
    }

    @Override
    protected Snippet nullCaseCondition() {
        importHelper();
        return Snippet.of("nextIfCurrentTokenIs($L, $L)", parserVariable.getSimpleName(), token("VALUE_NULL"));
    }

    @Override
    protected void readPrimitive(TypeMirror type) {
        String readMethod =
                switch (type.getKind()) {
                    case BOOLEAN -> "getBooleanValue";
                    case BYTE -> "getByteValue";
                    case SHORT -> "getShortValue";
                    case INT -> "getIntValue";
                    case LONG -> "getLongValue";
                    case FLOAT -> "getFloatValue";
                    case DOUBLE -> "getDoubleValue";
                    default -> throw new AssertionError(type.getKind());
                };
        if (lhs instanceof LHS.Return) {
            String tmp = "tmp$" + stackDepth();
            code.addStatement("$T $L = $L.$L()", type, tmp, parserVariable.getSimpleName(), readMethod);
            advance();
            code.addStatement("return $L", tmp);
        } else {
            lhs.assign(code, "$L.$L()", parserVariable.getSimpleName(), readMethod);
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
            String tmp = "tmp$" + stackDepth();
            code.addStatement(
                    "$T $L = $L.getText()$L",
                    stringKind == StringKind.STRING ? String.class : char[].class,
                    tmp,
                    parserVariable.getSimpleName(),
                    conversion);
            advance();
            code.addStatement("return $L", tmp);
        } else {
            lhs.assign(code, "$L.getText()$L", parserVariable.getSimpleName(), conversion);
            advance();
        }
    }

    @Override
    protected void iterateOverFields() {
        importHelper();
        // we immediately skip the END_OBJECT token once we encounter it
        code.beginControlFlow(
                "while (!nextIfCurrentTokenIs($L, $L))", parserVariable.getSimpleName(), token("END_OBJECT"));
    }

    @Override
    protected void afterObject() {}

    @Override
    protected void readFieldNameInIteration(String propertyName) {
        code.addStatement("String $L = $L.currentName()", propertyName, parserVariable.getSimpleName());
        advance();
    }

    @Override
    protected void readDiscriminator(String propertyName) {
        importHelper();
        lhs.assign(code, "readDiscriminator($S, $L)", propertyName, parserVariable.getSimpleName());
    }

    @Override
    protected void iterateOverElements() {
        importHelper();
        // we immediately skip the END_ARRAY token once we encounter it
        code.beginControlFlow(
                "while (!nextIfCurrentTokenIs($L, $L))", parserVariable.getSimpleName(), token("END_ARRAY"));
    }

    @Override
    protected void afterArray() {
        // we skipped the END_ARRAY token in the head of the loop
    }

    @Override
    protected void throwUnexpected(String expected) {
        code.addStatement(
                "throw new $T($S + $L.currentToken() + $S + $L.getCurrentLocation())",
                IOException.class,
                "Expected " + expected + ", got ",
                parserVariable.getSimpleName(),
                " at ",
                parserVariable.getSimpleName());
    }

    @Override
    protected void invokeDelegate(String instance, InstantiatedMethod callee) {
        lhs.assign(
                code,
                Snippet.of(
                        "$L.$L($C)",
                        instance,
                        callee,
                        Snippet.join(prototype.findArguments(callee, 0, generatedClass), ", ")));
    }

    @Override
    protected JacksonJsonParserReaderGenerator nest(
            TypeMirror type, @Nullable String propertyName, LHS lhs, boolean stackRelevantType) {
        return new JacksonJsonParserReaderGenerator(
                prototype,
                utils,
                utils.tf.getType(type),
                propertyName,
                code,
                parserVariable,
                lhs,
                this,
                stackRelevantType);
    }

    private Class<JacksonJsonParserReaderHelper> importHelper() {
        generatedClass.fileBuilderMods.add(
                builder -> builder.addStaticImport(JacksonJsonParserReaderHelper.class, "*"));
        return JacksonJsonParserReaderHelper.class;
    }

    private String token(String t) {
        generatedClass.fileBuilderMods.add(
                builder -> builder.addStaticImport(ClassName.get("com.fasterxml.jackson.core", "JsonToken"), "*"));
        return t;
    }

    private void advance() {
        code.addStatement("$L.nextToken()", parserVariable.getSimpleName());
    }
}
