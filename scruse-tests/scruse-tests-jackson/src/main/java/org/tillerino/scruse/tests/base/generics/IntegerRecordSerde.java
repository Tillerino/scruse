package org.tillerino.scruse.tests.base.generics;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.base.BoxedScalarsReader;
import org.tillerino.scruse.tests.base.delegate.BoxedScalarsWriter;
import org.tillerino.scruse.tests.model.GenericRecord;

import java.io.IOException;

@JsonConfig(uses = {
	BoxedScalarsWriter.class,
	BoxedScalarsReader.class,
	GenericRecordSerde.class,
})
public interface IntegerRecordSerde {
	@JsonInput
	GenericRecord<Integer> readIntegerRecord(JsonParser parser) throws IOException;

	@JsonOutput
	void writeIntegerRecord(GenericRecord<Integer> obj, JsonGenerator gen) throws IOException;
}
