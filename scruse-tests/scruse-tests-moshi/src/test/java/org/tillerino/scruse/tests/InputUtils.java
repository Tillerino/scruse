package org.tillerino.scruse.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.moshi.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableFunction;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.tillerino.scruse.api.DeserializationContext;

public class InputUtils {

    public static <T> T withJsonReader(String json, FailableFunction<JsonReader, T, IOException> consumer)
            throws IOException {
        Source source = Okio.source(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        BufferedSource buffer = Okio.buffer(source);
        JsonReader parser = JsonReader.of(buffer);
        return consumer.apply(parser);
    }

    public static <T> T assertIsEqualToDatabind(
            String json, FailableFunction<JsonReader, T, IOException> consumer, TypeReference<T> typeRef)
            throws IOException {
        T ours = deserialize(json, consumer);
        T databind = new ObjectMapper().readValue(json, typeRef);
        assertThat(ours).isEqualTo(databind);
        return ours;
    }

    public static <T, U> void assertIsEqualToDatabind2(
            String json, U arg2, FailableBiFunction<JsonReader, U, T, IOException> consumer, TypeReference<T> typeRef)
            throws IOException {
        T ours = deserialize2(json, arg2, consumer);
        T databind = new ObjectMapper().readValue(json, typeRef);
        assertThat(ours).isEqualTo(databind);
    }

    public static <T> T deserialize(String json, FailableFunction<JsonReader, T, IOException> consumer)
            throws IOException {
        return withJsonReader(json, consumer);
    }

    public static <T, U> T deserialize2(String json, U obj2, FailableBiFunction<JsonReader, U, T, IOException> consumer)
            throws IOException {
        return withJsonReader(json, parser -> consumer.apply(parser, obj2));
    }

    public static <T> T assertIsEqualToDatabind(
            String json,
            FailableBiFunction<JsonReader, DeserializationContext, T, IOException> consumer,
            TypeReference<T> typeRef)
            throws IOException {
        return withJsonReader(json, parser -> {
            T ours = consumer.apply(parser, new DeserializationContext());
            T databind = new ObjectMapper().readValue(json, typeRef);
            assertThat(ours).isEqualTo(databind);
            return ours;
        });
    }

    public static <T> T assertIsEqualToDatabindComparingRecursively(
            String json, FailableFunction<JsonReader, T, IOException> consumer, TypeReference<T> typeRef)
            throws IOException {
        return withJsonReader(json, parser -> {
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
