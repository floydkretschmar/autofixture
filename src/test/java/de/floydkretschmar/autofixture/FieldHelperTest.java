package de.floydkretschmar.autofixture;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FieldHelperTest {

    @Test
    @SneakyThrows
    public void setField_whenCalled_setFieldToNewValue() {
        final var instance = new TestClass();

        FieldHelper.setField(instance.getClass().getDeclaredField("field"), instance, "testValue");

        assertThat(instance.field, is("testValue"));
    }

    static class TestClass {

        private final String finalField = "constant";
        private String field;
    }
}
