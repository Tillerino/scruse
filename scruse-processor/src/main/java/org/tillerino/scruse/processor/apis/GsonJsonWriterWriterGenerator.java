package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Base64;

public class GsonJsonWriterWriterGenerator extends AbstractWriterGenerator<GsonJsonWriterWriterGenerator> {
	private final VariableElement writerVariable;

	public GsonJsonWriterWriterGenerator(AnnotationProcessorUtils utils, ExecutableElement method) {
		super(utils, method);
		this.writerVariable = method.getParameters().get(1);
	}

	public GsonJsonWriterWriterGenerator(AnnotationProcessorUtils utils, Type type, CodeBlock.Builder code, VariableElement writerVariable, GsonJsonWriterWriterGenerator parent, LHS lhs, RHS rhs, String propertyName) {
		super(utils, type, code, parent, lhs, propertyName, rhs);
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
			case STRING -> code.addStatement("$L.value(" + rhs.format() + ")", flatten(writerVariable.getSimpleName(), rhs.args()));
			case CHAR_ARRAY -> code.addStatement("$L.value(new String(" + rhs.format() + "))", flatten(writerVariable.getSimpleName(), rhs.args()));
		}
	}

	@Override
	protected void writeBinary(BinaryKind binaryKind) {
		switch (binaryKind) {
			case BYTE_ARRAY -> code.addStatement("$L.value($T.getEncoder().encodeToString(" + rhs.format() + "))", flatten(writerVariable.getSimpleName(), Base64.class, rhs.args()));
		}
	}

	@Override
	public void writePrimitive(TypeMirror typeMirror) {
		addFieldNameIfNeeded();
		TypeKind kind = typeMirror.getKind();
		if (kind == TypeKind.CHAR) {
			code.addStatement("$L.value(String.valueOf(" + rhs.format() + "))", flatten(writerVariable.getSimpleName(), rhs.args()));
		} else if (kind == TypeKind.FLOAT || kind == TypeKind.DOUBLE) {
			code.beginControlFlow("if ($T.isFinite(" + rhs.format() + "))", flatten(kind == TypeKind.FLOAT ? Float.class : Double.class, rhs.args()))
				.addStatement("$L.value(" + rhs.format() + ")", flatten(writerVariable.getSimpleName(), rhs.args()))
				.nextControlFlow("else")
				.addStatement("$L.value(String.valueOf(" + rhs.format() + "))", flatten(writerVariable.getSimpleName(), rhs.args()))
				.endControlFlow();
		} else {
			code.addStatement("$L.value(" + rhs.format() + ")", flatten(writerVariable.getSimpleName(), rhs.args()));
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
	protected void startObject() {
		addFieldNameIfNeeded();
		code.addStatement("$L.beginObject()", writerVariable.getSimpleName());
	}

	@Override
	protected void endObject() {
		code.addStatement("$L.endObject()", writerVariable.getSimpleName());
	}

	@Override
	protected GsonJsonWriterWriterGenerator nest(TypeMirror type, LHS lhs, String propertyName, RHS rhs) {
		return new GsonJsonWriterWriterGenerator(utils, utils.tf.getType(type), code, writerVariable, this, lhs, rhs, propertyName);
	}

	private void addFieldNameIfNeeded() {
		if (lhs instanceof LHS.Field f) {
			code.addStatement("$L.name(" + f.format() + ")", flatten(writerVariable.getSimpleName(), f.args()));
		}
	}
}
