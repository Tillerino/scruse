package org.tillerino.scruse.tests.base.features;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.util.List;
import java.util.Map;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.base.features.DelegationSerde.BoxedScalarsSerde;
import org.tillerino.scruse.tests.model.features.GenericsModel.GenericRecord;

public interface GenericsSerde {
    // use 'V' instead of 'T' to make sure that we actually instantiate the type
    @JsonConfig(implement = JsonConfig.ImplementationMode.DO_NOT_IMPLEMENT)
    interface GenericInputSerde<V> {
        @JsonInput
        V readOnGenericInterface(JsonParser parser) throws Exception;
    }

    // use 'U' instead of 'T' to make sure that we actually instantiate the type
    @JsonConfig(implement = JsonConfig.ImplementationMode.DO_NOT_IMPLEMENT)
    interface GenericOutputSerde<U> {
        @JsonOutput
        void writeOnGenericInterface(U obj, JsonGenerator gen) throws Exception;
    }

    interface GenericRecordSerde {
        @JsonInput
        <T> GenericRecord<T> readGenericRecord(JsonParser parser, GenericInputSerde<T> fieldSerde) throws Exception;

        @JsonOutput
        <T> void writeGenericRecord(GenericRecord<T> obj, JsonGenerator gen, GenericOutputSerde<T> fieldSerde)
                throws Exception;
    }

    @JsonConfig(
            uses = {
                BoxedScalarsSerde.class,
                GenericRecordSerde.class,
            })
    interface IntegerRecordSerde {
        @JsonInput
        GenericRecord<Integer> readIntegerRecord(JsonParser parser) throws Exception;

        @JsonOutput
        void writeIntegerRecord(GenericRecord<Integer> obj, JsonGenerator gen) throws Exception;
    }

    @JsonConfig(
            uses = {
                StringSerde.class,
                GenericRecordSerde.class,
            })
    interface StringRecordSerde {
        @JsonInput
        GenericRecord<String> readStringRecord(JsonParser parser) throws Exception;

        @JsonOutput
        void writeStringRecord(GenericRecord<String> obj, JsonGenerator gen) throws Exception;
    }

    @JsonConfig(implement = JsonConfig.ImplementationMode.DO_IMPLEMENT)
    interface StringSerde extends GenericOutputSerde<String>, GenericInputSerde<String> {}

    /**
     * Primitive types cannot be used to instantiate generics (not yet anyway #JEP218believe). If we are not careful,
     * the implementation of this interface will try to pass writeDouble to writeGenericArray, which will not compile.
     * We do not need to use the implementation in tests. Compiling this is sufficient.
     */
    interface PrimitivesVsGenerics {
        @JsonOutput
        void writeDouble(double d, JsonGenerator gen) throws Exception;

        @JsonOutput
        <T> void writeGenericArray(T[] array, JsonGenerator gen, GenericOutputSerde<T> componentDelegator)
                throws Exception;

        @JsonOutput
        void writeDoubleArray(double[] array, JsonGenerator gen) throws Exception;
    }

    interface GenericContainersSerde {
        @JsonOutput
        <T> void writeGenericList(List<T> list, JsonGenerator gen, GenericOutputSerde<T> componentWriter)
                throws Exception;

        @JsonInput
        <T> List<T> readGenericList(JsonParser parser, GenericInputSerde<T> componentReader) throws Exception;

        @JsonOutput
        <V> void writeGenericMap(Map<String, V> map, JsonGenerator gen, GenericOutputSerde<V> valueWriter)
                throws Exception;

        @JsonInput
        <V> Map<String, V> readGenericMap(JsonParser parser, GenericInputSerde<V> valueReader) throws Exception;
    }

    @JsonConfig(uses = {GenericContainersSerde.class, BoxedScalarsSerde.class})
    interface GenericListSerde {
        @JsonOutput
        void writeDoubleList(List<Double> l, JsonGenerator gen) throws Exception;

        @JsonInput
        List<Double> readDoubleList(JsonParser parser) throws Exception;
    }

    @JsonConfig(uses = {GenericContainersSerde.class, BoxedScalarsSerde.class})
    interface GenericMapSerde {
        @JsonOutput
        void writeStringDoubleMap(Map<String, Double> map, JsonGenerator gen) throws Exception;

        @JsonInput
        Map<String, Double> readStringDoubleMap(JsonParser parser) throws Exception;
    }
}
