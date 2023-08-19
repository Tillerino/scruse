package org.tillerino.scruse.tests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.tillerino.scruse.tests.OutputUtils.*;

class FieldsTest {

	@Test
	void scalarFields() throws IOException {
		List<ScalarFieldsRecord> values = List.of(
			new ScalarFieldsRecord(false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, "six"),
			new ScalarFieldsRecord(false, (byte) 1, (short) 2, 3, 4L, 'c', Float.NaN, Float.NaN, null, null, null, null, null, null, null, null, null)
		);

		ScalarFieldsRecord.Output impl = new ScalarFieldsRecord$OutputImpl();

		for (ScalarFieldsRecord object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::write);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::write);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::write);
		}
	}

	@Test
	void scalarFieldsClass() throws IOException {
		List<ScalarFieldsClass> values = List.of(
			new ScalarFieldsClass(false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, "six"),
			new ScalarFieldsClass(false, (byte) 1, (short) 2, 3, 4L, 'c', Float.NaN, Float.NaN, null, null, null, null, null, null, null, null, null)
		);

		ScalarFieldsClass.Output impl = new ScalarFieldsClass$OutputImpl();

		for (ScalarFieldsClass object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::write);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::write);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::write);
		}
	}

	@Test
	void scalarAccessorsClass() throws IOException {
		List<ScalarAccessorsClass> values = List.of(
			new ScalarAccessorsClass(false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, "six"),
			new ScalarAccessorsClass(false, (byte) 1, (short) 2, 3, 4L, 'c', Float.NaN, Float.NaN, null, null, null, null, null, null, null, null, null)
		);

		ScalarAccessorsClass.Output impl = new ScalarAccessorsClass$OutputImpl();

		for (ScalarAccessorsClass object : values) {
			assertThatJacksonJsonGeneratorIsEqualToDatabind(object, impl::write);
			assertThatGsonJsonWriterIsEqualToDatabind(object, impl::write);
			assertThatJacksonJsonNodeIsEqualToDatabind(object, impl::write);
		}
	}
}
