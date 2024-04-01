package org.tillerino.scruse.tests.base.delegate;

import static org.tillerino.scruse.tests.OutputUtils.roundTrip;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CodeAssertions;

class ScalarArraysSerdeTest {
    ScalarArraysWriter impl = new ScalarArraysWriterImpl();

    @Test
    void testBooleanArray() throws Exception {
        for (boolean[] object : SETTINGS.javaData().BOOLEAN_ARRAYS) {
            roundTrip(object, impl::writeBooleanArray, impl::readBooleanArray, new TypeReference<boolean[]>() {});
        }
        assertThatCalls("writeBooleanArray", "writePrimitiveBooleanX", !SETTINGS.canWriteBooleanArrayNatively());
        assertThatCalls("readBooleanArray", "readPrimitiveBooleanX", true);
    }

    @Test
    void testByteArray() throws Exception {
        for (byte[] object : SETTINGS.javaData().BYTE_ARRAYS) {
            roundTrip(object, impl::writeByteArray, impl::readByteArray, new TypeReference<byte[]>() {});
        }
        assertThatCalls("writeByteArray", "writePrimitiveByteX", false);
        assertThatCalls("readByteArray", "readPrimitiveByteX", true);
    }

    @Test
    void testShortArray() throws Exception {
        for (short[] object : SETTINGS.javaData().SHORT_ARRAYS) {
            roundTrip(object, impl::writeShortArray, impl::readShortArray, new TypeReference<short[]>() {});
        }
        assertThatCalls("writeShortArray", "writePrimitiveShortX", !SETTINGS.canWriteShortArrayNatively());
        assertThatCalls("readShortArray", "readPrimitiveShortX", true);
    }

    @Test
    void testIntArray() throws Exception {
        for (int[] object : SETTINGS.javaData().INT_ARRAYS) {
            roundTrip(object, impl::writeIntArray, impl::readIntArray, new TypeReference<int[]>() {});
        }
        assertThatCalls("writeIntArray", "writePrimitiveIntX", !SETTINGS.canWriteIntArrayNatively());
        assertThatCalls("readIntArray", "readPrimitiveIntX", !SETTINGS.canReadIntArrayNatively());
    }

    @Test
    void testLongArray() throws Exception {
        for (long[] object : SETTINGS.javaData().LONG_ARRAYS) {
            roundTrip(object, impl::writeLongArray, impl::readLongArray, new TypeReference<long[]>() {});
        }
        assertThatCalls("writeLongArray", "writePrimitiveLongX", !SETTINGS.canWriteLongArrayNatively());
        assertThatCalls("readLongArray", "readPrimitiveLongX", !SETTINGS.canReadLongArrayNatively());
    }

    @Test
    void testFloatArray() throws Exception {
        for (float[] object : SETTINGS.javaData().floatArrays) {
            roundTrip(object, impl::writeFloatArray, impl::readFloatArray, new TypeReference<float[]>() {});
        }
        assertThatCalls("writeFloatArray", "writePrimitiveFloatX", !SETTINGS.canWriteFloatArrayNatively());
        assertThatCalls("readFloatArray", "readPrimitiveFloatX", true);
    }

    @Test
    void testDoubleArray() throws Exception {
        for (double[] object : SETTINGS.javaData().DOUBLE_ARRAYS) {
            roundTrip(object, impl::writeDoubleArray, impl::readDoubleArray, new TypeReference<double[]>() {});
        }
        assertThatCalls("writeDoubleArray", "writePrimitiveDoubleX", !SETTINGS.canWriteDoubleArrayNatively());
        assertThatCalls("readDoubleArray", "readPrimitiveDoubleX", true);
    }

    @Test
    void testBoxedBooleanArray() throws Exception {
        for (Boolean[] object : SETTINGS.javaData().BOXED_BOOLEAN_ARRAYS) {
            roundTrip(
                    object,
                    impl::writeBoxedBooleanArray,
                    impl::readBoxedBooleanArray,
                    new TypeReference<Boolean[]>() {});
        }
        assertThatCalls("writeBoxedBooleanArray", "writeBoxedBooleanX", true);
        assertThatCalls("readBoxedBooleanArray", "readBoxedBooleanX", true);
    }

    @Test
    void testBoxedByteArray() throws Exception {
        for (Byte[] object : SETTINGS.javaData().BOXED_BYTE_ARRAYS) {
            roundTrip(object, impl::writeBoxedByteArray, impl::readBoxedByteArray, new TypeReference<Byte[]>() {});
        }
        assertThatCalls("writeBoxedByteArray", "writeBoxedByteX", true);
        assertThatCalls("readBoxedByteArray", "readBoxedByteX", true);
    }

    @Test
    void testBoxedShortArray() throws Exception {
        for (Short[] object : SETTINGS.javaData().BOXED_SHORT_ARRAYS) {
            roundTrip(object, impl::writeBoxedShortArray, impl::readBoxedShortArray, new TypeReference<Short[]>() {});
        }
        assertThatCalls("writeBoxedShortArray", "writeBoxedShortX", true);
        assertThatCalls("readBoxedShortArray", "readBoxedShortX", true);
    }

    @Test
    void testBoxedIntArray() throws Exception {
        for (Integer[] object : SETTINGS.javaData().BOXED_INT_ARRAYS) {
            roundTrip(object, impl::writeBoxedIntArray, impl::readBoxedIntArray, new TypeReference<Integer[]>() {});
        }
        assertThatCalls("writeBoxedIntArray", "writeBoxedIntX", true);
        assertThatCalls("readBoxedIntArray", "readBoxedIntX", true);
    }

    @Test
    void testBoxedLongArray() throws Exception {
        for (Long[] object : SETTINGS.javaData().BOXED_LONG_ARRAYS) {
            roundTrip(object, impl::writeBoxedLongArray, impl::readBoxedLongArray, new TypeReference<Long[]>() {});
        }
        assertThatCalls("writeBoxedLongArray", "writeBoxedLongX", true);
        assertThatCalls("readBoxedLongArray", "readBoxedLongX", true);
    }

    @Test
    void testBoxedFloatArray() throws Exception {
        for (Float[] object : SETTINGS.javaData().boxedFloatArrays) {
            roundTrip(object, impl::writeBoxedFloatArray, impl::readBoxedFloatArray, new TypeReference<Float[]>() {});
        }
        assertThatCalls("writeBoxedFloatArray", "writeBoxedFloatX", true);
        assertThatCalls("readBoxedFloatArray", "readBoxedFloatX", true);
    }

    @Test
    void testBoxedDoubleArray() throws Exception {
        for (Double[] object : SETTINGS.javaData().BOXED_DOUBLE_ARRAYS) {
            roundTrip(
                    object, impl::writeBoxedDoubleArray, impl::readBoxedDoubleArray, new TypeReference<Double[]>() {});
        }
        assertThatCalls("writeBoxedDoubleArray", "writeBoxedDoubleX", true);
        assertThatCalls("readBoxedDoubleArray", "readBoxedDoubleX", true);
    }

    @Test
    void testStringArray() throws Exception {
        for (String[] object : SETTINGS.javaData().STRING_ARRAYS) {
            roundTrip(object, impl::writeStringArray, impl::readStringArray, new TypeReference<String[]>() {});
        }
        assertThatCalls("writeStringArray", "writeStringX", !SETTINGS.canWriteStringArrayNatively());
        assertThatCalls("readStringArray", "readStringX", !SETTINGS.canReadStringArrayNatively());
    }

    private static void assertThatCalls(String writeBooleanArray, String writePrimitiveBooleanX, boolean doesCall)
            throws Exception {
        CodeAssertions.MethodAssert method =
                CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class).method(writeBooleanArray);
        if (doesCall) {
            method.calls(writePrimitiveBooleanX);
        } else {
            method.doesNotCall(writePrimitiveBooleanX);
        }
    }
}
