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

public class ScruseWriterGenerator extends AbstractWriterGenerator<ScruseWriterGenerator> {
    private final VariableElement generatorVariable;

    public ScruseWriterGenerator(
            AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        super(utils, prototype, generatedClass);
        this.generatorVariable = prototype.methodElement().getParameters().get(1);
    }

    protected ScruseWriterGenerator(
            ScrusePrototype prototype,
            AnnotationProcessorUtils utils,
            Type type,
            CodeBlock.Builder code,
            VariableElement generatorVariable,
            ScruseWriterGenerator parent,
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
        this.generatorVariable = generatorVariable;
    }

    @Override
    protected Features features() {
        return new Features(false);
    }

    @Override
    protected void writeNull() {
        if (lhs instanceof LHS.Field f) {
            code.addStatement(
                    "$L.writeNullField(" + f.format() + ")", flatten(generatorVariable.getSimpleName(), f.args()));
        } else {
            code.addStatement("$L.writeNull()", generatorVariable.getSimpleName());
        }
    }

    @Override
    protected void writeString(StringKind stringKind) {
        Snippet string = stringKind == StringKind.STRING ? rhs : charArrayToString(rhs);
        if (lhs instanceof LHS.Field f) {
            Snippet.of("$L.writeField($C, $C)", generatorVariable.getSimpleName(), f, string)
                    .addStatementTo(code);
        } else {
            Snippet.of("$L.write($C)", generatorVariable, string).addStatementTo(code);
        }
    }

    @Override
    protected void writeBinary(BinaryKind binaryKind) {
        Snippet asString = base64Encode(rhs);
        if (lhs instanceof LHS.Field f) {
            if (binaryKind == BinaryKind.BYTE_ARRAY) {
                Snippet.of("$L.writeField($C, $C)", generatorVariable, f, asString)
                        .addStatementTo(code);
                return;
            } else {
            }
        }
        switch (binaryKind) {
            case BYTE_ARRAY -> Snippet.of("$L.write($C)", generatorVariable, asString)
                    .addStatementTo(code);
        }
    }

    @Override
    public void writePrimitive(TypeMirror typeMirror) {
        Snippet rhs_ = rhs;
        if (typeMirror.getKind() == TypeKind.CHAR) {
            rhs_ = Snippet.of("String.valueOf($C)", rhs);
        }
        if (lhs instanceof LHS.Field f) {
            Snippet.of("$L.writeField($C, $C)", generatorVariable, f, rhs_).addStatementTo(code);
        } else {
            Snippet.of("$L.write($C)", generatorVariable, rhs_).addStatementTo(code);
        }
    }

    @Override
    protected void startArray() {
        if (lhs instanceof LHS.Field f) {
            code.addStatement(
                    "$L.startArrayField(" + f.format() + ")", flatten(generatorVariable.getSimpleName(), f.args()));
        } else {
            code.addStatement("$L.startArray()", generatorVariable.getSimpleName());
        }
    }

    @Override
    protected void endArray() {
        code.addStatement("$L.endArray()", generatorVariable.getSimpleName());
    }

    @Override
    protected void startObject() {
        if (lhs instanceof LHS.Field f) {
            code.addStatement(
                    "$L.startObjectField(" + f.format() + ")", flatten(generatorVariable.getSimpleName(), f.args()));
        } else {
            code.addStatement("$L.startObject()", generatorVariable.getSimpleName());
        }
    }

    @Override
    protected void endObject() {
        code.addStatement("$L.endObject()", generatorVariable.getSimpleName());
    }

    @Override
    protected void invokeDelegate(String instance, InstantiatedMethod callee) {
        if (lhs instanceof LHS.Field f) {
            Snippet.of("$L.writeFieldName($C)", generatorVariable, f).addStatementTo(code);
        }
        Snippet.of(
                        "$L.$L($C$C)",
                        instance,
                        callee,
                        rhs,
                        Snippet.joinPrependingCommaToEach(prototype.findArguments(callee, 1, generatedClass)))
                .addStatementTo(code);
    }

    @Override
    protected ScruseWriterGenerator nest(
            TypeMirror type, LHS lhs, String propertyName, RHS rhs, boolean stackRelevantType, AnyConfig config) {
        return new ScruseWriterGenerator(
                prototype,
                utils,
                utils.tf.getType(type),
                code,
                generatorVariable,
                this,
                lhs,
                rhs,
                propertyName,
                stackRelevantType,
                config);
    }
}
