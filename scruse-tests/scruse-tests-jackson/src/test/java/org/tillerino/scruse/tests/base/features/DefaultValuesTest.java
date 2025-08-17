package org.tillerino.scruse.tests.base.features;

import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.SerdeUtil;
import org.tillerino.scruse.tests.model.AnEnum;
import org.tillerino.scruse.tests.model.features.DefaultValuesModel.Mixed;

public class DefaultValuesTest extends ReferenceTest {
    DefaultValuesSerde serde = SerdeUtil.impl(DefaultValuesSerde.class);

    @Test
    void testAllMissing() throws Exception {
        Mixed deserialized = inputUtils.deserialize("{}", serde::read);
        Assertions.assertThat(deserialized)
                .usingRecursiveComparison()
                .isEqualTo(new Mixed(
                        "foo",
                        42,
                        new double[] {1.0, 2.0},
                        AnEnum.ANOTHER_VALUE,
                        Arrays.asList("foo", "bar"),
                        new Mixed(null, 0, null, null, null, null)));
    }
}
