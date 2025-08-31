package org.tillerino.scruse.processor.apis;

import static org.tillerino.scruse.processor.Snippet.joinPrependingCommaToEach;
import static org.tillerino.scruse.processor.Snippet.of;

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
            addStatement("$L.nul(" + f.format() + ")", flatten(generatorVariable.getSimpleName(), f.args()));
        } else {
            addStatement("$L.nul()", generatorVariable.getSimpleName());
        }
    }

    @Override
    protected void writeString(StringKind stringKind) {
        Snippet string = stringKind == StringKind.STRING ? rhs : charArrayToString(rhs);
        if (lhs instanceof LHS.Field f) {
            addStatement(Snippet.of("$L.value($C, $C)", generatorVariable.getSimpleName(), f, string));
        } else {
            addStatement(Snippet.of("$L.value($C)", generatorVariable, string));
        }
    }

    @Override
    protected void writeBinary(BinaryKind binaryKind) {
        Snippet asString = base64Encode(rhs);
        if (lhs instanceof LHS.Field f) {
            if (binaryKind == BinaryKind.BYTE_ARRAY) {
                addStatement(Snippet.of("$L.value($C, $C)", generatorVariable, f, asString));
                return;
            } else {
            }
        }
        switch (binaryKind) {
            case BYTE_ARRAY -> addStatement(of("$L.value($C)", generatorVariable, asString));
        }
    }

    @Override
    public void writePrimitive(TypeMirror typeMirror) {
        Snippet rhs_ = rhs;
        if (typeMirror.getKind() == TypeKind.CHAR) {
            rhs_ = Snippet.of("String.valueOf($C)", rhs);
        }
        if (lhs instanceof LHS.Field f) {
            addStatement(of("$L.value($C, $C)", generatorVariable, f, rhs_));
        } else {
            addStatement(of("$L.value($C)", generatorVariable, rhs_));
        }
    }

    @Override
    protected void startArray() {
        if (lhs instanceof LHS.Field f) {
            addStatement("$L.array(" + f.format() + ")", flatten(generatorVariable.getSimpleName(), f.args()));
        } else {
            addStatement("$L.array()", generatorVariable.getSimpleName());
        }
    }

    @Override
    protected void endArray() {
        addStatement("$L.end()", generatorVariable.getSimpleName());
    }

    @Override
    protected void startObject() {
        if (lhs instanceof LHS.Field f) {
            addStatement("$L.object(" + f.format() + ")", flatten(generatorVariable.getSimpleName(), f.args()));
        } else {
            addStatement("$L.object()", generatorVariable.getSimpleName());
        }
    }

    @Override
    protected void endObject() {
        addStatement("$L.end()", generatorVariable.getSimpleName());
    }

    @Override
    protected void invokeDelegate(String instance, InstantiatedMethod callee) {
        if (lhs instanceof LHS.Field f) {
            addStatement(of("$L.key($C)", generatorVariable, f));
        }
        addStatement(of(
                "$L.$L($C$C)",
                instance,
                callee,
                rhs,
                joinPrependingCommaToEach(prototype.findArguments(callee, 1, generatedClass))));
    }

    @Override
    protected NanojsonWriterGenerator nest(
            TypeMirror type, LHS lhs, Property property, RHS rhs, boolean stackRelevantType, AnyConfig config) {
        return new NanojsonWriterGenerator(
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
