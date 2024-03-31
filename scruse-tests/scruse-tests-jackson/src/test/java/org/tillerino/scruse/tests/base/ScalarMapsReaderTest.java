package org.tillerino.scruse.tests.base;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.JsonData;
import org.tillerino.scruse.tests.model.AnEnum;

class ScalarMapsReaderTest {
    ScalarMapsReader impl = new ScalarMapsReaderImpl();

    @Test
    void readStringBooleanMap() throws IOException {
        for (String json : JsonData.STRING_BOOLEAN_MAPS) {
            Map<String, Boolean> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readStringBooleanMap, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void readStringByteMap() throws IOException {
        for (String json : JsonData.STRING_BYTE_MAPS) {
            Map<String, Byte> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readStringByteMap, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void readStringShortMap() throws IOException {
        for (String json : JsonData.STRING_SHORT_MAPS) {
            Map<String, Short> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readStringShortMap, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void readStringIntMap() throws IOException {
        for (String json : JsonData.STRING_INT_MAPS) {
            Map<String, Integer> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readStringIntMap, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void readStringLongMap() throws IOException {
        for (String json : JsonData.STRING_LONG_MAPS) {
            Map<String, Long> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readStringLongMap, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void readStringCharMap() throws IOException {
        for (String json : JsonData.STRING_CHAR_MAPS) {
            Map<String, Character> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readStringCharMap, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void readStringFloatMap() throws IOException {
        for (String json : JsonData.STRING_FLOAT_MAPS) {
            Map<String, Float> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readStringFloatMap, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void readStringDoubleMap() throws IOException {
        for (String json : JsonData.STRING_DOUBLE_MAPS) {
            Map<String, Double> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readStringDoubleMap, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void readStringStringMap() throws IOException {
        for (String json : JsonData.STRING_STRING_MAPS) {
            Map<String, String> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readStringStringMap, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(LinkedHashMap.class);
            }
        }
    }

    @Test
    void readStringEnumMap() throws IOException {
        for (String json : JsonData.STRING_ENUM_MAPS) {
            Map<String, AnEnum> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readStringEnumMap, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(LinkedHashMap.class);
            }
        }
    }
}
