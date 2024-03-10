package org.tillerino.scruse.tests.base;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.JavaData;
import org.tillerino.scruse.tests.model.AnEnum;

import java.io.IOException;
import java.util.Map;

import static org.tillerino.scruse.tests.OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind;

class ScalarMapsWriterTest {
	ScalarMapsWriter impl = new ScalarMapsWriterImpl();

	@Test
	void testStringBooleanMap() throws IOException {
		for (Map<String, Boolean> object : JavaData.STRING_BOOLEAN_MAPS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringBooleanMap);
		}
	}

	@Test
	void testStringByteMap() throws IOException {
		for (Map<String, Byte> object : JavaData.STRING_BYTE_MAPS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringByteMap);
		}
	}

	@Test
	void testStringShortMap() throws IOException {
		for (Map<String, Short> object : JavaData.STRING_SHORT_MAPS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringShortMap);
		}
	}

	@Test
	void testStringIntMap() throws IOException {
		for (Map<String, Integer> object : JavaData.STRING_INT_MAPS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringIntMap);
		}
	}

	@Test
	void testStringLongMap() throws IOException {
		for (Map<String, Long> object : JavaData.STRING_LONG_MAPS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringLongMap);
		}
	}

	@Test
	void testStringCharMap() throws IOException {
		for (Map<String, Character> object : JavaData.STRING_CHAR_MAPS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringCharMap);
		}
	}

	@Test
	void testStringFloatMap() throws IOException {
		for (Map<String, Float> object : JavaData.STRING_FLOAT_MAPS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringFloatMap);
		}
	}

	@Test
	void testStringDoubleMap() throws IOException {
		for (Map<String, Double> object : JavaData.STRING_DOUBLE_MAPS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringDoubleMap);
		}
	}
	
	@Test
	void testStringStringMap() throws IOException {
		for (Map<String, String> object : JavaData.STRING_STRING_MAPS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringStringMap);
		}
	}

	@Test
	void testStringEnumMap() throws IOException {
		for (Map<String, AnEnum> object : JavaData.STRING_ENUM_MAPS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringEnumMap);
		}
	}
}
