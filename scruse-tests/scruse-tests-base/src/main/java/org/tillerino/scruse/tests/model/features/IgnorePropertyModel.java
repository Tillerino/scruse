package org.tillerino.scruse.tests.model.features;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface IgnorePropertyModel {
    class JsonIgnoreOnFieldWithGetter {
        @JsonIgnore
        private String s;

        public JsonIgnoreOnFieldWithGetter(String s) {
            this.s = s;
        }

        public String getS() {
            return s;
        }
    }

    /** Field annotation takes precedence. */
    class JsonIgnoreOnFieldAndGetter {
        @JsonIgnore
        private String s;

        public JsonIgnoreOnFieldAndGetter(String s) {
            this.s = s;
        }

        @JsonIgnore(false)
        public String getS() {
            return s;
        }
    }

    class JsonIgnoreOnFieldWithoutGetter {
        @JsonIgnore
        public String s;

        public JsonIgnoreOnFieldWithoutGetter(String s) {
            this.s = s;
        }
    }

    class JsonIgnoreOnGetter {
        private String s;

        @JsonIgnore
        public String getS() {
            return s;
        }

        public JsonIgnoreOnGetter(String s) {
            this.s = s;
        }
    }

    record JsonIgnoreOnRecordComponent(@JsonIgnore String s) {}
}
