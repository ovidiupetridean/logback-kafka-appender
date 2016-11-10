package com.github.danielwegener.logback.kafka.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Mark Paluch
 * @since 28.02.14 09:50
 */
public class Values {
    private Map<String, Object> values = new HashMap<String, Object>();

    public Values() {
    }

    public Values(String name, Object value) {
        if (name != null && value != null) {
            values.put(name, value);
        }
    }

    public boolean hasValues() {
        return size() != 0;
    }

    public int size() {
        return values.size();
    }

    public Set<String> getEntryNames() {
        return Collections.unmodifiableSet(values.keySet());
    }

    public void setValue(String key, Object value) {
        values.put(key, value);
    }

    public <T> T getValue(String key) {
        return (T) values.get(key);
    }

}
