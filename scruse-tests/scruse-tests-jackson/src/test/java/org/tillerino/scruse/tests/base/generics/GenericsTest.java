package org.tillerino.scruse.tests.base.generics;

import static org.tillerino.scruse.tests.CodeAssertions.assertThatCode;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.SerdeUtil;
import org.tillerino.scruse.tests.model.GenericRecord;

class GenericsTest extends ReferenceTest {
    GenericRecordSerde genericRecordSerde = SerdeUtil.impl(GenericRecordSerde.class);

    StringRecordSerde stringRecordSerde = SerdeUtil.impl(StringRecordSerde.class);

    IntegerRecordSerde integerRecordSerde = SerdeUtil.impl(IntegerRecordSerde.class);

    @Test
    void passGenericImplExplicitly() throws Exception {
        outputUtils.roundTrip2(
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
        outputUtils.roundTrip(
                new GenericRecord<>("x"),
                stringRecordSerde::writeStringRecord,
                stringRecordSerde::readStringRecord,
                new TypeReference<>() {});

        assertThatCode(StringRecordSerdeImpl.class).method("writeStringRecord").calls("writeGenericRecord");
        assertThatCode(StringRecordSerdeImpl.class).method("readStringRecord").calls("readGenericRecord");
    }

    @Test
    void createLambdaFromDelegatees() throws Exception {
        outputUtils.roundTrip(
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
