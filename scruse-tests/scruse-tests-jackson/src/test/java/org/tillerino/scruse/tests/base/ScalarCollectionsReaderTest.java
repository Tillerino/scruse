package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

class ScalarCollectionsReaderTest {
	ScalarCollectionsReader impl = new ScalarCollectionsReaderImpl();

	@Test
	void testBooleanSet() throws IOException {
		TypeReference<Set<Boolean>> typeRef = new TypeReference<>() {
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
			Set<Boolean> set = InputUtils.assertIsEqualToDatabind(json, impl::readBooleanSet, typeRef);
			if (set != null) {
				assertThat(set).isInstanceOf(LinkedHashSet.class);
			}
		}
	}

	@Test
	void testBooleanTreeSet() throws IOException {
		TypeReference<TreeSet<Boolean>> typeRef = new TypeReference<>() {
		};
		// cannot contain null
		String[] jsons = {
			"null",
			"[]",
			"[true]",
			"[false]",
			"[true,false]",
		};
		for (String json : jsons) {
			Set<Boolean> set = InputUtils.assertIsEqualToDatabind(json, impl::readBooleanTreeSet, typeRef);
			if (set != null) {
				assertThat(set).isInstanceOf(TreeSet.class);
			}
		}
	}
}
