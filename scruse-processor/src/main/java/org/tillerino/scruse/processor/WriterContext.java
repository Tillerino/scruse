package org.tillerino.scruse.processor;

import java.util.Map;
import java.util.Set;

public record WriterContext(Map<String, ScruseMethod> writers, Set<ScruseMethod> usedWriters) {
}
