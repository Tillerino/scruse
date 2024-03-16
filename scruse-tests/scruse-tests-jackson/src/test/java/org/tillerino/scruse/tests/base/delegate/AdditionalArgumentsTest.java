package org.tillerino.scruse.tests.base.delegate;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.OutputUtils;

import java.io.IOException;
import java.util.*;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class AdditionalArgumentsTest {
	AdditionalArgumentsSerde serde = new AdditionalArgumentsSerdeImpl();

	@Test
	void output() throws IOException {
		ArrayList<Integer> l = new ArrayList<>();
		String json = OutputUtils.serialize2(Map.of("a", 1), l, serde::writeStringIntMap);
		assertThatJson(json).isEqualTo("{\"a\":\"\"}");
		assertThat(l).containsExactly(1);
	}

	@Test
	void input() throws IOException {
		Queue<Integer> l = new LinkedList<>(List.of(1));
		Map<String, Integer> map = InputUtils.deserialize2("{\"a\":\"\"}", l, serde::readStringIntMap);
		assertThat(map).containsExactly(Map.entry("a", 1));
		assertThat(l).isEmpty();
	}
}
