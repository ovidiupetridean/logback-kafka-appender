package com.github.danielwegener.logback.kafka.message;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by opetridean on 11/10/16.
 */
public class LogMessageField implements MessageField {

    public static final String VERBOSE_LOGGING_PROPERTY = "logstash-gelf.LogMessageField.verbose";

    private static final String DEFAULT_MAPPING = "default-logstash-fields.properties";

    /**
     * Named references to common log event fields.
     */
    public static enum NamedLogField {
        Time("Time"), Severity("Severity"), ThreadName("Thread"), SourceClassName("SourceClassName"), SourceSimpleClassName(
                "SourceSimpleClassName"), SourceMethodName("SourceMethodName"), SourceLineNumber("SourceLineNumber"), Server(
                "Server"), LoggerName("LoggerName"), Marker("Marker"), NDC("NDC");

        private final String fieldName;

        NamedLogField(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }

        public static NamedLogField byName(String name) {
            for (NamedLogField namedLogField : values()) {
                if (namedLogField.name().equalsIgnoreCase(name)) {
                    return namedLogField;
                }
            }

            return null;
        }
    }

    private String name;
    private NamedLogField namedLogField;

    public LogMessageField(String name, NamedLogField namedLogField) {
        this.namedLogField = namedLogField;
        this.name = name;
    }

    public NamedLogField getNamedLogField() {
        return namedLogField;
    }

    @Override
    public String getName() {
        return name;
    }

    public static List<LogMessageField> getDefaultMapping(NamedLogField... supportedFields) {
        return getDefaultMapping(true, supportedFields);
    }

    public static List<LogMessageField> getDefaultMapping(boolean readFromDefaultsFile, NamedLogField... supportedFields) {

        List<LogMessageField> result = new ArrayList<LogMessageField>();
        List<NamedLogField> supportedLogFields = Arrays.asList(supportedFields);

        if (readFromDefaultsFile) {
            InputStream is = null;

            try {
                is = getStream();

                if (is != null) {
                    Properties p = new Properties();
                    p.load(is);

                    if (!p.isEmpty()) {
                        loadFields(p, result, supportedLogFields);
                    }
                }
            } catch (IOException e) {
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    //
                }
            }
        }

        if (result.isEmpty()) {

            for (NamedLogField namedLogField : NamedLogField.values()) {
                if (supportedLogFields.contains(namedLogField)) {
                    result.add(new LogMessageField(namedLogField.fieldName, namedLogField));
                }
            }
        }

        return result;
    }

    private static void loadFields(Properties p, List<LogMessageField> result, List<NamedLogField> supportedLogFields) {
        for (Map.Entry<Object, Object> entry : p.entrySet()) {

            String targetName = entry.getKey().toString();
            String sourceFieldName = entry.getValue().toString();

            NamedLogField namedLogField = NamedLogField.byName(sourceFieldName);
            if (namedLogField != null && supportedLogFields.contains(namedLogField)) {
                result.add(new LogMessageField(targetName, namedLogField));
            }
        }
    }

    private static InputStream getStream() {

        Thread thread = Thread.currentThread();
        InputStream is = LogMessageField.class.getResourceAsStream(DEFAULT_MAPPING);
        if (is == null && thread.getContextClassLoader() != null) {
            is = thread.getContextClassLoader().getResourceAsStream(DEFAULT_MAPPING);
        }
        return is;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append(" [name='").append(name).append('\'');
        sb.append(", namedLogField=").append(namedLogField);
        sb.append(']');
        return sb.toString();
    }
}
