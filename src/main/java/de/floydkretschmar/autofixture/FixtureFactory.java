package de.floydkretschmar.autofixture;

import de.floydkretschmar.autofixture.strategies.FixtureCreationStategy;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Properties;

@Builder(toBuilder = true)
@Value
public class FixtureFactory {

    @NonNull
    FixtureCreationStategy defaultFixtureCreationStrategy;

    public <T> T createFixture(Class<T> fixtureClass, Properties fixtureValues) {
        return defaultFixtureCreationStrategy.createFixture(fixtureClass, fixtureValues);
    }
}
