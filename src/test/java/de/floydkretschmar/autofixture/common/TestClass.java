package de.floydkretschmar.autofixture.common;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TestClass {

    private boolean testProperty;

    private int testNumberProperty;
}
