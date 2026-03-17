package edu.eci.dosw.tdd.core.util;

import java.util.UUID;

public final class IdGeneratorUtil {

    private IdGeneratorUtil() {
    }

    public static String randomId() {
        return UUID.randomUUID().toString();
    }
}

