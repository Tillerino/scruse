package org.tillerino.scruse.tests.base;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.JavaData;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.model.AnEnum;

import java.io.IOException;

import static org.tillerino.scruse.tests.OutputUtils.assertIsEqualToDatabind;

class ScalarArraysWriterTest {
	ScalarArraysWriter impl = new ScalarArraysWriterImpl();

	@Test
	void testBooleanArray() throws IOException {
		for (boolean[] object : JavaData.BOOLEAN_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBooleanArray);
		}
	}

	@Test
	void testByteArray() throws IOException {
		for (byte[] object : JavaData.BYTE_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeByteArray);
		}
	}

	@Test
	void testShortArray() throws IOException {
		for (short[] object : JavaData.SHORT_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeShortArray);
		}
	}

	@Test
	void testIntArray() throws IOException {
		for (int[] object : JavaData.INT_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeIntArray);
		}
	}

	@Test
	void testLongArray() throws IOException {
		for (long[] object : JavaData.LONG_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeLongArray);
		}
	}

	@Test
	void testCharArray() throws IOException {
		for (char[] object : JavaData.CHAR_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeCharArray);
		}
	}

	@Test
	void testFloatArray() throws IOException {
		for (float[] object : JavaData.FLOAT_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeFloatArray);
		}
	}

	@Test
	void testDoubleArray() throws IOException {
		for (double[] object : JavaData.DOUBLE_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeDoubleArray);
		}
	}

	@Test
	void testBoxedBooleanArray() throws IOException {
		for (Boolean[] object : JavaData.BOXED_BOOLEAN_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedBooleanArray);
		}
	}

	@Test
	void testBoxedByteArray() throws IOException {
		for (Byte[] object : JavaData.BOXED_BYTE_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedByteArray);
		}
	}

	@Test
	void testBoxedShortArray() throws IOException {
		for (Short[] object : JavaData.BOXED_SHORT_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedShortArray);
		}
	}

	@Test
	void testBoxedIntArray() throws IOException {
		for (Integer[] object : JavaData.BOXED_INT_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedIntArray);
		}
	}

	@Test
	void testBoxedLongArray() throws IOException {
		for (Long[] object : JavaData.BOXED_LONG_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedLongArray);
		}
	}

	@Test
	void testBoxedCharArray() throws IOException {
		for (Character[] object : JavaData.BOXED_CHAR_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedCharArray);
		}
	}

	@Test
	void testBoxedFloatArray() throws IOException {
		for (Float[] object : JavaData.BOXED_FLOAT_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedFloatArray);
		}
	}

	@Test
	void testBoxedDoubleArray() throws IOException {
		for (Double[] object : JavaData.BOXED_DOUBLE_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeBoxedDoubleArray);
		}
	}

	@Test
	void testStringArray() throws IOException {
		for (String[] object : JavaData.STRING_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeStringArray);
		}
	}

	@Test
	void testEnumArray() throws IOException {
		for (AnEnum[] object : JavaData.ENUM_ARRAYS) {
			assertIsEqualToDatabind(object, impl::writeEnumArray);
		}
	}
}
