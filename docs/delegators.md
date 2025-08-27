# Delegators

<!-- toc -->

- [Basics](#basics)
- [Delegate to other classes](#delegate-to-other-classes)
- [Build a library](#build-a-library)
- [Recursive Types](#recursive-types)

<!-- tocstop -->

## Basics

Scruse will delegate to other suitable `@JsonInput` and `@JsonOutput` methods whenever possible.
This is very important for keeping the generated code small.
Take the following example:

```java
// ../scruse-tests/scruse-tests-jackson/src/main/java/org/tillerino/scruse/tests/base/features/DelegationSerde.java#L23-L27

@JsonInput
ScalarFieldsRecord deserializeSingle(JsonParser parser) throws Exception;

@JsonInput
List<ScalarFieldsRecord> deserializeList(JsonParser parser) throws Exception;
```

`deserializeList` will then refer to `deserializeSingle` instead of repeating the entire deserialization of `ScalarFieldsRecord`.
```java
// ../scruse-tests/scruse-tests-jackson/target/generated-sources/annotations/org/tillerino/scruse/tests/base/features/DelegationSerde$SimpleDelegationSerdeImpl.java#L326-L343

@Override
public List<ScalarFieldsRecord> deserializeList(JsonParser parser) throws Exception {
  if (!parser.hasCurrentToken()) {
    parser.nextToken();
  }
  if (nextIfCurrentTokenIs(parser, VALUE_NULL)) {
    return null;
  } else if (parser.currentToken() == START_ARRAY) {
    parser.nextToken();
    List<ScalarFieldsRecord> container$1 = new ArrayList<>();
    while (!nextIfCurrentTokenIs(parser, END_ARRAY)) {
      container$1.add(this.deserializeSingle(parser));
    }
    return container$1;
  } else {
    throw new IOException("Expected array, got " + parser.currentToken() + " at " + parser.getCurrentLocation());
  }
}
```

## Delegate to other classes

To organize your methods, you can use the `uses` attribute of the `@JsonConfig` annotation:
```java
// ../scruse-tests/scruse-tests-jackson/src/main/java/org/tillerino/scruse/tests/base/PrimitiveScalarsSerde.java#L8-L10

public interface PrimitiveScalarsSerde {
    @JsonOutput
    void writePrimitiveBooleanX(boolean b, JsonGenerator generator) throws Exception;
```

```java
// ../scruse-tests/scruse-tests-jackson/src/main/java/org/tillerino/scruse/tests/base/features/DelegationSerde.java#L61-L64

@JsonConfig(uses = PrimitiveScalarsSerde.class)
interface BoxedScalarsSerde {
    @JsonOutput
    void writeBoxedBooleanX(Boolean b, JsonGenerator generator) throws Exception;
```

The implementation of `BoxedScalarsSerde` will then call instantiate and call the first serializer:

```java
// ../scruse-tests/scruse-tests-jackson/target/generated-sources/annotations/org/tillerino/scruse/tests/base/features/DelegationSerde$BoxedScalarsSerdeImpl.java#L23-L33

public class DelegationSerde$BoxedScalarsSerdeImpl implements DelegationSerde.BoxedScalarsSerde {
  PrimitiveScalarsSerde primitiveScalarsSerde$0$delegate = new PrimitiveScalarsSerdeImpl();

  @Override
  public void writeBoxedBooleanX(Boolean b, JsonGenerator generator) throws Exception {
    if (b != null) {
      primitiveScalarsSerde$0$delegate.writePrimitiveBooleanX(b, generator);
    } else {
      generator.writeNull();
    }
  }
```

## Build a library

To keep the generated code small and readable, it is recommended to build a library of reusable serializers down to primitives.
Even something as simple as reading a `Float[]` generates a lot of code:

```java
// ../scruse-tests/scruse-tests-jackson/target/generated-sources/annotations/org/tillerino/scruse/tests/base/ScalarArraysSerdeImpl.java#L717-L759

public Float[] readBoxedFloatArray(JsonParser parser) throws Exception {
  if (!parser.hasCurrentToken()) {
    parser.nextToken();
  }
  if (nextIfCurrentTokenIs(parser, VALUE_NULL)) {
    return null;
  } else if (parser.currentToken() == START_ARRAY) {
    parser.nextToken();
    // Like ArrayList
    Object[] array$1 = EmptyArrays.EMPTY_OBJECT_ARRAY;
    int len$1 = 0;
    while (!nextIfCurrentTokenIs(parser, END_ARRAY)) {
      if (len$1 == array$1.length) {
        // simplified version of ArrayList growth
        array$1 = Arrays.copyOf(array$1, Math.max(10, array$1.length + (array$1.length >> 1)));
      }
      if (nextIfCurrentTokenIs(parser, VALUE_NULL)) {
        array$1[len$1++] = null;
      } else if (parser.currentToken() == VALUE_STRING) {
        String string$4;
        string$4 = parser.getText();
        parser.nextToken();
        if (string$4.equals("NaN")) {
          array$1[len$1++] = Float.NaN;
        } else if (string$4.equals("Infinity")) {
          array$1[len$1++] = Float.POSITIVE_INFINITY;
        } else if (string$4.equals("-Infinity")) {
          array$1[len$1++] = Float.NEGATIVE_INFINITY;
        } else {
          throw new IOException();
        }
      } else if (parser.currentToken().isNumeric()) {
        array$1[len$1++] = parser.getFloatValue();
        parser.nextToken();
      } else {
        throw new IOException("Expected number, got " + parser.currentToken() + " at " + parser.getCurrentLocation());
      }
    }
    return Arrays.copyOf(array$1, len$1, Float[].class);
  } else {
    throw new IOException("Expected array, got " + parser.currentToken() + " at " + parser.getCurrentLocation());
  }
}
```

It is recommended to build a library of central serializers for primitives, boxed primitives, other scalars like `String`,
common arrays, e.g. `double[]`, etc. This is how you would go about that:

First, define primitive serializers:
```java
// ../scruse-tests/scruse-tests-jackson/src/main/java/org/tillerino/scruse/tests/base/PrimitiveScalarsSerde.java#L8-L19

public interface PrimitiveScalarsSerde {
    @JsonOutput
    void writePrimitiveBooleanX(boolean b, JsonGenerator generator) throws Exception;

    @JsonInput
    boolean readPrimitiveBooleanX(JsonParser parser) throws Exception;

    @JsonOutput
    void writePrimitiveByteX(byte b, JsonGenerator generator) throws Exception;

    @JsonInput
    byte readPrimitiveByteX(JsonParser parser) throws Exception;
```
(and so on)

Then define boxed serializers that reuse the primitive serializers:
```java
// ../scruse-tests/scruse-tests-jackson/src/main/java/org/tillerino/scruse/tests/base/features/DelegationSerde.java#L61-L73

@JsonConfig(uses = PrimitiveScalarsSerde.class)
interface BoxedScalarsSerde {
    @JsonOutput
    void writeBoxedBooleanX(Boolean b, JsonGenerator generator) throws Exception;

    @JsonInput
    Boolean readBoxedBooleanX(JsonParser parser) throws Exception;

    @JsonOutput
    void writeBoxedByteX(Byte b, JsonGenerator generator) throws Exception;

    @JsonInput
    Byte readBoxedByteX(JsonParser parser) throws Exception;
```
(and so on)

Finally, define array serializers that reuse both primitive and boxed serializers:
```java
// ../scruse-tests/scruse-tests-jackson/src/main/java/org/tillerino/scruse/tests/base/features/DelegationSerde.java#L125-L137

@JsonConfig(uses = BoxedScalarsSerde.class)
interface ScalarArraysSerde {
    @JsonOutput
    void writeBooleanArrayX(boolean[] input, JsonGenerator generator) throws Exception;

    @JsonInput
    boolean[] readBooleanArrayX(JsonParser parser) throws Exception;

    @JsonOutput
    void writeBoxedBooleanArrayX(Boolean[] input, JsonGenerator generator) throws Exception;

    @JsonInput
    Boolean[] readBoxedBooleanArrayX(JsonParser parser) throws Exception;
```

This approach allows you to build a comprehensive library of reusable serializers while keeping the generated code small and efficient.
Note that `uses` works transitively, 

## Recursive Types

For recursive types, delegation is crucial. Without delegation, the generated code would have to be infinite.
Take the following Type:
```java
// ../scruse-tests/scruse-tests-base/src/main/java/org/tillerino/scruse/tests/model/features/DelegationModel.java#L12-L12

record SelfReferencingRecord(String prop, SelfReferencingRecord self) {}
```

This type cannot be used in any other serialization without adding a dedicated serializer:
```java
// ../scruse-tests/scruse-tests-jackson/src/main/java/org/tillerino/scruse/tests/base/features/DelegationSerde.java#L233-L237

@JsonInput
SelfReferencingRecord deserializeRecord(JsonParser input) throws Exception;

@JsonInput
List<SelfReferencingRecord> deserializeList(JsonParser input) throws Exception;
```

This is because when serializing `SelfReferencingRecord`, a recursive call must be made:

```java
// ../scruse-tests/scruse-tests-jackson/target/generated-sources/annotations/org/tillerino/scruse/tests/base/features/DelegationSerde$SelfReferencingSerdeImpl.java#L66-L69

case "self": {
  self$2 = this.deserializeRecord(input);
  break;
}
```