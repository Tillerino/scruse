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

public class JakartaJsonGeneratorGenerator extends AbstractWriterGenerator<JakartaJsonGeneratorGenerator> {
    private final VariableElement generatorVariable;

    public JakartaJsonGeneratorGenerator(
            AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        super(utils, prototype, generatedClass);
        this.generatorVariable = prototype.methodElement().getParameters().get(1);
    }

    public JakartaJsonGeneratorGenerator(
            ScrusePrototype prototype,
            AnnotationProcessorUtils utils,
            Type type,
            CodeBlock.Builder code,
            VariableElement generatorVariable,
            JakartaJsonGeneratorGenerator parent,
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
        return new Features(true);
    }

    @Override
    protected void writeNull() {
        if (lhs instanceof LHS.Field f) {
            code.addStatement("$L.writeNull(" + f.format() + ")", flatten(generatorVariable.getSimpleName(), f.args()));
        } else {
            code.addStatement("$L.writeNull()", generatorVariable.getSimpleName());
        }
    }

    @Override
    protected void writeString(StringKind stringKind) {
        Snippet string = stringKind == StringKind.STRING ? rhs : charArrayToString(rhs);
        if (lhs instanceof LHS.Field f) {
            Snippet.of("$L.write($C, $C)", generatorVariable, f, string).addStatementTo(code);
        } else {
            Snippet.of("$L.write($C)", generatorVariable, string).addStatementTo(code);
        }
    }

    @Override
    protected void writeBinary(BinaryKind binaryKind) {
        addFieldNameIfRequired();
        switch (binaryKind) {
            case BYTE_ARRAY -> Snippet.of("$L.write($C)", generatorVariable, base64Encode(rhs))
                    .addStatementTo(code);
        }
    }

    private boolean addFieldNameIfRequired() {
        if (lhs instanceof LHS.Field f) {
            code.addStatement("$L.writeKey(" + f.format() + ")", flatten(generatorVariable.getSimpleName(), f.args()));
            return true;
        }
        return false;
    }

    @Override
    public void writePrimitive(TypeMirror typeMirror) {
        Snippet value = typeMirror.getKind() == TypeKind.CHAR ? Snippet.of("String.valueOf($C)", rhs) : rhs;
        if (lhs instanceof LHS.Field f) {
            Snippet.of("$L.write($C, $C)", generatorVariable, f, value).addStatementTo(code);
        } else {
            Snippet.of("$L.write($C)", generatorVariable, value).addStatementTo(code);
        }
    }

    @Override
    protected void startArray() {
        addFieldNameIfRequired();
        code.addStatement("$L.writeStartArray()", generatorVariable.getSimpleName());
    }

    @Override
    protected void endArray() {
        code.addStatement("$L.writeEnd()", generatorVariable.getSimpleName());
    }

    @Override
    protected void startObject() {
        addFieldNameIfRequired();
        code.addStatement("$L.writeStartObject()", generatorVariable.getSimpleName());
    }

    @Override
    protected void endObject() {
        code.addStatement("$L.writeEnd()", generatorVariable.getSimpleName());
    }

    @Override
    protected void invokeDelegate(String instance, InstantiatedMethod callee) {
        addFieldNameIfRequired();
        Snippet.of(
                        "$L.$L($C$C)",
                        instance,
                        callee,
                        rhs,
                        Snippet.joinPrependingCommaToEach(prototype.findArguments(callee, 1, generatedClass)))
                .addStatementTo(code);
    }

    @Override
    protected JakartaJsonGeneratorGenerator nest(
            TypeMirror type, LHS lhs, String propertyName, RHS rhs, boolean stackRelevantType, AnyConfig config) {
        return new JakartaJsonGeneratorGenerator(
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
