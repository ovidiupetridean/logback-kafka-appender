package com.github.danielwegener.logback.kafka.message;

import java.util.Set;

/**
 * Created by opetridean on 11/10/16.
 */
public class MdcKafkaMessageAssembler extends KafkaMessageAssembler {

    public static final String PROPERTY_MDC_PROFILING = "mdcProfiling";
    public static final String PROPERTY_INCLUDE_FULL_MDC = "includeFullMdc";
    public static final String PROPERTY_MDC_FIELD = "mdcField.";
    public static final String PROPERTY_DYNAMIC_MDC_FIELD = "dynamicMdcFields.";

    private boolean mdcProfiling;
    private boolean includeFullMdc;

    public void initialize(PropertyProvider propertyProvider) {

        super.initialize(propertyProvider);
        mdcProfiling = "true".equalsIgnoreCase(propertyProvider.getProperty(PROPERTY_MDC_PROFILING));
        includeFullMdc = "true".equalsIgnoreCase(propertyProvider.getProperty(PROPERTY_INCLUDE_FULL_MDC));

    }

    public KafkaLogMessage createGelfMessage(LogEvent logEvent) {

        KafkaLogMessage gelfMessage = super.createGelfMessage(logEvent);
        if (mdcProfiling) {
            KafkaLogUtil.addMdcProfiling(logEvent, gelfMessage);
        }

        if (includeFullMdc) {
            Set<String> mdcNames = logEvent.getMdcNames();
            for (String mdcName : mdcNames) {

                if (mdcName == null) {
                    continue;
                }

                String mdcValue = logEvent.getMdcValue(mdcName);
                if (mdcValue != null) {
                    gelfMessage.addField(mdcName, mdcValue);
                }
            }
        }

        return gelfMessage;
    }

    public boolean isMdcProfiling() {
        return mdcProfiling;
    }

    public void setMdcProfiling(boolean mdcProfiling) {
        this.mdcProfiling = mdcProfiling;
    }

    public boolean isIncludeFullMdc() {
        return includeFullMdc;
    }

    public void setIncludeFullMdc(boolean includeFullMdc) {
        this.includeFullMdc = includeFullMdc;
    }


}
