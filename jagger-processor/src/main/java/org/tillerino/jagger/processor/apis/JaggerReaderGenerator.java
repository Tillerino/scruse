package org.tillerino.jagger.processor.apis;

import static org.tillerino.jagger.api.JaggerReader.Advance.CONSUME;
import static org.tillerino.jagger.processor.Snippet.join;
import static org.tillerino.jagger.processor.Snippet.of;

import com.squareup.javapoet.ClassName;
import jakarta.annotation.Nonnull;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.jagger.api.JaggerReader;
import org.tillerino.jagger.processor.AnnotationProcessorUtils;
import org.tillerino.jagger.processor.GeneratedClass;
import org.tillerino.jagger.processor.JaggerPrototype;
import org.tillerino.jagger.processor.Snippet;
import org.tillerino.jagger.processor.config.AnyConfig;
import org.tillerino.jagger.processor.util.InstantiatedMethod;

public class JaggerReaderGenerator extends AbstractReaderGenerator<JaggerReaderGenerator> {
    private final VariableElement parserVariable;

    public JaggerReaderGenerator(
            AnnotationProcessorUtils utils, JaggerPrototype prototype, GeneratedClass generatedClass) {
        super(utils, prototype, generatedClass);
        parserVariable = prototype.methodElement().getParameters().get(0);
    }

    public JaggerReaderGenerator(
            Type type,
            Property property,
            LHS lhs,
            @Nonnull JaggerReaderGenerator parent,
            boolean stackRelevantType,
            AnyConfig config) {
        super(parent, type, stackRelevantType, property, lhs, config);
        this.parserVariable = parent.parserVariable;
    }

    @Override
    protected void startStringCase(Branch branch) {
        branch.controlFlow(this, "$L.isText()", parserVariable.getSimpleName());
    }

    @Override
    protected void startNumberCase(Branch branch) {
        branch.controlFlow(this, "$L.isNumber()", parserVariable.getSimpleName());
    }

    @Override
    protected Snippet objectCaseCondition() {
        return Snippet.of("$L.isObjectStart($L)", parserVariable.getSimpleName(), importAdvance(CONSUME));
    }

    @Override
    protected void startArrayCase(Branch branch) {
        branch.controlFlow(this, "$L.isArrayStart($L)", parserVariable.getSimpleName(), importAdvance(CONSUME));
    }

    @Override
    protected void startBooleanCase(Branch branch) {
        branch.controlFlow(this, "$L.isBoolean()", parserVariable.getSimpleName());
    }

    @Override
    protected void startFieldCase(Branch branch) {
        branch.controlFlow(this, "$L.isFieldName()", parserVariable.getSimpleName());
    }

    @Override
    protected void initializeParser() {
        // nothing to do, reader always starts with a token, yay!
    }

    @Override
    protected Snippet nullCaseCondition() {
        return Snippet.of("$L.isNull($L)", parserVariable.getSimpleName(), importAdvance(CONSUME));
    }

    @Override
    protected void readPrimitive(TypeMirror type) {
        String method =
                switch (type.getKind()) {
                    case BOOLEAN -> "getBoolean";
                    case BYTE -> "getByte";
                    case SHORT -> "getShort";
                    case INT -> "getInt";
                    case LONG -> "getLong";
                    case FLOAT -> "getFloat";
                    case DOUBLE -> "getDouble";
                    default -> throw new ContextedRuntimeException(
                            type.getKind().toString());
                };
        Snippet snippet = of("$L.$L($L)", parserVariable.getSimpleName(), method, importAdvance(CONSUME));
        addStatement(lhs.assign(snippet));
    }

    @Override
    protected void readString(StringKind stringKind) {
        String conversion =
                switch (stringKind) {
                    case STRING -> "";
                    case CHAR_ARRAY -> ".toCharArray()";
                };
        Snippet snippet = of("$L.getText($L)$L", parserVariable.getSimpleName(), importAdvance(CONSUME), conversion);
        addStatement(lhs.assign(snippet));
    }

    @Override
    protected void iterateOverFields() {
        beginControlFlow("while (!$L.isObjectEnd($L))", parserVariable.getSimpleName(), importAdvance(CONSUME));
    }

    @Override
    protected void skipValue() {
        addStatement("$L.skipChildren($L)", parserVariable.getSimpleName(), importAdvance(CONSUME));
    }

    @Override
    protected void afterObject() {}

    @Override
    protected void readFieldNameInIteration(String propertyName) {
        addStatement(
                "String $L = $L.getFieldName($L)",
                propertyName,
                parserVariable.getSimpleName(),
                importAdvance(CONSUME));
    }

    @Override
    protected void readDiscriminator(String propertyName) {
        addStatement(lhs.assign("$L.getDiscriminator($S, false)", parserVariable.getSimpleName(), propertyName));
    }

    @Override
    protected void iterateOverElements() {
        beginControlFlow("while (!$L.isArrayEnd($L))", parserVariable.getSimpleName(), importAdvance(CONSUME));
    }

    @Override
    protected void afterArray() {
        // we skipped the END_ARRAY token in the head of the loop
    }

    @Override
    protected void throwUnexpected(String expectedToken) {
        addStatement("throw $L.unexpectedToken($S)", parserVariable.getSimpleName(), expectedToken);
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
    protected JaggerReaderGenerator nest(
            TypeMirror type, Property property, LHS lhs, boolean stackRelevantType, AnyConfig config) {
        return new JaggerReaderGenerator(utils.tf.getType(type), property, lhs, this, stackRelevantType, config);
    }

    private String importAdvance(JaggerReader.Advance advance) {
        generatedClass.fileBuilderMods.add(
                builder -> builder.addStaticImport(ClassName.get(JaggerReader.Advance.class), "*"));
        return advance.name();
    }
}
