package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import lombok.EqualsAndHashCode;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.AnEnum;

@EqualsAndHashCode
class ScalarFieldsClass {
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
    public AnEnum en;

    public ScalarFieldsClass() {}

    public ScalarFieldsClass(
            boolean bo,
            byte by,
            short s,
            int i,
            long l,
            char c,
            float f,
            double d,
            Boolean bbo,
            Byte bby,
            Short ss,
            Integer ii,
            Long ll,
            Character cc,
            Float ff,
            Double dd,
            String str,
            AnEnum en) {
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
        this.en = en;
    }

    interface Serde {
        @JsonOutput
        void write(ScalarFieldsClass record, JsonGenerator generator) throws Exception;

        @JsonInput
        ScalarFieldsClass read(JsonParser parser) throws Exception;
    }
}
