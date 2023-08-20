package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;

import java.io.IOException;

public record NoFieldsRecord() {
	interface Input {
		@JsonInput
		NoFieldsRecord read(JsonParser parser) throws IOException;
	}
}
