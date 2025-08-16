package org.tillerino.scruse.tests.base.annotations;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.tests.model.annotations.JsonConfigIgnoreUnknown;

public interface JsonConfigIgnoreUnknownSerde {
    @JsonInput
    JsonConfigIgnoreUnknown readJsonConfigIgnoreUnknown(JsonParser in) throws Exception;
}
