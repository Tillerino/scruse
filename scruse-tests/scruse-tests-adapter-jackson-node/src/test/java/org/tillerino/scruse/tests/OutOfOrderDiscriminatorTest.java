package org.tillerino.scruse.tests;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.alt.adapter.jsonnode.PolymorphismSerde;
import org.tillerino.scruse.tests.alt.adapter.jsonnode.PolymorphismSerdeImpl;

class OutOfOrderDiscriminatorTest extends ReferenceTest {
    PolymorphismSerde serde = new PolymorphismSerdeImpl();

    /**
     * This tests a special case that we can only handle with {@link com.fasterxml.jackson.databind.JsonNode}. The
     * discriminator is NOT the first field in the object.
     */
    @Test
    void outOfOrderDiscriminator() throws Exception {
        PolymorphismSerde.SealedInterface deserialize = inputUtils.deserialize(
                "{\"s\":\"abc\", \"@c\": \".PolymorphismSerde$RecordOne\"}", serde::readPolymorphism);
    }
}
