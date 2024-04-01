package org.tillerino.scruse.tests.base;

import static org.tillerino.scruse.tests.OutputUtils.assertIsEqualToDatabind;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.model.AnEnum;

class ScalarArraysWriterTest {
    ScalarArraysWriter impl = new ScalarArraysWriterImpl();

    @Test
    void testBooleanArray() throws IOException {
        for (boolean[] object : SETTINGS.javaData().BOOLEAN_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBooleanArray);
        }
    }

    @Test
    void testByteArray() throws IOException {
        for (byte[] object : SETTINGS.javaData().BYTE_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeByteArray);
        }
    }

    @Test
    void testShortArray() throws IOException {
        for (short[] object : SETTINGS.javaData().SHORT_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeShortArray);
        }
    }

    @Test
    void testIntArray() throws IOException {
        for (int[] object : SETTINGS.javaData().INT_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeIntArray);
        }
    }

    @Test
    void testLongArray() throws IOException {
        for (long[] object : SETTINGS.javaData().LONG_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeLongArray);
        }
    }

    @Test
    void testCharArray() throws IOException {
        for (char[] object : SETTINGS.javaData().CHAR_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeCharArray);
        }
    }

    @Test
    void testFloatArray() throws IOException {
        for (float[] object : SETTINGS.javaData().floatArrays) {
            assertIsEqualToDatabind(object, impl::writeFloatArray);
        }
    }

    @Test
    void testDoubleArray() throws IOException {
        for (double[] object : SETTINGS.javaData().DOUBLE_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeDoubleArray);
        }
    }

    @Test
    void testBoxedBooleanArray() throws IOException {
        for (Boolean[] object : SETTINGS.javaData().BOXED_BOOLEAN_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBoxedBooleanArray);
        }
    }

    @Test
    void testBoxedByteArray() throws IOException {
        for (Byte[] object : SETTINGS.javaData().BOXED_BYTE_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBoxedByteArray);
        }
    }

    @Test
    void testBoxedShortArray() throws IOException {
        for (Short[] object : SETTINGS.javaData().BOXED_SHORT_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBoxedShortArray);
        }
    }

    @Test
    void testBoxedIntArray() throws IOException {
        for (Integer[] object : SETTINGS.javaData().BOXED_INT_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBoxedIntArray);
        }
    }

    @Test
    void testBoxedLongArray() throws IOException {
        for (Long[] object : SETTINGS.javaData().BOXED_LONG_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBoxedLongArray);
        }
    }

    @Test
    void testBoxedCharArray() throws IOException {
        for (Character[] object : SETTINGS.javaData().BOXED_CHAR_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBoxedCharArray);
        }
    }

    @Test
    void testBoxedFloatArray() throws IOException {
        for (Float[] object : SETTINGS.javaData().boxedFloatArrays) {
            assertIsEqualToDatabind(object, impl::writeBoxedFloatArray);
        }
    }

    @Test
    void testBoxedDoubleArray() throws IOException {
        for (Double[] object : SETTINGS.javaData().BOXED_DOUBLE_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBoxedDoubleArray);
        }
    }

    @Test
    void testStringArray() throws IOException {
        for (String[] object : SETTINGS.javaData().STRING_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeStringArray);
        }
    }

    @Test
    void testEnumArray() throws IOException {
        for (AnEnum[] object : SETTINGS.javaData().ENUM_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeEnumArray);
        }
    }
}
