package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.model.PrimitiveArrayFieldsRecord;

import java.io.IOException;

class ArrayFieldsRecordTest {
	ArrayFieldsRecordSerde serde = new ArrayFieldsRecordSerdeImpl();

	@Test
	void roundtripPrimitive() throws IOException {
		for (PrimitiveArrayFieldsRecord record : PrimitiveArrayFieldsRecord.INSTANCES) {
			String json = OutputUtils.assertIsEqualToDatabind(record, serde::writePrimitive);
			InputUtils.assertIsEqualToDatabindComparingRecursively(json, serde::readPrimitive, new TypeReference<PrimitiveArrayFieldsRecord>() {
			});
		}
	}

	@Test
	void roundtripReference() throws IOException {
		for (PrimitiveArrayFieldsRecord record : PrimitiveArrayFieldsRecord.INSTANCES) {
			String json = OutputUtils.assertIsEqualToDatabind(record, serde::writePrimitive);
			InputUtils.assertIsEqualToDatabindComparingRecursively(json, serde::readPrimitive, new TypeReference<PrimitiveArrayFieldsRecord>() {
			});
		}
	}
}