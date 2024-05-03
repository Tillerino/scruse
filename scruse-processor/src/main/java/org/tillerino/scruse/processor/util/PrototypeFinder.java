package org.tillerino.scruse.processor.util;

import java.util.*;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseBlueprint;
import org.tillerino.scruse.processor.ScrusePrototype;

public record PrototypeFinder(AnnotationProcessorUtils utils) {
    public Optional<Prototype> findPrototype(
            Type type, ScrusePrototype signatureReference, boolean allowRecursion, boolean allowExact) {
        ScruseBlueprint blueprint = signatureReference.blueprint();
        for (ScrusePrototype method : blueprint.prototypes) {
            if (method.config().delegateTo().canBeDelegatedTo() && (method != signatureReference || allowRecursion)) {
                InstantiatedMethod match = method.matches(signatureReference, type, allowExact);
                if (match != null) {
                    return Optional.of(new Prototype(blueprint, method, match));
                }
            }
        }
        for (ScruseBlueprint use : blueprint.reversedUses()) {
            for (ScrusePrototype method : use.prototypes) {
                if (method.config().delegateTo().canBeDelegatedTo()) {
                    InstantiatedMethod match = method.matches(signatureReference, type, allowExact);
                    if (match != null) {
                        return Optional.of(new Prototype(use, method, match));
                    }
                }
            }
        }
        return Optional.empty();
    }

    public record Prototype(ScruseBlueprint blueprint, ScrusePrototype prototype, InstantiatedMethod method) {}
}
