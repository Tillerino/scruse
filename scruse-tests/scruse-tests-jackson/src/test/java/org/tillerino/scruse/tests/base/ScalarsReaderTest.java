package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.JsonData;

import java.io.IOException;

import static org.tillerino.scruse.tests.InputUtils.assertIsEqualToDatabind;

class ScalarsReaderTest {
	PrimitiveScalarsReader primitive = new PrimitiveScalarsReaderImpl();
	BoxedScalarsReader boxed = new BoxedScalarsReaderImpl();

	@Test
	void testBoolean() throws IOException {
		for (String json : JsonData.BOOLEANS) {
			assertIsEqualToDatabind(json, primitive::readPrimitiveBooleanX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testByte() throws IOException {
		for (String json : JsonData.BYTES) {
			assertIsEqualToDatabind(json, primitive::readPrimitiveByteX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testShort() throws IOException {
		for (String json : JsonData.SHORTS) {
			assertIsEqualToDatabind(json, primitive::readPrimitiveShortX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testInt() throws IOException {
		for (String json : JsonData.INTS) {
			assertIsEqualToDatabind(json, primitive::readPrimitiveIntX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testLong() throws IOException {
		for (String json : JsonData.LONGS) {
			assertIsEqualToDatabind(json, primitive::readPrimitiveLongX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testChar() throws IOException {
		for (String json : JsonData.CHARS) {
			assertIsEqualToDatabind(json, primitive::readPrimitiveCharX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testFloat() throws IOException {
		for (String json : JsonData.FLOATS) {
			assertIsEqualToDatabind(json, primitive::readPrimitiveFloatX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testDouble() throws IOException {
		for (String json : JsonData.DOUBLES) {
			assertIsEqualToDatabind(json, primitive::readPrimitiveDoubleX, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedBoolean() throws IOException {
		for (String json : JsonData.BOXED_BOOLEANS) {
			assertIsEqualToDatabind(json, boxed::readBoxedBoolean, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedByte() throws IOException {
		for (String json : JsonData.BOXED_BYTES) {
			assertIsEqualToDatabind(json, boxed::readBoxedByte, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedShort() throws IOException {
		for (String json : JsonData.BOXED_SHORTS) {
			assertIsEqualToDatabind(json, boxed::readBoxedShort, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedInteger() throws IOException {
		for (String json : JsonData.BOXED_INTEGERS) {
			assertIsEqualToDatabind(json, boxed::readBoxedInt, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedLong() throws IOException {
		for (String json : JsonData.BOXED_LONGS) {
			assertIsEqualToDatabind(json, boxed::readBoxedLong, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedChar() throws IOException {
		for (String json : JsonData.BOXED_CHARS) {
			assertIsEqualToDatabind(json, boxed::readBoxedChar, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedFloat() throws IOException {
		for (String json : JsonData.BOXED_FLOATS) {
			assertIsEqualToDatabind(json, boxed::readBoxedFloat, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedDouble() throws IOException {
		for (String json : JsonData.BOXED_DOUBLES) {
			assertIsEqualToDatabind(json, boxed::readBoxedDouble, new TypeReference<>() {
			});
		}
	}

	@Test
	void testString() throws IOException {
		for (String json : JsonData.STRINGS) {
			assertIsEqualToDatabind(json, boxed::readString, new TypeReference<>() {
			});
		}
	}

	@Test
	void testEnum() throws IOException {
		for (String json : JsonData.ENUMS) {
			assertIsEqualToDatabind(json, boxed::readEnum, new TypeReference<>() {
			});
		}
	}
}
