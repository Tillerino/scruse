package org.tillerino.scruse.tests.base.delegate;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CodeAssertions;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.JsonData;

class ScalarArraysReaderTest {
	ScalarArraysReader impl = new ScalarArraysReaderImpl();

	@Test
	public void testBooleanArray() throws Exception {
		for (String json : JsonData.BOOLEAN_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBooleanArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readBooleanArray")
			.calls("readPrimitiveBooleanX");
	}

	@Test
	public void testByteArray() throws Exception {
		for (String json : JsonData.BYTE_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readByteArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readByteArray")
			.calls("readPrimitiveByteX");
	}

	@Test
	public void testShortArray() throws Exception {
		for (String json : JsonData.SHORT_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readShortArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readShortArray")
			.calls("readPrimitiveShortX");
	}

	@Test
	public void testIntArray() throws Exception {
		for (String json : JsonData.INT_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readIntArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readIntArray")
			.calls("readPrimitiveIntX");
	}

	@Test
	public void testLongArray() throws Exception {
		for (String json : JsonData.LONG_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readLongArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readLongArray")
			.calls("readPrimitiveLongX");
	}

	@Test
	public void testFloatArray() throws Exception {
		for (String json : JsonData.FLOAT_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readFloatArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readFloatArray")
			.calls("readPrimitiveFloatX");
	}

	@Test
	public void testDoubleArray() throws Exception {
		for (String json : JsonData.DOUBLE_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readDoubleArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readDoubleArray")
			.calls("readPrimitiveDoubleX");
	}

	@Test
	public void testBoxedBooleanArray() throws Exception {
		for (String json : JsonData.BOXED_BOOLEAN_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedBooleanArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readBoxedBooleanArray")
			.calls("readBoxedBooleanX");
	}

	@Test
	public void testBoxedByteArray() throws Exception {
		for (String json : JsonData.BOXED_BYTE_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedByteArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readBoxedByteArray")
			.calls("readBoxedByteX");
	}

	@Test
	public void testBoxedShortArray() throws Exception {
		for (String json : JsonData.BOXED_SHORT_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedShortArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readBoxedShortArray")
			.calls("readBoxedShortX");
	}

	@Test
	public void testBoxedIntArray() throws Exception {
		for (String json : JsonData.BOXED_INT_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedIntArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readBoxedIntArray")
			.calls("readBoxedIntX");
	}

	@Test
	public void testBoxedLongArray() throws Exception {
		for (String json : JsonData.BOXED_LONG_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedLongArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readBoxedLongArray")
			.calls("readBoxedLongX");
	}

	@Test
	public void testBoxedFloatArray() throws Exception {
		for (String json : JsonData.BOXED_FLOAT_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedFloatArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readBoxedFloatArray")
			.calls("readBoxedFloatX");
	}

	@Test
	public void testBoxedDoubleArray() throws Exception {
		for (String json : JsonData.BOXED_DOUBLE_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBoxedDoubleArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readBoxedDoubleArray")
			.calls("readBoxedDoubleX");
	}

	@Test
	public void testStringArray() throws Exception {
		for (String json : JsonData.STRING_ARRAYS) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readStringArray, new TypeReference<>() {
			});
		}
		CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class)
			.method("readStringArray")
			.calls("readStringX");
	}
}
