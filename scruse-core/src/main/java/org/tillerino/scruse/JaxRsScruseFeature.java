package org.tillerino.scruse;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

public class JaxRsScruseFeature implements Feature {
    @Override
    public boolean configure(FeatureContext context) {

        return false;
    }

    public class ScruseMessageBodyReader {

    }
}
