package org.tillerino.scruse.tests.base.features;

import static org.tillerino.scruse.tests.CodeAssertions.assertThatCode;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.SerdeUtil;
import org.tillerino.scruse.tests.base.features.GenericsSerde.GenericRecordSerde;
import org.tillerino.scruse.tests.base.features.GenericsSerde.IntegerRecordSerde;
import org.tillerino.scruse.tests.base.features.GenericsSerde.StringRecordSerde;
import org.tillerino.scruse.tests.base.features.GenericsSerde.StringSerde;
import org.tillerino.scruse.tests.model.features.GenericsModel.GenericRecord;

class GenericsTest extends ReferenceTest {
    StringSerde stringSerde = SerdeUtil.impl(StringSerde.class);

    GenericRecordSerde genericRecordSerde = SerdeUtil.impl(GenericRecordSerde.class);

    StringRecordSerde stringRecordSerde = SerdeUtil.impl(StringRecordSerde.class);

    IntegerRecordSerde integerRecordSerde = SerdeUtil.impl(IntegerRecordSerde.class);

    @Test
    void passGenericImplExplicitly() throws Exception {
        outputUtils.roundTrip2(
                new GenericRecord<>("x"),
                stringSerde,
                genericRecordSerde::writeGenericRecord,
                genericRecordSerde::readGenericRecord,
                new TypeReference<>() {});

        assertThatCode(SerdeUtil.implClass(GenericRecordSerde.class))
                .method("writeGenericRecord")
                .calls("writeOnGenericInterface");

        assertThatCode(SerdeUtil.implClass(GenericRecordSerde.class))
                .method("readGenericRecord")
                .calls("readOnGenericInterface");
    }

    @Test
    void takeGenericImplFromDelegatees() throws Exception {
        outputUtils.roundTrip(
                new GenericRecord<>("x"),
                stringRecordSerde::writeStringRecord,
                stringRecordSerde::readStringRecord,
                new TypeReference<>() {});

        assertThatCode(SerdeUtil.implClass(StringRecordSerde.class))
                .method("writeStringRecord")
                .calls("writeGenericRecord");
        assertThatCode(SerdeUtil.implClass(StringRecordSerde.class))
                .method("readStringRecord")
                .calls("readGenericRecord");
    }

    @Test
    void createLambdaFromDelegatees() throws Exception {
        outputUtils.roundTrip(
                new GenericRecord<>(1),
                integerRecordSerde::writeIntegerRecord,
                integerRecordSerde::readIntegerRecord,
                new TypeReference<>() {});

        assertThatCode(SerdeUtil.implClass(IntegerRecordSerde.class))
                .method("writeIntegerRecord")
                .calls("writeGenericRecord")
                .bodyContains("::writeBoxedIntX");
        assertThatCode(SerdeUtil.implClass(IntegerRecordSerde.class))
                .method("readIntegerRecord")
                .calls("readGenericRecord")
                .bodyContains("::readBoxedInt");
    }
}
