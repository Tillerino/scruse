package org.tillerino.scruse.processor.util;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Need this to instantiate generics.
 */
public record InstantiatedMethod(String name, TypeMirror returnType, List<InstantiatedVariable> parameters,
	 ExecutableElement element) implements Named {

}
