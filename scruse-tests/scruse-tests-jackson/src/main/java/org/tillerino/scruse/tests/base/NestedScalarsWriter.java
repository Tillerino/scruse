package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.tillerino.scruse.annotations.JsonOutput;

interface NestedScalarsWriter {
    @JsonOutput
    void writeDoubleArrayArray(Double[][] array, JsonGenerator output) throws IOException;

    @JsonOutput
    void writeStringDoubleArrayMap(Map<String, Double[]> map, JsonGenerator output) throws IOException;

    @JsonOutput
    void writeStringDoubleMapList(List<Map<String, Double>> list, JsonGenerator output) throws IOException;

    @JsonOutput
    void writeStringDoubleMapMap(Map<String, Map<String, Double>> map, JsonGenerator output) throws IOException;
}
