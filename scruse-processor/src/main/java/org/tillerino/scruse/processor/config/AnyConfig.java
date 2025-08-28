package org.tillerino.scruse.processor.config;

import jakarta.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.util.accessor.Accessor;
import org.mapstruct.ap.internal.util.accessor.AccessorType;
import org.mapstruct.ap.internal.util.accessor.ExecutableElementAccessor;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseBlueprint;
import org.tillerino.scruse.processor.config.ConfigProperty.InstantiatedProperty;
import org.tillerino.scruse.processor.config.ConfigProperty.LocationKind;
import org.tillerino.scruse.processor.features.*;

public final class AnyConfig {
    static final ConfigProperty[] available = {
        UnknownProperties.UNKNOWN_PROPERTIES,
        ConfigProperty.USES,
        ConfigProperty.DELEGATEE,
        ConfigProperty.IMPLEMENT,
        PropertyName.PROPERTY_NAME,
        IgnoreProperty.IGNORE_PROPERTY,
        IgnoreProperties.IGNORED_PROPERTIES,
        Verification.VERIFY_SYMMETRY,
        // leave a trailing comma for cleaner diffs :)
    };

    static {
        Arrays.sort(available, Comparator.comparingInt(p -> p.index));
    }

    static final Comparator<InstantiatedProperty> COMPARATOR = Comparator.<InstantiatedProperty>comparingInt(
                    prop -> prop.property().index)
            .thenComparing(InstantiatedProperty::locationKind);

    private final List<InstantiatedProperty> properties;

    public AnyConfig(List<InstantiatedProperty> properties) {
        // validate that the properties are sorted
        for (int i = 1; i < properties.size(); i++) {
            if (COMPARATOR.compare(properties.get(i - 1), properties.get(i)) >= 0) {
                throw new ContextedRuntimeException("properties not sorted monotonously")
                        .addContextValue("properties", properties);
            }
        }
        this.properties = List.copyOf(properties); // copy so order cannot be changed externally
    }

    public static AnyConfig create(Element element, LocationKind elementType, AnnotationProcessorUtils utils) {
        // TODO resolve config
        List<InstantiatedProperty> list = Stream.of(available)
                .flatMap(prop -> prop.instantiate(element, elementType, utils).stream())
                .toList();

        return new AnyConfig(list);
    }

    /**
     * @param property the accessor. in recursive calls the element might be missing
     * @param accessorName to reconstruct the element in recursive calls
     * @param dto the dto containing the property
     * @param canonicalPropertyName to find the field
     * @return can return null in a recursive call
     */
    public static AnyConfig fromAccessorConsideringField(
            Accessor property,
            String accessorName,
            Type dto,
            String canonicalPropertyName,
            AnnotationProcessorUtils utils) {

        // nullable during recursion. if element is null, this means the accessor does not exist in this type, but maybe
        // parent types.
        AnyConfig accessorConfig = fromAccessorAndField(property, dto, canonicalPropertyName, utils);

        if (property.getAccessorType() != AccessorType.GETTER && property.getAccessorType() != AccessorType.SETTER) {
            return accessorConfig;
        }

        for (Type directSuperType : dto.getDirectSuperTypes()) {
            if (directSuperType.toString().equals(Object.class.getName())) {
                continue;
            }
            TypeElement superTypeElement = directSuperType.getTypeElement();
            Optional<ExecutableElement> superMethod =
                    ElementFilter.methodsIn(superTypeElement.getEnclosedElements()).stream()
                            .filter(m -> m.getSimpleName().contentEquals(accessorName))
                            .findFirst();
            ExecutableElementAccessor parentAccessor = new ExecutableElementAccessor(
                    superMethod.orElse(null), property.getAccessedType(), property.getAccessorType());
            AnyConfig superConfig = fromAccessorConsideringField(
                    parentAccessor, accessorName, directSuperType, canonicalPropertyName, utils);
            if (superConfig != null) {
                accessorConfig = accessorConfig != null ? accessorConfig.merge(superConfig) : superConfig;
            }
        }

        return accessorConfig;
    }

    private static AnyConfig fromAccessorAndField(
            Accessor property, Type dto, String canonicalPropertyName, AnnotationProcessorUtils utils) {
        AnyConfig accessorConfig =
                property.getElement() != null ? create(property.getElement(), LocationKind.PROPERTY, utils) : null;
        if (property.getAccessorType() == AccessorType.FIELD) {
            return accessorConfig;
        }

        Optional<AnyConfig> maybeFieldConfig =
                ElementFilter.fieldsIn(dto.getTypeElement().getEnclosedElements()).stream()
                        .filter(f -> f.getSimpleName().contentEquals(canonicalPropertyName))
                        .findFirst()
                        .map(f -> create(f, LocationKind.PROPERTY, utils));

        return maybeFieldConfig
                .map(anyConfig -> accessorConfig != null ? anyConfig.merge(accessorConfig) : anyConfig)
                .orElse(accessorConfig);
    }

    /**
     * Uses are appended during merging of configuration. So when we search them, we do it in reverse to make sure that
     * the latest gets prio.
     */
    public List<ScruseBlueprint> reversedUses() {
        Set<ScruseBlueprint> uses = resolveProperty(ConfigProperty.USES).value();
        List<ScruseBlueprint> reversedUses = new ArrayList<>(uses);
        Collections.reverse(reversedUses);
        return reversedUses;
    }

    public AnyConfig merge(AnyConfig weaker) {
        List<InstantiatedProperty> mergedProperties = new ArrayList<>();

        for (int i = 0, j = 0; i < properties.size() || j < weaker.properties.size(); ) {
            if (i == properties.size()) {
                mergedProperties.add(weaker.properties.get(j++));
            } else if (j == weaker.properties.size()) {
                mergedProperties.add(properties.get(i++));
            } else if (COMPARATOR.compare(properties.get(i), weaker.properties.get(j)) < 0) {
                mergedProperties.add(properties.get(i++));
            } else if (COMPARATOR.compare(properties.get(i), weaker.properties.get(j)) > 0) {
                mergedProperties.add(weaker.properties.get(j++));
            } else {
                InstantiatedProperty strong = properties.get(i++);
                InstantiatedProperty weak = weaker.properties.get(j++);
                mergedProperties.add(strong.property().merger.merge(strong, weak));
            }
        }

        return new AnyConfig(mergedProperties);
    }

    public <T> ResolvedProperty<T> resolveProperty(ConfigProperty<T> prop) {
        Optional<InstantiatedProperty<T>> value = Optional.empty();
        for (InstantiatedProperty<?> p : properties) {
            // properties are sorted from stronges to weakest
            if (p.property() == prop) {
                InstantiatedProperty<T> weaker = (InstantiatedProperty<T>) p;
                value = value.map(stronger -> prop.merger.merge(stronger, weaker))
                        .or(() -> Optional.of(weaker));
            }
        }
        return value.map(inst -> new ResolvedProperty<>(inst.value(), inst.sourceLocation()))
                .orElseGet(() -> new ResolvedProperty<>(prop.defaultValue, null));
    }

    public AnyConfig keepUntilIncluding(LocationKind locationKind) {
        return new AnyConfig(properties.stream()
                .filter(p -> p.locationKind().compareTo(locationKind) <= 0)
                .toList());
    }

    public record ResolvedProperty<T>(T value, @Nullable String location) {}
}
