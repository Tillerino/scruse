package org.tillerino.scruse.tests.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.tillerino.scruse.tests.OutputUtils.roundTrip;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.model.AnEnum;

class ScalarMapsSerdeTest {
    ScalarMapsSerde impl = new ScalarMapsSerdeImpl();

    @Test
    void testStringBooleanMap() throws IOException {
        for (Map<String, Boolean> object : SETTINGS.javaData().STRING_BOOLEAN_MAPS) {
            Map<String, Boolean> map = roundTrip(
                    object, impl::writeStringBooleanMap, impl::readStringBooleanMap, new TypeReference<>() {});
            if (map != null) {
                assertThat(map).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void testStringByteMap() throws IOException {
        for (Map<String, Byte> object : SETTINGS.javaData().STRING_BYTE_MAPS) {
            Map<String, Byte> map =
                    roundTrip(object, impl::writeStringByteMap, impl::readStringByteMap, new TypeReference<>() {});
            if (map != null) {
                assertThat(map).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void testStringShortMap() throws IOException {
        for (Map<String, Short> object : SETTINGS.javaData().STRING_SHORT_MAPS) {
            Map<String, Short> map =
                    roundTrip(object, impl::writeStringShortMap, impl::readStringShortMap, new TypeReference<>() {});
            if (map != null) {
                assertThat(map).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void testStringIntMap() throws IOException {
        for (Map<String, Integer> object : SETTINGS.javaData().STRING_INT_MAPS) {
            Map<String, Integer> map =
                    roundTrip(object, impl::writeStringIntMap, impl::readStringIntMap, new TypeReference<>() {});
            if (map != null) {
                assertThat(map).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void testStringLongMap() throws IOException {
        for (Map<String, Long> object : SETTINGS.javaData().STRING_LONG_MAPS) {
            Map<String, Long> map =
                    roundTrip(object, impl::writeStringLongMap, impl::readStringLongMap, new TypeReference<>() {});
            if (map != null) {
                assertThat(map).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void testStringCharMap() throws IOException {
        for (Map<String, Character> object : SETTINGS.javaData().STRING_CHAR_MAPS) {
            Map<String, Character> map =
                    roundTrip(object, impl::writeStringCharMap, impl::readStringCharMap, new TypeReference<>() {});
            if (map != null) {
                assertThat(map).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void testStringFloatMap() throws IOException {
        for (Map<String, Float> object : SETTINGS.javaData().STRING_FLOAT_MAPS) {
            Map<String, Float> map =
                    roundTrip(object, impl::writeStringFloatMap, impl::readStringFloatMap, new TypeReference<>() {});
            if (map != null) {
                assertThat(map).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void testStringDoubleMap() throws IOException {
        for (Map<String, Double> object : SETTINGS.javaData().STRING_DOUBLE_MAPS) {
            Map<String, Double> map =
                    roundTrip(object, impl::writeStringDoubleMap, impl::readStringDoubleMap, new TypeReference<>() {});
            if (map != null) {
                assertThat(map).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void testStringStringMap() throws IOException {
        for (Map<String, String> object : SETTINGS.javaData().STRING_STRING_MAPS) {
            Map<String, String> map =
                    roundTrip(object, impl::writeStringStringMap, impl::readStringStringMap, new TypeReference<>() {});
            if (map != null) {
                assertThat(map).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void testStringEnumMap() throws IOException {
        for (Map<String, AnEnum> object : SETTINGS.javaData().STRING_ENUM_MAPS) {
            Map<String, AnEnum> map =
                    roundTrip(object, impl::writeStringEnumMap, impl::readStringEnumMap, new TypeReference<>() {});
            if (map != null) {
                assertThat(map).isInstanceOf(LinkedHashMap.class);
            }
        }
    }
}
