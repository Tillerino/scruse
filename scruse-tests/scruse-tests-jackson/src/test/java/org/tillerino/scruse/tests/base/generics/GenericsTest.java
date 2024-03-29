package org.tillerino.scruse.tests.base.generics;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CodeAssertions;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.model.GenericRecord;

class GenericsTest {
	GenericRecordSerde genericRecordSerde = new GenericRecordSerdeImpl();

	StringRecordSerde stringRecordSerde = new StringRecordSerdeImpl();

	IntegerRecordSerde integerRecordSerde = new IntegerRecordSerdeImpl();

	@Test
	void passGenericImplExplicitly() throws Exception {
		OutputUtils.assertIsEqualToDatabind2(new GenericRecord<>("x"),
			new StringSerdeImpl(),
			genericRecordSerde::writeGenericRecord);

		CodeAssertions.assertThatCode(GenericRecordSerdeImpl.class)
				.method("writeGenericRecord")
				.calls("writeOnGenericInterface");
	}

	@Test
	void takeGenericImplFromDelegatees() throws Exception {
		OutputUtils.assertIsEqualToDatabind(new GenericRecord<>("x"), stringRecordSerde::writeStringRecord);

		CodeAssertions.assertThatCode(StringRecordSerdeImpl.class)
			.method("writeStringRecord")
			.calls("writeGenericRecord");
	}

	@Test
	void createLambdaFromDelegatees() throws Exception {
		OutputUtils.assertIsEqualToDatabind(new GenericRecord<>(1), integerRecordSerde::writeIntegerRecord);

		CodeAssertions.assertThatCode(IntegerRecordSerdeImpl.class)
			.method("writeIntegerRecord")
				.calls("writeGenericRecord")
				.bodyContains("::writeBoxedIntX");
	}
}
