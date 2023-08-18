package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;

public class JacksonJsonParserReaderGenerator extends AbstractReaderGenerator<JacksonJsonParserReaderGenerator> {
	private final VariableElement parserVariable;

	public JacksonJsonParserReaderGenerator(AnnotationProcessorUtils utils, ExecutableElement method) {
		super(utils, utils.tf.getType(method.getReturnType()), AbstractWriterGenerator.Key.root(method.getParameters().get(0)), CodeBlock.builder(), AbstractWriterGenerator.Mode.ROOT, null);
		parserVariable = method.getParameters().get(0);
	}

	public JacksonJsonParserReaderGenerator(AnnotationProcessorUtils utils, Type type, AbstractWriterGenerator.Key key, CodeBlock.Builder code, VariableElement parserVariable, AbstractWriterGenerator.Mode mode, JacksonJsonParserReaderGenerator parent) {
		super(utils, type, key, code, mode, parent);
		this.parserVariable = parserVariable;
	}

	@Override
	protected void startFieldCase(boolean firstCase, String string) {
		if (firstCase) {
			code.beginControlFlow("if ($L.nextToken() == $T.FIELD_NAME && $S.equals($L.currentName()))", parserVariable.getSimpleName(), jsonToken(), string, parserVariable.getSimpleName());
		} else {
			code.nextControlFlow("else if ($L.currentToken() == $T.FIELD_NAME && $S.equals($L.currentName()))", parserVariable.getSimpleName(), jsonToken(), string, parserVariable.getSimpleName());
		}
	}

	@Override
	protected void startStringCase(boolean firstCase) {
		if (firstCase) {
			code.beginControlFlow("if ($L.nextToken() == $T.VALUE_STRING)", parserVariable.getSimpleName(), jsonToken());
		} else {
			code.nextControlFlow("else if ($L.currentToken() == $T.VALUE_STRING)", parserVariable.getSimpleName(), jsonToken());
		}
	}

	@Override
	protected void startNumberCase(boolean firstCase) {
		if (firstCase) {
			code.beginControlFlow("if ($L.nextToken().isNumeric())", parserVariable.getSimpleName());
		} else {
			code.nextControlFlow("else if ($L.currentToken().isNumeric())", parserVariable.getSimpleName());
		}
	}

	@Override
	protected void startObjectCase(boolean firstCase) {
		if (firstCase) {
			code.beginControlFlow("if ($L.nextToken() == $T.START_OBJECT)", parserVariable.getSimpleName(), jsonToken());
		} else {
			code.nextControlFlow("else if ($L.currentToken() == $T.START_OBJECT)", parserVariable.getSimpleName(), jsonToken());
		}
	}

	@Override
	protected void startArrayCase(boolean firstCase) {
		if (firstCase) {
			code.beginControlFlow("if ($L.nextToken() == $T.START_ARRAY)", parserVariable.getSimpleName(), jsonToken());
		} else {
			code.nextControlFlow("else if ($L.currentToken() == $T.START_ARRAY)", parserVariable.getSimpleName(), jsonToken());
		}
	}

	@Override
	protected void startBooleanCase(boolean firstCase) {
		if (firstCase) {
			code.beginControlFlow("if ($L.nextToken().isBoolean())", parserVariable.getSimpleName());
		} else {
			code.nextControlFlow("else if ($L.currentToken().isBoolean())", parserVariable.getSimpleName());
		}
	}

	@Override
	protected void startNullCase(boolean firstCase) {
		if (firstCase) {
			code.beginControlFlow("if ($L.nextToken() == $T.VALUE_NULL)", parserVariable.getSimpleName(), jsonToken());
		} else {
			code.nextControlFlow("else if ($L.currentToken() == $T.VALUE_NULL)", parserVariable.getSimpleName(), jsonToken());
		}
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
		if (mode == AbstractWriterGenerator.Mode.ROOT) {
			code.addStatement("return $L.$L()", parserVariable.getSimpleName(), readMethod);
		} else {
			code.addStatement("$L = $L.$L()", varName(), parserVariable.getSimpleName(), readMethod);
		}
	}

	@Override
	protected void readString() {
		if (mode == AbstractWriterGenerator.Mode.ROOT) {
			code.addStatement("return $L.getText()", parserVariable.getSimpleName());
		} else {
			code.addStatement("$L = $L.getText()", varName(), parserVariable.getSimpleName());
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
	protected JacksonJsonParserReaderGenerator nest(TypeMirror type, Key nestedKey, Mode mode) {
		return new JacksonJsonParserReaderGenerator(utils, utils.tf.getType(type), nestedKey, code, parserVariable, mode, this);
	}
}
