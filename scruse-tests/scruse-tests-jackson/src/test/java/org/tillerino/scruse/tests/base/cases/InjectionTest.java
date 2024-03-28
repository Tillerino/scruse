package org.tillerino.scruse.tests.base.cases;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.base.cases.InjectionSerde.InjectionSerializationContext;
import org.tillerino.scruse.tests.model.SelfReferencingRecord;

import java.io.IOException;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test demonstrates how to hack injection into the serialization process.
 */
class InjectionTest {
	InjectionSerde serde = new InjectionSerdeImpl();

	SelfReferencingRecord b = new SelfReferencingRecord("b", null);
	SelfReferencingRecord a = new SelfReferencingRecord("a", b);

	List<SelfReferencingRecord> list = List.of(b, a, b, a);

	String json = """
		[{"prop":"b","self":null},{"prop":"a","self":"b"},"b","a"]
		""";

	@Test
	void testInjectionOutput() throws IOException {
		assertThatJson(OutputUtils.serialize2(list, new InjectionSerializationContext(), serde::writeList))
			.isEqualTo(json);
	}

	/**
	 * NOCOPY: The output code can be automatically copied, the input code cannot.
	 * Not much to prove anyway, since this is just a demo.
	 */
	@Test
	void testInjectionInput() throws IOException {
		List<SelfReferencingRecord> records = InputUtils.withJacksonJsonParser(json, parser -> serde.readList(parser, new InjectionSerde.InjectionDeserializationContext()));
		assertThat(records).isEqualTo(list);
		assertThat(records.get(1).self()).isSameAs(records.get(0));
		assertThat(records.get(2)).isSameAs(records.get(0));
		assertThat(records.get(3)).isSameAs(records.get(1));
	}
}
