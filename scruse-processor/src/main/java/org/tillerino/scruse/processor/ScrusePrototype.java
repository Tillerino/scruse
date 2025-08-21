package org.tillerino.scruse.processor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.*;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.config.ConfigProperty;
import org.tillerino.scruse.processor.features.Generics.TypeVar;
import org.tillerino.scruse.processor.util.InstantiatedMethod;
import org.tillerino.scruse.processor.util.InstantiatedVariable;
import org.tillerino.scruse.processor.util.PrototypeKind;

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
    public InstantiatedMethod matches(ScrusePrototype caller, Type callerType, boolean allowExact) {
        if (kind.direction() != caller.kind().direction()
                || !utils.types.isSameType(kind().jsonType(), caller.kind().jsonType())) {
            return null;
        }

        LinkedHashSet<TypeVar> localTypeVars = methodElement.getTypeParameters().stream()
                .map(TypeParameterElement::asType)
                .map(TypeVariable.class::cast)
                .map(TypeVar::of)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        LinkedHashMap<TypeVar, TypeMirror> typeBindings = new LinkedHashMap<>();

        if (isSameTypeWithBindings(kind().javaType(), callerType.getTypeMirror(), localTypeVars, typeBindings)) {
            if (!allowExact && typeBindings.isEmpty()) {
                return null;
            }
            return utils.generics.applyTypeBindings(this.asInstantiatedMethod(), typeBindings);
        }
        return null;
    }

    private static boolean isSameTypeWithBindings(
            TypeMirror calleeType,
            TypeMirror callerType,
            LinkedHashSet<TypeVar> calleeTypeVars,
            LinkedHashMap<TypeVar, TypeMirror> calleeBindings) {
        if (calleeType instanceof TypeVariable t) {
            TypeVar typeVar = TypeVar.of(t);
            if (calleeBindings.containsKey(typeVar)) {
                return calleeBindings.get(typeVar).equals(callerType);
            }
            if (calleeTypeVars.contains(typeVar)) {
                if (callerType.getKind().isPrimitive()) {
                    return false;
                }
                calleeBindings.put(typeVar, callerType);
                return true;
            }
            return false;
        }
        if (calleeType instanceof DeclaredType t) {
            if (!(callerType instanceof DeclaredType tt) || !t.asElement().equals(tt.asElement())) {
                return false;
            }
            if (t.getTypeArguments().isEmpty() || tt.getTypeArguments().isEmpty()) {
                // if either is raw
                return true;
            }
            for (int i = 0; i < t.getTypeArguments().size(); i++) {
                TypeMirror type = t.getTypeArguments().get(i);
                TypeMirror targetTypeArg = tt.getTypeArguments().get(i);
                if (!isSameTypeWithBindings(type, targetTypeArg, calleeTypeVars, calleeBindings)) {
                    return false;
                }
            }
            return true;
        }
        if (calleeType instanceof PrimitiveType p && callerType instanceof PrimitiveType pt) {
            return p.getKind().equals(pt.getKind());
        }
        if (calleeType instanceof ArrayType a && callerType instanceof ArrayType at) {
            return isSameTypeWithBindings(a.getComponentType(), at.getComponentType(), calleeTypeVars, calleeBindings);
        }
        if (calleeType.getKind() == TypeKind.WILDCARD && callerType.getKind() == TypeKind.WILDCARD) {
            return true;
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
        return IntStream.range(firstArgument, callee.parameters().size())
                .mapToObj(i -> {
                    InstantiatedVariable targetArgument = callee.parameters().get(i);
                    return findArgument(generatedClass, targetArgument).orElseThrow(() -> new ContextedRuntimeException(
                                    "Could not find value to pass to method argument")
                            .addContextValue("argument", targetArgument)
                            .addContextValue("callee", callee)
                            .addContextValue("caller", asInstantiatedMethod()));
                })
                .collect(Collectors.toList());
    }

    private Optional<Snippet> findArgument(GeneratedClass generatedClass, InstantiatedVariable targetArgument) {
        // search in caller's own parameters
        for (InstantiatedVariable instantiatedParameter : instantiatedParameters) {
            if (utils.types.isAssignable(instantiatedParameter.type(), targetArgument.type())) {
                return Optional.of(Snippet.of("$L", instantiatedParameter.name()));
            }
        }
        // see if we can instantiate an instance from our list of used blueprints
        String delegateeInField = generatedClass.getOrCreateUsedBlueprintWithTypeField(targetArgument.type(), config);
        if (delegateeInField != null) {
            return Optional.of(Snippet.of("$L", delegateeInField));
        }
        // see if we can instantiate a lambda from our list of used blueprints
        return utils.generics.getOrCreateLambda(generatedClass, targetArgument.type(), instantiatedParameters, 0);
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
