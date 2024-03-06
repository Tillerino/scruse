package org.tillerino.scruse.processor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Optional;

public record Polymorphism(String discriminator, JsonTypeInfo.Id id, List<Child> children) {
	public static Optional<Polymorphism> of(TypeElement type, Elements elements) {
		JsonTypeInfo annotation = type.getAnnotation(JsonTypeInfo.class);
		if (annotation == null) {
			return Optional.empty();
		}
		String discriminator = StringUtils.defaultIfEmpty(annotation.property(), annotation.use().getDefaultPropertyName());
		if (type.getPermittedSubclasses().isEmpty()) {
			throw new UnsupportedOperationException("Only supporting polymorphism for sealed interfaces for now");
		}
		List<Child> children = type.getPermittedSubclasses().stream()
			.map(e -> new Child(e, name(annotation.use(), e, elements))).toList();
		return Optional.of(new Polymorphism(discriminator, annotation.use(), children));
	}

	static String name(JsonTypeInfo.Id id, TypeMirror subtype, Elements elements) {
		return switch (id) {
			// TODO: both are slightly off
			case CLASS -> subtype.toString();
			case MINIMAL_CLASS -> minimalName(subtype, elements);
			default -> throw new UnsupportedOperationException("Only supporting CLASS and MINIMAL_CLASS for now");
		};
	}

	static String minimalName(TypeMirror subtype, Elements elements) {
		TypeElement element = elements.getTypeElement(subtype.toString());
		if (element.getEnclosingElement() instanceof PackageElement) {
			return "." + element.getSimpleName().toString();
		}
		if (element.getEnclosingElement() instanceof TypeElement t) {
			return minimalName(t.asType(), elements) + "$" + element.getSimpleName().toString();
		}
		return subtype.toString();
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

	public record Child(TypeMirror type, String name) {
	}
}
