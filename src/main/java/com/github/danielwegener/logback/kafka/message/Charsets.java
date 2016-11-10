package com.github.danielwegener.logback.kafka.message;

import java.nio.charset.Charset;

/**
 * Created by opetridean on 11/10/16.
 */
class Charsets {

    public final static Charset ASCII = Charset.forName("ASCII");
    public final static Charset UTF8 = Charset.forName("UTF-8");

    public final static byte[] ascii(String input) {
        return input.getBytes(ASCII);
    }

    public final static byte[] utf8(String input) {
        return input.getBytes(UTF8);
    }

}

