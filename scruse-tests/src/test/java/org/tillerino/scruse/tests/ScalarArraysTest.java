package org.tillerino.scruse.tests;

import org.junit.jupiter.api.Test;

import static org.tillerino.scruse.tests.OutputUtils.*;

public class ScalarArraysTest {
	ScalarArraysWriter impl = new ScalarArraysWriterImpl();

	@Test
	public void testPrimitiveBooleanArray() throws Exception {
		boolean[][] values = {
			null,
			ScalarsTests.BOOLEANS
		};
		for (boolean[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writePrimitiveBooleanArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writePrimitiveBooleanArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writePrimitiveBooleanArray);
		}
	}

	@Test
	public void testPrimitiveByteArray() throws Exception {
		byte[][] values = {
			null,
			ScalarsTests.BYTES
		};
		for (byte[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writePrimitiveByteArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writePrimitiveByteArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writePrimitiveByteArray);
		}
	}

	@Test
	public void testPrimitiveShortArray() throws Exception {
		short[][] values = {
			null,
			ScalarsTests.SHORTS
		};
		for (short[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writePrimitiveShortArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writePrimitiveShortArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writePrimitiveShortArray);
		}
	}

	@Test
	public void testPrimitiveIntArray() throws Exception {
		int[][] values = {
			null,
			ScalarsTests.INTS
		};
		for (int[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writePrimitiveIntArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writePrimitiveIntArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writePrimitiveIntArray);
		}
	}

	@Test
	public void testPrimitiveLongArray() throws Exception {
		long[][] values = {
			null,
			ScalarsTests.LONGS
		};
		for (long[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writePrimitiveLongArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writePrimitiveLongArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writePrimitiveLongArray);
		}
	}

	@Test
	public void testPrimitiveCharArray() throws Exception {
		char[][] values = {
			null,
			ScalarsTests.CHARS
		};
		for (char[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writePrimitiveCharArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writePrimitiveCharArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writePrimitiveCharArray);
		}
	}

	@Test
	public void testPrimitiveFloatArray() throws Exception {
		float[][] values = {
			null,
			ScalarsTests.FLOATS
		};
		for (float[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writePrimitiveFloatArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writePrimitiveFloatArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writePrimitiveFloatArray);
		}
	}

	@Test
	public void testPrimitiveDoubleArray() throws Exception {
		double[][] values = {
			null,
			ScalarsTests.DOUBLES
		};
		for (double[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writePrimitiveDoubleArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writePrimitiveDoubleArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writePrimitiveDoubleArray);
		}
	}

	@Test
	public void testBoxedBooleanArray() throws Exception {
		Boolean[][] values = {
			null,
			ScalarsTests.BOXED_BOOLEANS
		};
		for (Boolean[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedBooleanArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedBooleanArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedBooleanArray);
		}
	}

	@Test
	public void testBoxedByteArray() throws Exception {
		Byte[][] values = {
			null,
			ScalarsTests.BOXED_BYTES
		};
		for (Byte[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedByteArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedByteArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedByteArray);
		}
	}

	@Test
	public void testBoxedShortArray() throws Exception {
		Short[][] values = {
			null,
			ScalarsTests.BOXED_SHORTS
		};
		for (Short[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedShortArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedShortArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedShortArray);
		}
	}

	@Test
	public void testBoxedIntArray() throws Exception {
		Integer[][] values = {
			null,
			ScalarsTests.INTEGERS
		};
		for (Integer[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedIntArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedIntArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedIntArray);
		}
	}

	@Test
	public void testBoxedLongArray() throws Exception {
		Long[][] values = {
			null,
			ScalarsTests.BOXED_LONGS
		};
		for (Long[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedLongArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedLongArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedLongArray);
		}
	}

	@Test
	public void testBoxedCharArray() throws Exception {
		Character[][] values = {
			null,
			ScalarsTests.CHARACTERS
		};
		for (Character[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedCharArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedCharArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedCharArray);
		}
	}

	@Test
	public void testBoxedFloatArray() throws Exception {
		Float[][] values = {
			null,
			ScalarsTests.BOXED_FLOATS
		};
		for (Float[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedFloatArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedFloatArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedFloatArray);
		}
	}

	@Test
	public void testBoxedDoubleArray() throws Exception {
		Double[][] values = {
			null,
			ScalarsTests.BOXED_DOUBLES
		};
		for (Double[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedDoubleArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedDoubleArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedDoubleArray);
		}
	}

	@Test
	public void testStringArray() throws Exception {
		String[][] values = {
			null,
			ScalarsTests.STRINGS
		};
		for (String[] object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringArray);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeStringArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeStringArray);
		}
	}
}
