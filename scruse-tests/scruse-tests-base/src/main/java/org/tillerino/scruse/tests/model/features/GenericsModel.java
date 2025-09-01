package org.tillerino.scruse.tests.model.features;

public interface GenericsModel {
    record GenericRecord<F>(F f) {}

    record PointlessGenericsRecord<F>(String prop) {}

    record UsesGenericRecord(GenericRecord<Integer> gi) {}
}
