package org.tillerino.scruse.processor;

import java.util.*;
import javax.lang.model.util.Types;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public record PrototypeFinder(Types types, Map<String, ScruseBlueprint> blueprints) {
    public Optional<Prototype> findPrototype(
            Type type, ScruseMethod signatureReference, boolean allowRecursion, boolean allowExact) {
        ScruseBlueprint blueprint = signatureReference.blueprint();
        for (ScruseMethod method : blueprint.methods) {
            if (method.config().delegateTo().canBeDelegatedTo() && (method != signatureReference || allowRecursion)) {
                InstantiatedMethod match = method.matches(signatureReference, type, allowExact);
                if (match != null) {
                    return Optional.of(new Prototype(blueprint, method, match));
                }
            }
        }
        List<ScruseBlueprint> uses = new ArrayList<>(blueprint.config.uses());
        // reverse for correct precedence
        Collections.reverse(uses);
        for (ScruseBlueprint use : uses) {
            for (ScruseMethod method : use.methods) {
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

    public record Prototype(ScruseBlueprint blueprint, ScruseMethod prototype, InstantiatedMethod method) {}
}
