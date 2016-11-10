package com.github.danielwegener.logback.kafka.message;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * @author Ovidiu Petridean
 * @author petridean.ovidiu@gmail.com
 *         <p>
 *         Similar to Gelf formatter
 */
public class KafkaLogMessage {

    public static final String FIELD_SHORT_MESSAGE = "short_message";
    public static final String FIELD_FULL_MESSAGE = "full_message";
    public static final String FIELD_TIMESTAMP = "timestamp";
    public static final String FIELD_LEVEL = "level";
    public static final String FIELD_FACILITY = "facility";
    public static final String ID_NAME = "id";
    public static final int DEFAUL_LEVEL = 7;
    /**
     * Discover the field type by trying to parse it.
     */
    public static final String FIELD_TYPE_DISCOVER = "discover";

    /**
     * String field type.
     */
    public static final String FIELD_TYPE_STRING = "String";

    /**
     * long field type. Zero if value cannot be converted.
     */
    public static final String FIELD_TYPE_LONG = "long";

    /**
     * Long field type. Null if value cannot be converted.
     */
    public static final String FIELD_TYPE_LONG2 = "Long";

    /**
     * double field type. Zero if value cannot be converted.
     */
    public static final String FIELD_TYPE_DOUBLE = "double";

    /**
     * Double field type. Null if value cannot be converted.
     */
    public static final String FIELD_TYPE_DOUBLE2 = "Double";

    /*
     * Default field type: Discover
     */
    public static final String FIELD_TYPE_DEFAULT = FIELD_TYPE_DISCOVER;
    public static final String DEFAULT_FACILITY = "<override-this>";
    private static final BigDecimal TIME_DIVISOR = new BigDecimal(1000);

    private byte[] hostBytes = lastFourAsciiBytes("none");
    private String shortMessage;
    private String fullMessage;
    private long javaTimestamp;
    private String level;
    private String facility = DEFAULT_FACILITY;
    private Map<String, String> additonalFields = new HashMap<String, String>();
    private Map<String, String> additionalFieldTypes = new HashMap<String, String>();
    Gson gson = new Gson();


    public KafkaLogMessage() {
    }

    public KafkaLogMessage(String shortMessage, String fullMessage, long timestamp, String level) {
        this.shortMessage = shortMessage;
        this.fullMessage = fullMessage;
        this.javaTimestamp = timestamp;
        this.level = level;
    }


    public String toJson(String additionalFieldPrefix) {
        Map<String, Object> map = new HashMap<String, Object>();

        if (!isEmpty(shortMessage)) {
            map.put(FIELD_SHORT_MESSAGE, getShortMessage());
        }

        if (!isEmpty(getFullMessage())) {
            map.put(FIELD_FULL_MESSAGE, getFullMessage());
        }

        if (getJavaTimestamp() != 0) {
            map.put(FIELD_TIMESTAMP, getTimestamp());
        }

        if (!isEmpty(getLevel())) {
            map.put(FIELD_LEVEL, getLevel());
        }

        if (!isEmpty(getFacility())) {
            map.put(FIELD_FACILITY, getFacility());
        }

        for (Map.Entry<String, String> additionalField : additonalFields.entrySet()) {
            if (!ID_NAME.equals(additionalField.getKey()) && additionalField.getValue() != null) {
                String value = additionalField.getValue();
                String fieldType = additionalFieldTypes.get(additionalField.getKey());
                if (fieldType == null) {
                    fieldType = FIELD_TYPE_DEFAULT;
                }
                Object result = getAdditionalFieldValue(value, fieldType);
                if (result != null) {
                    map.put(additionalFieldPrefix + additionalField.getKey(), result);
                }
            }
        }

        return gson.toJson(map);
    }

    /**
     * Get the field value as requested data type.
     *
     * @param value     the value as string
     * @param fieldType see field types
     * @return the field value in the appropriate data type or {@literal null}.
     */
    protected Object getAdditionalFieldValue(String value, String fieldType) {

        Object result = null;
        if (fieldType.equalsIgnoreCase(FIELD_TYPE_DISCOVER)) {
            try {
                try {
                    // try adding the value as a long
                    result = Long.parseLong(value);
                } catch (NumberFormatException ex) {
                    // fallback on the double value
                    result = Double.parseDouble(value);
                }
            } catch (NumberFormatException ex) {
                // fallback on the string value
                result = value;
            }
        }

        if (fieldType.equalsIgnoreCase(FIELD_TYPE_STRING)) {
            result = value;
        }

        if (fieldType.equals(FIELD_TYPE_DOUBLE) || fieldType.equalsIgnoreCase(FIELD_TYPE_DOUBLE2)) {
            try {
                result = Double.parseDouble(value);
            } catch (NumberFormatException ex) {
                if (fieldType.equals(FIELD_TYPE_DOUBLE)) {
                    result = Double.valueOf(0);
                }
            }
        }

        if (fieldType.equals(FIELD_TYPE_LONG) || fieldType.equalsIgnoreCase(FIELD_TYPE_LONG2)) {
            try {
                result = (long) Double.parseDouble(value);
            } catch (NumberFormatException ex) {
                if (fieldType.equals(FIELD_TYPE_LONG)) {
                    result = Long.valueOf(0);
                }
            }
        }

        return result;
    }

    /**
     * Get bytes of the gzipped messages
     *
     * @param message
     * @return
     */
    private byte[] gzipMessage(String message) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (GZIPOutputStream stream = new GZIPOutputStream(bos)) {
            byte[] bytes = Charsets.utf8(message);
            stream.write(bytes);
            stream.finish();
            byte[] zipped = bos.toByteArray();
            return zipped;
        } catch (IOException e) {
            return null;
        }
    }

    public int getCurrentMillis() {
        return (int) System.currentTimeMillis();
    }


    private byte[] lastFourAsciiBytes(String host) {
        final String shortHost = host.length() >= 4 ? host.substring(host.length() - 4) : host;
        return Charsets.ascii(shortHost);
    }

    public String getShortMessage() {
        return !isEmpty(shortMessage) ? shortMessage : "<empty>";
    }

    public void setShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
    }

    public String getFullMessage() {
        return fullMessage;
    }

    public void setFullMessage(String fullMessage) {
        this.fullMessage = fullMessage;
    }

    public BigDecimal getTimestampAsBigDecimal() {
        return new BigDecimal(javaTimestamp).divide(TIME_DIVISOR);
    }

    public String getTimestamp() {
        return getTimestampAsBigDecimal().toPlainString();
    }

    public Long getJavaTimestamp() {
        return javaTimestamp;
    }

    public void setJavaTimestamp(long javaTimestamp) {
        this.javaTimestamp = javaTimestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public Map<String, String> getAdditionalFieldTypes() {
        return additionalFieldTypes;
    }

    public void setAdditionalFieldTypes(Map<String, String> additionalFieldTypes) {
        this.additionalFieldTypes = additionalFieldTypes;
    }

    /**
     * Add multiple fields (key/value pairs)
     *
     * @param fields map of fields
     * @return the current KafkaLogMessage.
     */
    public KafkaLogMessage addFields(Map<String, String> fields) {

        if (fields == null) {
            throw new IllegalArgumentException("fields is null");
        }
        getAdditonalFields().putAll(fields);
        return this;
    }

    /**
     * Add a particular field.
     *
     * @param key   the key
     * @param value the value
     * @return the current KafkaLogMessage.
     */
    public KafkaLogMessage addField(String key, String value) {
        getAdditonalFields().put(key, value);
        return this;
    }

    public Map<String, String> getAdditonalFields() {
        return additonalFields;
    }

    public boolean isValid() {
        return isShortOrFullMessagesExists();
    }

    private boolean isShortOrFullMessagesExists() {
        return !isEmpty(shortMessage) || !isEmpty(fullMessage);
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    private byte[] concatByteArray(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public String getField(String fieldName) {
        return getAdditonalFields().get(fieldName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KafkaLogMessage)) {
            return false;
        }

        KafkaLogMessage that = (KafkaLogMessage) o;

        if (javaTimestamp != that.javaTimestamp) {
            return false;
        }
        if (additonalFields != null ? !additonalFields.equals(that.additonalFields) : that.additonalFields != null) {
            return false;
        }
        if (facility != null ? !facility.equals(that.facility) : that.facility != null) {
            return false;
        }
        if (fullMessage != null ? !fullMessage.equals(that.fullMessage) : that.fullMessage != null) {
            return false;
        }
        if (!Arrays.equals(hostBytes, that.hostBytes)) {
            return false;
        }
        if (level != null ? !level.equals(that.level) : that.level != null) {
            return false;
        }
        if (shortMessage != null ? !shortMessage.equals(that.shortMessage) : that.shortMessage != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = hostBytes != null ? Arrays.hashCode(hostBytes) : 0;
        result = 31 * result + (shortMessage != null ? shortMessage.hashCode() : 0);
        result = 31 * result + (fullMessage != null ? fullMessage.hashCode() : 0);
        result = 31 * result + (int) (javaTimestamp ^ (javaTimestamp >>> 32));
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (facility != null ? facility.hashCode() : 0);
        result = 31 * result + (additonalFields != null ? additonalFields.hashCode() : 0);
        return result;
    }
}
