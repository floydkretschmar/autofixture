package de.floydkretschmar.autofixture;

import de.floydkretschmar.autofixture.exceptions.FixtureCreationException;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AutofixtureExtensionTest {
    @Mock
    private ExtensionContext extensionContext;

    @Test
    public void postProcessTestInstance_WhenTestInstanceHasAutofixtureAnnotations_shouldInitializeFixtures() {
        final var initializer = new AutofixtureExtension();
        final var instance = new TestInstance();

        initializer.postProcessTestInstance(instance, extensionContext);

        assertThat(instance.orderFixture, notNullValue());
        assertThat(instance.orderFixture.getOrderNo(), is("A001"));
        assertThat(instance.orderFixture.getDate(), is(LocalDate.of(2019, 4, 17)));
        assertThat(instance.orderFixture.getCustomerName(), is("Customer, Joe"));
        assertThat(instance.orderFixture.getOrderLines(), hasSize(2));
        assertThat(instance.orderFixture.getOrderLines(), hasItem(OrderLine.builder().item("No. 9 Sprockets").quantity(12).unitPrice(BigDecimal.valueOf(1.23)).build()));
        assertThat(instance.orderFixture.getOrderLines(), hasItem(OrderLine.builder().item("Widget (10mm)").quantity(4).unitPrice(BigDecimal.valueOf(3.45)).build()));
    }

    @Test
    public void postProcessTestInstance_WhenTestInstanceHasNoAutofixtureAnnotations_shouldDoNothing() {
        final var initializer = new AutofixtureExtension();
        final var instance = new TestInstanceWithoutAnnotation();

        initializer.postProcessTestInstance(instance, extensionContext);

        assertThat(instance.orderFixture, nullValue());
    }

    @Test
    public void postProcessTestInstance_WhenTestInstanceHasAnnotationOnInvalidField_shouldDoThrowFixtureCreationException() {
        final var initializer = new AutofixtureExtension();
        final var instance = new TestInvalidInstance();

        final var exception = assertThrows(FixtureCreationException.class, () -> initializer.postProcessTestInstance(instance, extensionContext));
        assertThat(exception.getMessage(), containsString("because the instance of autofixture could not be assigned to the field."));
    }

    @Test
    public void postProcessTestInstance_WhenConfigIsInvalidYaml_shouldDoThrowFixtureCreationException() {
        final var initializer = new AutofixtureExtension();
        final var instance = new TestInvalidConfig();

        final var exception = assertThrows(FixtureCreationException.class, () -> initializer.postProcessTestInstance(instance, extensionContext));
        assertThat(exception.getMessage(), containsString("because configuration file"));
        assertThat(exception.getMessage(), containsString("could not be parsed."));
    }

    static class TestInstance {

        @Autofixture
        private Order orderFixture;
    }

    static class TestInstanceWithoutAnnotation {

        private Order orderFixture;
    }

    static class TestInvalidInstance {
        @Autofixture
        private final static Order invalidAutoFixture = Order.builder().build();
    }

    static class TestInvalidConfig {

        @Autofixture
        private FixtureClassWithInvalidConfig fixture;
    }

    static class FixtureClassWithInvalidConfig {

    }

    @Builder
    @Value
    @Jacksonized
    static class Order {
        String orderNo;
        LocalDate date;
        String customerName;
        List<OrderLine> orderLines;
    }

    @Builder
    @Value
    @Jacksonized
    static class OrderLine {
        String item;
        int quantity;
        BigDecimal unitPrice;
    }
}
