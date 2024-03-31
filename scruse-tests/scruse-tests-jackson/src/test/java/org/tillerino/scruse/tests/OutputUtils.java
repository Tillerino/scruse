package org.tillerino.scruse.tests;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.function.FailableBiConsumer;
import org.apache.commons.lang3.function.FailableConsumer;
import org.tillerino.scruse.api.SerializationContext;

public class OutputUtils {
    public static String withJacksonJsonGenerator(FailableConsumer<JsonGenerator, IOException> output)
            throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator generator = new JsonFactory().createGenerator(out, JsonEncoding.UTF8);
        output.accept(generator);
        generator.flush();
        return out.toString(StandardCharsets.UTF_8);
    }

    public static <T> String serialize(T obj, FailableBiConsumer<T, JsonGenerator, IOException> output)
            throws IOException {
        return withJacksonJsonGenerator(generator -> output.accept(obj, generator));
    }

    public static <T, U> String serialize2(T obj, U obj2, FailableTriConsumer<T, JsonGenerator, U, IOException> output)
            throws IOException {
        return withJacksonJsonGenerator(generator -> output.accept(obj, generator, obj2));
    }

    public static <T> String assertIsEqualToDatabind(T obj, FailableBiConsumer<T, JsonGenerator, IOException> output)
            throws IOException {
        String databind = new ObjectMapper().writeValueAsString(obj);
        String ours = serialize(obj, output);
        System.out.println(ours);
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> String assertIsEqualToDatabind(
            T obj, FailableTriConsumer<T, JsonGenerator, SerializationContext, IOException> output) throws IOException {
        return assertIsEqualToDatabind2(
                obj, new SerializationContext(), (obj2, generator, context) -> output.accept(obj, generator, context));
    }

    public static <T, U> String assertIsEqualToDatabind2(
            T obj, U obj2, FailableTriConsumer<T, JsonGenerator, U, IOException> output) throws IOException {
        String databind = new ObjectMapper().writeValueAsString(obj);
        String ours = serialize2(obj, obj2, output);
        System.out.println(ours);
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public interface FailableTriConsumer<T, U, V, E extends Throwable> {
        void accept(T t, U u, V v) throws E;
    }
}
