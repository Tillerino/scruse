package org.tillerino.scruse.tests.base.annotations;

import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.annotations.*;

public interface JsonValueAndJsonCreatorSerde {
    @JsonOutput
    void write(JsonValueRecord<Integer> obj, JsonGenerator generator) throws Exception;
}
