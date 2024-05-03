package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.converters.OptionalInputConverters;
import org.tillerino.scruse.converters.OptionalOutputConverters;
import org.tillerino.scruse.tests.model.OptionalComponentsRecord;

@JsonConfig(uses = {OptionalInputConverters.class, OptionalOutputConverters.class})
interface ConvertersSerde {
    @JsonOutput
    void writeOptionalComponentsRecord(OptionalComponentsRecord record, JsonGenerator generator) throws Exception;

    @JsonInput
    OptionalComponentsRecord readOptionalComponentsRecord(JsonParser parser) throws Exception;
}
