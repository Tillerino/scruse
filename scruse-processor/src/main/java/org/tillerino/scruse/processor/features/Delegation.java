package org.tillerino.scruse.processor.features;

import java.util.Optional;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseBlueprint;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.config.ConfigProperty;
import org.tillerino.scruse.processor.util.InstantiatedMethod;
import org.tillerino.scruse.processor.util.InstantiatedVariable;
import org.tillerino.scruse.processor.util.PrototypeKind;

public record Delegation(AnnotationProcessorUtils utils) {
    public Optional<InstantiatedPrototype> findPrototype(
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

    public Optional<Delegatee> findDelegateeInMethodParameters(ScrusePrototype prototype, Type type) {
        for (InstantiatedVariable parameter : prototype.kind().otherParameters()) {
            for (InstantiatedMethod method : utils.generics.instantiateMethods(parameter.type())) {
                Optional<PrototypeKind> prototypeKind = PrototypeKind.of(method, utils)
                        .filter(kind -> kind.matchesWithJavaType(prototype.kind(), type.getTypeMirror(), utils));
                if (prototypeKind.isPresent()) {
                    return Optional.of(new Delegatee(parameter.name(), method));
                }
            }
        }
        return Optional.empty();
    }

    public record Delegatee(String fieldOrParameter, InstantiatedMethod method) {}

    public record InstantiatedPrototype(
            ScruseBlueprint blueprint, ScrusePrototype prototype, InstantiatedMethod method) {}
}
