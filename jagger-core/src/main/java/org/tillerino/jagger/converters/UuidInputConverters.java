package org.tillerino.jagger.converters;

import java.util.UUID;
import org.tillerino.jagger.annotations.JsonInputConverter;

public class UuidInputConverters {
    @JsonInputConverter
    public static UUID uuidFromString(String name) {
        return UUID.fromString(name);
    }
}
