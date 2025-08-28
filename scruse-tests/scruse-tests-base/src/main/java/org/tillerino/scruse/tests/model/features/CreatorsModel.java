package org.tillerino.scruse.tests.model.features;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public interface CreatorsModel {
    @EqualsAndHashCode
    @Getter
    class JsonCreatorConstructorCreatorClass<T> {
        private final T prop;
        private final String s;

        @JsonCreator
        public JsonCreatorConstructorCreatorClass(T notprop, String nots) {
            this.prop = notprop;
            this.s = nots;
        }
    }

    @EqualsAndHashCode
    @Getter
    class JsonCreatorConstructorCreatorSinglePropertyClass<T> {
        private final List<T> prop;

        @JsonCreator(mode = Mode.PROPERTIES)
        public JsonCreatorConstructorCreatorSinglePropertyClass(T notprop) {
            this.prop = List.of(notprop);
        }
    }

    @EqualsAndHashCode
    @Getter
    class JsonCreatorConstructorFactoryClass<T> {
        private final T prop;

        @JsonCreator
        public JsonCreatorConstructorFactoryClass(Map<String, T> props) {
            this.prop = props.get("notprop");
        }
    }

    @EqualsAndHashCode
    @Getter
    class JsonCreatorConstructorFactoryMultiplePropertiesClass<T> {
        private final T prop;
        private final Thread t;

        @JsonCreator(mode = Mode.DELEGATING)
        public JsonCreatorConstructorFactoryMultiplePropertiesClass(T notprop, Thread t) {
            this.prop = notprop;
            this.t = t;
        }
    }

    record JsonCreatorMethodCreatorRecord<T>(T prop, String s) {
        @JsonCreator
        public static <U> JsonCreatorMethodCreatorRecord<U> of(U notprop, String nots) {
            return new JsonCreatorMethodCreatorRecord<>(notprop, nots);
        }
    }

    record JsonCreatorMethodCreatorSinglePropertyRecord<T>(List<T> prop) {
        @JsonCreator(mode = Mode.PROPERTIES)
        public static <U> JsonCreatorMethodCreatorSinglePropertyRecord<U> of(U notprop) {
            return new JsonCreatorMethodCreatorSinglePropertyRecord<>(List.of(notprop));
        }
    }

    record JsonCreatorMethodFactoryRecord<T>(T prop) {
        @JsonCreator
        public static <U> JsonCreatorMethodFactoryRecord<U> of(Map<String, U> props) {
            return new JsonCreatorMethodFactoryRecord<>(props.get("notprop"));
        }
    }

    record JsonCreatorMethodFactoryMultiplePropertiesRecord<T>(T prop, Thread t) {
        @JsonCreator(mode = Mode.DELEGATING)
        public static <U> JsonCreatorMethodFactoryMultiplePropertiesRecord<U> of(U notprop, Thread t) {
            return new JsonCreatorMethodFactoryMultiplePropertiesRecord<>(notprop, t);
        }
    }
}
