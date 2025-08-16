package org.tillerino.scruse.tests.base.annotations;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.tests.model.annotations.JsonIgnorePropertiesIgnoreUnknown;

public interface JsonIgnorePropertiesSerde {
    @JsonInput
    JsonIgnorePropertiesIgnoreUnknown readJsonIgnorePropertiesIgnoreUnknown(JsonParser in) throws Exception;
}
