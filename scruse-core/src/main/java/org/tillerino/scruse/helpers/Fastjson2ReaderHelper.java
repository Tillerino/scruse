package org.tillerino.scruse.helpers;

import com.alibaba.fastjson2.JSONReader;
import java.io.IOException;

public class Fastjson2ReaderHelper {
    public static String readDiscriminator(String discriminatorName, JSONReader parser) throws IOException {
        String fieldName = parser.readFieldName();
        if (fieldName == null) {
            throw new IOException("Expected field name, got " + parser.current());
        }
        if (!fieldName.equals(discriminatorName)) {
            throw new IOException("Expected field name " + discriminatorName + ", got " + fieldName);
        } else if (!parser.isString()) {
            throw new IOException("Expected string, got " + parser.current());
        } else {
            return parser.readString();
        }
    }
}
