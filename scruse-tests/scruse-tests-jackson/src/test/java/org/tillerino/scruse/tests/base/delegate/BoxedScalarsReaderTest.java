package org.tillerino.scruse.tests.base.delegate;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.JsonData;

import static org.tillerino.scruse.tests.CodeAssertions.assertThatCode;
import static org.tillerino.scruse.tests.InputUtils.assertIsEqualToDatabind;

public class BoxedScalarsReaderTest {
	BoxedScalarsReader impl = new BoxedScalarsReaderImpl();

	@Test
	void testBoxedBoolean() throws Exception {
		for (String json : JsonData.BOXED_BOOLEANS) {
			assertIsEqualToDatabind(json, impl::readBoxedBooleanX, new TypeReference<>() {
			});
		}

		assertThatCode(BoxedScalarsReaderImpl.class)
			.method("readBoxedBooleanX")
			.calls("readPrimitiveBooleanX");
	}

	@Test
	void testBoxedByte() throws Exception {
		for (String json : JsonData.BOXED_BYTES) {
			assertIsEqualToDatabind(json, impl::readBoxedByteX, new TypeReference<>() {
			});
		}

		assertThatCode(BoxedScalarsReaderImpl.class)
			.method("readBoxedByteX")
			.calls("readPrimitiveByteX");
	}

	@Test
	void testBoxedShort() throws Exception {
		for (String json : JsonData.BOXED_SHORTS) {
			assertIsEqualToDatabind(json, impl::readBoxedShortX, new TypeReference<>() {
			});
		}

		assertThatCode(BoxedScalarsReaderImpl.class)
			.method("readBoxedShortX")
			.calls("readPrimitiveShortX");
	}

	@Test
	void testBoxedInt() throws Exception {
		for (String json : JsonData.BOXED_INTEGERS) {
			assertIsEqualToDatabind(json, impl::readBoxedIntX, new TypeReference<>() {
			});
		}

		assertThatCode(BoxedScalarsReaderImpl.class)
			.method("readBoxedIntX")
			.calls("readPrimitiveIntX");
	}

	@Test
	void testBoxedLong() throws Exception {
		for (String json : JsonData.BOXED_LONGS) {
			assertIsEqualToDatabind(json, impl::readBoxedLongX, new TypeReference<>() {
			});
		}

		assertThatCode(BoxedScalarsReaderImpl.class)
			.method("readBoxedLongX")
			.calls("readPrimitiveLongX");
	}

	@Test
	void testBoxedChar() throws Exception {
		for (String json : JsonData.BOXED_CHARS) {
			assertIsEqualToDatabind(json, impl::readBoxedCharX, new TypeReference<>() {
			});
		}

		assertThatCode(BoxedScalarsReaderImpl.class)
			.method("readBoxedCharX")
			.calls("readPrimitiveCharX");
	}

	@Test
	void testBoxedFloat() throws Exception {
		for (String json : JsonData.BOXED_FLOATS) {
			assertIsEqualToDatabind(json, impl::readBoxedFloatX, new TypeReference<>() {
			});
		}

		assertThatCode(BoxedScalarsReaderImpl.class)
			.method("readBoxedFloatX")
			.calls("readPrimitiveFloatX");
	}

	@Test
	void testBoxedDouble() throws Exception {
		for (String json : JsonData.BOXED_DOUBLES) {
			assertIsEqualToDatabind(json, impl::readBoxedDoubleX, new TypeReference<>() {
			});
		}

		assertThatCode(BoxedScalarsReaderImpl.class)
			.method("readBoxedDoubleX")
			.calls("readPrimitiveDoubleX");
	}
}
