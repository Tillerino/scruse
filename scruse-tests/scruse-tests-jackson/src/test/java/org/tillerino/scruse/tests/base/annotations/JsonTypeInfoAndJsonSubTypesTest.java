package org.tillerino.scruse.tests.base.annotations;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.model.annotations.JsonTypeInfoUseClass.RecordOne;
import org.tillerino.scruse.tests.model.annotations.JsonTypeInfoUseClass.RecordTwo;
import org.tillerino.scruse.tests.model.annotations.JsonTypeInfoUseName;
import org.tillerino.scruse.tests.model.annotations.JsonTypeInfoUseSimpleName;

class JsonTypeInfoAndJsonSubTypesTest {
    JsonTypeInfoAndJsonSubTypesSerde serde = new JsonTypeInfoAndJsonSubTypesSerdeImpl();

    // minimal class is used in our basic polymorphism tests

    @Test
    void useClass() throws Exception {
        for (var record : List.of(new RecordOne("x"), new RecordTwo(1))) {
            OutputUtils.roundTrip(record, serde::writeUseClass, serde::readUseClass, new TypeReference<>() {});
        }
    }

    @Test
    void useName() throws Exception {
        for (var record : List.of(new JsonTypeInfoUseName.RecordOne("x"), new JsonTypeInfoUseName.RecordTwo(1))) {
            OutputUtils.roundTrip(record, serde::writeUseName, serde::readUseName, new TypeReference<>() {});
        }
    }

    @Test
    void useSimpleName() throws Exception {
        for (var record :
                List.of(new JsonTypeInfoUseSimpleName.RecordOne("x"), new JsonTypeInfoUseSimpleName.RecordTwo(1))) {
            OutputUtils.roundTrip(
                    record, serde::writeUseSimpleName, serde::readUseSimpleName, new TypeReference<>() {});
        }
    }
}
