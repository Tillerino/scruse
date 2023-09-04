package org.tillerino.scruse.tests.base;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.tillerino.scruse.tests.OutputUtils.*;

class ScalarMapsWriterTest {
	ScalarMapsWriter impl = new ScalarMapsWriterImpl();

	@Test
	void testStringBooleanMap() throws IOException {
		List<Map<String, Boolean>> values = Arrays.asList(
			null,
			map(),
			map("a", true, "b", null, "c", false)
		);
		for (Map<String, Boolean> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringBooleanMap);
		}
	}

	@Test
	void testStringByteMap() throws IOException {
		List<Map<String, Byte>> values = Arrays.asList(
			null,
			map(),
			map("a", (byte) 1, "b", null, "c", (byte) Byte.MIN_VALUE, "d", Byte.MAX_VALUE)
		);
		for (Map<String, Byte> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringByteMap);
		}
	}

	@Test
	void testStringShortMap() throws IOException {
		List<Map<String, Short>> values = Arrays.asList(
			null,
			map(),
			map("a", (short) 1, "b", null, "c", (short) Short.MIN_VALUE, "d", Short.MAX_VALUE)
		);
		for (Map<String, Short> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringShortMap);
		}
	}

	@Test
	void testStringIntMap() throws IOException {
		List<Map<String, Integer>> values = Arrays.asList(
			null,
			map(),
			map("a", 1, "b", null, "c", Integer.MIN_VALUE, "d", Integer.MAX_VALUE)
		);
		for (Map<String, Integer> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringIntMap);
		}
	}

	@Test
	void testStringLongMap() throws IOException {
		List<Map<String, Long>> values = Arrays.asList(
			null,
			map(),
			map("a", 1L, "b", null, "c", Long.MIN_VALUE, "d", Long.MAX_VALUE)
		);
		for (Map<String, Long> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringLongMap);
		}
	}

	@Test
	void testStringCharMap() throws IOException {
		List<Map<String, Character>> values = Arrays.asList(
			null,
			map(),
			map("a", 'a', "b", null, "c", '❆', "d", Character.MAX_VALUE)
		);
		for (Map<String, Character> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringCharMap);
		}
	}

	@Test
	void testStringFloatMap() throws IOException {
		List<Map<String, Float>> values = Arrays.asList(
			null,
			map(),
			map("a", 1.0f, "b", null, "c", Float.MIN_VALUE, "d", Float.MAX_VALUE, "e", Float.NaN, "f", Float.NEGATIVE_INFINITY, "g", Float.POSITIVE_INFINITY)
		);
		for (Map<String, Float> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringFloatMap);
		}
	}

	@Test
	void testStringDoubleMap() throws IOException {
		List<Map<String, Double>> values = Arrays.asList(
			null,
			map(),
			map("a", 1.0, "b", null, "c", Double.MIN_VALUE, "d", Double.MAX_VALUE, "e", Double.NaN, "f", Double.NEGATIVE_INFINITY, "g", Double.POSITIVE_INFINITY)
		);
		for (Map<String, Double> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringDoubleMap);
		}
	}
	
	@Test
	void testStringStringMap() throws IOException {
		List<Map<String, String>> values = Arrays.asList(
			null,
			map(),
			map("a", "a", "b", null, "c", "❆", "d", "a\b\f\n\r\t")
		);
		for (Map<String, String> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringStringMap);
		}
	}

	static <K, V> Map<K, V> map() {
		return Map.of();
	}

	static <K, V> Map<K, V> map(K k1, V v1) {
		Map<K, V> map = new LinkedHashMap<>();
		map.put(k1, v1);
		return map;
	}

	static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2) {
		Map<K, V> map = map(k1, v1);
		map.put(k2, v2);
		return map;
	}

	static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3) {
		Map<K, V> map = map(k1, v1, k2, v2);
		map.put(k3, v3);
		return map;
	}

	static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
		Map<K, V> map = map(k1, v1, k2, v2, k3, v3);
		map.put(k4, v4);
		return map;
	}

	static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
		Map<K, V> map = map(k1, v1, k2, v2, k3, v3, k4, v4);
		map.put(k5, v5);
		return map;
	}

	static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
		Map<K, V> map = map(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
		map.put(k6, v6);
		return map;
	}

	static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
		Map<K, V> map = map(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
		map.put(k7, v7);
		return map;
	}
}