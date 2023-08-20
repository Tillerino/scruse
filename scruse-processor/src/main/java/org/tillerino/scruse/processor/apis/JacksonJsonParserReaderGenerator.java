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
		} else if (lhs instanceof LHS.Variable v) {
			code.addStatement("$L = $L.$L()", v.name(), parserVariable.getSimpleName(), readMethod);
			advance();
		} else if (lhs instanceof LHS.Array a) {
			code.addStatement("$L[$L++] = $L.$L()", a.arrayVar(), a.indexVar(), parserVariable.getSimpleName(), readMethod);
			advance();
		} else if (lhs instanceof LHS.Collection c) {
			code.addStatement("$L.add($L.$L())", c.variable(), parserVariable.getSimpleName(), readMethod);
			advance();
		} else if (lhs instanceof LHS.Map m) {
			code.addStatement("$L.put($L, $L.$L())", m.mapVar(), m.keyVar(), parserVariable.getSimpleName(), readMethod);
			advance();
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
			String tmp = "tmp$" + stackDepth();
			code.addStatement("$T $L = $L.getText()$L", stringKind == StringKind.STRING ? String.class : char[].class, tmp, parserVariable.getSimpleName(), conversion);
			advance();
			code.addStatement("return $L", tmp);
		} else if (lhs instanceof LHS.Variable v) {
			code.addStatement("$L = $L.getText()$L", v.name(), parserVariable.getSimpleName(), conversion);
			advance();
		} else if (lhs instanceof LHS.Array a) {
			code.addStatement("$L[$L++] = $L.getText()$L", a.arrayVar(), a.indexVar(), parserVariable.getSimpleName(), conversion);
			advance();
		} else if (lhs instanceof LHS.Collection c) {
			code.addStatement("$L.add($L.getText()$L)", c.variable(), parserVariable.getSimpleName(), conversion);
			advance();
		} else if (lhs instanceof LHS.Map m) {
			code.addStatement("$L.put($L, $L.getText()$L)", m.mapVar(), m.keyVar(), parserVariable.getSimpleName(), conversion);
			advance();
		} else {
			throw new AssertionError(lhs);
		}
	}

	@Override
	protected void iterateOverFields() {
		// we immediately skip the END_OBJECT token once we encounter it
		code.beginControlFlow("while ($L.currentToken() != $T.END_OBJECT || $L.nextToken() == null && false)", parserVariable.getSimpleName(), jsonToken(), parserVariable.getSimpleName());
	}

	@Override
	protected void readFieldName(String propertyName) {
		code.addStatement("String $L = $L.currentName()", propertyName, parserVariable.getSimpleName());
		advance();
	}

	@Override
	protected void iterateOverElements() {
		// we immediately skip the END_ARRAY token once we encounter it
		code.beginControlFlow("while ($L.currentToken() != $T.END_ARRAY || $L.nextToken() == null && false)", parserVariable.getSimpleName(), jsonToken(), parserVariable.getSimpleName());
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

	private void advance() {
		code.addStatement("$L.nextToken()", parserVariable.getSimpleName());
	}
}
