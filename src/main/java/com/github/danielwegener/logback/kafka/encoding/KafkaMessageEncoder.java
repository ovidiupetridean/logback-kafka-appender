package com.github.danielwegener.logback.kafka.encoding;

/**
 * An Encoder that is able to take an {@code E} and return a {byte[]}.
 * This Encoder should naturally be referential transparent.
 *
 * @param <T> message
 * @since 0.0.1
 */
public interface KafkaMessageEncoder<T> {

    /**
     * Encodes a loggingEvent into a byte array.
     *
     * @param message the loggingEvent to be encoded.
     */
    byte[] doEncode(T message);

}
