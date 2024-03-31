package org.tillerino.scruse.tests.base;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.JsonData;
import org.tillerino.scruse.tests.model.AnEnum;

class ScalarListsReaderTest {
    ScalarListsReader impl = new ScalarListsReaderImpl();

    @Test
    void testBooleanList() throws IOException {
        for (String json : JsonData.BOXED_BOOLEAN_ARRAYS) {
            List<Boolean> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readBooleanList, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(ArrayList.class);
            }
        }
    }

    @Test
    void testByteList() throws IOException {
        for (String json : JsonData.BOXED_BYTE_ARRAYS) {
            List<Byte> list = InputUtils.assertIsEqualToDatabind(json, impl::readByteList, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(ArrayList.class);
            }
        }
    }

    @Test
    void testShortList() throws IOException {
        for (String json : JsonData.BOXED_SHORT_ARRAYS) {
            List<Short> list = InputUtils.assertIsEqualToDatabind(json, impl::readShortList, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(ArrayList.class);
            }
        }
    }

    @Test
    void testIntegerList() throws IOException {
        for (String json : JsonData.BOXED_INT_ARRAYS) {
            List<Integer> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readIntegerList, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(ArrayList.class);
            }
        }
    }

    @Test
    void testLongList() throws IOException {
        for (String json : JsonData.BOXED_LONG_ARRAYS) {
            List<Long> list = InputUtils.assertIsEqualToDatabind(json, impl::readLongList, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(ArrayList.class);
            }
        }
    }

    @Test
    void testFloatList() throws IOException {
        for (String json : JsonData.BOXED_FLOAT_ARRAYS) {
            List<Float> list = InputUtils.assertIsEqualToDatabind(json, impl::readFloatList, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(ArrayList.class);
            }
        }
    }

    @Test
    void testDoubleList() throws IOException {
        for (String json : JsonData.BOXED_DOUBLE_ARRAYS) {
            List<Double> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readDoubleList, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(ArrayList.class);
            }
        }
    }

    @Test
    void testCharList() throws IOException {
        String[] jsons = {
            "null", "[]", "[null]", "[\"a\"]", "[\"b\"]", "[\"a\",\"b\",null]",
        };
        for (String json : jsons) {
            List<Character> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readCharacterList, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(ArrayList.class);
            }
        }
    }

    @Test
    void testStringList() throws IOException {
        for (String json : JsonData.STRING_ARRAYS) {
            List<String> list =
                    InputUtils.assertIsEqualToDatabind(json, impl::readStringList, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(ArrayList.class);
            }
        }
    }

    @Test
    void testEnumList() throws IOException {
        for (String json : JsonData.ENUM_ARRAYS) {
            List<AnEnum> list = InputUtils.assertIsEqualToDatabind(json, impl::readEnumList, new TypeReference<>() {});
            if (list != null) {
                assertThat(list).isInstanceOf(ArrayList.class);
            }
        }
    }
}
