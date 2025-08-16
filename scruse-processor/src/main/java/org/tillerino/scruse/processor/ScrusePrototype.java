package org.tillerino.scruse.processor;

import java.util.*;
import java.util.stream.Collectors;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.*;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.util.*;
import org.tillerino.scruse.processor.util.Generics.TypeVar;

/**
 * Accessor object for a method which is annotated with {@link org.tillerino.scruse.annotations.JsonInput} or
 * {@link org.tillerino.scruse.annotations.JsonOutput}.
 */
public record ScrusePrototype(
        ScruseBlueprint blueprint,
        String name,
        ExecutableElement methodElement,
        PrototypeKind kind,
        AnnotationProcessorUtils utils,
        TypeMirror instantiatedReturnType,
        List<InstantiatedVariable> instantiatedParameters,
        AnyConfig config) {

    static ScrusePrototype of(
            ScruseBlueprint blueprint,
            InstantiatedMethod instantiated,
            PrototypeKind kind,
            AnnotationProcessorUtils utils) {
        AnyConfig config = AnyConfig.create(instantiated.element(), ConfigProperty.LocationKind.PROTOTYPE, utils)
                .merge(blueprint.config);
        if (kind.javaType() instanceof DeclaredType d) {
            config = AnyConfig.create(d.asElement(), ConfigProperty.LocationKind.DTO, utils)
                    .merge(config);
        }

        return new ScrusePrototype(
                blueprint,
                instantiated.name(),
                instantiated.element(),
                kind,
                utils,
                instantiated.returnType(),
                instantiated.parameters(),
                config);
    }

    /** Checks if reads/writes the given type and matches the signature of a reference method. */
    public InstantiatedMethod matches(ScrusePrototype referenceSignature, Type targetType, boolean allowExact) {
        if (kind.direction() != referenceSignature.kind().direction()
                || !utils.types.isSameType(
                        kind().jsonType(), referenceSignature.kind().jsonType())) {
            return null;
        }

        LinkedHashSet<TypeVar> localTypeVars = methodElement.getTypeParameters().stream()
                .map(TypeParameterElement::asType)
                .map(TypeVariable.class::cast)
                .map(TypeVar::of)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashMap<TypeVar, TypeMirror> typeBindings = new LinkedHashMap<>();

        if (isSameTypeWithBindings(kind().javaType(), targetType.getTypeMirror(), localTypeVars, typeBindings)) {
            if (!allowExact && typeBindings.isEmpty()) {
                return null;
            }
            return utils.generics.applyTypeBindings(this.asInstantiatedMethod(), typeBindings);
        }
        return null;
    }

    private static boolean isSameTypeWithBindings(
            TypeMirror javaType,
            TypeMirror targetType,
            LinkedHashSet<TypeVar> localTypeVars,
            LinkedHashMap<TypeVar, TypeMirror> typeBindings) {
        if (javaType instanceof TypeVariable t) {
            TypeVar typeVar = TypeVar.of(t);
            if (typeBindings.containsKey(typeVar)) {
                return typeBindings.get(typeVar).equals(targetType);
            }
            if (localTypeVars.contains(typeVar)) {
                typeBindings.put(typeVar, targetType);
                return true;
            }
            return false;
        }
        if (javaType instanceof DeclaredType t) {
            if (!(targetType instanceof DeclaredType tt) || !t.asElement().equals(tt.asElement())) {
                return false;
            }
            for (int i = 0; i < t.getTypeArguments().size(); i++) {
                TypeMirror type = t.getTypeArguments().get(i);
                TypeMirror targetTypeArg = tt.getTypeArguments().get(i);
                if (!isSameTypeWithBindings(type, targetTypeArg, localTypeVars, typeBindings)) {
                    return false;
                }
            }
            return true;
        }
        if (javaType instanceof PrimitiveType p && targetType instanceof PrimitiveType pt) {
            return p.getKind().equals(pt.getKind());
        }
        if (javaType instanceof ArrayType a && targetType instanceof ArrayType at) {
            return isSameTypeWithBindings(a.getComponentType(), at.getComponentType(), localTypeVars, typeBindings);
        }
        return false;
    }

    public Optional<VariableElement> contextParameter() {
        for (VariableElement parameter : methodElement.getParameters()) {
            if (utils.types.isAssignable(
                    parameter.asType(),
                    switch (kind.direction()) {
                        case INPUT -> utils.commonTypes.deserializationContext;
                        case OUTPUT -> utils.commonTypes.serializationContext;
                    })) {
                return Optional.of(parameter);
            }
        }
        return Optional.empty();
    }

    public List<Snippet> findArguments(InstantiatedMethod callee, int firstArgument, GeneratedClass generatedClass) {
        List<Snippet> arguments = new ArrayList<>();
        calleeParameter:
        for (int i = firstArgument; i < callee.parameters().size(); i++) {
            TypeMirror calleeParameterType = callee.parameters().get(i).type();
            // search in caller's own parameters
            for (int j = 0; j < methodElement.getParameters().size(); j++) {
                if (utils.types.isAssignable(
                        methodElement.getParameters().get(j).asType(), calleeParameterType)) {
                    arguments.add(Snippet.of("$L", methodElement.getParameters().get(j)));
                    continue calleeParameter;
                }
            }
            // see if we can instantiate an instance from our list of used blueprints
            String delegateeInField = generatedClass.getOrCreateUsedBlueprintWithTypeField(calleeParameterType, config);
            if (delegateeInField != null) {
                arguments.add(Snippet.of("$L", delegateeInField));
                continue calleeParameter;
            }
            // see if we can instantiate a lambda from our list of used blueprints
            Pair<String, String> lambda = generatedClass.getOrCreateLambda(calleeParameterType);
            if (lambda != null) {
                arguments.add(Snippet.of("$L::$L", lambda.getLeft(), lambda.getRight()));
                continue calleeParameter;
            }
            throw new ContextedRuntimeException("Could not find value to pass to method argument")
                    .addContextValue("caller", this.methodElement)
                    .addContextValue("callee", callee)
                    .addContextValue("argument", callee.parameters().get(i));
        }
        return arguments;
    }

    @Override
    public String toString() {
        return blueprint + "." + name
                + instantiatedParameters().stream()
                        .map(InstantiatedVariable::toString)
                        .collect(Collectors.joining(", ", "(", ")"));
    }

    public InstantiatedMethod asInstantiatedMethod() {
        return new InstantiatedMethod(name, instantiatedReturnType, instantiatedParameters, methodElement);
    }

    @Override
    public int hashCode() {
        throw new NotImplementedException("hashCode");
    }

    @Override
    public boolean equals(Object obj) {
        throw new NotImplementedException("equals");
    }
}
