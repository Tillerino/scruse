package org.tillerino.scruse.tests.base.generics;

import static org.tillerino.scruse.tests.CodeAssertions.assertThatCode;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.model.GenericRecord;

class GenericsTest {
    GenericRecordSerde genericRecordSerde = new GenericRecordSerdeImpl();

    StringRecordSerde stringRecordSerde = new StringRecordSerdeImpl();

    IntegerRecordSerde integerRecordSerde = new IntegerRecordSerdeImpl();

    @Test
    void passGenericImplExplicitly() throws Exception {
        OutputUtils.roundTrip2(
                new GenericRecord<>("x"),
                new StringSerdeImpl(),
                genericRecordSerde::writeGenericRecord,
                genericRecordSerde::readGenericRecord,
                new TypeReference<>() {});

        assertThatCode(GenericRecordSerdeImpl.class)
                .method("writeGenericRecord")
                .calls("writeOnGenericInterface");

        assertThatCode(GenericRecordSerdeImpl.class).method("readGenericRecord").calls("readOnGenericInterface");
    }

    @Test
    void takeGenericImplFromDelegatees() throws Exception {
        OutputUtils.roundTrip(
                new GenericRecord<>("x"),
                stringRecordSerde::writeStringRecord,
                stringRecordSerde::readStringRecord,
                new TypeReference<>() {});

        assertThatCode(StringRecordSerdeImpl.class).method("writeStringRecord").calls("writeGenericRecord");
        assertThatCode(StringRecordSerdeImpl.class).method("readStringRecord").calls("readGenericRecord");
    }

    @Test
    void createLambdaFromDelegatees() throws Exception {
        OutputUtils.roundTrip(
                new GenericRecord<>(1),
                integerRecordSerde::writeIntegerRecord,
                integerRecordSerde::readIntegerRecord,
                new TypeReference<>() {});

        assertThatCode(IntegerRecordSerdeImpl.class)
                .method("writeIntegerRecord")
                .calls("writeGenericRecord")
                .bodyContains("::writeBoxedIntX");
        assertThatCode(IntegerRecordSerdeImpl.class)
                .method("readIntegerRecord")
                .calls("readGenericRecord")
                .bodyContains("::readBoxedInt");
    }
}
