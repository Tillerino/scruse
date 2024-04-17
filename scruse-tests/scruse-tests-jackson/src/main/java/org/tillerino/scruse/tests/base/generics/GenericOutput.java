package org.tillerino.scruse.tests.base.generics;

import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonOutput;

// use 'U' instead of 'T' to make sure that we actually instantiate the type
@JsonConfig(implement = JsonConfig.ImplementationMode.DO_NOT_IMPLEMENT)
public interface GenericOutput<U> {
    @JsonOutput
    void writeOnGenericInterface(U obj, JsonGenerator gen) throws Exception;
}
