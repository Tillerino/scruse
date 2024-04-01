package org.tillerino.scruse.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.alibaba.fastjson2.JSONReader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableFunction;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.tillerino.scruse.api.DeserializationContext;

public class InputUtils {
    public static <T> T withReader(String json, FailableFunction<JSONReader, T, IOException> consumer)
            throws IOException {
        try (JSONReader reader = JSONReader.of(json)) {
            return consumer.apply(reader);
        }
    }

    public static <T> T assertIsEqualToDatabind(
            String json, FailableFunction<JSONReader, T, IOException> consumer, TypeReference<T> typeRef)
            throws IOException {
        T ours = deserialize(json, consumer);
        T databind = new ObjectMapper().readValue(json, typeRef);
        assertThat(ours).isEqualTo(databind);
        return ours;
    }

    public static <T, U> T assertIsEqualToDatabind2(
            String json, U arg2, FailableBiFunction<JSONReader, U, T, IOException> consumer, TypeReference<T> typeRef)
            throws IOException {
        T ours = deserialize2(json, arg2, consumer);
        T databind = new ObjectMapper().readValue(json, typeRef);
        assertThat(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> T deserialize(String json, FailableFunction<JSONReader, T, IOException> consumer)
            throws IOException {
        return withReader(json, consumer);
    }

    public static <T, U> T deserialize2(String json, U obj2, FailableBiFunction<JSONReader, U, T, IOException> consumer)
            throws IOException {
        return withReader(json, parser -> consumer.apply(parser, obj2));
    }

    public static <T> T assertIsEqualToDatabind(
            String json,
            FailableBiFunction<JSONReader, DeserializationContext, T, IOException> consumer,
            TypeReference<T> typeRef)
            throws IOException {
        return withReader(json, parser -> {
            T ours = consumer.apply(parser, new DeserializationContext());
            T databind = new ObjectMapper().readValue(json, typeRef);
            assertThat(ours).isEqualTo(databind);
            return ours;
        });
    }

    public static <T> T assertIsEqualToDatabindComparingRecursively(
            String json, FailableFunction<JSONReader, T, IOException> consumer, TypeReference<T> typeRef)
            throws IOException {
        return withReader(json, parser -> {
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
