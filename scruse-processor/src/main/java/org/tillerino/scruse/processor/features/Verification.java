package org.tillerino.scruse.processor.features;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import org.tillerino.scruse.annotations.JsonConfig.VerificationMode;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseBlueprint;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.config.ConfigProperty;
import org.tillerino.scruse.processor.config.ConfigProperty.ConfigPropertyRetriever;
import org.tillerino.scruse.processor.config.ConfigProperty.LocationKind;
import org.tillerino.scruse.processor.config.ConfigProperty.MergeFunction;
import org.tillerino.scruse.processor.util.Exceptions;

public class Verification {
    public static ConfigProperty<VerificationMode> VERIFY_SYMMETRY = ConfigProperty.createConfigProperty(
            List.of(LocationKind.values()),
            List.of(ConfigPropertyRetriever.jsonConfigPropertyRetriever("verifySymmetry", VerificationMode.class)),
            VerificationMode.NO_VERIFICATION,
            MergeFunction.notDefault(VerificationMode.NO_VERIFICATION),
            null);

    final Map<ScruseBlueprint, ForBlueprint> st = new ConcurrentHashMap<>();
    final AnnotationProcessorUtils utils;

    public Verification(AnnotationProcessorUtils utils) {
        this.utils = utils;
    }

    public ForBlueprint startBlueprint(ScruseBlueprint blueprint) {
        return st.merge(blueprint, new ForBlueprint(blueprint), (x, y) -> {
            throw Exceptions.unexpected();
        });
    }

    private void log(AnyConfig config, String format, Object... arguments) {
        switch (config.resolveProperty(VERIFY_SYMMETRY).value()) {
            case FAIL -> utils.messager.printMessage(Kind.ERROR, format.formatted(arguments));
            case WARN -> utils.messager.printMessage(Kind.WARNING, format.formatted(arguments));
            default -> {}
        }
    }

    public class ForBlueprint {
        private final Map<TypeMirror, ForType> types = new ConcurrentHashMap<>();
        private final ScruseBlueprint blueprint;

        public ForBlueprint(ScruseBlueprint blueprint) {
            this.blueprint = blueprint;
        }

        public ProtoAndProps addReader(ScrusePrototype prototype, TypeMirror type) {
            ForType forType = types.computeIfAbsent(type, __ -> new ForType());
            if (forType.read != null) {
                log(
                        prototype.config(),
                        "Duplicate readers for %s. Nested readers are considered for this verification.\n"
                                + "on: %s\nFirst reader in prototype: %s\nSecond reader in prototype: %s\n",
                        type,
                        blueprint,
                        forType.read.prototype,
                        prototype);
            }
            return forType.read = new ProtoAndProps(prototype);
        }

        public ProtoAndProps addWriter(ScrusePrototype prototype, TypeMirror type) {
            ForType forType = types.computeIfAbsent(type, __ -> new ForType());
            if (forType.write != null) {
                log(
                        prototype.config(),
                        "Duplicate writers for %s. Nested writers are considered for this verification.\n"
                                + "on: %s\nFirst writer in prototype: %s\nSecond writer in prototype: %s\n",
                        type,
                        blueprint,
                        forType.write.prototype,
                        prototype);
            }
            return forType.write = new ProtoAndProps(prototype);
        }

        public void finish() {
            types.forEach((type, forType) -> {
                if (forType.read != null && forType.write == null) {
                    log(
                            blueprint.config,
                            "Reader defined but no writer for %s\non %s\nreader: %s",
                            type,
                            blueprint,
                            forType.read.prototype);
                }
                if (forType.read == null && forType.write != null) {
                    log(
                            blueprint.config,
                            "Writer defined but no reader for %s\non %s\nwriter: %s",
                            type,
                            blueprint,
                            forType.write.prototype);
                }
                if (forType.read != null && forType.write != null) {
                    forType.read.props.forEach((propertyName, propertyType) -> {
                        if (!forType.write.props.containsKey(propertyName)) {
                            log(
                                    propertyType.config,
                                    "Reading but not writing property `%s`\nof %s\nreader: %s\nwriter: %s",
                                    propertyName,
                                    type,
                                    forType.read.prototype,
                                    forType.write.prototype);
                        }
                    });
                    forType.write.props.forEach((propertyName, propertyType) -> {
                        if (!forType.read.props.containsKey(propertyName)) {
                            log(
                                    propertyType.config,
                                    "Writing but not reading property `%s`\nof %s\nwriter: %s\nreader: %s",
                                    propertyName,
                                    type,
                                    forType.write.prototype,
                                    forType.read.prototype);
                        }
                    });
                }
            });
        }
    }

    class ForType {
        ProtoAndProps read;
        ProtoAndProps write;
    }

    public class ProtoAndProps {
        final ScrusePrototype prototype;
        final Map<String, Prop> props = new LinkedHashMap<>();

        public ProtoAndProps(ScrusePrototype prototype) {
            this.prototype = prototype;
        }

        public void addProperty(String name, TypeMirror type, AnyConfig propertyConfig) {
            Prop existingType = props.put(name, new Prop(type, propertyConfig));
            if (existingType != null) {
                log(propertyConfig, "Duplicate property `%s`\non %s", name, prototype);
            }
        }

        record Prop(TypeMirror typeMirror, AnyConfig config) {}
    }
}
