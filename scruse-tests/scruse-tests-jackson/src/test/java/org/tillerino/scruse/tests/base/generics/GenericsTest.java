package org.tillerino.scruse.tests.base.generics;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CodeAssertions;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.model.GenericRecord;

import java.io.IOException;

class GenericsTest {
	GenericRecordSerde genericRecordSerde = new GenericRecordSerdeImpl();

	StringRecordSerde stringRecordSerde = new StringRecordSerdeImpl();

	@Test
	void genericFieldOutput() throws Exception {
		OutputUtils.assertIsEqualToDatabind2(new GenericRecord<>("x"),
			new StringSerdeImpl(),
			genericRecordSerde::writeGenericRecord);

		CodeAssertions.assertThatCode(GenericRecordSerdeImpl.class)
				.method("writeGenericRecord")
				.calls("writeOnGenericInterface");
	}

	@Test
	void perDelegateedFieldOutput() throws Exception {
		OutputUtils.assertIsEqualToDatabind(new GenericRecord<>("x"),
			stringRecordSerde::writeStringRecord);

		CodeAssertions.assertThatCode(StringRecordSerdeImpl.class)
			.method("writeStringRecord")
			.calls("writeGenericRecord");
	}
}
