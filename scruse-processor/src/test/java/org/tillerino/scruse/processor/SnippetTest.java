package org.tillerino.scruse.processor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SnippetTest {
	@Test
	void empty() {
		assertEqual(Snippet.of(""), "");
	}

	@Test
	void simple() {
		assertEqual(Snippet.of("a$Sb", ""), "a$Sb", "");
	}

	@Test
	void nested() {
		assertEqual(Snippet.of("a$Cb", Snippet.of("$S", "y")), "a$Sb", "y");
	}

	@Test
	void dollar() {
		assertEqual(Snippet.of("a$S$$$Sb", "a", "b"), "a$S$$$Sb", "a", "b");
	}

	@Test
	void startsWith() {
		assertEqual(Snippet.of("$Cbbbb$Sc", Snippet.of("$S", "y"), "z"), "$Sbbbb$Sc", "y", "z");
	}

	static void assertEqual(Snippet s, String format, Object... args) {
		assertThat(s.format()).isEqualTo(format);
		assertThat(s.args()).containsExactly(args);
	}
}