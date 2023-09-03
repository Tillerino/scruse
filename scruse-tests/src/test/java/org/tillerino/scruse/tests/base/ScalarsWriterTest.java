package org.tillerino.scruse.tests.base;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.tillerino.scruse.tests.OutputUtils.*;

class ScalarsWriterTest {
	static final boolean[] BOOLEANS = {true, false};
	static final Boolean[] BOXED_BOOLEANS = {true, false, null};
	static final byte[] BYTES = {Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE};
	static final Byte[] BOXED_BYTES = {Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE, null};
	static final short[] SHORTS = {Short.MIN_VALUE, -1, 0, 1, Short.MAX_VALUE};
	static final Short[] BOXED_SHORTS = {Short.MIN_VALUE, -1, 0, 1, Short.MAX_VALUE, null};
	static final int[] INTS = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE};
	static final Integer[] INTEGERS = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE, null};
	static final long[] LONGS = {Long.MIN_VALUE, -1, 0, 1, Long.MAX_VALUE};
	static final Long[] BOXED_LONGS = {Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE, null};
	static final char[] CHARS = {Character.MIN_VALUE, 'a', 'A', 'ö', 'Ö', Character.MAX_VALUE};
	static final Character[] CHARACTERS = {Character.MIN_VALUE, 'a', 'A', 'ö', 'Ö', Character.MAX_VALUE, null};
	static final float[] FLOATS = {Float.MIN_VALUE, -1, 0, 1, Float.MAX_VALUE, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY};
	static final Float[] BOXED_FLOATS = {Float.MIN_VALUE, -1f, 0f, 1f, Float.MAX_VALUE, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, null};
	static final double[] DOUBLES = {Double.MIN_VALUE, -1, 0, 1, Double.MAX_VALUE, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY};
	static final Double[] BOXED_DOUBLES = {Double.MIN_VALUE, -1d, 0d, 1d, Double.MAX_VALUE, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null};
	static final String[] STRINGS = new String[]{"", " ", "a", "A", "ö", "Ö", "a b", "a\tb", "a\nb", "a\rb", "a\"b", "a\\b", "a/b", "a\b", "a\f", "a\b\f\n\r\t", null};
	ScalarsWriter impl = new ScalarsWriterImpl();

	@Test
	void testBoolean() throws IOException {
		for (boolean b : BOOLEANS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(b, impl::writeBoolean);
		}
	}

	@Test
	void testByte() throws IOException {
		for (byte b : BYTES) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(b, impl::writeByte);
		}
	}

	@Test
	void testShort() throws IOException {
		for (short s : SHORTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(s, impl::writeShort);
		}
	}

	@Test
	void testInt() throws IOException {
		for (int i : INTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(i, impl::writeInt);
		}
	}

	@Test
	void testLong() throws IOException {
		for (long l : LONGS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(l, impl::writeLong);
		}
	}

	@Test
	void testChar() throws IOException {
		for (char c : CHARS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(c, impl::writeCharacter);
		}
	}

	@Test
	void testFloat() throws IOException {
		for (float f : FLOATS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(f, impl::writeFloat);
		}
	}

	@Test
	void testDouble() throws IOException {
		for (double d : DOUBLES) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(d, impl::writeDouble);
		}
	}

	@Test
	void testBoxedBoolean() throws IOException {
		for (Boolean b : BOXED_BOOLEANS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(b, impl::writeBoxedBoolean);
		}
	}

	@Test
	void testBoxedByte() throws IOException {
		for (Byte b : BOXED_BYTES) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(b, impl::writeBoxedByte);
		}
	}

	@Test
	void testBoxedShort() throws IOException {
		for (Short s : BOXED_SHORTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(s, impl::writeBoxedShort);
		}
	}

	@Test
	void testBoxedInteger() throws IOException {
		for (Integer i : INTEGERS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(i, impl::writeBoxedInt);
		}
	}

	@Test
	void testBoxedLong() throws IOException {
		for (Long l : BOXED_LONGS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(l, impl::writeBoxedLong);
		}
	}

	@Test
	void testBoxedCharacter() throws IOException {
		for (Character c : CHARACTERS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(c, impl::writeBoxedCharacter);
		}
	}

	@Test
	void testBoxedFloat() throws IOException {
		for (Float f : BOXED_FLOATS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(f, impl::writeBoxedFloat);
		}
	}

	@Test
	void testBoxedDouble() throws IOException {
		for (Double d : BOXED_DOUBLES) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(d, impl::writeBoxedDouble);
		}
	}

	@Test
	void testString() throws IOException {
		for (String s : STRINGS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(s, impl::writeString);
		}
	}
}
