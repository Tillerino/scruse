package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ScalarMapsReaderTest {
	ScalarMapsReader impl = new ScalarMapsReaderImpl();

	@Test
	void readStringBooleanMap() throws IOException {
		TypeReference<Map<String, Boolean>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"{ }",
			"{\"a\": null}",
			"{\"a\": true}",
			"{\"a\": false}",
			"{\"a\": true, \"b\": null, \"c\": false}",
		};
		for (String json : jsons) {
			Map<String, Boolean> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readStringBooleanMap, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(LinkedHashMap.class);
			}
		}
	}

	@Test
	void readStringByteMap() throws IOException {
		TypeReference<Map<String, Byte>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"{ }",
			"{\"a\": null}",
			"{\"a\": 0}",
			"{\"a\": 1}",
			"{\"a\": 0, \"b\": null, \"c\": 2}",
		};
		for (String json : jsons) {
			Map<String, Byte> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readStringByteMap, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(LinkedHashMap.class);
			}
		}
	}

	@Test
	void readStringShortMap() throws IOException {
		TypeReference<Map<String, Short>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"{ }",
			"{\"a\": null}",
			"{\"a\": 0}",
			"{\"a\": 1}",
			"{\"a\": 0, \"b\": null, \"c\": 2}",
		};
		for (String json : jsons) {
			Map<String, Short> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readStringShortMap, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(LinkedHashMap.class);
			}
		}
	}

	@Test
	void readStringIntMap() throws IOException {
		TypeReference<Map<String, Integer>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"{ }",
			"{\"a\": null}",
			"{\"a\": 0}",
			"{\"a\": 1}",
			"{\"a\": 0, \"b\": null, \"c\": 2}",
		};
		for (String json : jsons) {
			Map<String, Integer> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readStringIntMap, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(LinkedHashMap.class);
			}
		}
	}

	@Test
	void readStringLongMap() throws IOException {
		TypeReference<Map<String, Long>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"{ }",
			"{\"a\": null}",
			"{\"a\": 0}",
			"{\"a\": 1}",
			"{\"a\": 0, \"b\": null, \"c\": 2}",
		};
		for (String json : jsons) {
			Map<String, Long> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readStringLongMap, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(LinkedHashMap.class);
			}
		}
	}

	@Test
	void readStringCharMap() throws IOException {
		TypeReference<Map<String, Character>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"{ }",
			"{\"a\": null}",
			"{\"a\": \"a\"}",
			"{\"a\": \"b\"}",
			"{\"a\": \"a\", \"b\": null, \"c\": \"c\"}",
		};
		for (String json : jsons) {
			Map<String, Character> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readStringCharMap, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(LinkedHashMap.class);
			}
		}
	}

	@Test
	void readStringFloatMap() throws IOException {
		TypeReference<Map<String, Float>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"{ }",
			"{\"a\": null}",
			"{\"a\": 0.0}",
			"{\"a\": 1.0}",
			"{\"a\": 0.0, \"b\": null, \"c\": 2.0, \"d\": \"NaN\", \"e\": \"Infinity\", \"f\": \"-Infinity\"}",
		};
		for (String json : jsons) {
			Map<String, Float> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readStringFloatMap, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(LinkedHashMap.class);
			}
		}
	}

	@Test
	void readStringDoubleMap() throws IOException {
		TypeReference<Map<String, Double>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"{ }",
			"{\"a\": null}",
			"{\"a\": 0.0}",
			"{\"a\": 1.0}",
			"{\"a\": 0.0, \"b\": null, \"c\": 2.0, \"d\": \"NaN\", \"e\": \"Infinity\", \"f\": \"-Infinity\"}",
		};
		for (String json : jsons) {
			Map<String, Double> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readStringDoubleMap, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(LinkedHashMap.class);
			}
		}
	}

	@Test
	void readStringStringMap() throws IOException {
		TypeReference<Map<String, String>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"{ }",
			"{\"a\": null}",
			"{\"a\": \"a\"}",
			"{\"a\": \"b\"}",
			"{\"a\": \"a\", \"b\": null, \"c\": \"c\"}",
		};
		for (String json : jsons) {
			Map<String, String> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readStringStringMap, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(LinkedHashMap.class);
			}
		}
	}
}
