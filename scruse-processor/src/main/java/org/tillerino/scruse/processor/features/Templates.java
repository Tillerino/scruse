package org.tillerino.scruse.processor.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.tillerino.scruse.annotations.JsonTemplate;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseBlueprint;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.config.ConfigProperty.LocationKind;
import org.tillerino.scruse.processor.features.Generics.TypeVar;
import org.tillerino.scruse.processor.util.Annotations.AnnotationMirrorWrapper;
import org.tillerino.scruse.processor.util.Annotations.AnnotationValueWrapper;
import org.tillerino.scruse.processor.util.InstantiatedMethod;
import org.tillerino.scruse.processor.util.PrototypeKind;

public record Templates(AnnotationProcessorUtils utils) {
    public List<ScrusePrototype> instantiateTemplatedPrototypes(ScruseBlueprint blueprint) {
        List<ScrusePrototype> instantiatedPrototypes = new ArrayList<>();
        // TODO multiple
        AnnotationMirrorWrapper templateAnnotation = utils.annotations
                .findAnnotation(blueprint.typeElement, JsonTemplate.class.getCanonicalName())
                .orElseThrow(() -> new ContextedRuntimeException("?"));
        List<Template> templates = findTemplates(templateAnnotation);
        List<TypeMirror> types = findTypes(templateAnnotation);
        for (TypeMirror type : types) {
            for (Template template : templates) {
                PrototypeKind prototypeKind = template.kind.withJavaType(type);
                InstantiatedMethod instantiatedMethod = utils.generics
                        .applyTypeBindings(template.method, Map.of(template.typeVar, type))
                        .withName(prototypeKind.defaultMethodName());

                instantiatedPrototypes.add(
                        ScrusePrototype.of(blueprint, instantiatedMethod, prototypeKind, utils, false));
            }
        }
        return instantiatedPrototypes;
    }

    private List<Template> findTemplates(AnnotationMirrorWrapper templateAnnotation) {
        return templateAnnotation
                .method("templates", false)
                .orElseThrow(() -> new ContextedRuntimeException("?"))
                .asArray()
                .stream()
                .map(templateWrapper -> {
                    TypeMirror templateType = templateWrapper.asTypeMirror();
                    List<InstantiatedMethod> templateMethods =
                            utils.generics.instantiateMethods(templateType, LocationKind.PROTOTYPE);
                    if (templateMethods.size() != 1) {
                        throw new ContextedRuntimeException("Template is not a functional interface")
                                .addContextValue("template", templateType);
                    }
                    InstantiatedMethod template = templateMethods.get(0);
                    PrototypeKind prototypeKind = PrototypeKind.of(template, utils)
                            .orElseThrow(() -> new ContextedRuntimeException("Template prototype of unknown kind")
                                    .addContextValue("prototype", template));
                    if (!(prototypeKind.javaType() instanceof TypeVariable v)) {
                        throw new ContextedRuntimeException("Template prototype must serialize a type variable")
                                .addContextValue("prototype", template)
                                .addContextValue("serialized", prototypeKind.javaType());
                    }
                    return new Template(template, prototypeKind, TypeVar.of(v));
                })
                .toList();
    }

    private List<TypeMirror> findTypes(AnnotationMirrorWrapper templateAnnotation) {
        return templateAnnotation
                .method("types", false)
                .orElseThrow(() -> new ContextedRuntimeException("?"))
                .asArray()
                .stream()
                .map(AnnotationValueWrapper::asTypeMirror)
                .toList();
    }

    record Template(InstantiatedMethod method, PrototypeKind kind, TypeVar typeVar) {}
}
