package org.tillerino.scruse.tests.base.features;

import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.features.IgnorePropertyModel.*;

public interface IgnorePropertySerde {
    @JsonOutput
    void writeFieldWithGetter(JsonIgnoreOnFieldWithGetter obj, JsonGenerator out) throws Exception;

    @JsonOutput
    void writeFieldAndGetter(JsonIgnoreOnFieldAndGetter obj, JsonGenerator out) throws Exception;

    @JsonOutput
    void writeFieldWithoutGetter(JsonIgnoreOnFieldWithoutGetter obj, JsonGenerator out) throws Exception;

    @JsonOutput
    void writeGetter(JsonIgnoreOnGetter obj, JsonGenerator out) throws Exception;

    @JsonOutput
    void writeRecordComponent(JsonIgnoreOnRecordComponent obj, JsonGenerator out) throws Exception;

    @JsonOutput
    void writeChildInheritsParentIgnore(ChildInheritsParentIgnore obj, JsonGenerator out) throws Exception;

    @JsonOutput
    void writeChildImplementsParentInterface(ChildImplementsParentInterface obj, JsonGenerator out) throws Exception;
}
