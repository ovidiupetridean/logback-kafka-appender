package com.github.danielwegener.logback.kafka.message;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by opetridean on 11/10/16.
 */
public class KafkaMessageAssembler {
    public static final String FIELD_MESSAGE_PARAM = "MessageParam";
    public static final String FIELD_STACK_TRACE = "StackTrace";

    private String facility;
    private boolean extractStackTrace;
    private boolean filterStackTrace;

    private List<MessageField> fields = new ArrayList<MessageField>();
    private Map<String, String> additionalFieldTypes = new HashMap<String, String>();

    private String timestampPattern = "yyyy-MM-dd HH:mm:ss,SSSS";


    public void initialize(PropertyProvider propertyProvider) {

        extractStackTrace = "true".equalsIgnoreCase(propertyProvider.getProperty(PropertyProvider.PROPERTY_EXTRACT_STACKTRACE));
        filterStackTrace = "true".equalsIgnoreCase(propertyProvider.getProperty(PropertyProvider.PROPERTY_FILTER_STACK_TRACE));

        setupStaticFields(propertyProvider);
        setupAdditionalFieldTypes(propertyProvider);
        facility = propertyProvider.getProperty(PropertyProvider.PROPERTY_FACILITY);
    }

    public KafkaLogMessage createGelfMessage(LogEvent logEvent) {

        KafkaLogMessageBuilder builder = KafkaLogMessageBuilder.newInstance();

        Throwable throwable = logEvent.getThrowable();
        String message = logEvent.getMessage();

        if (KafkaLogMessage.isEmpty(message) && throwable != null) {
            message = throwable.toString();
        }

        builder.withFullMessage(message).withJavaTimestamp(logEvent.getLogTimestamp());
        builder.withLevel(logEvent.getSyslogLevel());
        builder.withAdditionalFieldTypes(additionalFieldTypes);

        for (MessageField field : fields) {
            Values values = getValues(logEvent, field);
            if (values == null || !values.hasValues()) {
                continue;
            }

            for (String entryName : values.getEntryNames()) {
                String value = values.getValue(entryName);
                if (value == null) {
                    continue;

                }
                builder.withField(entryName, value);
            }
        }

        if (extractStackTrace && throwable != null) {
            addStackTrace(throwable, builder);
        }

        if (logEvent.getParameters() != null) {
            for (int i = 0; i < logEvent.getParameters().length; i++) {
                Object param = logEvent.getParameters()[i];
                builder.withField(FIELD_MESSAGE_PARAM + i, "" + param);
            }
        }

        if (null != facility) {
            builder.withFacility(facility);
        }

        return builder.build();
    }

    private Values getValues(LogEvent logEvent, MessageField field) {

        if (field instanceof StaticMessageField) {
            return new Values(field.getName(), getValue((StaticMessageField) field));
        }

        if (field instanceof LogMessageField) {
            LogMessageField logMessageField = (LogMessageField) field;
            if (logMessageField.getNamedLogField() == LogMessageField.NamedLogField.Time) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(timestampPattern);
                return new Values(field.getName(), dateFormat.format(new Date(logEvent.getLogTimestamp())));
            }
        }

        return logEvent.getValues(field);
    }

    private String getValue(StaticMessageField field) {
        return field.getValue();
    }

    private void addStackTrace(Throwable thrown, KafkaLogMessageBuilder builder) {
        if (filterStackTrace) {
            builder.withField(FIELD_STACK_TRACE, StackTraceFilter.getFilteredStackTrace(thrown));
        } else {
            final StringWriter sw = new StringWriter();
            thrown.printStackTrace(new PrintWriter(sw));
            builder.withField(FIELD_STACK_TRACE, sw.toString());
        }
    }

    private void setupStaticFields(PropertyProvider propertyProvider) {
        int fieldNumber = 0;
        while (true) {
            final String property = propertyProvider.getProperty(PropertyProvider.PROPERTY_ADDITIONAL_FIELD + fieldNumber);
            if (null == property) {
                break;
            }
            final int index = property.indexOf('=');
            if (-1 != index) {

                StaticMessageField field = new StaticMessageField(property.substring(0, index), property.substring(index + 1));
                addField(field);
            }

            fieldNumber++;
        }
    }

    private void setupAdditionalFieldTypes(PropertyProvider propertyProvider) {
        int fieldNumber = 0;
        while (true) {
            final String property = propertyProvider.getProperty(PropertyProvider.PROPERTY_ADDITIONAL_FIELD_TYPE + fieldNumber);
            if (null == property) {
                break;
            }
            final int index = property.indexOf('=');
            if (-1 != index) {

                String field = property.substring(0, index);
                String type = property.substring(index + 1);
                setAdditionalFieldType(field, type);
            }

            fieldNumber++;
        }
    }

    public void setAdditionalFieldType(String field, String type) {
        additionalFieldTypes.put(field, type);
    }

    public void addField(MessageField field) {
        if (!fields.contains(field)) {
            this.fields.add(field);
        }
    }

    public void addFields(Collection<? extends MessageField> fields) {
        this.fields.addAll(fields);
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public boolean isExtractStackTrace() {
        return extractStackTrace;
    }

    public void setExtractStackTrace(boolean extractStackTrace) {
        this.extractStackTrace = extractStackTrace;
    }

    public boolean isFilterStackTrace() {
        return filterStackTrace;
    }

    public void setFilterStackTrace(boolean filterStackTrace) {
        this.filterStackTrace = filterStackTrace;
    }

    public String getTimestampPattern() {
        return timestampPattern;
    }

    public void setTimestampPattern(String timestampPattern) {
        this.timestampPattern = timestampPattern;
    }

}
