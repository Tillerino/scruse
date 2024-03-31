package org.tillerino.scruse.tests.base;

import static org.tillerino.scruse.tests.OutputUtils.assertIsEqualToDatabind;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.model.NestingModel;

/**
 * We test ways of nesting JSON objects (Java records, plain Java objects, and Java Maps) and JSON arrays (Java arrays,
 * Java Lists, and Java Sets). These tests do not need to be tested with multiple backends, and we test everything with
 * Jackson streaming.
 *
 * <p>"Outer" objects are meant to contain all sorts of fields to test the nesting. "Inner" objects are contained by
 * outer objects and do not need to be complex. Both inner and outer objects are tested with records, field-based access
 * and accessor-based access.
 *
 * <p>Since all collections are treated identically internally, we can just test Java Lists. Since the JSON of Java
 * lists and Java arrays is identical, we can combine these tests.
 *
 * <p>Checks: (record, fields, accessors, array, list, map)
 *
 * <ul>
 *   <li>Outer (record, fields, accessors), inner (record, fields, accessors, array list, map): testOuterObject
 *   <li>Outer (array, list), inner (record, fields, accessors): testInnerObjectArray
 *   <li>Outer (array, list), inner (array, list): testDoubleArrayArray
 *   <li>Outer (array, list), inner map: testStringDoubleMapArray
 *   <li>Outer map, inner (record, fields, accessors): testStringInnerObjectMap
 *   <li>Outer map, inner (array, list): testStringDoubleArrayMap
 *   <li>Outer map, inner map: testStringStringDoubleMapMap
 * </ul>
 */
class NestingTest {
    @Test
    void testOuterObject() throws IOException {
        NestingSerde.OuterRecordSerde recordImpl = new NestingSerde$OuterRecordSerdeImpl();
        NestingSerde.OuterFieldsSerde fieldsImpl = new NestingSerde$OuterFieldsSerdeImpl();
        NestingSerde.OuterAccessorsSerde accessorsImpl = new NestingSerde$OuterAccessorsSerdeImpl();

        String[] jsons = {
            "null",
            "{}",
            "{\"doubleArray\":null, \"doubleList\":null, \"doubleMap\":null, \"innerRecord\":null, \"innerFields\":null, \"innerAccessors\":null}",
            "{\"doubleArray\":[], \"doubleList\":[], \"doubleMap\":{}, \"innerRecord\":{}, \"innerFields\":{}, \"innerAccessors\":{}}",
            "{\"doubleArray\":[1.0], \"doubleList\":[1.0], \"doubleMap\":{\"a\":1.0}, \"innerRecord\":{\"d\":1.0}, \"innerFields\":{\"d\":1.0}, \"innerAccessors\":{\"d\":1.0}}",
            """
				{
					"doubleArray":[1.0,null,"NaN","Infinity","-Infinity",2.0],
					"doubleList":[1.0,null,"NaN","Infinity","-Infinity",2.0],
					"doubleMap":{"a":1.0,"b":null,"c":"NaN","d":"Infinity","e":"-Infinity","f":2.0},
					"innerRecord":{"d":"NaN"}, "innerFields":{"d":"Infinity"}, "innerAccessors":{"d":"-Infinity"}
				}
			""",
        };

        for (String json : jsons) {
            NestingModel.OuterRecord outerRecord = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, recordImpl::read, new TypeReference<>() {});
            assertIsEqualToDatabind(outerRecord, recordImpl::write);

            NestingModel.OuterFields outerFields = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, fieldsImpl::read, new TypeReference<>() {});
            assertIsEqualToDatabind(outerFields, fieldsImpl::write);

            NestingModel.OuterAccessors outerAccessors = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, accessorsImpl::read, new TypeReference<>() {});
            assertIsEqualToDatabind(outerAccessors, accessorsImpl::write);
        }
    }

    @Test
    void testDoubleArrayArray() throws IOException {
        NestingSerde.ArraySerde arrayImpl = new NestingSerde$ArraySerdeImpl();
        NestingSerde.ListSerde listImpl = new NestingSerde$ListSerdeImpl();

        String[] jsons = {
            "null",
            "[]",
            "[null]",
            "[[]]",
            "[[1.0]]",
            """
				[
					null,
					[1.0,null,"NaN","Infinity","-Infinity",2.0]
				]
			""",
        };

        for (String json : jsons) {
            Double[][] doubleArrayArray = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, arrayImpl::readDoubleArrayArray, new TypeReference<>() {});
            assertIsEqualToDatabind(doubleArrayArray, arrayImpl::writeDoubleArrayArray);

            List<Double>[] doubleListArray = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, arrayImpl::readDoubleListArray, new TypeReference<>() {});
            assertIsEqualToDatabind(doubleListArray, arrayImpl::writeDoubleListArray);

            List<Double[]> doubleArrayList = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, listImpl::readDoubleArrayList, new TypeReference<>() {});
            assertIsEqualToDatabind(doubleArrayList, listImpl::writeDoubleArrayList);

            List<List<Double>> doubleListList = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, listImpl::readDoubleListList, new TypeReference<>() {});
            assertIsEqualToDatabind(doubleListList, listImpl::writeDoubleListList);
        }
    }

    @Test
    void testStringDoubleMapArray() throws IOException {
        NestingSerde.ArraySerde arrayImpl = new NestingSerde$ArraySerdeImpl();
        NestingSerde.ListSerde listImpl = new NestingSerde$ListSerdeImpl();

        String[] jsons = {
            "null",
            "[]",
            "[null]",
            "[{}]",
            "[{\"a\":1.0}]",
            """
				[
					null,
					{"a":1.0,"b":null,"c":"NaN","d":"Infinity","e":"-Infinity","f":2.0}
				]
			""",
        };

        for (String json : jsons) {
            Map<String, Double>[] stringDoubleMapArray = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, arrayImpl::readStringDoubleMapArray, new TypeReference<>() {});
            assertIsEqualToDatabind(stringDoubleMapArray, arrayImpl::writeStringDoubleMapArray);

            List<Map<String, Double>> stringDoubleMapList = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, listImpl::readStringDoubleMapList, new TypeReference<>() {});
            assertIsEqualToDatabind(stringDoubleMapList, listImpl::writeStringDoubleMapList);
        }
    }

    @Test
    void testStringInnerObjectMap() throws IOException {
        NestingSerde.MapSerde mapImpl = new NestingSerde$MapSerdeImpl();

        String[] jsons = {
            "null",
            "{}",
            "{\"a\":null}",
            "{\"a\":{}}",
            "{\"a\":{\"d\":1.0}}",
            """
				{
					"a":null,
					"b":{"d":"NaN"},
					"c":{"d":"Infinity"},
					"d":{"d":"-Infinity"}
				}
			""",
        };

        for (String json : jsons) {
            Map<String, NestingModel.InnerRecord> stringRecordMap =
                    InputUtils.assertIsEqualToDatabindComparingRecursively(
                            json, mapImpl::readStringRecordMap, new TypeReference<>() {});
            assertIsEqualToDatabind(stringRecordMap, mapImpl::writeStringRecordMap);

            Map<String, NestingModel.InnerAccessors> stringAccessorsMap =
                    InputUtils.assertIsEqualToDatabindComparingRecursively(
                            json, mapImpl::readStringAccessorsMap, new TypeReference<>() {});
            assertIsEqualToDatabind(stringAccessorsMap, mapImpl::writeStringAccessorsMap);

            Map<String, NestingModel.InnerFields> stringFieldsMap =
                    InputUtils.assertIsEqualToDatabindComparingRecursively(
                            json, mapImpl::readStringFieldsMap, new TypeReference<>() {});
            assertIsEqualToDatabind(stringFieldsMap, mapImpl::writeStringFieldsMap);
        }
    }

    @Test
    void testInnerObjectArray() throws IOException {
        NestingSerde.ArraySerde arrayImpl = new NestingSerde$ArraySerdeImpl();
        NestingSerde.ListSerde listImpl = new NestingSerde$ListSerdeImpl();

        String[] jsons = {
            "null",
            "[]",
            "[null]",
            "[{}]",
            "[{\"d\":1.0}]",
            """
				[
					null,
					{"d":"NaN"},
					{"d":"Infinity"},
					{"d":"-Infinity"}
				]
			""",
        };

        for (String json : jsons) {
            NestingModel.InnerRecord[] innerRecordArray = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, arrayImpl::readInnerRecordArray, new TypeReference<>() {});
            assertIsEqualToDatabind(innerRecordArray, arrayImpl::writeInnerRecordArray);

            NestingModel.InnerFields[] innerFieldsArray = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, arrayImpl::readInnerFieldsArray, new TypeReference<>() {});
            assertIsEqualToDatabind(innerFieldsArray, arrayImpl::writeInnerFieldsArray);

            NestingModel.InnerAccessors[] innerAccessorsArray = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, arrayImpl::readInnerAccessorsArray, new TypeReference<>() {});
            assertIsEqualToDatabind(innerAccessorsArray, arrayImpl::writeInnerAccessorsArray);

            List<NestingModel.InnerRecord> innerRecordList = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, listImpl::readInnerRecordList, new TypeReference<>() {});
            assertIsEqualToDatabind(innerRecordList, listImpl::writeInnerRecordList);

            List<NestingModel.InnerFields> innerFieldsList = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, listImpl::readInnerFieldsList, new TypeReference<>() {});
            assertIsEqualToDatabind(innerFieldsList, listImpl::writeInnerFieldsList);

            List<NestingModel.InnerAccessors> innerAccessorsList =
                    InputUtils.assertIsEqualToDatabindComparingRecursively(
                            json, listImpl::readInnerAccessorsList, new TypeReference<>() {});
            assertIsEqualToDatabind(innerAccessorsList, listImpl::writeInnerAccessorsList);
        }
    }

    @Test
    void testStringStringDoubleMapMap() throws IOException {
        NestingSerde.MapSerde mapImpl = new NestingSerde$MapSerdeImpl();

        String[] jsons = {
            "null",
            "{}",
            "{\"a\":null}",
            "{\"a\":{}}",
            "{\"a\":{\"b\":1.0}}",
            """
				{
					"a":null,
					"b":{"a": 1.0, "b":"NaN", "c":"Infinity", "d":"-Infinity", "e": null, "f": 2.0}
				}
			""",
        };

        for (String json : jsons) {
            Map<String, Map<String, Double>> stringStringDoubleMapMap =
                    InputUtils.assertIsEqualToDatabindComparingRecursively(
                            json, mapImpl::readStringStringDoubleMapMap, new TypeReference<>() {});
            assertIsEqualToDatabind(stringStringDoubleMapMap, mapImpl::writeStringStringDoubleMapMap);
        }
    }

    @Test
    void testStringDoubleArrayMap() throws IOException {
        NestingSerde.MapSerde mapImpl = new NestingSerde$MapSerdeImpl();

        String[] jsons = {
            "null",
            "{}",
            "{\"a\":null}",
            "{\"a\":[]}",
            "{\"a\":[1.0]}",
            """
				{
					"a":null,
					"b":[1.0,null,"NaN","Infinity","-Infinity",2.0]
				}
			""",
        };

        for (String json : jsons) {
            Map<String, Double[]> stringDoubleArrayMap = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, mapImpl::readStringDoubleArrayMap, new TypeReference<>() {});
            assertIsEqualToDatabind(stringDoubleArrayMap, mapImpl::writeStringDoubleArrayMap);

            Map<String, List<Double>> stringDoubleListMap = InputUtils.assertIsEqualToDatabindComparingRecursively(
                    json, mapImpl::readStringDoubleListMap, new TypeReference<>() {});
            assertIsEqualToDatabind(stringDoubleListMap, mapImpl::writeStringDoubleListMap);
        }
    }
}
