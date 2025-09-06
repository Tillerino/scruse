package org.tillerino.jagger.helpers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnumHelper {
    public static <T extends Enum<T>> Map<String, T> buildValuesMap(Class<T> enumType, Function<T, String> keyMapper) {
        return Arrays.stream(enumType.getEnumConstants())
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(keyMapper, Function.identity()), Collections::unmodifiableMap));
    }
}
