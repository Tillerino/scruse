package org.tillerino.scruse.processor.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.AnnotationProcessorUtils.GetAnnotationValues;
import org.tillerino.scruse.processor.features.Generics.TypeVar;

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

    public Optional<AnnotationMirrorWrapper> findAnnotation(Element element, String annotationType) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(annotationType)) {
                return Optional.of(new AnnotationMirrorWrapper(annotationMirror, utils));
            }
        }
        return Optional.empty();
    }

    public record AnnotationMirrorWrapper(AnnotationMirror mirror, AnnotationProcessorUtils utils) {
        public Optional<AnnotationValueWrapper> method(String name, boolean withDefaults) {
            return filterMethod(
                            name,
                            withDefaults
                                    ? utils.elements.getElementValuesWithDefaults(mirror)
                                    : mirror.getElementValues())
                    .map(Map.Entry::getValue)
                    .map(AnnotationValueWrapper::new);
        }

        private static Optional<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>>
                filterMethod(String name, Map<? extends ExecutableElement, ? extends AnnotationValue> baseValues) {
            return baseValues.entrySet().stream()
                    .filter(entry -> entry.getKey().getSimpleName().toString().equals(name))
                    .findFirst();
        }
    }

    public record AnnotationValueWrapper(AnnotationValue value) {
        public List<AnnotationValueWrapper> asArray() {
            return Exceptions.notNull(
                    value.accept(
                            new GetAnnotationValues<List<AnnotationValueWrapper>, Void>() {
                                @Override
                                public List<AnnotationValueWrapper> visitArray(
                                        List<? extends AnnotationValue> vals, Void o) {
                                    return vals.stream()
                                            .map(AnnotationValueWrapper::new)
                                            .toList();
                                }
                            },
                            null),
                    "not an array: %s",
                    value);
        }

        public AnnotationMirrorWrapper asAnnotation() {
            return new AnnotationMirrorWrapper(
                    Exceptions.notNull(
                            value.accept(
                                    new GetAnnotationValues<AnnotationMirror, Void>() {
                                        @Override
                                        public AnnotationMirror visitAnnotation(AnnotationMirror a, Void o) {
                                            return a;
                                        }
                                    },
                                    null),
                            "not an annotation: %s",
                            value),
                    null);
        }

        public String asString() {
            return Exceptions.notNull(
                    value.accept(
                            new GetAnnotationValues<String, Void>() {
                                @Override
                                public String visitString(String s, Void o) {
                                    return s;
                                }
                            },
                            null),
                    "not a string: %s",
                    value);
        }

        public TypeMirror asTypeMirror() {
            return Exceptions.notNull(
                    value.accept(
                            new GetAnnotationValues<TypeMirror, Void>() {
                                @Override
                                public TypeMirror visitType(TypeMirror t, Void o) {
                                    return t;
                                }
                            },
                            null),
                    "not a type: %s",
                    value);
        }

        public <T extends Enum<T>> T asEnum(Class<T> cls) {
            return Exceptions.notNull(
                    value.accept(
                            new GetAnnotationValues<T, Void>() {
                                @Override
                                public T visitEnumConstant(VariableElement c, Void o) {
                                    return Enum.valueOf(cls, c.getSimpleName().toString());
                                }
                            },
                            null),
                    "not an enum: %s",
                    value);
        }

        public boolean asBoolean() {
            return Exceptions.notNull(
                    value.accept(
                            new GetAnnotationValues<Boolean, Void>() {
                                @Override
                                public Boolean visitBoolean(boolean b, Void o) {
                                    return b;
                                }
                            },
                            null),
                    "not a boolean: %s",
                    value);
        }
    }
}
