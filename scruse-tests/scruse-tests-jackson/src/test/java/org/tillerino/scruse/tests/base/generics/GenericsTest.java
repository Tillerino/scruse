package org.tillerino.scruse.tests.base.generics;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.model.GenericRecord;

import java.io.IOException;

class GenericsTest {
	GenericRecordSerde genericRecordSerde = new GenericRecordSerdeImpl();

	@Test
	void genericFieldOutput() throws IOException {
		OutputUtils.assertIsEqualToDatabind2(new GenericRecord<>("x"),
			new GenericRecordSerde$StringSerdeImpl(),
			genericRecordSerde::writeGenericRecord);

	}
}
