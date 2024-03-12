package org.tillerino.scruse.processor;

import org.mapstruct.ap.internal.model.common.Type;

import javax.lang.model.util.Types;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record PrototypeFinder(Types types, Map<String, ScruseBlueprint> blueprints)  {
	public Optional<Prototype> findPrototype(Type type, ScruseMethod signatureReference, boolean allowSelfCall) {
		return findPrototype(type, signatureReference, signatureReference.blueprint().className().importName(), new LinkedHashSet<>(), signatureReference.blueprint(), allowSelfCall);
	}

	private Optional<Prototype> findPrototype(Type type, ScruseMethod signatureReference, String root, Set<String> visited, ScruseBlueprint blueprint, boolean allowSelfCall) {
		for (ScruseMethod method : blueprint.methods()) {
			if ((method != signatureReference || allowSelfCall) && method.matches(types, signatureReference, type)) {
				return Optional.of(new Prototype(blueprint, method));
			}
		}
		for (ScruseBlueprint use : blueprint.uses()) {
			if (use.className().importName().equals(root)) {
				throw new StackOverflowError("circular dependency: " + root);
			}
			if (visited.add(use.className().importName())) {
				Optional<Prototype> prototype = findPrototype(type, signatureReference, root, visited, use, allowSelfCall);
				if (prototype.isPresent()) {
					return prototype;
				}
			}
		}
		return Optional.empty();
	}

	public record Prototype(ScruseBlueprint blueprint, ScruseMethod method) {

	}
}
