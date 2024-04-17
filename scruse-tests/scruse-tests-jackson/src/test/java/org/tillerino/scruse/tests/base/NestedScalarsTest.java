package org.tillerino.scruse.tests.base;

import static org.tillerino.scruse.tests.OutputUtils.assertIsEqualToDatabind;
import static org.tillerino.scruse.tests.TestSettingsBase.JavaData.map;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class NestedScalarsTest {
    NestedScalarsWriter impl = new NestedScalarsWriterImpl();

    @Test
    void testDoubleArrayArray() throws Exception {
        Double[][][] values = {null, {null}, {{null}}, {null, {null}}, {null, {1D, null, 3D}}};
        for (Double[][] value : values) {
            assertIsEqualToDatabind(value, impl::writeDoubleArrayArray);
        }
    }

    @Test
    void testStringDoubleArrayMap() throws Exception {
        List<Map<String, Double[]>> values = Arrays.asList(
                null, map(), map("a", null), map("a", new Double[] {1D, null, 3D}, "b", null, "c", new Double[] {
                    4D, 5D, null
                }));
        for (Map<String, Double[]> value : values) {
            assertIsEqualToDatabind(value, impl::writeStringDoubleArrayMap);
        }
    }

    @Test
    void testStringDoubleMapList() throws Exception {
        List<List<Map<String, Double>>> values = Arrays.<List<Map<String, Double>>>asList(
                null,
                List.of(),
                Arrays.<Map<String, Double>>asList(new Map[] {null}),
                List.of(map()),
                List.of(map("a", 1D, "b", null, "c", 3D), map("d", 4D, "e", 5D, "f", null)));
        for (List<Map<String, Double>> value : values) {
            assertIsEqualToDatabind(value, impl::writeStringDoubleMapList);
        }
    }

    @Test
    void testStringDoubleMapMap() throws Exception {
        List<Map<String, Map<String, Double>>> values = Arrays.asList(
                null,
                map(),
                map("a", null),
                map("a", map("a", 1D, "b", null, "c", 3D), "b", null, "c", map("d", 4D, "e", 5D, "f", null)));
        for (Map<String, Map<String, Double>> value : values) {
            assertIsEqualToDatabind(value, impl::writeStringDoubleMapMap);
        }
    }
}
