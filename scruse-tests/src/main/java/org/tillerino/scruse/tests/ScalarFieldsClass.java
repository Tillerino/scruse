package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.stream.JsonWriter;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;
import java.util.Objects;

public class ScalarFieldsClass {
	// !!! CARE !!! equals and hash code have been generated
	public boolean bo;
	public byte by;
	public short s;
	public int i;
	public long l;
	public char c;
	public float f;
	public double d;
	public Boolean bbo;
	public Byte bby;
	public Short ss;
	public Integer ii;
	public Long ll;
	public Character cc;
	public Float ff;
	public Double dd;
	public String str;

	public ScalarFieldsClass() {
	}

	public ScalarFieldsClass(boolean bo, byte by, short s, int i, long l, char c, float f, double d, Boolean bbo, Byte bby, Short ss, Integer ii, Long ll, Character cc, Float ff, Double dd, String str) {
		this.bo = bo;
		this.by = by;
		this.s = s;
		this.i = i;
		this.l = l;
		this.c = c;
		this.f = f;
		this.d = d;
		this.bbo = bbo;
		this.bby = bby;
		this.ss = ss;
		this.ii = ii;
		this.ll = ll;
		this.cc = cc;
		this.ff = ff;
		this.dd = dd;
		this.str = str;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ScalarFieldsClass that = (ScalarFieldsClass) o;
		return bo == that.bo && by == that.by && s == that.s && i == that.i && l == that.l && c == that.c && Float.compare(that.f, f) == 0 && Double.compare(that.d, d) == 0 && Objects.equals(bbo, that.bbo) && Objects.equals(bby, that.bby) && Objects.equals(ss, that.ss) && Objects.equals(ii, that.ii) && Objects.equals(ll, that.ll) && Objects.equals(cc, that.cc) && Objects.equals(ff, that.ff) && Objects.equals(dd, that.dd) && Objects.equals(str, that.str);
	}

	@Override
	public int hashCode() {
		return Objects.hash(bo, by, s, i, l, c, f, d, bbo, bby, ss, ii, ll, cc, ff, dd, str);
	}

	interface Output {
		@JsonOutput
		void write(ScalarFieldsClass record, JsonGenerator generator) throws IOException;
		@JsonOutput
		void write(ScalarFieldsClass record, JsonWriter generator) throws IOException;
		@JsonOutput
		JsonNode write(ScalarFieldsClass record);
	}

	interface Input {
		@JsonInput
		ScalarFieldsClass read(JsonParser parser) throws IOException;
	}
}
