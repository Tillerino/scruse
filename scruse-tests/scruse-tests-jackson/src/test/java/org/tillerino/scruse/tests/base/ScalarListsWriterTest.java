package org.tillerino.scruse.tests.base;

import static org.tillerino.scruse.tests.OutputUtils.assertIsEqualToDatabind;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.model.AnEnum;

class ScalarListsWriterTest {
    ScalarListsWriter impl = new ScalarListsWriterImpl();

    @Test
    void testBoxedBooleanList() throws IOException {
        for (List<Boolean> object : SETTINGS.javaData().BOOLEAN_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedBooleanList);
        }
    }

    @Test
    void testBoxedByteList() throws IOException {
        for (List<Byte> object : SETTINGS.javaData().BYTE_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedByteList);
        }
    }

    @Test
    void testBoxedShortList() throws IOException {
        for (List<Short> object : SETTINGS.javaData().SHORT_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedShortList);
        }
    }

    @Test
    void testBoxedIntList() throws IOException {
        for (List<Integer> object : SETTINGS.javaData().INT_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedIntList);
        }
    }

    @Test
    void testBoxedLongList() throws IOException {
        for (List<Long> object : SETTINGS.javaData().LONG_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedLongList);
        }
    }

    @Test
    void testBoxedCharList() throws IOException {
        for (List<Character> object : SETTINGS.javaData().CHAR_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedCharList);
        }
    }

    @Test
    void testBoxedFloatList() throws IOException {
        for (List<Float> object : SETTINGS.javaData().floatLists) {
            assertIsEqualToDatabind(object, impl::writeBoxedFloatList);
        }
    }

    @Test
    void testBoxedDoubleList() throws IOException {
        for (List<Double> object : SETTINGS.javaData().DOUBLE_LISTS) {
            assertIsEqualToDatabind(object, impl::writeBoxedDoubleList);
        }
    }

    @Test
    void testStringList() throws IOException {
        for (List<String> object : SETTINGS.javaData().STRING_LISTS) {
            assertIsEqualToDatabind(object, impl::writeStringList);
        }
    }

    @Test
    void testEnumList() throws IOException {
        for (List<AnEnum> object : SETTINGS.javaData().ENUM_LISTS) {
            assertIsEqualToDatabind(object, impl::writeEnumList);
        }
    }
}
