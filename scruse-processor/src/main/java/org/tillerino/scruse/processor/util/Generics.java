package org.tillerino.scruse.processor.util;

import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record Generics(AnnotationProcessorUtils utils) {
	public Map<TypeVar, TypeMirror> recordTypeBindings(DeclaredType d) {
		Map<TypeVar, TypeMirror> map = new LinkedHashMap<>();
		for (int i = 0; i < d.getTypeArguments().size(); i++) {
			TypeMirror type = ((TypeElement) d.asElement()).getTypeParameters().get(i).asType();
			if (type instanceof TypeVariable tVar) {
				map.put(TypeVar.of(tVar), d.getTypeArguments().get(i));
			}
		}
		return map;
	}

	public TypeMirror applyTypeBindings(TypeMirror t, Map<TypeVar, TypeMirror> bindings) {
		return t.accept(new RebuildingTypeVisitor() {
			@Override
			public TypeMirror visitTypeVariable(TypeVariable t, Types types) {
				return bindings.getOrDefault(TypeVar.of(t), t);
			}
		}, utils.types);
	}

	public InstantiatedVariable applyTypeBindings(InstantiatedVariable v, Map<TypeVar, TypeMirror> bindings) {
		return new InstantiatedVariable(applyTypeBindings(v.type(), bindings), v.name());
	}

	public List<InstantiatedVariable> applyTypeBindingsToAll(List<InstantiatedVariable> v, Map<TypeVar, TypeMirror> bindings) {
		return v.stream().map(p -> applyTypeBindings(p, bindings)).toList();
	}

	public InstantiatedMethod applyTypeBindings(Map<Generics.TypeVar, TypeMirror> typeBindings, InstantiatedMethod instantiatedMethod) {
		List<InstantiatedVariable> newParameters = applyTypeBindingsToAll(instantiatedMethod.parameters(), typeBindings);
		TypeMirror newReturnType = applyTypeBindings(instantiatedMethod.returnType(), typeBindings);
		return new InstantiatedMethod(instantiatedMethod.name(), newReturnType, newParameters, instantiatedMethod.element());
	}

	public List<InstantiatedMethod> instantiateMethods(TypeMirror tm) {
		// TODO: do this eagerly, since it is probably not too cheap
		List<InstantiatedMethod> methods = new ArrayList<>();
		if (tm instanceof DeclaredType t) {
			Map<TypeVar, TypeMirror> typeVariableMapping = recordTypeBindings(t);
			for (Element methodPossibly : t.asElement().getEnclosedElements()) {
				if (methodPossibly instanceof ExecutableElement method) {
					methods.add(new InstantiatedMethod(method.getSimpleName().toString(),
						applyTypeBindings(method.getReturnType(), typeVariableMapping),
						method.getParameters().stream().map(p -> new InstantiatedVariable(
							applyTypeBindings(p.asType(), typeVariableMapping), p.getSimpleName().toString()
						)).toList(),
						method));
				}
			}
		}
		return methods;
	}

	/**
	 * Required since {@link TypeVariable} and its corresponding element do not implement hashCode and equals?
	 */
	public record TypeVar(Element owner, String name) {
		public static TypeVar of(TypeVariable t) {
			return new TypeVar(t.asElement().getEnclosingElement(), t.asElement().getSimpleName().toString());
		}
	}
}
