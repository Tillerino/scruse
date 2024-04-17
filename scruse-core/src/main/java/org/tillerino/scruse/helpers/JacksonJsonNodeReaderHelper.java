package org.tillerino.scruse.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class JacksonJsonNodeReaderHelper {
    public static String readDiscriminator(String discriminatorName, JsonNode parser) throws IOException {
        if (!parser.isObject()) {
            throw new IOException("Expected object, got " + parser.getNodeType());
        }
        JsonNode field = parser.get(discriminatorName);
        if (field == null) {
            throw new IOException("Expected field name " + discriminatorName + ", got null");
        } else if (!field.isTextual()) {
            throw new IOException("Expected string, got " + field.getNodeType());
        } else {
            return field.asText();
        }
    }
}
