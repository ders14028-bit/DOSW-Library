package edu.eci.dosw.tdd.core.util;

public final class ValidationUtil {

    private ValidationUtil() {
    }

    public static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El campo '" + fieldName + "' es obligatorio.");
        }
        return value;
    }
}


