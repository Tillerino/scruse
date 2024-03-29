package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.processor.*;
import org.tillerino.scruse.processor.apis.AbstractReaderGenerator.Branch;
import org.tillerino.scruse.processor.util.InstantiatedMethod;
import org.tillerino.scruse.processor.util.InstantiatedVariable;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractWriterGenerator<SELF extends AbstractWriterGenerator<SELF>> extends AbstractCodeGeneratorStack<SELF> {
	protected final LHS lhs;

	protected final RHS rhs;

	protected AbstractWriterGenerator(AnnotationProcessorUtils utils, GeneratedClass generatedClass, ScruseMethod prototype, CodeBlock.Builder code, SELF parent, Type type, String propertyName, RHS rhs, LHS lhs, boolean isStackRelevantType) {
		super(utils, generatedClass, prototype, code, parent, type, isStackRelevantType, propertyName);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	protected AbstractWriterGenerator(AnnotationProcessorUtils utils, ScruseMethod prototype, GeneratedClass generatedClass) {
		this(utils, generatedClass, prototype, CodeBlock.builder(), null, utils.tf.getType(prototype.instantiatedParameters().get(0).type()), null, new RHS.Variable(prototype.methodElement().getParameters().get(0).getSimpleName().toString(), true), new LHS.Return(),
			true);
	}

	public CodeBlock.Builder build() {
		Optional<PrototypeFinder.Prototype> delegate = utils.prototypeFinder.findPrototype(type, prototype, !(lhs instanceof LHS.Return), stackDepth() > 1);
		// delegate to any of the used blueprints
		if (delegate.isPresent()) {
			String delegateField = generatedClass.getOrCreateDelegateeField(prototype.blueprint(), delegate.get().blueprint());
			invokeDelegate(delegateField, delegate.get().method());
			return code;
		}

		// delegate to any of the interfaces passed to the prototype
		for (InstantiatedVariable parameter : prototype.instantiatedParameters()) {
			for (InstantiatedMethod method : utils.generics.instantiateMethods(parameter.type())) {
				if (method.element().getAnnotation(JsonOutput.class) != null
						&& !method.parameters().isEmpty()
						&& utils.types.isSameType(method.parameters().get(0).type(), type.getTypeMirror())) {
					invokeDelegate(parameter.name(), method);
					return code;
				}
			}
		}

		detectSelfReferencingType();
		if (type.isPrimitive()) {
			writePrimitive(type.getTypeMirror());
		} else {
			writeNullable();
		}
		return code;
	}

	/**
	 * Writes non-primitive types.
	 * This is a good method to override if you want to add specializations for some types that work with null values.
	 */
	protected void writeNullable() {
		if (rhs.nullable()) {
			if (rhs instanceof RHS.Variable v) {
				code.beginControlFlow("if ($L != null)", v.name());
			} else {
				RHS.Variable nest = new RHS.Variable(property + "$" + (stackDepth() + 1), true);
				code.addStatement("$T $L = " + rhs.format(), flatten(type.getTypeMirror(), nest.name(), rhs.args()));
				nest(type.getTypeMirror(), lhs, null, nest, false).build();
				return;
			}
		}

		writeNullCheckedObject();

		if (rhs.nullable()) {
			code.nextControlFlow("else");
			writeNull();
			code.endControlFlow();
		}
	}

	/**
	 * Writes non-primitive types that are known to be non-null.
	 * This is a good method to override if you want to add specializations for some types that require a dedicated null check.
	 */
	protected void writeNullCheckedObject() {
		if (utils.isBoxed(type.getTypeMirror())) {
			nest(utils.types.unboxedType(type.getTypeMirror()), lhs, null, rhs, false).build();
		} else if (type.isString() || AnnotationProcessorUtils.isArrayOf(type, TypeKind.CHAR)) {
			writeString(type.isString() ? StringKind.STRING : StringKind.CHAR_ARRAY);
		} else if (type.isEnumType()) {
			writeEnum();
		} else if (AnnotationProcessorUtils.isArrayOf(type, TypeKind.BYTE)) {
			writeBinary(BinaryKind.BYTE_ARRAY);
		} else if (type.isIterableType()) {
			writeIterable();
		} else if (type.isMapType()) {
			writeMap();
		} else if (type.isTypeVar()) {
			throw new IllegalArgumentException("Missing serializer for type variable " + type.getTypeMirror());
		} else {
			writeObjectAsMap();
		}
	}

	protected void writeIterable() {
		Type componentType = type.isArrayType()
			? type.getComponentType()
			: type.determineTypeArguments(Iterable.class).iterator().next().getTypeBound();

		RHS.Variable elemVar = new RHS.Variable("item$" + (stackDepth() + 1), true);
		SELF nested = nest(componentType.getTypeMirror(), new LHS.Array(), "item", elemVar, true);
		startArray();
		writeCommaMarkerIfNecessary();
		code.beginControlFlow("for ($T $L : " + rhs.format() + ")", flatten(nested.type.getTypeMirror(), elemVar.name(), rhs.args()));
		writeCommaIfNecessary();
		nested.build();
		code.endControlFlow();
		endArray();
	}

	private void writeCommaMarkerIfNecessary() {
		if (needsToWriteComma()) {
			code.addStatement("boolean $L = true", "$" + stackDepth() + "$first");
		}
	}

	private void writeCommaIfNecessary() {
		if (needsToWriteComma()) {
			code.beginControlFlow("if (!$L)", "$" + stackDepth() + "$first");
			writeComma();
			code.endControlFlow();
			code.addStatement("$L = false", "$" + stackDepth() + "$first");
		}
	}

	private void writeMap() {
		Type keyType = type.determineTypeArguments(Map.class).get(0);
		Type valueType = type.determineTypeArguments(Map.class).get(1);

		RHS.Variable entry = new RHS.Variable("entry$" + (stackDepth() + 1), false);

		RHS.Accessor value = new RHS.Accessor(entry, "getValue()", true);
		LHS.Field key = new LHS.Field("$L.getKey()", new Object[] { entry.name() });
		SELF valueNested = nest(valueType.getTypeMirror(), key, "value", value, true);

		startObject();
		code.beginControlFlow("for ($T<$T, $T> $L : " + rhs.format() + ".entrySet())",
			flatten(Map.Entry.class, keyType.getTypeMirror(), valueType.getTypeMirror(), entry.name(), rhs.args()));
		valueNested.build();
		code.endControlFlow();
		endObject();
	}

	protected void writeObjectAsMap() {
		Polymorphism.of(type.getTypeElement(), utils.elements).ifPresentOrElse(this::writePolymorphicObject, () -> {
			startObject();
			code.add("\n");
			writeObjectPropertiesAsFields();
			endObject();
		});
	}

	private void writePolymorphicObject(Polymorphism polymorphism) {
		Branch branch = Branch.IF;
		for (Polymorphism.Child child : polymorphism.children()) {
			branch.controlFlow(code, rhs.format() + " instanceof $T", flatten(rhs.args(), child.type()));
			branch = Branch.ELSE_IF;
			RHS.Variable casted = new RHS.Variable(propertyName() + "$" + stackDepth() + "$cast", false);
			code.addStatement("$T $L = ($T) " + rhs.format(), flatten(child.type(), casted.name, child.type(), rhs.args()));

			utils.prototypeFinder.findPrototype(utils.tf.getType(child.type()), prototype, false, true).ifPresentOrElse(delegate -> {
				String delegateField = generatedClass.getOrCreateDelegateeField(prototype.blueprint(), delegate.blueprint());
				VariableElement calleeContext = delegate.prototype().contextParameter().orElseThrow(() -> new IllegalArgumentException("Delegate method must have a context parameter"));
				VariableElement callerContext = prototype.contextParameter().orElseThrow(() -> new IllegalArgumentException("Prototype method must have a context parameter"));
				if (!utils.types.isAssignable(callerContext.asType(), calleeContext.asType())) {
				throw new IllegalArgumentException("Context types must be compatible");
				}
				code.addStatement("$L.setPendingDiscriminator($S, $S)", callerContext, polymorphism.discriminator(), child.name());
				// TODO crude call
				nest(child.type(), lhs, "instance", casted, true)
					.invokeDelegate(delegateField, delegate.method());
			}, () -> {
				startObject();
				code.add("\n");
				nest(utils.commonTypes.string, new LHS.Field("$S", new Object[] { polymorphism.discriminator() }),
					polymorphism.discriminator(), new RHS.StringLiteral(child.name()), false).build();
				nest(child.type(), lhs, "instance", casted, true).writeObjectPropertiesAsFields();
				endObject();
			});
		}
		if (branch == Branch.IF) {
			throw new IllegalArgumentException("Polymorphism must have at least one child type");
		}
		code.nextControlFlow("else");
		code.addStatement("throw new $T($S)", IllegalArgumentException.class, "Unknown type");
		code.endControlFlow();
	}

	void writeObjectPropertiesAsFields() {
		if (canBePolyChild) {
			VariableElement context = prototype.contextParameter().orElseThrow();
			code.beginControlFlow("if ($L.isDiscriminatorPending())", context);
			nest(utils.commonTypes.string, new LHS.Field("$L.pendingDiscriminatorProperty", new Object[] { context.getSimpleName() }), "discriminator",
				new RHS.Accessor(new RHS.Variable(context.getSimpleName().toString(), false), "pendingDiscriminatorValue", false), false).build();
			code.addStatement("$L.pendingDiscriminatorProperty = null", context);
			code.endControlFlow();
		}
		DeclaredType t = (DeclaredType) type.getTypeMirror();

		type.getPropertyReadAccessors().forEach((propertyName, accessor) -> {
			LHS lhs = new LHS.Field("$S", new Object[] { propertyName });
			RHS.Accessor nest = new RHS.Accessor(rhs, accessor.getReadValueSource(), true);
			SELF nested = nest(accessor.getAccessedType(), lhs, propertyName, nest, true);
			nested.build();
			code.add("\n");
		});
	}

	private void writeEnum() {
		RHS.Variable enumValue = new RHS.Variable(propertyName() + "$" + stackDepth() + "$string", false);
		code.addStatement("$T $L = " + rhs.format() + ".name()", flatten(utils.commonTypes.string, enumValue.name(), rhs.args()));
		nest(utils.commonTypes.string, lhs, null, enumValue, false).build();
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

	boolean needsToWriteComma() {
		return false;
	}

	protected void writeComma() { }

	protected abstract void invokeDelegate(String instance, InstantiatedMethod callee);

	protected abstract SELF nest(TypeMirror type, LHS lhs, String propertyName, RHS rhs, boolean stackRelevantType);

	sealed interface LHS {
		record Return() implements LHS { }
		record Array() implements LHS { }
		record Field(String format, Object[] args) implements LHS, Snippet { }
	}

	sealed interface RHS extends Snippet {
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
