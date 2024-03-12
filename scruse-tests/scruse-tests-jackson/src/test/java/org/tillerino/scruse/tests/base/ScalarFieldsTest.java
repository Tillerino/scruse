package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.model.AnEnum;

import java.io.IOException;
import java.util.List;

class ScalarFieldsTest {

	@Test
	void scalarFieldsRecordOutput() throws IOException {
		List<ScalarFieldsRecord> values = List.of(
			new ScalarFieldsRecord(false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, "six", AnEnum.SOME_VALUE),
			new ScalarFieldsRecord(false, (byte) 1, (short) 2, 3, 4L, 'c', Float.NaN, Float.NaN, null, null, null, null, null, null, null, null, null, null)
		);

		ScalarFieldsRecord.Serde impl = new ScalarFieldsRecord$SerdeImpl();

		for (ScalarFieldsRecord object : values) {
			OutputUtils.assertIsEqualToDatabind(object, impl::write);
		}
	}

	@Test
	void scalarFieldsClassOutput() throws IOException {
		List<ScalarFieldsClass> values = List.of(
			new ScalarFieldsClass(false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, "six", AnEnum.SOME_VALUE),
			new ScalarFieldsClass(false, (byte) 1, (short) 2, 3, 4L, 'c', Float.NaN, Float.NaN, null, null, null, null, null, null, null, null, null, null)
		);

		ScalarFieldsClass.Serde impl = new ScalarFieldsClass$SerdeImpl();

		for (ScalarFieldsClass object : values) {
			OutputUtils.assertIsEqualToDatabind(object, impl::write);
		}
	}

	@Test
	void scalarAccessorsClassOutput() throws IOException {
		List<ScalarAccessorsClass> values = List.of(
			new ScalarAccessorsClass(false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, "six", AnEnum.SOME_VALUE),
			new ScalarAccessorsClass(false, (byte) 1, (short) 2, 3, 4L, 'c', Float.NaN, Float.NaN, null, null, null, null, null, null, null, null, null, null)
		);

		ScalarAccessorsClass.Serde impl = new ScalarAccessorsClass$SerdeImpl();

		for (ScalarAccessorsClass object : values) {
			OutputUtils.assertIsEqualToDatabind(object, impl::write);
		}
	}

	@Test
	void scalarFieldsRecordInput() throws IOException {
		TypeReference<ScalarFieldsRecord> typeRef = new TypeReference<>() {
		};

		ScalarFieldsRecord.Serde impl = new ScalarFieldsRecord$SerdeImpl();

		String[] jsons = {
			"null",
			"{}",
			"{\"bo\":false,\"by\":1,\"s\":2,\"i\":3,\"l\":4,\"c\":\"c\",\"f\":\"NaN\",\"d\":\"NaN\",\"bbo\":null,\"bby\":null,\"ss\":null,\"ii\":null,\"ll\":null,\"cc\":null,\"ff\":null,\"dd\":null,\"str\":null,\"en\":null}",
			"{\"bo\":false,\"by\":1,\"s\":2,\"i\":3,\"l\":4,\"c\":\"c\",\"f\":4.0,\"d\":5.0,\"bbo\":false,\"bby\":1,\"ss\":2,\"ii\":3,\"ll\":4,\"cc\":\"c\",\"ff\":4.0,\"dd\":5.0,\"str\":\"six\",\"en\":\"SOME_VALUE\"}"
		};
		for (String json : jsons) {
			InputUtils.assertIsEqualToDatabind(json, impl::read, typeRef);
		}
	}

	@Test
	void scalarFieldsClassInput() throws IOException {
		TypeReference<ScalarFieldsClass> typeRef = new TypeReference<>() {
		};

		ScalarFieldsClass.Serde impl = new ScalarFieldsClass$SerdeImpl();

		String[] jsons = {
			"null",
			"{}",
			"{\"bo\":false,\"by\":1,\"s\":2,\"i\":3,\"l\":4,\"c\":\"c\",\"f\":\"NaN\",\"d\":\"NaN\",\"bbo\":null,\"bby\":null,\"ss\":null,\"ii\":null,\"ll\":null,\"cc\":null,\"ff\":null,\"dd\":null,\"str\":null,\"en\":null}",
			"{\"bo\":false,\"by\":1,\"s\":2,\"i\":3,\"l\":4,\"c\":\"c\",\"f\":4.0,\"d\":5.0,\"bbo\":false,\"bby\":1,\"ss\":2,\"ii\":3,\"ll\":4,\"cc\":\"c\",\"ff\":4.0,\"dd\":5.0,\"str\":\"six\",\"en\":\"SOME_VALUE\"}"
		};
		for (String json : jsons) {
			InputUtils.assertIsEqualToDatabind(json, impl::read, typeRef);
		}
	}

	@Test
	void scalarAccessorsClassInput() throws IOException {
		TypeReference<ScalarAccessorsClass> typeRef = new TypeReference<>() {
		};

		ScalarAccessorsClass.Serde impl = new ScalarAccessorsClass$SerdeImpl();

		String[] jsons = {
			"null",
			"{}",
			"{\"bo\":false,\"by\":1,\"s\":2,\"i\":3,\"l\":4,\"c\":\"c\",\"f\":\"NaN\",\"d\":\"NaN\",\"bbo\":null,\"bby\":null,\"ss\":null,\"ii\":null,\"ll\":null,\"cc\":null,\"ff\":null,\"dd\":null,\"str\":null,\"en\":null}",
			"{\"bo\":false,\"by\":1,\"s\":2,\"i\":3,\"l\":4,\"c\":\"c\",\"f\":4.0,\"d\":5.0,\"bbo\":false,\"bby\":1,\"ss\":2,\"ii\":3,\"ll\":4,\"cc\":\"c\",\"ff\":4.0,\"dd\":5.0,\"str\":\"six\",\"en\":\"SOME_VALUE\"}"
		};
		for (String json : jsons) {
			InputUtils.assertIsEqualToDatabind(json, impl::read, typeRef);
		}
	}
}
