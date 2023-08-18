package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.util.accessor.ReadAccessor;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class JacksonJsonGeneratorWriterGenerator extends AbstractWriterGenerator<JacksonJsonGeneratorWriterGenerator> {
	private final VariableElement generatorVariable;

	public JacksonJsonGeneratorWriterGenerator(AnnotationProcessorUtils utils, ExecutableElement method) {
		super(utils, utils.tf.getType(method.getParameters().get(0).asType()), Key.root(method.getParameters().get(0)), CodeBlock.builder(), Mode.ROOT, null);
		this.generatorVariable = method.getParameters().get(1);
	}

	protected JacksonJsonGeneratorWriterGenerator(AnnotationProcessorUtils utils, Type type, Key key, CodeBlock.Builder code, VariableElement generatorVariable, Mode mode, JacksonJsonGeneratorWriterGenerator parent) {
		super(utils, type, key, code, mode, parent);
		this.generatorVariable = generatorVariable;
	}

	@Override
	protected void writeNull() {
		if (mode == Mode.IN_OBJECT) {
			code.addStatement("$L.writeNullField(" + key.keyDollar() + ")", generatorVariable.getSimpleName(), key.keyValue());
		} else {
			code.addStatement("$L.writeNull()", generatorVariable.getSimpleName());
		}
	}

	@Override
	protected void writeString(StringKind stringKind) {
		if (mode == Mode.IN_OBJECT) {
			code.addStatement("$L.writeFieldName(" + key.keyDollar() + ")", generatorVariable.getSimpleName(), key.keyValue());
		}
		switch (stringKind) {
			case STRING -> code.addStatement("$L.writeString($L)", generatorVariable.getSimpleName(), varName());
			case CHAR_ARRAY ->
				code.addStatement("$L.writeString($L, 0, $L.length)", generatorVariable.getSimpleName(), varName(), varName());
		}
	}

	@Override
	protected void writeBinary(BinaryKind binaryKind) {
		if (mode == Mode.IN_OBJECT) {
			code.addStatement("$L.writeFieldName(" + key.keyDollar() + ")", generatorVariable.getSimpleName(), key.keyValue());
		}
		switch (binaryKind) {
			case BYTE_ARRAY -> code.addStatement("$L.writeBinary($L)", generatorVariable.getSimpleName(), varName());
		}
	}

	@Override
	public void writePrimitive(TypeMirror typeMirror) {
		if (mode == Mode.IN_OBJECT) {
			if (typeMirror.getKind() == TypeKind.BOOLEAN) {
				code.addStatement("$L.writeBooleanField(" + key.keyDollar() + ", $L)", generatorVariable.getSimpleName(), key.keyValue(), varName());
			} else if (typeMirror.getKind() == TypeKind.CHAR) {
				code.addStatement("$L.writeStringField(" + key.keyDollar() + ", String.valueOf($L))", generatorVariable.getSimpleName(), key.keyValue(), varName());
			} else {
				code.addStatement("$L.writeNumberField(" + key.keyDollar() + ", $L)", generatorVariable.getSimpleName(), key.keyValue(), varName());
			}
		} else {
			if (typeMirror.getKind() == TypeKind.BOOLEAN) {
				code.addStatement("$L.writeBoolean($L)", generatorVariable.getSimpleName(), varName());
			} else if (typeMirror.getKind() == TypeKind.CHAR) {
				code.addStatement("$L.writeString(String.valueOf($L))", generatorVariable.getSimpleName(), varName());
			} else {
				code.addStatement("$L.writeNumber($L)", generatorVariable.getSimpleName(), varName());
			}
		}
	}

	@Override
	protected void startArray() {
		if (mode == Mode.IN_OBJECT) {
			code.addStatement("$L.writeFieldName(" + key.keyDollar() + ")", generatorVariable.getSimpleName(), key.keyValue());
		}
		code.addStatement("$L.writeStartArray()", generatorVariable.getSimpleName());
	}

	@Override
	protected void endArray() {
		code.addStatement("$L.writeEndArray()", generatorVariable.getSimpleName());
	}

	@Override
	protected void startObject() {
		if (mode == Mode.IN_OBJECT) {
			code.addStatement("$L.writeFieldName(" + key.keyDollar() + ")", generatorVariable.getSimpleName(), key.keyValue());
		}
		code.addStatement("$L.writeStartObject()", generatorVariable.getSimpleName());
	}

	@Override
	protected void endObject() {
		code.addStatement("$L.writeEndObject()", generatorVariable.getSimpleName());
	}

	@Override
	protected boolean writePrimitiveField(String propertyName, ReadAccessor accessor) {
		if (accessor.getAccessedType().getKind() == TypeKind.BOOLEAN) {
			code.addStatement("$L.writeBooleanField($S, $L.$L)", generatorVariable.getSimpleName(),
				propertyName, varName(), accessor.getReadValueSource());
		} else if (accessor.getAccessedType().getKind() == TypeKind.CHAR) {
			code.addStatement("$L.writeStringField($S, String.valueOf($L.$L))", generatorVariable.getSimpleName(),
				propertyName, varName(), accessor.getReadValueSource());
		} else {
			code.addStatement("$L.writeNumberField($S, $L.$L)", generatorVariable.getSimpleName(),
				propertyName, varName(), accessor.getReadValueSource());
		}
		return true;
	}

	@Override
	protected JacksonJsonGeneratorWriterGenerator nest(TypeMirror type, Key key, Mode mode) {
		return new JacksonJsonGeneratorWriterGenerator(utils, utils.tf.getType(type), key, code, generatorVariable, mode, this);
	}

}
