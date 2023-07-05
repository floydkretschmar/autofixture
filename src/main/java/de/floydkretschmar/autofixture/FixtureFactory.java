package de.floydkretschmar.autofixture;

import de.floydkretschmar.autofixture.steps.creation.CreationStrategy;
import de.floydkretschmar.autofixture.utils.FieldHelper;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Builder(toBuilder = true)
@Value
public class FixtureFactory {

    @Singular
    List<CreationStrategy> creationStrategies;

    public <T> T createFixture(Class<T> fixtureClass, Properties fixtureValues) {
        return createFixture(fixtureClass, "", fixtureValues);
    }
    public <T> T createFixture(Class<T> fixtureClass, String qualifiedFieldName, Properties fixtureValues) {
        final T instance = createFixtureInstance(fixtureClass);

        final var declaredFields = fixtureClass.getDeclaredFields();
        for (final var declaredField : declaredFields) {
            final var newQualifiedFieldName = FieldHelper.appendToQualifiedFieldName(qualifiedFieldName, declaredField.getName());
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

    private <T> T createFixtureInstance(Class<T> fixtureClass) {
        if (creationStrategies.isEmpty()) throw new FixtureCreationException("No creation strategies were registered");

        Optional<T> instance = Optional.empty();
        for (final var creationStrategy :
                creationStrategies) {
            instance = creationStrategy.tryCreateInstance(fixtureClass);
            if (instance.isPresent()) break;
        }

        if (instance.isEmpty()) throw new FixtureCreationException("None of the registered creation strategies %s was able to create an instance of %s".formatted(creationStrategies, fixtureClass.getSimpleName()));
        return instance.get();
    }
}
