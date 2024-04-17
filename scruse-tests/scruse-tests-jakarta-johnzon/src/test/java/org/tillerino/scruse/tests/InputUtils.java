package org.tillerino.scruse.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableFunction;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.tillerino.scruse.api.DeserializationContext;
import org.tillerino.scruse.helpers.JakartaJsonParserHelper.JsonParserWrapper;

public class InputUtils {

    public static <T> T assertIsEqualToDatabind(
            String json, FailableFunction<JsonParserWrapper, T, Exception> consumer, TypeReference<T> typeRef)
            throws Exception {
        T ours = deserialize(json, consumer);
        T databind = new ObjectMapper().readValue(json, typeRef);
        assertThat(ours).isEqualTo(databind);
        return ours;
    }

    public static <T, U> T assertIsEqualToDatabind2(
            String json,
            U arg2,
            FailableBiFunction<JsonParserWrapper, U, T, Exception> consumer,
            TypeReference<T> typeRef)
            throws Exception {
        T ours = deserialize2(json, arg2, consumer);
        T databind = new ObjectMapper().readValue(json, typeRef);
        assertThat(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> T deserialize(String json, FailableFunction<JsonParserWrapper, T, Exception> consumer)
            throws Exception {
        return ToShadeUtils.withJsonReader(json, consumer);
    }

    public static <T, U> T deserialize2(
            String json, U obj2, FailableBiFunction<JsonParserWrapper, U, T, Exception> consumer) throws Exception {
        return ToShadeUtils.withJsonReader(json, parser -> consumer.apply(parser, obj2));
    }

    public static <T> T assertIsEqualToDatabind(
            String json,
            FailableBiFunction<JsonParserWrapper, DeserializationContext, T, Exception> consumer,
            TypeReference<T> typeRef)
            throws Exception {
        return ToShadeUtils.withJsonReader(json, parser -> {
            T ours = consumer.apply(parser, new DeserializationContext());
            T databind = new ObjectMapper().readValue(json, typeRef);
            assertThat(ours).isEqualTo(databind);
            return ours;
        });
    }

    public static <T> T assertIsEqualToDatabindComparingRecursively(
            String json, FailableFunction<JsonParserWrapper, T, Exception> consumer, TypeReference<T> typeRef)
            throws Exception {
        return ToShadeUtils.withJsonReader(json, parser -> {
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
