package org.tillerino.scruse.processor;

import java.util.*;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName;
import org.tillerino.scruse.processor.util.Config;
import org.tillerino.scruse.processor.util.Generics.TypeVar;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

/** Accessor object for the interface which contains Scruse methods. */
public final class ScruseBlueprint {
    public final FullyQualifiedClassName className;
    public final TypeElement typeElement;
    public final List<ScrusePrototype> prototypes = new ArrayList<>();
    public final List<InstantiatedMethod> declaredMethods;
    public final Config config;
    public final Map<TypeVar, TypeMirror> typeBindings;

    private ScruseBlueprint(
            FullyQualifiedClassName className,
            TypeElement typeElement,
            List<InstantiatedMethod> declaredMethods,
            Config config,
            Map<TypeVar, TypeMirror> typeBindings) {
        this.className = className;
        this.typeElement = typeElement;
        this.declaredMethods = declaredMethods;
        this.config = config;
        this.typeBindings = typeBindings;
    }

    static ScruseBlueprint of(TypeElement element, AnnotationProcessorUtils utils) {
        Map<TypeVar, TypeMirror> typeBindings = new LinkedHashMap<>();
        for (TypeMirror directSupertype : utils.types.directSupertypes(element.asType())) {
            if (directSupertype.toString().equals(Object.class.getName())
                    || !(directSupertype instanceof DeclaredType dt)) {
                continue;
            }
            typeBindings.putAll(utils.generics.recordTypeBindings(dt));
        }

        List<InstantiatedMethod> declaredMethods = ElementFilter.methodsIn(element.getEnclosedElements()).stream()
                .map(method -> utils.generics.instantiateMethod(method, typeBindings))
                .toList();

        return new ScruseBlueprint(
                FullyQualifiedClassName.of(element),
                element,
                declaredMethods,
                Config.defaultConfig(element, utils).merge(null),
                typeBindings);
    }

    /**
     * Returns the used blueprints in the reverse order they are collected. This way earlier matches are more specific -
     * i.e. closer to the location where the blueprint is used.
     */
    public List<ScruseBlueprint> reversedUses() {
        List<ScruseBlueprint> uses = new ArrayList<>(config.uses());
        Collections.reverse(uses);
        return uses;
    }

    @Override
    public String toString() {
        return className.importName();
    }
}
