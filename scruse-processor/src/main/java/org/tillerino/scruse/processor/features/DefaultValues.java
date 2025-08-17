package org.tillerino.scruse.processor.features;

import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseBlueprint;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.util.Generics.TypeVar;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public record DefaultValues(AnnotationProcessorUtils utils) {

    public Optional<InstantiatedMethod> findInputDefaultValue(
            ScruseBlueprint blueprint, TypeMirror targetType, AnyConfig config) {
        Map<TypeVar, TypeMirror> typeBindings = new LinkedHashMap<>();
        return Converters.declaredMethodsFromSelfAndUsed(blueprint, config)
                .flatMap(method -> {
                    typeBindings.clear();
                    if (isInputDefaultValue(method.element())
                            && utils.generics.tybeBindingsSatisfyingEquality(
                                    targetType, method.returnType(), typeBindings)) {
                        return Stream.of(utils.generics.applyTypeBindings(method, typeBindings));
                    }
                    return Stream.empty();
                })
                .findFirst();
    }

    public boolean isInputDefaultValue(ExecutableElement methodElement) {
        return methodElement.getModifiers().contains(Modifier.STATIC)
                && utils.annotations
                        .findAnnotation(methodElement, "org.tillerino.scruse.annotations.JsonInputDefaultValue")
                        .isPresent()
                && methodElement.getParameters().isEmpty();
    }
}
