package com.github.danielwegener.logback.kafka.message;

public class ConfigurationSupport {
    public static final String MULTI_VALUE_DELIMITTER = ",";
    public static final char EQ = '=';

    private ConfigurationSupport() {

    }

    /**
     * Set the additional (static) fields.
     *
     * @param spec                  field=value,field1=value1, ...
     * @param kafkaMessageAssembler the Gelf message assembler to apply the configuration
     */
    public static void setAdditionalFields(String spec, KafkaMessageAssembler kafkaMessageAssembler) {
        if (null != spec) {
            String[] properties = spec.split(MULTI_VALUE_DELIMITTER);

            for (String field : properties) {
                final int index = field.indexOf(EQ);
                if (-1 == index) {
                    continue;
                }
                kafkaMessageAssembler.addField(new StaticMessageField(field.substring(0, index), field.substring(index + 1)));
            }
        }
    }

    /**
     * Set the MDC fields.
     *
     * @param spec                  field, field2, field3
     * @param kafkaMessageAssembler the Gelf message assembler to apply the configuration
     */
    public static void setMdcFields(String spec, KafkaMessageAssembler kafkaMessageAssembler) {
        if (null != spec) {
            String[] fields = spec.split(MULTI_VALUE_DELIMITTER);

            for (String field : fields) {
                kafkaMessageAssembler.addField(new MdcMessageField(field.trim(), field.trim()));
            }
        }
    }

    /**
     * Set the dynamic MDC fields.
     *
     * @param spec                  field, .*FieldSuffix, fieldPrefix.*
     * @param kafkaMessageAssembler
     */
    public static void setDynamicMdcFields(String spec, KafkaMessageAssembler kafkaMessageAssembler) {
        if (null != spec) {
            String[] fields = spec.split(MULTI_VALUE_DELIMITTER);

            for (String field : fields) {
                kafkaMessageAssembler.addField(new DynamicMdcMessageField(field.trim()));
            }
        }
    }

    /**
     * Set the additional field types.
     *
     * @param spec                  field=String,field1=Double, ... See {@link KafkaLogMessage} for supported types.
     * @param kafkaMessageAssembler the Gelf message assembler to apply the configuration
     */
    public static void setAdditionalFieldTypes(String spec, KafkaMessageAssembler kafkaMessageAssembler) {
        if (null != spec) {
            String[] properties = spec.split(MULTI_VALUE_DELIMITTER);

            for (String field : properties) {
                final int index = field.indexOf(EQ);
                if (-1 != index) {
                    kafkaMessageAssembler.setAdditionalFieldType(field.substring(0, index), field.substring(index + 1));
                }
            }
        }
    }

}