package org.tillerino.jagger.converters;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import org.tillerino.jagger.annotations.JsonOutputConverter;

/** Output converters for the optional types. */
public class OptionalOutputConverters {
    @JsonOutputConverter
    public static <T> T optionalToNullable(Optional<T> optional) {
        return optional.orElse(null);
    }

    @JsonOutputConverter
    public static Integer optionalIntToBoxed(OptionalInt optional) {
        return optional.isPresent() ? optional.getAsInt() : null;
    }

    @JsonOutputConverter
    public static Long optionalLongToBoxed(OptionalLong optional) {
        return optional.isPresent() ? optional.getAsLong() : null;
    }

    @JsonOutputConverter
    public static Double optionalDoubleToBoxed(OptionalDouble optional) {
        return optional.isPresent() ? optional.getAsDouble() : null;
    }
}
