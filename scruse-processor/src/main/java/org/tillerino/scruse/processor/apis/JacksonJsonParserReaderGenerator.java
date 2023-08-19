package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;

public class JacksonJsonParserReaderGenerator extends AbstractReaderGenerator<JacksonJsonParserReaderGenerator> {
	private final VariableElement parserVariable;

	public JacksonJsonParserReaderGenerator(AnnotationProcessorUtils utils, ExecutableElement method) {
		super(utils, utils.tf.getType(method.getReturnType()), null, CodeBlock.builder(), null, new LHS.Return());
		parserVariable = method.getParameters().get(0);
	}

	public JacksonJsonParserReaderGenerator(AnnotationProcessorUtils utils, Type type, String propertyName, CodeBlock.Builder code, VariableElement parserVariable, LHS lhs, JacksonJsonParserReaderGenerator parent) {
		super(utils, type, propertyName, code, parent, lhs);
		this.parserVariable = parserVariable;
	}

	@Override
	protected void startFieldCase(Case casey, Token token, String string) {
		casey.controlFlow(code, "$L.$L() == $T.FIELD_NAME && $S.equals($L.currentName()))", parserVariable.getSimpleName(), token(token), jsonToken(), string, parserVariable.getSimpleName());
	}

	@Override
	protected void startStringCase(Case casey, Token token) {
		casey.controlFlow(code, "$L.$L() == $T.VALUE_STRING", parserVariable.getSimpleName(), token(token), jsonToken());
	}

	@Override
	protected void startNumberCase(Case casey, Token token) {
		casey.controlFlow(code, "$L.$L().isNumeric()", parserVariable.getSimpleName(), token(token));
	}

	@Override
	protected void startObjectCase(Case casey, Token token) {
		casey.controlFlow(code, "$L.$L() == $T.START_OBJECT", parserVariable.getSimpleName(), token(token), jsonToken());
	}

	@Override
	protected void startArrayCase(Case casey, Token token) {
		casey.controlFlow(code, "$L.$L() == $T.START_ARRAY", parserVariable.getSimpleName(), token(token), jsonToken());
	}

	@Override
	protected void startBooleanCase(Case casey, Token token) {
		casey.controlFlow(code, "$L.$L().isBoolean()", parserVariable.getSimpleName(), token(token));
	}

	@Override
	protected void startNullCase(Case casey, Token token) {
		casey.controlFlow(code, "$L.$L() == $T.VALUE_NULL", parserVariable.getSimpleName(), token(token), jsonToken());
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
			code.addStatement("return $L.$L()", parserVariable.getSimpleName(), readMethod);
		} else if (lhs instanceof LHS.Variable v) {
			code.addStatement("$L = $L.$L()", v.name(), parserVariable.getSimpleName(), readMethod);
		} else if (lhs instanceof LHS.Array a) {
			code.addStatement("$L[$L++] = $L.$L()", a.arrayName(), a.indexName(), parserVariable.getSimpleName(), readMethod);
		} else {
			throw new AssertionError(lhs);
		}
	}

	@Override
	protected void readString(StringKind stringKind) {
		String conversion = switch (stringKind) {
			case STRING -> "";
			case CHAR_ARRAY -> ".toCharArray()";
		};
		if (lhs instanceof LHS.Return) {
			code.addStatement("return $L.getText()$L", parserVariable.getSimpleName(), conversion);
		} else if (lhs instanceof LHS.Variable v) {
			code.addStatement("$L = $L.getText()$L", v.name(), parserVariable.getSimpleName(), conversion);
		} else if (lhs instanceof LHS.Array a) {
			code.addStatement("$L[$L++] = $L.getText()$L", a.arrayName(), a.indexName(), parserVariable.getSimpleName(), conversion);
		} else {
			throw new AssertionError(lhs);
		}
	}

	@Override
	protected void iterateOverFields() {
		code.beginControlFlow("while ($L.nextToken() != $T.END_OBJECT)", parserVariable.getSimpleName(), jsonToken());
	}

	@Override
	protected void iterateOverElements() {
		code.beginControlFlow("while ($L.nextToken() != $T.END_ARRAY)", parserVariable.getSimpleName(), jsonToken());
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
	protected JacksonJsonParserReaderGenerator nest(TypeMirror type, @Nullable String propertyName, LHS lhs) {
		return new JacksonJsonParserReaderGenerator(utils, utils.tf.getType(type), propertyName, code, parserVariable, lhs, this);
	}

	private String token(Token token) {
		return switch (token) {
			case NEXT_TOKEN -> "nextToken";
			case CURRENT_TOKEN -> "currentToken";
		};
	}
}
