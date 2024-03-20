package org.tillerino.scruse.processor;

import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName;
import org.tillerino.scruse.processor.util.Generics;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Accessor object for the interface which contains Scruse methods.
 */
record ScruseBlueprint(AtomicBoolean toBeGenerated, FullyQualifiedClassName className, TypeElement typeElement,
		List<ScruseMethod> methods, List<ScruseBlueprint> uses, Map<Generics.TypeVar, TypeMirror> typeBindings) {

	static ScruseBlueprint of(AtomicBoolean toBeGenerated, TypeElement element,
			List<ScruseMethod> methods, List<ScruseBlueprint> uses, AnnotationProcessorUtils utils) {
		Map<Generics.TypeVar, TypeMirror> typeBindings = new LinkedHashMap<>();
		for (TypeMirror directSupertype : utils.types.directSupertypes(element.asType())) {
			if (directSupertype.toString().equals(Object.class.getName()) || !(directSupertype instanceof DeclaredType dt)) {
				continue;
			}
			typeBindings.putAll(utils.generics.recordTypeBindings(dt));
		}

		return new ScruseBlueprint(toBeGenerated, FullyQualifiedClassName.of(element), element, methods, uses, typeBindings);
	}

	@Override
	public String toString() {
		return className.importName();
	}
}
