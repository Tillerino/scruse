package org.tillerino.scruse.tests.base.generics;

import org.tillerino.scruse.annotations.JsonConfig;

@JsonConfig(implement = JsonConfig.ImplementationMode.DO_IMPLEMENT)
public interface StringSerde extends GenericOutput<String>, GenericInput<String> {}
