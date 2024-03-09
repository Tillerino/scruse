package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.helpers.JacksonJsonNodeReaderHelper;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScruseMethod;

import javax.annotation.Nullable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JacksonJsonNodeReaderGenerator extends AbstractReaderGenerator<JacksonJsonNodeReaderGenerator> {
	private final VariableElement parserVariable;

	private String nodeVarOverride = null;

	private String fieldVar;

	public JacksonJsonNodeReaderGenerator(AnnotationProcessorUtils utils, ScruseMethod prototype, GeneratedClass generatedClass) {
		super(prototype, utils, generatedClass, null, CodeBlock.builder(), null, new LHS.Return(), utils.tf.getType(prototype.methodElement().getReturnType()));
		parserVariable = prototype.methodElement().getParameters().get(0);
	}

	public JacksonJsonNodeReaderGenerator(ScruseMethod prototype, AnnotationProcessorUtils utils, Type type, String propertyName, CodeBlock.Builder code, VariableElement parserVariable, LHS lhs, JacksonJsonNodeReaderGenerator parent) {
		super(prototype, utils, parent.generatedClass, propertyName, code, parent, lhs, type);
		this.parserVariable = parserVariable;
	}

	@Override
	protected void startStringCase(Branch branch) {
		branch.controlFlow(code, "$L.isTextual()", nodeVar());
	}

	@Override
	protected void startNumberCase(Branch branch) {
		branch.controlFlow(code, "$L.isNumber()", nodeVar());
	}

	@Override
	protected Snippet objectCaseCondition() {
		return new Snippet("$L.isObject()", nodeVar());
	}

	@Override
	protected void startArrayCase(Branch branch) {
		branch.controlFlow(code, "$L.isArray()", nodeVar());
	}

	@Override
	protected void startBooleanCase(Branch branch) {
		branch.controlFlow(code, "$L.isBoolean()", nodeVar());
	}

	@Override
	protected void startFieldCase(Branch branch) {
		if (fieldVar == null) {
			throw new AssertionError();
		}
		branch.controlFlow(code, "true", fieldVar);
	}

	@Override
	protected void initializeParser() {
	}

	@Override
	protected Snippet nullCaseCondition() {
		return new Snippet("$L.isNull()", nodeVar());
	}

	@Override
	protected void readPrimitive(TypeMirror type) {
		record R(String cast, String method) {}
		R readMethod = switch (type.getKind()) {
			case BOOLEAN -> new R("", "asBoolean");
			case BYTE -> new R("(byte) ", "asInt");
			case SHORT -> new R("(short) ", "asInt");
			case INT -> new R("", "asInt");
			case LONG -> new R("", "asLong");
			case FLOAT -> new R("(float) ", "asDouble");
			case DOUBLE -> new R("", "asDouble");
			default -> throw new AssertionError(type.getKind());
		};
		lhs.assign(code, "$L$L.$L()", readMethod.cast, nodeVar(), readMethod.method);
	}

	@Override
	protected void readString(StringKind stringKind) {
		String conversion = switch (stringKind) {
			case STRING -> "";
			case CHAR_ARRAY -> ".toCharArray()";
		};
		lhs.assign(code, "$L.asText()$L", nodeVar(), conversion);
	}

	@Override
	protected void iterateOverFields() {
		String entryVar = "$" + stackDepth() + "$entry";
		code.beginControlFlow("for ($T<String, $T> $L : ($T<$T<String, $T>>) () -> $L.fields())",
			Map.Entry.class, jsonNode(), entryVar, Iterable.class, Map.Entry.class, jsonNode(), nodeVar());
		nodeVarOverride = entryVar + ".getValue()";
		fieldVar = entryVar + ".getKey()";
	}

	@Override
	protected void afterObject() {
		nodeVarOverride = null;
	}

	@Override
	protected void readFieldNameInIteration(String propertyName) {
		code.addStatement("String $L = $L", propertyName, fieldVar);
	}

	@Override
	protected void readDiscriminator(String propertyName) {
		lhs.assign(code, "$T.readDiscriminator($S, $L)", JacksonJsonNodeReaderHelper.class, propertyName, parserVariable.getSimpleName());
	}

	@Override
	protected void iterateOverElements() {
		String itemVar = "$" + stackDepth() + "$item";
		code.beginControlFlow("for ($T $L : $L)", jsonNode(), itemVar, nodeVar());
		nodeVarOverride = itemVar;
	}

	@Override
	protected void afterArray() {
		nodeVarOverride = null;
	}

	@Override
	protected void throwUnexpected(String expected) {
		code.addStatement("throw new $T($S + $L.getClass().getSimpleName() + $S)",
			IOException.class,
			"Expected " + expected + ", got ",
			nodeVar(),
			" at (TODO)");
	}

	@Override
	protected void invokeDelegate(String instance, String methodName, List<String> ownArguments) {
		lhs.assign(code, "$L.$L($L" + ownArguments.stream().skip(1).map(a -> ", " + a).collect(Collectors.joining()) + ")", instance, methodName, nodeVar());
	}

	@Override
	protected JacksonJsonNodeReaderGenerator nest(TypeMirror type, @Nullable String propertyName, LHS lhs) {
		return new JacksonJsonNodeReaderGenerator(prototype, utils, utils.tf.getType(type), propertyName, code, parserVariable, lhs, this);
	}

	private TypeElement jsonNode() {
		return utils.elements.getTypeElement("com.fasterxml.jackson.databind.JsonNode");
	}

	private String nodeVar() {
		return nodeVarOverride != null ? nodeVarOverride : parent != null ? parent.nodeVar() : parserVariable.getSimpleName().toString();
	}
}
