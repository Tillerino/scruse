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
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public class NanojsonWriterGenerator extends AbstractWriterGenerator<NanojsonWriterGenerator> {
    private final VariableElement generatorVariable;

    public NanojsonWriterGenerator(
            AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        super(utils, prototype, generatedClass);
        this.generatorVariable = prototype.methodElement().getParameters().get(1);
    }

    protected NanojsonWriterGenerator(
            ScrusePrototype prototype,
            AnnotationProcessorUtils utils,
            Type type,
            CodeBlock.Builder code,
            VariableElement generatorVariable,
            NanojsonWriterGenerator parent,
            LHS lhs,
            RHS rhs,
            String propertyName,
            boolean stackRelevantType) {
        super(utils, parent.generatedClass, prototype, code, parent, type, propertyName, rhs, lhs, stackRelevantType);
        this.generatorVariable = generatorVariable;
    }

    @Override
    protected Features features() {
        return Features.builder().onlySupportsFiniteNumbers(true).build();
    }

    @Override
    protected void writeNull() {
        if (lhs instanceof LHS.Field f) {
            code.addStatement("$L.nul(" + f.format() + ")", flatten(generatorVariable.getSimpleName(), f.args()));
        } else {
            code.addStatement("$L.nul()", generatorVariable.getSimpleName());
        }
    }

    @Override
    protected void writeString(StringKind stringKind) {
        Snippet string = stringKind == StringKind.STRING ? rhs : charArrayToString(rhs);
        if (lhs instanceof LHS.Field f) {
            Snippet.of("$L.value($C, $C)", generatorVariable.getSimpleName(), f, string)
                    .addStatementTo(code);
        } else {
            Snippet.of("$L.value($C)", generatorVariable, string).addStatementTo(code);
        }
    }

    @Override
    protected void writeBinary(BinaryKind binaryKind) {
        Snippet asString = base64Encode(rhs);
        if (lhs instanceof LHS.Field f) {
            if (binaryKind == BinaryKind.BYTE_ARRAY) {
                Snippet.of("$L.value($C, $C)", generatorVariable, f, asString).addStatementTo(code);
                return;
            } else {
            }
        }
        switch (binaryKind) {
            case BYTE_ARRAY -> Snippet.of("$L.value($C)", generatorVariable, asString)
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
            Snippet.of("$L.value($C, $C)", generatorVariable, f, rhs_).addStatementTo(code);
        } else {
            Snippet.of("$L.value($C)", generatorVariable, rhs_).addStatementTo(code);
        }
    }

    @Override
    protected void startArray() {
        if (lhs instanceof LHS.Field f) {
            code.addStatement("$L.array(" + f.format() + ")", flatten(generatorVariable.getSimpleName(), f.args()));
        } else {
            code.addStatement("$L.array()", generatorVariable.getSimpleName());
        }
    }

    @Override
    protected void endArray() {
        code.addStatement("$L.end()", generatorVariable.getSimpleName());
    }

    @Override
    protected void startObject() {
        if (lhs instanceof LHS.Field f) {
            code.addStatement("$L.object(" + f.format() + ")", flatten(generatorVariable.getSimpleName(), f.args()));
        } else {
            code.addStatement("$L.object()", generatorVariable.getSimpleName());
        }
    }

    @Override
    protected void endObject() {
        code.addStatement("$L.end()", generatorVariable.getSimpleName());
    }

    @Override
    protected void invokeDelegate(String instance, InstantiatedMethod callee) {
        if (lhs instanceof LHS.Field f) {
            Snippet.of("$L.key($C)", generatorVariable, f).addStatementTo(code);
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
    protected NanojsonWriterGenerator nest(
            TypeMirror type, LHS lhs, String propertyName, RHS rhs, boolean stackRelevantType) {
        return new NanojsonWriterGenerator(
                prototype,
                utils,
                utils.tf.getType(type),
                code,
                generatorVariable,
                this,
                lhs,
                rhs,
                propertyName,
                stackRelevantType);
    }
}
