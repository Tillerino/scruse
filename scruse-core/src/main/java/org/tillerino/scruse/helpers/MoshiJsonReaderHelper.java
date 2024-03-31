package org.tillerino.scruse.helpers;

import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonReader.Token;
import java.io.IOException;

public class MoshiJsonReaderHelper {
    public static String readDiscriminator(String descriminatorName, JsonReader parser) throws IOException {
        if (parser.peek() != Token.NAME) {
            throw new IOException("Expected field name, got " + parser.peek() + " at " + parser.getPath());
        }
        String fieldName = parser.nextName();
        if (!fieldName.equals(descriminatorName)) {
            throw new IOException(
                    "Expected field name " + descriminatorName + ", got " + fieldName + " at " + parser.getPath());
        } else if (parser.peek() != Token.STRING) {
            throw new IOException("Expected string, got " + parser.peek() + " at " + parser.getPath());
        } else {
            return parser.nextString();
        }
    }

    public static boolean isBeginObject(JsonReader parser) throws IOException {
        if (parser.peek() != Token.BEGIN_OBJECT) {
            return false;
        }
        parser.beginObject();
        return true;
    }

    public static boolean isNull(JsonReader parser) throws IOException {
        if (parser.peek() != Token.NULL) {
            return false;
        }
        parser.nextNull();
        return true;
    }
}
