package org.tillerino.scruse.processor;

import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName;

import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

record ScruseBlueprint(AtomicBoolean toBeGenerated, FullyQualifiedClassName className, TypeMirror typeMirror, List<ScruseMethod> methods, List<ScruseBlueprint> uses) {
}
