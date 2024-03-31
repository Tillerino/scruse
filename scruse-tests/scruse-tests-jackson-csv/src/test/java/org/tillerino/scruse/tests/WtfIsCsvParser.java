package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.io.ContentReference;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WtfIsCsvParser {
	record A(String b, String c) { }
	record D(String e) { }
	record Record(A a, D d, int e) { }

	@Test
	void name() throws IOException {
		CsvParser parser = new CsvFactory().createParser("a.b,a.c,d.e,e\nA.B,A.C,D.E,1");

		MyParserBase p = new MyParserBase(parser);
		System.out.println(p.currentToken());
		System.out.println(new ObjectMapper().readValue(p, Record.class));
	}

	private static class MyParserBase extends ParserBase {
		private final CsvParser csv;

		List<Object> tokens;

		int pos = -1;

		public MyParserBase(CsvParser csv) {
			super(new IOContext(null, ContentReference.rawReference(null), false), 0);
			this.csv = csv;
		}

		@Override
		public ObjectCodec getCodec() {
			return null;
		}

		@Override
		public void setCodec(ObjectCodec oc) {

		}

		@Override
		public JsonToken nextToken() throws IOException {
			prepareHeader(csv);
			pos = (pos + 1) % tokens.size();
			if (pos == 0) {
				if (csv.nextToken() != JsonToken.START_ARRAY) {
					throw new IOException();
				}
			}
			if (pos == tokens.size() - 1) {
				if (csv.nextToken() != JsonToken.END_ARRAY) {
					throw new IOException();
				}
			}

			Object o = tokens.get(pos);
			_currToken = o == null ? csv.nextToken() : o instanceof JsonToken t ? t : JsonToken.FIELD_NAME;
			System.out.println(o + " " + _currToken);
			return _currToken;
		}

		@Override
		public String getCurrentName() {
			return (String) tokens.get(pos);
		}

		@Override
		public String getText() throws IOException {
			return csv.getText();
		}

		@Override
		public char[] getTextCharacters() throws IOException {
			return csv.getTextCharacters();
		}

		@Override
		public int getTextLength() throws IOException {
			return csv.getTextLength();
		}

		@Override
		public int getTextOffset() throws IOException {
			return csv.getTextOffset();
		}

		@Override
		protected void _closeInput() throws IOException {
			csv.close();
		}

		private void prepareHeader(CsvParser parser) throws IOException {
			if (tokens != null) {
				return;
			}
			tokens = new ArrayList<>(List.of(JsonToken.START_OBJECT));
			int objectDepth = 0;

			if (!parser.hasCurrentToken()) {
				parser.nextToken();
			}

			String[] previousHeader = { };
			try {
				if (parser.currentToken() != JsonToken.START_ARRAY) {
					throw new IOException();
				}
				parser.nextToken();
				while (parser.currentToken() != JsonToken.END_ARRAY) {
					String text = getText();
					if (text.isBlank()) {
						throw new IOException();
					}
					String[] segments = text.split("\\.");

					int commonPrefix = commonPrefix(previousHeader, segments);
					while (objectDepth > commonPrefix) {
						tokens.add(JsonToken.END_OBJECT);
						objectDepth--;
					}

					for(int i = commonPrefix; i < segments.length; i++) {
						if (objectDepth < i) {
							tokens.add(JsonToken.START_OBJECT);
							objectDepth++;
						}
						tokens.add(segments[i]);
					}

					tokens.add(null);
					previousHeader = segments;
					parser.nextToken();
				}

				tokens.add(JsonToken.END_OBJECT);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	static int commonPrefix(String[] a, String[] b) {
		int i = 0;
		while (i < a.length && i < b.length && a[i].equals(b[i])) {
			i++;
		}
		return i;
	}
}
