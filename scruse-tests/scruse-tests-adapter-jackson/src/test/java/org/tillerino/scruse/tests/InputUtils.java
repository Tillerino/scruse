package org.tillerino.scruse.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableFunction;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.tillerino.scruse.adapters.JacksonJsonParserAdapter;
import org.tillerino.scruse.api.DeserializationContext;

public class InputUtils {

    static ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    public static <T> T withJsonParser(String json, FailableFunction<JacksonJsonParserAdapter, T, Exception> consumer)
            throws Exception {
        try (JsonParser parser = new JsonFactory().createParser(json)) {
            return consumer.apply(new JacksonJsonParserAdapter(parser));
        }
    }

    public static <T> T assertIsEqualToDatabind(
            String json, FailableFunction<JacksonJsonParserAdapter, T, Exception> consumer, TypeReference<T> typeRef)
            throws Exception {
        T ours = deserialize(json, consumer);
        T databind = objectMapper.readValue(json, typeRef);
        assertThat(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> T deserialize(String json, FailableFunction<JacksonJsonParserAdapter, T, Exception> consumer)
            throws Exception {
        return withJsonParser(json, consumer);
    }

    public static <T, U> T deserialize2(
            String json, U obj2, FailableBiFunction<JacksonJsonParserAdapter, U, T, Exception> consumer)
            throws Exception {
        return withJsonParser(json, parser -> consumer.apply(parser, obj2));
    }

    public static <T> T assertIsEqualToDatabind(
            String json,
            FailableBiFunction<JacksonJsonParserAdapter, DeserializationContext, T, Exception> consumer,
            TypeReference<T> typeRef)
            throws Exception {
        return withJsonParser(json, parser -> {
            T ours = consumer.apply(parser, new DeserializationContext());
            T databind = objectMapper.readValue(json, typeRef);
            assertThat(ours).isEqualTo(databind);
            return ours;
        });
    }

    public static <T, U> T assertIsEqualToDatabind2(
            String json,
            U arg2,
            FailableBiFunction<JacksonJsonParserAdapter, U, T, Exception> consumer,
            TypeReference<T> typeRef)
            throws Exception {
        T ours = deserialize2(json, arg2, consumer);
        T databind = objectMapper.readValue(json, typeRef);
        assertThat(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> T assertIsEqualToDatabindComparingRecursively(
            String json, FailableFunction<JacksonJsonParserAdapter, T, Exception> consumer, TypeReference<T> typeRef)
            throws Exception {
        return withJsonParser(json, parser -> {
            T ours = consumer.apply(parser);
            T databind = objectMapper.readValue(json, typeRef);
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
