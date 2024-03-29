package org.tillerino.scruse.processor.util;

import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Need this to instantiate generics.
 */
public record InstantiatedMethod(String name, TypeMirror returnType, List<InstantiatedVariable> parameters,
	 ExecutableElement element) implements Named {

	public boolean sameTypes(InstantiatedMethod other, AnnotationProcessorUtils utils) {
		if (!utils.types.isSameType(returnType, other.returnType)) {
			return false;
		}
		if (parameters.size() != other.parameters.size()) {
			return false;
		}
		for (int i = 0; i < parameters.size(); i++) {
			if (!utils.types.isSameType(parameters.get(i).type(), other.parameters.get(i).type())) {
				return false;
			}
		}
		return true;
	}
}
