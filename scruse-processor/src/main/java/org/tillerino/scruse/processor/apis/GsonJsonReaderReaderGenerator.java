package org.tillerino.scruse.processor.apis;

import static org.tillerino.scruse.processor.Snippet.join;
import static org.tillerino.scruse.processor.Snippet.of;

import com.squareup.javapoet.CodeBlock;
import jakarta.annotation.Nullable;
import java.io.IOException;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.helpers.GsonJsonReaderHelper;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public class GsonJsonReaderReaderGenerator extends AbstractReaderGenerator<GsonJsonReaderReaderGenerator> {
    private final VariableElement parserVariable;

    public GsonJsonReaderReaderGenerator(
            AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        super(utils, prototype, generatedClass);
        parserVariable = prototype.methodElement().getParameters().get(0);
    }

    public GsonJsonReaderReaderGenerator(
            ScrusePrototype prototype,
            AnnotationProcessorUtils utils,
            Type type,
            Property property,
            CodeBlock.Builder code,
            VariableElement parserVariable,
            LHS lhs,
            GsonJsonReaderReaderGenerator parent,
            boolean stackRelevantType,
            AnyConfig config) {
        super(utils, parent.generatedClass, prototype, code, parent, type, stackRelevantType, property, lhs, config);
        this.parserVariable = parserVariable;
    }

    @Override
    protected void startStringCase(Branch branch) {
        branch.controlFlow(this, "$L.peek() == $T.STRING", parserVariable.getSimpleName(), jsonToken());
    }

    @Override
    protected void startNumberCase(Branch branch) {
        branch.controlFlow(this, "$L.peek() == $T.NUMBER", parserVariable.getSimpleName(), jsonToken());
    }

    @Override
    protected Snippet objectCaseCondition() {
        return Snippet.of("$T.isBeginObject($L, true)", GsonJsonReaderHelper.class, parserVariable.getSimpleName());
    }

    @Override
    protected void startArrayCase(Branch branch) {
        branch.controlFlow(this, "$L.peek() == $T.BEGIN_ARRAY", parserVariable.getSimpleName(), jsonToken());
        addStatement("$L.beginArray()", parserVariable.getSimpleName());
    }

    @Override
    protected void startBooleanCase(Branch branch) {
        branch.controlFlow(this, "$L.peek() == $T.BOOLEAN", parserVariable.getSimpleName(), jsonToken());
    }

    @Override
    protected void startFieldCase(Branch branch) {
        branch.controlFlow(this, "$L.peek() == $T.NAME", parserVariable.getSimpleName(), jsonToken());
    }

    @Override
    protected void initializeParser() {}

    @Override
    protected Snippet nullCaseCondition() {
        return Snippet.of("$T.isNull($L, true)", GsonJsonReaderHelper.class, parserVariable.getSimpleName());
    }

    private TypeElement jsonToken() {
        return utils.elements.getTypeElement("com.google.gson.stream.JsonToken");
    }

    @Override
    protected void readPrimitive(TypeMirror type) {
        record R(String cast, String method) {}
        R readMethod =
                switch (type.getKind()) {
                    case BOOLEAN -> new R("", "nextBoolean");
                    case BYTE -> new R("(byte) ", "nextInt");
                    case SHORT -> new R("(short) ", "nextInt");
                    case INT -> new R("", "nextInt");
                    case LONG -> new R("", "nextLong");
                    case FLOAT -> new R("(float) ", "nextDouble");
                    case DOUBLE -> new R("", "nextDouble");
                    default -> throw new ContextedRuntimeException(
                            type.getKind().toString());
                };
        addStatement(lhs.assign("$L$L.$L()", readMethod.cast, parserVariable.getSimpleName(), readMethod.method));
    }

    @Override
    protected void readString(StringKind stringKind) {
        String conversion =
                switch (stringKind) {
                    case STRING -> "";
                    case CHAR_ARRAY -> ".toCharArray()";
                };
        addStatement(lhs.assign("$L.nextString()$L", parserVariable.getSimpleName(), conversion));
    }

    @Override
    protected void iterateOverFields() {
        beginControlFlow("while ($L.peek() != $T.END_OBJECT)", parserVariable.getSimpleName(), jsonToken());
    }

    @Override
    protected void skipValue() {
        addStatement("$L.skipValue()", parserVariable.getSimpleName());
    }

    @Override
    protected void afterObject() {
        addStatement("$L.endObject()", parserVariable.getSimpleName());
    }

    @Override
    protected void readFieldNameInIteration(String propertyName) {
        addStatement("String $L = $L.nextName()", propertyName, parserVariable.getSimpleName());
    }

    @Override
    protected void readDiscriminator(String propertyName) {
        addStatement(lhs.assign(
                "$T.readDiscriminator($S, $L)",
                GsonJsonReaderHelper.class,
                propertyName,
                parserVariable.getSimpleName()));
    }

    @Override
    protected void iterateOverElements() {
        beginControlFlow("while ($L.peek() != $T.END_ARRAY)", parserVariable.getSimpleName(), jsonToken());
    }

    @Override
    protected void afterArray() {
        addStatement("$L.endArray()", parserVariable.getSimpleName());
    }

    @Override
    protected void throwUnexpected(String expected) {
        addStatement(
                "throw new $T($S + $L.peek() + $S + $L.getPath())",
                IOException.class,
                "Expected " + expected + ", got ",
                parserVariable.getSimpleName(),
                " at ",
                parserVariable.getSimpleName());
    }

    @Override
    protected void invokeDelegate(String instance, InstantiatedMethod callee) {
        addStatement(lhs.assign(
                of("$L.$L($C)", instance, callee, join(prototype.findArguments(callee, 0, generatedClass), ", "))));
    }

    @Override
    protected GsonJsonReaderReaderGenerator nest(
            TypeMirror type, @Nullable Property property, LHS lhs, boolean stackRelevantType, AnyConfig config) {
        return new GsonJsonReaderReaderGenerator(
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
}
