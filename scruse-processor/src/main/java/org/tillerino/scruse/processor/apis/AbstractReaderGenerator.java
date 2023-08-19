package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class AbstractReaderGenerator<SELF extends AbstractReaderGenerator<SELF>> extends AbstractCodeGeneratorStack<SELF> {
	protected final LHS lhs;
	protected AbstractReaderGenerator(AnnotationProcessorUtils utils, Type type, String propertyName, CodeBlock.Builder code, SELF parent, LHS lhs) {
		super(utils, type, code, parent, propertyName);
		this.lhs = lhs;
	}

	public CodeBlock.Builder build() {
		return build(Case.IF, Token.NEXT_TOKEN);
	}

	public CodeBlock.Builder build(Case casey, Token token) {
		if (type.isPrimitive()) {
			readPrimitive(casey, token, type.getTypeMirror());
		} else {
			startNullCase(casey, token);
			if (lhs instanceof LHS.Return) {
				code.addStatement("return null");
			} else if (lhs instanceof LHS.Variable v) {
				code.addStatement("$L = null", v.name());
			} else if (lhs instanceof LHS.Array a) {
				code.addStatement("$L[$L++] = null", a.arrayName(), a.indexName());
			} else {
				throw new AssertionError(lhs);
			}
			readNullCheckedObject();
		}
		return code;
	}

	protected void readPrimitive(Case casey, Token token, TypeMirror type) {
		String typeName;
		switch (type.getKind()) {
			case BOOLEAN -> {
				startBooleanCase(casey, token);
				typeName = "boolean";
			}
			case BYTE, SHORT, INT, LONG -> {
				startNumberCase(casey, token);
				typeName = "number";
			}
			case FLOAT, DOUBLE -> {
				startStringCase(casey, token);
				readNumberFromString(type);
				startNumberCase(Case.ELSE_IF, Token.CURRENT_TOKEN);
				typeName = "number";
			}
			case CHAR -> {
				startStringCase(casey, token);
				typeName = "string";
			}
			default -> throw new AssertionError(type.getKind());
		}
		if (type.getKind() == TypeKind.CHAR) {
			readCharFromString();
		} else {
			readPrimitive(type);
		}
		code.nextControlFlow("else");
		throwUnexpected(typeName);
		code.endControlFlow();
	}

	private void readCharFromString() {
		String stringVar = readStringInstead();
		code.beginControlFlow("if ($L.length() == 1)", stringVar);
		if (lhs instanceof LHS.Return) {
			code.addStatement("return $L.charAt(0)", stringVar);
		} else if (lhs instanceof LHS.Variable v) {
			code.addStatement("$L = $L.charAt(0)", v.name(), stringVar);
		}
		code.nextControlFlow("else");
		code.addStatement("throw new $T()", IOException.class);
		code.endControlFlow();
	}

	private void readNumberFromString(TypeMirror type) {
		String stringVar = readStringInstead();
		BiConsumer<String, String> print = (t, v) -> {
			if (lhs instanceof LHS.Return) {
				code.addStatement("return $L.$L", StringUtils.capitalize(t), v);
			} else if (lhs instanceof LHS.Variable vv) {
				code.addStatement("$L = $L.$L", vv.name(), StringUtils.capitalize(t), v);
			} else if (lhs instanceof LHS.Array a) {
				code.addStatement("$L[$L++] = $L.$L", a.arrayName(), a.indexName(), StringUtils.capitalize(t), v);
			} else {
				throw new AssertionError(lhs);
			}
		};
		code.beginControlFlow("if ($L.equals($S))", stringVar, "NaN");
		print.accept(type.toString(), "NaN");
		code.nextControlFlow("else if ($L.equals($S))", stringVar, "Infinity");
		print.accept(type.toString(), "POSITIVE_INFINITY");
		code.nextControlFlow("else if ($L.equals($S))", stringVar, "-Infinity");
		print.accept(type.toString(), "NEGATIVE_INFINITY");
		code.nextControlFlow("else");
		code.addStatement("throw new $T()", IOException.class);
		code.endControlFlow();
	}

	private String readStringInstead() {
		String stringVar = "string$" + (stackDepth() + 1);
		SELF nested = nest(utils.commonTypes.string, null, new LHS.Variable(stringVar));
		code.addStatement("$T $L", nested.type.getTypeMirror(), stringVar);
		nested.readString(StringKind.STRING);
		return stringVar;
	}

	private void readNullCheckedObject() {
		if (utils.isBoxed(type.getTypeMirror())) {
			readPrimitive(Case.ELSE_IF, Token.CURRENT_TOKEN, utils.types.unboxedType(type.getTypeMirror()));
		} else if (type.isString() || AnnotationProcessorUtils.isArrayOf(type, TypeKind.CHAR)) {
			readString(Case.ELSE_IF, Token.CURRENT_TOKEN, type.isString() ? StringKind.STRING : StringKind.CHAR_ARRAY);
		} else if (type.isArrayType()) {
			readArray(Case.ELSE_IF, Token.CURRENT_TOKEN);
		} else if (type.isIterableType()) {
			readCollection(Case.ELSE_IF, Token.CURRENT_TOKEN);
		} else {
			readObject(Case.ELSE_IF, Token.CURRENT_TOKEN);
		}
	}

	private void readArray(Case casey, Token token) {
		startArrayCase(casey, token);
		{
			Type componentType = type.getComponentType();
			if (utils.types.isSameType(componentType.getTypeMirror(), utils.commonTypes.boxedCharacter)) {
				throw new AssertionError("Please provide a custom reader for " + type);
			}
			TypeMirror rawComponentType = componentType.asRawType().getTypeMirror();
			String varName;
			if (lhs instanceof LHS.Return) {
				varName = "array$" + stackDepth();
				code.addStatement("$T[] $L = new $T[1024]", rawComponentType, varName, rawComponentType);
			} else if (lhs instanceof LHS.Variable v) {
				varName = v.name();
				code.addStatement("$L = new $T[1024]", varName, rawComponentType);
			} else {
				throw new AssertionError(lhs);
			}
			String len = "len$" + stackDepth();
			code.addStatement("int $L = 0", len);
			iterateOverElements();
			{
				code.beginControlFlow("if ($L == $L.length)", len, varName);
				code.addStatement("$L = $T.copyOf($L, $L.length * 2)", varName, java.util.Arrays.class, varName, varName);
				code.endControlFlow();

				SELF nested = nest(componentType.getTypeMirror(), "elem", new LHS.Array(varName, len));
				nested.build(Case.IF, Token.CURRENT_TOKEN);
			}
			code.endControlFlow();
			if (lhs instanceof LHS.Return) {
				code.addStatement("return $T.copyOf($L, $L)", java.util.Arrays.class, varName, len);
			} else if (lhs instanceof LHS.Variable v) {
				code.addStatement("$L = $T.copyOf($L, $L)", v.name(), java.util.Arrays.class, varName, len);
			} else {
				throw new AssertionError(lhs);
			}
		}
		code.nextControlFlow("else");
		{
			throwUnexpected("array");
		}
		code.endControlFlow();
	}

	private void readCollection(Case casey, Token token) {
		startArrayCase(casey, token);
		{

			Type componentType = type.determineTypeArguments(Iterable.class).iterator().next().getTypeBound();
			TypeMirror collectionType = determineCollectionType();

			String varName;
			if (lhs instanceof LHS.Return) {
				varName = "collection" + stackDepth();
				code.addStatement("$T $L = new $T<>()", type.getTypeMirror(), varName, collectionType);
			} else if (lhs instanceof LHS.Variable v) {
				varName = v.name();
				code.addStatement("$L = new $T<>()", varName, collectionType);
			} else {
				throw new AssertionError(lhs);
			}
			iterateOverElements();
			{
				String elemName = "elem$" + (stackDepth() + 1);
				SELF nested = nest(componentType.getTypeMirror(), "elem", new LHS.Variable(elemName));
				code.addStatement("$T $L", nested.type.getTypeMirror(), varName);
				nested.build();
				code.addStatement("$L.add($L)", varName, elemName);
			}
			code.endControlFlow();
		}
		code.nextControlFlow("else");
		{
			throwUnexpected("array");
		}
		code.endControlFlow();
	}

	private TypeMirror determineCollectionType() {
		if (!type.asRawType().isAbstract()) {
			return type.asRawType().getTypeMirror();
		} else if (utils.tf.getType(Set.class).isAssignableTo(type.asRawType())) {
			return utils.tf.getType(LinkedHashSet.class).asRawType().getTypeMirror();
		} else if (utils.tf.getType(List.class).isAssignableTo(type.asRawType())) {
			return utils.tf.getType(ArrayList.class).asRawType().getTypeMirror();
		} else {
			throw new AssertionError(type);
		}
	}

	private void readObject(Case casey, Token token) {
		startObjectCase(casey, token);
		if (type.isRecord()) {
			for (Element component : type.getRecordComponents()) {
				String varName = component.getSimpleName().toString() + "$" + (stackDepth() + 1);
				SELF nest = nest(component.asType(), component.getSimpleName().toString(), new LHS.Variable(varName));
				code.addStatement("$T $L = $L", component.asType(), varName, nest.type.getNull());
			}
			iterateOverFields();
			boolean first = true;
			for (Element component : type.getRecordComponents()) {
				String varName = component.getSimpleName().toString() + "$" + (stackDepth() + 1);
				SELF nest = nest(component.asType(), component.getSimpleName().toString(), new LHS.Variable(varName));
				nest.startFieldCase(Case.IF, Token.CURRENT_TOKEN, component.getSimpleName().toString());
				nest.build();
				first = false;
			}
			// unknown fields are ignored for now
			if (!first) {
				code.endControlFlow();
			}
			code.endControlFlow();
			code.addStatement("return new $T($L)", type.getTypeMirror(), type.getRecordComponents().stream().map(c -> c.getSimpleName().toString() + "$" + (stackDepth() + 1)).collect(Collectors.joining(", ")));
		}
		code.nextControlFlow("else");
		throwUnexpected("object");
		code.endControlFlow();
	}

	private void readString(Case casey, Token token, StringKind stringKind) {
		startStringCase(casey, token);
		readString(stringKind);
		code.nextControlFlow("else");
		throwUnexpected("string");
		code.endControlFlow();
	}

	protected abstract void startFieldCase(Case casey, Token token, String string);

	protected abstract void startStringCase(Case casey, Token token);

	protected abstract void startNumberCase(Case casey, Token token);

	protected abstract void startObjectCase(Case casey, Token token);

	protected abstract void startArrayCase(Case casey, Token token);

	protected abstract void startBooleanCase(Case casey, Token token);

	protected abstract void startNullCase(Case casey, Token token);

	protected abstract void readPrimitive(TypeMirror type);

	protected abstract void readString(StringKind stringKind);

	protected abstract void iterateOverFields();

	protected abstract void iterateOverElements();

	protected abstract void throwUnexpected(String expected);

	protected abstract SELF nest(TypeMirror type, @Nullable String propertyName, LHS lhs);

	sealed interface LHS {
		record Return() implements LHS {}
		record Variable(String name) implements LHS {}
		record Array(String arrayName, String indexName) implements LHS {}
	}

	enum Case {
		IF,
		ELSE_IF,
		;
		CodeBlock.Builder controlFlow(CodeBlock.Builder code, String s, Object... args) {
			return switch (this) {
				case IF -> code.beginControlFlow("if (" + s + ")", args);
				case ELSE_IF -> code.nextControlFlow("else if (" + s + ")", args);
			};
		}
	}

	enum Token {
		NEXT_TOKEN,
		CURRENT_TOKEN,
	}
}
