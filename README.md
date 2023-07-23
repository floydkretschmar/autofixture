# Autofixture

[![License](https://img.shields.io/badge/License-Apache%202.0-yellowgreen.svg)](https://github.com/floydkretschmar/autofixture/blob/master/LICENSE.txt)
[![Latest Version](https://img.shields.io/maven-metadata/v.svg?label=Latest%20Release&maxAge=3600&metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fde%2Ffloydkretschmar%2Fautofixture%2Fmaven-metadata.xml)](https://central.sonatype.com/artifact/de.floydkretschmar/autofixture/0.0.1)

* [What is autofixture?](#what-is-autofixture)
* [Using autofixture?](#using-autofixture)
* [Requirements](#requirements)
* [Licensing](#licensing)

## What is Autofixture?

autofixture is an extension for JUnit 5 that allows the automatic initialization of fixtures in test classes from
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

for the corresponding `Order` class

```java

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

It is also possible to compose a fixture configuration file from multiple separate configuration files using
the `!include` operator. For example, instead of defining the order configuration in one file as above, it is also
possible to compose the fixture as follows:

order.yaml:

```yaml
orderNo: A001
date: 2019-04-17
customerName: Customer, Joe
orderLines:
  - !include item1.yaml
  - !include item2.yaml
```

item1.yaml:

```yaml
item: No. 9 Sprockets
quantity: 12
unitPrice: 1.23
```

item2.yaml:

```yaml
item: Widget (10mm)
quantity: 4
unitPrice: 3.45
```

The `!include` keyword can be used both for properties like for example `item: !inlcude itemObject.yaml` or for items of
a list as shown in the example above. The specified file name has to be qualified within the resource folder of the
project.

## Using autofixture

Just add the dependency with your build tool of choice, like for example maven:

```maven
<dependency>
    <groupId>de.floydkretschmar</groupId>
    <artifactId>autofixture</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Requirements

Autofixture requires Java 17 or later and JUnit 5.

## Licensing

Autofixture is licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in
compliance with the License. You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.
