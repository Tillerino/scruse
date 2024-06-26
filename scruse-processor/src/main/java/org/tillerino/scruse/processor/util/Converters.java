package org.tillerino.scruse.processor.util;

import java.util.*;
import java.util.stream.Stream;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseBlueprint;

public record Converters(AnnotationProcessorUtils utils) {
    public Optional<InstantiatedMethod> findInputConverter(ScruseBlueprint blueprint, TypeMirror targetType) {
        Map<Generics.TypeVar, TypeMirror> typeBindings = new LinkedHashMap<>();
        return declaredMethodsFromSelfAndUsed(blueprint)
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

    public Optional<InstantiatedMethod> findOutputConverter(ScruseBlueprint blueprint, TypeMirror targetType) {
        Map<Generics.TypeVar, TypeMirror> typeBindings = new LinkedHashMap<>();
        return declaredMethodsFromSelfAndUsed(blueprint)
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

    public Optional<InstantiatedMethod> findInputDefaultValue(ScruseBlueprint blueprint, TypeMirror targetType) {
        Map<Generics.TypeVar, TypeMirror> typeBindings = new LinkedHashMap<>();
        return declaredMethodsFromSelfAndUsed(blueprint)
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

    public boolean isInputDefaultValue(ExecutableElement methodElement) {
        return methodElement.getModifiers().contains(Modifier.STATIC)
                && utils.annotations
                        .findAnnotation(methodElement, "org.tillerino.scruse.annotations.JsonInputDefaultValue")
                        .isPresent()
                && methodElement.getParameters().isEmpty();
    }

    private static Stream<InstantiatedMethod> declaredMethodsFromSelfAndUsed(ScruseBlueprint blueprint) {
        return Stream.concat(
                blueprint.declaredMethods.stream(),
                blueprint.reversedUses().stream().flatMap(use -> use.declaredMethods.stream()));
    }
}
