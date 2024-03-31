package org.tillerino.scruse.processor.util;

import javax.lang.model.type.TypeMirror;

public record InstantiatedVariable(TypeMirror type, String name) {
    @Override
    public String toString() {
        return type + " " + name;
    }
}
