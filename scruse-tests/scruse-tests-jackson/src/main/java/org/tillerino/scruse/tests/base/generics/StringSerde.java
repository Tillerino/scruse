package org.tillerino.scruse.tests.base.generics;

import org.tillerino.scruse.annotations.JsonImpl;

@JsonImpl
public interface StringSerde extends GenericSerde<String> {
}
