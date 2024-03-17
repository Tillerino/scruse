package org.tillerino.scruse.tests.base.generics;

import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonImpl;
import org.tillerino.scruse.annotations.JsonInterface;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.GenericRecord;

import java.io.IOException;

public interface GenericRecordSerde {
	@JsonOutput
	<T> void writeGenericRecord(GenericRecord<T> obj, JsonGenerator gen, GenericSerde<T> fieldSerde) throws IOException;

	@JsonInterface
	// use 'U' instead of 'T' to make sure that we actually instantiate the type
	interface GenericSerde<U> {
		@JsonOutput
		void write(U obj, JsonGenerator gen) throws IOException;
	}

	@JsonImpl
	interface StringSerde extends GenericSerde<String> { }
}
