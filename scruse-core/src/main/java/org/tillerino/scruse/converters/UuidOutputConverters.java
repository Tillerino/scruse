package org.tillerino.scruse.converters;

import java.util.UUID;
import org.tillerino.scruse.annotations.JsonOutputConverter;

public class UuidOutputConverters {
    @JsonOutputConverter
    public static String uuidToString(UUID uuid) {
        return uuid.toString();
    }
}
