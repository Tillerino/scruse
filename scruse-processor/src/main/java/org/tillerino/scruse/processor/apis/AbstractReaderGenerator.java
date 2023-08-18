package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class AbstractReaderGenerator<SELF extends AbstractReaderGenerator<SELF>> extends AbstractCodeGeneratorStack<SELF> {
	protected AbstractReaderGenerator(AnnotationProcessorUtils utils, Type type, Key key, CodeBlock.Builder code, Mode mode, SELF parent) {
		super(utils, type, key, code, mode, parent);
	}

	public CodeBlock.Builder build() {
		if (type.isPrimitive()) {
			readPrimitive(true, type.getTypeMirror());
		} else {
			startNullCase(true);
			if (mode == Mode.ROOT) {
				code.addStatement("return null");
			} else {
				code.addStatement("$L = null", key.varName());
			}
			readNullCheckedObject();
		}
		return code;
	}

	protected void readPrimitive(boolean firstCase, TypeMirror type) {
		String typeName;
		switch (type.getKind()) {
			case BOOLEAN -> {
				startBooleanCase(firstCase);
				typeName = "boolean";
			}
			case BYTE, SHORT, INT, LONG -> {
				startNumberCase(firstCase);
				typeName = "number";
			}
			case FLOAT, DOUBLE -> {
				startStringCase(firstCase);
				readNumberFromString(type);
				startNumberCase(false);
				typeName = "number";
			}
			case CHAR -> {
				startStringCase(firstCase);
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
		SELF nested = nest(utils.commonTypes.string, new Key(null, key.varName() + "$string", null, null), Mode.IN_OBJECT);
		code.addStatement("$T $L", nested.type.getTypeMirror(), nested.varName());
		nested.readString();
		code.beginControlFlow("if ($L.length() == 1)", nested.varName());
		if (mode == Mode.ROOT) {
			code.addStatement("return $L.charAt(0)", nested.varName());
		} else {
			code.addStatement("$L = $L.charAt(0)", varName(), nested.varName());
		}
		code.nextControlFlow("else");
		code.addStatement("throw new $T()", IOException.class);
		code.endControlFlow();
	}

	private void readNumberFromString(TypeMirror type) {
		SELF nested = nest(utils.commonTypes.string, new Key(null, key.varName() + "$string", null, null), Mode.IN_OBJECT);
		code.addStatement("$T $L", nested.type.getTypeMirror(), nested.varName());
		nested.readString();
		BiConsumer<String, String> print = (t, v) -> {
			if (mode == Mode.ROOT) {
				code.addStatement("return $L.$L", StringUtils.capitalize(t), v);
			} else {
				code.addStatement("$L = $L.$L", varName(), t, v);
			}
		};
		code.beginControlFlow("if ($L.equals($S))", nested.varName(), "NaN");
		print.accept(type.toString(), "NaN");
		code.nextControlFlow("if ($L.equals($S))", nested.varName(), "Infinity");
		print.accept(type.toString(), "POSITIVE_INFINITY");
		code.nextControlFlow("if ($L.equals($S))", nested.varName(), "-Infinity");
		print.accept(type.toString(), "NEGATIVE_INFINITY");
		code.nextControlFlow("else");
		code.addStatement("throw new $T()", IOException.class);
		code.endControlFlow();
	}

	private void readNullCheckedObject() {
		if (utils.isBoxed(type.getTypeMirror())) {
			readPrimitive(false, utils.types.unboxedType(type.getTypeMirror()));
		} else if (type.isArrayType()) {
			readArray();
		} else if (type.isIterableType()) {
			readCollection();
		} else if (type.isString()) {
			readString(false);
		} else {
			readObject();
		}
	}

	private void readArray() {
		startArrayCase(false);
		{
			Type componentType = type.getComponentType();
			code.addStatement("$L = new $T[1024]", key.varName(), componentType.asRawType().getTypeMirror());
			String len = key.varName() + "$len";
			code.addStatement("int $L = 0", len);
			iterateOverElements();
			{
				code.beginControlFlow("if ($L == $L.length)", len, key.varName());
				code.addStatement("$L = $T.copyOf($L, $L.length * 2)", key.varName(), java.util.Arrays.class, key.varName(), key.varName());
				code.endControlFlow();

				SELF nested = nestIntoArray(componentType.getTypeMirror(), StringUtils.uncapitalize(componentType.getName()));
				code.addStatement("$T $L", nested.type.getTypeMirror(), nested.varName());
				nested.build();
				code.addStatement("$L[$L++] = $L", varName(), len, nested.varName());
			}
			code.endControlFlow();
			code.addStatement("$L = $T.copyOf($L, $L)", key.varName(), java.util.Arrays.class, key.varName(), len);
		}
		code.nextControlFlow("else");
		{
			throwUnexpected("array");
		}
		code.endControlFlow();
	}

	private void readCollection() {
		startArrayCase(false);
		{
			Type componentType = type.determineTypeArguments(Iterable.class).iterator().next().getTypeBound();
			TypeMirror collectionType = determineCollectionType();

			code.addStatement("$L = new $T<>()", varName(), collectionType);
			iterateOverElements();
			{
				SELF nested = nestIntoArray(componentType.getTypeMirror(), StringUtils.uncapitalize(componentType.getName()));
				code.addStatement("$T $L", nested.type.getTypeMirror(), nested.varName());
				nested.build();
				code.addStatement("$L.add($L)", varName(), nested.varName());
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

	private void readObject() {
		startObjectCase(false);
		if (type.isRecord()) {
			for (Element component : type.getRecordComponents()) {
				SELF nest = nestIntoObject(component.asType(), component.getSimpleName().toString());
				code.addStatement("$T $L = $L", component.asType(), nest.varName(), nest.type.getNull());
			}
			iterateOverFields();
			boolean first = true;
			for (Element component : type.getRecordComponents()) {
				SELF nest = nestIntoObject(component.asType(), component.getSimpleName().toString());
				nest.startFieldCase(first, component.getSimpleName().toString());
				nest.build();
				first = false;
			}
			// unknown fields are ignored for now
			if (!first) {
				code.endControlFlow();
			}
			code.endControlFlow();
			code.addStatement("return new $T($L)", type.getTypeMirror(), type.getRecordComponents().stream().map(c -> nestIntoObject(c.asType(), c.getSimpleName().toString()).varName()).collect(Collectors.joining(", ")));
		}
		code.nextControlFlow("else");
		throwUnexpected("object");
		code.endControlFlow();
	}

	private void readString(boolean firstCase) {
		startStringCase(firstCase);
		readString();
		code.nextControlFlow("else");
		throwUnexpected("string");
		code.endControlFlow();
	}

	protected abstract void startFieldCase(boolean b, String string);

	protected abstract void startStringCase(boolean firstCase);

	protected abstract void startNumberCase(boolean firstCase);

	protected abstract void startObjectCase(boolean firstCase);

	protected abstract void startArrayCase(boolean firstCase);

	protected abstract void startBooleanCase(boolean firstCase);

	protected abstract void startNullCase(boolean firstCase);

	protected abstract void readPrimitive(TypeMirror type);

	protected abstract void readString();

	protected abstract void iterateOverFields();

	protected abstract void iterateOverElements();

	protected abstract void throwUnexpected(String expected);
}
