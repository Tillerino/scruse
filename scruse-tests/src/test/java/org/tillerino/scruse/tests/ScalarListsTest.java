package org.tillerino.scruse.tests;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.tillerino.scruse.tests.OutputUtils.*;

public class ScalarListsTest {
	ScalarListsWriter impl = new ScalarListsWriterImpl();
	@Test
	public void testBoxedBooleanList() throws Exception {
		List<List<Boolean>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsTests.BOXED_BOOLEANS)
		);
		for (List<Boolean> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedBooleanList);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedBooleanList);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedBooleanList);
		}
	}

	@Test
	public void testBoxedByteList() throws Exception {
		List<List<Byte>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsTests.BOXED_BYTES)
		);
		for (List<Byte> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedByteList);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedByteList);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedByteList);
		}
	}

	@Test
	public void testBoxedShortList() throws Exception {
		List<List<Short>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsTests.BOXED_SHORTS)
		);
		for (List<Short> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedShortList);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedShortList);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedShortList);
		}
	}

	@Test
	public void testBoxedIntList() throws Exception {
		List<List<Integer>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsTests.INTEGERS)
		);
		for (List<Integer> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedIntList);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedIntList);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedIntList);
		}
	}

	@Test
	public void testBoxedLongList() throws Exception {
		List<List<Long>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsTests.BOXED_LONGS)
		);
		for (List<Long> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedLongList);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedLongList);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedLongList);
		}
	}

	@Test
	public void testBoxedCharList() throws Exception {
		List<List<Character>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsTests.CHARACTERS)
		);
		for (List<Character> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedCharList);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedCharList);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedCharList);
		}
	}

	@Test
	public void testBoxedFloatList() throws Exception {
		List<List<Float>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsTests.BOXED_FLOATS)
		);
		for (List<Float> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedFloatList);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedFloatList);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedFloatList);
		}
	}

	@Test
	public void testBoxedDoubleList() throws Exception {
		List<List<Double>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsTests.BOXED_DOUBLES)
		);
		for (List<Double> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedDoubleList);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeBoxedDoubleList);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeBoxedDoubleList);
		}
	}

	@Test
	public void testStringList() throws Exception {
		List<List<String>> values = Arrays.asList(
			null,
			Arrays.asList(ScalarsTests.STRINGS)
		);
		for (List<String> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringList);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeStringList);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeStringList);
		}
	}
}
