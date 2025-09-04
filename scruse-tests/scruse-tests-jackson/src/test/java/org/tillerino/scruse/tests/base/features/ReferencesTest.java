package org.tillerino.scruse.tests.base.features;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.api.DeserializationContext;
import org.tillerino.scruse.api.SerializationContext;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.SerdeUtil;
import org.tillerino.scruse.tests.model.features.ReferencesModel.*;

class ReferencesTest extends ReferenceTest {
    ReferencesSerde serde = SerdeUtil.impl(ReferencesSerde.class);

    @Test
    void writeIntSequenceIdRecord() throws Exception {
        IntSequenceIdRecord bla = new IntSequenceIdRecord("bla");
        outputUtils.assertIsEqualToDatabind(List.of(bla, bla), serde::writeListOfIntSequenceIdRecord);
    }

    @Test
    void readIntSequenceIdRecord() throws Exception {
        List<IntSequenceIdRecord> list = inputUtils.assertIsEqualToDatabind(
                "[{\"@id\":1,\"prop\":\"bla\"},1]", serde::readListOfIntSequenceIdRecord, new TypeReference<>() {});
        assertThat(list.get(0)).isSameAs(list.get(1));
    }

    @Test
    void writeIntSequenceIdPojo() throws Exception {
        IntSequenceIdPojo bla = new IntSequenceIdPojo();
        bla.setProp("bla");
        outputUtils.assertIsEqualToDatabind(List.of(bla, bla), serde::writeListOfIntSequenceIdPojo);
    }

    @Test
    void readIntSequenceIdPojo() throws Exception {
        List<IntSequenceIdPojo> list = inputUtils.assertIsEqualToDatabind(
                "[{\"@id\":1,\"prop\":\"bla\"},1]", serde::readListOfIntSequenceIdPojo, new TypeReference<>() {});
        assertThat(list.get(0)).isSameAs(list.get(1));
    }

    @Test
    void writeUuidIdRecord() throws Exception {
        UuidIdRecord bla = new UuidIdRecord("bla");
        String json =
                outputUtils.serialize2(List.of(bla, bla), new SerializationContext(), serde::writeListOfUuidIdRecord);
        JsonNode jsonNode = new ObjectMapper().readTree(json);
        assertThat(jsonNode).isInstanceOfSatisfying(ArrayNode.class, array -> {
            assertThat(array.size()).isEqualTo(2);
            assertThat(array.get(0).get("@id")).isEqualTo(array.get(1));
        });
    }

    @Test
    void readUuidIdRecord() throws Exception {
        List<UuidIdRecord> list = inputUtils.assertIsEqualToDatabind(
                "[{\"@id\":\"1d2af8a2-198f-4a00-b799-7959f0b971bf\",\"prop\":\"bla\"},\"1d2af8a2-198f-4a00-b799-7959f0b971bf\"]",
                serde::readListOfUuidIdRecord,
                new TypeReference<>() {});
        assertThat(list.get(0)).isSameAs(list.get(1));
    }

    @Test
    void writePropertyIdRecord() throws Exception {
        PropertyIdRecord bla = new PropertyIdRecord("bla");
        outputUtils.assertIsEqualToDatabind(List.of(bla, bla), serde::writeListOfPropertyIdRecord);
    }

    @Test
    void readPropertyIdRecord() throws Exception {
        // Jackson throws up here. Something about a fallback setter/field. Probably because it's a record.
        // Jackson works just fine with a POJO, see below.
        List<PropertyIdRecord> list = inputUtils.deserialize2(
                "[{\"prop\":\"bla\"},\"bla\"]", new DeserializationContext(), serde::readListOfPropertyIdRecord);
        assertThat(list.get(0)).isEqualTo(new PropertyIdRecord("bla"));
        assertThat(list.get(0)).isSameAs(list.get(1));
    }

    @Test
    void writePropertyIdPojo() throws Exception {
        PropertyIdPojo bla = new PropertyIdPojo();
        bla.setProp("bla");
        outputUtils.assertIsEqualToDatabind(List.of(bla, bla), serde::writeListOfPropertyIdPojo);
    }

    @Test
    void readPropertyIdPojo() throws Exception {
        List<PropertyIdPojo> list = inputUtils.assertIsEqualToDatabind(
                "[{\"prop\":\"bla\"},\"bla\"]", serde::readListOfPropertyIdPojo, new TypeReference<>() {});
        assertThat(list.get(0)).isSameAs(list.get(1));
    }
}
