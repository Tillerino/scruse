package org.tillerino.scruse.tests.base.generics;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CodeAssertions;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.model.GenericRecord;

class GenericsTest {
	GenericRecordSerde genericRecordSerde = new GenericRecordSerdeImpl();

	StringRecordSerde stringRecordSerde = new StringRecordSerdeImpl();

	IntegerRecordSerde integerRecordSerde = new IntegerRecordSerdeImpl();

	@Test
	void passGenericImplExplicitlyOutput() throws Exception {
		OutputUtils.assertIsEqualToDatabind2(new GenericRecord<>("x"),
			new StringSerdeImpl(),
			genericRecordSerde::writeGenericRecord);

		CodeAssertions.assertThatCode(GenericRecordSerdeImpl.class)
				.method("writeGenericRecord")
				.calls("writeOnGenericInterface");
	}

	@Test
	void passGenericImplExplicitlyInput() throws Exception {
		InputUtils.assertIsEqualToDatabind2("{ \"f\": \"x\" }",
			new StringSerdeImpl(),
			genericRecordSerde::readGenericRecord,
			new TypeReference<>() {
			});

		CodeAssertions.assertThatCode(GenericRecordSerdeImpl.class)
			.method("readGenericRecord")
			.calls("readOnGenericInterface");
	}

	@Test
	void takeGenericImplFromDelegateesOutput() throws Exception {
		OutputUtils.assertIsEqualToDatabind(new GenericRecord<>("x"), stringRecordSerde::writeStringRecord);

		CodeAssertions.assertThatCode(StringRecordSerdeImpl.class)
			.method("writeStringRecord")
			.calls("writeGenericRecord");
	}

	@Test
	void takeGenericImplFromDelegateesInput() throws Exception {
		InputUtils.assertIsEqualToDatabind("{ \"f\": \"x\" }", stringRecordSerde::readStringRecord, new TypeReference<>() {
		});

		CodeAssertions.assertThatCode(StringRecordSerdeImpl.class)
			.method("readStringRecord")
			.calls("readGenericRecord");
	}

	@Test
	void createLambdaFromDelegateesOutput() throws Exception {
		OutputUtils.assertIsEqualToDatabind(new GenericRecord<>(1), integerRecordSerde::writeIntegerRecord);

		CodeAssertions.assertThatCode(IntegerRecordSerdeImpl.class)
			.method("writeIntegerRecord")
				.calls("writeGenericRecord")
				.bodyContains("::writeBoxedIntX");
	}

	@Test
	void createLambdaFromDelegateesInput() throws Exception {
		InputUtils.assertIsEqualToDatabind("{ \"f\": 1 }", integerRecordSerde::readIntegerRecord, new TypeReference<>() {
		});

		CodeAssertions.assertThatCode(IntegerRecordSerdeImpl.class)
			.method("readIntegerRecord")
			.calls("readGenericRecord")
			.bodyContains("::readBoxedInt");
	}
}
