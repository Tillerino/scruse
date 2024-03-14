package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.PrimitiveArrayFieldsRecord;
import org.tillerino.scruse.tests.model.ReferenceArrayFieldsRecord;

import java.io.IOException;

public interface ArrayFieldsRecordSerde {
	@JsonOutput
	void writePrimitive(PrimitiveArrayFieldsRecord record, JsonGenerator out) throws IOException;

	@JsonInput
	PrimitiveArrayFieldsRecord readPrimitive(JsonParser in) throws IOException;

	@JsonOutput
	void writeReference(ReferenceArrayFieldsRecord record, JsonGenerator out) throws IOException;

	@JsonInput
	ReferenceArrayFieldsRecord readReference(JsonParser in) throws IOException;
}
