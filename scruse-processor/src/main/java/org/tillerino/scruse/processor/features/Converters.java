package org.tillerino.scruse.processor.features;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseBlueprint;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.features.Generics.TypeVar;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public record Converters(AnnotationProcessorUtils utils) {
    public Optional<InstantiatedMethod> findInputConverter(
            ScruseBlueprint blueprint, TypeMirror targetType, AnyConfig config) {
        Map<TypeVar, TypeMirror> typeBindings = new LinkedHashMap<>();
        return declaredMethodsFromSelfAndUsed(blueprint, config)
                .flatMap(method -> {
                    typeBindings.clear();
                    if (isInputConverter(method.element())
                            && utils.generics.tybeBindingsSatisfyingEquality(
                                    targetType, method.returnType(), typeBindings)) {
                        return Stream.of(utils.generics.applyTypeBindings(method, typeBindings));
                    }
                    return Stream.empty();
                })
                .findFirst();
    }

    public Optional<InstantiatedMethod> findOutputConverter(
            ScruseBlueprint blueprint, TypeMirror targetType, AnyConfig config) {
        Map<TypeVar, TypeMirror> typeBindings = new LinkedHashMap<>();
        return declaredMethodsFromSelfAndUsed(blueprint, config)
                .flatMap(method -> {
                    typeBindings.clear();
                    if (isOutputConverter(method.element())
                            && utils.generics.tybeBindingsSatisfyingEquality(
                                    targetType, method.parameters().get(0).type(), typeBindings)) {
                        return Stream.of(utils.generics.applyTypeBindings(method, typeBindings));
                    }
                    return Stream.empty();
                })
                .findFirst();
    }

    public boolean isInputConverter(ExecutableElement methodElement) {
        return methodElement.getModifiers().contains(Modifier.STATIC)
                && utils.annotations
                        .findAnnotation(methodElement, "org.tillerino.scruse.annotations.JsonInputConverter")
                        .isPresent()
                && !methodElement.getParameters().isEmpty();
    }

    public boolean isOutputConverter(ExecutableElement methodElement) {
        return methodElement.getModifiers().contains(Modifier.STATIC)
                && utils.annotations
                        .findAnnotation(methodElement, "org.tillerino.scruse.annotations.JsonOutputConverter")
                        .isPresent()
                && !methodElement.getParameters().isEmpty();
    }

    static Stream<InstantiatedMethod> declaredMethodsFromSelfAndUsed(ScruseBlueprint blueprint, AnyConfig config) {
        return Stream.concat(
                blueprint.declaredMethods.stream(),
                config.reversedUses().stream().flatMap(use -> use.declaredMethods.stream()));
    }
}
