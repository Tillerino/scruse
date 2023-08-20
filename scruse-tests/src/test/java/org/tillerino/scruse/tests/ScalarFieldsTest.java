package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.tillerino.scruse.tests.OutputUtils.*;

class ScalarFieldsTest {

	@Test
	void scalarFieldsRecordOutput() throws IOException {
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
	void scalarFieldsClassOutput() throws IOException {
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
	void scalarAccessorsClassOutput() throws IOException {
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

	@Test
	void scalarFieldsRecordInput() throws IOException {
		TypeReference<ScalarFieldsRecord> typeRef = new TypeReference<>() {
		};

		ScalarFieldsRecord.Input impl = new ScalarFieldsRecord$InputImpl();

		String[] jsons = {
			"null",
			"{}",
			"{\"bo\":false,\"by\":1,\"s\":2,\"i\":3,\"l\":4,\"c\":\"c\",\"f\":\"NaN\",\"d\":\"NaN\",\"bbo\":null,\"bby\":null,\"ss\":null,\"ii\":null,\"ll\":null,\"cc\":null,\"ff\":null,\"dd\":null,\"str\":null}",
			"{\"bo\":false,\"by\":1,\"s\":2,\"i\":3,\"l\":4,\"c\":\"c\",\"f\":4.0,\"d\":5.0,\"bbo\":false,\"bby\":1,\"ss\":2,\"ii\":3,\"ll\":4,\"cc\":\"c\",\"ff\":4.0,\"dd\":5.0,\"str\":\"six\"}"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::read, typeRef);
		}
	}

	@Test
	void scalarFieldsClassInput() throws IOException {
		TypeReference<ScalarFieldsClass> typeRef = new TypeReference<>() {
		};

		ScalarFieldsClass.Input impl = new ScalarFieldsClass$InputImpl();

		String[] jsons = {
			"null",
			"{}",
			"{\"bo\":false,\"by\":1,\"s\":2,\"i\":3,\"l\":4,\"c\":\"c\",\"f\":\"NaN\",\"d\":\"NaN\",\"bbo\":null,\"bby\":null,\"ss\":null,\"ii\":null,\"ll\":null,\"cc\":null,\"ff\":null,\"dd\":null,\"str\":null}",
			"{\"bo\":false,\"by\":1,\"s\":2,\"i\":3,\"l\":4,\"c\":\"c\",\"f\":4.0,\"d\":5.0,\"bbo\":false,\"bby\":1,\"ss\":2,\"ii\":3,\"ll\":4,\"cc\":\"c\",\"ff\":4.0,\"dd\":5.0,\"str\":\"six\"}"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::read, typeRef);
		}
	}

	@Test
	void scalarAccessorsClassInput() throws IOException {
		TypeReference<ScalarAccessorsClass> typeRef = new TypeReference<>() {
		};

		ScalarAccessorsClass.Input impl = new ScalarAccessorsClass$InputImpl();

		String[] jsons = {
			"null",
			"{}",
			"{\"bo\":false,\"by\":1,\"s\":2,\"i\":3,\"l\":4,\"c\":\"c\",\"f\":\"NaN\",\"d\":\"NaN\",\"bbo\":null,\"bby\":null,\"ss\":null,\"ii\":null,\"ll\":null,\"cc\":null,\"ff\":null,\"dd\":null,\"str\":null}",
			"{\"bo\":false,\"by\":1,\"s\":2,\"i\":3,\"l\":4,\"c\":\"c\",\"f\":4.0,\"d\":5.0,\"bbo\":false,\"bby\":1,\"ss\":2,\"ii\":3,\"ll\":4,\"cc\":\"c\",\"ff\":4.0,\"dd\":5.0,\"str\":\"six\"}"
		};
		for (String json : jsons) {
			InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::read, typeRef);
		}
	}
}
