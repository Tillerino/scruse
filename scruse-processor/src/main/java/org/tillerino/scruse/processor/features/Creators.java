package org.tillerino.scruse.processor.features;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.config.ConfigProperty.LocationKind;
import org.tillerino.scruse.processor.features.Generics.TypeVar;
import org.tillerino.scruse.processor.util.Exceptions;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public record Creators(AnnotationProcessorUtils utils) {

    public Optional<Creator> findJsonCreatorMethod(TypeMirror tm) {
        if (!(tm instanceof DeclaredType dt)) {
            return Optional.empty();
        }
        Map<TypeVar, TypeMirror> typeBindings = utils.generics.recordTypeBindings(dt);
        for (ExecutableElement constructor :
                ElementFilter.constructorsIn(dt.asElement().getEnclosedElements())) {
            Optional<JsonCreator.Mode> annotation = jsonCreatorType(constructor);
            if (annotation.isEmpty()) {
                continue;
            }
            return Optional.of(Creator.of(
                    annotation.get(),
                    utils.generics.instantiateMethod(constructor, typeBindings, LocationKind.CREATOR)));
        }
        for (ExecutableElement method : ElementFilter.methodsIn(dt.asElement().getEnclosedElements())) {
            Optional<JsonCreator.Mode> annotation = jsonCreatorType(method);
            if (annotation.isEmpty()
                    || method.getReturnType().getKind() == TypeKind.VOID
                    || !method.getModifiers().contains(javax.lang.model.element.Modifier.STATIC)) {
                continue;
            }
            // Cannot instantiate with type bindings from class.
            // Need to infer from return type.
            InstantiatedMethod methodWithTypeTypeVars =
                    utils.generics.instantiateMethod(method, typeBindings, LocationKind.CREATOR);
            Map<TypeVar, TypeMirror> methodTypeVars = new LinkedHashMap<>();
            if (!utils.generics.tybeBindingsSatisfyingEquality(
                    tm, methodWithTypeTypeVars.returnType(), methodTypeVars)) {
                continue;
            }
            return Optional.of(Creator.of(
                    annotation.get(), utils.generics.applyTypeBindings(methodWithTypeTypeVars, methodTypeVars)));
        }
        return Optional.empty();
    }

    private Optional<JsonCreator.Mode> jsonCreatorType(ExecutableElement element) {
        return utils.annotations
                .findAnnotation(element, "com.fasterxml.jackson.annotation.JsonCreator")
                .map(wrapper -> wrapper.method("mode", true)
                        .orElseThrow(Exceptions::unexpected)
                        .asEnum(JsonCreator.Mode.class))
                .filter(mode -> mode != Mode.DISABLED);
    }

    public Optional<InstantiatedMethod> findJsonValueMethod(TypeMirror tm) {
        if (!(tm instanceof DeclaredType dt)) {
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
            return Optional.of(utils.generics.instantiateMethod(method, typeBindings, LocationKind.BLUEPRINT));
        }
        return Optional.empty();
    }

    public sealed interface Creator {
        static Creator of(JsonCreator.Mode mode, InstantiatedMethod method) {
            return switch (mode) {
                case DEFAULT -> method.parameters().size() == 1 ? new Converter(method) : new Properties(method);
                case DELEGATING -> new Converter(method);
                case PROPERTIES -> new Properties(method);
                default -> throw new ContextedRuntimeException(String.valueOf(mode));
            };
        }

        record Converter(InstantiatedMethod method) implements Creator {}

        record Properties(InstantiatedMethod method) implements Creator {}
    }
}
