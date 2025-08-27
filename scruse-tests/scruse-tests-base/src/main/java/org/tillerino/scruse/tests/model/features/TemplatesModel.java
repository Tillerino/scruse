package org.tillerino.scruse.tests.model.features;

import org.tillerino.scruse.tests.model.AnEnum;

public interface TemplatesModel {
    record HasAnEnumArrayProperty(AnEnum[] a, double[][] d) {}
}
