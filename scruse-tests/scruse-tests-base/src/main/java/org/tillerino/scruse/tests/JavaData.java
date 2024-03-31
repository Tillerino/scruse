package org.tillerino.scruse.tests;

import static java.util.Arrays.asList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.tillerino.scruse.tests.model.AnEnum;

public class JavaData {

    // Primitives
    public static final boolean[] BOOLEANS = {true, false};
    public static final byte[] BYTES = {Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE};
    public static final short[] SHORTS = {Short.MIN_VALUE, -1, 0, 1, Short.MAX_VALUE};
    public static final int[] INTS = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE};
    public static final long[] LONGS = {Long.MIN_VALUE, -1, 0, 1, Long.MAX_VALUE};
    public static final char[] CHARS = {Character.MIN_VALUE, 'a', 'A', 'ö', 'Ö', Character.MAX_VALUE};
    public static final float[] FLOATS = {
        Float.MIN_VALUE, -1, 0, 1, Float.MAX_VALUE, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY
    };
    public static final double[] DOUBLES = {
        Double.MIN_VALUE, -1, 0, 1, Double.MAX_VALUE, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY
    };

    // Boxed scalars
    public static final Boolean[] BOXED_BOOLEANS = {true, false, null};
    public static final Byte[] BOXED_BYTES = {Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE, null};
    public static final Short[] BOXED_SHORTS = {Short.MIN_VALUE, -1, 0, 1, Short.MAX_VALUE, null};
    public static final Integer[] BOXED_INTS = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE, null};
    public static final Long[] BOXED_LONGS = {Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE, null};
    public static final Character[] BOXED_CHARS = {Character.MIN_VALUE, 'a', 'A', 'ö', 'Ö', Character.MAX_VALUE, null};
    public static final Float[] BOXED_FLOATS = {
        Float.MIN_VALUE, -1f, 0f, 1f, Float.MAX_VALUE, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, null
    };
    public static final Double[] BOXED_DOUBLES = {
        Double.MIN_VALUE,
        -1d,
        0d,
        1d,
        Double.MAX_VALUE,
        Double.NaN,
        Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY,
        null
    };
    public static final String[] STRINGS = new String[] {
        "",
        " ",
        "a",
        "A",
        "ö",
        "Ö",
        "a b",
        "a\tb",
        "a\nb",
        "a\rb",
        "a\"b",
        "a\\b",
        "a/b",
        "a\b",
        "a\f",
        "a\b\f\n\r\t",
        null
    };
    public static final AnEnum[] ENUMS = {null, AnEnum.SOME_VALUE, AnEnum.ANOTHER_VALUE};

    // Primitive arrays
    public static final List<boolean[]> BOOLEAN_ARRAYS = asList(null, BOOLEANS);
    public static final List<byte[]> BYTE_ARRAYS = asList(null, BYTES);
    public static final List<short[]> SHORT_ARRAYS = asList(null, SHORTS);
    public static final List<int[]> INT_ARRAYS = asList(null, INTS);
    public static final List<long[]> LONG_ARRAYS = asList(null, LONGS);
    public static final List<char[]> CHAR_ARRAYS = asList(null, CHARS);
    public static final List<float[]> FLOAT_ARRAYS = asList(null, FLOATS);
    public static final List<double[]> DOUBLE_ARRAYS = asList(null, DOUBLES);

    // Boxed arrays
    public static final List<Boolean[]> BOXED_BOOLEAN_ARRAYS = asList(null, BOXED_BOOLEANS);
    public static final List<Byte[]> BOXED_BYTE_ARRAYS = asList(null, BOXED_BYTES);
    public static final List<Short[]> BOXED_SHORT_ARRAYS = asList(null, BOXED_SHORTS);
    public static final List<Integer[]> BOXED_INT_ARRAYS = asList(null, BOXED_INTS);
    public static final List<Long[]> BOXED_LONG_ARRAYS = asList(null, BOXED_LONGS);
    public static final List<Character[]> BOXED_CHAR_ARRAYS = asList(null, BOXED_CHARS);
    public static final List<Float[]> BOXED_FLOAT_ARRAYS = asList(null, BOXED_FLOATS);
    public static final List<Double[]> BOXED_DOUBLE_ARRAYS = asList(null, BOXED_DOUBLES);
    public static final List<String[]> STRING_ARRAYS = asList(null, STRINGS);
    public static final List<AnEnum[]> ENUM_ARRAYS = asList(null, ENUMS);

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
    public static final List<List<AnEnum>> ENUM_LISTS = asList(null, asList(ENUMS));

    // Scalar maps
    public static final List<Map<String, Boolean>> STRING_BOOLEAN_MAPS =
            asList(null, map(), map("a", true, "b", null, "c", false));
    public static final List<Map<String, Byte>> STRING_BYTE_MAPS =
            asList(null, map(), map("a", (byte) 1, "b", null, "c", (byte) Byte.MIN_VALUE, "d", Byte.MAX_VALUE));
    public static final List<Map<String, Short>> STRING_SHORT_MAPS =
            asList(null, map(), map("a", (short) 1, "b", null, "c", (short) Short.MIN_VALUE, "d", Short.MAX_VALUE));
    public static final List<Map<String, Integer>> STRING_INT_MAPS =
            asList(null, map(), map("a", 1, "b", null, "c", Integer.MIN_VALUE, "d", Integer.MAX_VALUE));
    public static final List<Map<String, Long>> STRING_LONG_MAPS =
            asList(null, map(), map("a", 1L, "b", null, "c", Long.MIN_VALUE, "d", Long.MAX_VALUE));
    public static final List<Map<String, Character>> STRING_CHAR_MAPS =
            asList(null, map(), map("a", 'a', "b", null, "c", '❆', "d", Character.MAX_VALUE));
    public static final List<Map<String, Float>> STRING_FLOAT_MAPS = asList(
            null,
            map(),
            map(
                    "a",
                    1.0f,
                    "b",
                    null,
                    "c",
                    Float.MIN_VALUE,
                    "d",
                    Float.MAX_VALUE,
                    "e",
                    Float.NaN,
                    "f",
                    Float.NEGATIVE_INFINITY,
                    "g",
                    Float.POSITIVE_INFINITY));
    public static final List<Map<String, Double>> STRING_DOUBLE_MAPS = asList(
            null,
            map(),
            map(
                    "a",
                    1.0,
                    "b",
                    null,
                    "c",
                    Double.MIN_VALUE,
                    "d",
                    Double.MAX_VALUE,
                    "e",
                    Double.NaN,
                    "f",
                    Double.NEGATIVE_INFINITY,
                    "g",
                    Double.POSITIVE_INFINITY));
    public static final List<Map<String, String>> STRING_STRING_MAPS =
            asList(null, map(), map("a", "a", "b", null, "c", "❆", "d", "a\b\f\n\r\t"));
    public static final List<Map<String, AnEnum>> STRING_ENUM_MAPS =
            asList(null, map(), map("a", AnEnum.SOME_VALUE, "b", null, "c", AnEnum.ANOTHER_VALUE));

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

    public static <K, V> Map<K, V> map(
            K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        Map<K, V> map = map(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
        map.put(k7, v7);
        return map;
    }
}
