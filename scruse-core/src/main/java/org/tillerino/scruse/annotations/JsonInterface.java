package org.tillerino.scruse.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * For types annotated with {@link JsonInterface}, no implementation will be generated.
 */
@Target(ElementType.TYPE)
public @interface JsonInterface {
}
