package org.tillerino.scruse.converters;

import java.time.OffsetDateTime;
import org.tillerino.scruse.annotations.JsonInputConverter;
import org.tillerino.scruse.annotations.JsonOutputConverter;

public class JavaTimeInputConverters {
    @JsonOutputConverter
    public static String offsetDateTimeToString(OffsetDateTime offsetDateTime) {
        return offsetDateTime.toString();
    }

    @JsonInputConverter
    public static OffsetDateTime stringToOffsetDateTime(String string) {
        return OffsetDateTime.parse(string);
    }
}
