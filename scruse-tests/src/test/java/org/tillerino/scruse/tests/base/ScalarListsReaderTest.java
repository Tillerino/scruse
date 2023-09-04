package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScalarListsReaderTest {
	ScalarListsReader impl = new ScalarListsReaderImpl();

	@Test
	void testBooleanList() throws IOException {
		TypeReference<List<Boolean>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[true]",
			"[false]",
			"[true,false,null]",
		};
		for (String json : jsons) {
			List<Boolean> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readBooleanList, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(ArrayList.class);
			}
		}
	}

	@Test
	void testByteList() throws IOException {
		TypeReference<List<Byte>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[0]",
			"[1]",
			"[0,1,null]",
		};
		for (String json : jsons) {
			List<Byte> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readByteList, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(ArrayList.class);
			}
		}
	}

	@Test
	void testShortList() throws IOException {
		TypeReference<List<Short>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[0]",
			"[1]",
			"[0,1,null]",
		};
		for (String json : jsons) {
			List<Short> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readShortList, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(ArrayList.class);
			}
		}
	}

	@Test
	void testIntegerList() throws IOException {
		TypeReference<List<Integer>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[0]",
			"[1]",
			"[0,1,null]",
		};
		for (String json : jsons) {
			List<Integer> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readIntegerList, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(ArrayList.class);
			}
		}
	}

	@Test
	void testLongList() throws IOException {
		TypeReference<List<Long>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[0]",
			"[1]",
			"[0,1,null]",
		};
		for (String json : jsons) {
			List<Long> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readLongList, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(ArrayList.class);
			}
		}
	}

	@Test
	void testFloatList() throws IOException {
		TypeReference<List<Float>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[0.0]",
			"[1.0]",
			"[0.0,1.0,null,\"NaN\",\"Infinity\",\"-Infinity\"]",
		};
		for (String json : jsons) {
			List<Float> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readFloatList, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(ArrayList.class);
			}
		}
	}

	@Test
	void testDoubleList() throws IOException {
		TypeReference<List<Double>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[0.0]",
			"[1.0]",
			"[0.0,1.0,null,\"NaN\",\"Infinity\",\"-Infinity\"]",
		};
		for (String json : jsons) {
			List<Double> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readDoubleList, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(ArrayList.class);
			}
		}
	}

	@Test
	void testCharacterList() throws IOException {
		TypeReference<List<Character>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[null]",
			"[\"a\"]",
			"[\"b\"]",
			"[\"a\",\"b\",null]",
		};
		for (String json : jsons) {
			List<Character> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readCharacterList, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(ArrayList.class);
			}
		}
	}

	@Test
	void testStringList() throws IOException {
		TypeReference<List<String>> typeRef = new TypeReference<>() {
		};
		String[] jsons = {
			"null",
			"[]",
			"[\"\"]",
			"[\"a\"]",
			"[\"b\"]",
			"[\"a\",\"b\",null]",
		};
		for (String json : jsons) {
			List<String> list = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, impl::readStringList, typeRef);
			if (list != null) {
				assertThat(list).isInstanceOf(ArrayList.class);
			}
		}
	}
}
