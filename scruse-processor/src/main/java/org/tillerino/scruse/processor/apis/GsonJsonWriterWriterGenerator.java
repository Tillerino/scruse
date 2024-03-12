package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScruseMethod;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class GsonJsonWriterWriterGenerator extends AbstractWriterGenerator<GsonJsonWriterWriterGenerator> {
	private final VariableElement writerVariable;

	public GsonJsonWriterWriterGenerator(AnnotationProcessorUtils utils, ScruseMethod prototype, GeneratedClass generatedClass) {
		super(utils, prototype, generatedClass);
		this.writerVariable = prototype.methodElement().getParameters().get(1);
	}

	public GsonJsonWriterWriterGenerator(ScruseMethod prototype, AnnotationProcessorUtils utils, Type type, CodeBlock.Builder code, VariableElement writerVariable, GsonJsonWriterWriterGenerator parent, LHS lhs, RHS rhs, String propertyName, boolean stackRelevantType) {
		super(utils, parent.generatedClass, prototype, code, parent, type, propertyName, rhs, lhs, stackRelevantType);
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
	protected void invokeDelegate(String instance, String methodName, List<String> ownArguments) {
		addFieldNameIfNeeded();
		code.addStatement("$L.$L(" + rhs.format() + ownArguments.stream().skip(1).map(a -> ", " + a).collect(Collectors.joining("")) + ")",
			flatten(instance, methodName, rhs.args()));
	}

	@Override
	protected GsonJsonWriterWriterGenerator nest(TypeMirror type, LHS lhs, String propertyName, RHS rhs, boolean stackRelevantType) {
		return new GsonJsonWriterWriterGenerator(prototype, utils, utils.tf.getType(type), code, writerVariable, this, lhs, rhs, propertyName, stackRelevantType);
	}

	private void addFieldNameIfNeeded() {
		if (lhs instanceof LHS.Field f) {
			code.addStatement("$L.name(" + f.format() + ")", flatten(writerVariable.getSimpleName(), f.args()));
		}
	}
}
