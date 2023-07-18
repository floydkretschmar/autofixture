//package de.floydkretschmar.autofixture.junit;
//
//import de.floydkretschmar.autofixture.Autofixture;
//import de.floydkretschmar.autofixture.common.TestClass;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.api.extension.ExtensionContext;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
//
//@ExtendWith(MockitoExtension.class)
//public class AutofixtureExtensionTest {
//    @Mock
//    private ExtensionContext extensionContext;
//
//    @Test
//    public void postProcessTestInstance_WhenTestInstanceHasAutofixtureAnnotations_shouldInitializeFixtures() {
//        final var initializer = AutofixtureExtension.builder().build();
//        final var instance = new TestInstance();
//
//        initializer.postProcessTestInstance(instance, extensionContext);
//
//        assertThat(instance.testFixture, notNullValue());
//        assertThat(instance.testFixture.isTestProperty(), is(true));
//        assertThat(instance.testFixture.getTestNumberProperty(), is(0));
//    }
//
//    @Test
//    public void postProcessTestInstance_WhenTestInstanceHasNoAutofixtureAnnotations_shouldDoNothing() {
//        final var initializer = AutofixtureExtension.builder().build();
//        final var instance = new TestInstanceWithoutAnnotation();
//
//        initializer.postProcessTestInstance(instance, extensionContext);
//
//        assertThat(instance.testFixture, nullValue());
//    }
//
//    static class TestInstance {
//
//        @Autofixture
//        private TestClass testFixture;
//    }
//
//    static class TestInstanceWithoutAnnotation {
//
//        private TestClass testFixture;
//    }
//}
