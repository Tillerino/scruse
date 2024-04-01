package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.model.AnEnum;

class ScalarFieldsSerdeTest {

    @Test
    void scalarFieldsRecordOutput() throws IOException {
        List<ScalarFieldsRecord> values = List.of(
                new ScalarFieldsRecord(
                        false,
                        (byte) 1,
                        (short) 2,
                        3,
                        4L,
                        'c',
                        4f,
                        5.0d,
                        false,
                        (byte) 1,
                        (short) 2,
                        3,
                        4L,
                        'c',
                        4f,
                        5.0d,
                        "six",
                        AnEnum.SOME_VALUE),
                new ScalarFieldsRecord(
                        false, (byte) 1, (short) 2, 3, 4L, 'c', Float.NaN, Float.NaN, null, null, null, null, null,
                        null, null, null, null, null));

        ScalarFieldsRecord.Serde impl = new ScalarFieldsRecord$SerdeImpl();

        for (ScalarFieldsRecord object : values) {
            OutputUtils.roundTrip(object, impl::write, impl::read, new TypeReference<>() {});
        }
    }

    @Test
    void scalarFieldsClassOutput() throws IOException {
        List<ScalarFieldsClass> values = List.of(
                new ScalarFieldsClass(
                        false,
                        (byte) 1,
                        (short) 2,
                        3,
                        4L,
                        'c',
                        4f,
                        5.0d,
                        false,
                        (byte) 1,
                        (short) 2,
                        3,
                        4L,
                        'c',
                        4f,
                        5.0d,
                        "six",
                        AnEnum.SOME_VALUE),
                new ScalarFieldsClass(
                        false, (byte) 1, (short) 2, 3, 4L, 'c', Float.NaN, Float.NaN, null, null, null, null, null,
                        null, null, null, null, null));

        ScalarFieldsClass.Serde impl = new ScalarFieldsClass$SerdeImpl();

        for (ScalarFieldsClass object : values) {
            OutputUtils.roundTrip(object, impl::write, impl::read, new TypeReference<>() {});
        }
    }

    @Test
    void scalarAccessorsClassOutput() throws IOException {
        List<ScalarAccessorsClass> values = List.of(
                new ScalarAccessorsClass(
                        false,
                        (byte) 1,
                        (short) 2,
                        3,
                        4L,
                        'c',
                        4f,
                        5.0d,
                        false,
                        (byte) 1,
                        (short) 2,
                        3,
                        4L,
                        'c',
                        4f,
                        5.0d,
                        "six",
                        AnEnum.SOME_VALUE),
                new ScalarAccessorsClass(
                        false, (byte) 1, (short) 2, 3, 4L, 'c', Float.NaN, Float.NaN, null, null, null, null, null,
                        null, null, null, null, null));

        ScalarAccessorsClass.Serde impl = new ScalarAccessorsClass$SerdeImpl();

        for (ScalarAccessorsClass object : values) {
            OutputUtils.roundTrip(object, impl::write, impl::read, new TypeReference<>() {});
        }
    }
}
