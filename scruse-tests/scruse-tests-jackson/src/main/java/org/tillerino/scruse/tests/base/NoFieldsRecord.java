package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;

import java.io.IOException;

record NoFieldsRecord() {
	interface Input {
		@JsonInput
		NoFieldsRecord read(JsonParser parser) throws IOException;
	}
}
