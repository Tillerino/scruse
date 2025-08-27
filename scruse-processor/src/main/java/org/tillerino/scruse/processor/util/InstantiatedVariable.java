package org.tillerino.scruse.processor.util;

import javax.lang.model.type.TypeMirror;
import org.tillerino.scruse.processor.config.AnyConfig;

public record InstantiatedVariable(TypeMirror type, String name, AnyConfig config) {
    @Override
    public String toString() {
        return type + " " + name();
    }
}
