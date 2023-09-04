package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.stream.JsonReader;
import org.apache.commons.lang3.function.FailableFunction;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

public class InputUtils {
	static <T> T withJacksonJsonParser(String json, FailableFunction<JsonParser, T, IOException> consumer) throws IOException {
		try (JsonParser parser = new JsonFactory().createParser(json)) {
			return consumer.apply(parser);
		}
	}

	static <T> T withGsonJsonReader(String json, FailableFunction<JsonReader, T, IOException> consumer) throws IOException {
		JsonReader parser = new JsonReader(new StringReader(json));
		return consumer.apply(parser);
	}

	public static <T> T assertThatJacksonJsonParserIsEqualToDatabind(String json, FailableFunction<JsonParser, T, IOException> consumer, TypeReference<T> typeRef) throws IOException {
		return withJacksonJsonParser(json, parser -> {
			T ours = consumer.apply(parser);
			T databind = new ObjectMapper().readValue(json, typeRef);
			assertThat(ours).isEqualTo(databind);
			return ours;
		});
	}

	public static <T> T assertThatGsonJsonReaderIsEqualToDatabind(String json, FailableFunction<JsonReader, T, IOException> consumer, TypeReference<T> typeRef) throws IOException {
		return withGsonJsonReader(json, parser -> {
			T ours = consumer.apply(parser);
			T databind = new ObjectMapper().readValue(json, typeRef);
			assertThat(ours).isEqualTo(databind);
			return ours;
		});
	}
	public static <T> T assertThatJacksonJsonParserIsEqualToDatabindComparingRecursively(String json, FailableFunction<JsonParser, T, IOException> consumer, TypeReference<T> typeRef) throws IOException {
		return withJacksonJsonParser(json, parser -> {
			T ours = consumer.apply(parser);
			T databind = new ObjectMapper().readValue(json, typeRef);
			assertEqualsComparingRecursively(ours, databind);
			return ours;
		});
	}

	public static <T> T assertThatGsonJsonReaderIsEqualToDatabindComparingRecursively(String json, FailableFunction<JsonReader, T, IOException> consumer, TypeReference<T> typeRef) throws IOException {
		return withGsonJsonReader(json, parser -> {
			T ours = consumer.apply(parser);
			T databind = new ObjectMapper().readValue(json, typeRef);
			assertEqualsComparingRecursively(ours, databind);
			return ours;
		});
	}

	private static <T> void assertEqualsComparingRecursively(T ours, T databind) {
		assertThat(ours)
			.usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
				.withStrictTypeChecking(true)
				.withComparatorForType(Float::compare, Float.class)
				.withComparatorForType(Double::compare, Double.class)
				.build())
			.isEqualTo(databind);
	}
}
