package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.helpers.JacksonJsonParserReaderHelper;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseMethod;

import javax.annotation.Nullable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.List;

public class JacksonJsonParserReaderGenerator extends AbstractReaderGenerator<JacksonJsonParserReaderGenerator> {
	private final VariableElement parserVariable;

	public JacksonJsonParserReaderGenerator(AnnotationProcessorUtils utils, ScruseMethod prototype) {
		super(prototype, utils, utils.tf.getType(prototype.methodElement().getReturnType()), null, CodeBlock.builder(), null, new LHS.Return());
		parserVariable = prototype.methodElement().getParameters().get(0);
	}

	public JacksonJsonParserReaderGenerator(ScruseMethod prototype, AnnotationProcessorUtils utils, Type type, String propertyName, CodeBlock.Builder code, VariableElement parserVariable, LHS lhs, JacksonJsonParserReaderGenerator parent) {
		super(prototype, utils, type, propertyName, code, parent, lhs);
		this.parserVariable = parserVariable;
	}

	@Override
	protected void startStringCase(Branch branch) {
		branch.controlFlow(code, "$L.currentToken() == $T.VALUE_STRING", parserVariable.getSimpleName(), jsonToken());
	}

	@Override
	protected void startNumberCase(Branch branch) {
		branch.controlFlow(code, "$L.currentToken().isNumeric()", parserVariable.getSimpleName());
	}

	@Override
	protected void startObjectCase(Branch branch) {
		branch.controlFlow(code, "$L.currentToken() == $T.START_OBJECT", parserVariable.getSimpleName(), jsonToken());
		advance();
	}

	@Override
	protected void startArrayCase(Branch branch) {
		branch.controlFlow(code, "$L.currentToken() == $T.START_ARRAY", parserVariable.getSimpleName(), jsonToken());
		advance();
	}

	@Override
	protected void startBooleanCase(Branch branch) {
		branch.controlFlow(code, "$L.currentToken().isBoolean()", parserVariable.getSimpleName());
	}

	@Override
	protected void startFieldCase(Branch branch) {
		branch.controlFlow(code, "$L.currentToken() == $T.FIELD_NAME", parserVariable.getSimpleName(), jsonToken());
	}

	@Override
	protected void initializeParser() {
		code.beginControlFlow("if (!$L.hasCurrentToken())", parserVariable.getSimpleName());
		advance();
		code.endControlFlow();
	}

	@Override
	protected void startNullCase(Branch branch) {
		branch.controlFlow(code, "$L.currentToken() == $T.VALUE_NULL", parserVariable.getSimpleName(), jsonToken());
		advance();
	}

	private TypeElement jsonToken() {
		return utils.elements.getTypeElement("com.fasterxml.jackson.core.JsonToken");
	}

	@Override
	protected void readPrimitive(TypeMirror type) {
		String readMethod = switch (type.getKind()) {
			case BOOLEAN -> "getBooleanValue";
			case BYTE -> "getByteValue";
			case SHORT -> "getShortValue";
			case INT -> "getIntValue";
			case LONG -> "getLongValue";
			case FLOAT -> "getFloatValue";
			case DOUBLE -> "getDoubleValue";
			default -> throw new AssertionError(type.getKind());
		};
		if (lhs instanceof LHS.Return) {
			String tmp = "tmp$" + stackDepth();
			code.addStatement("$T $L = $L.$L()", type, tmp, parserVariable.getSimpleName(), readMethod);
			advance();
			code.addStatement("return $L", tmp);
		} else {
			lhs.assign(code, "$L.$L()", parserVariable.getSimpleName(), readMethod);
			advance();
		}
	}

	@Override
	protected void readString(StringKind stringKind) {
		String conversion = switch (stringKind) {
			case STRING -> "";
			case CHAR_ARRAY -> ".toCharArray()";
		};
		if (lhs instanceof LHS.Return) {
			String tmp = "tmp$" + stackDepth();
			code.addStatement("$T $L = $L.getText()$L", stringKind == StringKind.STRING ? String.class : char[].class, tmp, parserVariable.getSimpleName(), conversion);
			advance();
			code.addStatement("return $L", tmp);
		} else {
			lhs.assign(code, "$L.getText()$L", parserVariable.getSimpleName(), conversion);
			advance();
		}
	}

	@Override
	protected void iterateOverFields() {
		// we immediately skip the END_OBJECT token once we encounter it
		code.beginControlFlow("while ($L.currentToken() != $T.END_OBJECT || $L.nextToken() == null && false)", parserVariable.getSimpleName(), jsonToken(), parserVariable.getSimpleName());
	}

	@Override
	protected void afterObject() {
		// we skipped the END_OBJECT token in the head of the loop
	}

	@Override
	protected void readFieldNameInIteration(String propertyName) {
		code.addStatement("String $L = $L.currentName()", propertyName, parserVariable.getSimpleName());
		advance();
	}

	@Override
	protected void readDiscriminator(String propertyName) {
		lhs.assign(code, "$T.readDiscriminator($S, $L)", JacksonJsonParserReaderHelper.class, propertyName, parserVariable.getSimpleName());
	}

	@Override
	protected void iterateOverElements() {
		// we immediately skip the END_ARRAY token once we encounter it
		code.beginControlFlow("while ($L.currentToken() != $T.END_ARRAY || $L.nextToken() == null && false)", parserVariable.getSimpleName(), jsonToken(), parserVariable.getSimpleName());
	}

	@Override
	protected void afterArray() {
		// we skipped the END_ARRAY token in the head of the loop
	}

	@Override
	protected void throwUnexpected(String expected) {
		code.addStatement("throw new $T($S + $L.currentToken() + $S + $L.getCurrentLocation())",
			IOException.class,
			"Expected " + expected + ", got ",
			parserVariable.getSimpleName(),
			" at ",
			parserVariable.getSimpleName());
	}

	@Override
	protected void invokeDelegate(String instance, String methodName, List<String> ownArguments) {
		lhs.assign(code, "$L.$L($L)", instance, methodName, String.join(", ", ownArguments));
	}

	@Override
	protected JacksonJsonParserReaderGenerator nest(TypeMirror type, @Nullable String propertyName, LHS lhs) {
		return new JacksonJsonParserReaderGenerator(prototype, utils, utils.tf.getType(type), propertyName, code, parserVariable, lhs, this);
	}

	private void advance() {
		code.addStatement("$L.nextToken()", parserVariable.getSimpleName());
	}
}
