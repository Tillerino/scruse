package org.tillerino.scruse.tests;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.tillerino.scruse.tests.OutputUtils.*;

public class ScalarMapsTest {
	ScalarMapsWriter impl = new ScalarMapsWriterImpl();

	@Test
	public void testStringBooleanMap() throws Exception {
		List<Map<String, Boolean>> values = Arrays.asList(
			null,
			map(),
			map("a", true, "b", null, "c", false)
		);
		for (Map<String, Boolean> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringBooleanMap);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeStringBooleanMap);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeStringBooleanMap);
		}
	}

	@Test
	public void testStringByteMap() throws Exception {
		List<Map<String, Byte>> values = Arrays.asList(
			null,
			map(),
			map("a", (byte) 1, "b", null, "c", (byte) Byte.MIN_VALUE, "d", Byte.MAX_VALUE)
		);
		for (Map<String, Byte> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringByteMap);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeStringByteMap);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeStringByteMap);
		}
	}

	@Test
	public void testStringShortMap() throws Exception {
		List<Map<String, Short>> values = Arrays.asList(
			null,
			map(),
			map("a", (short) 1, "b", null, "c", (short) Short.MIN_VALUE, "d", Short.MAX_VALUE)
		);
		for (Map<String, Short> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringShortMap);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeStringShortMap);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeStringShortMap);
		}
	}

	@Test
	public void testStringIntMap() throws Exception {
		List<Map<String, Integer>> values = Arrays.asList(
			null,
			map(),
			map("a", 1, "b", null, "c", Integer.MIN_VALUE, "d", Integer.MAX_VALUE)
		);
		for (Map<String, Integer> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringIntMap);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeStringIntMap);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeStringIntMap);
		}
	}

	@Test
	public void testStringLongMap() throws Exception {
		List<Map<String, Long>> values = Arrays.asList(
			null,
			map(),
			map("a", 1L, "b", null, "c", Long.MIN_VALUE, "d", Long.MAX_VALUE)
		);
		for (Map<String, Long> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringLongMap);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeStringLongMap);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeStringLongMap);
		}
	}

	@Test
	public void testStringCharMap() throws Exception {
		List<Map<String, Character>> values = Arrays.asList(
			null,
			map(),
			map("a", 'a', "b", null, "c", '❆', "d", Character.MAX_VALUE)
		);
		for (Map<String, Character> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringCharMap);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeStringCharMap);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeStringCharMap);
		}
	}

	@Test
	public void testStringFloatMap() throws Exception {
		List<Map<String, Float>> values = Arrays.asList(
			null,
			map(),
			map("a", 1.0f, "b", null, "c", Float.MIN_VALUE, "d", Float.MAX_VALUE, "e", Float.NaN, "f", Float.NEGATIVE_INFINITY, "g", Float.POSITIVE_INFINITY)
		);
		for (Map<String, Float> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringFloatMap);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeStringFloatMap);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeStringFloatMap);
		}
	}

	@Test
	public void testStringDoubleMap() throws Exception {
		List<Map<String, Double>> values = Arrays.asList(
			null,
			map(),
			map("a", 1.0, "b", null, "c", Double.MIN_VALUE, "d", Double.MAX_VALUE, "e", Double.NaN, "f", Double.NEGATIVE_INFINITY, "g", Double.POSITIVE_INFINITY)
		);
		for (Map<String, Double> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringDoubleMap);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeStringDoubleMap);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeStringDoubleMap);
		}
	}
	
	@Test
	public void testStringStringMap() throws Exception {
		List<Map<String, String>> values = Arrays.asList(
			null,
			map(),
			map("a", "a", "b", null, "c", "❆", "d", "a\b\f\n\r\t")
		);
		for (Map<String, String> object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringStringMap);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::writeStringStringMap);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::writeStringStringMap);
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
