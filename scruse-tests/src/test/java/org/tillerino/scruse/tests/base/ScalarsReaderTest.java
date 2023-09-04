package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;

import java.io.IOException;

class ScalarsReaderTest {
	ScalarsReader impl = new ScalarsReaderImpl();

	@Test
	void testBoolean() throws IOException {
		TypeReference<Boolean> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"true",
			"false"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoolean, typeRef);
		}
	}

	@Test
	void testByte() throws IOException {
		TypeReference<Byte> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"1",
			"0",
			"-1",
			"127",
			"-128"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readByte, typeRef);
		}
	}

	@Test
	void testShort() throws IOException {
		TypeReference<Short> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"1",
			"0",
			"-1",
			"32767",
			"-32768"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readShort, typeRef);
		}
	}

	@Test
	void testInt() throws IOException {
		TypeReference<Integer> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"1",
			"0",
			"-1",
			"2147483647",
			"-2147483648"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readInt, typeRef);
		}
	}

	@Test
	void testLong() throws IOException {
		TypeReference<Long> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"1",
			"0",
			"-1",
			"9223372036854775807",
			"-9223372036854775808"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readLong, typeRef);
		}
	}

	@Test
	void testChar() throws IOException {
		TypeReference<Character> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"\"a\"",
			"\"A\"",
			"\"ö\"",
			"\"Ö\"",
			"\"\\u0000\"",
			"\"\\uFFFF\""
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readCharacter, typeRef);
		}
	}

	@Test
	void testFloat() throws IOException {
		TypeReference<Float> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"1",
			"0",
			"-1",
			"3.4028235E38",
			"-3.4028235E38",
			"1.4E-45",
			"-1.4E-45",
			"\"NaN\"",
			"\"Infinity\"",
			"\"-Infinity\""
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readFloat, typeRef);
		}
	}

	@Test
	void testDouble() throws IOException {
		TypeReference<Double> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"1",
			"0",
			"-1",
			"1.7976931348623157E308",
			"-1.7976931348623157E308",
			"4.9E-324",
			"-4.9E-324",
			"\"NaN\"",
			"\"Infinity\"",
			"\"-Infinity\""
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readDouble, typeRef);
		}
	}

	@Test
	void testBoxedBoolean() throws IOException {
		TypeReference<Boolean> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"true",
			"false",
			"null"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedBoolean, typeRef);
		}
	}

	@Test
	void testBoxedByte() throws IOException {
		TypeReference<Byte> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"1",
			"0",
			"-1",
			"127",
			"-128",
			"null"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedByte, typeRef);
		}
	}

	@Test
	void testBoxedShort() throws IOException {
		TypeReference<Short> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"1",
			"0",
			"-1",
			"32767",
			"-32768",
			"null"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedShort, typeRef);
		}
	}

	@Test
	void testBoxedInteger() throws IOException {
		TypeReference<Integer> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"1",
			"0",
			"-1",
			"2147483647",
			"-2147483648",
			"null"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedInt, typeRef);
		}
	}

	@Test
	void testBoxedLong() throws IOException {
		TypeReference<Long> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"1",
			"0",
			"-1",
			"9223372036854775807",
			"-9223372036854775808",
			"null"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedLong, typeRef);
		}
	}

	@Test
	void testBoxedCharacter() throws IOException {
		TypeReference<Character> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"\"a\"",
			"\"A\"",
			"\"ö\"",
			"\"Ö\"",
			"\"\\u0000\"",
			"\"\\uFFFF\"",
			"null"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedCharacter, typeRef);
		}
	}

	@Test
	void testBoxedFloat() throws IOException {
		TypeReference<Float> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"1",
			"0",
			"-1",
			"3.4028235E38",
			"-3.4028235E38",
			"1.4E-45",
			"-1.4E-45",
			"\"NaN\"",
			"\"Infinity\"",
			"\"-Infinity\"",
			"null"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedFloat, typeRef);
		}
	}

	@Test
	void testBoxedDouble() throws IOException {
		TypeReference<Double> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"1",
			"0",
			"-1",
			"1.7976931348623157E308",
			"-1.7976931348623157E308",
			"4.9E-324",
			"-4.9E-324",
			"\"NaN\"",
			"\"Infinity\"",
			"\"-Infinity\"",
			"null"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedDouble, typeRef);
		}
	}

	@Test
	void testString() throws IOException {
		TypeReference<String> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"\"\"",
			"\"a\"",
			"\"A\"",
			"\"ö\"",
			"\"Ö\"",
			"\"\\u0000\"",
			"\"\\uFFFF\"",
			"null"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readString, typeRef);
		}
	}
}
