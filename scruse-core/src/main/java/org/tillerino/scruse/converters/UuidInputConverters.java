package org.tillerino.scruse.converters;

import java.util.UUID;
import org.tillerino.scruse.annotations.JsonInputConverter;

public class UuidInputConverters {
    @JsonInputConverter
    public static UUID uuidFromString(String name) {
        return UUID.fromString(name);
    }
}
