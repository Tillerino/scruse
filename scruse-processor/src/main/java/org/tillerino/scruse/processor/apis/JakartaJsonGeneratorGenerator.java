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
        this.generatorVariable = generatorVariable;
    }

    @Override
    protected Features features() {
        return new Features(true);
    }

    @Override
    protected void writeNull() {
        if (lhs instanceof LHS.Field f) {
            addStatement("$L.writeNull(" + f.format() + ")", flatten(generatorVariable.getSimpleName(), f.args()));
        } else {
            addStatement("$L.writeNull()", generatorVariable.getSimpleName());
        }
    }

    @Override
    protected void writeString(StringKind stringKind) {
        Snippet string = stringKind == StringKind.STRING ? rhs : charArrayToString(rhs);
        if (lhs instanceof LHS.Field f) {
            addStatement(Snippet.of("$L.write($C, $C)", generatorVariable, f, string));
        } else {
            addStatement(Snippet.of("$L.write($C)", generatorVariable, string));
        }
    }

    @Override
    protected void writeBinary(BinaryKind binaryKind) {
        addFieldNameIfRequired();
        switch (binaryKind) {
            case BYTE_ARRAY -> addStatement(Snippet.of("$L.write($C)", generatorVariable, base64Encode(rhs)));
        }
    }

    private boolean addFieldNameIfRequired() {
        if (lhs instanceof LHS.Field f) {
            addStatement("$L.writeKey(" + f.format() + ")", flatten(generatorVariable.getSimpleName(), f.args()));
            return true;
        }
        return false;
    }

    @Override
    public void writePrimitive(TypeMirror typeMirror) {
        Snippet value = typeMirror.getKind() == TypeKind.CHAR ? Snippet.of("String.valueOf($C)", rhs) : rhs;
        if (lhs instanceof LHS.Field f) {
            addStatement(Snippet.of("$L.write($C, $C)", generatorVariable, f, value));
        } else {
            addStatement(Snippet.of("$L.write($C)", generatorVariable, value));
        }
    }

    @Override
    protected void startArray() {
        addFieldNameIfRequired();
        addStatement("$L.writeStartArray()", generatorVariable.getSimpleName());
    }

    @Override
    protected void endArray() {
        addStatement("$L.writeEnd()", generatorVariable.getSimpleName());
    }

    @Override
    protected void startObject() {
        addFieldNameIfRequired();
        addStatement("$L.writeStartObject()", generatorVariable.getSimpleName());
    }

    @Override
    protected void endObject() {
        addStatement("$L.writeEnd()", generatorVariable.getSimpleName());
    }

    @Override
    protected void invokeDelegate(String instance, InstantiatedMethod callee) {
        addFieldNameIfRequired();
        addStatement(Snippet.of(
                "$L.$L($C$C)",
                instance,
                callee,
                rhs,
                Snippet.joinPrependingCommaToEach(
                        utils.delegation.findArguments(prototype, callee, 1, generatedClass))));
    }

    @Override
    protected JakartaJsonGeneratorGenerator nest(
            TypeMirror type, LHS lhs, Property property, RHS rhs, boolean stackRelevantType, AnyConfig config) {
        return new JakartaJsonGeneratorGenerator(
                prototype,
                utils,
                utils.tf.getType(type),
                code,
                generatorVariable,
                this,
                lhs,
                rhs,
                property,
                stackRelevantType,
                config);
    }
}
