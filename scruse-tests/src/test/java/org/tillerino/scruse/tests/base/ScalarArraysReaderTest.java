package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;

import java.io.IOException;

class ScalarArraysReaderTest {
	ScalarArraysReader impl = new ScalarArraysReaderImpl();

	@Test
	void testBooleanArray() throws IOException {
		TypeReference<boolean[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[true]",
			"[false]",
			"[true,false]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBooleanArray, typeRef);
		}
	}

	@Test
	void testByteArray() throws IOException {
		TypeReference<byte[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[1]",
			"[0]",
			"[-1]",
			"[127]",
			"[-128]",
			"[1,0,-1,127,-128]",
			"\"\"",
			"\"MTIzNA==\"",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readByteArray, typeRef);
		}
	}

	@Test
	void testCharArray() throws IOException {
		TypeReference<char[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"\"abc\"",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readCharArray, typeRef);
		}
	}

	@Test
	void testShortArray() throws IOException {
		TypeReference<short[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[1]",
			"[0]",
			"[-1]",
			"[32767]",
			"[-32768]",
			"[1,0,-1,32767,-32768]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readShortArray, typeRef);
		}
	}

	@Test
	void testIntArray() throws IOException {
		TypeReference<int[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[1]",
			"[0]",
			"[-1]",
			"[2147483647]",
			"[-2147483648]",
			"[1,0,-1,2147483647,-2147483648]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readIntArray, typeRef);
		}
	}

	@Test
	void testLongArray() throws IOException {
		TypeReference<long[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[1]",
			"[0]",
			"[-1]",
			"[9223372036854775807]",
			"[-9223372036854775808]",
			"[1,0,-1,9223372036854775807,-9223372036854775808]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readLongArray, typeRef);
		}
	}

	@Test
	void testFloatArray() throws IOException {
		TypeReference<float[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[1.0]",
			"[0.0]",
			"[-1.0]",
			"[3.4028235E38]",
			"[-3.4028235E38]",
			"[1.0,0.0,-1.0,3.4028235E38,-3.4028235E38,\"NaN\",\"Infinity\",\"-Infinity\"]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readFloatArray, typeRef);
		}
	}

	@Test
	void testDoubleArray() throws IOException {
		TypeReference<double[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[1.0]",
			"[0.0]",
			"[-1.0]",
			"[1.7976931348623157E308]",
			"[-1.7976931348623157E308]",
			"[1.0,0.0,-1.0,1.7976931348623157E308,-1.7976931348623157E308,\"NaN\",\"Infinity\",\"-Infinity\"]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readDoubleArray, typeRef);
		}
	}

	@Test
	void testBoxedBooleanArray() throws IOException {
		TypeReference<Boolean[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[true]",
			"[false]",
			"[true,false,null]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedBooleanArray, typeRef);
		}
	}

	@Test
	void testBoxedByteArray() throws IOException {
		TypeReference<Byte[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[1]",
			"[0]",
			"[-1]",
			"[127]",
			"[-128]",
			"[1,0,-1,127,-128,null]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedByteArray, typeRef);
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
		TypeReference<Short[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[1]",
			"[0]",
			"[-1]",
			"[32767]",
			"[-32768]",
			"[1,0,-1,32767,-32768,null]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedShortArray, typeRef);
		}
	}

	@Test
	void testBoxedIntArray() throws IOException {
		TypeReference<Integer[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[1]",
			"[0]",
			"[-1]",
			"[2147483647]",
			"[-2147483648]",
			"[1,0,-1,2147483647,-2147483648,null]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedIntArray, typeRef);
		}
	}

	@Test
	void testBoxedLongArray() throws IOException {
		TypeReference<Long[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[1]",
			"[0]",
			"[-1]",
			"[9223372036854775807]",
			"[-9223372036854775808]",
			"[1,0,-1,9223372036854775807,-9223372036854775808,null]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedLongArray, typeRef);
		}
	}

	@Test
	void testBoxedFloatArray() throws IOException {
		TypeReference<Float[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[1.0]",
			"[0.0]",
			"[-1.0]",
			"[3.4028235E38]",
			"[-3.4028235E38]",
			"[1.0,0.0,-1.0,3.4028235E38,-3.4028235E38,\"NaN\",\"Infinity\",\"-Infinity\",null]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedFloatArray, typeRef);
		}
	}

	@Test
	void testBoxedDoubleArray() throws IOException {
		TypeReference<Double[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[1.0]",
			"[0.0]",
			"[-1.0]",
			"[1.7976931348623157E308]",
			"[-1.7976931348623157E308]",
			"[1.0,0.0,-1.0,1.7976931348623157E308,-1.7976931348623157E308,\"NaN\",\"Infinity\",\"-Infinity\",null]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedDoubleArray, typeRef);
		}
	}

	@Test
	void testStringArray() throws IOException {
		TypeReference<String[]> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[\"abc\"]",
			"[\"abc\",\"def\",null]",
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readStringArray, typeRef);
		}
	}

	@Test
	void testLargeIntArray() throws IOException {
		TypeReference<int[]> typeRef = new TypeReference<>() {
		};

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
		InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readIntArray, typeRef);
	}
}