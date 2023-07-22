package de.floydkretschmar.autofixture;

import java.lang.reflect.Field;

public class FieldHelper {

    public static void setField(Field field, Object instance, Object value) throws IllegalAccessException {
        final var isFieldAccessible = field.canAccess(instance);
        field.setAccessible(true);
        field.set(instance, value);
        field.setAccessible(isFieldAccessible);
    }
}
