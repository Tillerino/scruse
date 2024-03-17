package org.tillerino.scruse.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marks a descendant of {@link JsonInterface} as an implementation.
 * An implementation will be generated.
 */
@Target(ElementType.TYPE)
public @interface JsonImpl {
}
