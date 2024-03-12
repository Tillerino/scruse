package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.OutputUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.tillerino.scruse.tests.OutputUtils.assertIsEqualToDatabind;

/**
 * We test ways of nesting JSON objects (Java records, plain Java objects, and Java Maps)
 * and JSON arrays (Java arrays, Java Lists, and Java Sets).
 * These tests do not need to be tested with multiple backends, and we test everything with Jackson streaming.
 *
 * <p>"Outer" objects are meant to contain all sorts of fields to test the nesting.
 * "Inner" objects are contained by outer objects and do not need to be complex.
 * Both inner and outer objects are tested with records, field-based access and accessor-based access.
 *
 * <p>Since all collections are treated identically internally, we can just test Java Lists.
 * Since the JSON of Java lists and Java arrays is identical, we can combine these tests.
 *
 * <p>Checks:
 * (record, fields, accessors, array, list, map)
 * <ul>
 *   <li>Outer (record, fields, accessors), inner (record, fields, accessors, array list, map): testOuterObject</li>
 *   <li>Outer (array, list), inner (record, fields, accessors): testInnerObjectArray</li>
 *   <li>Outer (array, list), inner (array, list): testDoubleArrayArray</li>
 *   <li>Outer (array, list), inner map: testStringDoubleMapArray</li>
 *   <li>Outer map, inner (record, fields, accessors): testStringInnerObjectMap</li>
 *   <li>Outer map, inner (array, list): testStringDoubleArrayMap</li>
 *   <li>Outer map, inner map: testStringStringDoubleMapMap</li>
 * </ul>
 */
class NestingTest {
	@Test
	void testOuterObject() throws IOException {
		OuterRecord.Serde recordImpl = new NestingTest$OuterRecord$SerdeImpl();
		OuterFields.Serde fieldsImpl = new NestingTest$OuterFields$SerdeImpl();
		OuterAccessors.Serde accessorsImpl = new NestingTest$OuterAccessors$SerdeImpl();

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
			OuterRecord outerRecord = InputUtils.assertIsEqualToDatabindComparingRecursively(json, recordImpl::read, new TypeReference<>() {
			});
			assertIsEqualToDatabind(outerRecord, recordImpl::write);

			OuterFields outerFields = InputUtils.assertIsEqualToDatabindComparingRecursively(json, fieldsImpl::read, new TypeReference<>() {
			});
			assertIsEqualToDatabind(outerFields, fieldsImpl::write);

			OuterAccessors outerAccessors = InputUtils.assertIsEqualToDatabindComparingRecursively(json, accessorsImpl::read, new TypeReference<>() {
			});
			assertIsEqualToDatabind(outerAccessors, accessorsImpl::write);
		}
	}

	@Test
	void testDoubleArrayArray() throws IOException {
		ArraySerde arrayImpl = new NestingTest$ArraySerdeImpl();
		ListSerde listImpl = new NestingTest$ListSerdeImpl();

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
			Double[][] doubleArrayArray = InputUtils.assertIsEqualToDatabindComparingRecursively(json, arrayImpl::readDoubleArrayArray, new TypeReference<>() {
			});
			assertIsEqualToDatabind(doubleArrayArray, arrayImpl::writeDoubleArrayArray);

			List<Double>[] doubleListArray = InputUtils.assertIsEqualToDatabindComparingRecursively(json, arrayImpl::readDoubleListArray, new TypeReference<>() {
			});
			assertIsEqualToDatabind(doubleListArray, arrayImpl::writeDoubleListArray);

			List<Double[]> doubleArrayList = InputUtils.assertIsEqualToDatabindComparingRecursively(json, listImpl::readDoubleArrayList, new TypeReference<>() {
			});
			assertIsEqualToDatabind(doubleArrayList, listImpl::writeDoubleArrayList);

			List<List<Double>> doubleListList = InputUtils.assertIsEqualToDatabindComparingRecursively(json, listImpl::readDoubleListList, new TypeReference<>() {
			});
			assertIsEqualToDatabind(doubleListList, listImpl::writeDoubleListList);
		}
	}

	@Test
	void testStringDoubleMapArray() throws IOException {
		ArraySerde arrayImpl = new NestingTest$ArraySerdeImpl();
		ListSerde listImpl = new NestingTest$ListSerdeImpl();

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
			Map<String, Double>[] stringDoubleMapArray = InputUtils.assertIsEqualToDatabindComparingRecursively(json, arrayImpl::readStringDoubleMapArray, new TypeReference<>() {
			});
			assertIsEqualToDatabind(stringDoubleMapArray, arrayImpl::writeStringDoubleMapArray);

			List<Map<String, Double>> stringDoubleMapList = InputUtils.assertIsEqualToDatabindComparingRecursively(json, listImpl::readStringDoubleMapList, new TypeReference<>() {
			});
			assertIsEqualToDatabind(stringDoubleMapList, listImpl::writeStringDoubleMapList);
		}
	}

	@Test
	void testStringInnerObjectMap() throws IOException {
		MapSerde mapImpl = new NestingTest$MapSerdeImpl();

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
			Map<String, InnerRecord> stringRecordMap = InputUtils.assertIsEqualToDatabindComparingRecursively(json, mapImpl::readStringRecordMap, new TypeReference<>() {
			});
			assertIsEqualToDatabind(stringRecordMap, mapImpl::writeStringRecordMap);

			Map<String, InnerAccessors> stringAccessorsMap = InputUtils.assertIsEqualToDatabindComparingRecursively(json, mapImpl::readStringAccessorsMap, new TypeReference<>() {
			});
			assertIsEqualToDatabind(stringAccessorsMap, mapImpl::writeStringAccessorsMap);

			Map<String, InnerFields> stringFieldsMap = InputUtils.assertIsEqualToDatabindComparingRecursively(json, mapImpl::readStringFieldsMap, new TypeReference<>() {
			});
			assertIsEqualToDatabind(stringFieldsMap, mapImpl::writeStringFieldsMap);
		}
	}

	@Test
	void testInnerObjectArray() throws IOException {
		ArraySerde arrayImpl = new NestingTest$ArraySerdeImpl();
		ListSerde listImpl = new NestingTest$ListSerdeImpl();

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
			InnerRecord[] innerRecordArray = InputUtils.assertIsEqualToDatabindComparingRecursively(json, arrayImpl::readInnerRecordArray, new TypeReference<>() {
			});
			assertIsEqualToDatabind(innerRecordArray, arrayImpl::writeInnerRecordArray);

			InnerFields[] innerFieldsArray = InputUtils.assertIsEqualToDatabindComparingRecursively(json, arrayImpl::readInnerFieldsArray, new TypeReference<>() {
			});
			assertIsEqualToDatabind(innerFieldsArray, arrayImpl::writeInnerFieldsArray);

			InnerAccessors[] innerAccessorsArray = InputUtils.assertIsEqualToDatabindComparingRecursively(json, arrayImpl::readInnerAccessorsArray, new TypeReference<>() {
			});
			assertIsEqualToDatabind(innerAccessorsArray, arrayImpl::writeInnerAccessorsArray);

			List<InnerRecord> innerRecordList = InputUtils.assertIsEqualToDatabindComparingRecursively(json, listImpl::readInnerRecordList, new TypeReference<>() {
			});
			assertIsEqualToDatabind(innerRecordList, listImpl::writeInnerRecordList);

			List<InnerFields> innerFieldsList = InputUtils.assertIsEqualToDatabindComparingRecursively(json, listImpl::readInnerFieldsList, new TypeReference<>() {
			});
			assertIsEqualToDatabind(innerFieldsList, listImpl::writeInnerFieldsList);

			List<InnerAccessors> innerAccessorsList = InputUtils.assertIsEqualToDatabindComparingRecursively(json, listImpl::readInnerAccessorsList, new TypeReference<>() {
			});
			assertIsEqualToDatabind(innerAccessorsList, listImpl::writeInnerAccessorsList);
		}
	}

	@Test
	void testStringStringDoubleMapMap() throws IOException {
		MapSerde mapImpl = new NestingTest$MapSerdeImpl();

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
			Map<String, Map<String, Double>> stringStringDoubleMapMap = InputUtils.assertIsEqualToDatabindComparingRecursively(json, mapImpl::readStringStringDoubleMapMap, new TypeReference<>() {
			});
			assertIsEqualToDatabind(stringStringDoubleMapMap, mapImpl::writeStringStringDoubleMapMap);
		}
	}

	@Test
	void testStringDoubleArrayMap() throws IOException {
		MapSerde mapImpl = new NestingTest$MapSerdeImpl();

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
			Map<String, Double[]> stringDoubleArrayMap = InputUtils.assertIsEqualToDatabindComparingRecursively(json, mapImpl::readStringDoubleArrayMap, new TypeReference<>() {
			});
			assertIsEqualToDatabind(stringDoubleArrayMap, mapImpl::writeStringDoubleArrayMap);

			Map<String, List<Double>> stringDoubleListMap = InputUtils.assertIsEqualToDatabindComparingRecursively(json, mapImpl::readStringDoubleListMap, new TypeReference<>() {
			});
			assertIsEqualToDatabind(stringDoubleListMap, mapImpl::writeStringDoubleListMap);
		}
	}

	record OuterRecord(Double[] doubleArray, List<Double> doubleList, Map<String, Double> doubleMap, InnerRecord innerRecord, InnerFields innerFields, InnerAccessors innerAccessors) {
		interface Serde {
			@JsonOutput
			void write(OuterRecord obj, JsonGenerator out) throws IOException;

			@JsonInput
			OuterRecord read(JsonParser parser) throws IOException;
		}
	}

	record InnerRecord(Double d) {
	}

	@EqualsAndHashCode
	static class OuterFields {
		public Double[] doubleArray;
		public List<Double> doubleList;
		public Map<String, Double> doubleMap;
		public InnerRecord innerRecord;
		public InnerFields innerFields;
		public InnerAccessors innerAccessors;

		interface Serde {
			@JsonOutput
			void write(OuterFields obj, JsonGenerator out) throws IOException;

			@JsonInput
			OuterFields read(JsonParser parser) throws IOException;
		}
	}

	@EqualsAndHashCode
	static class InnerFields {
		public Double d;
	}

	@EqualsAndHashCode
	static class OuterAccessors {
		private Double[] doubleArray;
		private List<Double> doubleList;
		private Map<String, Double> doubleMap;
		private InnerRecord innerRecord;
		private InnerFields innerFields;
		private InnerAccessors innerAccessors;

		public Double[] getDoubleArray() {
			return doubleArray;
		}

		public void setDoubleArray(Double[] doubleArray) {
			this.doubleArray = doubleArray;
		}

		public List<Double> getDoubleList() {
			return doubleList;
		}

		public void setDoubleList(List<Double> doubleList) {
			this.doubleList = doubleList;
		}

		public Map<String, Double> getDoubleMap() {
			return doubleMap;
		}

		public void setDoubleMap(Map<String, Double> doubleMap) {
			this.doubleMap = doubleMap;
		}

		public InnerRecord getInnerRecord() {
			return innerRecord;
		}

		public void setInnerRecord(InnerRecord innerRecord) {
			this.innerRecord = innerRecord;
		}

		public InnerFields getInnerFields() {
			return innerFields;
		}

		public void setInnerFields(InnerFields innerFields) {
			this.innerFields = innerFields;
		}

		public InnerAccessors getInnerAccessors() {
			return innerAccessors;
		}

		public void setInnerAccessors(InnerAccessors innerAccessors) {
			this.innerAccessors = innerAccessors;
		}

		interface Serde {
			@JsonOutput
			void write(OuterAccessors obj, JsonGenerator out) throws IOException;

			@JsonInput
			OuterAccessors read(JsonParser parser) throws IOException;
		}
	}

	@EqualsAndHashCode
	static class InnerAccessors {
		private Double d;

		public Double getD() {
			return d;
		}

		public void setD(Double d) {
			this.d = d;
		}
	}

	interface ArraySerde {
		@JsonOutput
		void writeDoubleArrayArray(Double[][] obj, JsonGenerator out) throws IOException;

		@JsonInput
		Double[][] readDoubleArrayArray(JsonParser parser) throws IOException;

		@JsonOutput
		void writeDoubleListArray(List<Double>[] obj, JsonGenerator out) throws IOException;

		@JsonInput
		List<Double>[] readDoubleListArray(JsonParser parser) throws IOException;

		@JsonOutput
		void writeStringDoubleMapArray(Map<String, Double>[] obj, JsonGenerator out) throws IOException;

		@JsonInput
		Map<String, Double>[] readStringDoubleMapArray(JsonParser parser) throws IOException;

		@JsonOutput
		void writeInnerRecordArray(InnerRecord[] obj, JsonGenerator out) throws IOException;

		@JsonInput
		InnerRecord[] readInnerRecordArray(JsonParser parser) throws IOException;

		@JsonOutput
		void writeInnerFieldsArray(InnerFields[] obj, JsonGenerator out) throws IOException;

		@JsonInput
		InnerFields[] readInnerFieldsArray(JsonParser parser) throws IOException;

		@JsonOutput
		void writeInnerAccessorsArray(InnerAccessors[] obj, JsonGenerator out) throws IOException;

		@JsonInput
		InnerAccessors[] readInnerAccessorsArray(JsonParser parser) throws IOException;
	}

	interface ListSerde {
		@JsonOutput
		void writeDoubleArrayList(List<Double[]> obj, JsonGenerator out) throws IOException;

		@JsonInput
		List<Double[]> readDoubleArrayList(JsonParser parser) throws IOException;

		@JsonOutput
		void writeDoubleListList(List<List<Double>> obj, JsonGenerator out) throws IOException;

		@JsonInput
		List<List<Double>> readDoubleListList(JsonParser parser) throws IOException;

		@JsonOutput
		void writeStringDoubleMapList(List<Map<String, Double>> obj, JsonGenerator out) throws IOException;

		@JsonInput
		List<Map<String, Double>> readStringDoubleMapList(JsonParser parser) throws IOException;

		@JsonOutput
		void writeInnerRecordList(List<InnerRecord> obj, JsonGenerator out) throws IOException;

		@JsonInput
		List<InnerRecord> readInnerRecordList(JsonParser parser) throws IOException;

		@JsonOutput
		void writeInnerFieldsList(List<InnerFields> obj, JsonGenerator out) throws IOException;

		@JsonInput
		List<InnerFields> readInnerFieldsList(JsonParser parser) throws IOException;

		@JsonOutput
		void writeInnerAccessorsList(List<InnerAccessors> obj, JsonGenerator out) throws IOException;

		@JsonInput
		List<InnerAccessors> readInnerAccessorsList(JsonParser parser) throws IOException;
	}

	interface MapSerde {

		@JsonInput
		Map<String, Map<String, Double>> readStringStringDoubleMapMap(JsonParser parser) throws IOException;

		@JsonOutput
		void writeStringStringDoubleMapMap(Map<String, Map<String, Double>> obj, JsonGenerator out) throws IOException;

		@JsonOutput
		void writeStringDoubleArrayMap(Map<String, Double[]> obj, JsonGenerator out) throws IOException;

		@JsonOutput
		void writeStringDoubleListMap(Map<String, List<Double>> obj, JsonGenerator out) throws IOException;

		@JsonInput
		Map<String, List<Double>> readStringDoubleListMap(JsonParser parser) throws IOException;

		@JsonInput
		Map<String, Double[]> readStringDoubleArrayMap(JsonParser parser) throws IOException;

		@JsonOutput
		void writeStringRecordMap(Map<String, InnerRecord> obj, JsonGenerator out) throws IOException;

		@JsonInput
		Map<String, InnerRecord> readStringRecordMap(JsonParser parser) throws IOException;

		@JsonOutput
		void writeStringAccessorsMap(Map<String, InnerAccessors> obj, JsonGenerator out) throws IOException;

		@JsonInput
		Map<String, InnerAccessors> readStringAccessorsMap(JsonParser parser) throws IOException;

		@JsonInput
		Map<String, InnerFields> readStringFieldsMap(JsonParser parser) throws IOException;

		@JsonOutput
		void writeStringFieldsMap(Map<String, InnerFields> obj, JsonGenerator out) throws IOException;
	}
}
