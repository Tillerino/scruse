package org.tillerino.scruse.tests.base.features;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.tests.model.features.UnknownPropertiesModel.JsonConfigIgnoreUnknown;
import org.tillerino.scruse.tests.model.features.UnknownPropertiesModel.JsonIgnorePropertiesIgnoreUnknown;

public interface UnknownPropertiesSerde {
    @JsonInput
    JsonIgnorePropertiesIgnoreUnknown readJsonIgnorePropertiesIgnoreUnknown(JsonParser in) throws Exception;

    @JsonInput
    JsonConfigIgnoreUnknown readJsonConfigIgnoreUnknown(JsonParser in) throws Exception;
}
