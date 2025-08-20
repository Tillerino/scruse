package org.tillerino.scruse.processor.apis;

import static org.tillerino.scruse.api.ScruseReader.Advance.CONSUME;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.api.ScruseReader;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public class ScruseReaderGenerator extends AbstractReaderGenerator<ScruseReaderGenerator> {
    private final VariableElement parserVariable;

    public ScruseReaderGenerator(
            AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        super(utils, prototype, generatedClass);
        parserVariable = prototype.methodElement().getParameters().get(0);
    }

    public ScruseReaderGenerator(
            ScrusePrototype prototype,
            AnnotationProcessorUtils utils,
            Type type,
            String propertyName,
            CodeBlock.Builder code,
            VariableElement parserVariable,
            LHS lhs,
            ScruseReaderGenerator parent,
            boolean stackRelevantType,
            AnyConfig config) {
        super(
                utils,
                parent.generatedClass,
                prototype,
                code,
                parent,
                type,
                stackRelevantType,
                propertyName,
                lhs,
                config);
        this.parserVariable = parserVariable;
    }

    @Override
    protected void startStringCase(Branch branch) {
        branch.controlFlow(code, "$L.isText()", parserVariable.getSimpleName());
    }

    @Override
    protected void startNumberCase(Branch branch) {
        branch.controlFlow(code, "$L.isNumber()", parserVariable.getSimpleName());
    }

    @Override
    protected Snippet objectCaseCondition() {
        return Snippet.of("$L.isObjectStart($L)", parserVariable.getSimpleName(), importAdvance(CONSUME));
    }

    @Override
    protected void startArrayCase(Branch branch) {
        branch.controlFlow(code, "$L.isArrayStart($L)", parserVariable.getSimpleName(), importAdvance(CONSUME));
    }

    @Override
    protected void startBooleanCase(Branch branch) {
        branch.controlFlow(code, "$L.isBoolean()", parserVariable.getSimpleName());
    }

    @Override
    protected void startFieldCase(Branch branch) {
        branch.controlFlow(code, "$L.isFieldName()", parserVariable.getSimpleName());
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
        Snippet snippet = Snippet.of("$L.$L($L)", parserVariable.getSimpleName(), method, importAdvance(CONSUME));
        lhs.assign(code, snippet);
    }

    @Override
    protected void readString(StringKind stringKind) {
        String conversion =
                switch (stringKind) {
                    case STRING -> "";
                    case CHAR_ARRAY -> ".toCharArray()";
                };
        Snippet snippet =
                Snippet.of("$L.getText($L)$L", parserVariable.getSimpleName(), importAdvance(CONSUME), conversion);
        lhs.assign(code, snippet);
    }

    @Override
    protected void iterateOverFields() {
        code.beginControlFlow("while (!$L.isObjectEnd($L))", parserVariable.getSimpleName(), importAdvance(CONSUME));
    }

    @Override
    protected void skipValue() {
        code.addStatement("$L.skipChildren($L)", parserVariable.getSimpleName(), importAdvance(CONSUME));
    }

    @Override
    protected void afterObject() {}

    @Override
    protected void readFieldNameInIteration(String propertyName) {
        code.addStatement(
                "String $L = $L.getFieldName($L)",
                propertyName,
                parserVariable.getSimpleName(),
                importAdvance(CONSUME));
    }

    @Override
    protected void readDiscriminator(String propertyName) {
        lhs.assign(code, "$L.getDiscriminator($S, false)", parserVariable.getSimpleName(), propertyName);
    }

    @Override
    protected void iterateOverElements() {
        code.beginControlFlow("while (!$L.isArrayEnd($L))", parserVariable.getSimpleName(), importAdvance(CONSUME));
    }

    @Override
    protected void afterArray() {
        // we skipped the END_ARRAY token in the head of the loop
    }

    @Override
    protected void throwUnexpected(String expectedToken) {
        code.addStatement("throw $L.unexpectedToken($S)", parserVariable.getSimpleName(), expectedToken);
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
    protected ScruseReaderGenerator nest(
            TypeMirror type, String propertyName, LHS lhs, boolean stackRelevantType, AnyConfig config) {
        return new ScruseReaderGenerator(
                prototype,
                utils,
                utils.tf.getType(type),
                propertyName,
                code,
                parserVariable,
                lhs,
                this,
                stackRelevantType,
                config);
    }

    private String importAdvance(ScruseReader.Advance advance) {
        generatedClass.fileBuilderMods.add(
                builder -> builder.addStaticImport(ClassName.get(ScruseReader.Advance.class), "*"));
        return advance.name();
    }
}
