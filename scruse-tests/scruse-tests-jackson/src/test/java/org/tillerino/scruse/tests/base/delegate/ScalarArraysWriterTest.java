package org.tillerino.scruse.tests.base.delegate;

import static org.tillerino.scruse.tests.OutputUtils.assertIsEqualToDatabind;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CodeAssertions;

class ScalarArraysWriterTest {
    ScalarArraysWriter impl = new ScalarArraysWriterImpl();

    @Test
    void testBooleanArray() throws Exception {
        for (boolean[] object : SETTINGS.javaData().BOOLEAN_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBooleanArray);
        }
        assertThatCalls("writeBooleanArray", "writePrimitiveBooleanX", !SETTINGS.canWriteBooleanArrayNatively());
    }

    @Test
    void testByteArray() throws Exception {
        for (byte[] object : SETTINGS.javaData().BYTE_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeByteArray);
        }
        assertThatCalls("writeByteArray", "writePrimitiveByteX", false);
    }

    @Test
    void testShortArray() throws Exception {
        for (short[] object : SETTINGS.javaData().SHORT_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeShortArray);
        }
        assertThatCalls("writeShortArray", "writePrimitiveShortX", !SETTINGS.canWriteShortArrayNatively());
    }

    @Test
    void testIntArray() throws Exception {
        for (int[] object : SETTINGS.javaData().INT_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeIntArray);
        }
        assertThatCalls("writeIntArray", "writePrimitiveIntX", !SETTINGS.canWriteIntArrayNatively());
    }

    @Test
    void testLongArray() throws Exception {
        for (long[] object : SETTINGS.javaData().LONG_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeLongArray);
        }
        assertThatCalls("writeLongArray", "writePrimitiveLongX", !SETTINGS.canWriteLongArrayNatively());
    }

    @Test
    void testFloatArray() throws Exception {
        for (float[] object : SETTINGS.javaData().floatArrays) {
            assertIsEqualToDatabind(object, impl::writeFloatArray);
        }
        assertThatCalls("writeFloatArray", "writePrimitiveFloatX", !SETTINGS.canWriteFloatArrayNatively());
    }

    @Test
    void testDoubleArray() throws Exception {
        for (double[] object : SETTINGS.javaData().DOUBLE_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeDoubleArray);
        }
        assertThatCalls("writeDoubleArray", "writePrimitiveDoubleX", !SETTINGS.canWriteDoubleArrayNatively());
    }

    @Test
    void testBoxedBooleanArray() throws Exception {
        for (Boolean[] object : SETTINGS.javaData().BOXED_BOOLEAN_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBoxedBooleanArray);
        }
        assertThatCalls("writeBoxedBooleanArray", "writeBoxedBooleanX", true);
    }

    @Test
    void testBoxedByteArray() throws Exception {
        for (Byte[] object : SETTINGS.javaData().BOXED_BYTE_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBoxedByteArray);
        }
        assertThatCalls("writeBoxedByteArray", "writeBoxedByteX", true);
    }

    @Test
    void testBoxedShortArray() throws Exception {
        for (Short[] object : SETTINGS.javaData().BOXED_SHORT_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBoxedShortArray);
        }
        assertThatCalls("writeBoxedShortArray", "writeBoxedShortX", true);
    }

    @Test
    void testBoxedIntArray() throws Exception {
        for (Integer[] object : SETTINGS.javaData().BOXED_INT_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBoxedIntArray);
        }
        assertThatCalls("writeBoxedIntArray", "writeBoxedIntX", true);
    }

    @Test
    void testBoxedLongArray() throws Exception {
        for (Long[] object : SETTINGS.javaData().BOXED_LONG_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBoxedLongArray);
        }
        assertThatCalls("writeBoxedLongArray", "writeBoxedLongX", true);
    }

    @Test
    void testBoxedFloatArray() throws Exception {
        for (Float[] object : SETTINGS.javaData().boxedFloatArrays) {
            assertIsEqualToDatabind(object, impl::writeBoxedFloatArray);
        }
        assertThatCalls("writeBoxedFloatArray", "writeBoxedFloatX", true);
    }

    @Test
    void testBoxedDoubleArray() throws Exception {
        for (Double[] object : SETTINGS.javaData().BOXED_DOUBLE_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeBoxedDoubleArray);
        }
        assertThatCalls("writeBoxedDoubleArray", "writeBoxedDoubleX", true);
    }

    @Test
    void testStringArray() throws Exception {
        for (String[] object : SETTINGS.javaData().STRING_ARRAYS) {
            assertIsEqualToDatabind(object, impl::writeStringArray);
        }
        assertThatCalls("writeStringArray", "writeStringX", !SETTINGS.canWriteStringArrayNatively());
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
