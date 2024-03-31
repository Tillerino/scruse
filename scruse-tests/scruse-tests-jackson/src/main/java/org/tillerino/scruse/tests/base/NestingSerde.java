package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.NestingModel;

public class NestingSerde {
    interface OuterRecordSerde {
        @JsonOutput
        void write(NestingModel.OuterRecord obj, JsonGenerator out) throws IOException;

        @JsonInput
        NestingModel.OuterRecord read(JsonParser parser) throws IOException;
    }

    interface OuterFieldsSerde {
        @JsonOutput
        void write(NestingModel.OuterFields obj, JsonGenerator out) throws IOException;

        @JsonInput
        NestingModel.OuterFields read(JsonParser parser) throws IOException;
    }

    interface OuterAccessorsSerde {
        @JsonOutput
        void write(NestingModel.OuterAccessors obj, JsonGenerator out) throws IOException;

        @JsonInput
        NestingModel.OuterAccessors read(JsonParser parser) throws IOException;
    }

    interface ArraySerde {
        @JsonOutput
        void writeDoubleArrayArray(Double[][] obj, JsonGenerator out) throws IOException;

        @JsonInput
        Double[][] readDoubleArrayArray(JsonParser parser) throws IOException;

        @JsonOutput
        void writeDoubleListArray(List<Double>[] obj, JsonGenerator out) throws IOException;

        @JsonInput
        List<Double>[] readDoubleListArray(JsonParser parser) throws IOException;

        @JsonOutput
        void writeStringDoubleMapArray(Map<String, Double>[] obj, JsonGenerator out) throws IOException;

        @JsonInput
        Map<String, Double>[] readStringDoubleMapArray(JsonParser parser) throws IOException;

        @JsonOutput
        void writeInnerRecordArray(NestingModel.InnerRecord[] obj, JsonGenerator out) throws IOException;

        @JsonInput
        NestingModel.InnerRecord[] readInnerRecordArray(JsonParser parser) throws IOException;

        @JsonOutput
        void writeInnerFieldsArray(NestingModel.InnerFields[] obj, JsonGenerator out) throws IOException;

        @JsonInput
        NestingModel.InnerFields[] readInnerFieldsArray(JsonParser parser) throws IOException;

        @JsonOutput
        void writeInnerAccessorsArray(NestingModel.InnerAccessors[] obj, JsonGenerator out) throws IOException;

        @JsonInput
        NestingModel.InnerAccessors[] readInnerAccessorsArray(JsonParser parser) throws IOException;
    }

    interface ListSerde {
        @JsonOutput
        void writeDoubleArrayList(List<Double[]> obj, JsonGenerator out) throws IOException;

        @JsonInput
        List<Double[]> readDoubleArrayList(JsonParser parser) throws IOException;

        @JsonOutput
        void writeDoubleListList(List<List<Double>> obj, JsonGenerator out) throws IOException;

        @JsonInput
        List<List<Double>> readDoubleListList(JsonParser parser) throws IOException;

        @JsonOutput
        void writeStringDoubleMapList(List<Map<String, Double>> obj, JsonGenerator out) throws IOException;

        @JsonInput
        List<Map<String, Double>> readStringDoubleMapList(JsonParser parser) throws IOException;

        @JsonOutput
        void writeInnerRecordList(List<NestingModel.InnerRecord> obj, JsonGenerator out) throws IOException;

        @JsonInput
        List<NestingModel.InnerRecord> readInnerRecordList(JsonParser parser) throws IOException;

        @JsonOutput
        void writeInnerFieldsList(List<NestingModel.InnerFields> obj, JsonGenerator out) throws IOException;

        @JsonInput
        List<NestingModel.InnerFields> readInnerFieldsList(JsonParser parser) throws IOException;

        @JsonOutput
        void writeInnerAccessorsList(List<NestingModel.InnerAccessors> obj, JsonGenerator out) throws IOException;

        @JsonInput
        List<NestingModel.InnerAccessors> readInnerAccessorsList(JsonParser parser) throws IOException;
    }

    interface MapSerde {

        @JsonInput
        Map<String, Map<String, Double>> readStringStringDoubleMapMap(JsonParser parser) throws IOException;

        @JsonOutput
        void writeStringStringDoubleMapMap(Map<String, Map<String, Double>> obj, JsonGenerator out) throws IOException;

        @JsonOutput
        void writeStringDoubleArrayMap(Map<String, Double[]> obj, JsonGenerator out) throws IOException;

        @JsonOutput
        void writeStringDoubleListMap(Map<String, List<Double>> obj, JsonGenerator out) throws IOException;

        @JsonInput
        Map<String, List<Double>> readStringDoubleListMap(JsonParser parser) throws IOException;

        @JsonInput
        Map<String, Double[]> readStringDoubleArrayMap(JsonParser parser) throws IOException;

        @JsonOutput
        void writeStringRecordMap(Map<String, NestingModel.InnerRecord> obj, JsonGenerator out) throws IOException;

        @JsonInput
        Map<String, NestingModel.InnerRecord> readStringRecordMap(JsonParser parser) throws IOException;

        @JsonOutput
        void writeStringAccessorsMap(Map<String, NestingModel.InnerAccessors> obj, JsonGenerator out)
                throws IOException;

        @JsonInput
        Map<String, NestingModel.InnerAccessors> readStringAccessorsMap(JsonParser parser) throws IOException;

        @JsonInput
        Map<String, NestingModel.InnerFields> readStringFieldsMap(JsonParser parser) throws IOException;

        @JsonOutput
        void writeStringFieldsMap(Map<String, NestingModel.InnerFields> obj, JsonGenerator out) throws IOException;
    }
}
