package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.stream.JsonWriter;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;

public record ScalarFieldsRecord(boolean bo, byte by, short s, int i, long l, char c, float f, double d, Boolean bbo, Byte bby, Short ss, Integer ii, Long ll, Character cc, Float ff, Double dd, String str) {
	interface Output {
		@JsonOutput
		void write(ScalarFieldsRecord record, JsonGenerator generator) throws IOException;
		@JsonOutput
		void write(ScalarFieldsRecord record, JsonWriter generator) throws IOException;
		@JsonOutput
		JsonNode write(ScalarFieldsRecord record);
	}
}
