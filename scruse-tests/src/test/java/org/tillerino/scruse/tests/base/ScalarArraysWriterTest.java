package org.tillerino.scruse.tests.base;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.tillerino.scruse.tests.OutputUtils.*;

class ScalarArraysWriterTest {
	ScalarArraysWriter impl = new ScalarArraysWriterImpl();

	@Test
	void testBooleanArray() throws IOException {
		boolean[][] values = {
			null,
			ScalarsWriterTest.BOOLEANS
		};
		for (boolean[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBooleanArray);
		}
	}

	@Test
	void testByteArray() throws IOException {
		byte[][] values = {
			null,
			ScalarsWriterTest.BYTES
		};
		for (byte[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeByteArray);
		}
	}

	@Test
	void testShortArray() throws IOException {
		short[][] values = {
			null,
			ScalarsWriterTest.SHORTS
		};
		for (short[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeShortArray);
		}
	}

	@Test
	void testIntArray() throws IOException {
		int[][] values = {
			null,
			ScalarsWriterTest.INTS
		};
		for (int[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeIntArray);
		}
	}

	@Test
	void testLongArray() throws IOException {
		long[][] values = {
			null,
			ScalarsWriterTest.LONGS
		};
		for (long[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeLongArray);
		}
	}

	@Test
	void testCharArray() throws IOException {
		char[][] values = {
			null,
			ScalarsWriterTest.CHARS
		};
		for (char[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeCharArray);
		}
	}

	@Test
	void testFloatArray() throws IOException {
		float[][] values = {
			null,
			ScalarsWriterTest.FLOATS
		};
		for (float[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeFloatArray);
		}
	}

	@Test
	void testDoubleArray() throws IOException {
		double[][] values = {
			null,
			ScalarsWriterTest.DOUBLES
		};
		for (double[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeDoubleArray);
		}
	}

	@Test
	void testBoxedBooleanArray() throws IOException {
		Boolean[][] values = {
			null,
			ScalarsWriterTest.BOXED_BOOLEANS
		};
		for (Boolean[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedBooleanArray);
		}
	}

	@Test
	void testBoxedByteArray() throws IOException {
		Byte[][] values = {
			null,
			ScalarsWriterTest.BOXED_BYTES
		};
		for (Byte[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedByteArray);
		}
	}

	@Test
	void testBoxedShortArray() throws IOException {
		Short[][] values = {
			null,
			ScalarsWriterTest.BOXED_SHORTS
		};
		for (Short[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedShortArray);
		}
	}

	@Test
	void testBoxedIntArray() throws IOException {
		Integer[][] values = {
			null,
			ScalarsWriterTest.INTEGERS
		};
		for (Integer[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedIntArray);
		}
	}

	@Test
	void testBoxedLongArray() throws IOException {
		Long[][] values = {
			null,
			ScalarsWriterTest.BOXED_LONGS
		};
		for (Long[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedLongArray);
		}
	}

	@Test
	void testBoxedCharArray() throws IOException {
		Character[][] values = {
			null,
			ScalarsWriterTest.CHARACTERS
		};
		for (Character[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedCharArray);
		}
	}

	@Test
	void testBoxedFloatArray() throws IOException {
		Float[][] values = {
			null,
			ScalarsWriterTest.BOXED_FLOATS
		};
		for (Float[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedFloatArray);
		}
	}

	@Test
	void testBoxedDoubleArray() throws IOException {
		Double[][] values = {
			null,
			ScalarsWriterTest.BOXED_DOUBLES
		};
		for (Double[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedDoubleArray);
		}
	}

	@Test
	void testStringArray() throws IOException {
		String[][] values = {
			null,
			ScalarsWriterTest.STRINGS
		};
		for (String[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringArray);
		}
	}
}
