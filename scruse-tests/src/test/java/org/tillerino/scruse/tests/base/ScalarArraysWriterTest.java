package org.tillerino.scruse.tests.base;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.JavaData;

import java.io.IOException;

import static org.tillerino.scruse.tests.OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind;

class ScalarArraysWriterTest {
	ScalarArraysWriter impl = new ScalarArraysWriterImpl();

	@Test
	void testBooleanArray() throws IOException {
		for (boolean[] object : JavaData.BOOLEAN_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBooleanArray);
		}
	}

	@Test
	void testByteArray() throws IOException {
		for (byte[] object : JavaData.BYTE_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeByteArray);
		}
	}

	@Test
	void testShortArray() throws IOException {
		for (short[] object : JavaData.SHORT_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeShortArray);
		}
	}

	@Test
	void testIntArray() throws IOException {
		for (int[] object : JavaData.INT_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeIntArray);
		}
	}

	@Test
	void testLongArray() throws IOException {
		for (long[] object : JavaData.LONG_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeLongArray);
		}
	}

	@Test
	void testCharArray() throws IOException {
		for (char[] object : JavaData.CHAR_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeCharArray);
		}
	}

	@Test
	void testFloatArray() throws IOException {
		for (float[] object : JavaData.FLOAT_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeFloatArray);
		}
	}

	@Test
	void testDoubleArray() throws IOException {
		for (double[] object : JavaData.DOUBLE_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeDoubleArray);
		}
	}

	@Test
	void testBoxedBooleanArray() throws IOException {
		for (Boolean[] object : JavaData.BOXED_BOOLEAN_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedBooleanArray);
		}
	}

	@Test
	void testBoxedByteArray() throws IOException {
		for (Byte[] object : JavaData.BOXED_BYTE_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedByteArray);
		}
	}

	@Test
	void testBoxedShortArray() throws IOException {
		for (Short[] object : JavaData.BOXED_SHORT_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedShortArray);
		}
	}

	@Test
	void testBoxedIntArray() throws IOException {
		for (Integer[] object : JavaData.BOXED_INT_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedIntArray);
		}
	}

	@Test
	void testBoxedLongArray() throws IOException {
		for (Long[] object : JavaData.BOXED_LONG_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedLongArray);
		}
	}

	@Test
	void testBoxedCharArray() throws IOException {
		for (Character[] object : JavaData.BOXED_CHAR_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedCharArray);
		}
	}

	@Test
	void testBoxedFloatArray() throws IOException {
		for (Float[] object : JavaData.BOXED_FLOAT_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedFloatArray);
		}
	}

	@Test
	void testBoxedDoubleArray() throws IOException {
		for (Double[] object : JavaData.BOXED_DOUBLE_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedDoubleArray);
		}
	}

	@Test
	void testStringArray() throws IOException {
		for (String[] object : JavaData.STRING_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringArray);
		}
	}
}
