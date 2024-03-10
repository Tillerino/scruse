package org.tillerino.scruse.tests.base;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.JavaData;
import org.tillerino.scruse.tests.model.AnEnum;

import java.io.IOException;
import java.util.List;

import static org.tillerino.scruse.tests.OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind;

class ScalarListsWriterTest {
	ScalarListsWriter impl = new ScalarListsWriterImpl();
	@Test
	void testBoxedBooleanList() throws IOException {
		for (List<Boolean> object : JavaData.BOOLEAN_LISTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedBooleanList);
		}
	}

	@Test
	void testBoxedByteList() throws IOException {
		for (List<Byte> object : JavaData.BYTE_LISTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedByteList);
		}
	}

	@Test
	void testBoxedShortList() throws IOException {
		for (List<Short> object : JavaData.SHORT_LISTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedShortList);
		}
	}

	@Test
	void testBoxedIntList() throws IOException {
		for (List<Integer> object : JavaData.INT_LISTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedIntList);
		}
	}

	@Test
	void testBoxedLongList() throws IOException {
		for (List<Long> object : JavaData.LONG_LISTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedLongList);
		}
	}

	@Test
	void testBoxedCharList() throws IOException {
		for (List<Character> object : JavaData.CHAR_LISTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedCharList);
		}
	}

	@Test
	void testBoxedFloatList() throws IOException {
		for (List<Float> object : JavaData.FLOAT_LISTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedFloatList);
		}
	}

	@Test
	void testBoxedDoubleList() throws IOException {
		for (List<Double> object : JavaData.DOUBLE_LISTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedDoubleList);
		}
	}

	@Test
	void testStringList() throws IOException {
		for (List<String> object : JavaData.STRING_LISTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringList);
		}
	}

	@Test
	void testEnumList() throws IOException {
		for (List<AnEnum> object : JavaData.ENUM_LISTS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeEnumList);
		}
	}
}
