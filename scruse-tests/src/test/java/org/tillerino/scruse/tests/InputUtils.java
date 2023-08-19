package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class InputUtils {
	static void withJacksonJsonParser(String json, FailableConsumer<JsonParser, IOException> consumer) throws Exception {
		try (JsonParser parser = new JsonFactory().createParser(json)) {
			consumer.accept(parser);
		}
	}

	static <T> void assertThatJacksonJsonParserIsEqualToDatabind(String json, FailableFunction<JsonParser, T, IOException> consumer, TypeReference<T> typeRef) throws Exception {
		withJacksonJsonParser(json, parser -> {
			T ours = consumer.apply(parser);
			T databind = new ObjectMapper().readValue(json, typeRef);
			assertThat(ours).isEqualTo(databind);
		});
	}
}
