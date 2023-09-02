package org.tillerino.scruse.tests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.tillerino.scruse.tests.OutputUtils.*;
import static org.tillerino.scruse.tests.ScalarMapsWriterTest.map;

class NestedScalarsTest {
	NestedScalarsWriter impl = new NestedScalarsWriterImpl();

	@Test
	void testDoubleArrayArray() throws IOException {
		Double[][][] values = {
			null,
			{ null },
			{ { null } },
			{ null, { null } },
			{ null, { 1D, null, 3D } }
		};
		for (Double[][] value : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(value, impl::writeDoubleArrayArray);
			assertThatGsonJsonWriterIsEqualToDatabind(value, impl::writeDoubleArrayArray);
			assertThatJacksonJsonNodeIsEqualToDatabind(value, impl::writeDoubleArrayArray);
		}
	}

	@Test
	void testStringDoubleArrayMap() throws IOException {
		List<Map<String, Double[]>> values = Arrays.asList(
			null,
			map(),
			map("a", null),
			map("a", new Double[]{ 1D, null, 3D }, "b", null, "c", new Double[]{ 4D, 5D, null })
		);
		for (Map<String, Double[]> value : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(value, impl::writeStringDoubleArrayMap);
			assertThatGsonJsonWriterIsEqualToDatabind(value, impl::writeStringDoubleArrayMap);
			assertThatJacksonJsonNodeIsEqualToDatabind(value, impl::writeStringDoubleArrayMap);
		}
	}

	@Test
	void testStringDoubleMapList() throws IOException {
		List<List<Map<String, Double>>> values = Arrays.<List<Map<String, Double>>> asList(
			null,
			List.of(),
			Arrays.<Map<String, Double>> asList(new Map[] { null }),
			List.of(map()),
			List.of(map("a", 1D, "b", null, "c", 3D), map("d", 4D, "e", 5D, "f", null))
		);
		for (List<Map<String, Double>> value : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(value, impl::writeStringDoubleMapList);
			assertThatGsonJsonWriterIsEqualToDatabind(value, impl::writeStringDoubleMapList);
			assertThatJacksonJsonNodeIsEqualToDatabind(value, impl::writeStringDoubleMapList);
		}
	}

	@Test
	void testStringDoubleMapMap() throws IOException {
		List<Map<String, Map<String, Double>>> values = Arrays.asList(
			null,
			map(),
			map("a", null),
			map("a", map("a", 1D, "b", null, "c", 3D), "b", null, "c", map("d", 4D, "e", 5D, "f", null))
		);
		for (Map<String, Map<String, Double>> value : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(value, impl::writeStringDoubleMapMap);
			assertThatGsonJsonWriterIsEqualToDatabind(value, impl::writeStringDoubleMapMap);
			assertThatJacksonJsonNodeIsEqualToDatabind(value, impl::writeStringDoubleMapMap);
		}
	}
}
