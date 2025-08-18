package org.tillerino.scruse.tests.base.features;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
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
}
