package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.AnEnum;

import java.io.IOException;

public record ScalarFieldsRecord(boolean bo, byte by, short s, int i, long l, char c, float f, double d,
		 Boolean bbo, Byte bby, Short ss, Integer ii, Long ll, Character cc, Float ff, Double dd, String str, AnEnum en) {
	interface Serde {
		@JsonOutput
		void write(ScalarFieldsRecord record, JsonGenerator generator) throws IOException;
		@JsonInput
		ScalarFieldsRecord read(JsonParser parser) throws IOException;
	}
}
