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
		initializeParser();
		return build(Branch.IF);
	}

	public CodeBlock.Builder build(Branch branch) {
		if (type.isPrimitive()) {
			readPrimitive(branch, type.getTypeMirror());
		} else {
			startNullCase(branch);
			readNull();
			readNullCheckedObject();
		}
		return code;
	}

	protected void readPrimitive(Branch branch, TypeMirror type) {
		String typeName;
		switch (type.getKind()) {
			case BOOLEAN -> {
				startBooleanCase(branch);
				typeName = "boolean";
			}
			case BYTE, SHORT, INT, LONG -> {
				startNumberCase(branch);
				typeName = "number";
			}
			case FLOAT, DOUBLE -> {
				startStringCase(branch);
				readNumberFromString(type);
				startNumberCase(Branch.ELSE_IF);
				typeName = "number";
			}
			case CHAR -> {
				startStringCase(branch);
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
		} else if (lhs instanceof LHS.Collection c) {
			code.addStatement("$L.add($L.charAt(0))", c.variable(), stringVar);
		} else if (lhs instanceof LHS.Map m) {
			code.addStatement("$L.put($L, $L.charAt(0))", m.mapVar(), m.keyVar(), stringVar);
		} else {
			throw new AssertionError(lhs);
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
				code.addStatement("$L[$L++] = $L.$L", a.arrayVar(), a.indexVar(), StringUtils.capitalize(t), v);
			} else if (lhs instanceof LHS.Collection c) {
				code.addStatement("$L.add($L.$L)", c.variable(), StringUtils.capitalize(t), v);
			} else if (lhs instanceof LHS.Map m) {
				code.addStatement("$L.put($L, $L.$L)", m.mapVar(), m.keyVar(), StringUtils.capitalize(t), v);
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

	private void readNull() {
		if (lhs instanceof LHS.Return) {
			code.addStatement("return null");
		} else if (lhs instanceof LHS.Variable v) {
			code.addStatement("$L = null", v.name());
		} else if (lhs instanceof LHS.Array a) {
			code.addStatement("$L[$L++] = null", a.arrayVar(), a.indexVar());
		} else if (lhs instanceof LHS.Collection c) {
			code.addStatement("$L.add(null)", c.variable());
		} else if (lhs instanceof LHS.Map m) {
			code.addStatement("$L.put($L, null)", m.mapVar(), m.keyVar());
		} else {
			throw new AssertionError(lhs);
		}
	}

	private void readNullCheckedObject() {
		if (utils.isBoxed(type.getTypeMirror())) {
			readPrimitive(Branch.ELSE_IF, utils.types.unboxedType(type.getTypeMirror()));
		} else if (type.isString() || AnnotationProcessorUtils.isArrayOf(type, TypeKind.CHAR)) {
			readString(Branch.ELSE_IF, type.isString() ? StringKind.STRING : StringKind.CHAR_ARRAY);
		} else if (type.isArrayType()) {
			readArray(Branch.ELSE_IF);
		} else if (type.isIterableType()) {
			readCollection(Branch.ELSE_IF);
		} else if (type.isMapType()) {
			readMap(Branch.ELSE_IF);
		} else {
			readObject(Branch.ELSE_IF);
		}
	}

	private void readString(Branch branch, StringKind stringKind) {
		startStringCase(branch);
		readString(stringKind);
		code.nextControlFlow("else");
		throwUnexpected("string");
		code.endControlFlow();
	}

	private void readArray(Branch branch) {
		Type componentType = type.getComponentType();
		startArrayCase(branch);
		{
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
				nested.build(Branch.IF);
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
		if (componentType.getTypeMirror().getKind() == TypeKind.BYTE) {
			startStringCase(Branch.ELSE_IF);
			String stringVar = readStringInstead();
			if (lhs instanceof LHS.Return) {
				code.addStatement("return $T.getDecoder().decode($L)", Base64.class, stringVar);
			} else if (lhs instanceof LHS.Variable v) {
				code.addStatement("$L = $T.getDecoder().decode($L)", v.name(), Base64.class, stringVar);
			} else if (lhs instanceof LHS.Array a) {
				code.addStatement("$L[$L++] = $T.getDecoder().decode($L)", a.arrayVar(), a.indexVar(), Base64.class, stringVar);
			} else if (lhs instanceof LHS.Collection c) {
				code.addStatement("$L.add($T.getDecoder().decode($L))", c.variable(), Base64.class, stringVar);
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

	private void readCollection(Branch branch) {
		startArrayCase(branch);
		{

			Type componentType = type.determineTypeArguments(Iterable.class).get(0).getTypeBound();
			TypeMirror collectionType = determineCollectionType();
			String varName = instantiateContainer(collectionType);

			iterateOverElements();
			{
				SELF nested = nest(componentType.getTypeMirror(), "elem", new LHS.Collection(varName));
				nested.build(Branch.IF);
			}
			code.endControlFlow();
			if (lhs instanceof LHS.Return) {
				code.addStatement("return $L", varName);
			}
		}
		code.nextControlFlow("else");
		throwUnexpected("array");
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

	private String instantiateContainer(TypeMirror collectionType) {
		String varName;
		if (lhs instanceof LHS.Return) {
			varName = "container$" + stackDepth();
			code.addStatement("$T $L = new $T<>()", type.getTypeMirror(), varName, collectionType);
		} else if (lhs instanceof LHS.Variable v) {
			varName = v.name();
			code.addStatement("$L = new $T<>()", varName, collectionType);
		} else {
			throw new AssertionError(lhs);
		}
		return varName;
	}

	private void readMap(Branch branch) {
		startObjectCase(branch);
		{
			Type keyType = type.determineTypeArguments(Map.class).get(0).getTypeBound();
			if (!utils.types.isSameType(keyType.getTypeMirror(), utils.commonTypes.string)) {
				throw new AssertionError("Only String keys supported for now. " + stack());
			}
			Type valueType = type.determineTypeArguments(Map.class).get(1).getTypeBound();
			TypeMirror mapType = determineMapType();
			String varName = instantiateContainer(mapType);
			iterateOverFields();
			{
				startFieldCase(Branch.IF);
				String keyVar = "key$" + stackDepth();
				readFieldName(keyVar);
				nest(valueType.getTypeMirror(), "value", new LHS.Map(varName, keyVar)).build(Branch.IF);
				code.nextControlFlow("else");
				throwUnexpected("field name");
				code.endControlFlow();
			}
			code.endControlFlow();

			if (lhs instanceof LHS.Return) {
				code.addStatement("return $L", varName);
			}
		}
		code.nextControlFlow("else");
		throwUnexpected("object");
		code.endControlFlow();
	}

	private TypeMirror determineMapType() {
		if (!type.asRawType().isAbstract()) {
			return type.asRawType().getTypeMirror();
		} else {
			return utils.tf.getType(LinkedHashMap.class).asRawType().getTypeMirror();
		}
	}

	private void readObject(Branch branch) {
		startObjectCase(branch);
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
				nest.startFieldCase(Branch.IF, component.getSimpleName().toString());
				nest.build(Branch.IF);
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

	protected abstract void initializeParser();

	protected abstract void startFieldCase(Branch branch);

	protected abstract void startFieldCase(Branch branch, String string);

	protected abstract void startStringCase(Branch branch);

	protected abstract void startNumberCase(Branch branch);

	protected abstract void startObjectCase(Branch branch);

	protected abstract void startArrayCase(Branch branch);

	protected abstract void startBooleanCase(Branch branch);

	protected abstract void startNullCase(Branch branch);

	protected abstract void readPrimitive(TypeMirror type);

	protected abstract void readString(StringKind stringKind);

	protected abstract void readFieldName(String propertyName);

	protected abstract void iterateOverFields();

	protected abstract void iterateOverElements();

	protected abstract void throwUnexpected(String expected);

	protected abstract SELF nest(TypeMirror type, @Nullable String propertyName, LHS lhs);
	sealed interface LHS {

		record Return() implements LHS {}
		record Variable(String name) implements LHS {}
		record Array(String arrayVar, String indexVar) implements LHS {}
		record Collection(String variable) implements LHS {}
		record Map(String mapVar, String keyVar) implements LHS {}
	}

	enum Branch {
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

}
