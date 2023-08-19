package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.util.accessor.ReadAccessor;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Base64;

public class GsonJsonWriterWriterGenerator extends AbstractWriterGenerator<GsonJsonWriterWriterGenerator> {
	private final VariableElement writerVariable;

	public GsonJsonWriterWriterGenerator(AnnotationProcessorUtils utils, ExecutableElement method) {
		super(utils, utils.tf.getType(method.getParameters().get(0).asType()), Key.root(method.getParameters().get(0).getSimpleName().toString()), CodeBlock.builder(), Mode.ROOT, null);
		this.writerVariable = method.getParameters().get(1);
	}

	public GsonJsonWriterWriterGenerator(AnnotationProcessorUtils utils, Type type, Key key, CodeBlock.Builder code, VariableElement writerVariable, Mode mode, GsonJsonWriterWriterGenerator parent) {
		super(utils, type, key, code, mode, parent);
		this.writerVariable = writerVariable;
	}

	@Override
	protected void writeNull() {
		addFieldNameIfNeeded();
		code.addStatement("$L.nullValue()", writerVariable.getSimpleName());
	}

	@Override
	protected void writeString(StringKind stringKind) {
		addFieldNameIfNeeded();
		switch (stringKind) {
			case STRING -> code.addStatement("$L.value($L)", writerVariable.getSimpleName(), varName());
			case CHAR_ARRAY -> code.addStatement("$L.value(new String($L))", writerVariable.getSimpleName(), varName());
		}
	}

	@Override
	protected void writeBinary(BinaryKind binaryKind) {
		switch (binaryKind) {
			case BYTE_ARRAY -> code.addStatement("$L.value($T.getEncoder().encodeToString($L))", writerVariable.getSimpleName(), Base64.class, varName());
		}
	}

	@Override
	public void writePrimitive(TypeMirror typeMirror) {
		addFieldNameIfNeeded();
		TypeKind kind = typeMirror.getKind();
		if (kind == TypeKind.CHAR) {
			code.addStatement("$L.value(String.valueOf($L))", writerVariable.getSimpleName(), varName());
		} else if (kind == TypeKind.FLOAT || kind == TypeKind.DOUBLE) {
			code.beginControlFlow("if ($T.isFinite($L))", kind == TypeKind.FLOAT ? Float.class : Double.class, varName())
				.addStatement("$L.value($L)", writerVariable.getSimpleName(), varName())
				.nextControlFlow("else")
				.addStatement("$L.value(String.valueOf($L))", writerVariable.getSimpleName(), varName())
				.endControlFlow();
		} else {
			code.addStatement("$L.value($L)", writerVariable.getSimpleName(), varName());
		}
	}

	@Override
	protected void startArray() {
		addFieldNameIfNeeded();
		code.addStatement("$L.beginArray()", writerVariable.getSimpleName());
	}

	@Override
	protected void endArray() {
		code.addStatement("$L.endArray()", writerVariable.getSimpleName());
	}

	@Override
	protected boolean writePrimitiveField(String propertyName, ReadAccessor accessor) {
		TypeKind kind = accessor.getAccessedType().getKind();
		if(kind == TypeKind.FLOAT || kind == TypeKind.DOUBLE) {
			return false;
		}

		code.addStatement("$L.name($S)", writerVariable.getSimpleName(), propertyName);
		if (kind == TypeKind.CHAR) {
			code.addStatement("$L.value(String.valueOf($L.$L))", writerVariable.getSimpleName(), varName(), accessor.getReadValueSource());
		} else {
			code.addStatement("$L.value($L.$L)", writerVariable.getSimpleName(), varName(), accessor.getReadValueSource());
		}
		return true;
	}

	@Override
	protected void startObject() {
		addFieldNameIfNeeded();
		code.addStatement("$L.beginObject()", writerVariable.getSimpleName());
	}

	@Override
	protected void endObject() {
		code.addStatement("$L.endObject()", writerVariable.getSimpleName());
	}

	@Override
	protected GsonJsonWriterWriterGenerator nest(TypeMirror type, Key key, Mode mode) {
		return new GsonJsonWriterWriterGenerator(utils, utils.tf.getType(type), key, code, writerVariable, mode, this);
	}

	private void addFieldNameIfNeeded() {
		if (mode == Mode.IN_OBJECT) {
			code.addStatement("$L.name(" + key.keyDollar() + ")", writerVariable.getSimpleName(), key.keyValue());
		}
	}
}
