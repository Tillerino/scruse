package org.tillerino.scruse.processor.features;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.tillerino.scruse.processor.*;
import org.tillerino.scruse.processor.Snippet.TypedSnippet;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.config.ConfigProperty.LocationKind;
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

    public Optional<TypedSnippet> findOutputConverter(
            TypedSnippet toConvert, ScrusePrototype prototype, AnyConfig config, GeneratedClass generatedClass) {
        Map<TypeVar, TypeMirror> typeBindings = new LinkedHashMap<>();
        return declaredMethodsFromSelfAndUsed(prototype.blueprint(), config)
                .flatMap(method -> {
                    typeBindings.clear();
                    if (isOutputConverter(method.element())
                            && utils.generics.tybeBindingsSatisfyingEquality(
                                    toConvert.type(), method.parameters().get(0).type(), typeBindings)) {
                        InstantiatedMethod instantiatedMethod = utils.generics.applyTypeBindings(method, typeBindings);
                        return Stream.of(TypedSnippet.of(
                                instantiatedMethod.returnType(),
                                "$C($C$C)",
                                method.callSymbol(utils),
                                toConvert,
                                Snippet.joinPrependingCommaToEach(
                                        utils.delegation.findArguments(prototype, method, 1, generatedClass))));
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

    public Optional<TypedSnippet> findJsonValueMethod(TypedSnippet toConvert) {
        if (!(toConvert.type() instanceof DeclaredType dt)) {
            return Optional.empty();
        }
        Map<TypeVar, TypeMirror> typeBindings = utils.generics.recordTypeBindings(dt);
        for (ExecutableElement method : ElementFilter.methodsIn(dt.asElement().getEnclosedElements())) {
            if (utils.annotations
                            .findAnnotation(method, "com.fasterxml.jackson.annotation.JsonValue")
                            .isEmpty()
                    || !method.getParameters().isEmpty()
                    || method.getReturnType().getKind() == TypeKind.VOID) {
                continue;
            }
            InstantiatedMethod instantiatedMethod =
                    utils.generics.instantiateMethod(method, typeBindings, LocationKind.BLUEPRINT);
            return Optional.of(TypedSnippet.of(
                    instantiatedMethod.returnType(), Snippet.of("$C.$L()", toConvert, instantiatedMethod.name())));
        }
        return Optional.empty();
    }
}
