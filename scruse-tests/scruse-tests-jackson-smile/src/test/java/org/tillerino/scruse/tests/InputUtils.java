package org.tillerino.scruse.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.tillerino.scruse.tests.ToShadeUtils.withJsonParser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.smile.databind.SmileMapper;
import java.io.IOException;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableFunction;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.tillerino.scruse.api.DeserializationContext;

public class InputUtils {

    public static <T> T assertIsEqualToDatabind(
            byte[] smile, FailableFunction<JsonParser, T, IOException> consumer, TypeReference<T> typeRef)
            throws IOException {
        T ours = deserialize(smile, consumer);
        T databind = new SmileMapper().readValue(smile, typeRef);
        assertThat(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> T deserialize(byte[] smile, FailableFunction<JsonParser, T, IOException> consumer)
            throws IOException {
        return withJsonParser(smile, consumer);
    }

    public static <T, U> T deserialize2(
            byte[] smile, U obj2, FailableBiFunction<JsonParser, U, T, IOException> consumer) throws IOException {
        return withJsonParser(smile, parser -> consumer.apply(parser, obj2));
    }

    public static <T> T assertIsEqualToDatabind(
            byte[] smile,
            FailableBiFunction<JsonParser, DeserializationContext, T, IOException> consumer,
            TypeReference<T> typeRef)
            throws IOException {
        return withJsonParser(smile, parser -> {
            T ours = consumer.apply(parser, new DeserializationContext());
            T databind = new SmileMapper().readValue(smile, typeRef);
            assertThat(ours).isEqualTo(databind);
            return ours;
        });
    }

    public static <T, U> T assertIsEqualToDatabind2(
            byte[] smile, U arg2, FailableBiFunction<JsonParser, U, T, IOException> consumer, TypeReference<T> typeRef)
            throws IOException {
        T ours = deserialize2(smile, arg2, consumer);
        T databind = new SmileMapper().readValue(smile, typeRef);
        assertThat(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> T assertIsEqualToDatabindComparingRecursively(
            byte[] smile, FailableFunction<JsonParser, T, IOException> consumer, TypeReference<T> typeRef)
            throws IOException {
        return withJsonParser(smile, parser -> {
            T ours = consumer.apply(parser);
            T databind = new SmileMapper().readValue(smile, typeRef);
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
