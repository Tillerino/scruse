package org.tillerino.scruse.processor.features;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.util.Annotations.AnnotationValueWrapper;

public record Polymorphism(String discriminator, JsonTypeInfo.Id id, List<Child> children) {
    public static Optional<Polymorphism> of(TypeElement type, AnnotationProcessorUtils utils) {
        JsonTypeInfo annotation = type.getAnnotation(JsonTypeInfo.class);
        if (annotation == null) {
            return Optional.empty();
        }
        String discriminator = StringUtils.defaultIfEmpty(
                annotation.property(), annotation.use().getDefaultPropertyName());
        List<Child> children = utils.annotations
                .findAnnotation(type, "com.fasterxml.jackson.annotation.JsonSubTypes")
                .flatMap(subTypes -> subTypes.method("value", false))
                .map(AnnotationValueWrapper::asArray)
                .map(subTypes -> subTypes.stream()
                        .map(AnnotationValueWrapper::asAnnotation)
                        .map(subTypeAnnotation -> {
                            TypeMirror subType = subTypeAnnotation
                                    .method("value", false)
                                    .orElseThrow()
                                    .asTypeMirror();
                            Optional<String> nameFromTypeAnnotation =
                                    subTypeAnnotation.method("name", false).map(AnnotationValueWrapper::asString);
                            String name = name(
                                    annotation.use(),
                                    utils.elements.getTypeElement(subType.toString()),
                                    type,
                                    nameFromTypeAnnotation);
                            return new Child(subType, name);
                        })
                        .toList())
                .orElseGet(() -> {
                    if (type.getPermittedSubclasses().isEmpty()) {
                        throw new ContextedRuntimeException(
                                "Specify subclasses for " + type.getQualifiedName() + " or use a sealed interface.");
                    }
                    return type.getPermittedSubclasses().stream()
                            .map(e -> new Child(
                                    e,
                                    name(
                                            annotation.use(),
                                            utils.elements.getTypeElement(e.toString()),
                                            type,
                                            Optional.empty())))
                            .toList();
                });

        return Optional.of(new Polymorphism(discriminator, annotation.use(), children));
    }

    static String name(JsonTypeInfo.Id id, TypeElement subType, TypeElement superType, Optional<String> name) {
        return switch (id) {
            case CLASS -> fullName(subType);
            case MINIMAL_CLASS -> minimalName(subType, superType);
            case SIMPLE_NAME -> name.orElseGet(subType.getSimpleName()::toString);
            case NAME -> name.orElseThrow(() -> new UnsupportedOperationException("No name specified for " + subType));
            default -> throw new ContextedRuntimeException("Unsupported id: " + id);
        };
    }

    static String fullName(TypeElement element) {
        if (element.getEnclosingElement() instanceof PackageElement p) {
            return p.getQualifiedName().toString() + "."
                    + element.getSimpleName().toString();
        }
        if (element.getEnclosingElement() instanceof TypeElement t) {
            return fullName(t) + "$" + element.getSimpleName().toString();
        }
        throw new ContextedRuntimeException("Cannot determine full name of " + element);
    }

    static String minimalName(TypeElement element, TypeElement superType) {
        if (element.getEnclosingElement() instanceof PackageElement p) {
            List<String> packageNames = new ArrayList<>();
            List<String> superPackageNames = new ArrayList<>();
            packageNames(p, packageNames);
            packageNames(superType, superPackageNames);
            while (!packageNames.isEmpty()
                    && !superPackageNames.isEmpty()
                    && packageNames.get(0).equals(superPackageNames.get(0))) {
                packageNames.remove(0);
                superPackageNames.remove(0);
            }
            return packageNames.stream().map(s -> "." + s).collect(Collectors.joining()) + "."
                    + element.getSimpleName().toString();
        }
        if (element.getEnclosingElement() instanceof TypeElement t) {
            return minimalName(t, superType) + "$" + element.getSimpleName().toString();
        }
        throw new ContextedRuntimeException("Cannot determine full name of " + element);
    }

    static void packageNames(Element element, List<String> names) {
        if (element.getEnclosingElement() != null) {
            packageNames(element.getEnclosingElement(), names);
        }
        if (element instanceof PackageElement p) {
            names.add(p.getSimpleName().toString());
        }
    }

    public static boolean isSomeChild(TypeMirror type, Types types) {
        // this test is pretty half-assed, but false-positives only produce a bit of extra code
        for (TypeMirror directSupertype : types.directSupertypes(type)) {
            // care: annotations not on type, only on element
            if (directSupertype instanceof DeclaredType d && d.asElement().getAnnotation(JsonTypeInfo.class) != null
                    || isSomeChild(directSupertype, types)) {
                return true;
            }
        }
        return false;
    }

    public record Child(TypeMirror type, String name) {}
}
