package org.tillerino.scruse.tests.model.features;

import org.tillerino.scruse.tests.model.AnEnum;

import java.util.List;

public interface DefaultValuesModel {
    record Mixed(String s, int i, double[] ds, AnEnum e, List<String> l, Mixed m) {}
}
