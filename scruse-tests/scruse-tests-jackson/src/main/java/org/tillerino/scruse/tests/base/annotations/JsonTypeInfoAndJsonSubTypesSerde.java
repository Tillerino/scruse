package org.tillerino.scruse.tests.base.annotations;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.annotations.JsonTypeInfoUseClass;
import org.tillerino.scruse.tests.model.annotations.JsonTypeInfoUseName;
import org.tillerino.scruse.tests.model.annotations.JsonTypeInfoUseSimpleName;

public interface JsonTypeInfoAndJsonSubTypesSerde {
    @JsonOutput
    void writeUseClass(JsonTypeInfoUseClass obj, JsonGenerator generator) throws Exception;

    @JsonInput
    JsonTypeInfoUseClass readUseClass(JsonParser parser) throws Exception;

    @JsonOutput
    void writeUseName(JsonTypeInfoUseName obj, JsonGenerator generator) throws Exception;

    @JsonInput
    JsonTypeInfoUseName readUseName(JsonParser parser) throws Exception;

    @JsonOutput
    void writeUseSimpleName(JsonTypeInfoUseSimpleName obj, JsonGenerator generator) throws Exception;

    @JsonInput
    JsonTypeInfoUseSimpleName readUseSimpleName(JsonParser parser) throws Exception;
}
