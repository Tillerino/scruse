package org.tillerino.scruse.tests;

import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.johnzon.core.JsonProviderImpl;
import org.tillerino.scruse.helpers.JakartaJsonParserHelper.JsonParserWrapper;

/** This class is in the main class path since we base our shading on this. */
public class ToShadeUtils {

    public static <T> T withJsonReader(String json, FailableFunction<JsonParserWrapper, T, IOException> consumer)
            throws IOException {
        JsonParser parser = new JsonProviderImpl().createParser(new StringReader(json));
        return consumer.apply(new JsonParserWrapper(parser));
    }

    public static String withGsonJsonWriter(FailableConsumer<JsonGenerator, IOException> output) throws IOException {
        StringWriter out = new StringWriter();
        JsonGenerator generator = new JsonProviderImpl().createGenerator(out);
        output.accept(generator);
        generator.flush();
        return out.toString();
    }
}
