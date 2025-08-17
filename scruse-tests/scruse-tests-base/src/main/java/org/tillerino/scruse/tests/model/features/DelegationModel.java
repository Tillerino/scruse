package org.tillerino.scruse.tests.model.features;

import java.util.Arrays;
import java.util.List;

public interface DelegationModel {
    record SelfReferencingRecord(String prop, SelfReferencingRecord self) {
        public static List<SelfReferencingRecord> INSTANCES = Arrays.asList(
                null,
                new SelfReferencingRecord("a", null),
                new SelfReferencingRecord("c", new SelfReferencingRecord("d", null)));

        public static List<String> JSON = Arrays.asList(
                "null", "{\"prop\":\"a\",\"self\":null}", "{\"prop\":\"c\",\"self\":{\"prop\":\"d\",\"self\":null}}");
    }
}
