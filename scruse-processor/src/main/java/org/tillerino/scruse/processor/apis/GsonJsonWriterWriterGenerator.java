package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public class GsonJsonWriterWriterGenerator extends AbstractWriterGenerator<GsonJsonWriterWriterGenerator> {
    private final VariableElement writerVariable;

    public GsonJsonWriterWriterGenerator(
            AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        super(utils, prototype, generatedClass);
        this.writerVariable = prototype.methodElement().getParameters().get(1);
    }

    public GsonJsonWriterWriterGenerator(
            ScrusePrototype prototype,
            AnnotationProcessorUtils utils,
            Type type,
            CodeBlock.Builder code,
            VariableElement writerVariable,
            GsonJsonWriterWriterGenerator parent,
            LHS lhs,
            RHS rhs,
            Property property,
            boolean stackRelevantType,
            AnyConfig config) {
        super(
                utils,
                parent.generatedClass,
                prototype,
                code,
                parent,
                type,
                property,
                rhs,
                lhs,
                stackRelevantType,
                config);
        this.writerVariable = writerVariable;
    }

    @Override
    protected void writeNull() {
        addFieldNameIfNeeded();
        addStatement("$L.nullValue()", writerVariable.getSimpleName());
    }

    @Override
    protected void writeString(StringKind stringKind) {
        addFieldNameIfNeeded();
        switch (stringKind) {
            case STRING -> addStatement(
                    "$L.value(" + rhs.format() + ")", flatten(writerVariable.getSimpleName(), rhs.args()));
            case CHAR_ARRAY -> addStatement(
                    "$L.value(new String(" + rhs.format() + "))", flatten(writerVariable.getSimpleName(), rhs.args()));
        }
    }

    @Override
    protected void writeBinary(BinaryKind binaryKind) {
        addFieldNameIfNeeded();
        switch (binaryKind) {
            case BYTE_ARRAY -> addStatement(Snippet.of("$L.value($C)", writerVariable, base64Encode(rhs)));
        }
    }

    @Override
    public void writePrimitive(TypeMirror typeMirror) {
        addFieldNameIfNeeded();
        TypeKind kind = typeMirror.getKind();
        if (kind == TypeKind.CHAR) {
            addStatement(
                    "$L.value(String.valueOf(" + rhs.format() + "))",
                    flatten(writerVariable.getSimpleName(), rhs.args()));
        } else if (kind == TypeKind.FLOAT || kind == TypeKind.DOUBLE) {
            beginControlFlow(
                    "if ($T.isFinite(" + rhs.format() + "))",
                    flatten(kind == TypeKind.FLOAT ? Float.class : Double.class, rhs.args()));
            addStatement("$L.value(" + rhs.format() + ")", flatten(writerVariable.getSimpleName(), rhs.args()));
            nextControlFlow("else");
            addStatement(
                    "$L.value(String.valueOf(" + rhs.format() + "))",
                    flatten(writerVariable.getSimpleName(), rhs.args()));
            endControlFlow();
        } else {
            addStatement("$L.value(" + rhs.format() + ")", flatten(writerVariable.getSimpleName(), rhs.args()));
        }
    }

    @Override
    protected void startArray() {
        addFieldNameIfNeeded();
        addStatement("$L.beginArray()", writerVariable.getSimpleName());
    }

    @Override
    protected void endArray() {
        addStatement("$L.endArray()", writerVariable.getSimpleName());
    }

    @Override
    protected void startObject() {
        addFieldNameIfNeeded();
        addStatement("$L.beginObject()", writerVariable.getSimpleName());
    }

    @Override
    protected void endObject() {
        addStatement("$L.endObject()", writerVariable.getSimpleName());
    }

    @Override
    protected void invokeDelegate(String instance, InstantiatedMethod callee) {
        addFieldNameIfNeeded();
        addStatement(Snippet.of(
                "$L.$L($C$C)",
                instance,
                callee,
                rhs,
                Snippet.joinPrependingCommaToEach(
                        utils.delegation.findArguments(prototype, callee, 1, generatedClass))));
    }

    @Override
    protected GsonJsonWriterWriterGenerator nest(
            TypeMirror type, LHS lhs, Property property, RHS rhs, boolean stackRelevantType, AnyConfig config) {
        return new GsonJsonWriterWriterGenerator(
                prototype,
                utils,
                utils.tf.getType(type),
                code,
                writerVariable,
                this,
                lhs,
                rhs,
                property,
                stackRelevantType,
                config);
    }

    private void addFieldNameIfNeeded() {
        if (lhs instanceof LHS.Field f) {
            addStatement("$L.name(" + f.format() + ")", flatten(writerVariable.getSimpleName(), f.args()));
        }
    }
}
