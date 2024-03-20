package org.tillerino.scruse.tests.base.generics;

import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.GenericRecord;

import java.io.IOException;

public interface GenericRecordSerde {
	@JsonOutput
	<T> void writeGenericRecord(GenericRecord<T> obj, JsonGenerator gen, GenericSerde<T> fieldSerde) throws IOException;
}
