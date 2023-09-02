package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Map;

public abstract class AbstractWriterGenerator<SELF extends AbstractWriterGenerator<SELF>> extends AbstractCodeGeneratorStack<SELF> {
	protected final LHS lhs;

	protected final RHS rhs;

	protected AbstractWriterGenerator(AnnotationProcessorUtils utils, Type type, CodeBlock.Builder code, SELF parent, LHS lhs, String propertyName, RHS rhs) {
		super(utils, type, code, parent, propertyName);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	protected AbstractWriterGenerator(AnnotationProcessorUtils utils, ExecutableElement method) {
		this(utils, utils.tf.getType(method.getParameters().get(0).asType()), CodeBlock.builder(), null, new LHS.Return(), null, new RHS.Variable(method.getParameters().get(0).getSimpleName().toString()));
	}

	public CodeBlock.Builder build() {
		if (type.isPrimitive()) {
			writePrimitive(type.getTypeMirror());
		} else {
			if (rhs instanceof RHS.Variable v) {
				code.beginControlFlow("if ($L != null)", v.name());
			} else {
				RHS.Variable nest = new RHS.Variable(property + "$" + (stackDepth() + 1));
				code.addStatement("$T $L = " + rhs.format(), flatten(type.getTypeMirror(), nest.name(), rhs.args()));
				nest(type.getTypeMirror(), lhs, null, nest).build();
				return code;
			}

			writeNullCheckedObject();

			code.nextControlFlow("else");
			writeNull();
			code.endControlFlow();
		}
		return code;
	}

	/**
	 * If your implementation has specializations for some types, you can override this method.
	 * At this point, {@link #rhs} is guaranteed to be non-null and has type {@link #type}.
	 */
	protected void writeNullCheckedObject() {
		if (utils.isBoxed(type.getTypeMirror())) {
			writePrimitive(utils.types.unboxedType(type.getTypeMirror()));
		} else if (type.isString() || AnnotationProcessorUtils.isArrayOf(type, TypeKind.CHAR)) {
			writeString(type.isString() ? StringKind.STRING : StringKind.CHAR_ARRAY);
		} else if (AnnotationProcessorUtils.isArrayOf(type, TypeKind.BYTE)) {
			writeBinary(BinaryKind.BYTE_ARRAY);
		} else if (type.isIterableType()) {
			writeIterable();
		} else if (type.isMapType()) {
			writeMap();
		} else {
			writeObjectAsMap();
		}
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

	protected void writeIterable() {
		Type componentType = type.isArrayType()
			? type.getComponentType()
			: type.determineTypeArguments(Iterable.class).iterator().next().getTypeBound();

		RHS.Variable elemVar = new RHS.Variable("elem$" + (stackDepth() + 1));
		SELF nested = nest(componentType.getTypeMirror(), new LHS.Array(), "elem", elemVar);
		startArray();
		code.beginControlFlow("for ($T $L : " + rhs.format() + ")", flatten(nested.type.getTypeMirror(), elemVar.name(), rhs.args()));
		nested.build();
		code.endControlFlow();
		endArray();
	}

	private void writeMap() {
		Type keyType = type.determineTypeArguments(Map.class).get(0);
		Type valueType = type.determineTypeArguments(Map.class).get(1);

		RHS.Variable entry = new RHS.Variable("entry$" + (stackDepth() + 1));

		RHS.Accessor value = new RHS.Accessor(entry, "getValue()");
		LHS.Field key = new LHS.Field("$L.getKey()", new Object[] { entry.name() });
		SELF valueNested = nest(valueType.getTypeMirror(), key, "entry", value);

		startObject();
		code.beginControlFlow("for ($T<$T, $T> $L : " + rhs.format() + ".entrySet())",
			flatten(Map.Entry.class, keyType.getTypeMirror(), valueType.getTypeMirror(), entry.name(), rhs.args()));
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
			LHS lhs = new LHS.Field("$S", new Object[] { propertyName });
			RHS.Accessor nest = new RHS.Accessor(rhs, accessor.getReadValueSource());
			SELF nested = nest(accessor.getAccessedType(), lhs, propertyName, nest);
			nested.build();
			code.add("\n");
		});

		endObject();
	}

	protected abstract void startObject();

	protected abstract void endObject();

	protected abstract SELF nest(TypeMirror type, LHS lhs, String propertyName, RHS rhs);

	sealed interface LHS {
		record Return() implements LHS { }
		record Array() implements LHS { }
		record Field(String format, Object[] args) implements LHS { }
	}

	sealed interface RHS {
		default String format() {
			if (this instanceof Variable) {
				return "$L";
			}
			else if (this instanceof Accessor a) {
				return a.object().format() + ".$L";
			}
			else {
				throw new IllegalArgumentException();
			}
		}

		default Object[] args() {
			if (this instanceof Variable v) {
				return new Object[] { v.name() };
			}
			else if (this instanceof Accessor a) {
				return new Object[] { a.object().args(), a.accessorLiteral() };
			}
			else {
				throw new IllegalArgumentException();
			}
		}

		record Variable(String name) implements RHS { }
		record Accessor(RHS object, String accessorLiteral) implements RHS { }
	}
}
