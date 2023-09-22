package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.JsonData;

import java.io.IOException;

class ScalarsReaderTest {
	PrimitiveScalarsReader primitive = new PrimitiveScalarsReaderImpl();
	BoxedScalarsReader boxed = new BoxedScalarsReaderImpl();

	@Test
	void testBoolean() throws IOException {
		for (String json : JsonData.BOOLEANS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, primitive::readPrimitiveBooleanX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testByte() throws IOException {
		for (String json : JsonData.BYTES) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, primitive::readPrimitiveByteX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testShort() throws IOException {
		for (String json : JsonData.SHORTS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, primitive::readPrimitiveShortX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testInt() throws IOException {
		for (String json : JsonData.INTS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, primitive::readPrimitiveIntX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testLong() throws IOException {
		for (String json : JsonData.LONGS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, primitive::readPrimitiveLongX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testChar() throws IOException {
		for (String json : JsonData.CHARS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, primitive::readPrimitiveCharX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testFloat() throws IOException {
		for (String json : JsonData.FLOATS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, primitive::readPrimitiveFloatX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testDouble() throws IOException {
		for (String json : JsonData.DOUBLES) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, primitive::readPrimitiveDoubleX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedBoolean() throws IOException {
		for (String json : JsonData.BOXED_BOOLEANS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, boxed::readBoxedBoolean, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedByte() throws IOException {
		for (String json : JsonData.BOXED_BYTES) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, boxed::readBoxedByte, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedShort() throws IOException {
		for (String json : JsonData.BOXED_SHORTS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, boxed::readBoxedShort, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedInteger() throws IOException {
		for (String json : JsonData.BOXED_INTEGERS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, boxed::readBoxedInt, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedLong() throws IOException {
		for (String json : JsonData.BOXED_LONGS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, boxed::readBoxedLong, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedChar() throws IOException {
		for (String json : JsonData.BOXED_CHARS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, boxed::readBoxedChar, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedFloat() throws IOException {
		for (String json : JsonData.BOXED_FLOATS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, boxed::readBoxedFloat, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedDouble() throws IOException {
		for (String json : JsonData.BOXED_DOUBLES) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, boxed::readBoxedDouble, new TypeReference<>() {
			});
		}
	}

	@Test
	void testString() throws IOException {
		for (String json : JsonData.STRINGS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, boxed::readString, new TypeReference<>() {
			});
		}
	}
}
