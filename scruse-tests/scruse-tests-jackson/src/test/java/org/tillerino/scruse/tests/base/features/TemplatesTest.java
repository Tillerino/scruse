package org.tillerino.scruse.tests.base.features;

import static org.tillerino.scruse.tests.CodeAssertions.assertThatImpl;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CodeAssertions.CompileUnitAssert;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.SerdeUtil;
import org.tillerino.scruse.tests.base.features.TemplatesSerde.*;

class TemplatesTest extends ReferenceTest {
    TemplatedSerde templatedSerde = SerdeUtil.impl(TemplatedSerde.class);

    @Test
    void templatesGenerateMethods() throws Exception {
        CompileUnitAssert assertThat = assertThatImpl(TemplatedSerde.class);
        assertThat.method("readPrimitiveDouble");
        assertThat.method("writePrimitiveDouble");
        assertThat.method("readAnEnum");
        assertThat.method("writeAnEnum");
        assertThat.method("readArrayOfPrimitiveDouble");
        assertThat.method("writeArrayOfPrimitiveDouble");
        assertThat.method("readArrayOfAnEnum").calls("readAnEnum");
        assertThat.method("writeArrayOfAnEnum").calls("writeGenericArray").references("writeAnEnum");
    }

    @Test
    void canDelegateToAndReferenceTemplatedPrototypes() throws Exception {
        CompileUnitAssert assertThat = assertThatImpl(CallsTemplatePrototypes.class);
        assertThat.methods().hasSize(2); // nothing was added here that was declared on the referenced blueprint
        assertThat
                .method("readHasAnEnumArrayProperty")
                .calls("readArrayOfAnEnum")
                .calls("readArrayOfPrimitiveDouble");
        assertThat
                .method("writeHasAnEnumArrayProperty")
                .calls("writeArrayOfAnEnum")
                .calls("writeGenericArray")
                .references("writeArrayOfPrimitiveDouble");
    }
}
