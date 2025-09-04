package org.tillerino.scruse.processor.features;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.*;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.config.ConfigProperty;
import org.tillerino.scruse.processor.config.ConfigProperty.LocationKind;
import org.tillerino.scruse.processor.util.InstantiatedMethod;
import org.tillerino.scruse.processor.util.InstantiatedMethod.InstantiatedVariable;
import org.tillerino.scruse.processor.util.PrototypeKind;
import org.tillerino.scruse.processor.util.ShortName;

public record Delegation(AnnotationProcessorUtils utils) {
    public Optional<Delegatee> findDelegatee(
            Type type,
            ScrusePrototype caller,
            boolean allowRecursion,
            boolean allowExact,
            AnyConfig config,
            GeneratedClass generatedClass) {
        return findPrototype(type, caller, allowRecursion, allowExact, config)
                .map(d -> new Delegatee(
                        generatedClass.getOrCreateDelegateeField(
                                caller.blueprint(),
                                d.blueprint(),
                                !d.prototype().overrides()),
                        d.method()))
                .or(() -> utils.delegation.findDelegateeInMethodParameters(caller, type));
    }

    private Optional<InstantiatedPrototype> findPrototype(
            Type type, ScrusePrototype caller, boolean allowRecursion, boolean allowExact, AnyConfig config) {
        ScruseBlueprint blueprint = caller.blueprint();
        for (ScrusePrototype callee : blueprint.prototypes) {
            if (canBeDelegatedTo(callee) && (callee != caller || allowRecursion)) {
                InstantiatedMethod match = callee.matches(caller, type, allowExact);
                if (match != null) {
                    return Optional.of(new InstantiatedPrototype(blueprint, callee, match));
                }
            }
        }
        for (ScruseBlueprint use : config.reversedUses()) {
            for (ScrusePrototype callee : use.prototypes) {
                if (canBeDelegatedTo(callee)) {
                    InstantiatedMethod match = callee.matches(caller, type, allowExact);
                    if (match != null) {
                        return Optional.of(new InstantiatedPrototype(use, callee, match));
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static boolean canBeDelegatedTo(ScrusePrototype callee) {
        return callee.config().resolveProperty(ConfigProperty.DELEGATEE).value().canBeDelegatedTo();
    }

    private Optional<Delegatee> findDelegateeInMethodParameters(ScrusePrototype prototype, Type type) {
        for (InstantiatedVariable parameter : prototype.kind().otherParameters()) {
            for (InstantiatedMethod method :
                    utils.generics.instantiateMethods(parameter.type(), LocationKind.PROTOTYPE)) {
                Optional<PrototypeKind> prototypeKind = PrototypeKind.of(method, utils)
                        .filter(kind -> kind.matchesWithJavaType(prototype.kind(), type.getTypeMirror(), utils));
                if (prototypeKind.isPresent()) {
                    return Optional.of(new Delegatee(parameter.name(), method));
                }
            }
        }
        return Optional.empty();
    }

    public List<Snippet> findArguments(
            ScrusePrototype caller, InstantiatedMethod callee, int firstArgument, GeneratedClass generatedClass) {
        return IntStream.range(firstArgument, callee.parameters().size())
                .mapToObj(i -> {
                    InstantiatedVariable targetParameter = callee.parameters().get(i);
                    return findArgument(caller, generatedClass, targetParameter)
                            .orElseThrow(() -> new ContextedRuntimeException(
                                            ("Could not find a value of type %s to pass in method call. Consider declaring a parameter of this type on the caller.")
                                                    .formatted(ShortName.of(targetParameter.type())))
                                    .addContextValue("parameter", targetParameter)
                                    .addContextValue("callee", callee)
                                    .addContextValue("caller", caller.asInstantiatedMethod()));
                })
                .collect(Collectors.toList());
    }

    private Optional<Snippet> findArgument(
            ScrusePrototype caller, GeneratedClass generatedClass, InstantiatedVariable targetArgument) {
        // search in caller's own parameters
        for (InstantiatedVariable instantiatedParameter : caller.instantiatedParameters()) {
            if (utils.types.isAssignable(instantiatedParameter.type(), targetArgument.type())) {
                return Optional.of(Snippet.of("$L", instantiatedParameter.name()));
            }
        }
        // see if we can instantiate an instance from our list of used blueprints
        String delegateeInField =
                generatedClass.getOrCreateUsedBlueprintWithTypeField(targetArgument.type(), caller.config());
        if (delegateeInField != null) {
            return Optional.of(Snippet.of("$L", delegateeInField));
        }
        if (targetArgument.type() instanceof DeclaredType t
                && t.asElement().equals(utils.commonTypes.classElement)
                && !t.getTypeArguments().isEmpty()) {
            TypeMirror typeOfClass = t.getTypeArguments().get(0);
            if (Generics.canBeClass(typeOfClass)) {
                return Optional.of(Snippet.of("$T.class", typeOfClass));
            }
        }
        // see if we can instantiate a lambda from our list of used blueprints
        return utils.generics.getOrCreateLambda(
                generatedClass, targetArgument.type(), caller.instantiatedParameters(), 0);
    }

    public record Delegatee(String fieldOrParameter, InstantiatedMethod method) {}

    public record InstantiatedPrototype(
            ScruseBlueprint blueprint, ScrusePrototype prototype, InstantiatedMethod method) {}
}
