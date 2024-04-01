package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;

/** This class is in the main class path since we base our shading on this. */
public class ToShadeUtils {
    public static byte[] withJsonGenerator(FailableConsumer<JsonGenerator, IOException> output) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator generator = new SmileFactory().createGenerator(out);
        output.accept(generator);
        generator.flush();
        return out.toByteArray();
    }

    public static <T> T withJsonParser(byte[] smile, FailableFunction<JsonParser, T, IOException> consumer)
            throws IOException {

        try (JsonParser parser = new SmileFactory().createParser(smile)) {
            return consumer.apply(parser);
        }
    }
}
