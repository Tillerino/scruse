package org.tillerino.scruse.processor.util;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public record InstantiatedVariable(TypeMirror type, VariableElement element) {
    @Override
    public String toString() {
        return type + " " + name();
    }

    public String name() {
        return element.getSimpleName().toString();
    }
}
