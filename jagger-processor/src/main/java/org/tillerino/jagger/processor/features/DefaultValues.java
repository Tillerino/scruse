package org.tillerino.jagger.processor.features;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import org.tillerino.jagger.processor.AnnotationProcessorUtils;
import org.tillerino.jagger.processor.JaggerBlueprint;
import org.tillerino.jagger.processor.config.AnyConfig;
import org.tillerino.jagger.processor.features.Generics.TypeVar;
import org.tillerino.jagger.processor.util.InstantiatedMethod;

public record DefaultValues(AnnotationProcessorUtils utils) {

    public Optional<InstantiatedMethod> findInputDefaultValue(
            JaggerBlueprint blueprint, TypeMirror targetType, AnyConfig config) {
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
                        .findAnnotation(methodElement, "org.tillerino.jagger.annotations.JsonInputDefaultValue")
                        .isPresent()
                && methodElement.getParameters().isEmpty();
    }
}
