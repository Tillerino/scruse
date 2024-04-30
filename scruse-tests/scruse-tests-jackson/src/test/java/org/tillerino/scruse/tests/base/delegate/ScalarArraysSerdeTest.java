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
            roundTrip(object, impl::writeBooleanArrayX, impl::readBooleanArrayX, new TypeReference<boolean[]>() {});
        }
        assertThatCalls("writeBooleanArrayX", "writePrimitiveBooleanX", !SETTINGS.canWriteBooleanArrayNatively());
        assertThatCalls("readBooleanArrayX", "readPrimitiveBooleanX", true);
    }

    @Test
    void testByteArray() throws Exception {
        for (byte[] object : SETTINGS.javaData().BYTE_ARRAYS) {
            roundTrip(object, impl::writeByteArrayX, impl::readByteArrayX, new TypeReference<byte[]>() {});
        }
        assertThatCalls("writeByteArrayX", "writePrimitiveByteX", false);
        assertThatCalls("readByteArrayX", "readPrimitiveByteX", true);
    }

    @Test
    void testShortArray() throws Exception {
        for (short[] object : SETTINGS.javaData().SHORT_ARRAYS) {
            roundTrip(object, impl::writeShortArrayX, impl::readShortArrayX, new TypeReference<short[]>() {});
        }
        assertThatCalls("writeShortArrayX", "writePrimitiveShortX", !SETTINGS.canWriteShortArrayNatively());
        assertThatCalls("readShortArrayX", "readPrimitiveShortX", true);
    }

    @Test
    void testIntArray() throws Exception {
        for (int[] object : SETTINGS.javaData().INT_ARRAYS) {
            roundTrip(object, impl::writeIntArrayX, impl::readIntArrayX, new TypeReference<int[]>() {});
        }
        assertThatCalls("writeIntArrayX", "writePrimitiveIntX", !SETTINGS.canWriteIntArrayNatively());
        assertThatCalls("readIntArrayX", "readPrimitiveIntX", !SETTINGS.canReadIntArrayNatively());
    }

    @Test
    void testLongArray() throws Exception {
        for (long[] object : SETTINGS.javaData().LONG_ARRAYS) {
            roundTrip(object, impl::writeLongArrayX, impl::readLongArrayX, new TypeReference<long[]>() {});
        }
        assertThatCalls("writeLongArrayX", "writePrimitiveLongX", !SETTINGS.canWriteLongArrayNatively());
        assertThatCalls("readLongArrayX", "readPrimitiveLongX", !SETTINGS.canReadLongArrayNatively());
    }

    @Test
    void testFloatArray() throws Exception {
        for (float[] object : SETTINGS.javaData().floatArrays) {
            roundTrip(object, impl::writeFloatArrayX, impl::readFloatArrayX, new TypeReference<float[]>() {});
        }
        assertThatCalls("writeFloatArrayX", "writePrimitiveFloatX", !SETTINGS.canWriteFloatArrayNatively());
        assertThatCalls("readFloatArrayX", "readPrimitiveFloatX", true);
    }

    @Test
    void testDoubleArray() throws Exception {
        for (double[] object : SETTINGS.javaData().DOUBLE_ARRAYS) {
            roundTrip(object, impl::writeDoubleArrayX, impl::readDoubleArrayX, new TypeReference<double[]>() {});
        }
        assertThatCalls("writeDoubleArrayX", "writePrimitiveDoubleX", !SETTINGS.canWriteDoubleArrayNatively());
        assertThatCalls("readDoubleArrayX", "readPrimitiveDoubleX", true);
    }

    @Test
    void testBoxedBooleanArray() throws Exception {
        for (Boolean[] object : SETTINGS.javaData().BOXED_BOOLEAN_ARRAYS) {
            roundTrip(
                    object,
                    impl::writeBoxedBooleanArrayX,
                    impl::readBoxedBooleanArrayX,
                    new TypeReference<Boolean[]>() {});
        }
        assertThatCalls("writeBoxedBooleanArrayX", "writeBoxedBooleanX", true);
        assertThatCalls("readBoxedBooleanArrayX", "readBoxedBooleanX", true);
    }

    @Test
    void testBoxedByteArray() throws Exception {
        for (Byte[] object : SETTINGS.javaData().BOXED_BYTE_ARRAYS) {
            roundTrip(object, impl::writeBoxedByteArrayX, impl::readBoxedByteArrayX, new TypeReference<Byte[]>() {});
        }
        assertThatCalls("writeBoxedByteArrayX", "writeBoxedByteX", true);
        assertThatCalls("readBoxedByteArrayX", "readBoxedByteX", true);
    }

    @Test
    void testBoxedShortArray() throws Exception {
        for (Short[] object : SETTINGS.javaData().BOXED_SHORT_ARRAYS) {
            roundTrip(object, impl::writeBoxedShortArrayX, impl::readBoxedShortArrayX, new TypeReference<Short[]>() {});
        }
        assertThatCalls("writeBoxedShortArrayX", "writeBoxedShortX", true);
        assertThatCalls("readBoxedShortArrayX", "readBoxedShortX", true);
    }

    @Test
    void testBoxedIntArray() throws Exception {
        for (Integer[] object : SETTINGS.javaData().BOXED_INT_ARRAYS) {
            roundTrip(object, impl::writeBoxedIntArrayX, impl::readBoxedIntArrayX, new TypeReference<Integer[]>() {});
        }
        assertThatCalls("writeBoxedIntArrayX", "writeBoxedIntX", true);
        assertThatCalls("readBoxedIntArrayX", "readBoxedIntX", true);
    }

    @Test
    void testBoxedLongArray() throws Exception {
        for (Long[] object : SETTINGS.javaData().BOXED_LONG_ARRAYS) {
            roundTrip(object, impl::writeBoxedLongArrayX, impl::readBoxedLongArrayX, new TypeReference<Long[]>() {});
        }
        assertThatCalls("writeBoxedLongArrayX", "writeBoxedLongX", true);
        assertThatCalls("readBoxedLongArrayX", "readBoxedLongX", true);
    }

    @Test
    void testBoxedFloatArray() throws Exception {
        for (Float[] object : SETTINGS.javaData().boxedFloatArrays) {
            roundTrip(object, impl::writeBoxedFloatArrayX, impl::readBoxedFloatArrayX, new TypeReference<Float[]>() {});
        }
        assertThatCalls("writeBoxedFloatArrayX", "writeBoxedFloatX", true);
        assertThatCalls("readBoxedFloatArrayX", "readBoxedFloatX", true);
    }

    @Test
    void testBoxedDoubleArray() throws Exception {
        for (Double[] object : SETTINGS.javaData().BOXED_DOUBLE_ARRAYS) {
            roundTrip(
                    object,
                    impl::writeBoxedDoubleArrayX,
                    impl::readBoxedDoubleArrayX,
                    new TypeReference<Double[]>() {});
        }
        assertThatCalls("writeBoxedDoubleArrayX", "writeBoxedDoubleX", true);
        assertThatCalls("readBoxedDoubleArrayX", "readBoxedDoubleX", true);
    }

    @Test
    void testStringArray() throws Exception {
        for (String[] object : SETTINGS.javaData().STRING_ARRAYS) {
            roundTrip(object, impl::writeStringArrayX, impl::readStringArrayX, new TypeReference<String[]>() {});
        }
        assertThatCalls("writeStringArrayX", "writeStringX", !SETTINGS.canWriteStringArrayNatively());
        assertThatCalls("readStringArrayX", "readStringX", !SETTINGS.canReadStringArrayNatively());
    }

    private static void assertThatCalls(String caller, String callee, boolean doesCall) throws Exception {
        CodeAssertions.MethodAssert method =
                CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class).method(caller);
        if (doesCall) {
            method.calls(callee);
        } else {
            method.doesNotCall(callee);
        }
    }
}
