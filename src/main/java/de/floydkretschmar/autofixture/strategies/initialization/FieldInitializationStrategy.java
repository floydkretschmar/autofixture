package de.floydkretschmar.autofixture.strategies.initialization;

import de.floydkretschmar.autofixture.strategies.instantiation.InstantiationStrategyRegistry;
import de.floydkretschmar.autofixture.utils.FieldHelper;
import lombok.RequiredArgsConstructor;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Properties;

@RequiredArgsConstructor
public class FieldInitializationStrategy implements InitializationStrategy {
    private final InstantiationStrategyRegistry strategyRegistry;
    @Override
    public <T> void initializeInstance(T instance, Properties instanceValues) {
        initializeInstance(instance, "", instanceValues);
    }

    private <T> void initializeInstance(T instance, String qualifiedFieldName, Properties instanceValues) {
        final var declaredFields = instance.getClass().getDeclaredFields();
        for (final var declaredField : declaredFields) {
            final var newQualifiedFieldName = FieldHelper.appendToQualifiedFieldName(qualifiedFieldName, declaredField.getName());
            if (declaredField.getType().isPrimitive() || declaredField.getType() == String.class) {
                final var valueAsString = instanceValues.getProperty(newQualifiedFieldName);

                if (valueAsString != null) {
                    PropertyEditor editor = PropertyEditorManager.findEditor(declaredField.getType());
                    editor.setAsText(valueAsString);
                    FieldHelper.setField(declaredField, instance, editor.getValue());
                }
            }
            else {
                final var fieldType = declaredField.getType();
                final var valueOptional = strategyRegistry.createInstance(fieldType);

                initializeInstance(valueOptional, newQualifiedFieldName, instanceValues);
                FieldHelper.setField(declaredField, instance, valueOptional);
            }
        }
    }
}
