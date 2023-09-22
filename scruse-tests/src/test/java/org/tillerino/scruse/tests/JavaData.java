package org.tillerino.scruse.tests;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class JavaData {

	// Primitives
	public static final boolean[] BOOLEANS = { true, false };
	public static final byte[] BYTES = { Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE };
	public static final short[] SHORTS = { Short.MIN_VALUE, -1, 0, 1, Short.MAX_VALUE };
	public static final int[] INTS = { Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE };
	public static final long[] LONGS = { Long.MIN_VALUE, -1, 0, 1, Long.MAX_VALUE };
	public static final char[] CHARS = { Character.MIN_VALUE, 'a', 'A', 'ö', 'Ö', Character.MAX_VALUE };
	public static final float[] FLOATS = { Float.MIN_VALUE, -1, 0, 1, Float.MAX_VALUE, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY };
	public static final double[] DOUBLES = { Double.MIN_VALUE, -1, 0, 1, Double.MAX_VALUE, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };

	// Boxed scalars
	public static final Boolean[] BOXED_BOOLEANS = { true, false, null };
	public static final Byte[] BOXED_BYTES = { Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE, null };
	public static final Short[] BOXED_SHORTS = { Short.MIN_VALUE, -1, 0, 1, Short.MAX_VALUE, null };
	public static final Integer[] BOXED_INTS = { Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE, null };
	public static final Long[] BOXED_LONGS = { Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE, null };
	public static final Character[] BOXED_CHARS = { Character.MIN_VALUE, 'a', 'A', 'ö', 'Ö', Character.MAX_VALUE, null };
	public static final Float[] BOXED_FLOATS = { Float.MIN_VALUE, -1f, 0f, 1f, Float.MAX_VALUE, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, null };
	public static final Double[] BOXED_DOUBLES = { Double.MIN_VALUE, -1d, 0d, 1d, Double.MAX_VALUE, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null };
	public static final String[] STRINGS = new String[] { "", " ", "a", "A", "ö", "Ö", "a b", "a\tb", "a\nb", "a\rb", "a\"b", "a\\b", "a/b", "a\b", "a\f", "a\b\f\n\r\t", null };

	// Primitive arrays
	public static final boolean[][] BOOLEAN_ARRAYS = {null, BOOLEANS};
	public static final byte[][] BYTE_ARRAYS = {null, BYTES};
	public static final short[][] SHORT_ARRAYS = {null, SHORTS};
	public static final int[][] INT_ARRAYS = {null, INTS};
	public static final long[][] LONG_ARRAYS = {null, LONGS};
	public static final char[][] CHAR_ARRAYS = {null, CHARS};
	public static final float[][] FLOAT_ARRAYS = {null, FLOATS};
	public static final double[][] DOUBLE_ARRAYS = {null, DOUBLES};

	// Boxed arrays
	public static final Boolean[][] BOXED_BOOLEAN_ARRAYS = {null, BOXED_BOOLEANS};
	public static final Byte[][] BOXED_BYTE_ARRAYS = {null, BOXED_BYTES};
	public static final Short[][] BOXED_SHORT_ARRAYS = {null, BOXED_SHORTS};
	public static final Integer[][] BOXED_INT_ARRAYS = {null, BOXED_INTS};
	public static final Long[][] BOXED_LONG_ARRAYS = {null, BOXED_LONGS};
	public static final Character[][] BOXED_CHAR_ARRAYS = {null, BOXED_CHARS};
	public static final Float[][] BOXED_FLOAT_ARRAYS = {null, BOXED_FLOATS};
	public static final Double[][] BOXED_DOUBLE_ARRAYS = {null, BOXED_DOUBLES};
	public static final String[][] STRING_ARRAYS = {null, STRINGS};

	// Scalar lists
	public static final List<List<Boolean>> BOOLEAN_LISTS = asList(null, asList(BOXED_BOOLEANS));
	public static final List<List<Byte>> BYTE_LISTS = asList(null, asList(BOXED_BYTES));
	public static final List<List<Short>> SHORT_LISTS = asList(null, asList(BOXED_SHORTS));
	public static final List<List<Integer>> INT_LISTS = asList(null, asList(BOXED_INTS));
	public static final List<List<Long>> LONG_LISTS = asList(null, asList(BOXED_LONGS));
	public static final List<List<Character>> CHAR_LISTS = asList(null, asList(BOXED_CHARS));
	public static final List<List<Float>> FLOAT_LISTS = asList(null, asList(BOXED_FLOATS));
	public static final List<List<Double>> DOUBLE_LISTS = asList(null, asList(BOXED_DOUBLES));
	public static final List<List<String>> STRING_LISTS = asList(null, asList(STRINGS));

	// Scalar maps
	public static final List<Map<String, Boolean>> STRING_BOOLEAN_MAPS = asList(
		null,
		map(),
		map("a", true, "b", null, "c", false)
	);
	public static final List<Map<String, Byte>> STRING_BYTE_MAPS = asList(
		null,
		map(),
		map("a", (byte) 1, "b", null, "c", (byte) Byte.MIN_VALUE, "d", Byte.MAX_VALUE)
	);
	public static final List<Map<String, Short>> STRING_SHORT_MAPS = asList(
		null,
		map(),
		map("a", (short) 1, "b", null, "c", (short) Short.MIN_VALUE, "d", Short.MAX_VALUE)
	);
	public static final List<Map<String, Integer>> STRING_INT_MAPS = asList(
		null,
		map(),
		map("a", 1, "b", null, "c", Integer.MIN_VALUE, "d", Integer.MAX_VALUE)
	);
	public static final List<Map<String, Long>> STRING_LONG_MAPS = asList(
		null,
		map(),
		map("a", 1L, "b", null, "c", Long.MIN_VALUE, "d", Long.MAX_VALUE)
	);
	public static final List<Map<String, Character>> STRING_CHAR_MAPS = asList(
		null,
		map(),
		map("a", 'a', "b", null, "c", '❆', "d", Character.MAX_VALUE)
	);
	public static final List<Map<String, Float>> STRING_FLOAT_MAPS = asList(
		null,
		map(),
		map("a", 1.0f, "b", null, "c", Float.MIN_VALUE, "d", Float.MAX_VALUE, "e", Float.NaN, "f", Float.NEGATIVE_INFINITY, "g", Float.POSITIVE_INFINITY)
	);
	public static final List<Map<String, Double>> STRING_DOUBLE_MAPS = asList(
		null,
		map(),
		map("a", 1.0, "b", null, "c", Double.MIN_VALUE, "d", Double.MAX_VALUE, "e", Double.NaN, "f", Double.NEGATIVE_INFINITY, "g", Double.POSITIVE_INFINITY)
	);
	public static final List<Map<String, String>> STRING_STRING_MAPS = asList(
		null,
		map(),
		map("a", "a", "b", null, "c", "❆", "d", "a\b\f\n\r\t")
	);

	public static <K, V> Map<K, V> map() {
		return Map.of();
	}

	public static <K, V> Map<K, V> map(K k1, V v1) {
		Map<K, V> map = new LinkedHashMap<>();
		map.put(k1, v1);
		return map;
	}

	static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2) {
		Map<K, V> map = map(k1, v1);
		map.put(k2, v2);
		return map;
	}

	public static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3) {
		Map<K, V> map = map(k1, v1, k2, v2);
		map.put(k3, v3);
		return map;
	}

	public static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
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

	public static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
		Map<K, V> map = map(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
		map.put(k7, v7);
		return map;
	}
}
