package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public class JacksonJsonNodeWriterGenerator extends AbstractWriterGenerator<JacksonJsonNodeWriterGenerator> {
    private final ClassName jsonNodeFactory = ClassName.get("com.fasterxml.jackson.databind.node", "JsonNodeFactory");
    private final ClassName arrayNode = ClassName.get("com.fasterxml.jackson.databind.node", "ArrayNode");
    private final ClassName objectNode = ClassName.get("com.fasterxml.jackson.databind.node", "ObjectNode");

    // MUTABLE!!! Created with startObject or startArray
    private String nodeName = null;

    public JacksonJsonNodeWriterGenerator(
            AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        super(utils, prototype, generatedClass);
    }

    public JacksonJsonNodeWriterGenerator(
            AnnotationProcessorUtils utils,
            Type type,
            CodeBlock.Builder code,
            JacksonJsonNodeWriterGenerator parent,
            LHS lhs,
            RHS rhs,
            String propertyName,
            ScrusePrototype prototype,
            boolean stackRelevantType) {
        super(utils, parent.generatedClass, prototype, code, parent, type, propertyName, rhs, lhs, stackRelevantType);
    }

    @Override
    protected void writeNull() {
        if (lhs instanceof LHS.Return) {
            code.addStatement("return $T.instance.nullNode()", jsonNodeFactory);
        } else if (lhs instanceof LHS.Array) {
            code.addStatement("$L.addNull()", parent.nodeName());
        } else if (lhs instanceof LHS.Field f) {
            code.addStatement("$L.putNull(" + f.format() + ")", flatten(parent.nodeName(), f.args()));
        } else {
            throw new IllegalStateException("Unknown lhs " + lhs);
        }
    }

    @Override
    protected void writeString(StringKind stringKind) {
        String format = stringKind == StringKind.CHAR_ARRAY ? ("new String(" + rhs.format() + ")") : rhs.format();
        if (lhs instanceof LHS.Return) {
            code.addStatement("return $T.instance.textNode(" + format + ")", flatten(jsonNodeFactory, rhs.args()));
        } else if (lhs instanceof LHS.Array) {
            code.addStatement("$L.add(" + format + ")", flatten(parent.nodeName(), rhs.args()));
        } else if (lhs instanceof LHS.Field f) {
            code.addStatement(
                    "$L.put(" + f.format() + ", " + format + ")", flatten(parent.nodeName(), f.args(), rhs.args()));
        } else {
            throw new IllegalStateException("Unknown lhs " + lhs);
        }
    }

    @Override
    protected void writeBinary(BinaryKind binaryKind) {
        if (lhs instanceof LHS.Return) {
            code.addStatement(
                    "return $T.instance.binaryNode(" + rhs.format() + ")", flatten(jsonNodeFactory, rhs.args()));
        } else if (lhs instanceof LHS.Array) {
            code.addStatement("$L.add(" + rhs.format() + ")", flatten(parent.nodeName(), rhs.args()));
        } else if (lhs instanceof LHS.Field f) {
            code.addStatement(
                    "$L.put(" + f.format() + ", " + rhs.format() + ")",
                    flatten(parent.nodeName(), f.args(), rhs.args()));
        } else {
            throw new IllegalStateException("Unknown lhs " + lhs);
        }
    }

    @Override
    public void writePrimitive(TypeMirror typeMirror) {
        String value = typeMirror.getKind() == TypeKind.CHAR ? "String.valueOf(" + rhs.format() + ")" : rhs.format();
        if (lhs instanceof LHS.Return) {
            String suffix =
                    switch (typeMirror.getKind()) {
                        case BOOLEAN -> "boolean";
                        case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE -> "number";
                        case CHAR -> "text";
                        default -> throw new UnsupportedOperationException(
                                stack() + " unsupported type " + typeMirror.getKind());
                    };
            code.addStatement("return $T.instance.$LNode(" + value + ")", flatten(jsonNodeFactory, suffix, rhs.args()));
        } else if (lhs instanceof LHS.Array) {
            code.addStatement("$L.add(" + value + ")", flatten(parent.nodeName(), rhs.args()));
        } else if (lhs instanceof LHS.Field f) {
            code.addStatement(
                    "$L.put(" + f.format() + ", " + value + ")", flatten(parent.nodeName(), f.args(), rhs.args()));
        }
    }

    @Override
    protected void startArray() {
        nodeName = propertyName() + "$" + stackDepth() + "$node";
        if (lhs instanceof LHS.Return) {
            code.addStatement("$T $L = $T.instance.arrayNode()", arrayNode, nodeName(), jsonNodeFactory);
        } else if (lhs instanceof LHS.Array) {
            code.addStatement("$T $L = $L.addArray()", arrayNode, nodeName(), parent.nodeName());
        } else if (lhs instanceof LHS.Field f) {
            code.addStatement(
                    "$T $L = $L.putArray(" + f.format() + ")",
                    flatten(arrayNode, nodeName(), parent.nodeName(), f.args()));
        } else {
            throw new IllegalStateException("Unknown lhs " + lhs);
        }
    }

    @Override
    protected void endArray() {
        if (lhs instanceof LHS.Return) {
            code.addStatement("return $L", nodeName());
        }
    }

    @Override
    protected void startObject() {
        nodeName = propertyName() + "$" + stackDepth() + "$node";
        if (lhs instanceof LHS.Return) {
            code.addStatement("$T $L = $T.instance.objectNode()", objectNode, nodeName(), jsonNodeFactory);
        } else if (lhs instanceof LHS.Array) {
            code.addStatement("$T $L = $L.addObject()", objectNode, nodeName(), parent.nodeName());
        } else if (lhs instanceof LHS.Field f) {
            code.addStatement(
                    "$T $L = $L.putObject(" + f.format() + ")",
                    flatten(objectNode, nodeName(), parent.nodeName(), f.args()));
        } else {
            throw new IllegalStateException("Unknown lhs " + lhs);
        }
    }

    @Override
    protected void endObject() {
        if (lhs instanceof LHS.Return) {
            code.addStatement("return $L", nodeName());
        }
    }

    @Override
    protected void invokeDelegate(String instance, InstantiatedMethod callee) {
        Snippet invocation = Snippet.of(
                "$L.$L($C$C)",
                instance,
                callee,
                rhs,
                Snippet.joinPrependingCommaToEach(prototype.findArguments(callee, 1, generatedClass)));
        if (lhs instanceof LHS.Return) {
            Snippet.of("return $C", invocation).addStatementTo(code);
        } else if (lhs instanceof LHS.Array) {
            Snippet.of("$L.add($C)", parent.nodeName(), invocation).addStatementTo(code);
        } else if (lhs instanceof LHS.Field f) {
            Snippet.of("$L.put($C, $C)", parent.nodeName(), f, invocation).addStatementTo(code);
        } else {
            throw new IllegalStateException("Unknown lhs " + lhs);
        }
    }

    @Override
    protected JacksonJsonNodeWriterGenerator nest(
            TypeMirror type, LHS lhs, String propertyName, RHS rhs, boolean stackRelevantType) {
        return new JacksonJsonNodeWriterGenerator(
                utils, utils.tf.getType(type), code, this, lhs, rhs, propertyName, prototype, stackRelevantType);
    }

    String nodeName() {
        return nodeName != null ? nodeName : parent.nodeName();
    }
}
