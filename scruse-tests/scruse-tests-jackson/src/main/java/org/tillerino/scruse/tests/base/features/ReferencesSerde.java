package org.tillerino.scruse.tests.base.features;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.util.List;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.api.DeserializationContext;
import org.tillerino.scruse.api.SerializationContext;
import org.tillerino.scruse.converters.UuidInputConverters;
import org.tillerino.scruse.converters.UuidOutputConverters;
import org.tillerino.scruse.tests.model.features.ReferencesModel.*;

@JsonConfig(uses = {UuidInputConverters.class, UuidOutputConverters.class})
public interface ReferencesSerde {
    @JsonOutput
    void writeIntSequenceIdRecord(IntSequenceIdRecord hasIntId, JsonGenerator gen, SerializationContext ctx)
            throws Exception;

    @JsonInput
    IntSequenceIdRecord readIntSequenceIdRecord(JsonParser parser, DeserializationContext ctx) throws Exception;

    @JsonOutput
    void writeListOfIntSequenceIdRecord(
            List<IntSequenceIdRecord> hasIntIds, JsonGenerator gen, SerializationContext ctx) throws Exception;

    @JsonInput
    List<IntSequenceIdRecord> readListOfIntSequenceIdRecord(JsonParser parser, DeserializationContext ctx)
            throws Exception;

    @JsonOutput
    void writeIntSequenceIdPojo(IntSequenceIdPojo hasIntId, JsonGenerator gen, SerializationContext ctx)
            throws Exception;

    @JsonInput
    IntSequenceIdPojo readIntSequenceIdPojo(JsonParser parser, DeserializationContext ctx) throws Exception;

    @JsonOutput
    void writeListOfIntSequenceIdPojo(List<IntSequenceIdPojo> hasIntIds, JsonGenerator gen, SerializationContext ctx)
            throws Exception;

    @JsonInput
    List<IntSequenceIdPojo> readListOfIntSequenceIdPojo(JsonParser parser, DeserializationContext ctx) throws Exception;

    @JsonOutput
    void writeUuidIdRecord(UuidIdRecord hasIntId, JsonGenerator gen, SerializationContext ctx) throws Exception;

    @JsonInput
    UuidIdRecord readUuidIdRecord(JsonParser parser, DeserializationContext ctx) throws Exception;

    @JsonOutput
    void writeListOfUuidIdRecord(List<UuidIdRecord> hasIntIds, JsonGenerator gen, SerializationContext ctx)
            throws Exception;

    @JsonInput
    List<UuidIdRecord> readListOfUuidIdRecord(JsonParser parser, DeserializationContext ctx) throws Exception;

    @JsonOutput
    void writePropertyIdRecord(PropertyIdRecord hasIntId, JsonGenerator gen, SerializationContext ctx) throws Exception;

    @JsonInput
    PropertyIdRecord readPropertyIdRecord(JsonParser parser, DeserializationContext ctx) throws Exception;

    @JsonOutput
    void writeListOfPropertyIdRecord(List<PropertyIdRecord> hasIntIds, JsonGenerator gen, SerializationContext ctx)
            throws Exception;

    @JsonInput
    List<PropertyIdRecord> readListOfPropertyIdRecord(JsonParser parser, DeserializationContext ctx) throws Exception;

    @JsonOutput
    void writePropertyIdPojo(PropertyIdPojo hasIntId, JsonGenerator gen, SerializationContext ctx) throws Exception;

    @JsonInput
    PropertyIdPojo readPropertyIdPojo(JsonParser parser, DeserializationContext ctx) throws Exception;

    @JsonOutput
    void writeListOfPropertyIdPojo(List<PropertyIdPojo> hasIntIds, JsonGenerator gen, SerializationContext ctx)
            throws Exception;

    @JsonInput
    List<PropertyIdPojo> readListOfPropertyIdPojo(JsonParser parser, DeserializationContext ctx) throws Exception;
}
