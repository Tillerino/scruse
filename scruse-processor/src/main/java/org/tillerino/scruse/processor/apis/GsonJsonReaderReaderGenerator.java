package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.helpers.GsonJsonReaderHelper;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScruseMethod;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

import javax.annotation.Nullable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;

public class GsonJsonReaderReaderGenerator extends AbstractReaderGenerator<GsonJsonReaderReaderGenerator> {
	private final VariableElement parserVariable;

	public GsonJsonReaderReaderGenerator(AnnotationProcessorUtils utils, ScruseMethod prototype, GeneratedClass generatedClass) {
		super(utils, generatedClass, prototype, CodeBlock.builder(), null, utils.tf.getType(prototype.instantiatedReturnType()), true, null, new LHS.Return());
		parserVariable = prototype.methodElement().getParameters().get(0);
	}

	public GsonJsonReaderReaderGenerator(ScruseMethod prototype, AnnotationProcessorUtils utils, Type type, String propertyName, CodeBlock.Builder code, VariableElement parserVariable, LHS lhs, GsonJsonReaderReaderGenerator parent, boolean stackRelevantType) {
		super(utils, parent.generatedClass, prototype, code, parent, type, stackRelevantType, propertyName, lhs);
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
	protected Snippet objectCaseCondition() {
		return Snippet.of("$T.isBeginObject($L, true)", GsonJsonReaderHelper.class, parserVariable.getSimpleName());
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
	protected Snippet nullCaseCondition() {
		return Snippet.of("$T.isNull($L, true)", GsonJsonReaderHelper.class, parserVariable.getSimpleName());
	}

	private TypeElement jsonToken() {
		return utils.elements.getTypeElement("com.google.gson.stream.JsonToken");
	}

	@Override
	protected void readPrimitive(TypeMirror type) {
		record R(String cast, String method) {
		}
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
		code.beginControlFlow("while ($L.peek() != $T.END_OBJECT)", parserVariable.getSimpleName(), jsonToken());
	}

	@Override
	protected void afterObject() {
		code.addStatement("$L.endObject()", parserVariable.getSimpleName());
	}

	@Override
	protected void readFieldNameInIteration(String propertyName) {
		code.addStatement("String $L = $L.nextName()", propertyName, parserVariable.getSimpleName());
	}

	@Override
	protected void readDiscriminator(String propertyName) {
		lhs.assign(code, "$T.readDiscriminator($S, $L)", GsonJsonReaderHelper.class, propertyName, parserVariable.getSimpleName());
	}

	@Override
	protected void iterateOverElements() {
		code.beginControlFlow("while ($L.peek() != $T.END_ARRAY)", parserVariable.getSimpleName(), jsonToken());
	}

	@Override
	protected void afterArray() {
		code.addStatement("$L.endArray()", parserVariable.getSimpleName());
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
	protected void invokeDelegate(String instance, InstantiatedMethod callee) {
		lhs.assign(code, Snippet.of("$L.$L($C)", instance, callee,
			Snippet.join(prototype.findArguments(callee, 0, generatedClass), ", ")));
	}

	@Override
	protected GsonJsonReaderReaderGenerator nest(TypeMirror type, @Nullable String propertyName, LHS lhs, boolean stackRelevantType) {
		return new GsonJsonReaderReaderGenerator(prototype, utils, utils.tf.getType(type), propertyName, code, parserVariable, lhs, this, stackRelevantType);
	}
}
