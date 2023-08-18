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

public abstract class AbstractWriterGenerator<SELF extends AbstractWriterGenerator<SELF>> extends AbstractCodeGeneratorStack<SELF> {

	public AbstractWriterGenerator(AnnotationProcessorUtils utils, Type type, Key key, CodeBlock.Builder code, Mode mode, SELF parent) {
		super(utils, type, key, code, mode, parent);
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
				SELF nested = nestIntoObject(accessor.getAccessedType(), propertyName);
				code.addStatement("$T $L = $L.$L", accessor.getAccessedType(), nested.varName(), varName(), accessor.getReadValueSource());
				nested.build();
				code.add("\n");
			}
		});

		endObject();
	}

	protected abstract void startObject();

	protected abstract void endObject();

}
