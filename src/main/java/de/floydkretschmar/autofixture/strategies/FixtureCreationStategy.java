package de.floydkretschmar.autofixture.strategies;

import java.util.Properties;

public interface FixtureCreationStategy {
    <T> T createFixture(Class<T> fixtureClass, Properties fixtureValues);
}
