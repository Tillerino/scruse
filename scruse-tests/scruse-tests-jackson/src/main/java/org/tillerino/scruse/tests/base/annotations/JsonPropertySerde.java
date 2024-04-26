package org.tillerino.scruse.tests.base.annotations;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.annotations.JsonPropertyCustomName;
import org.tillerino.scruse.tests.model.annotations.JsonPropertyCustomNameWithSetter;

public interface JsonPropertySerde {
    @JsonOutput
    void writeCustomName(JsonPropertyCustomName obj, JsonGenerator out) throws Exception;

    @JsonInput
    JsonPropertyCustomName readCustomName(JsonParser in) throws Exception;

    @JsonOutput
    void writeCustomNameWithSetter(JsonPropertyCustomNameWithSetter obj, JsonGenerator out) throws Exception;

    @JsonInput
    JsonPropertyCustomNameWithSetter readCustomNameWithSetter(JsonParser in) throws Exception;
}
