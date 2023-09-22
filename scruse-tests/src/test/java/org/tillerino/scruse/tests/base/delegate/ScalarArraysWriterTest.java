package org.tillerino.scruse.tests.base.delegate;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CodeAssertions;
import org.tillerino.scruse.tests.JavaData;

import static org.tillerino.scruse.tests.OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind;

class ScalarArraysWriterTest {
	ScalarArraysWriter impl = new ScalarArraysWriterImpl();

	@Test
	void testBooleanArray() throws Exception {
		for (boolean[] object : JavaData.BOOLEAN_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBooleanArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeBooleanArray")
			.calls("writePrimitiveBooleanX");
	}

	@Test
	void testByteArray() throws Exception {
		for (byte[] object : JavaData.BYTE_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeByteArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeByteArray")
			.doesNotCall("writePrimitiveByteX");
	}

	@Test
	void testShortArray() throws Exception {
		for (short[] object : JavaData.SHORT_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeShortArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeShortArray")
			.calls("writePrimitiveShortX");
	}

	@Test
	void testIntArray() throws Exception {
		for (int[] object : JavaData.INT_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeIntArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeIntArray")
			.calls("writePrimitiveIntX");
	}

	@Test
	void testLongArray() throws Exception {
		for (long[] object : JavaData.LONG_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeLongArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeLongArray")
			.calls("writePrimitiveLongX");
	}

	@Test
	void testFloatArray() throws Exception {
		for (float[] object : JavaData.FLOAT_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeFloatArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeFloatArray")
			.calls("writePrimitiveFloatX");
	}

	@Test
	void testDoubleArray() throws Exception {
		for (double[] object : JavaData.DOUBLE_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeDoubleArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeDoubleArray")
			.calls("writePrimitiveDoubleX");
	}

	@Test
	void testBoxedBooleanArray() throws Exception {
		for (Boolean[] object : JavaData.BOXED_BOOLEAN_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedBooleanArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeBoxedBooleanArray")
			.calls("writeBoxedBooleanX");
	}

	@Test
	void testBoxedByteArray() throws Exception {
		for (Byte[] object : JavaData.BOXED_BYTE_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedByteArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeBoxedByteArray")
			.calls("writeBoxedByteX");
	}

	@Test
	void testBoxedShortArray() throws Exception {
		for (Short[] object : JavaData.BOXED_SHORT_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedShortArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeBoxedShortArray")
			.calls("writeBoxedShortX");
	}

	@Test
	void testBoxedIntArray() throws Exception {
		for (Integer[] object : JavaData.BOXED_INT_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedIntArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeBoxedIntArray")
			.calls("writeBoxedIntX");
	}

	@Test
	void testBoxedLongArray() throws Exception {
		for (Long[] object : JavaData.BOXED_LONG_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedLongArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeBoxedLongArray")
			.calls("writeBoxedLongX");
	}

	@Test
	void testBoxedFloatArray() throws Exception {
		for (Float[] object : JavaData.BOXED_FLOAT_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedFloatArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeBoxedFloatArray")
			.calls("writeBoxedFloatX");
	}

	@Test
	void testBoxedDoubleArray() throws Exception {
		for (Double[] object : JavaData.BOXED_DOUBLE_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeBoxedDoubleArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeBoxedDoubleArray")
			.calls("writeBoxedDoubleX");
	}

	@Test
	void testStringArray() throws Exception {
		for (String[] object : JavaData.STRING_ARRAYS) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::writeStringArray);
		}
		CodeAssertions.assertThatCode(ScalarArraysWriterImpl.class)
			.method("writeStringArray")
			.calls("writeStringX");
	}
}
