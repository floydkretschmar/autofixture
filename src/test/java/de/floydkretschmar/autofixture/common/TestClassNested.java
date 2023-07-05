package de.floydkretschmar.autofixture.common;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TestClassNested {

    private String stringProperty;

    private TestClass nestedProperty;
}
