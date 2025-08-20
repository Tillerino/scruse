package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import java.util.*;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.apis.AbstractReaderGenerator.Branch;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.features.IgnoreProperties;
import org.tillerino.scruse.processor.features.IgnoreProperty;
import org.tillerino.scruse.processor.features.Polymorphism;
import org.tillerino.scruse.processor.features.PropertyName;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public abstract class AbstractWriterGenerator<SELF extends AbstractWriterGenerator<SELF>>
        extends AbstractCodeGeneratorStack<SELF> {
    protected final LHS lhs;

    protected final RHS rhs;

    protected AbstractWriterGenerator(
            AnnotationProcessorUtils utils,
            GeneratedClass generatedClass,
            ScrusePrototype prototype,
            CodeBlock.Builder code,
            SELF parent,
            Type type,
            String propertyName,
            RHS rhs,
            LHS lhs,
            boolean isStackRelevantType,
            AnyConfig config) {
        super(utils, generatedClass, prototype, code, parent, type, isStackRelevantType, propertyName, config);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    protected AbstractWriterGenerator(
            AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        this(
                utils,
                generatedClass,
                prototype,
                CodeBlock.builder(),
                null,
                utils.tf.getType(prototype.instantiatedParameters().get(0).type()),
                null,
                new RHS.Variable(
                        prototype
                                .methodElement()
                                .getParameters()
                                .get(0)
                                .getSimpleName()
                                .toString(),
                        true),
                new LHS.Return(),
                true,
                prototype.config());
    }

    public CodeBlock.Builder build() {
        Optional<Delegatee> delegate = utils.delegation
                // delegate to any of the used blueprints
                .findPrototype(type, prototype, !(lhs instanceof LHS.Return), stackDepth() > 1, config)
                .map(d -> new Delegatee(
                        generatedClass.getOrCreateDelegateeField(prototype.blueprint(), d.blueprint()), d.method()))
                .or(this::findDelegateeInMethodParameters);
        if (delegate.isPresent()) {
            invokeDelegate(delegate.get().fieldOrParameter(), delegate.get().method());
            return code;
        }

        detectSelfReferencingType();
        if (type.isPrimitive()) {
            PrimitiveType pt = (PrimitiveType) type.getTypeMirror();
            if (List.of(TypeKind.FLOAT, TypeKind.DOUBLE).contains(pt.getKind())
                    && features().onlySupportsFiniteNumbers()) {
                TypeMirror boxedType = utils.types.boxedClass(pt).asType();
                code.beginControlFlow("if ($T.isFinite(" + rhs.format() + "))", flatten(boxedType, rhs.args()));
                writePrimitive(type.getTypeMirror());
                code.nextControlFlow("else");
                RHS asString = new RHS.AnySnippet(Snippet.of("$T.toString($C)", boxedType, rhs), false);
                nest(utils.commonTypes.string, lhs, null, asString, false, config)
                        .build();
                code.endControlFlow();
            } else {
                writePrimitive(type.getTypeMirror());
            }
        } else {
            writeNullable();
        }
        return code;
    }

    /**
     * Writes non-primitive types. This is a good method to override if you want to add specializations for some types
     * that work with null values.
     */
    protected void writeNullable() {
        if (rhs.nullable()) {
            if (rhs instanceof RHS.Variable v) {
                code.beginControlFlow("if ($L != null)", v.name());
            } else {
                RHS.Variable nest = new RHS.Variable(property + "$" + (stackDepth() + 1), true);
                code.addStatement("$T $L = " + rhs.format(), flatten(type.getTypeMirror(), nest.name(), rhs.args()));
                nest(type.getTypeMirror(), lhs, null, nest, false, config).build();
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
     * Writes non-primitive types that are known to be non-null. This is a good method to override if you want to add
     * specializations for some types that require a dedicated null check.
     */
    protected void writeNullCheckedObject() {
        Optional<InstantiatedMethod> jsonValueMethod = utils.annotations.findJsonValueMethod(type.getTypeMirror());
        if (jsonValueMethod.isPresent()) {
            InstantiatedMethod method = jsonValueMethod.get();
            RHS.Variable newValue = new RHS.Variable("$" + stackDepth() + "$value", true);
            RHS.Accessor valueMethodInvocation = new RHS.Accessor(rhs, method.name() + "()", true);
            Snippet.of("$T $L = $C", method.returnType(), newValue.name, valueMethodInvocation)
                    .addStatementTo(code);
            nest(method.returnType(), lhs, null, newValue, true, config).build();
            return;
        }
        Optional<InstantiatedMethod> converter =
                utils.converters.findOutputConverter(prototype.blueprint(), type.getTypeMirror(), config);
        if (converter.isPresent()) {
            InstantiatedMethod method = converter.get();
            RHS.Variable newValue = new RHS.Variable("$" + stackDepth() + "$converted", true);
            Snippet.of(
                            "$T $L = $C($C$C)",
                            method.returnType(),
                            newValue.name,
                            method.callSymbol(utils),
                            rhs,
                            Snippet.joinPrependingCommaToEach(prototype.findArguments(method, 1, generatedClass)))
                    .addStatementTo(code);
            nest(method.returnType(), lhs, null, newValue, true, config).build();
            return;
        }
        if (utils.isBoxed(type.getTypeMirror())) {
            nest(utils.types.unboxedType(type.getTypeMirror()), lhs, null, rhs, false, config)
                    .build();
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
            throw new ContextedRuntimeException("Missing serializer for type variable " + type.getTypeMirror());
        } else {
            writeObjectAsMap();
        }
    }

    protected void writeIterable() {
        Type componentType = type.isArrayType()
                ? type.getComponentType()
                : type.determineTypeArguments(Iterable.class).iterator().next().getTypeBound();

        RHS.Variable elemVar = new RHS.Variable("item$" + (stackDepth() + 1), true);
        SELF nested = nest(componentType.getTypeMirror(), new LHS.Array(), "item", elemVar, true, config);
        startArray();
        writeCommaMarkerIfNecessary();
        code.beginControlFlow(
                "for ($T $L : " + rhs.format() + ")", flatten(nested.type.getTypeMirror(), elemVar.name(), rhs.args()));
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

        RHS.Variable entry = new RHS.Variable("$" + (stackDepth() + 1) + "$entry", false);

        RHS.Accessor value = new RHS.Accessor(entry, "getValue()", true);
        LHS.Field key = new LHS.Field("$L.getKey()", new Object[] {entry.name()});
        SELF valueNested = nest(valueType.getTypeMirror(), key, "value", value, true, config);

        startObject();
        code.beginControlFlow(
                "for ($T<$T, $T> $L : " + rhs.format() + ".entrySet())",
                flatten(Map.Entry.class, keyType.getTypeMirror(), valueType.getTypeMirror(), entry.name(), rhs.args()));
        valueNested.build();
        code.endControlFlow();
        endObject();
    }

    protected void writeObjectAsMap() {
        TypeElement typeElement = type.getTypeElement();
        if (typeElement == null) {
            throw new ContextedRuntimeException("Trying to write object which has no type element: " + type);
        }
        Polymorphism.of(typeElement, utils).ifPresentOrElse(this::writePolymorphicObject, () -> {
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
            code.addStatement(
                    "$T $L = ($T) " + rhs.format(), flatten(child.type(), casted.name, child.type(), rhs.args()));

            utils.delegation
                    .findPrototype(utils.tf.getType(child.type()), prototype, false, true, config)
                    .ifPresentOrElse(
                            delegate -> {
                                String delegateField = generatedClass.getOrCreateDelegateeField(
                                        prototype.blueprint(), delegate.blueprint());
                                VariableElement calleeContext = delegate.prototype()
                                        .contextParameter()
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                "Delegate method must have a context parameter"));
                                VariableElement callerContext = prototype
                                        .contextParameter()
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                "Prototype method must have a context parameter"));
                                if (!utils.types.isAssignable(callerContext.asType(), calleeContext.asType())) {
                                    throw new ContextedRuntimeException("Context types must be compatible");
                                }
                                code.addStatement(
                                        "$L.setPendingDiscriminator($S, $S)",
                                        callerContext,
                                        polymorphism.discriminator(),
                                        child.name());
                                // TODO crude call
                                nest(child.type(), lhs, "instance", casted, true, config)
                                        .invokeDelegate(delegateField, delegate.method());
                            },
                            () -> {
                                startObject();
                                code.add("\n");
                                nest(
                                                utils.commonTypes.string,
                                                new LHS.Field("$S", new Object[] {polymorphism.discriminator()}),
                                                polymorphism.discriminator(),
                                                new RHS.StringLiteral(child.name()),
                                                false,
                                                config)
                                        .build();
                                nest(child.type(), lhs, "instance", casted, true, config)
                                        .writeObjectPropertiesAsFields();
                                endObject();
                            });
        }
        if (branch == Branch.IF) {
            throw new ContextedRuntimeException("Polymorphism must have at least one child type");
        }
        code.nextControlFlow("else");
        code.addStatement("throw new $T($S)", IllegalArgumentException.class, "Unknown type");
        code.endControlFlow();
    }

    void writeObjectPropertiesAsFields() {
        if (canBePolyChild) {
            VariableElement context = prototype.contextParameter().orElseThrow();
            code.beginControlFlow("if ($L.isDiscriminatorPending())", context);
            nest(
                            utils.commonTypes.string,
                            new LHS.Field("$L.pendingDiscriminatorProperty", new Object[] {context.getSimpleName()}),
                            "discriminator",
                            new RHS.Accessor(
                                    new RHS.Variable(context.getSimpleName().toString(), false),
                                    "pendingDiscriminatorValue",
                                    false),
                            false,
                            config)
                    .build();
            code.addStatement("$L.pendingDiscriminatorProperty = null", context);
            code.endControlFlow();
        }

        Set<String> ignoredProperties =
                config.resolveProperty(IgnoreProperties.IGNORED_PROPERTIES).value();

        type.getPropertyReadAccessors().forEach((canonicalPropertyName, accessor) -> {
            AnyConfig propertyConfig = AnyConfig.fromAccessorConsideringField(
                    accessor, type.getTypeElement(), canonicalPropertyName, utils);
            if (propertyConfig.resolveProperty(IgnoreProperty.IGNORE_PROPERTY).value()) {
                return;
            }
            String propertyName = PropertyName.resolvePropertyName(propertyConfig, canonicalPropertyName);

            if (ignoredProperties.contains(propertyName)) {
                return;
            }

            LHS lhs = new LHS.Field("$S", new Object[] {propertyName});
            RHS.Accessor nest = new RHS.Accessor(rhs, accessor.getReadValueSource(), true);
            SELF nested = nest(accessor.getAccessedType(), lhs, propertyName, nest, true, config);
            nested.build();
            code.add("\n");
        });
    }

    private void writeEnum() {
        RHS.Variable enumValue = new RHS.Variable(propertyName() + "$" + stackDepth() + "$string", false);
        code.addStatement(
                "$T $L = " + rhs.format() + ".name()", flatten(utils.commonTypes.string, enumValue.name(), rhs.args()));
        nest(utils.commonTypes.string, lhs, null, enumValue, false, config).build();
    }

    protected Features features() {
        return new Features(false);
    }

    protected abstract void writeNull();

    protected abstract void writeString(StringKind stringKind);

    protected abstract void writeBinary(BinaryKind binaryKind);

    /** @param typeMirror if non-null, the type is a primitive wrapper and this is the primitive type */
    public abstract void writePrimitive(TypeMirror typeMirror);

    protected abstract void startArray();

    protected abstract void endArray();

    protected abstract void startObject();

    protected abstract void endObject();

    boolean needsToWriteComma() {
        return false;
    }

    protected void writeComma() {}

    protected abstract void invokeDelegate(String instance, InstantiatedMethod callee);

    protected abstract SELF nest(
            TypeMirror type, LHS lhs, String propertyName, RHS rhs, boolean stackRelevantType, AnyConfig config);

    Snippet base64Encode(Snippet snippet) {
        return Snippet.of("$T.getEncoder().encodeToString($C)", Base64.class, snippet);
    }

    Snippet charArrayToString(Snippet snippet) {
        return Snippet.of("new $T($C)", String.class, snippet);
    }

    sealed interface LHS {

        record Return() implements LHS {}

        record Array() implements LHS {}

        record Field(String format, Object[] args) implements LHS, Snippet {}
    }

    sealed interface RHS extends Snippet {
        default String format() {
            if (this instanceof Variable) {
                return "$L";
            } else if (this instanceof Accessor a) {
                return a.object().format() + ".$L";
            } else if (this instanceof StringLiteral) {
                return "$S";
            } else if (this instanceof AnySnippet a) {
                return a.snippet().format();
            } else {
                throw new ContextedRuntimeException();
            }
        }

        default Object[] args() {
            if (this instanceof Variable v) {
                return new Object[] {v.name()};
            } else if (this instanceof Accessor a) {
                return new Object[] {a.object().args(), a.accessorLiteral()};
            } else if (this instanceof StringLiteral s) {
                return new Object[] {s.value()};
            } else if (this instanceof AnySnippet a) {
                return a.snippet().args();
            } else {
                throw new ContextedRuntimeException();
            }
        }

        boolean nullable();

        record Variable(String name, boolean nullable) implements RHS {}

        /** @param accessorLiteral can be field or method - append () for method */
        record Accessor(RHS object, String accessorLiteral, boolean nullable) implements RHS {}

        record StringLiteral(String value) implements RHS {
            @Override
            public boolean nullable() {
                return false;
            }
        }

        record AnySnippet(Snippet snippet, boolean nullable) implements RHS {}
    }

    record Features(boolean onlySupportsFiniteNumbers) {}
}
