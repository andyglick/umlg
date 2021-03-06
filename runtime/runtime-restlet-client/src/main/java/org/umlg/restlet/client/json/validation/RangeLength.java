package org.umlg.restlet.client.json.validation;

import java.util.Map;

public class RangeLength implements UmlgValidation {
    private int min;
    private int max;

    public RangeLength(Map<String, Integer> values) {
        super();
        this.min = values.get("min");
        this.max = values.get("max");
    }
}
