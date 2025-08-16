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
import org.tillerino.scruse.processor.util.AnyConfig;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public class Fastjson2WriterGenerator extends AbstractWriterGenerator<Fastjson2WriterGenerator> {
    private final VariableElement writerVariable;

    public Fastjson2WriterGenerator(
            AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        super(utils, prototype, generatedClass);
        this.writerVariable = prototype.methodElement().getParameters().get(1);
    }

    public Fastjson2WriterGenerator(
            ScrusePrototype prototype,
            AnnotationProcessorUtils utils,
            Type type,
            CodeBlock.Builder code,
            VariableElement writerVariable,
            Fastjson2WriterGenerator parent,
            LHS lhs,
            RHS rhs,
            String propertyName,
            boolean stackRelevantType,
            AnyConfig config) {
        super(
                utils,
                parent.generatedClass,
                prototype,
                code,
                parent,
                type,
                propertyName,
                rhs,
                lhs,
                stackRelevantType,
                config);
        this.writerVariable = writerVariable;
    }

    @Override
    protected void writeNullable() {
        if (writeNatively()) {
            return;
        }
        super.writeNullable();
    }

    @Override
    protected void writeNull() {
        addFieldNameIfNeeded();
        code.addStatement("$L.writeNull()", writerVariable.getSimpleName());
    }

    @Override
    protected void writeString(StringKind stringKind) {
        addFieldNameIfNeeded();
        switch (stringKind) {
            case STRING -> code.addStatement(
                    "$L.writeString(" + rhs.format() + ")", flatten(writerVariable.getSimpleName(), rhs.args()));
            case CHAR_ARRAY -> code.addStatement(
                    "$L.writeString(new String(" + rhs.format() + "))",
                    flatten(writerVariable.getSimpleName(), rhs.args()));
        }
    }

    @Override
    protected void writeBinary(BinaryKind binaryKind) {
        addFieldNameIfNeeded();
        switch (binaryKind) {
            case BYTE_ARRAY -> code.addStatement(
                    "$L.writeBase64(" + rhs.format() + ")", flatten(writerVariable.getSimpleName(), rhs.args()));
        }
    }

    @Override
    public void writePrimitive(TypeMirror typeMirror) {
        addFieldNameIfNeeded();
        TypeKind kind = typeMirror.getKind();
        if (kind == TypeKind.CHAR) {
            code.addStatement(
                    "$L.writeString(String.valueOf(" + rhs.format() + "))",
                    flatten(writerVariable.getSimpleName(), rhs.args()));
        } else if (kind == TypeKind.FLOAT || kind == TypeKind.DOUBLE) {
            String write = kind == TypeKind.FLOAT ? "writeFloat" : "writeDouble";
            String cast = kind == TypeKind.FLOAT ? "(float)" : "(double)";
            code.beginControlFlow(
                            "if ($T.isFinite(" + rhs.format() + "))",
                            flatten(kind == TypeKind.FLOAT ? Float.class : Double.class, rhs.args()))
                    .addStatement(
                            "$L.$L($L " + rhs.format() + ")",
                            flatten(writerVariable.getSimpleName(), write, cast, rhs.args()))
                    .nextControlFlow("else")
                    .addStatement(
                            "$L.writeString(String.valueOf(" + rhs.format() + "))",
                            flatten(writerVariable.getSimpleName(), rhs.args()))
                    .endControlFlow();
        } else {
            String write =
                    switch (kind) {
                        case BOOLEAN -> "writeBool";
                        case BYTE -> "writeInt8";
                        case SHORT -> "writeInt16";
                        case INT -> "writeInt32";
                        case LONG -> "writeInt64";
                        default -> throw new AssertionError("Unexpected type: " + kind);
                    };
            code.addStatement(
                    "$L.$L(" + rhs.format() + ")", flatten(writerVariable.getSimpleName(), write, rhs.args()));
        }
    }

    @Override
    protected void startArray() {
        addFieldNameIfNeeded();
        code.addStatement("$L.startArray()", writerVariable.getSimpleName());
    }

    @Override
    protected void endArray() {
        code.addStatement("$L.endArray()", writerVariable.getSimpleName());
    }

    @Override
    protected void startObject() {
        addFieldNameIfNeeded();
        code.addStatement("$L.startObject()", writerVariable.getSimpleName());
    }

    @Override
    boolean needsToWriteComma() {
        return true;
    }

    @Override
    protected void writeComma() {
        code.addStatement("$L.writeComma()", writerVariable.getSimpleName());
    }

    @Override
    protected void endObject() {
        code.addStatement("$L.endObject()", writerVariable.getSimpleName());
    }

    @Override
    protected void invokeDelegate(String instance, InstantiatedMethod callee) {
        addFieldNameIfNeeded();
        Snippet.of(
                        "$L.$L($C$C)",
                        instance,
                        callee,
                        rhs,
                        Snippet.joinPrependingCommaToEach(prototype.findArguments(callee, 1, generatedClass)))
                .addStatementTo(code);
    }

    @Override
    protected Fastjson2WriterGenerator nest(
            TypeMirror type, LHS lhs, String propertyName, RHS rhs, boolean stackRelevantType, AnyConfig config) {
        return new Fastjson2WriterGenerator(
                prototype,
                utils,
                utils.tf.getType(type),
                code,
                writerVariable,
                this,
                lhs,
                rhs,
                propertyName,
                stackRelevantType,
                config);
    }

    private boolean writeNatively() {
        if (type.isArrayType() && writeArrayNatively(type.getComponentType().getTypeMirror())) {
            return true;
        }
        return false;
    }

    private boolean writeArrayNatively(TypeMirror componentType) {
        if (writePrimitiveArrayNatively(componentType.getKind())) {
            return true;
        }
        return false;
    }

    private boolean writePrimitiveArrayNatively(TypeKind kind) {
        String t =
                switch (kind) {
                    case BOOLEAN -> "Bool";
                    case SHORT -> "Int16";
                    case INT -> "Int32";
                    case LONG -> "Int64";
                        // for floating point, writes null for non-finite, so we cannot use those
                    default -> null;
                };
        if (t != null) {
            addFieldNameIfNeeded();
            code.addStatement(
                    "$L.write" + t + "(" + rhs.format() + ")", flatten(writerVariable.getSimpleName(), rhs.args()));
            return true;
        }
        return false;
    }

    private void addFieldNameIfNeeded() {
        if (lhs instanceof LHS.Field f) {
            code.addStatement("$L.writeName(" + f.format() + ")", flatten(writerVariable.getSimpleName(), f.args()));
            code.addStatement("$L.writeColon()", writerVariable.getSimpleName());
        }
    }
}
