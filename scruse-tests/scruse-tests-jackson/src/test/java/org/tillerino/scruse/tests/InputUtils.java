package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableFunction;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.tillerino.scruse.api.DeserializationContext;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class InputUtils {
	public static <T> T withJacksonJsonParser(String json, FailableFunction<JsonParser, T, IOException> consumer) throws IOException {
		try (JsonParser parser = new JsonFactory().createParser(json)) {
			return consumer.apply(parser);
		}
	}

	public static <T> T assertIsEqualToDatabind(String json, FailableFunction<JsonParser, T, IOException> consumer, TypeReference<T> typeRef) throws IOException {
		T ours = deserialize(json, consumer);
		T databind = new ObjectMapper().readValue(json, typeRef);
		assertThat(ours).isEqualTo(databind);
		return ours;
	}

	public static <T> T deserialize(String json, FailableFunction<JsonParser, T, IOException> consumer) throws IOException {
		return withJacksonJsonParser(json, consumer::apply);
	}

	public static <T, U> T deserialize2(String json, U obj2, FailableBiFunction<JsonParser, U, T, IOException> consumer) throws IOException {
		return withJacksonJsonParser(json, parser -> consumer.apply(parser, obj2));
	}

	public static <T> T assertIsEqualToDatabind(String json, FailableBiFunction<JsonParser, DeserializationContext, T, IOException> consumer, TypeReference<T> typeRef) throws IOException {
		return withJacksonJsonParser(json, parser -> {
			T ours = consumer.apply(parser, new DeserializationContext());
			T databind = new ObjectMapper().readValue(json, typeRef);
			assertThat(ours).isEqualTo(databind);
			return ours;
		});
	}

	public static <T> T assertIsEqualToDatabindComparingRecursively(String json, FailableFunction<JsonParser, T, IOException> consumer, TypeReference<T> typeRef) throws IOException {
		return withJacksonJsonParser(json, parser -> {
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
