package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScruseMethod;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.stream.Collectors;

public class Fastjson2WriterGenerator extends AbstractWriterGenerator<Fastjson2WriterGenerator> {
	private final VariableElement writerVariable;

	public Fastjson2WriterGenerator(AnnotationProcessorUtils utils, ScruseMethod prototype, GeneratedClass generatedClass) {
		super(utils, prototype, generatedClass);
		this.writerVariable = prototype.methodElement().getParameters().get(1);
	}

	public Fastjson2WriterGenerator(ScruseMethod prototype, AnnotationProcessorUtils utils, Type type, CodeBlock.Builder code, VariableElement writerVariable, Fastjson2WriterGenerator parent, LHS lhs, RHS rhs, String propertyName, boolean stackRelevantType) {
		super(utils, parent.generatedClass, prototype, code, parent, type, propertyName, rhs, lhs, stackRelevantType);
		this.writerVariable = writerVariable;
	}

	@Override
	protected void writeNull() {
		addFieldNameIfNeeded();
		code.addStatement("$L.writeNull()", writerVariable.getSimpleName());
	}

	@Override
	protected void writeString(StringKind stringKind) {
		addFieldNameIfNeeded();
		switch (stringKind) {
			case STRING -> code.addStatement("$L.writeString(" + rhs.format() + ")", flatten(writerVariable.getSimpleName(), rhs.args()));
			case CHAR_ARRAY -> code.addStatement("$L.writeString(new String(" + rhs.format() + "))", flatten(writerVariable.getSimpleName(), rhs.args()));
		}
	}

	@Override
	protected void writeBinary(BinaryKind binaryKind) {
		switch (binaryKind) {
			case BYTE_ARRAY -> code.addStatement("$L.writeBase64(" + rhs.format() + ")", flatten(writerVariable.getSimpleName(), rhs.args()));
		}
	}

	@Override
	public void writePrimitive(TypeMirror typeMirror) {
		addFieldNameIfNeeded();
		TypeKind kind = typeMirror.getKind();
		if (kind == TypeKind.CHAR) {
			code.addStatement("$L.writeString(String.valueOf(" + rhs.format() + "))", flatten(writerVariable.getSimpleName(), rhs.args()));
		} else if (kind == TypeKind.FLOAT || kind == TypeKind.DOUBLE) {
			String write = kind == TypeKind.FLOAT ? "writeFloat" : "writeDouble";
			String cast = kind == TypeKind.FLOAT ? "(float)" : "(double)";
			code.beginControlFlow("if ($T.isFinite(" + rhs.format() + "))", flatten(kind == TypeKind.FLOAT ? Float.class : Double.class, rhs.args()))
					.addStatement("$L.$L($L " + rhs.format() + ")", flatten(writerVariable.getSimpleName(), write, cast, rhs.args()))
					.nextControlFlow("else")
					.addStatement("$L.writeString(String.valueOf(" + rhs.format() + "))", flatten(writerVariable.getSimpleName(), rhs.args()))
					.endControlFlow();
		} else {
			String write = switch (kind) {
				case BOOLEAN -> "writeBool";
				case BYTE -> "writeInt8";
				case SHORT -> "writeInt16";
				case INT -> "writeInt32";
				case LONG -> "writeInt64";
				default -> throw new AssertionError("Unexpected type: " + kind);
			};
			code.addStatement("$L.$L(" + rhs.format() + ")", flatten(writerVariable.getSimpleName(), write, rhs.args()));
		}
	}

	@Override
	protected void startArray() {
		addFieldNameIfNeeded();
		code.addStatement("$L.startArray()", writerVariable.getSimpleName());
	}

	@Override
	protected void endArray() {
		code.addStatement("$L.endArray()", writerVariable.getSimpleName());
	}

	@Override
	protected void startObject() {
		addFieldNameIfNeeded();
		code.addStatement("$L.startObject()", writerVariable.getSimpleName());
	}

	@Override
	boolean needsToWriteComma() {
		return true;
	}

	@Override
	protected void writeComma() {
		code.addStatement("$L.writeComma()", writerVariable.getSimpleName());
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
	protected Fastjson2WriterGenerator nest(TypeMirror type, LHS lhs, String propertyName, RHS rhs, boolean stackRelevantType) {
		return new Fastjson2WriterGenerator(prototype, utils, utils.tf.getType(type), code, writerVariable, this, lhs, rhs, propertyName, stackRelevantType);
	}

	private void addFieldNameIfNeeded() {
		if (lhs instanceof LHS.Field f) {
			code.addStatement("$L.writeName(" + f.format() + ")", flatten(writerVariable.getSimpleName(), f.args()));
			code.addStatement("$L.writeColon()", writerVariable.getSimpleName());
		}
	}
}
