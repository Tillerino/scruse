package org.tillerino.scruse.processor.features;

import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseBlueprint;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.config.ConfigProperty;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

import java.util.Optional;

public record Delegation(AnnotationProcessorUtils utils) {
    public Optional<InstantiatedPrototype> findPrototype(
            Type type,
            ScrusePrototype signatureReference,
            boolean allowRecursion,
            boolean allowExact,
            AnyConfig config) {
        ScruseBlueprint blueprint = signatureReference.blueprint();
        for (ScrusePrototype method : blueprint.prototypes) {
            if (method.config()
                            .resolveProperty(ConfigProperty.DELEGATEE)
                            .value()
                            .canBeDelegatedTo()
                    && (method != signatureReference || allowRecursion)) {
                InstantiatedMethod match = method.matches(signatureReference, type, allowExact);
                if (match != null) {
                    return Optional.of(new InstantiatedPrototype(blueprint, method, match));
                }
            }
        }
        for (ScruseBlueprint use : config.reversedUses()) {
            for (ScrusePrototype method : use.prototypes) {
                if (method.config()
                        .resolveProperty(ConfigProperty.DELEGATEE)
                        .value()
                        .canBeDelegatedTo()) {
                    InstantiatedMethod match = method.matches(signatureReference, type, allowExact);
                    if (match != null) {
                        return Optional.of(new InstantiatedPrototype(use, method, match));
                    }
                }
            }
        }
        return Optional.empty();
    }

    public record InstantiatedPrototype(ScruseBlueprint blueprint, ScrusePrototype prototype, InstantiatedMethod method) {}
}
