package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;

record NoFieldsRecord() {
    interface Input {
        @JsonInput
        NoFieldsRecord read(JsonParser parser) throws Exception;
    }
}
