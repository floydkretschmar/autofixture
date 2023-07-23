# Autofixture

[![License](https://img.shields.io/badge/License-Apache%202.0-yellowgreen.svg)](https://github.com/floydkretschmar/autofixture/LICENSE.txt)

* [What is Autofixture?](#what-is-mapstruct)
* [Requirements](#requirements)
* [Licensing](#licensing)

## What is Autofixture?

Autofixture is an extension for JUnit 5 that allows the automatic initialization of fixtures in test classes from
predefined configuration files using the .yaml format.

For example if you want to test business code using a domain object `Order` you can define the following fixture
configuration file

```yaml
orderNo: A001
date: 2019-04-17
customerName: Customer, Joe
orderLines:
  - item: No. 9 Sprockets
    quantity: 12
    unitPrice: 1.23
  - item: Widget (10mm)
    quantity: 4
    unitPrice: 3.45
```

To automatically initialize an `Order`-Fixture in your unit test, use the `@Autofixture` annotation as follows

```java
import de.floydkretschmar.autofixture.Autofixture;
import de.floydkretschmar.autofixture.AutofixtureExtension;

@ExtendWith(AutofixtureExtension.class)
public interface OrderTest {

  @Autofixture(configurationResourcePath = "fixtures/orderForBusinessLogicTest.yaml")
  private Order orderFixture;

  @Test
  void shouldTestBusinessLogic() {
    // Setup your tests
    
    final Order actualOrder = //... execute your test
    
    assertThat(actualOrder, is(orderFixture));
  }
}
```

All the fields that are tagged with the `@Autofixture` annotation will automatically be initialized as a test fixture.
If no configuration ressource path is specified, the library tries to find a file named like the class of the fixture (
in this case order.yaml) in the base resource path.

## Requirements

Autofixture requires Java 11 or later and JUnit 5.

## Licensing

Autofixture is licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in
compliance with the License. You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.
