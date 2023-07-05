package de.floydkretschmar.autofixture.utils;

import java.lang.reflect.Field;

public class FieldHelper {

    public static void setField(Field field, Object instance, Object value) {
        try {
            final var isFieldAccessible = field.canAccess(instance);
            field.setAccessible(true);
            field.set(instance, value);
            field.setAccessible(isFieldAccessible);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("The value for field %s could not be set to value %s".formatted(field.getType().getSimpleName(), value), e);
        }
    }
}
