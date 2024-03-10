package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.JsonData;

import java.io.IOException;

class ScalarArraysReaderTest {
	ScalarArraysReader impl = new ScalarArraysReaderImpl();

	@Test
	void testBooleanArray() throws IOException {
		for (String json : JsonData.BOOLEAN_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBooleanArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testByteArray() throws IOException {
		for (String json : JsonData.BYTE_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readByteArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testCharArray() throws IOException {
		for (String json : JsonData.STRINGS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readCharArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testShortArray() throws IOException {
		for (String json : JsonData.SHORT_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readShortArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testIntArray() throws IOException {
		for (String json : JsonData.INT_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readIntArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testLongArray() throws IOException {
		for (String json : JsonData.LONG_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readLongArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testFloatArray() throws IOException {
		for (String json : JsonData.FLOAT_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readFloatArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testDoubleArray() throws IOException {
		for (String json : JsonData.DOUBLE_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readDoubleArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedBooleanArray() throws IOException {
		for (String json : JsonData.BOXED_BOOLEAN_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedBooleanArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedByteArray() throws IOException {
		for (String json : JsonData.BOXED_BYTE_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedByteArray, new TypeReference<>() {
			});
		}
	}

	/**
	 * Whereas, char[] is serialized as a String, Jackson databind does not support Character[].
	 * This test is here so that the overall method count in this class is as expected.
	 */
	@Test
	void testBoxedCharArray() {
		Assertions.assertThat(true).isTrue();
	}

	@Test
	void testBoxedShortArray() throws IOException {
		for (String json : JsonData.BOXED_SHORT_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedShortArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedIntArray() throws IOException {
		for (String json : JsonData.BOXED_INT_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedIntArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedLongArray() throws IOException {
		for (String json : JsonData.BOXED_LONG_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedLongArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedFloatArray() throws IOException {
		for (String json : JsonData.BOXED_FLOAT_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedFloatArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testBoxedDoubleArray() throws IOException {
		for (String json : JsonData.BOXED_DOUBLE_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedDoubleArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testStringArray() throws IOException {
		for (String json : JsonData.STRING_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readStringArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testEnumArray() throws IOException {
		for (String json : JsonData.ENUM_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readEnumArray, new TypeReference<>() {
			});
		}
	}

	@Test
	void testLargeIntArray() throws IOException {
		String json = """
			[
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
				10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
				20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
				30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
				40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
				50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
				60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
				70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
				80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
				90, 91, 92, 93, 94, 95, 96, 97, 98, 99
			]
		""";
		InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readIntArray, new TypeReference<int[]>() {
		});
	}
}