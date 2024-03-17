package org.tillerino.scruse.processor.util;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Types;
import java.util.LinkedHashMap;
import java.util.Map;

public class Generics {
	public static Map<TypeVar, TypeMirror> recordTypeBindings(TypeMirror t) {
		if (!(t instanceof DeclaredType d)) {
			return Map.of();
		}
		Map<TypeVar, TypeMirror> map = new LinkedHashMap<>();
		for (int i = 0; i < d.getTypeArguments().size(); i++) {
			TypeMirror type = ((TypeElement) d.asElement()).getTypeParameters().get(i).asType();
			if (type instanceof TypeVariable tVar) {
				map.put(TypeVar.of(tVar), d.getTypeArguments().get(i));
			}
		}
		return map;
	}

	public static TypeMirror applyTypeBindings(TypeMirror t, Map<TypeVar, TypeMirror> bindings, Types types) {
		return t.accept(new RebuildingTypeVisitor() {
			@Override
			public TypeMirror visitTypeVariable(TypeVariable t, Types types) {
				return bindings.getOrDefault(TypeVar.of(t), t);
			}
		}, types);
	}

	/**
	 * Required since {@link TypeVariable} and its corresponding element do not implement hashCode and equals?
	 */
	public record TypeVar(Element owner, String name) {
		static TypeVar of(TypeVariable t) {
			return new TypeVar(t.asElement().getEnclosingElement(), t.asElement().getSimpleName().toString());
		}
	}
}
