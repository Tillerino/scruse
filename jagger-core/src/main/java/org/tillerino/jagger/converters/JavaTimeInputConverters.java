package org.tillerino.jagger.converters;

import java.time.OffsetDateTime;
import org.tillerino.jagger.annotations.JsonInputConverter;
import org.tillerino.jagger.annotations.JsonOutputConverter;

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
