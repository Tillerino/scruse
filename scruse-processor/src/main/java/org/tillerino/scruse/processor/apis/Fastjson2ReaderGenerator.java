package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.helpers.Fastjson2ReaderHelper;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScruseMethod;

import javax.annotation.Nullable;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.List;

public class Fastjson2ReaderGenerator extends AbstractReaderGenerator<Fastjson2ReaderGenerator> {

	private final VariableElement parserVariable;

	public Fastjson2ReaderGenerator(AnnotationProcessorUtils utils, ScruseMethod prototype, GeneratedClass generatedClass) {
		super(utils, generatedClass, prototype, CodeBlock.builder(), null, utils.tf.getType(prototype.methodElement().getReturnType()), true, null, new LHS.Return());
		parserVariable = prototype.methodElement().getParameters().get(0);
	}

	public Fastjson2ReaderGenerator(ScruseMethod prototype, AnnotationProcessorUtils utils, Type type, String propertyName, CodeBlock.Builder code, VariableElement parserVariable, LHS lhs, Fastjson2ReaderGenerator parent, boolean stackRelevantType) {
		super(utils, parent.generatedClass, prototype, code, parent, type, stackRelevantType, propertyName, lhs);
		this.parserVariable = parserVariable;
	}

	@Override
	protected void startStringCase(Branch branch) {
		branch.controlFlow(code, "$L.isString()", parserVariable.getSimpleName());
	}

	@Override
	protected void startNumberCase(Branch branch) {
		branch.controlFlow(code, "$L.isNumber()", parserVariable.getSimpleName());
	}

	@Override
	protected Snippet objectCaseCondition() {
		return new Snippet("$L.nextIfObjectStart()", parserVariable.getSimpleName());
	}

	@Override
	protected void startArrayCase(Branch branch) {
		branch.controlFlow(code, "$L.nextIfArrayStart()", parserVariable.getSimpleName());
	}

	@Override
	protected void startBooleanCase(Branch branch) {
		branch.controlFlow(code, "(((Object) $L.readBool()) instanceof Boolean $L)", parserVariable.getSimpleName(), "$" + stackDepth() + "$bool");
	}

	@Override
	protected void startFieldCase(Branch branch) {
		branch.controlFlow(code, "(((Object) $L.readFieldName()) instanceof String $L)", parserVariable.getSimpleName(), "$" + stackDepth() + "$name");
	}

	@Override
	protected void initializeParser() {
	}

	@Override
	protected Snippet nullCaseCondition() {
		return new Snippet("$L.nextIfNull()", parserVariable.getSimpleName());
	}

	@Override
	protected void readPrimitive(TypeMirror type) {
		record R(String cast, String method) {
		}
		if (type.getKind() == TypeKind.BOOLEAN) {
			lhs.assign(code, "$L", "$" + stackDepth() + "$bool");
			return;
		}
		R readMethod = switch (type.getKind()) {
			case BYTE -> new R("(byte) ", "readInt8Value");
			case SHORT -> new R("(short) ", "readInt16Value");
			case INT -> new R("", "readInt32Value");
			case LONG -> new R("", "readInt64Value");
			case FLOAT -> new R("(float) ", "readFloatValue");
			case DOUBLE -> new R("", "readDoubleValue");
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
		lhs.assign(code, "$L.readString()$L", parserVariable.getSimpleName(), conversion);
	}

	@Override
	protected void iterateOverFields() {
		code.beginControlFlow("while (!$L.nextIfObjectEnd())", parserVariable.getSimpleName());
	}

	@Override
	protected void afterObject() {
	}

	@Override
	protected void readFieldNameInIteration(String propertyName) {
		code.addStatement("String $L = $L", propertyName, "$" + stackDepth() + "$name");
	}

	@Override
	protected void readDiscriminator(String propertyName) {
		lhs.assign(code, "$T.readDiscriminator($S, $L)", Fastjson2ReaderHelper.class, propertyName, parserVariable.getSimpleName());
	}

	@Override
	protected void iterateOverElements() {
		code.beginControlFlow("while (!$L.nextIfArrayEnd())", parserVariable.getSimpleName());
	}

	@Override
	protected void afterArray() {
	}

	@Override
	protected void throwUnexpected(String expected) {
		code.addStatement("throw new $T($S + $L.current())",
				IOException.class,
				"Expected " + expected + ", got ",
				parserVariable.getSimpleName());
	}

	@Override
	protected void invokeDelegate(String instance, String methodName, List<String> ownArguments) {
		lhs.assign(code, "$L.$L($L)", instance, methodName, String.join(", ", ownArguments));
	}

	@Override
	protected Fastjson2ReaderGenerator nest(TypeMirror type, @Nullable String propertyName, LHS lhs, boolean stackRelevantType) {
		return new Fastjson2ReaderGenerator(prototype, utils, utils.tf.getType(type), propertyName, code, parserVariable, lhs, this, stackRelevantType);
	}
}
