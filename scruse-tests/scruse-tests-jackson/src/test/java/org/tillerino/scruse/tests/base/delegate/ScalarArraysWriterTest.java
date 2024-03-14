package org.tillerino.scruse.tests.base.delegate;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CodeAssertions;
import org.tillerino.scruse.tests.JavaData;
import org.tillerino.scruse.tests.TestSettings;

import static org.tillerino.scruse.tests.OutputUtils.assertIsEqualToDatabind;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

class ScalarArraysWriterTest {
	ScalarArraysWriter impl = new ScalarArraysWriterImpl();

	@Test
	void testBooleanArray() throws Exception {
		for (boolean[] object : JavaData.BOOLEAN_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBooleanArray);
		}
		assertThatCalls("writeBooleanArray", "writePrimitiveBooleanX", !SETTINGS.canWriteBooleanArrayNatively());
	}

	@Test
	void testByteArray() throws Exception {
		for (byte[] object : JavaData.BYTE_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeByteArray);
		}
		assertThatCalls("writeByteArray", "writePrimitiveByteX", false);
	}

	@Test
	void testShortArray() throws Exception {
		for (short[] object : JavaData.SHORT_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeShortArray);
		}
		assertThatCalls("writeShortArray", "writePrimitiveShortX", !SETTINGS.canWriteShortArrayNatively());
	}

	@Test
	void testIntArray() throws Exception {
		for (int[] object : JavaData.INT_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeIntArray);
		}
		assertThatCalls("writeIntArray", "writePrimitiveIntX", !SETTINGS.canWriteIntArrayNatively());
	}

	@Test
	void testLongArray() throws Exception {
		for (long[] object : JavaData.LONG_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeLongArray);
		}
		assertThatCalls("writeLongArray", "writePrimitiveLongX", !SETTINGS.canWriteLongArrayNatively());
	}

	@Test
	void testFloatArray() throws Exception {
		for (float[] object : JavaData.FLOAT_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeFloatArray);
		}
		assertThatCalls("writeFloatArray", "writePrimitiveFloatX", !SETTINGS.canWriteFloatArrayNatively());
	}

	@Test
	void testDoubleArray() throws Exception {
		for (double[] object : JavaData.DOUBLE_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeDoubleArray);
		}
		assertThatCalls("writeDoubleArray", "writePrimitiveDoubleX", !SETTINGS.canWriteDoubleArrayNatively());
	}

	@Test
	void testBoxedBooleanArray() throws Exception {
		for (Boolean[] object : JavaData.BOXED_BOOLEAN_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedBooleanArray);
		}
		assertThatCalls("writeBoxedBooleanArray", "writeBoxedBooleanX", true);
	}

	@Test
	void testBoxedByteArray() throws Exception {
		for (Byte[] object : JavaData.BOXED_BYTE_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedByteArray);
		}
		assertThatCalls("writeBoxedByteArray", "writeBoxedByteX", true);
	}

	@Test
	void testBoxedShortArray() throws Exception {
		for (Short[] object : JavaData.BOXED_SHORT_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedShortArray);
		}
		assertThatCalls("writeBoxedShortArray", "writeBoxedShortX", true);
	}

	@Test
	void testBoxedIntArray() throws Exception {
		for (Integer[] object : JavaData.BOXED_INT_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedIntArray);
		}
		assertThatCalls("writeBoxedIntArray", "writeBoxedIntX", true);
	}

	@Test
	void testBoxedLongArray() throws Exception {
		for (Long[] object : JavaData.BOXED_LONG_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedLongArray);
		}
		assertThatCalls("writeBoxedLongArray", "writeBoxedLongX", true);
	}

	@Test
	void testBoxedFloatArray() throws Exception {
		for (Float[] object : JavaData.BOXED_FLOAT_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedFloatArray);
		}
		assertThatCalls("writeBoxedFloatArray", "writeBoxedFloatX", true);
	}

	@Test
	void testBoxedDoubleArray() throws Exception {
		for (Double[] object : JavaData.BOXED_DOUBLE_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedDoubleArray);
		}
		assertThatCalls("writeBoxedDoubleArray", "writeBoxedDoubleX", true);
	}

	@Test
	void testStringArray() throws Exception {
		for (String[] object : JavaData.STRING_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeStringArray);
		}
		assertThatCalls("writeStringArray", "writeStringX", !SETTINGS.canWriteStringArrayNatively());
	}

	private static void assertThatCalls(String writeBooleanArray, String writePrimitiveBooleanX, boolean doesCall) throws Exception {
		CodeAssertions.MethodAssert method = CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
				.method(writeBooleanArray);
		if (doesCall) {
			method.calls(writePrimitiveBooleanX);
		} else {
			method.doesNotCall(writePrimitiveBooleanX);
		}
	}
}
