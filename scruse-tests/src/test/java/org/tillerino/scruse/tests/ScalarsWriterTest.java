package org.tillerino.scruse.tests;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.tillerino.scruse.tests.OutputUtils.*;

public class ScalarsWriterTest {
	public static final boolean[] BOOLEANS = {true, false};
	public static final Boolean[] BOXED_BOOLEANS = {true, false, null};
	public static final byte[] BYTES = {Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE};
	public static final Byte[] BOXED_BYTES = {Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE, null};
	public static final short[] SHORTS = {Short.MIN_VALUE, -1, 0, 1, Short.MAX_VALUE};
	public static final Short[] BOXED_SHORTS = {Short.MIN_VALUE, -1, 0, 1, Short.MAX_VALUE, null};
	public static final int[] INTS = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE};
	public static final Integer[] INTEGERS = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE, null};
	public static final long[] LONGS = {Long.MIN_VALUE, -1, 0, 1, Long.MAX_VALUE};
	public static final Long[] BOXED_LONGS = {Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE, null};
	public static final char[] CHARS = {Character.MIN_VALUE, 'a', 'A', 'ö', 'Ö', Character.MAX_VALUE};
	public static final Character[] CHARACTERS = {Character.MIN_VALUE, 'a', 'A', 'ö', 'Ö', Character.MAX_VALUE, null};
	public static final float[] FLOATS = {Float.MIN_VALUE, -1, 0, 1, Float.MAX_VALUE, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY};
	public static final Float[] BOXED_FLOATS = {Float.MIN_VALUE, -1f, 0f, 1f, Float.MAX_VALUE, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, null};
	public static final double[] DOUBLES = {Double.MIN_VALUE, -1, 0, 1, Double.MAX_VALUE, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY};
	public static final Double[] BOXED_DOUBLES = {Double.MIN_VALUE, -1d, 0d, 1d, Double.MAX_VALUE, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null};
	public static final String[] STRINGS = new String[]{"", " ", "a", "A", "ö", "Ö", "a b", "a\tb", "a\nb", "a\rb", "a\"b", "a\\b", "a/b", "a\b", "a\f", "a\b\f\n\r\t", null};
	ScalarsWriter impl = new ScalarsWriterImpl();

	@Test
	void primitiveBoolean() throws IOException {
		for (boolean b : BOOLEANS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(b, impl::writeBoolean);
			assertThatGsonJsonWriterIsEqualToDatabind(b, impl::writeBoolean);
			assertThatJacksonJsonNodeIsEqualToDatabind(b, impl::writeBoolean);
		}
	}

	@Test
	void primitiveByte() throws IOException {
		for (byte b : BYTES) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(b, impl::writeByte);
			assertThatGsonJsonWriterIsEqualToDatabind(b, impl::writeByte);
			assertThatJacksonJsonNodeIsEqualToDatabind(b, impl::writeByte);
		}
	}

	@Test
	void primitiveShort() throws IOException {
		for (short s : SHORTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(s, impl::writeShort);
			assertThatGsonJsonWriterIsEqualToDatabind(s, impl::writeShort);
			assertThatJacksonJsonNodeIsEqualToDatabind(s, impl::writeShort);
		}
	}

	@Test
	void primitiveInt() throws IOException {
		for (int i : INTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(i, impl::writeInt);
			assertThatGsonJsonWriterIsEqualToDatabind(i, impl::writeInt);
			assertThatJacksonJsonNodeIsEqualToDatabind(i, impl::writeInt);
		}
	}

	@Test
	void primitiveLong() throws IOException {
		for (long l : LONGS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(l, impl::writeLong);
			assertThatGsonJsonWriterIsEqualToDatabind(l, impl::writeLong);
			assertThatJacksonJsonNodeIsEqualToDatabind(l, impl::writeLong);
		}
	}

	@Test
	void primitiveChar() throws IOException {
		for (char c : CHARS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(c, impl::writeCharacter);
			assertThatGsonJsonWriterIsEqualToDatabind(c, impl::writeCharacter);
			assertThatJacksonJsonNodeIsEqualToDatabind(c, impl::writeCharacter);
		}
	}

	@Test
	void primitiveFloat() throws IOException {
		for (float f : FLOATS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(f, impl::writeFloat);
			assertThatGsonJsonWriterIsEqualToDatabind(f, impl::writeFloat);
			assertThatJacksonJsonNodeIsEqualToDatabind(f, impl::writeFloat);
		}
	}

	@Test
	void primitiveDouble() throws IOException {
		for (double d : DOUBLES) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(d, impl::writeDouble);
			assertThatGsonJsonWriterIsEqualToDatabind(d, impl::writeDouble);
			assertThatJacksonJsonNodeIsEqualToDatabind(d, impl::writeDouble);
		}
	}

	@Test
	void boxedBoolean() throws IOException {
		for (Boolean b : BOXED_BOOLEANS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(b, impl::writeBoxedBoolean);
			assertThatGsonJsonWriterIsEqualToDatabind(b, impl::writeBoxedBoolean);
			assertThatJacksonJsonNodeIsEqualToDatabind(b, impl::writeBoxedBoolean);
		}
	}

	@Test
	void boxedByte() throws IOException {
		for (Byte b : BOXED_BYTES) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(b, impl::writeBoxedByte);
			assertThatGsonJsonWriterIsEqualToDatabind(b, impl::writeBoxedByte);
			assertThatJacksonJsonNodeIsEqualToDatabind(b, impl::writeBoxedByte);
		}
	}

	@Test
	void boxedShort() throws IOException {
		for (Short s : BOXED_SHORTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(s, impl::writeBoxedShort);
			assertThatGsonJsonWriterIsEqualToDatabind(s, impl::writeBoxedShort);
			assertThatJacksonJsonNodeIsEqualToDatabind(s, impl::writeBoxedShort);
		}
	}

	@Test
	void boxedInteger() throws IOException {
		for (Integer i : INTEGERS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(i, impl::writeBoxedInt);
			assertThatGsonJsonWriterIsEqualToDatabind(i, impl::writeBoxedInt);
			assertThatJacksonJsonNodeIsEqualToDatabind(i, impl::writeBoxedInt);
		}
	}

	@Test
	void boxedLong() throws IOException {
		for (Long l : BOXED_LONGS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(l, impl::writeBoxedLong);
			assertThatGsonJsonWriterIsEqualToDatabind(l, impl::writeBoxedLong);
			assertThatJacksonJsonNodeIsEqualToDatabind(l, impl::writeBoxedLong);
		}
	}

	@Test
	void boxedCharacter() throws IOException {
		for (Character c : CHARACTERS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(c, impl::writeBoxedCharacter);
			assertThatGsonJsonWriterIsEqualToDatabind(c, impl::writeBoxedCharacter);
			assertThatJacksonJsonNodeIsEqualToDatabind(c, impl::writeBoxedCharacter);
		}
	}

	@Test
	void boxedFloat() throws IOException {
		for (Float f : BOXED_FLOATS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(f, impl::writeBoxedFloat);
			assertThatGsonJsonWriterIsEqualToDatabind(f, impl::writeBoxedFloat);
			assertThatJacksonJsonNodeIsEqualToDatabind(f, impl::writeBoxedFloat);
		}
	}

	@Test
	void boxedDouble() throws IOException {
		for (Double d : BOXED_DOUBLES) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(d, impl::writeBoxedDouble);
			assertThatGsonJsonWriterIsEqualToDatabind(d, impl::writeBoxedDouble);
			assertThatJacksonJsonNodeIsEqualToDatabind(d, impl::writeBoxedDouble);
		}
	}

	@Test
	void string() throws IOException {
		for (String s : STRINGS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(s, impl::writeString);
			assertThatGsonJsonWriterIsEqualToDatabind(s, impl::writeString);
			assertThatJacksonJsonNodeIsEqualToDatabind(s, impl::writeString);
		}
	}
}
