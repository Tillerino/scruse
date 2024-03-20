package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.helpers.Fastjson2ReaderHelper;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScruseMethod;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

import javax.annotation.Nullable;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;

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
	protected void readNullable(Branch branch) {
		if (type.isArrayType()) {
			if (type.getComponentType().isString()) {
				lhs.assign(code, "$L.readStringArray()", parserVariable.getSimpleName());
				return;
			}
			if (type.getComponentType().getTypeMirror().getKind() == TypeKind.INT) {
				lhs.assign(code, "$L.readInt32ValueArray()", parserVariable.getSimpleName());
				return;
			}
			if (type.getComponentType().getTypeMirror().getKind() == TypeKind.LONG) {
				lhs.assign(code, "$L.readInt64ValueArray()", parserVariable.getSimpleName());
				return;
			}
		}
		super.readNullable(branch);
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
		return Snippet.of("$L.nextIfObjectStart()", parserVariable.getSimpleName());
	}

	@Override
	protected void startArrayCase(Branch branch) {
		branch.controlFlow(code, "$L.nextIfArrayStart()", parserVariable.getSimpleName());
	}

	@Override
	protected void startBooleanCase(Branch branch) {
		branch.controlFlow(code, "$L.current() == 'f' || $L.current() == 't'", parserVariable.getSimpleName(), parserVariable.getSimpleName());
	}

	@Override
	protected void startFieldCase(Branch branch) {
		branch.controlFlow(code, "$L.isString()", parserVariable.getSimpleName());
	}

	@Override
	protected void initializeParser() {
	}

	@Override
	protected Snippet nullCaseCondition() {
		return Snippet.of("$L.nextIfNull()", parserVariable.getSimpleName());
	}

	@Override
	protected void readPrimitive(TypeMirror type) {
		String readMethod = switch (type.getKind()) {
			case BOOLEAN -> "readBoolValue";
			case BYTE -> "readInt8Value";
			case SHORT -> "readInt16Value";
			case INT -> "readInt32Value";
			case LONG -> "readInt64Value";
			case FLOAT -> "readFloatValue";
			case DOUBLE -> "readDoubleValue";
			default -> throw new AssertionError(type.getKind());
		};
		lhs.assign(code, "$L.$L()", parserVariable.getSimpleName(), readMethod);
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
		code.addStatement("String $L = $L.readFieldName()", propertyName, parserVariable.getSimpleName());
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
	protected void invokeDelegate(String instance, InstantiatedMethod callee) {
		lhs.assign(code, Snippet.of("$L.$L($C)", instance, callee,
			Snippet.join(prototype.findArguments(callee, 0, generatedClass), ", ")));
	}

	@Override
	protected Fastjson2ReaderGenerator nest(TypeMirror type, @Nullable String propertyName, LHS lhs, boolean stackRelevantType) {
		return new Fastjson2ReaderGenerator(prototype, utils, utils.tf.getType(type), propertyName, code, parserVariable, lhs, this, stackRelevantType);
	}
}
