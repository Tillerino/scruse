package org.tillerino.scruse.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * For types annotated with {@link JsonInterface}, no implementation will be generated.
 * Child types can then be annotated with {@link JsonImpl} to generate an implementation.
 */
@Target(ElementType.TYPE)
public @interface JsonInterface {
}
