package com.github.danielwegener.logback.kafka.encoding;

import ch.qos.logback.core.Layout;

import java.nio.charset.Charset;

/**
 * A KafkaMessageEncoder that can be configured with a {@link Layout} and a {@link Charset} and creates
 * a serialized string for each event using the given layout.
 *
 * @since 0.1.0
 */
public class LayoutKafkaMessageEncoder<T> extends KafkaMessageEncoderBase<T> {

    public LayoutKafkaMessageEncoder() {
    }

    public LayoutKafkaMessageEncoder(Layout<T> layout, Charset charset) {
        this.layout = layout;
        this.charset = charset;
    }

    private Layout<T> layout;
    private Charset charset;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public void start() {
        if (charset == null) {
            addInfo("No charset specified for PatternLayoutKafkaEncoder. Using default UTF8 encoding.");
            charset = UTF8;
        }
        super.start();
    }

    @Override
    public byte[] doEncode(T message) {
        byte[] messageBytes = new byte[0];

        if (message instanceof String) {
            messageBytes = ((String) message).getBytes(charset);
        }
        return messageBytes;
    }

    public void setLayout(Layout<T> layout) {
        this.layout = layout;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Layout<T> getLayout() {
        return layout;
    }

    public Charset getCharset() {
        return charset;
    }
}
