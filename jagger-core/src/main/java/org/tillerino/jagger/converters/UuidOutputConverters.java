package org.tillerino.jagger.converters;

import java.util.UUID;
import org.tillerino.jagger.annotations.JsonOutputConverter;

public class UuidOutputConverters {
    @JsonOutputConverter
    public static String uuidToString(UUID uuid) {
        return uuid.toString();
    }
}
