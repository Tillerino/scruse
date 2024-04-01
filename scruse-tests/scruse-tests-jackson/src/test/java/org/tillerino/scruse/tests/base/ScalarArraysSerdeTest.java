package org.tillerino.scruse.tests.base;

import static org.tillerino.scruse.tests.OutputUtils.assertIsEqualToDatabind;
import static org.tillerino.scruse.tests.OutputUtils.roundTripRecursive;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.model.AnEnum;

class ScalarArraysSerdeTest {
    ScalarArraysSerde impl = new ScalarArraysSerdeImpl();

    @Test
    void testBooleanArray() throws IOException {
        for (boolean[] object : SETTINGS.javaData().BOOLEAN_ARRAYS) {
            roundTripRecursive(
                    object, impl::writeBooleanArray, impl::readBooleanArray, new TypeReference<boolean[]>() {});
        }
    }

    @Test
    void testByteArray() throws IOException {
        for (byte[] object : SETTINGS.javaData().BYTE_ARRAYS) {
            roundTripRecursive(object, impl::writeByteArray, impl::readByteArray, new TypeReference<byte[]>() {});
        }
    }

    @Test
    void testShortArray() throws IOException {
        for (short[] object : SETTINGS.javaData().SHORT_ARRAYS) {
            roundTripRecursive(object, impl::writeShortArray, impl::readShortArray, new TypeReference<short[]>() {});
        }
    }

    @Test
    void testIntArray() throws IOException {
        for (int[] object : SETTINGS.javaData().INT_ARRAYS) {
            roundTripRecursive(object, impl::writeIntArray, impl::readIntArray, new TypeReference<int[]>() {});
        }
    }

    @Test
    void testLongArray() throws IOException {
        for (long[] object : SETTINGS.javaData().LONG_ARRAYS) {
            roundTripRecursive(object, impl::writeLongArray, impl::readLongArray, new TypeReference<long[]>() {});
        }
    }

    @Test
    void testCharArray() throws IOException {
        for (char[] object : SETTINGS.javaData().CHAR_ARRAYS) {
            roundTripRecursive(object, impl::writeCharArray, impl::readCharArray, new TypeReference<char[]>() {});
        }
    }

    @Test
    void testFloatArray() throws IOException {
        for (float[] object : SETTINGS.javaData().floatArrays) {
            roundTripRecursive(object, impl::writeFloatArray, impl::readFloatArray, new TypeReference<float[]>() {});
        }
    }

    @Test
    void testDoubleArray() throws IOException {
        for (double[] object : SETTINGS.javaData().DOUBLE_ARRAYS) {
            roundTripRecursive(object, impl::writeDoubleArray, impl::readDoubleArray, new TypeReference<double[]>() {});
        }
    }

    @Test
    void testBoxedBooleanArray() throws IOException {
        for (Boolean[] object : SETTINGS.javaData().BOXED_BOOLEAN_ARRAYS) {
            roundTripRecursive(
                    object,
                    impl::writeBoxedBooleanArray,
                    impl::readBoxedBooleanArray,
                    new TypeReference<Boolean[]>() {});
        }
    }

    @Test
    void testBoxedByteArray() throws IOException {
        for (Byte[] object : SETTINGS.javaData().BOXED_BYTE_ARRAYS) {
            roundTripRecursive(
                    object, impl::writeBoxedByteArray, impl::readBoxedByteArray, new TypeReference<Byte[]>() {});
        }
    }

    @Test
    void testBoxedShortArray() throws IOException {
        for (Short[] object : SETTINGS.javaData().BOXED_SHORT_ARRAYS) {
            roundTripRecursive(
                    object, impl::writeBoxedShortArray, impl::readBoxedShortArray, new TypeReference<Short[]>() {});
        }
    }

    @Test
    void testBoxedIntArray() throws IOException {
        for (Integer[] object : SETTINGS.javaData().BOXED_INT_ARRAYS) {
            roundTripRecursive(
                    object, impl::writeBoxedIntArray, impl::readBoxedIntArray, new TypeReference<Integer[]>() {});
        }
    }

    @Test
    void testBoxedLongArray() throws IOException {
        for (Long[] object : SETTINGS.javaData().BOXED_LONG_ARRAYS) {
            roundTripRecursive(
                    object, impl::writeBoxedLongArray, impl::readBoxedLongArray, new TypeReference<Long[]>() {});
        }
    }

    @Test
    void testBoxedCharArray() throws IOException {
        for (Character[] object : SETTINGS.javaData().BOXED_CHAR_ARRAYS) {
            // only testing output since Jackson does not like reading boxed char arrays
            assertIsEqualToDatabind(object, impl::writeBoxedCharArray);
        }
    }

    @Test
    void testBoxedFloatArray() throws IOException {
        for (Float[] object : SETTINGS.javaData().boxedFloatArrays) {
            roundTripRecursive(
                    object, impl::writeBoxedFloatArray, impl::readBoxedFloatArray, new TypeReference<Float[]>() {});
        }
    }

    @Test
    void testBoxedDoubleArray() throws IOException {
        for (Double[] object : SETTINGS.javaData().BOXED_DOUBLE_ARRAYS) {
            roundTripRecursive(
                    object, impl::writeBoxedDoubleArray, impl::readBoxedDoubleArray, new TypeReference<Double[]>() {});
        }
    }

    @Test
    void testStringArray() throws IOException {
        for (String[] object : SETTINGS.javaData().STRING_ARRAYS) {
            roundTripRecursive(object, impl::writeStringArray, impl::readStringArray, new TypeReference<String[]>() {});
        }
    }

    @Test
    void testEnumArray() throws IOException {
        for (AnEnum[] object : SETTINGS.javaData().ENUM_ARRAYS) {
            roundTripRecursive(object, impl::writeEnumArray, impl::readEnumArray, new TypeReference<AnEnum[]>() {});
        }
    }

    @Test
    void testReadingLargeIntArray() throws IOException {
        String json =
                """
			[
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
				10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
				20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
				30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
				40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
				50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
				60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
				70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
				80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
				90, 91, 92, 93, 94, 95, 96, 97, 98, 99
			]
		""";
        InputUtils.assertIsEqualToDatabind(json, impl::readIntArray, new TypeReference<int[]>() {});
    }
}
