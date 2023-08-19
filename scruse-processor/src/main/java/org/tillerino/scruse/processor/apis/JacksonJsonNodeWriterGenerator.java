package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.util.accessor.ReadAccessor;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class JacksonJsonNodeWriterGenerator extends AbstractWriterGenerator<JacksonJsonNodeWriterGenerator> {
	private final ClassName jsonNodeFactory = ClassName.get("com.fasterxml.jackson.databind.node", "JsonNodeFactory");
	private final ClassName arrayNode = ClassName.get("com.fasterxml.jackson.databind.node", "ArrayNode");
	private final ClassName objectNode = ClassName.get("com.fasterxml.jackson.databind.node", "ObjectNode");

	public JacksonJsonNodeWriterGenerator(AnnotationProcessorUtils utils, ExecutableElement method) {
		super(utils, utils.tf.getType(method.getParameters().get(0).asType()), Key.root(method.getParameters().get(0).getSimpleName().toString()), CodeBlock.builder(), Mode.ROOT, null);
	}

	public JacksonJsonNodeWriterGenerator(AnnotationProcessorUtils utils, Type type, Key key, CodeBlock.Builder code, Mode mode, JacksonJsonNodeWriterGenerator parent) {
		super(utils, type, key, code, mode, parent);
	}

	@Override
	protected void writeNull() {
		if (mode == Mode.ROOT) {
			code.addStatement("return $T.instance.nullNode()", jsonNodeFactory);
		} else if(mode == Mode.IN_ARRAY) {
			code.addStatement("$L.addNull()", parent.nodeName());
		} else if(mode == Mode.IN_OBJECT) {
			code.addStatement("$L.putNull(" + key.keyDollar() + ")", parent.nodeName(), key.keyValue());
		} else {
			throw new IllegalStateException("Unknown mode " + mode);
		}
	}

	@Override
	protected void writeString(StringKind stringKind) {
		if (mode == Mode.ROOT) {
			switch (stringKind) {
				case STRING -> code.addStatement("return $T.instance.textNode($L)", jsonNodeFactory, varName());
				case CHAR_ARRAY -> code.addStatement("return $T.instance.textNode(new String($L))", jsonNodeFactory, varName());
			}
		} else if(mode == Mode.IN_ARRAY) {
			code.addStatement("$L.add($L)", parent.nodeName(), varName());
		} else if(mode == Mode.IN_OBJECT) {
			code.addStatement("$L.put(" + key.keyDollar() + ", $L)", parent.nodeName(), key.keyValue(), varName());
		} else {
			throw new IllegalStateException("Unknown mode " + mode);
		}
	}

	@Override
	protected void writeBinary(BinaryKind binaryKind) {
		if (mode == Mode.ROOT) {
			code.addStatement("return $T.instance.binaryNode($L)", jsonNodeFactory, varName());
		} else if(mode == Mode.IN_ARRAY) {
			code.addStatement("$L.add($L)", parent.nodeName(), varName());
		} else if(mode == Mode.IN_OBJECT) {
			code.addStatement("$L.put(" + key.keyDollar() + ", $L)", parent.nodeName(), key.keyValue(), varName());
		} else {
			throw new IllegalStateException("Unknown mode " + mode);
		}
	}

	@Override
	public void writePrimitive(TypeMirror typeMirror) {
		String value = typeMirror.getKind() == TypeKind.CHAR ? "String.valueOf($L)" : "$L";
		if (mode == Mode.ROOT) {
			String suffix = switch (typeMirror.getKind()) {
				case BOOLEAN -> "boolean";
				case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE -> "number";
				case CHAR -> "text";
				default -> throw new UnsupportedOperationException(stack() + " unsupported type " + typeMirror.getKind());
			};
			code.addStatement("return $T.instance.$LNode(" + value + ")", jsonNodeFactory, suffix, varName());
		} else if(mode == Mode.IN_ARRAY) {
			code.addStatement("$L.add(" + value + ")", parent.nodeName(), varName());
		} else {
			code.addStatement("$L.put(" + key.keyDollar() + ", " + value + ")", parent.nodeName(), key.keyValue(), varName());
		}
	}

	@Override
	protected void startArray() {
		if (mode == Mode.ROOT) {
			code.addStatement("$T $L = $T.instance.arrayNode()", arrayNode, nodeName(), jsonNodeFactory);
		} else if(mode == Mode.IN_ARRAY) {
			code.addStatement("$T $L = $L.addArray()", arrayNode, nodeName(), parent.nodeName());
		} else if(mode == Mode.IN_OBJECT) {
			code.addStatement("$T $L = $L.putArray(" + key.keyDollar() + ")", arrayNode, nodeName(), parent.nodeName(), key.keyValue());
		} else {
			throw new IllegalStateException("Unknown mode " + mode);
		}
	}

	@Override
	protected void endArray() {
		if (mode == Mode.ROOT) {
			code.addStatement("return $L", nodeName());
		}
	}

	@Override
	protected boolean writePrimitiveField(String propertyName, ReadAccessor accessor) {
		if (accessor.getAccessedType().getKind() == TypeKind.CHAR) {
			code.addStatement("$L.put($S, String.valueOf($L.$L))", nodeName(), propertyName, varName(), accessor.getReadValueSource());
		} else {
			code.addStatement("$L.put($S, $L.$L)", nodeName(), propertyName, varName(), accessor.getReadValueSource());
		}
		return true;
	}

	@Override
	protected void startObject() {
		if (mode == Mode.ROOT) {
			code.addStatement("$T $L = $T.instance.objectNode()", objectNode, nodeName(), jsonNodeFactory);
		} else if(mode == Mode.IN_ARRAY) {
			code.addStatement("$T $L = $L.addObject()", objectNode, nodeName(), parent.nodeName());
		} else if(mode == Mode.IN_OBJECT) {
			code.addStatement("$T $L = $L.putObject(" + key.keyDollar() + ")", objectNode, nodeName(), parent.nodeName(), key.keyDollar());
		} else {
			throw new IllegalStateException("Unknown mode " + mode);
		}
	}

	@Override
	protected void endObject() {
		if (mode == Mode.ROOT) {
			code.addStatement("return $L", nodeName());
		}
	}

	@Override
	protected JacksonJsonNodeWriterGenerator nest(TypeMirror type, Key key, Mode mode) {
		return new JacksonJsonNodeWriterGenerator(utils, utils.tf.getType(type), key, code, mode, this);
	}

	String nodeName() {
		return varName() + "$node";
	}
}
