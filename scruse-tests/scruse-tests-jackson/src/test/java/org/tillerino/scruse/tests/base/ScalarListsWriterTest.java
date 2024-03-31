package org.tillerino.scruse.tests.base;

import static org.tillerino.scruse.tests.OutputUtils.assertIsEqualToDatabind;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.JavaData;
import org.tillerino.scruse.tests.model.AnEnum;

class ScalarListsWriterTest {
    ScalarListsWriter impl = new ScalarListsWriterImpl();

    @Test
    void testBoxedBooleanList() throws IOException {
        for (List<Boolean> object : JavaData.BOOLEAN_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedBooleanList);
        }
    }

    @Test
    void testBoxedByteList() throws IOException {
        for (List<Byte> object : JavaData.BYTE_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedByteList);
        }
    }

    @Test
    void testBoxedShortList() throws IOException {
        for (List<Short> object : JavaData.SHORT_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedShortList);
        }
    }

    @Test
    void testBoxedIntList() throws IOException {
        for (List<Integer> object : JavaData.INT_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedIntList);
        }
    }

    @Test
    void testBoxedLongList() throws IOException {
        for (List<Long> object : JavaData.LONG_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedLongList);
        }
    }

    @Test
    void testBoxedCharList() throws IOException {
        for (List<Character> object : JavaData.CHAR_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedCharList);
        }
    }

    @Test
    void testBoxedFloatList() throws IOException {
        for (List<Float> object : JavaData.FLOAT_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedFloatList);
        }
    }

    @Test
    void testBoxedDoubleList() throws IOException {
        for (List<Double> object : JavaData.DOUBLE_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedDoubleList);
        }
    }

    @Test
    void testStringList() throws IOException {
        for (List<String> object : JavaData.STRING_LISTS) {
            assertIsEqualToDatabind(object, impl::writeStringList);
        }
    }

    @Test
    void testEnumList() throws IOException {
        for (List<AnEnum> object : JavaData.ENUM_LISTS) {
            assertIsEqualToDatabind(object, impl::writeEnumList);
        }
    }
}
