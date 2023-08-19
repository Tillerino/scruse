package org.tillerino.scruse.tests;

import org.junit.jupiter.api.Test;

import static org.tillerino.scruse.tests.OutputUtils.*;

class ScalarArraysWriterTest {
	ScalarArraysWriter impl = new ScalarArraysWriterImpl();

	@Test
	void testBooleanArray() throws Exception {
		boolean[][] values = {
			null,
			ScalarsWriterTest.BOOLEANS
		};
		for (boolean[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBooleanArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBooleanArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBooleanArray);
		}
	}

	@Test
	void testByteArray() throws Exception {
		byte[][] values = {
			null,
			ScalarsWriterTest.BYTES
		};
		for (byte[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeByteArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeByteArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeByteArray);
		}
	}

	@Test
	void testShortArray() throws Exception {
		short[][] values = {
			null,
			ScalarsWriterTest.SHORTS
		};
		for (short[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeShortArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeShortArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeShortArray);
		}
	}

	@Test
	void testIntArray() throws Exception {
		int[][] values = {
			null,
			ScalarsWriterTest.INTS
		};
		for (int[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeIntArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeIntArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeIntArray);
		}
	}

	@Test
	void testLongArray() throws Exception {
		long[][] values = {
			null,
			ScalarsWriterTest.LONGS
		};
		for (long[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeLongArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeLongArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeLongArray);
		}
	}

	@Test
	void testCharArray() throws Exception {
		char[][] values = {
			null,
			ScalarsWriterTest.CHARS
		};
		for (char[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeCharArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeCharArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeCharArray);
		}
	}

	@Test
	void testFloatArray() throws Exception {
		float[][] values = {
			null,
			ScalarsWriterTest.FLOATS
		};
		for (float[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeFloatArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeFloatArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeFloatArray);
		}
	}

	@Test
	void testDoubleArray() throws Exception {
		double[][] values = {
			null,
			ScalarsWriterTest.DOUBLES
		};
		for (double[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeDoubleArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeDoubleArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeDoubleArray);
		}
	}

	@Test
	void testBoxedBooleanArray() throws Exception {
		Boolean[][] values = {
			null,
			ScalarsWriterTest.BOXED_BOOLEANS
		};
		for (Boolean[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedBooleanArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedBooleanArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedBooleanArray);
		}
	}

	@Test
	void testBoxedByteArray() throws Exception {
		Byte[][] values = {
			null,
			ScalarsWriterTest.BOXED_BYTES
		};
		for (Byte[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedByteArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedByteArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedByteArray);
		}
	}

	@Test
	void testBoxedShortArray() throws Exception {
		Short[][] values = {
			null,
			ScalarsWriterTest.BOXED_SHORTS
		};
		for (Short[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedShortArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedShortArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedShortArray);
		}
	}

	@Test
	void testBoxedIntArray() throws Exception {
		Integer[][] values = {
			null,
			ScalarsWriterTest.INTEGERS
		};
		for (Integer[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedIntArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedIntArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedIntArray);
		}
	}

	@Test
	void testBoxedLongArray() throws Exception {
		Long[][] values = {
			null,
			ScalarsWriterTest.BOXED_LONGS
		};
		for (Long[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedLongArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedLongArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedLongArray);
		}
	}

	@Test
	void testBoxedCharArray() throws Exception {
		Character[][] values = {
			null,
			ScalarsWriterTest.CHARACTERS
		};
		for (Character[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedCharArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedCharArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedCharArray);
		}
	}

	@Test
	void testBoxedFloatArray() throws Exception {
		Float[][] values = {
			null,
			ScalarsWriterTest.BOXED_FLOATS
		};
		for (Float[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedFloatArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedFloatArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedFloatArray);
		}
	}

	@Test
	void testBoxedDoubleArray() throws Exception {
		Double[][] values = {
			null,
			ScalarsWriterTest.BOXED_DOUBLES
		};
		for (Double[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedDoubleArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedDoubleArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedDoubleArray);
		}
	}

	@Test
	void testStringArray() throws Exception {
		String[][] values = {
			null,
			ScalarsWriterTest.STRINGS
		};
		for (String[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeStringArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeStringArray);
		}
	}
}
