package org.tillerino.scruse.processor.apis;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.mapstruct.ap.internal.gem.CollectionMappingStrategyGem.SETTER_PREFERRED;
import static org.tillerino.scruse.processor.Snippet.joinPrependingCommaToEach;
import static org.tillerino.scruse.processor.Snippet.of;
import static org.tillerino.scruse.processor.apis.AbstractCodeGeneratorStack.Property.ITEM;
import static org.tillerino.scruse.processor.apis.AbstractCodeGeneratorStack.StringKind.STRING;
import static org.tillerino.scruse.processor.apis.AbstractReaderGenerator.Branch.ELSE_IF;
import static org.tillerino.scruse.processor.apis.AbstractReaderGenerator.Branch.IF;
import static org.tillerino.scruse.processor.apis.AbstractReaderGenerator.LHS.Collection;
import static org.tillerino.scruse.processor.apis.AbstractReaderGenerator.LHS.Variable;
import static org.tillerino.scruse.processor.apis.AbstractReaderGenerator.LHS.from;
import static org.tillerino.scruse.processor.config.AnyConfig.fromAccessorConsideringField;
import static org.tillerino.scruse.processor.features.IgnoreProperty.IGNORE_PROPERTY;
import static org.tillerino.scruse.processor.features.PropertyName.resolvePropertyName;
import static org.tillerino.scruse.processor.util.Exceptions.runWithContext;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.util.accessor.Accessor;
import org.tillerino.scruse.input.EmptyArrays;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.config.ConfigProperty.LocationKind;
import org.tillerino.scruse.processor.features.Creators.Creator;
import org.tillerino.scruse.processor.features.Delegation.Delegatee;
import org.tillerino.scruse.processor.features.Generics.TypeVar;
import org.tillerino.scruse.processor.features.IgnoreProperties;
import org.tillerino.scruse.processor.features.Polymorphism;
import org.tillerino.scruse.processor.features.UnknownProperties;
import org.tillerino.scruse.processor.features.Verification.ProtoAndProps;
import org.tillerino.scruse.processor.util.Exceptions;
import org.tillerino.scruse.processor.util.InstantiatedMethod;
import org.tillerino.scruse.processor.util.InstantiatedVariable;

public abstract class AbstractReaderGenerator<SELF extends AbstractReaderGenerator<SELF>>
        extends AbstractCodeGeneratorStack<SELF> {
    protected final LHS lhs;

    AbstractReaderGenerator(AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        this(
                utils,
                generatedClass,
                prototype,
                CodeBlock.builder(),
                null,
                utils.tf.getType(prototype.instantiatedReturnType()),
                true,
                null,
                new LHS.Return(),
                prototype.config());
    }

    protected AbstractReaderGenerator(
            AnnotationProcessorUtils utils,
            GeneratedClass generatedClass,
            ScrusePrototype prototype,
            Builder code,
            SELF parent,
            Type type,
            boolean stackRelevantType,
            Property property,
            LHS lhs,
            AnyConfig config) {
        super(utils, generatedClass, prototype, code, parent, type, stackRelevantType, property, config);
        this.lhs = lhs;
    }

    public CodeBlock.Builder build() {
        initializeParser();
        return build(Branch.IF, true);
    }

    CodeBlock.Builder build(Branch branch, boolean nullable) {
        Optional<Delegatee> delegate = utils.delegation
                .findPrototype(type, prototype, !(lhs instanceof LHS.Return), stackDepth() > 1, config)
                .map(d -> new Delegatee(
                        generatedClass.getOrCreateDelegateeField(
                                prototype.blueprint(),
                                d.blueprint(),
                                !d.prototype().overrides()),
                        d.method()))
                .or(() -> utils.delegation.findDelegateeInMethodParameters(prototype, type));
        if (delegate.isPresent()) {
            if (branch != Branch.IF) {
                nextControlFlow("else");
            }
            invokeDelegate(delegate.get().fieldOrParameter(), delegate.get().method());
            if (branch != Branch.IF) {
                endControlFlow();
            }
            return code;
        }
        Optional<InstantiatedMethod> converter =
                utils.converters.findInputConverter(prototype.blueprint(), type.getTypeMirror(), config);
        if (converter.isPresent()) {
            if (branch != IF) {
                nextControlFlow("else");
            }
            InstantiatedMethod method = converter.get();

            Variable converterArg = Variable.from(createVariable("toConvert"));
            addStatement(of("$T $C", method.parameters().get(0).type(), converterArg));
            runWithContext(
                    () -> nest(method.parameters().get(0).type(), null, converterArg, true, config)
                            .build(IF, nullable),
                    "converter",
                    converter.get());
            addStatement(lhs.assign(of("$C($C)", method.callSymbol(utils), converterArg)));

            if (branch != IF) {
                endControlFlow();
            }
            return code;
        }
        detectSelfReferencingType();
        if (type.isPrimitive()) {
            readPrimitive(branch, type.getTypeMirror());
        } else {
            readNullable(branch, nullable);
        }
        return code;
    }

    /**
     * Reads non-primitive types. This is a good method to override if you want to add specializations for some types
     * that work with null values.
     */
    protected void readNullable(Branch branch, boolean nullable) {
        if (nullable) {
            Snippet cond = nullCaseCondition();
            if (canBePolyChild) {
                branch.controlFlow(
                        this,
                        "!$L.isObjectOpen(false) && " + cond.format(),
                        flatten(prototype.contextParameter().get(), cond.args()));
            } else {
                branch.controlFlow(this, cond.format(), cond.args());
            }
            addStatement(lhs.assign("null"));
            readNullCheckedObject(Branch.ELSE_IF);
        } else {
            readNullCheckedObject(branch);
        }
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
            default -> throw new ContextedRuntimeException(type.getKind().toString());
        }
        if (type.getKind() == TypeKind.CHAR) {
            readCharFromString();
        } else {
            readPrimitive(type);
        }
        nextControlFlow("else");
        throwUnexpected(typeName);
        endControlFlow();
    }

    private void readCharFromString() {
        ScopedVar stringVar = readStringInstead();
        beginControlFlow("if ($C.length() == 1)", stringVar);
        addStatement(lhs.assign("$C.charAt(0)", stringVar));
        nextControlFlow("else");
        addStatement("throw new $T()", IOException.class);
        endControlFlow();
    }

    private void readNumberFromString(TypeMirror type) {
        ScopedVar stringVar = readStringInstead();

        beginControlFlow("if ($C.equals($S))", stringVar, "NaN");
        addStatement(lhs.assign("$L.NaN", capitalize(type.toString())));

        nextControlFlow("else if ($C.equals($S))", stringVar, "Infinity");
        addStatement(lhs.assign("$L.POSITIVE_INFINITY", capitalize(type.toString())));

        nextControlFlow("else if ($C.equals($S))", stringVar, "-Infinity");
        addStatement(lhs.assign("$L.NEGATIVE_INFINITY", capitalize(type.toString())));

        nextControlFlow("else");
        addStatement("throw new $T()", IOException.class);
        endControlFlow();
    }

    private ScopedVar readStringInstead() {
        ScopedVar stringVar = createVariable("string");
        SELF nested = nest(utils.commonTypes.string, null, LHS.Variable.from(stringVar), false, config);
        addStatement("$T $C", nested.type.getTypeMirror(), stringVar);
        nested.readString(StringKind.STRING);
        return stringVar;
    }

    private void readNullCheckedObject(Branch branch) {
        Optional<Creator> jsonCreatorMethod = utils.creators.findJsonCreatorMethod(type.getTypeMirror());
        if (jsonCreatorMethod.isPresent()) {
            if (jsonCreatorMethod.get() instanceof Creator.Converter c) {
                readFactory(branch, c.method());
            } else if (jsonCreatorMethod.get() instanceof Creator.Properties p) {
                readObject(branch, p);
            } else {
                throw Exceptions.unexpected();
            }
        } else if (utils.isBoxed(type.getTypeMirror())) {
            nest(utils.types.unboxedType(type.getTypeMirror()), null, lhs, false, config)
                    .build(branch, true);
        } else if (type.isString() || AnnotationProcessorUtils.isArrayOf(type, TypeKind.CHAR)) {
            readString(branch, type.isString() ? StringKind.STRING : StringKind.CHAR_ARRAY);
        } else if (type.isEnumType()) {
            readEnum(branch);
        } else if (type.isArrayType()) {
            readArray(branch);
        } else if (type.isIterableType()) {
            readCollection(branch);
        } else if (type.isMapType()) {
            readMap(branch);
        } else {
            readObject(branch, null);
        }
    }

    private void readFactory(Branch branch, InstantiatedMethod method) {
        if (branch == ELSE_IF) {
            nextControlFlow("else");
        }
        Variable creatorArg = Variable.from(createVariable("creator"));
        addStatement(of("$T $L", method.parameters().get(0).type(), creatorArg.name));
        runWithContext(
                () -> nest(method.parameters().get(0).type(), null, creatorArg, true, config)
                        .build(IF, false),
                "creator",
                method);
        addStatement(lhs.assign(of(
                "$C($L$C)",
                method.callSymbol(utils),
                creatorArg.name,
                joinPrependingCommaToEach(prototype.findArguments(method, 1, generatedClass)))));
        if (branch == ELSE_IF) {
            endControlFlow();
        }
    }

    private void readString(Branch branch, StringKind stringKind) {
        startStringCase(branch);
        readString(stringKind);
        nextControlFlow("else");
        throwUnexpected("string");
        endControlFlow();
    }

    private void readEnum(Branch branch) {
        startStringCase(branch);
        {
            String enumValuesField = generatedClass.getOrCreateEnumField(type.getTypeMirror());
            Variable enumVar = Variable.from(createVariable("string"));
            addStatement("$T $L", utils.commonTypes.string, enumVar.name);
            nest(utils.commonTypes.string, null, enumVar, false, config).readString(STRING);
            beginControlFlow("if ($L.containsKey($L))", enumValuesField, enumVar.name);
            addStatement(lhs.assign("$L.get($L)", enumValuesField, enumVar.name));
            nextControlFlow("else");
            throwUnexpected("enum value");
            endControlFlow();
        }
        nextControlFlow("else");
        throwUnexpected("string");
        endControlFlow();
    }

    private void readArray(Branch branch) {
        Type componentType = type.getComponentType();
        startArrayCase(branch);
        {
            TypeMirror rawComponentType = componentType.asRawType().getTypeMirror();
            TypeMirror rawRawComponentType =
                    rawComponentType.getKind().isPrimitive() ? rawComponentType : utils.commonTypes.object;
            code.add("// Like ArrayList\n");
            ScopedVar varName = createVariable("array");
            addStatement(
                    "$T[] $C = $T.EMPTY_$L_ARRAY",
                    rawRawComponentType,
                    varName,
                    EmptyArrays.class,
                    rawRawComponentType.getKind().isPrimitive()
                            ? rawRawComponentType.toString().toUpperCase()
                            : "OBJECT");
            String len = createVariable("len").name();
            addStatement("int $L = 0", len);
            iterateOverElements();
            {
                beginControlFlow("if ($L == $C.length)", len, varName);
                code.add("// simplified version of ArrayList growth\n");
                addStatement(
                        "$C = $T.copyOf($C, $T.max(10, $C.length + ($C.length >> 1)))",
                        varName,
                        java.util.Arrays.class,
                        varName,
                        Math.class,
                        varName,
                        varName);
                endControlFlow();

                Exceptions.runWithContext(
                        () -> nest(
                                        componentType.getTypeMirror(),
                                        Property.ITEM,
                                        new LHS.Array(varName.name(), len),
                                        true,
                                        config)
                                .build(Branch.IF, true),
                        "component",
                        componentType);
            }
            endControlFlow(); // end of loop
            afterArray();
            if (utils.types.isSameType(rawRawComponentType, rawComponentType)) {
                addStatement(lhs.assign("$T.copyOf($C, $L)", Arrays.class, varName, len));
            } else {
                addStatement(lhs.assign("$T.copyOf($C, $L, $T[].class)", Arrays.class, varName, len, rawComponentType));
            }
        }
        if (componentType.getTypeMirror().getKind() == TypeKind.BYTE) {
            startStringCase(ELSE_IF);
            ScopedVar stringVar = readStringInstead();
            addStatement(lhs.assign("$T.getDecoder().decode($C)", Base64.class, stringVar));
        }
        nextControlFlow("else");
        {
            throwUnexpected("array");
        }
        endControlFlow();
    }

    private void readCollection(Branch branch) {
        startArrayCase(branch);
        {
            Type componentType = type.determineTypeArguments(Iterable.class).get(0);
            TypeMirror collectionType = determineCollectionType();
            String varName = instantiateContainer(collectionType);

            iterateOverElements();
            {
                runWithContext(
                        () -> nest(componentType.getTypeMirror(), ITEM, new Collection(varName), true, config)
                                .build(IF, true),
                        "component",
                        componentType);
            }
            endControlFlow(); // end of loop
            afterArray();
            addStatement(lhs.assign("$L", varName));
        }
        nextControlFlow("else");
        throwUnexpected("array");
        endControlFlow();
    }

    private TypeMirror determineCollectionType() {
        if (!type.asRawType().isAbstract()) {
            return type.asRawType().getTypeMirror();
        } else if (utils.tf.getType(Set.class).isAssignableTo(type.asRawType())) {
            return utils.tf.getType(LinkedHashSet.class).asRawType().getTypeMirror();
        } else if (utils.tf.getType(List.class).isAssignableTo(type.asRawType())) {
            return utils.tf.getType(ArrayList.class).asRawType().getTypeMirror();
        } else {
            throw new ContextedRuntimeException(type.toString());
        }
    }

    private String instantiateContainer(TypeMirror collectionType) {
        String varName;
        if (lhs instanceof LHS.Variable v) {
            varName = v.name();
            addStatement("$L = new $T<>()", varName, collectionType);
        } else {
            varName = createVariable("container").name();
            addStatement("$T $L = new $T<>()", type.getTypeMirror(), varName, collectionType);
        }
        return varName;
    }

    private void readMap(Branch branch) {
        Snippet cond = objectCaseCondition();
        branch.controlFlow(this, cond.format(), flatten(cond.args()));
        {
            Type keyType = type.determineTypeArguments(Map.class).get(0);
            if (!utils.types.isSameType(keyType.getTypeMirror(), utils.commonTypes.string)) {
                throw new ContextedRuntimeException("Only String keys supported for now.");
            }
            Type valueType = type.determineTypeArguments(Map.class).get(1);
            TypeMirror mapType = determineMapType();
            String varName = instantiateContainer(mapType);
            iterateOverFields();
            {
                startFieldCase(Branch.IF);
                String keyVar = createVariable("key").name();
                readFieldNameInIteration(keyVar);
                Exceptions.runWithContext(
                        () -> nest(
                                        valueType.getTypeMirror(),
                                        Property.VALUE,
                                        new LHS.Map(varName, keyVar),
                                        true,
                                        config)
                                .build(Branch.IF, true),
                        "value",
                        valueType);
                nextControlFlow("else");
                throwUnexpected("field name");
                endControlFlow();
            }
            endControlFlow(); // end of loop
            afterObject();

            if (!(lhs instanceof LHS.Variable)) {
                addStatement(lhs.assign("$L", varName));
            }
        }
        nextControlFlow("else");
        throwUnexpected("object");
        endControlFlow();
    }

    private TypeMirror determineMapType() {
        if (!type.asRawType().isAbstract()) {
            return type.asRawType().getTypeMirror();
        } else {
            return utils.tf.getType(LinkedHashMap.class).asRawType().getTypeMirror();
        }
    }

    private void readObject(Branch branch, @Nullable Creator.Properties properties) {
        Snippet cond = objectCaseCondition();
        if (canBePolyChild) {
            branch.controlFlow(
                    this,
                    "$L.isObjectOpen(true) || " + cond.format(),
                    flatten(prototype.contextParameter().get(), cond.args()));
        } else {
            branch.controlFlow(this, cond.format(), cond.args());
        }

        if (properties != null) {
            readCreator(properties.method());
        } else {
            Optional.ofNullable(type.getTypeElement())
                    .flatMap(t -> Polymorphism.of(t, utils))
                    .ifPresentOrElse(this::readPolymorphicObject, this::readObjectFields);
        }

        nextControlFlow("else");
        throwUnexpected("object");
        endControlFlow();
    }

    private void readPolymorphicObject(Polymorphism polymorphism) {
        LHS.Variable discriminator = LHS.Variable.from(createVariable("discriminator"));
        addStatement("$T $L = null", utils.commonTypes.string, discriminator.name());
        nest(
                        utils.commonTypes.string,
                        new Property("discriminator", polymorphism.discriminator()),
                        discriminator,
                        false,
                        config)
                .readDiscriminator(polymorphism.discriminator());
        Branch branch = Branch.IF;
        for (Polymorphism.Child child : polymorphism.children()) {
            branch.controlFlow(this, "$L.equals($S)", discriminator.name(), child.name());
            utils.delegation
                    .findPrototype(utils.tf.getType(child.type()), prototype, false, true, config)
                    .ifPresentOrElse(
                            delegate -> {
                                String delegateField = generatedClass.getOrCreateDelegateeField(
                                        prototype.blueprint(),
                                        delegate.blueprint(),
                                        !delegate.prototype().overrides());
                                if (delegate.prototype().contextParameter().isEmpty()) {
                                    throw new ContextedRuntimeException(
                                            "Delegate method must have a context parameter");
                                }
                                prototype
                                        .contextParameter()
                                        .ifPresentOrElse(ctx -> addStatement("$L.markObjectOpen()", ctx), () -> {
                                            throw new ContextedRuntimeException(
                                                    "Prototype method must have a context parameter");
                                        });
                                Exceptions.runWithContext(
                                        () -> nest(child.type(), Property.INSTANCE, lhs, true, config)
                                                .invokeDelegate(delegateField, delegate.method()),
                                        "instance",
                                        child.type());
                            },
                            () -> Exceptions.runWithContext(
                                    () -> nest(child.type(), Property.INSTANCE, lhs, true, config)
                                            .readObjectFields(),
                                    "instance",
                                    child.type()));
            branch = Branch.ELSE_IF;
        }
        if (branch == Branch.IF) {
            throw new ContextedRuntimeException("No children for " + type);
        }
        nextControlFlow("else");
        addStatement("throw new $T($S + $L)", IOException.class, "Unknown type ", discriminator.name());
        endControlFlow(); // ends the loop
    }

    void readObjectFields() {
        if (type.getTypeElement() != null && type.isRecord()) {
            Map<TypeVar, TypeMirror> typeBindings =
                    utils.generics.recordTypeBindings((DeclaredType) type.getTypeMirror());
            InstantiatedMethod instantiatedConstructor = utils.generics.instantiateMethod(
                    ElementFilter.constructorsIn(type.getTypeElement().getEnclosedElements())
                            .get(0),
                    typeBindings,
                    LocationKind.CREATOR);
            readCreator(instantiatedConstructor);
        } else if (type.getTypeElement() != null) {
            readObjectFromAccessors();
        } else {
            throw new ContextedRuntimeException("Cannot read " + type);
        }
    }

    private void readCreator(InstantiatedMethod method) {
        ProtoAndProps verificationForDto =
                generatedClass.verificationForBlueprint.addReader(prototype, type.getTypeMirror());

        List<SELF> nested = new ArrayList<>();
        AnyConfig creatorConfig = method.config().merge(config);

        for (InstantiatedVariable parameter : method.parameters()) {
            AnyConfig propertyConfig = parameter.config().merge(creatorConfig);

            // use parameter name as variable name because we know it is valid - unlike the configured property name
            String varName = createVariable(parameter.name()).name();
            String propertyName = resolvePropertyName(propertyConfig, parameter.name());

            SELF nest = nest(
                    parameter.type(),
                    new Property(parameter.name(), propertyName),
                    new Variable(varName),
                    true,
                    propertyConfig);
            Snippet defaultValue = utils.defaultValues
                    .findInputDefaultValue(prototype.blueprint(), nest.type.getTypeMirror(), propertyConfig)
                    .map(m -> of("$C()", m.callSymbol(utils)))
                    .orElse(of("$L", nest.type.getNull()));
            addStatement(of("$T $L = $C", nest.type.getTypeMirror(), varName, defaultValue));
            if (propertyConfig.resolveProperty(IGNORE_PROPERTY).value()) {
                // we do need the default value to call the creator, so we only skip reading the value
                continue;
            }
            verificationForDto.addProperty(propertyName, parameter.type(), propertyConfig);
            nested.add(nest);
        }
        readProperties(nested);
        String args = nested.stream().map(p -> ((Variable) p.lhs).name()).collect(joining(", "));
        addStatement(lhs.assign(of("$C($L)", method.callSymbol(utils), args)));
    }

    private void readObjectFromAccessors() {
        ProtoAndProps verificationForDto =
                generatedClass.verificationForBlueprint.addReader(prototype, type.getTypeMirror());

        List<SELF> nested = new ArrayList<>();
        String objectVar = createVariable("object").name();
        addStatement("$T $L = new $T()", type.getTypeMirror(), objectVar, type.getTypeMirror());
        type.getPropertyWriteAccessors(SETTER_PREFERRED).forEach((canonicalPropertyName, accessor) -> {
            AnyConfig propertyConfig = fromAccessorConsideringField(
                            accessor, accessor.getSimpleName(), type, canonicalPropertyName, utils)
                    .merge(config);
            if (propertyConfig.resolveProperty(IGNORE_PROPERTY).value()) {
                return;
            }

            LHS lhs = from(accessor, objectVar);
            String propertyName = resolvePropertyName(propertyConfig, canonicalPropertyName);
            verificationForDto.addProperty(propertyName, accessor.getAccessedType(), propertyConfig);
            SELF nest = nest(
                    accessor.getAccessedType(),
                    new Property(canonicalPropertyName, propertyName),
                    lhs,
                    true,
                    propertyConfig);
            nested.add(nest);
        });
        readProperties(nested);
        addStatement(lhs.assign("$L", objectVar));
    }

    private void readProperties(List<SELF> properties) {
        iterateOverFields();
        startFieldCase(Branch.IF);
        String fieldVar = createVariable("field").name();
        readFieldNameInIteration(fieldVar);

        Set<String> ignoredProperties =
                config.resolveProperty(IgnoreProperties.IGNORED_PROPERTIES).value();

        beginControlFlow("switch($L)", fieldVar);
        for (SELF nest : properties) {
            if (ignoredProperties.contains(nest.property.serializedName())) {
                continue;
            }
            beginControlFlow("case $S:", nest.property.serializedName());
            Exceptions.runWithContext(() -> nest.build(Branch.IF, true), "property", nest.property.serializedName());
            addStatement("break");
            endControlFlow();
        }
        if (!ignoredProperties.isEmpty()) {
            beginControlFlow(Snippet.of("case $C:", IgnoreProperties.toSnippet(ignoredProperties)));
            skipValue();
            addStatement("break");
            endControlFlow();
        }
        {
            beginControlFlow("default:");
            if (UnknownProperties.shouldThrow(config)) {
                addStatement("throw new $T($S + $L + $S)", IOException.class, "Unrecognized field \"", fieldVar, "\"");
            } else {
                skipValue();
            }
            endControlFlow();
        }
        endControlFlow(); // ends the last field
        nextControlFlow("else");
        throwUnexpected("field name");
        endControlFlow();
        endControlFlow(); // ends the loop
        afterObject();
    }

    protected abstract void initializeParser();

    protected abstract void startFieldCase(Branch branch);

    protected abstract void startStringCase(Branch branch);

    protected abstract void startNumberCase(Branch branch);

    protected abstract Snippet objectCaseCondition();

    protected abstract void startArrayCase(Branch branch);

    protected abstract void startBooleanCase(Branch branch);

    protected abstract Snippet nullCaseCondition();

    protected abstract void readPrimitive(TypeMirror type);

    protected abstract void readString(StringKind stringKind);

    protected abstract void readFieldNameInIteration(String propertyName);

    protected abstract void iterateOverFields();

    protected abstract void skipValue();

    protected abstract void afterObject();

    protected abstract void readDiscriminator(String propertyName);

    protected abstract void iterateOverElements();

    protected abstract void afterArray();

    protected abstract void throwUnexpected(String expected);

    protected abstract void invokeDelegate(String instance, InstantiatedMethod callee);

    protected abstract SELF nest(
            TypeMirror type, @Nullable Property property, LHS lhs, boolean stackRelevantType, AnyConfig config);

    protected sealed interface LHS {
        default Snippet assign(Snippet s) {
            return assign(s.format(), s.args());
        }

        default Snippet assign(String string, Object... args) {
            if (this instanceof Return) {
                return Snippet.of("return " + string, args);
            } else if (this instanceof Variable v) {
                return Snippet.of("$L = " + string, flatten(v.name(), args));
            } else if (this instanceof Array a) {
                return Snippet.of("$L[$L++] = " + string, flatten(a.arrayVar(), a.indexVar(), args));
            } else if (this instanceof Collection c) {
                return Snippet.of("$L.add(" + string + ")", flatten(c.variable(), args));
            } else if (this instanceof Map m) {
                return Snippet.of("$L.put($L, " + string + ")", flatten(m.mapVar(), m.keyVar(), args));
            } else if (this instanceof Field f) {
                return Snippet.of("$L.$L = " + string, flatten(f.objectVar(), f.fieldName(), args));
            } else if (this instanceof Setter s) {
                return Snippet.of("$L.$L(" + string + ")", flatten(s.objectVar(), s.methodName(), args));
            } else {
                throw new ContextedRuntimeException(this.toString());
            }
        }

        static LHS from(Accessor a, String objectVar) {
            return switch (a.getAccessorType()) {
                case FIELD -> new Field(
                        objectVar, a.getElement().getSimpleName().toString());
                case SETTER -> new Setter(
                        objectVar, a.getElement().getSimpleName().toString());
                case ADDER -> throw new ContextedRuntimeException(
                                "Adders are not supported. Define a setter as an alternative to the adder.")
                        .addContextValue("adder", a.getElement());
                case GETTER -> throw new ContextedRuntimeException(
                                "Getters are not supported. Define a setter as an alternative to the getter.")
                        .addContextValue("setter", a.getElement());
                default -> throw new ContextedRuntimeException(
                        a.getAccessorType().toString());
            };
        }

        record Return() implements LHS {}

        record Variable(String name) implements LHS, Snippet {
            static Variable from(ScopedVar v) {
                return new Variable(v.name());
            }

            @Override
            public String format() {
                return "$L";
            }

            @Override
            public Object[] args() {
                return new Object[] {name};
            }
        }

        record Array(String arrayVar, String indexVar) implements LHS {}

        record Collection(String variable) implements LHS {}

        record Map(String mapVar, String keyVar) implements LHS {}

        record Field(String objectVar, String fieldName) implements LHS {}

        record Setter(String objectVar, String methodName) implements LHS {}
    }

    protected enum Branch {
        IF,
        ELSE_IF,
        ;

        <T extends AbstractCodeGeneratorStack<T>> AbstractCodeGeneratorStack<T> controlFlow(
                AbstractCodeGeneratorStack<T> code, String s, Object... args) {
            return switch (this) {
                case IF -> code.beginControlFlow("if (" + s + ")", args);
                case ELSE_IF -> code.nextControlFlow("else if (" + s + ")", args);
            };
        }
    }
}
