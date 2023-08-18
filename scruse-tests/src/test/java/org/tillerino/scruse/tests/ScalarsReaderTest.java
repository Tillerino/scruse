package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

public class ScalarsReaderTest {
	ScalarsReaderImpl impl = new ScalarsReaderImpl();

	@Test
	public void primitiveBoolean() throws Exception {
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
	public void primitiveByte() throws Exception {
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
	public void primitiveShort() throws Exception {
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
	public void primitiveInt() throws Exception {
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
	public void primitiveLong() throws Exception {
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
	public void primitiveChar() throws Exception {
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
	public void primitiveFloat() throws Exception {
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
	public void primitiveDouble() throws Exception {
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
	public void boxedBoolean() throws Exception {
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
	public void boxedByte() throws Exception {
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
	public void boxedShort() throws Exception {
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
	public void boxedInteger() throws Exception {
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
	public void boxedLong() throws Exception {
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
	public void boxedCharacter() throws Exception {
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
	public void boxedFloat() throws Exception {
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
	public void boxedDouble() throws Exception {
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
	public void string() throws Exception {
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
