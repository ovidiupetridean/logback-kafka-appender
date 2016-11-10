package com.github.danielwegener.logback.kafka.message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by opetridean on 11/10/16.
 */
public class KafkaLogMessageBuilder {

    private String shortMessage;
    private String fullMessage;
    private long javaTimestamp;
    private String level;
    private String facility = KafkaLogMessage.DEFAULT_FACILITY;
    private Map<String, String> additonalFields = new HashMap<String, String>();
    private Map<String, String> additionalFieldTypes = new HashMap<String, String>();

    private KafkaLogMessageBuilder() {

    }

    /**
     * Creates a new instance of the KafkaMessageBuilder.
     *
     * @return KafkaMessageBuilder
     */
    public static KafkaLogMessageBuilder newInstance() {
        return new KafkaLogMessageBuilder();
    }

    /**
     * Set the short_message.
     *
     * @param shortMessage the short_message
     * @return KafkaMessageBuilder
     */
    public KafkaLogMessageBuilder withShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
        return this;
    }

    /**
     * Set the full_message.
     *
     * @param fullMessage the fullMessage
     * @return KafkaMessageBuilder
     */
    public KafkaLogMessageBuilder withFullMessage(String fullMessage) {
        this.fullMessage = fullMessage;
        return this;
    }

    /**
     * Set the level (severity).
     *
     * @param level the level
     * @return KafkaMessageBuilder
     */
    public KafkaLogMessageBuilder withLevel(String level) {
        this.level = level;
        return this;
    }

    /**
     * Set the facility.
     *
     * @param facility the facility
     * @return KafkaMessageBuilder
     */
    public KafkaLogMessageBuilder withFacility(String facility) {
        this.facility = facility;
        return this;
    }

    /**
     * Set the java timestamp (millis).
     *
     * @param javaTimestamp the javaTimestamp
     * @return KafkaMessageBuilder
     */
    public KafkaLogMessageBuilder withJavaTimestamp(long javaTimestamp) {
        this.javaTimestamp = javaTimestamp;
        return this;
    }

    /**
     * Add additional fields.
     *
     * @param additonalFields the additonalFields
     * @return KafkaMessageBuilder
     */
    public KafkaLogMessageBuilder withFields(Map<String, String> additonalFields) {
        this.additonalFields.putAll(additonalFields);
        return this;
    }

    /**
     * Add an additional field.
     *
     * @param key the key
     * @param value the value
     * @return KafkaMessageBuilder
     */
    public KafkaLogMessageBuilder withField(String key, String value) {
        this.additonalFields.put(key, value);
        return this;
    }

    /**
     * Set additional field types
     *
     * @param additionalFieldTypes the type map
     * @return KafkaMessageBuilder
     */
    public KafkaLogMessageBuilder withAdditionalFieldTypes(Map<String, String> additionalFieldTypes) {
        this.additionalFieldTypes.putAll(additionalFieldTypes);
        return this;
    }

    /**
     * Build a new Gelf message based on the builder settings.
     *
     * @return GelfMessage
     */
    public KafkaLogMessage build() {

        KafkaLogMessage gelfMessage = new KafkaLogMessage(shortMessage, fullMessage, javaTimestamp, level);
        gelfMessage.addFields(additonalFields);
        gelfMessage.setJavaTimestamp(javaTimestamp);
        gelfMessage.setFacility(facility);
        gelfMessage.setFacility(facility);
        gelfMessage.setAdditionalFieldTypes(additionalFieldTypes);

        return gelfMessage;
    }
}
