package org.tillerino.scruse.tests.base.generics;

import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonInterface;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;

@JsonInterface
// use 'U' instead of 'T' to make sure that we actually instantiate the type
public interface GenericSerde<U> {
	@JsonOutput
	void writeOnGenericInterface(U obj, JsonGenerator gen) throws IOException;
}
