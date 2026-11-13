package com.joyasmic.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/** Lee db.properties (src/main/resources) una sola vez. */
public final class Config {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("No se encontró db.properties en el classpath");
            }
            PROPS.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Config() {}

    public static String get(String clave) {
        return PROPS.getProperty(clave, "").trim();
    }
}
