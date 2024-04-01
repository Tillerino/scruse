package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.ap.internal.gem.CollectionMappingStrategyGem;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.input.EmptyArrays;
import org.tillerino.scruse.processor.*;
import org.tillerino.scruse.processor.util.Generics.TypeVar;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public abstract class AbstractReaderGenerator<SELF extends AbstractReaderGenerator<SELF>>
        extends AbstractCodeGeneratorStack<SELF> {
    protected final LHS lhs;

    protected AbstractReaderGenerator(
            AnnotationProcessorUtils utils,
            GeneratedClass generatedClass,
            ScruseMethod prototype,
            CodeBlock.Builder code,
            SELF parent,
            Type type,
            boolean stackRelevantType,
            String propertyName,
            LHS lhs) {
        super(utils, generatedClass, prototype, code, parent, type, stackRelevantType, propertyName);
        this.lhs = lhs;
    }

    public CodeBlock.Builder build() {
        initializeParser();
        return build(Branch.IF);
    }

    public CodeBlock.Builder build(Branch branch) {
        Optional<Delegatee> delegate = utils.prototypeFinder
                .findPrototype(type, prototype, !(lhs instanceof LHS.Return), stackDepth() > 1)
                .map(d -> new Delegatee(
                        generatedClass.getOrCreateDelegateeField(prototype.blueprint(), d.blueprint()), d.method()))
                .or(this::findDelegateeInMethodParameters);
        if (delegate.isPresent()) {
            if (branch != Branch.IF) {
                code.nextControlFlow("else");
            }
            invokeDelegate(delegate.get().fieldOrParameter(), delegate.get().method());
            if (branch != Branch.IF) {
                code.endControlFlow();
            }
            return code;
        }
        detectSelfReferencingType();
        if (type.isPrimitive()) {
            readPrimitive(branch, type.getTypeMirror());
        } else {
            readNullable(branch);
        }
        return code;
    }

    /**
     * Reads non-primitive types. This is a good method to override if you want to add specializations for some types
     * that work with null values.
     */
    protected void readNullable(Branch branch) {
        Snippet cond = nullCaseCondition();
        if (canBePolyChild) {
            branch.controlFlow(
                    code,
                    "!$L.isObjectOpen(false) && " + cond.format(),
                    flatten(prototype.contextParameter().get(), cond.args()));
        } else {
            branch.controlFlow(code, cond.format(), cond.args());
        }
        lhs.assign(code, "null");
        readNullCheckedObject();
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
        String stringVar = readStringInstead();
        code.beginControlFlow("if ($L.length() == 1)", stringVar);
        lhs.assign(code, "$L.charAt(0)", stringVar);
        code.nextControlFlow("else");
        code.addStatement("throw new $T()", IOException.class);
        code.endControlFlow();
    }

    private void readNumberFromString(TypeMirror type) {
        String stringVar = readStringInstead();

        code.beginControlFlow("if ($L.equals($S))", stringVar, "NaN");
        lhs.assign(code, "$L.NaN", StringUtils.capitalize(type.toString()));

        code.nextControlFlow("else if ($L.equals($S))", stringVar, "Infinity");
        lhs.assign(code, "$L.POSITIVE_INFINITY", StringUtils.capitalize(type.toString()));

        code.nextControlFlow("else if ($L.equals($S))", stringVar, "-Infinity");
        lhs.assign(code, "$L.NEGATIVE_INFINITY", StringUtils.capitalize(type.toString()));

        code.nextControlFlow("else");
        code.addStatement("throw new $T()", IOException.class);
        code.endControlFlow();
    }

    private String readStringInstead() {
        String stringVar = "string$" + (stackDepth() + 1);
        SELF nested = nest(utils.commonTypes.string, null, new LHS.Variable(stringVar), false);
        code.addStatement("$T $L", nested.type.getTypeMirror(), stringVar);
        nested.readString(StringKind.STRING);
        return stringVar;
    }

    protected void readNullCheckedObject() {
        if (utils.isBoxed(type.getTypeMirror())) {
            nest(utils.types.unboxedType(type.getTypeMirror()), null, lhs, false)
                    .build(Branch.ELSE_IF);
        } else if (type.isString() || AnnotationProcessorUtils.isArrayOf(type, TypeKind.CHAR)) {
            readString(Branch.ELSE_IF, type.isString() ? StringKind.STRING : StringKind.CHAR_ARRAY);
        } else if (type.isEnumType()) {
            readEnum(Branch.ELSE_IF);
        } else if (type.isArrayType()) {
            readArray(Branch.ELSE_IF);
        } else if (type.isIterableType()) {
            readCollection(Branch.ELSE_IF);
        } else if (type.isMapType()) {
            readMap(Branch.ELSE_IF);
        } else {
            readObject(Branch.ELSE_IF);
        }
    }

    private void readString(Branch branch, StringKind stringKind) {
        startStringCase(branch);
        readString(stringKind);
        code.nextControlFlow("else");
        throwUnexpected("string");
        code.endControlFlow();
    }

    private void readEnum(Branch branch) {
        startStringCase(branch);
        {
            String enumValuesField = generatedClass.getOrCreateEnumField(type.getTypeMirror());
            LHS.Variable enumVar = new LHS.Variable(propertyName() + "$" + stackDepth() + "$string");
            code.addStatement("$T $L", utils.commonTypes.string, enumVar.name);
            nest(utils.commonTypes.string, null, enumVar, false).readString(StringKind.STRING);
            code.beginControlFlow("if ($L.containsKey($L))", enumValuesField, enumVar.name);
            lhs.assign(code, "$L.get($L)", enumValuesField, enumVar.name);
            code.nextControlFlow("else");
            throwUnexpected("enum value");
            code.endControlFlow();
        }
        code.nextControlFlow("else");
        throwUnexpected("string");
        code.endControlFlow();
    }

    private void readArray(Branch branch) {
        Type componentType = type.getComponentType();
        if (componentType.getTypeMirror().getKind() == TypeKind.BYTE) {
            startStringCase(Branch.ELSE_IF);
            String stringVar = readStringInstead();
            lhs.assign(code, "$T.getDecoder().decode($L)", Base64.class, stringVar);
        } else {
            startArrayCase(branch);
            TypeMirror rawComponentType = componentType.asRawType().getTypeMirror();
            TypeMirror rawRawComponentType =
                    rawComponentType.getKind().isPrimitive() ? rawComponentType : utils.commonTypes.object;
            String varName;
            code.add("// Like ArrayList\n");
            varName = "array$" + stackDepth();
            code.addStatement(
                    "$T[] $L = $T.EMPTY_$L_ARRAY",
                    rawRawComponentType,
                    varName,
                    EmptyArrays.class,
                    rawRawComponentType.getKind().isPrimitive()
                            ? rawRawComponentType.toString().toUpperCase()
                            : "OBJECT");
            String len = "len$" + stackDepth();
            code.addStatement("int $L = 0", len);
            iterateOverElements();
            {
                code.beginControlFlow("if ($L == $L.length)", len, varName);
                code.add("// simplified version of ArrayList growth\n");
                code.addStatement(
                        "$L = $T.copyOf($L, $T.max(10, $L.length + ($L.length >> 1)))",
                        varName,
                        java.util.Arrays.class,
                        varName,
                        Math.class,
                        varName,
                        varName);
                code.endControlFlow();

                SELF nested = nest(componentType.getTypeMirror(), "item", new LHS.Array(varName, len), true);
                nested.build(Branch.IF);
            }
            code.endControlFlow(); // end of loop
            afterArray();
            if (utils.types.isSameType(rawRawComponentType, rawComponentType)) {
                lhs.assign(code, "$T.copyOf($L, $L)", java.util.Arrays.class, varName, len);
            } else {
                lhs.assign(
                        code, "$T.copyOf($L, $L, $T[].class)", java.util.Arrays.class, varName, len, rawComponentType);
            }
        }
        code.nextControlFlow("else");
        {
            throwUnexpected("array");
        }
        code.endControlFlow();
    }

    private void readCollection(Branch branch) {
        startArrayCase(branch);
        {
            Type componentType =
                    type.determineTypeArguments(Iterable.class).get(0).getTypeBound();
            TypeMirror collectionType = determineCollectionType();
            String varName = instantiateContainer(collectionType);

            iterateOverElements();
            {
                SELF nested = nest(componentType.getTypeMirror(), "item", new LHS.Collection(varName), true);
                nested.build(Branch.IF);
            }
            code.endControlFlow(); // end of loop
            afterArray();
            lhs.assign(code, "$L", varName);
        }
        code.nextControlFlow("else");
        throwUnexpected("array");
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

    private String instantiateContainer(TypeMirror collectionType) {
        String varName;
        if (lhs instanceof LHS.Variable v) {
            varName = v.name();
            code.addStatement("$L = new $T<>()", varName, collectionType);
        } else {
            varName = "container$" + stackDepth();
            code.addStatement("$T $L = new $T<>()", type.getTypeMirror(), varName, collectionType);
        }
        return varName;
    }

    private void readMap(Branch branch) {
        Snippet cond = objectCaseCondition();
        branch.controlFlow(code, cond.format(), flatten(cond.args()));
        {
            Type keyType = type.determineTypeArguments(Map.class).get(0).getTypeBound();
            if (!utils.types.isSameType(keyType.getTypeMirror(), utils.commonTypes.string)) {
                throw new AssertionError("Only String keys supported for now. " + stack());
            }
            Type valueType = type.determineTypeArguments(Map.class).get(1).getTypeBound();
            TypeMirror mapType = determineMapType();
            String varName = instantiateContainer(mapType);
            iterateOverFields();
            {
                startFieldCase(Branch.IF);
                String keyVar = "key$" + stackDepth();
                readFieldNameInIteration(keyVar);
                nest(valueType.getTypeMirror(), "value", new LHS.Map(varName, keyVar), true)
                        .build(Branch.IF);
                code.nextControlFlow("else");
                throwUnexpected("field name");
                code.endControlFlow();
            }
            code.endControlFlow(); // end of loop
            afterObject();

            lhs.assign(code, "$L", varName);
        }
        code.nextControlFlow("else");
        throwUnexpected("object");
        code.endControlFlow();
    }

    private TypeMirror determineMapType() {
        if (!type.asRawType().isAbstract()) {
            return type.asRawType().getTypeMirror();
        } else {
            return utils.tf.getType(LinkedHashMap.class).asRawType().getTypeMirror();
        }
    }

    private void readObject(Branch branch) {
        Snippet cond = objectCaseCondition();
        if (canBePolyChild) {
            branch.controlFlow(
                    code,
                    "$L.isObjectOpen(true) || " + cond.format(),
                    flatten(prototype.contextParameter().get(), cond.args()));
        } else {
            branch.controlFlow(code, cond.format(), cond.args());
        }

        Optional<Polymorphism> polymorphismMaybe =
                Optional.ofNullable(type.getTypeElement()).flatMap(t -> Polymorphism.of(t, utils.elements));
        if (polymorphismMaybe.isPresent()) {
            readPolymorphicObject(polymorphismMaybe.get());
        } else {
            readObjectFields();
        }

        code.nextControlFlow("else");
        throwUnexpected("object");
        code.endControlFlow();
    }

    private void readPolymorphicObject(Polymorphism polymorphism) {
        LHS.Variable discriminator = new LHS.Variable("discriminator$" + stackDepth());
        code.addStatement("$T $L = null", utils.commonTypes.string, discriminator.name());
        nest(utils.commonTypes.string, polymorphism.discriminator(), discriminator, false)
                .readDiscriminator(polymorphism.discriminator());
        Branch branch = Branch.IF;
        for (Polymorphism.Child child : polymorphism.children()) {
            branch.controlFlow(code, "$L.equals($S)", discriminator.name(), child.name());
            utils.prototypeFinder
                    .findPrototype(utils.tf.getType(child.type()), prototype, false, true)
                    .ifPresentOrElse(
                            delegate -> {
                                String delegateField = generatedClass.getOrCreateDelegateeField(
                                        prototype.blueprint(), delegate.blueprint());
                                if (delegate.prototype().contextParameter().isEmpty()) {
                                    throw new IllegalArgumentException("Delegate method must have a context parameter");
                                }
                                prototype
                                        .contextParameter()
                                        .ifPresentOrElse(ctx -> code.addStatement("$L.markObjectOpen()", ctx), () -> {
                                            throw new IllegalArgumentException(
                                                    "Prototype method must have a context parameter");
                                        });
                                nest(child.type(), "instance", lhs, true)
                                        .invokeDelegate(delegateField, delegate.method());
                            },
                            () -> {
                                nest(child.type(), "instance", lhs, true).readObjectFields();
                            });
            branch = Branch.ELSE_IF;
        }
        if (branch == Branch.IF) {
            throw new AssertionError("No children for " + type);
        }
        code.nextControlFlow("else");
        code.addStatement("throw new $T($S + $L)", IOException.class, "Unknown type ", discriminator.name());
        code.endControlFlow(); // ends the loop
    }

    void readObjectFields() {
        List<SELF> properties = findProperties();
        for (SELF nest : properties) {
            if (nest.lhs instanceof LHS.Variable v) {
                code.addStatement("$T $L = $L", nest.type.getTypeMirror(), v.name, nest.type.getNull());
            }
        }
        iterateOverFields();
        startFieldCase(Branch.IF);
        String fieldVar = "field$" + stackDepth();
        readFieldNameInIteration(fieldVar);
        code.beginControlFlow("switch($L)", fieldVar);
        for (SELF nest : properties) {
            code.beginControlFlow("case $S:", nest.property);
            nest.build(Branch.IF);
            code.addStatement("break");
            code.endControlFlow();
        }
        // unknown fields are ignored for now
        code.endControlFlow(); // ends the last field
        code.nextControlFlow("else");
        throwUnexpected("field name");
        code.endControlFlow();
        code.endControlFlow(); // ends the loop
        afterObject();
        if (type.isRecord()) {
            lhs.assign(
                    code,
                    "new $T($L)",
                    type.getTypeMirror(),
                    properties.stream().map(p -> ((LHS.Variable) p.lhs).name()).collect(Collectors.joining(", ")));
        } else {
            lhs.assign(code, "$L", "object$" + stackDepth());
        }
    }

    private List<SELF> findProperties() {
        List<SELF> nested = new ArrayList<>();
        if (type.isRecord()) {
            Map<TypeVar, TypeMirror> typeBindings =
                    utils.generics.recordTypeBindings((DeclaredType) type.getTypeMirror());
            for (Element component : type.getRecordComponents()) {
                TypeMirror instantiatedComponentType =
                        utils.generics.applyTypeBindings(component.asType(), typeBindings);
                String varName = component.getSimpleName().toString() + "$" + (stackDepth() + 1);
                SELF nest = nest(
                        instantiatedComponentType,
                        component.getSimpleName().toString(),
                        new LHS.Variable(varName),
                        true);
                nested.add(nest);
            }
        } else {
            String objectVar = "object$" + stackDepth();
            code.addStatement("$T $L = new $T()", type.getTypeMirror(), objectVar, type.getTypeMirror());
            type.getPropertyWriteAccessors(CollectionMappingStrategyGem.SETTER_PREFERRED)
                    .forEach((p, a) -> {
                        LHS lhs =
                                switch (a.getAccessorType()) {
                                    case FIELD -> new LHS.Field(
                                            objectVar,
                                            a.getElement().getSimpleName().toString());
                                    case SETTER -> new LHS.Setter(
                                            objectVar,
                                            a.getElement().getSimpleName().toString());
                                    default -> throw new AssertionError(a.getAccessorType());
                                };
                        SELF nest = nest(a.getAccessedType(), p, lhs, true);
                        nested.add(nest);
                    });
        }
        return nested;
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

    protected abstract void afterObject();

    protected abstract void readDiscriminator(String propertyName);

    protected abstract void iterateOverElements();

    protected abstract void afterArray();

    protected abstract void throwUnexpected(String expected);

    protected abstract void invokeDelegate(String instance, InstantiatedMethod callee);

    protected abstract SELF nest(TypeMirror type, @Nullable String propertyName, LHS lhs, boolean stackRelevantType);

    sealed interface LHS {
        default void assign(CodeBlock.Builder code, Snippet s) {
            assign(code, s.format(), s.args());
        }

        default void assign(CodeBlock.Builder code, String string, Object... args) {
            if (this instanceof Return) {
                code.addStatement("return " + string, args);
            } else if (this instanceof Variable v) {
                code.addStatement("$L = " + string, flatten(v.name(), args));
            } else if (this instanceof Array a) {
                code.addStatement("$L[$L++] = " + string, flatten(a.arrayVar(), a.indexVar(), args));
            } else if (this instanceof Collection c) {
                code.addStatement("$L.add(" + string + ")", flatten(c.variable(), args));
            } else if (this instanceof Map m) {
                code.addStatement("$L.put($L, " + string + ")", flatten(m.mapVar(), m.keyVar(), args));
            } else if (this instanceof Field f) {
                code.addStatement("$L.$L = " + string, flatten(f.objectVar(), f.fieldName(), args));
            } else if (this instanceof Setter s) {
                code.addStatement("$L.$L(" + string + ")", flatten(s.objectVar(), s.methodName(), args));
            } else {
                throw new AssertionError(this);
            }
        }

        record Return() implements LHS {}

        record Variable(String name) implements LHS {}

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

        CodeBlock.Builder controlFlow(CodeBlock.Builder code, String s, Object... args) {
            return switch (this) {
                case IF -> code.beginControlFlow("if (" + s + ")", args);
                case ELSE_IF -> code.nextControlFlow("else if (" + s + ")", args);
            };
        }
    }
}
