package org.tillerino.scruse.processor;

import java.util.*;
import java.util.stream.Stream;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.config.ConfigProperty;
import org.tillerino.scruse.processor.config.ConfigProperty.LocationKind;
import org.tillerino.scruse.processor.features.Generics.TypeVar;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

/** Accessor object for the interface which contains Scruse methods. */
public final class ScruseBlueprint {
    public final FullyQualifiedClassName className;
    public final TypeElement typeElement;
    public final List<ScrusePrototype> prototypes = new ArrayList<>();
    public final List<InstantiatedMethod> declaredMethods;
    public final AnyConfig config;
    public final Map<TypeVar, TypeMirror> typeBindings;

    private ScruseBlueprint(
            FullyQualifiedClassName className,
            TypeElement typeElement,
            List<InstantiatedMethod> declaredMethods,
            AnyConfig config,
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
                .map(method -> utils.generics.instantiateMethod(method, typeBindings, LocationKind.PROTOTYPE))
                .toList();

        return new ScruseBlueprint(
                FullyQualifiedClassName.of(element),
                element,
                declaredMethods,
                AnyConfig.create(element, ConfigProperty.LocationKind.BLUEPRINT, utils),
                typeBindings);
    }

    String generatedClassName() {
        return className.fileName().replace("/", ".") + "Impl";
    }

    public Stream<ScruseBlueprint> includeUses() {
        return Stream.concat(config.resolveProperty(ConfigProperty.USES).value().stream(), Stream.of(this));
    }

    @Override
    public String toString() {
        return className.className();
    }
}
