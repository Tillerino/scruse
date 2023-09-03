package org.tillerino.scruse.tests.base;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.tillerino.scruse.tests.OutputUtils.*;

class ScalarListsWriterTest {
	ScalarListsWriter impl = new ScalarListsWriterImpl();
	@Test
	void testBoxedBooleanList() throws IOException {
		List<List<Boolean>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsWriterTest.BOXED_BOOLEANS)
		);
		for (List<Boolean> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedBooleanList);
		}
	}

	@Test
	void testBoxedByteList() throws IOException {
		List<List<Byte>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsWriterTest.BOXED_BYTES)
		);
		for (List<Byte> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedByteList);
		}
	}

	@Test
	void testBoxedShortList() throws IOException {
		List<List<Short>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsWriterTest.BOXED_SHORTS)
		);
		for (List<Short> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedShortList);
		}
	}

	@Test
	void testBoxedIntList() throws IOException {
		List<List<Integer>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsWriterTest.INTEGERS)
		);
		for (List<Integer> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedIntList);
		}
	}

	@Test
	void testBoxedLongList() throws IOException {
		List<List<Long>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsWriterTest.BOXED_LONGS)
		);
		for (List<Long> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedLongList);
		}
	}

	@Test
	void testBoxedCharList() throws IOException {
		List<List<Character>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsWriterTest.CHARACTERS)
		);
		for (List<Character> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedCharList);
		}
	}

	@Test
	void testBoxedFloatList() throws IOException {
		List<List<Float>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsWriterTest.BOXED_FLOATS)
		);
		for (List<Float> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedFloatList);
		}
	}

	@Test
	void testBoxedDoubleList() throws IOException {
		List<List<Double>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsWriterTest.BOXED_DOUBLES)
		);
		for (List<Double> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedDoubleList);
		}
	}

	@Test
	void testStringList() throws IOException {
		List<List<String>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsWriterTest.STRINGS)
		);
		for (List<String> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringList);
		}
	}
}
