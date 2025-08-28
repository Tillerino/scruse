package org.tillerino.scruse.tests.model.features;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.tillerino.scruse.tests.model.ScalarFieldsRecord;

public interface VerificationModel {
    @Getter
    class DuplicatedProperty {
        @JsonProperty("foo")
        String s;

        @JsonProperty("foo")
        String t;
    }

    class MoreSettersThanGetters {
        @Getter
        @Setter
        String s;

        @Setter
        String t;
    }

    class MoreGettersThanSetters {
        @Getter
        String s;

        @Getter
        @Setter
        String t;
    }

    class TwoPropertiesOfTheSameType {
        public ScalarFieldsRecord first;
        public ScalarFieldsRecord second;
    }
}
