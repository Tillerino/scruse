package org.tillerino.scruse.tests;

import org.apache.commons.lang3.ArrayUtils;

public class JsonData {
	// Primitive scalars
	public static final String[] BOOLEANS = {"true", "false"};
	public static final String[] BYTES = {"1", "0", "-1", "127", "-128"};
	public static final String[] SHORTS = {"1", "0", "-1", "32767", "-32768"};
	public static final String[] INTS = {"1", "0", "-1", "2147483647", "-2147483648"};
	public static final String[] LONGS = {"1", "0", "-1", "9223372036854775807", "-9223372036854775808"};
	public static final String[] CHARS = {
		"\"a\"",
		"\"A\"",
		"\"ö\"",
		"\"Ö\"",
		"\"\\u0000\"",
		"\"\\uFFFF\""
	};
	public static final String[] FLOATS = {
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
	public static final String[] DOUBLES = {
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

	// Boxed scalars and Strings
	public static final String[] BOXED_BOOLEANS = ArrayUtils.addAll(BOOLEANS, "null");
	public static final String[] BOXED_BYTES = ArrayUtils.addAll(BYTES, "null");
	public static final String[] BOXED_SHORTS = ArrayUtils.addAll(SHORTS, "null");
	public static final String[] BOXED_INTEGERS = ArrayUtils.addAll(INTS, "null");
	public static final String[] BOXED_LONGS = ArrayUtils.addAll(LONGS, "null");
	public static final String[] BOXED_CHARS = ArrayUtils.addAll(CHARS, "null");
	public static final String[] BOXED_FLOATS = ArrayUtils.addAll(FLOATS, "null");
	public static final String[] BOXED_DOUBLES = ArrayUtils.addAll(DOUBLES, "null");
	public static final String[] STRINGS = ArrayUtils.addAll(BOXED_CHARS, "\"abc\"");
	public static final String[] ENUMS = {"null", "\"SOME_VALUE\"", "\"ANOTHER_VALUE\""};

	// Primitive arrays
	public static final String[] BOOLEAN_ARRAYS = {
		"null",
		"[]",
		"[true]",
		"[false]",
		asArray(BOOLEANS)
	};
	public static final String[] BYTE_ARRAYS = {
		"null",
		"[]",
		"[1]",
		"[0]",
		"[-1]",
		"[127]",
		"[-128]",
		asArray(BYTES),
		"\"\"",
		"\"MTIzNA==\"",
	};
	public static final String[] SHORT_ARRAYS = {
		"null",
		"[]",
		"[1]",
		"[0]",
		"[-1]",
		"[32767]",
		"[-32768]",
		asArray(SHORTS)
	};
	public static final String[] INT_ARRAYS = {
		"null",
		"[]",
		"[1]",
		"[0]",
		"[-1]",
		"[2147483647]",
		"[-2147483648]",
		asArray(INTS)
	};
	public static final String[] LONG_ARRAYS = {
		"null",
		"[]",
		"[1]",
		"[0]",
		"[-1]",
		"[9223372036854775807]",
		"[-9223372036854775808]",
		asArray(LONGS)
	};
	public static final String[] FLOAT_ARRAYS = {
		"null",
		"[]",
		"[1.0]",
		"[0.0]",
		"[-1.0]",
		"[3.4028235E38]",
		"[-3.4028235E38]",
		asArray(FLOATS)
	};
	public static final String[] DOUBLE_ARRAYS = {
		"null",
		"[]",
		"[1.0]",
		"[0.0]",
		"[-1.0]",
		"[1.7976931348623157E308]",
		"[-1.7976931348623157E308]",
		asArray(DOUBLES),
	};

	// Boxed arrays
	public static final String[] BOXED_BOOLEAN_ARRAYS = {
		"null",
		"[]",
		"[null]",
		"[true]",
		"[false]",
		asArray(BOXED_BOOLEANS)
	};
	public static final String[] BOXED_BYTE_ARRAYS = {
		"null",
		"[]",
		"[null]",
		"[1]",
		"[0]",
		"[-1]",
		"[127]",
		"[-128]",
		asArray(BOXED_BYTES)
	};
	public static final String[] BOXED_SHORT_ARRAYS = {
		"null",
		"[]",
		"[null]",
		"[1]",
		"[0]",
		"[-1]",
		"[32767]",
		"[-32768]",
		asArray(BOXED_SHORTS)
	};
	public static final String[] BOXED_INT_ARRAYS = {
		"null",
		"[]",
		"[null]",
		"[1]",
		"[0]",
		"[-1]",
		"[2147483647]",
		"[-2147483648]",
		asArray(BOXED_INTEGERS)
	};
	public static final String[] BOXED_LONG_ARRAYS = {
		"null",
		"[]",
		"[null]",
		"[1]",
		"[0]",
		"[-1]",
		"[9223372036854775807]",
		"[-9223372036854775808]",
		asArray(BOXED_LONGS),
	};
	public static final String[] BOXED_FLOAT_ARRAYS = {
		"null",
		"[]",
		"[null]",
		"[1.0]",
		"[0.0]",
		"[-1.0]",
		"[3.4028235E38]",
		"[-3.4028235E38]",
		asArray(BOXED_FLOATS)
	};
	public static final String[] BOXED_DOUBLE_ARRAYS = {
		"null",
		"[]",
		"[null]",
		"[1.0]",
		"[0.0]",
		"[-1.0]",
		"[1.7976931348623157E308]",
		"[-1.7976931348623157E308]",
		asArray(BOXED_DOUBLES),
	};
	public static final String[] STRING_ARRAYS = {
		"null",
		"[]",
		"[null]",
		"[\"abc\"]",
		asArray(STRINGS),
	};
	public static final String[] ENUM_ARRAYS = {
		"null",
		"[]",
		"[null]",
		"[\"SOME_VALUE\"]",
		"[\"ANOTHER_VALUE\"]",
		asArray(ENUMS),
	};

	// Scalar maps
	public static final String[] STRING_BOOLEAN_MAPS = {
		"null",
		"{ }",
		"{\"a\": null}",
		"{\"a\": true}",
		"{\"a\": false}",
		"{\"a\": true, \"b\": null, \"c\": false}",
	};
	public static final String[] STRING_BYTE_MAPS = {
		"null",
		"{ }",
		"{\"a\": null}",
		"{\"a\": 0}",
		"{\"a\": 1}",
		"{\"a\": 0, \"b\": null, \"c\": 2}",
	};
	public static final String[] STRING_SHORT_MAPS = {
		"null",
		"{ }",
		"{\"a\": null}",
		"{\"a\": 0}",
		"{\"a\": 1}",
		"{\"a\": 0, \"b\": null, \"c\": 2}",
	};
	public static final String[] STRING_INT_MAPS = {
		"null",
		"{ }",
		"{\"a\": null}",
		"{\"a\": 0}",
		"{\"a\": 1}",
		"{\"a\": 0, \"b\": null, \"c\": 2}",
	};
	public static final String[] STRING_LONG_MAPS = {
		"null",
		"{ }",
		"{\"a\": null}",
		"{\"a\": 0}",
		"{\"a\": 1}",
		"{\"a\": 0, \"b\": null, \"c\": 2}",
	};
	public static final String[] STRING_CHAR_MAPS = {
		"null",
		"{ }",
		"{\"a\": null}",
		"{\"a\": \"a\"}",
		"{\"a\": \"b\"}",
		"{\"a\": \"a\", \"b\": null, \"c\": \"c\"}",
	};
	public static final String[] STRING_FLOAT_MAPS = {
		"null",
		"{ }",
		"{\"a\": null}",
		"{\"a\": 0.0}",
		"{\"a\": 1.0}",
		"{\"a\": 0.0, \"b\": null, \"c\": 2.0, \"d\": \"NaN\", \"e\": \"Infinity\", \"f\": \"-Infinity\"}",
	};
	public static final String[] STRING_DOUBLE_MAPS = {
		"null",
		"{ }",
		"{\"a\": null}",
		"{\"a\": 0.0}",
		"{\"a\": 1.0}",
		"{\"a\": 0.0, \"b\": null, \"c\": 2.0, \"d\": \"NaN\", \"e\": \"Infinity\", \"f\": \"-Infinity\"}",
	};
	public static final String[] STRING_STRING_MAPS = {
		"null",
		"{ }",
		"{\"a\": null}",
		"{\"a\": \"a\"}",
		"{\"a\": \"b\"}",
		"{\"a\": \"a\", \"b\": null, \"c\": \"c\"}",
	};
	public static final String[] STRING_ENUM_MAPS = {
		"null",
		"{ }",
		"{\"a\": null}",
		"{\"a\": \"SOME_VALUE\"}",
		"{\"a\": \"ANOTHER_VALUE\"}",
		"{\"a\": \"SOME_VALUE\", \"b\": null, \"c\": \"ANOTHER_VALUE\"}",
	};

	static String asArray(String[] values) {
		return "[" + String.join(",", values) + "]";
	}
}
