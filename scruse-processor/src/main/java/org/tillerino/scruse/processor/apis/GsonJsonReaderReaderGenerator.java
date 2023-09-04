package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseMethod;

import javax.annotation.Nullable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;

public class GsonJsonReaderReaderGenerator extends AbstractReaderGenerator<GsonJsonReaderReaderGenerator> {
	private final VariableElement parserVariable;

	public GsonJsonReaderReaderGenerator(AnnotationProcessorUtils utils, ScruseMethod prototype) {
		super(prototype, utils, utils.tf.getType(prototype.methodElement().getReturnType()), null, CodeBlock.builder(), null, new LHS.Return());
		parserVariable = prototype.methodElement().getParameters().get(0);
	}

	public GsonJsonReaderReaderGenerator(ScruseMethod prototype, AnnotationProcessorUtils utils, Type type, String propertyName, CodeBlock.Builder code, VariableElement parserVariable, LHS lhs, GsonJsonReaderReaderGenerator parent) {
		super(prototype, utils, type, propertyName, code, parent, lhs);
		this.parserVariable = parserVariable;
	}

	@Override
	protected void startStringCase(Branch branch) {
		branch.controlFlow(code, "$L.peek() == $T.STRING", parserVariable.getSimpleName(), jsonToken());
	}

	@Override
	protected void startNumberCase(Branch branch) {
		branch.controlFlow(code, "$L.peek() == $T.NUMBER", parserVariable.getSimpleName(), jsonToken());
	}

	@Override
	protected void startObjectCase(Branch branch) {
		branch.controlFlow(code, "$L.peek() == $T.BEGIN_OBJECT", parserVariable.getSimpleName(), jsonToken());
		code.addStatement("$L.beginObject()", parserVariable.getSimpleName());
	}

	@Override
	protected void startArrayCase(Branch branch) {
		branch.controlFlow(code, "$L.peek() == $T.BEGIN_ARRAY", parserVariable.getSimpleName(), jsonToken());
		code.addStatement("$L.beginArray()", parserVariable.getSimpleName());
	}

	@Override
	protected void startBooleanCase(Branch branch) {
		branch.controlFlow(code, "$L.peek() == $T.BOOLEAN", parserVariable.getSimpleName(), jsonToken());
	}

	@Override
	protected void startFieldCase(Branch branch) {
		branch.controlFlow(code, "$L.peek() == $T.NAME", parserVariable.getSimpleName(), jsonToken());
	}

	@Override
	protected void initializeParser() {
	}

	@Override
	protected void startNullCase(Branch branch) {
		branch.controlFlow(code, "$L.peek() == $T.NULL", parserVariable.getSimpleName(), jsonToken());
		code.addStatement("$L.nextNull()", parserVariable.getSimpleName());
	}

	private TypeElement jsonToken() {
		return utils.elements.getTypeElement("com.google.gson.stream.JsonToken");
	}

	private TypeElement jsonReaderHelper() {
		return utils.elements.getTypeElement("org.tillerino.scruse.libs.gson.JsonReaderHelper");
	}

	@Override
	protected void readPrimitive(TypeMirror type) {
		record R(String cast, String method) {}
		R readMethod = switch (type.getKind()) {
			case BOOLEAN -> new R("", "nextBoolean");
			case BYTE -> new R("(byte) ", "nextInt");
			case SHORT -> new R("(short) ", "nextInt");
			case INT -> new R("", "nextInt");
			case LONG -> new R("", "nextLong");
			case FLOAT -> new R("(float) ", "nextDouble");
			case DOUBLE -> new R("", "nextDouble");
			default -> throw new AssertionError(type.getKind());
		};
		lhs.assign(code, "$L$L.$L()", readMethod.cast, parserVariable.getSimpleName(), readMethod.method);
	}

	@Override
	protected void readString(StringKind stringKind) {
		String conversion = switch (stringKind) {
			case STRING -> "";
			case CHAR_ARRAY -> ".toCharArray()";
		};
		lhs.assign(code, "$L.nextString()$L", parserVariable.getSimpleName(), conversion);
	}

	@Override
	protected void iterateOverFields() {
		// we immediately skip the END_OBJECT token once we encounter it
		code.beginControlFlow("while ($L.peek() != $T.END_OBJECT || $T.skipEndObject($L))",
			parserVariable.getSimpleName(), jsonToken(), jsonReaderHelper(), parserVariable.getSimpleName());
	}

	@Override
	protected void readFieldName(String propertyName) {
		code.addStatement("String $L = $L.nextName()", propertyName, parserVariable.getSimpleName());
	}

	@Override
	protected void iterateOverElements() {
		// we immediately skip the END_ARRAY token once we encounter it
		code.beginControlFlow("while ($L.peek() != $T.END_ARRAY || $T.skipEndArray($L))",
			parserVariable.getSimpleName(), jsonToken(), jsonReaderHelper(), parserVariable.getSimpleName());
	}

	@Override
	protected void throwUnexpected(String expected) {
		code.addStatement("throw new $T($S + $L.peek() + $S + $L.getPath())",
			IOException.class,
			"Expected " + expected + ", got ",
			parserVariable.getSimpleName(),
			" at ",
			parserVariable.getSimpleName());
	}

	@Override
	protected GsonJsonReaderReaderGenerator nest(TypeMirror type, @Nullable String propertyName, LHS lhs) {
		return new GsonJsonReaderReaderGenerator(prototype, utils, utils.tf.getType(type), propertyName, code, parserVariable, lhs, this);
	}
}
