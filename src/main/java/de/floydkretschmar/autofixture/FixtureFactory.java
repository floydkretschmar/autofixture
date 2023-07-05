package de.floydkretschmar.autofixture;

import de.floydkretschmar.autofixture.utils.FieldHelper;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public final class FixtureFactory {

    public <T> T createFixture(Class<T> fixtureClass, Properties fixtureValues) {
        return createFixture(fixtureClass, "", fixtureValues);
    }
    public <T> T createFixture(Class<T> fixtureClass, String qualifiedFieldName, Properties fixtureValues) {
        final var declaredFields = fixtureClass.getDeclaredFields();
        final T instance = createInstanceWithBuilder(fixtureClass);

        for (final var declaredField : declaredFields) {
            final var newQualifiedFieldName = qualifiedFieldName.equals("") ? declaredField.getName() : "%s.%s".formatted(qualifiedFieldName, declaredField.getName());
            if (declaredField.getType().isPrimitive() || declaredField.getType() == String.class) {
                final var valueAsString = fixtureValues.getProperty(newQualifiedFieldName);

                if (valueAsString != null) {
                    PropertyEditor editor = PropertyEditorManager.findEditor(declaredField.getType());
                    editor.setAsText(valueAsString);
                    FieldHelper.setField(declaredField, instance, editor.getValue());
                }
            }
            else {
                final var value = createFixture(declaredField.getType(), newQualifiedFieldName, fixtureValues);
                FieldHelper.setField(declaredField, instance, value);
            }
        }

        return instance;
    }

    private static <T> T createInstanceWithBuilder(Class<T> fixtureClass) {
        try {
            final var builderMethod = fixtureClass.getDeclaredMethod("builder");
            final var builder = builderMethod.invoke(null);
            final var buildMethod = builder.getClass().getDeclaredMethod("build");
            final var instance = buildMethod.invoke(builder);
            return (T)instance;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
            throw new FixtureCreationException("Unable to build fixture for class %s using the builder pattern.".formatted(fixtureClass.getSimpleName()), exception);
        }
    }
}
