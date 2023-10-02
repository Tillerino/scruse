package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.Polymorphism;
import org.tillerino.scruse.processor.PrototypeFinder;
import org.tillerino.scruse.processor.ScruseMethod;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractWriterGenerator<SELF extends AbstractWriterGenerator<SELF>> extends AbstractCodeGeneratorStack<SELF> {
	protected final LHS lhs;

	protected final RHS rhs;

	protected AbstractWriterGenerator(ScruseMethod prototype, AnnotationProcessorUtils utils, CodeBlock.Builder code, SELF parent, LHS lhs, String propertyName, RHS rhs, Type type) {
		super(prototype, utils, type, code, parent, propertyName);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	protected AbstractWriterGenerator(AnnotationProcessorUtils utils, ScruseMethod prototype) {
		this(prototype, utils, CodeBlock.builder(), null, new LHS.Return(), null,
			new RHS.Variable(prototype.methodElement().getParameters().get(0).getSimpleName().toString(), true),
			utils.tf.getType(prototype.methodElement().getParameters().get(0).asType()));
	}

	public CodeBlock.Builder build() {
		Optional<PrototypeFinder.Prototype> delegate = utils.prototypeFinder.findPrototype(type, prototype);
		if (delegate.isPresent()) {
			invokeDelegate(utils.delegates.getOrCreateField(prototype.blueprint(), delegate.get().blueprint()), delegate.get().method().name(),
				prototype.methodElement().getParameters().stream().map(e -> e.getSimpleName().toString()).toList());
			return code;
		}
		if (type.isPrimitive()) {
			writePrimitive(type.getTypeMirror());
		} else {
			if (rhs.nullable()) {
				if (rhs instanceof RHS.Variable v) {
					code.beginControlFlow("if ($L != null)", v.name());
				} else {
					RHS.Variable nest = new RHS.Variable(property + "$" + (stackDepth() + 1), true);
					code.addStatement("$T $L = " + rhs.format(), flatten(type.getTypeMirror(), nest.name(), rhs.args()));
					nest(type.getTypeMirror(), lhs, null, nest).build();
					return code;
				}
			}

			writeNullCheckedObject();

			if (rhs.nullable()) {
				code.nextControlFlow("else");
				writeNull();
				code.endControlFlow();
			}
		}
		return code;
	}

	/**
	 * If your implementation has specializations for some types, you can override this method.
	 * At this point, {@link #rhs} is guaranteed to be non-null and has type {@link #type}.
	 */
	protected void writeNullCheckedObject() {
		if (utils.isBoxed(type.getTypeMirror())) {
			nest(utils.types.unboxedType(type.getTypeMirror()), lhs, null, rhs).build();
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

	protected void writeIterable() {
		Type componentType = type.isArrayType()
			? type.getComponentType()
			: type.determineTypeArguments(Iterable.class).iterator().next().getTypeBound();

		RHS.Variable elemVar = new RHS.Variable("item$" + (stackDepth() + 1), true);
		SELF nested = nest(componentType.getTypeMirror(), new LHS.Array(), "item", elemVar);
		startArray();
		code.beginControlFlow("for ($T $L : " + rhs.format() + ")", flatten(nested.type.getTypeMirror(), elemVar.name(), rhs.args()));
		nested.build();
		code.endControlFlow();
		endArray();
	}

	private void writeMap() {
		Type keyType = type.determineTypeArguments(Map.class).get(0);
		Type valueType = type.determineTypeArguments(Map.class).get(1);

		RHS.Variable entry = new RHS.Variable("entry$" + (stackDepth() + 1), false);

		RHS.Accessor value = new RHS.Accessor(entry, "getValue()", true);
		LHS.Field key = new LHS.Field("$L.getKey()", new Object[] { entry.name() });
		SELF valueNested = nest(valueType.getTypeMirror(), key, "value", value);

		startObject();
		code.beginControlFlow("for ($T<$T, $T> $L : " + rhs.format() + ".entrySet())",
			flatten(Map.Entry.class, keyType.getTypeMirror(), valueType.getTypeMirror(), entry.name(), rhs.args()));
		valueNested.build();
		code.endControlFlow();
		endObject();
	}

	protected void writeObjectAsMap() {
		startObject();
		code.add("\n");

		Optional<Polymorphism> polymorphismMaybe = Polymorphism.of(type.getTypeElement(), utils.elements);
		if (polymorphismMaybe.isPresent()) {
			Polymorphism polymorphism = polymorphismMaybe.get();
			for (Polymorphism.Child child : polymorphism.children()) {
				code.beginControlFlow("if (" + rhs.format() + " instanceof $T)", flatten(rhs.args(), child.type()));
				nest(utils.commonTypes.string, new LHS.Field("$S", new Object[] { polymorphism.discriminator() }), polymorphism.discriminator(), new RHS.StringLiteral(child.name())).build();
				RHS.Variable casted = new RHS.Variable(propertyName() + "$" + stackDepth() + "$cast", false);
				code.addStatement("$T $L = ($T) " + rhs.format(), flatten(child.type(), casted.name, child.type(), rhs.args()));
				nest(child.type(), lhs, null, casted).writeObjectPropertiesAsFields();
				code.endControlFlow();
			}
		} else {
			writeObjectPropertiesAsFields();
		}

		endObject();
	}

	void writeObjectPropertiesAsFields() {
		DeclaredType t = (DeclaredType) type.getTypeMirror();
		if (!t.getTypeArguments().isEmpty()) {
			throw new IllegalArgumentException(stack().toString() + " Type parameters not yet supported");
		}

		type.getPropertyReadAccessors().forEach((propertyName, accessor) -> {
			LHS lhs = new LHS.Field("$S", new Object[] { propertyName });
			RHS.Accessor nest = new RHS.Accessor(rhs, accessor.getReadValueSource(), true);
			SELF nested = nest(accessor.getAccessedType(), lhs, propertyName, nest);
			nested.build();
			code.add("\n");
		});
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

	protected abstract void startObject();

	protected abstract void endObject();

	protected abstract void invokeDelegate(String instance, String methodName, List<String> ownArguments);

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
			else if (this instanceof StringLiteral s) {
				return "$S";
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
			else if (this instanceof StringLiteral s) {
				return new Object[] { s.value() };
			}
			else {
				throw new IllegalArgumentException();
			}
		}

		boolean nullable();

		record Variable(String name, boolean nullable) implements RHS { }
		record Accessor(RHS object, String accessorLiteral, boolean nullable) implements RHS { }
		record StringLiteral(String value) implements RHS {
			@Override
			public boolean nullable() {
				return false;
			}
		}
	}
}
