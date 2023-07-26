package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.util.accessor.ReadAccessor;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Map;

public abstract class AbstractWriterCodeGenerator<SELF extends AbstractWriterCodeGenerator<SELF>> {
	protected final AnnotationProcessorUtils utils;
	protected final Type type;
	protected final Key key;
	protected final CodeBlock.Builder code;
	protected final Mode mode;
	protected final SELF parent;

	enum Mode {
		ROOT,
		IN_OBJECT,
		IN_ARRAY
	}

	public AbstractWriterCodeGenerator(AnnotationProcessorUtils utils, Type type, Key key, CodeBlock.Builder code, Mode mode, SELF parent) {
		this.utils = utils;
		this.type = type;
		this.key = key;
		this.code = code;
		this.mode = mode;
		this.parent = parent;
	}

	String varName() {
		if (mode == Mode.ROOT) {
			// in this case, the name is determined by the method parameter
			return key.propertyName();
		}
		return key.propertyName() + "$" + stackDepth();
	}

	int stackDepth() {
		return parent != null ? 1 + parent.stackDepth() : 1;
	}

	public CodeBlock.Builder build() {
		if (type.isPrimitive()) {
			writePrimitive(type.getTypeMirror());
		} else {
			code.beginControlFlow("if ($L != null)", varName());

			writeNullCheckedObject();

			code.nextControlFlow("else");
			writeNull();
			code.endControlFlow();
		}
		return code;
	}

	/**
	 * If your implementation has specializations for some types, you can override this method.
	 * At this point, the variable in {@link #varName()} is guaranteed to be non-null and has type {@link #type}.
	 */
	protected void writeNullCheckedObject() {
		if (utils.isBoxed(type.getTypeMirror())) {
			writePrimitive(utils.types.unboxedType(type.getTypeMirror()));
		} else if (type.isString() || isArrayOf(type, TypeKind.CHAR)) {
			writeString(type.isString() ? StringKind.STRING : StringKind.CHAR_ARRAY);
		} else if (isArrayOf(type, TypeKind.BYTE)) {
			writeBinary(BinaryKind.BYTE_ARRAY);
		} else if (type.isIterableType()) {
			writeIterable();
		} else if (type.isMapType()) {
			writeMap();
		} else {
			writeObjectAsMap();
		}
	}

	private static boolean isArrayOf(Type type, TypeKind kind) {
		return type.isArrayType() && type.getComponentType().getTypeMirror().getKind() == kind;
	}

	protected abstract void writeNull();

	protected abstract void writeString(StringKind stringKind);

	protected abstract void writeBinary(BinaryKind binaryKind);

	/**
	 * @param typeMirror if non-null, the type is a primitive wrapper and this is the primitive type
	 */
	public abstract void writePrimitive(TypeMirror typeMirror);

	protected abstract void startArray();

	protected abstract void endArray();

	/**
	 * Mode is always IN_OBJECT.
	 *
	 * @return false if it is not possible to write the field inline and instead a variable needs to be declared
	 */
	protected abstract boolean writePrimitiveField(String propertyName, ReadAccessor accessor);

	protected void writeIterable() {
		Type componentType = type.isArrayType()
			? type.getComponentType()
			: type.determineTypeArguments(Iterable.class).iterator().next().getTypeBound();

		SELF nested = nestIntoArray(componentType.getTypeMirror(), StringUtils.uncapitalize(componentType.getName()));
		startArray();
		code.beginControlFlow("for ($T $L : $L)", nested.type.getTypeMirror(), nested.varName(), varName());
		nested.build();
		code.endControlFlow();
		endArray();
	}

	private void writeMap() {
		Type keyType = type.determineTypeArguments(Map.class).get(0);
		Type valueType = type.determineTypeArguments(Map.class).get(1);

		String entryVar = "entry$" + (stackDepth() + 1);
		String keyVar = "key$" + (stackDepth() + 1);
		String valueVar = "value$" + (stackDepth() + 1);

		SELF valueNested = nestIntoMap(valueType.getTypeMirror(), keyVar, valueVar);

		startObject();
		code.beginControlFlow("for ($T<$T, $T> $L : $L.entrySet())", Map.Entry.class, keyType.getTypeMirror(), valueType.getTypeMirror(), entryVar, varName());
		code.addStatement("$T $L = $L.getKey()", keyType.getTypeMirror(), keyVar, entryVar);
		code.addStatement("$T $L = $L.getValue()", valueType.getTypeMirror(), valueNested.varName(), entryVar);
		valueNested.build();
		code.endControlFlow();
		endObject();
	}

	protected void writeObjectAsMap() {
		DeclaredType t = (DeclaredType) type.getTypeMirror();
		if (!t.getTypeArguments().isEmpty()) {
			throw new IllegalArgumentException(stack().toString() + " Type parameters not yet supported");
		}

		startObject();
		code.add("\n");

		type.getPropertyReadAccessors().forEach((propertyName, accessor) -> {
			if (accessor.getAccessedType().getKind().isPrimitive() && writePrimitiveField(propertyName, accessor)) {
				// field was written
			} else {
				SELF nested = nest(accessor.getAccessedType(), propertyName);
				code.addStatement("$T $L = $L.$L", accessor.getAccessedType(), nested.varName(), varName(), accessor.getReadValueSource());
				nested.build();
				code.add("\n");
			}
		});

		endObject();
	}

	SELF nest(TypeMirror type, String propertyName) {
		return nest(type, new Key(propertyName, propertyName + "$" + stackDepth() + 1, "$S", propertyName), Mode.IN_OBJECT);
	}

	SELF nestIntoArray(TypeMirror type, String elemName) {
		return nest(type, new Key("element", elemName + "$" + stackDepth() + 1, null, null), Mode.IN_ARRAY);
	}

	SELF nestIntoMap(TypeMirror type, String keyVariable, String valueVariable) {
		return nest(type, new Key("value", valueVariable, "$L", keyVariable), Mode.IN_OBJECT);
	}

	protected abstract void startObject();

	protected abstract void endObject();

	protected abstract SELF nest(TypeMirror type, Key nestedKey, Mode mode);

	protected StringBuilder stack() {
		if (parent != null) {
			return parent.stack().append(" -> ").append(key.propertyName());
		}
		return new StringBuilder(key.propertyName());
	}

	enum StringKind {
		STRING,
		CHAR_ARRAY
	}

	enum BinaryKind {
		BYTE_ARRAY
	}

	/**
	 * If {@link Mode#IN_OBJECT}, describes the key of the current value.
	 *
	 * @param propertyName For error message traces. Either the property name or can be something like "element", or "value".
	 *                     This is also used as prefix for the variable name.
	 * @param varName      The variable name to use for the current value.
	 * @param keyDollar    $S or $L: how to retrieve the key value.
	 * @param keyValue     What to use put in keyDollar.
	 */
	protected record Key(String propertyName, String varName, String keyDollar, String keyValue) {
		static Key root(String variableName) {
			return new Key(variableName, variableName, null, null);
		}
	}
}
