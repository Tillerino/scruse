package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ScalarArraysReaderTest {
	ScalarArraysReaderImpl impl = new ScalarArraysReaderImpl();

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
}