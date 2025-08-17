package org.tillerino.scruse.tests.base.features;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.base.PrimitiveScalarsSerde;
import org.tillerino.scruse.tests.model.PrimitiveArrayFieldsRecord;
import org.tillerino.scruse.tests.model.features.DelegationModel.SelfReferencingRecord;

public interface DelegationSerde {
    interface AdditionalArgumentsSerde {
        @JsonOutput
        void writeStringIntMap(Map<String, Integer> obj, JsonGenerator out, List<Integer> collector) throws Exception;

        @JsonOutput
        default void writeInt(Integer obj, JsonGenerator out, List<Integer> collector) throws Exception {
            collector.add(obj);
            // WriterMode.RETURN: return writeString("");
            writeString("", out);
        }

        @JsonOutput
        void writeString(String obj, JsonGenerator gen) throws Exception;

        @JsonInput
        Map<String, Integer> readStringIntMap(JsonParser in, Queue<Integer> collector) throws Exception;

        @JsonInput
        default Integer readInt(JsonParser in, Queue<Integer> collector) throws Exception {
            readString(in);
            return collector.remove();
        }

        @JsonInput
        String readString(JsonParser in) throws Exception;
    }

    /**
     * We make assertions about the generated code, so we mark methods with an X to make sure they don't collide with
     * other libraries' methods.
     */
    @JsonConfig(uses = PrimitiveScalarsSerde.class)
    interface BoxedScalarsWriter {
        @JsonOutput
        void writeBoxedBooleanX(Boolean b, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedByteX(Byte b, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedShortX(Short s, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedIntX(Integer i, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedLongX(Long l, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedCharX(Character c, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedFloatX(Float f, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedDoubleX(Double d, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeStringX(String s, JsonGenerator generator) throws Exception;

        @JsonInput
        Boolean readBoxedBooleanX(JsonParser parser) throws Exception;

        @JsonInput
        Byte readBoxedByteX(JsonParser parser) throws Exception;

        @JsonInput
        Short readBoxedShortX(JsonParser parser) throws Exception;

        @JsonInput
        Integer readBoxedIntX(JsonParser parser) throws Exception;

        @JsonInput
        Long readBoxedLongX(JsonParser parser) throws Exception;

        @JsonInput
        Character readBoxedCharX(JsonParser parser) throws Exception;

        @JsonInput
        Float readBoxedFloatX(JsonParser parser) throws Exception;

        @JsonInput
        Double readBoxedDoubleX(JsonParser parser) throws Exception;

        @JsonInput
        String readStringX(JsonParser parser) throws Exception;
    }

    @JsonConfig(uses = ScalarArraysWriter.class)
    interface PrimitiveArrayFieldsRecordSerde {
        @JsonOutput
        void writePrimitiveArrayFieldsRecord(PrimitiveArrayFieldsRecord input, JsonGenerator generator)
                throws Exception;
    }

    // methods are marked with X to make sure no methods of the backend are named identically
    @JsonConfig(uses = BoxedScalarsWriter.class)
    interface ScalarArraysWriter {
        @JsonOutput
        void writeBooleanArrayX(boolean[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeByteArrayX(byte[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeShortArrayX(short[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeIntArrayX(int[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeLongArrayX(long[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeCharArrayX(char[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeFloatArrayX(float[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeDoubleArrayX(double[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedBooleanArrayX(Boolean[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedByteArrayX(Byte[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedShortArrayX(Short[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedIntArrayX(Integer[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedLongArrayX(Long[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedCharArrayX(Character[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedFloatArrayX(Float[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeBoxedDoubleArrayX(Double[] input, JsonGenerator generator) throws Exception;

        @JsonOutput
        void writeStringArrayX(String[] input, JsonGenerator generator) throws Exception;

        @JsonInput
        boolean[] readBooleanArrayX(JsonParser parser) throws Exception;

        @JsonInput
        byte[] readByteArrayX(JsonParser parser) throws Exception;

        @JsonInput
        char[] readCharArrayX(JsonParser parser) throws Exception;

        @JsonInput
        short[] readShortArrayX(JsonParser parser) throws Exception;

        @JsonInput
        int[] readIntArrayX(JsonParser parser) throws Exception;

        @JsonInput
        long[] readLongArrayX(JsonParser parser) throws Exception;

        @JsonInput
        float[] readFloatArrayX(JsonParser parser) throws Exception;

        @JsonInput
        double[] readDoubleArrayX(JsonParser parser) throws Exception;

        @JsonInput
        Boolean[] readBoxedBooleanArrayX(JsonParser parser) throws Exception;

        @JsonInput
        Byte[] readBoxedByteArrayX(JsonParser parser) throws Exception;

        @JsonInput
        Short[] readBoxedShortArrayX(JsonParser parser) throws Exception;

        @JsonInput
        Integer[] readBoxedIntArrayX(JsonParser parser) throws Exception;

        @JsonInput
        Long[] readBoxedLongArrayX(JsonParser parser) throws Exception;

        @JsonInput
        Float[] readBoxedFloatArrayX(JsonParser parser) throws Exception;

        @JsonInput
        Double[] readBoxedDoubleArrayX(JsonParser parser) throws Exception;

        @JsonInput
        String[] readStringArrayX(JsonParser parser) throws Exception;
    }

    interface SelfReferencingSerde {
        @JsonOutput
        void serialize(SelfReferencingRecord record, JsonGenerator output) throws Exception;

        @JsonInput
        SelfReferencingRecord deserialize(JsonParser input) throws Exception;
    }
}
