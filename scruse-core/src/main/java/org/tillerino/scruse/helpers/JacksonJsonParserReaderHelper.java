package org.tillerino.scruse.helpers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;

public class JacksonJsonParserReaderHelper {
    public static String readDiscriminator(String discriminatorName, JsonParser parser) throws IOException {
        if (parser.currentToken() != JsonToken.FIELD_NAME) {
            throw new IOException(
                    "Expected field name, got " + parser.currentToken() + " at " + parser.getCurrentLocation());
        } else if (!parser.currentName().equals(discriminatorName)) {
            throw new IOException("Expected field name " + discriminatorName + ", got " + parser.currentName() + " at "
                    + parser.getCurrentLocation());
        } else if (parser.nextToken() != JsonToken.VALUE_STRING) {
            throw new IOException(
                    "Expected string, got " + parser.currentToken() + " at " + parser.getCurrentLocation());
        } else {
            String value = parser.getText();
            parser.nextToken();
            return value;
        }
    }

    public static boolean nextIfCurrentTokenIs(JsonParser parser, JsonToken token) throws IOException {
        if (parser.currentToken() != token) {
            return false;
        }
        parser.nextToken();
        return true;
    }
}
