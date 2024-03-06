package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.Polymorphism;
import org.tillerino.scruse.processor.PrototypeFinder;
import org.tillerino.scruse.processor.ScruseMethod;
import org.tillerino.scruse.processor.apis.AbstractReaderGenerator.Branch;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
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
			String delegateField = utils.delegates.getOrCreateField(prototype.blueprint(), delegate.get().blueprint());
			invokeDelegate(delegateField, delegate.get().method().name(),
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
		Optional<Polymorphism> polymorphismMaybe = Polymorphism.of(type.getTypeElement(), utils.elements);
		if (polymorphismMaybe.isPresent()) {
			Polymorphism polymorphism = polymorphismMaybe.get();
			Branch branch = Branch.IF;
			for (Polymorphism.Child child : polymorphism.children()) {
				branch.controlFlow(code, rhs.format() + " instanceof $T", flatten(rhs.args(), child.type()));
				branch = Branch.ELSE_IF;
				RHS.Variable casted = new RHS.Variable(propertyName() + "$" + stackDepth() + "$cast", false);
				code.addStatement("$T $L = ($T) " + rhs.format(), flatten(child.type(), casted.name, child.type(), rhs.args()));
				Optional<PrototypeFinder.Prototype> delegate = utils.prototypeFinder.findPrototype(utils.tf.getType(child.type()), prototype);
				if (delegate.isPresent()) {
					String delegateField = utils.delegates.getOrCreateField(prototype.blueprint(), delegate.get().blueprint());
					if (!delegate.get().method().lastParameterIsContext()) {
						throw new IllegalArgumentException("Delegate method must have a context parameter");
					}
					if (!prototype.lastParameterIsContext()) {
						throw new IllegalArgumentException("Prototype method must have a context parameter");
					}
					code.addStatement("$L.setPendingDiscriminator($S, $S)", prototype.contextParameter().orElseThrow(), polymorphism.discriminator(), child.name());
					// TODO crude call
					nest(child.type(), lhs, null, casted)
						.invokeDelegate(delegateField, delegate.get().method().name(),
						prototype.methodElement().getParameters().stream().map(e -> e.getSimpleName().toString())
						.toList());
				} else {
					startObject();
					code.add("\n");
					nest(utils.commonTypes.string, new LHS.Field("$S", new Object[] { polymorphism.discriminator() }), polymorphism.discriminator(), new RHS.StringLiteral(child.name())).build();
					nest(child.type(), lhs, null, casted).writeObjectPropertiesAsFields();
					endObject();
				}
			}
			if (branch == Branch.IF) {
				throw new IllegalArgumentException("Polymorphism must have at least one child type");
			}
			code.nextControlFlow("else");
			code.addStatement("throw new $T($S)", IllegalArgumentException.class, "Unknown type");
			code.endControlFlow();
		} else {
			startObject();
			code.add("\n");
			writeObjectPropertiesAsFields();
			endObject();
		}
	}

	void writeObjectPropertiesAsFields() {
		if (canBePolyChild) {
			VariableElement context = prototype.contextParameter().orElseThrow();
			code.beginControlFlow("if ($L.isDiscriminatorPending())", context);
			nest(utils.commonTypes.string, new LHS.Field("$L.pendingDiscriminatorProperty", new Object[] { context.getSimpleName() }), "discriminator", new RHS.Accessor(new RHS.Variable(context.getSimpleName().toString(), false), "pendingDiscriminatorValue", false)).build();
			code.addStatement("$L.pendingDiscriminatorProperty = null", context);
			code.endControlFlow();
		}
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
