package org.tillerino.scruse.processor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName;
import org.tillerino.scruse.processor.util.Config;
import org.tillerino.scruse.processor.util.Generics.TypeVar;

/** Accessor object for the interface which contains Scruse methods. */
public final class ScruseBlueprint {
    public final FullyQualifiedClassName className;
    public final TypeElement typeElement;
    public final List<ScruseMethod> methods = new ArrayList<>();
    public final Config config;
    public final Map<TypeVar, TypeMirror> typeBindings;

    private ScruseBlueprint(
            FullyQualifiedClassName className,
            TypeElement typeElement,
            Config config,
            Map<TypeVar, TypeMirror> typeBindings) {
        this.className = className;
        this.typeElement = typeElement;
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

        return new ScruseBlueprint(
                FullyQualifiedClassName.of(element),
                element,
                Config.defaultConfig(element, utils).merge(null),
                typeBindings);
    }

    @Override
    public String toString() {
        return className.importName();
    }
}
