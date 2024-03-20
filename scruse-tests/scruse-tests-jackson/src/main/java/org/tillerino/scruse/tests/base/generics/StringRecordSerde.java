package org.tillerino.scruse.tests.base.generics;

import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.GenericRecord;

import java.io.IOException;

@JsonConfig(uses = {
	StringSerde.class,
	GenericRecordSerde.class,
})
public interface StringRecordSerde {
	@JsonOutput
	void writeStringRecord(GenericRecord<String> obj, JsonGenerator gen) throws IOException;
}
