package org.tillerino.scruse.tests.model.features;

import java.util.List;
import org.tillerino.scruse.tests.model.AnEnum;

public interface DefaultValuesModel {
    record Mixed(String s, int i, double[] ds, AnEnum e, List<String> l, Mixed m) {}
}
