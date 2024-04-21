package org.tillerino.scruse.processor.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.util.Generics.TypeVar;

public record Annotations(AnnotationProcessorUtils utils) {
    public Optional<InstantiatedMethod> findJsonValueMethod(TypeMirror tm) {
        if (!(tm instanceof DeclaredType dt)) {
            return Optional.empty();
        }
        Map<TypeVar, TypeMirror> typeBindings = utils.generics.recordTypeBindings(dt);
        for (ExecutableElement method : ElementFilter.methodsIn(dt.asElement().getEnclosedElements())) {
            if (findAnnotation(method, "com.fasterxml.jackson.annotation.JsonValue")
                            .isEmpty()
                    || !method.getParameters().isEmpty()
                    || method.getReturnType().getKind() == TypeKind.VOID) {
                continue;
            }
            return Optional.of(utils.generics.instantiateMethod(method, typeBindings));
        }
        return Optional.empty();
    }

    public Optional<InstantiatedMethod> findJsonCreatorMethod(TypeMirror tm) {
        if (!(tm instanceof DeclaredType dt)) {
            return Optional.empty();
        }
        Map<TypeVar, TypeMirror> typeBindings = utils.generics.recordTypeBindings(dt);
        for (ExecutableElement constructor :
                ElementFilter.constructorsIn(dt.asElement().getEnclosedElements())) {
            if (findAnnotation(constructor, "com.fasterxml.jackson.annotation.JsonCreator")
                    .isEmpty()) {
                continue;
            }
            return Optional.of(utils.generics.instantiateMethod(constructor, typeBindings));
        }
        for (ExecutableElement method : ElementFilter.methodsIn(dt.asElement().getEnclosedElements())) {
            if (findAnnotation(method, "com.fasterxml.jackson.annotation.JsonCreator")
                            .isEmpty()
                    || method.getReturnType().getKind() == TypeKind.VOID) {
                continue;
            }
            if (!method.getModifiers().contains(javax.lang.model.element.Modifier.STATIC)) {
                continue;
            }
            // Cannot instantiate with type bindings from class.
            // Need to infer from return type.
            InstantiatedMethod methodWithTypeTypeVars = utils.generics.instantiateMethod(method, typeBindings);
            Map<TypeVar, TypeMirror> methodTypeVars = new LinkedHashMap<>();
            if (!utils.generics.tybeBindingsSatisfyingEquality(
                    tm, methodWithTypeTypeVars.returnType(), methodTypeVars)) {
                continue;
            }
            return Optional.of(utils.generics.applyTypeBindings(methodWithTypeTypeVars, methodTypeVars));
        }
        return Optional.empty();
    }

    public Optional<AnnotationMirror> findAnnotation(Element element, String annotationType) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(annotationType)) {
                return Optional.of(annotationMirror);
            }
        }
        return Optional.empty();
    }
}
