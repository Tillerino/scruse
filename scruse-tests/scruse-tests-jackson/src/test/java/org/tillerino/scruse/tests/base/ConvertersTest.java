package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.TestSettings;
import org.tillerino.scruse.tests.TestSettingsBase;
import org.tillerino.scruse.tests.function.Zip;
import org.tillerino.scruse.tests.model.OptionalComponentsRecord;

class ConvertersTest {
    ConvertersSerde serde = new ConvertersSerdeImpl();

    @Test
    void optionalComponentsRecordRountrips() throws Exception {
        TestSettingsBase.JavaData javaData = TestSettings.SETTINGS.javaData();
        List<OptionalComponentsRecord> instances = Zip.instantiate5(
                javaData.OPTIONAL_STRINGS,
                javaData.OPTIONAL_INTS,
                javaData.OPTIONAL_LONGS,
                javaData.OPTIONAL_DOUBLES,
                List.of(Optional.empty(), Optional.of(Optional.empty()), Optional.of(Optional.of("nested"))),
                OptionalComponentsRecord::new);
        for (OptionalComponentsRecord instance : instances) {
            OutputUtils.roundTrip(
                    instance,
                    serde::writeOptionalComponentsRecord,
                    serde::readOptionalComponentsRecord,
                    new TypeReference<OptionalComponentsRecord>() {});
        }
    }

    /** FEATURE-JSON */
    @Test
    void testAllMissing() throws Exception {
        OptionalComponentsRecord deserialized = InputUtils.deserialize("{}", serde::readOptionalComponentsRecord);
        // We differ from Jackson here, which returns Optional.of(Optional.empty()) for the nested optional.
        // Since this is obscure enough, we don't mind.
        Assertions.assertThat(deserialized)
                .isEqualTo(new OptionalComponentsRecord(
                        Optional.empty(),
                        OptionalInt.empty(),
                        OptionalLong.empty(),
                        OptionalDouble.empty(),
                        Optional.empty()));
    }
}
