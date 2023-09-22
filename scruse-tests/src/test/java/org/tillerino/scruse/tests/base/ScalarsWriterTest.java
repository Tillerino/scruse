package org.tillerino.scruse.tests.base;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.JavaData;

import java.io.IOException;

import static org.tillerino.scruse.tests.OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind;

class ScalarsWriterTest {
	PrimitiveScalarsWriter primitive = new PrimitiveScalarsWriterImpl();
	BoxedScalarsWriter boxed = new BoxedScalarsWriterImpl();

	@Test
	void testBoolean() throws IOException {
		for (boolean b : JavaData.BOOLEANS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(b, primitive::writePrimitiveBooleanX);
		}
	}

	@Test
	void testByte() throws IOException {
		for (byte b : JavaData.BYTES) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(b, primitive::writePrimitiveByteX);
		}
	}

	@Test
	void testShort() throws IOException {
		for (short s : JavaData.SHORTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(s, primitive::writePrimitiveShortX);
		}
	}

	@Test
	void testInt() throws IOException {
		for (int i : JavaData.INTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(i, primitive::writePrimitiveIntX);
		}
	}

	@Test
	void testLong() throws IOException {
		for (long l : JavaData.LONGS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(l, primitive::writePrimitiveLongX);
		}
	}

	@Test
	void testChar() throws IOException {
		for (char c : JavaData.CHARS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(c, primitive::writePrimitiveCharX);
		}
	}

	@Test
	void testFloat() throws IOException {
		for (float f : JavaData.FLOATS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(f, primitive::writePrimitiveFloatX);
		}
	}

	@Test
	void testDouble() throws IOException {
		for (double d : JavaData.DOUBLES) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(d, primitive::writePrimitiveDoubleX);
		}
	}

	@Test
	void testBoxedBoolean() throws IOException {
		for (Boolean b : JavaData.BOXED_BOOLEANS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(b, boxed::writeBoxedBoolean);
		}
	}

	@Test
	void testBoxedByte() throws IOException {
		for (Byte b : JavaData.BOXED_BYTES) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(b, boxed::writeBoxedByte);
		}
	}

	@Test
	void testBoxedShort() throws IOException {
		for (Short s : JavaData.BOXED_SHORTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(s, boxed::writeBoxedShort);
		}
	}

	@Test
	void testBoxedInteger() throws IOException {
		for (Integer i : JavaData.BOXED_INTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(i, boxed::writeBoxedInt);
		}
	}

	@Test
	void testBoxedLong() throws IOException {
		for (Long l : JavaData.BOXED_LONGS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(l, boxed::writeBoxedLong);
		}
	}

	@Test
	void testBoxedChar() throws IOException {
		for (Character c : JavaData.BOXED_CHARS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(c, boxed::writeBoxedChar);
		}
	}

	@Test
	void testBoxedFloat() throws IOException {
		for (Float f : JavaData.BOXED_FLOATS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(f, boxed::writeBoxedFloat);
		}
	}

	@Test
	void testBoxedDouble() throws IOException {
		for (Double d : JavaData.BOXED_DOUBLES) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(d, boxed::writeBoxedDouble);
		}
	}

	@Test
	void testString() throws IOException {
		for (String s : JavaData.STRINGS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(s, boxed::writeString);
		}
	}
}
