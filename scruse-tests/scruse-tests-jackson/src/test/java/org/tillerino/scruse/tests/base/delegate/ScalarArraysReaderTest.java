package org.tillerino.scruse.tests.base.delegate;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CodeAssertions;
import org.tillerino.scruse.tests.JsonData;
import org.tillerino.scruse.tests.TestSettings;

import static org.tillerino.scruse.tests.InputUtils.assertIsEqualToDatabind;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

class ScalarArraysReaderTest {
	ScalarArraysReader impl = new ScalarArraysReaderImpl();

	@Test
	public void testBooleanArray() throws Exception {
		for (String json : JsonData.BOOLEAN_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readBooleanArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readBooleanArray", "readPrimitiveBooleanX", true);
	}

	@Test
	public void testByteArray() throws Exception {
		for (String json : JsonData.BYTE_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readByteArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readByteArray", "readPrimitiveByteX", true);
	}

	@Test
	public void testShortArray() throws Exception {
		for (String json : JsonData.SHORT_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readShortArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readShortArray", "readPrimitiveShortX", true);
	}

	@Test
	public void testIntArray() throws Exception {
		for (String json : JsonData.INT_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readIntArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readIntArray", "readPrimitiveIntX", !SETTINGS.canReadIntArrayNatively());
	}

	@Test
	public void testLongArray() throws Exception {
		for (String json : JsonData.LONG_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readLongArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readLongArray", "readPrimitiveLongX", !SETTINGS.canReadLongArrayNatively());
	}

	@Test
	public void testFloatArray() throws Exception {
		for (String json : JsonData.FLOAT_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readFloatArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readFloatArray", "readPrimitiveFloatX", true);
	}

	@Test
	public void testDoubleArray() throws Exception {
		for (String json : JsonData.DOUBLE_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readDoubleArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readDoubleArray", "readPrimitiveDoubleX", true);
	}

	@Test
	public void testBoxedBooleanArray() throws Exception {
		for (String json : JsonData.BOXED_BOOLEAN_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readBoxedBooleanArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readBoxedBooleanArray", "readBoxedBooleanX", true);
	}

	@Test
	public void testBoxedByteArray() throws Exception {
		for (String json : JsonData.BOXED_BYTE_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readBoxedByteArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readBoxedByteArray", "readBoxedByteX", true);
	}

	@Test
	public void testBoxedShortArray() throws Exception {
		for (String json : JsonData.BOXED_SHORT_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readBoxedShortArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readBoxedShortArray", "readBoxedShortX", true);
	}

	@Test
	public void testBoxedIntArray() throws Exception {
		for (String json : JsonData.BOXED_INT_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readBoxedIntArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readBoxedIntArray", "readBoxedIntX", true);
	}

	@Test
	public void testBoxedLongArray() throws Exception {
		for (String json : JsonData.BOXED_LONG_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readBoxedLongArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readBoxedLongArray", "readBoxedLongX", true);
	}

	@Test
	public void testBoxedFloatArray() throws Exception {
		for (String json : JsonData.BOXED_FLOAT_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readBoxedFloatArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readBoxedFloatArray", "readBoxedFloatX", true);
	}

	@Test
	public void testBoxedDoubleArray() throws Exception {
		for (String json : JsonData.BOXED_DOUBLE_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readBoxedDoubleArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readBoxedDoubleArray", "readBoxedDoubleX", true);
	}

	@Test
	public void testStringArray() throws Exception {
		for (String json : JsonData.STRING_ARRAYS) {
			assertIsEqualToDatabind(json, impl::readStringArray, new TypeReference<>() {
			});
		}
		assertThatCalls("readStringArray", "readStringX", !SETTINGS.canReadStringArrayNatively());
	}

	private static void assertThatCalls(String caller, String callee, boolean doesCall) throws Exception {
		CodeAssertions.MethodAssert method = CodeAssertions.assertThatCode(ScalarArraysReaderImpl.class).method(caller);
		if (doesCall) {
			method.calls(callee);
		} else {
			method.doesNotCall(callee);
		}
	}
}
