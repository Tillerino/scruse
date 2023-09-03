package org.tillerino.scruse.processor;

import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

record ScruseBlueprint(AtomicBoolean toBeGenerated, FullyQualifiedClassName className, TypeElement typeElement,
											 List<ScruseMethod> methods, List<ScruseBlueprint> uses) {
}
