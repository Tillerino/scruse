package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseMethod;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class JacksonJsonNodeWriterGenerator extends AbstractWriterGenerator<JacksonJsonNodeWriterGenerator> {
	private final ClassName jsonNodeFactory = ClassName.get("com.fasterxml.jackson.databind.node", "JsonNodeFactory");
	private final ClassName arrayNode = ClassName.get("com.fasterxml.jackson.databind.node", "ArrayNode");
	private final ClassName objectNode = ClassName.get("com.fasterxml.jackson.databind.node", "ObjectNode");

	// MUTABLE!!! Created with startObject or startArray
	private String nodeName = null;

	public JacksonJsonNodeWriterGenerator(AnnotationProcessorUtils utils, ScruseMethod prototype) {
		super(utils, prototype);
	}

	public JacksonJsonNodeWriterGenerator(AnnotationProcessorUtils utils, Type type, CodeBlock.Builder code, JacksonJsonNodeWriterGenerator parent, LHS lhs, RHS rhs, String propertyName, ScruseMethod prototype) {
		super(prototype, utils, code, parent, lhs, propertyName, rhs, type);
	}

	@Override
	protected void writeNull() {
		if (lhs instanceof LHS.Return) {
			code.addStatement("return $T.instance.nullNode()", jsonNodeFactory);
		} else if(lhs instanceof LHS.Array) {
			code.addStatement("$L.addNull()", parent.nodeName());
		} else if(lhs instanceof LHS.Field f) {
			code.addStatement("$L.putNull(" + f.format() + ")", flatten(parent.nodeName(), f.args()));
		} else {
			throw new IllegalStateException("Unknown lhs " + lhs);
		}
	}

	@Override
	protected void writeString(StringKind stringKind) {
		if (lhs instanceof LHS.Return) {
			switch (stringKind) {
				case STRING -> code.addStatement("return $T.instance.textNode(" + rhs.format() + ")", flatten(jsonNodeFactory, rhs.args()));
				case CHAR_ARRAY -> code.addStatement("return $T.instance.textNode(new String(" + rhs.format() + "))", flatten(jsonNodeFactory, rhs.args()));
			}
		} else if(lhs instanceof LHS.Array) {
			code.addStatement("$L.add(" + rhs.format() + ")", flatten(parent.nodeName(), rhs.args()));
		} else if(lhs instanceof LHS.Field f) {
			code.addStatement("$L.put(" + f.format() + ", " + rhs.format() + ")", flatten(parent.nodeName(), f.args(), rhs.args()));
		} else {
			throw new IllegalStateException("Unknown lhs " + lhs);
		}
	}

	@Override
	protected void writeBinary(BinaryKind binaryKind) {
		if (lhs instanceof LHS.Return) {
			code.addStatement("return $T.instance.binaryNode(" + rhs.format() + ")", flatten(jsonNodeFactory, rhs.args()));
		} else if(lhs instanceof LHS.Array) {
			code.addStatement("$L.add(" + rhs.format() + ")", flatten(parent.nodeName(), rhs.args()));
		} else if(lhs instanceof LHS.Field f) {
			code.addStatement("$L.put(" + f.format() + ", " + rhs.format() + ")", flatten(parent.nodeName(), f.args(), rhs.args()));
		} else {
			throw new IllegalStateException("Unknown lhs " + lhs);
		}
	}

	@Override
	public void writePrimitive(TypeMirror typeMirror) {
		String value = typeMirror.getKind() == TypeKind.CHAR ? "String.valueOf(" + rhs.format() + ")" : rhs.format();
		if (lhs instanceof LHS.Return) {
			String suffix = switch (typeMirror.getKind()) {
				case BOOLEAN -> "boolean";
				case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE -> "number";
				case CHAR -> "text";
				default -> throw new UnsupportedOperationException(stack() + " unsupported type " + typeMirror.getKind());
			};
			code.addStatement("return $T.instance.$LNode(" + value + ")", flatten(jsonNodeFactory, suffix, rhs.args()));
		} else if(lhs instanceof LHS.Array) {
			code.addStatement("$L.add(" + value + ")", flatten(parent.nodeName(), rhs.args()));
		} else if (lhs instanceof LHS.Field f) {
			code.addStatement("$L.put(" + f.format() + ", " + value + ")", flatten(parent.nodeName(), f.args(), rhs.args()));
		}
	}

	@Override
	protected void startArray() {
		nodeName = propertyName() + "$" + stackDepth() + "$node";
		if (lhs instanceof LHS.Return) {
			code.addStatement("$T $L = $T.instance.arrayNode()", arrayNode, nodeName(), jsonNodeFactory);
		} else if (lhs instanceof LHS.Array) {
			code.addStatement("$T $L = $L.addArray()", arrayNode, nodeName(), parent.nodeName());
		} else if(lhs instanceof LHS.Field f) {
			code.addStatement("$T $L = $L.putArray(" + f.format() + ")", flatten(arrayNode, nodeName(), parent.nodeName(), f.args()));
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
		} else if(lhs instanceof LHS.Array) {
			code.addStatement("$T $L = $L.addObject()", objectNode, nodeName(), parent.nodeName());
		} else if(lhs instanceof LHS.Field f) {
			code.addStatement("$T $L = $L.putObject(" + f.format() + ")", flatten(objectNode, nodeName(), parent.nodeName(), f.args()));
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
	protected JacksonJsonNodeWriterGenerator nest(TypeMirror type, LHS lhs, String propertyName, RHS rhs) {
		return new JacksonJsonNodeWriterGenerator(utils, utils.tf.getType(type), code, this, lhs, rhs, propertyName, prototype);
	}

	String nodeName() {
		return nodeName != null ? nodeName : parent.nodeName();
	}
}
