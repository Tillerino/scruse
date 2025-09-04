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
import org.mapstruct.ap.internal.util.accessor.ReadAccessor;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.Snippet.TypedSnippet;
import org.tillerino.scruse.processor.apis.AbstractReaderGenerator.Branch;
import org.tillerino.scruse.processor.apis.AbstractWriterGenerator.RHS.AnySnippet;
import org.tillerino.scruse.processor.apis.AbstractWriterGenerator.RHS.Variable;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.config.ConfigProperty.LocationKind;
import org.tillerino.scruse.processor.features.Delegation.Delegatee;
import org.tillerino.scruse.processor.features.IgnoreProperties;
import org.tillerino.scruse.processor.features.IgnoreProperty;
import org.tillerino.scruse.processor.features.Polymorphism;
import org.tillerino.scruse.processor.features.PropertyName;
import org.tillerino.scruse.processor.features.References.Setup;
import org.tillerino.scruse.processor.features.Verification.ProtoAndProps;
import org.tillerino.scruse.processor.util.Exceptions;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public abstract class AbstractWriterGenerator<SELF extends AbstractWriterGenerator<SELF>>
        extends AbstractCodeGeneratorStack<SELF> {
    protected final LHS lhs;

    protected final RHS rhs;

    protected AbstractWriterGenerator(
            SELF parent,
            Type type,
            Property property,
            RHS rhs,
            LHS lhs,
            boolean isStackRelevantType,
            AnyConfig config) {
        super(parent, type, isStackRelevantType, property, config);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    protected AbstractWriterGenerator(
            AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        super(
                utils,
                generatedClass,
                prototype,
                CodeBlock.builder(),
                null,
                utils.tf.getType(prototype.instantiatedParameters().get(0).type()),
                true,
                null,
                prototype.config());
        this.rhs = new RHS.Variable(
                prototype.methodElement().getParameters().get(0).getSimpleName().toString(), true);
        this.lhs = new LHS.Return();
    }

    public CodeBlock.Builder build() {
        Optional<Delegatee> delegate = utils.delegation
                // delegate to any of the used blueprints
                .findDelegatee(type, prototype, !(lhs instanceof LHS.Return), stackDepth() > 1, config, generatedClass);
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
                beginControlFlow("if ($T.isFinite(" + rhs.format() + "))", flatten(boxedType, rhs.args()));
                writePrimitive(type.getTypeMirror());
                nextControlFlow("else");
                RHS asString = new RHS.AnySnippet(Snippet.of("$T.toString($C)", boxedType, rhs), false);
                nest(utils.commonTypes.string, lhs, null, asString, false, config)
                        .build();
                endControlFlow();
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
                beginControlFlow("if ($L != null)", v.name());
            } else {
                RHS.Variable nest = new RHS.Variable(
                        createVariable(property.canonicalName()).name(), true);
                addStatement("$T $L = " + rhs.format(), flatten(type.getTypeMirror(), nest.name(), rhs.args()));
                nest(type.getTypeMirror(), lhs, null, nest, false, config).writeNullable();
                return;
            }
        }

        writeNullCheckedObject();

        if (rhs.nullable()) {
            nextControlFlow("else");
            writeNull();
            endControlFlow();
        }
    }

    /**
     * Writes non-primitive types that are known to be non-null. This is a good method to override if you want to add
     * specializations for some types that require a dedicated null check.
     */
    protected void writeNullCheckedObject() {
        Optional<Setup> referenceSetup = utils.references.resolveSetup(config, prototype, type.getTypeMirror());
        if (referenceSetup.isPresent()) {
            Setup setup = referenceSetup.get();
            Variable idVar = new Variable(createVariable("id").name(), false);
            TypeMirror idType = setup.finalIdType(type, utils);
            addStatement("$T $C = $C", idType, idVar, setup.previouslyWritten(rhs));
            beginControlFlow("if ($C != null)", idVar);
            nest(idType, lhs, new Property("id", "id"), idVar, true, config.propagateTo(LocationKind.DTO))
                    .build();
            nextControlFlow("else");
        }

        Optional<TypedSnippet> converter = utils.converters
                .findOutputConverter(TypedSnippet.of(type.getTypeMirror(), rhs), prototype, config, generatedClass)
                .or(() -> utils.converters.findJsonValueMethod(TypedSnippet.of(type.getTypeMirror(), rhs)));
        if (converter.isPresent()) {
            TypedSnippet converted = converter.get();
            RHS.Variable newValue = new RHS.Variable(createVariable("converted").name(), true);
            addStatement(Snippet.of("$T $L = $C", converted.type(), newValue.name, converted));
            nest(converted.type(), lhs, null, newValue, true, config).build();
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
        referenceSetup.ifPresent(__ -> endControlFlow());
    }

    protected void writeIterable() {
        Type componentType = type.isArrayType()
                ? type.getComponentType()
                : type.determineTypeArguments(Iterable.class).get(0);

        RHS.Variable elemVar = new RHS.Variable(createVariable("item").name(), true);
        SELF nested = nest(componentType.getTypeMirror(), new LHS.Array(), Property.ITEM, elemVar, true, config);
        startArray();
        ScopedVar firstMarker = writeCommaMarkerIfNecessary();
        beginControlFlow(
                "for ($T $L : " + rhs.format() + ")", flatten(nested.type.getTypeMirror(), elemVar.name(), rhs.args()));
        writeCommaIfNecessary(firstMarker);
        Exceptions.runWithContext(nested::build, "component", componentType);
        endControlFlow();
        endArray();
    }

    private ScopedVar writeCommaMarkerIfNecessary() {
        if (needsToWriteComma()) {
            ScopedVar variable = createVariable("first");
            addStatement("boolean $C = true", variable);
            return variable;
        }
        return null;
    }

    private void writeCommaIfNecessary(ScopedVar firstMarker) {
        if (firstMarker != null) {
            beginControlFlow("if (!$C)", firstMarker);
            writeComma();
            endControlFlow();
            addStatement("$C = false", firstMarker);
        }
    }

    private void writeMap() {
        Type keyType = type.determineTypeArguments(Map.class).get(0);
        Type valueType = type.determineTypeArguments(Map.class).get(1);

        RHS.Variable entry = new RHS.Variable(createVariable("entry").name(), false);

        RHS.Accessor value = new RHS.Accessor(entry, "getValue()", true);
        LHS.Field key = new LHS.Field("$L.getKey()", new Object[] {entry.name()});
        SELF valueNested = nest(valueType.getTypeMirror(), key, Property.VALUE, value, true, config);

        startObject();
        beginControlFlow(
                "for ($T<$T, $T> $L : " + rhs.format() + ".entrySet())",
                flatten(Map.Entry.class, keyType.getTypeMirror(), valueType.getTypeMirror(), entry.name(), rhs.args()));
        Exceptions.runWithContext(valueNested::build, "value", valueType);
        endControlFlow();
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
            branch.controlFlow(this, rhs.format() + " instanceof $T", flatten(rhs.args(), child.type()));
            branch = Branch.ELSE_IF;
            RHS.Variable casted =
                    new RHS.Variable(createVariable(propertyName() + "Cast").name(), false);
            addStatement("$T $L = ($T) " + rhs.format(), flatten(child.type(), casted.name, child.type(), rhs.args()));

            utils.delegation
                    .findDelegatee(utils.tf.getType(child.type()), prototype, false, true, config, generatedClass)
                    .ifPresentOrElse(
                            delegatee -> {
                                VariableElement callerContext = prototype
                                        .contextParameter()
                                        .orElseThrow(() -> new ContextedRuntimeException(
                                                "Prototype method must have a context parameter"));
                                if (!delegatee.method().hasParameterAssignableFrom(callerContext.asType(), utils)) {
                                    throw new ContextedRuntimeException(
                                            "Delegate method must have a context parameter");
                                }
                                if (!utils.types.isAssignable(callerContext.asType(), callerContext.asType())) {
                                    throw new ContextedRuntimeException("Context types must be compatible");
                                }
                                addStatement(
                                        "$L.setPendingDiscriminator($S, $S)",
                                        callerContext,
                                        polymorphism.discriminator(),
                                        child.name());
                                // TODO crude call
                                Exceptions.runWithContext(
                                        () -> nest(child.type(), lhs, Property.INSTANCE, casted, true, config)
                                                .invokeDelegate(delegatee.fieldOrParameter(), delegatee.method()),
                                        "instance",
                                        child.type());
                            },
                            () -> {
                                startObject();
                                code.add("\n");
                                nest(
                                                utils.commonTypes.string,
                                                new LHS.Field("$S", new Object[] {polymorphism.discriminator()}),
                                                new Property("discriminator", polymorphism.discriminator()),
                                                new RHS.StringLiteral(child.name()),
                                                false,
                                                config)
                                        .build();
                                Exceptions.runWithContext(
                                        () -> nest(child.type(), lhs, Property.INSTANCE, casted, true, config)
                                                .writeObjectPropertiesAsFields(),
                                        "instance",
                                        child.type());
                                endObject();
                            });
        }
        if (branch == Branch.IF) {
            throw new ContextedRuntimeException("Polymorphism must have at least one child type");
        }
        nextControlFlow("else");
        addStatement("throw new $T($S)", IllegalArgumentException.class, "Unknown type");
        endControlFlow();
    }

    void writeObjectPropertiesAsFields() {
        if (canBePolyChild) {
            VariableElement context = prototype.contextParameter().orElseThrow();
            beginControlFlow("if ($L.isDiscriminatorPending())", context);
            nest(
                            utils.commonTypes.string,
                            new LHS.Field("$L.pendingDiscriminatorProperty", new Object[] {context.getSimpleName()}),
                            Property.DISCRIMINATOR,
                            new RHS.Accessor(
                                    new RHS.Variable(context.getSimpleName().toString(), false),
                                    "pendingDiscriminatorValue",
                                    false),
                            false,
                            config)
                    .build();
            addStatement("$L.pendingDiscriminatorProperty = null", context);
            endControlFlow();
        }

        Optional<Setup> referencesSetup = utils.references.resolveSetup(config, prototype, type.getTypeMirror());
        referencesSetup.ifPresent(setup -> setup.generateId(rhs).ifPresent(id -> nest(
                        setup.idType(),
                        new LHS.Field("$S", new Object[] {setup.property()}),
                        new Property("id", "id"),
                        new AnySnippet(id, false),
                        true,
                        config.propagateTo(LocationKind.DTO))
                .build()));

        ProtoAndProps verificationForDto =
                generatedClass.verificationForBlueprint.addWriter(prototype, type.getTypeMirror());
        Set<String> ignoredProperties =
                config.resolveProperty(IgnoreProperties.IGNORED_PROPERTIES).value();

        outputProperties(type).forEach(property -> {
            if (property.config()
                            .resolveProperty(IgnoreProperty.IGNORE_PROPERTY)
                            .value()
                    || ignoredProperties.contains(property.externalName())) {
                return;
            }
            verificationForDto.addProperty(
                    property.externalName(), property.accessor().getAccessedType(), property.config());

            LHS lhs = new LHS.Field("$S", new Object[] {property.externalName()});
            RHS.Accessor accessorCall =
                    new RHS.Accessor(rhs, property.accessor().getReadValueSource(), true);
            SELF nested = nest(
                    property.accessor().getAccessedType(),
                    lhs,
                    new Property(property.canonicalName(), property.externalName()),
                    accessorCall,
                    true,
                    property.config());
            Exceptions.runWithContext(nested::build, "property", property.canonicalName());
            referencesSetup.flatMap(s -> s.rememberId(rhs, accessorCall)).ifPresent(this::addStatement);
            code.add("\n");
        });
    }

    private void writeEnum() {
        RHS.Variable enumValue =
                new RHS.Variable(createVariable(propertyName() + "String").name(), false);
        addStatement(
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
            TypeMirror type, LHS lhs, Property property, RHS rhs, boolean stackRelevantType, AnyConfig config);

    Snippet base64Encode(Snippet snippet) {
        return Snippet.of("$T.getEncoder().encodeToString($C)", Base64.class, snippet);
    }

    Snippet charArrayToString(Snippet snippet) {
        return Snippet.of("new $T($C)", String.class, snippet);
    }

    protected List<OutputProperty> outputProperties(Type type) {
        return type.getPropertyReadAccessors().entrySet().stream()
                .map(entry -> {
                    String canonicalName = entry.getKey();
                    ReadAccessor accessor = entry.getValue();
                    AnyConfig propertyConfig = AnyConfig.fromAccessorConsideringField(
                                    accessor, accessor.getSimpleName(), type.getTypeMirror(), canonicalName, utils)
                            .merge(config);
                    String externalName = PropertyName.resolvePropertyName(propertyConfig, canonicalName);
                    return new OutputProperty(canonicalName, externalName, accessor, propertyConfig);
                })
                .toList();
    }

    protected record OutputProperty(
            String canonicalName, String externalName, ReadAccessor accessor, AnyConfig config) {}

    protected sealed interface LHS {

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
