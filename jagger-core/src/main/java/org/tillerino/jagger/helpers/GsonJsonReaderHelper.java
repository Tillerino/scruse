package org.tillerino.jagger.helpers;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;

public class GsonJsonReaderHelper {
    public static String readDiscriminator(String discriminatorName, JsonReader parser) throws IOException {
        if (parser.peek() != JsonToken.NAME) {
            throw new IOException("Expected field name, got " + parser.peek() + " at " + parser.getPath());
        }
        String fieldName = parser.nextName();
        if (!fieldName.equals(discriminatorName)) {
            throw new IOException(
                    "Expected field name " + discriminatorName + ", got " + fieldName + " at " + parser.getPath());
        } else if (parser.peek() != JsonToken.STRING) {
            throw new IOException("Expected string, got " + parser.peek() + " at " + parser.getPath());
        } else {
            return parser.nextString();
        }
    }

    public static boolean isBeginObject(JsonReader parser, boolean advance) throws IOException {
        if (parser.peek() != JsonToken.BEGIN_OBJECT) {
            return false;
        }
        if (advance) {
            parser.beginObject();
        }
        return true;
    }

    public static boolean isNull(JsonReader parser, boolean advance) throws IOException {
        if (parser.peek() != JsonToken.NULL) {
            return false;
        }
        if (advance) {
            parser.nextNull();
        }
        return true;
    }
}
