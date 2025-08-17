package org.tillerino.scruse.tests.base.features;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.features.IgnorePropertiesModel.JsonIgnorePropertiesValue;

public interface IgnorePropertiesSerde {
    @JsonInput
    JsonIgnorePropertiesValue readJsonIgnorePropertiesValue(JsonParser in) throws Exception;

    @JsonOutput
    void writeJsonIgnorePropertiesValue(JsonIgnorePropertiesValue value, JsonGenerator out) throws Exception;
}
