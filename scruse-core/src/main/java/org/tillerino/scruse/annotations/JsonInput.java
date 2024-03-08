package org.tillerino.scruse.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
// runtime retention for reflection bridge
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonInput {
}
