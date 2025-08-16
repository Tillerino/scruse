package org.tillerino.scruse.tests.model.annotations;

import org.tillerino.scruse.annotations.JsonConfig;

@JsonConfig(unknownProperties = JsonConfig.UnknownPropertiesMode.IGNORE)
public record JsonConfigIgnoreUnknown(String name, int value) {}
