package org.tillerino.scruse.tests.base.features;

import static org.tillerino.scruse.tests.CodeAssertions.assertThatImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.SerdeUtil;
import org.tillerino.scruse.tests.base.features.GenericsSerde.*;
import org.tillerino.scruse.tests.model.features.GenericsModel.GenericRecord;
import org.tillerino.scruse.tests.model.features.GenericsModel.PointlessGenericsRecord;

class GenericsTest extends ReferenceTest {
    StringSerde stringSerde = SerdeUtil.impl(StringSerde.class);

    GenericRecordSerde genericRecordSerde = SerdeUtil.impl(GenericRecordSerde.class);

    StringRecordSerde stringRecordSerde = SerdeUtil.impl(StringRecordSerde.class);

    IntegerRecordSerde integerRecordSerde = SerdeUtil.impl(IntegerRecordSerde.class);

    DoubleListSerde doubleListSerde = SerdeUtil.impl(DoubleListSerde.class);

    GenericMapSerde genericMapSerde = SerdeUtil.impl(GenericMapSerde.class);

    PointlessGenericsSerde pointlessGenericsSerde = SerdeUtil.impl(PointlessGenericsSerde.class);

    @Test
    void passGenericImplExplicitly() throws Exception {
        outputUtils.roundTrip2(
                new GenericRecord<>("x"),
                stringSerde,
                genericRecordSerde::writeGenericRecord,
                genericRecordSerde::readGenericRecord,
                new TypeReference<>() {});

        assertThatImpl(GenericRecordSerde.class).method("writeGenericRecord").calls("writeOnGenericInterface");

        assertThatImpl(GenericRecordSerde.class).method("readGenericRecord").calls("readOnGenericInterface");
    }

    @Test
    void takeGenericImplFromDelegatees() throws Exception {
        outputUtils.roundTrip(
                new GenericRecord<>("x"),
                stringRecordSerde::writeStringRecord,
                stringRecordSerde::readStringRecord,
                new TypeReference<>() {});

        assertThatImpl(StringRecordSerde.class).method("writeStringRecord").calls("writeGenericRecord");
        assertThatImpl(StringRecordSerde.class).method("readStringRecord").calls("readGenericRecord");
    }

    @Test
    void createLambdaFromDelegatees() throws Exception {
        outputUtils.roundTrip(
                new GenericRecord<>(1),
                integerRecordSerde::writeIntegerRecord,
                integerRecordSerde::readIntegerRecord,
                new TypeReference<>() {});

        assertThatImpl(IntegerRecordSerde.class)
                .method("writeIntegerRecord")
                .calls("writeGenericRecord")
                .bodyContains("::writeBoxedIntX");
        assertThatImpl(IntegerRecordSerde.class)
                .method("readIntegerRecord")
                .calls("readGenericRecord")
                .bodyContains("::readBoxedInt");
    }

    @Test
    void genericListSerde() throws Exception {
        outputUtils.roundTrip(
                List.of(1.0, 2.0, 3.0),
                doubleListSerde::writeDoubleList,
                doubleListSerde::readDoubleList,
                new TypeReference<>() {});

        assertThatImpl(DoubleListSerde.class)
                .method("writeDoubleList")
                .calls("writeGenericList")
                .references("writeBoxedDoubleX");

        assertThatImpl(DoubleListSerde.class)
                .method("readDoubleList")
                .calls("readGenericList")
                .references("readBoxedDoubleX");
    }

    @Test
    void genericMapSerde() throws Exception {
        outputUtils.roundTrip(
                Map.of("a", 1.0, "b", 2.0, "c", 3.0),
                genericMapSerde::writeStringDoubleMap,
                genericMapSerde::readStringDoubleMap,
                new TypeReference<>() {});

        assertThatImpl(GenericMapSerde.class)
                .method("writeStringDoubleMap")
                .calls("writeGenericMap")
                .references("writeBoxedDoubleX");

        assertThatImpl(GenericMapSerde.class)
                .method("readStringDoubleMap")
                .calls("readGenericMap")
                .references("readBoxedDoubleX");
    }

    @Test
    void pointlessGenericsSerde() throws Exception {
        outputUtils.roundTrip(
                new PointlessGenericsRecord<>("hello"),
                pointlessGenericsSerde::write,
                pointlessGenericsSerde::read,
                new TypeReference<>() {});
    }
}
