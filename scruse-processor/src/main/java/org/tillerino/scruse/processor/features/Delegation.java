package org.tillerino.scruse.processor.features;

import java.util.Optional;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseBlueprint;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.config.ConfigProperty;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public record Delegation(AnnotationProcessorUtils utils) {
    public Optional<InstantiatedPrototype> findPrototype(
            Type type, ScrusePrototype caller, boolean allowRecursion, boolean allowExact, AnyConfig config) {
        ScruseBlueprint blueprint = caller.blueprint();
        for (ScrusePrototype callee : blueprint.prototypes) {
            if (callee.config()
                            .resolveProperty(ConfigProperty.DELEGATEE)
                            .value()
                            .canBeDelegatedTo()
                    && (callee != caller || allowRecursion)) {
                InstantiatedMethod match = callee.matches(caller, type, allowExact);
                if (match != null) {
                    return Optional.of(new InstantiatedPrototype(blueprint, callee, match));
                }
            }
        }
        for (ScruseBlueprint use : config.reversedUses()) {
            for (ScrusePrototype callee : use.prototypes) {
                if (callee.config()
                        .resolveProperty(ConfigProperty.DELEGATEE)
                        .value()
                        .canBeDelegatedTo()) {
                    InstantiatedMethod match = callee.matches(caller, type, allowExact);
                    if (match != null) {
                        return Optional.of(new InstantiatedPrototype(use, callee, match));
                    }
                }
            }
        }
        return Optional.empty();
    }

    public record InstantiatedPrototype(
            ScruseBlueprint blueprint, ScrusePrototype prototype, InstantiatedMethod method) {}
}
