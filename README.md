# Scruse

Scruse is an annotation-processor that generates databind classes to map JSON (and similar formats) to Java classes and vice versa.
It is intended for two contexts:

1) Reflection is not possible or is discouraged, e.g. when working with GraalVM native images or when static code analysis is important.
2) A tiny footprint is required, i.e. jars like jackson-databind are too big.

Scruse does not include any parsers or formatters and requires external ones.
Various JSON libraries are supported out-of-the-box, including:
- Jackson streaming (`JsonParser` and `JsonGenerator`) - note that this gives you support of many additional input and output formats
  through existing extensions of these classes like YAML, CBOR, Smile, and more.
- Jackson objects (`JsonNode`)
- Gson (`JsonParser` and `JsonWriter`)
- JSON-P
- Fastjson2 (`JSONReader` and `JSONWriter`)
- Nanojson

Note that Scruse is not a beginner-friendly library.
It optimizes for constraints that are not common in most applications.
If you _just_ want to serialize and deserialize JSON and do not have any of the constraints above, you are probably better off with Jackson.


## Usage

If you have ever used [Mapstruct](https://github.com/mapstruct/mapstruct), you will feel right at home with Scruse.

Include the following in your POM:

```xml
<dependency>
    <groupId>org.tillerino.scruse</groupId>
    <artifactId>scruse-core</artifactId>
    <version>${scruse.version}</version>
</dependency>
```

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <annotationProcessorPath>
                <groupId>org.tillerino.scruse</groupId>
                <artifactId>scruse-processor</artifactId>
                <version>${scruse.version}</version>
            </annotationProcessorPath>
        </annotationProcessorPaths>
        <compilerArgs>
            <arg>-parameters</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

To generate readers and writers, create an interface and annotate a method with `@JsonInput` or `@JsonOutput`:

```java
interface MyObjectSerde {
    @JsonInput
    MyObject read(JsonParser parser, DeserializationContext context) throws IOException;

    @JsonOutput
    void write(MyObject object, JsonGenerator generator, SerializationContext context) throws IOException;
}
```

The example above is based on Jackson streaming, which provides `JsonParser` for parsing and `JsonGenerator` for writing JSON.
The Scruse annotation processor will generate `MyJsonMapperImpl`, which implements the interface.
The context parameters can be omitted if they are not explicitly needed.

### Delegators

Scruse can delegate to other suitable `@JsonInput` and `@JsonOutput` methods whenever possible.
This is very important for keeping the generated code small.
Take the following example:

```java
interface MyObjectSerde {
  @JsonInput
  List<MyObject> read(JsonParser parser) throws IOException;

  @JsonInput
  MyObject read(JsonParser parser) throws IOException;
}
```

Here, the implementation of the first method will call the second method for each element in the list.
It is recommended to view the generated code and declare further methods to break down large generated methods.
This will work at any level, and you can even declare methods for primitive types.

To organize your methods, you can use the `uses` attribute of the `@JsonConfig` annotation:

```java
@JsonConfig(uses = {PrimitivesSerde.class})
interface MyObjectSerde {
  ...
}

interface PrimitivesSerde {
  @JsonInput
  int readInt(JsonParser parser) throws IOException;

  ...
}
```

### Converters

It is impractical to write actual (de-)serialisers for data types which have a simpler representation like a string.
`@JsonValue` and `@JsonCreator` are supported, but if you cannot (or do not want to) modify the actual types, you can use converters.
Converters are static methods annotated with `@JsonOutputConverter` or `@JsonInputConverter`.
See this example for `OffsetDateTime`:

```java
public class OffsetDateTimeConverters {
  @JsonOutputConverter
  public static String offsetDateTimeToString(OffsetDateTime offsetDateTime) {
    return offsetDateTime.toString();
  }

  @JsonInputConverter
  public static OffsetDateTime stringToOffsetDateTime(String string) {
    return OffsetDateTime.parse(string);
  }
}
```

Converter methods can be either located in the same class as the `@JsonInput` or `@JsonOutput` method
or in a separate class and referenced with the `@JsonConfig` `uses` value.

Generics are supported for converters, `@JsonValue`, and `@JsonCreator` methods.
For example, you can write a converter for `Optional<T>`:

```java
public class OptionalConverters {
  @JsonOutputConverter
  public static <T> T optionalToNullable(Optional<T> optional) {
    return optional.orElse(null);
  }

  @JsonInputConverter
  public static <T> Optional<T> nullableToOptional(T value) {
    return Optional.ofNullable(value);
  }
}
```

This specific case has already been implemented for reuse
in [OptionalInputConverters](scruse-core/src/main/java/org/tillerino/scruse/converters/OptionalInputConverters.java)
and [OptionalOutputConverters](scruse-core/src/main/java/org/tillerino/scruse/converters/OptionalOutputConverters.java).
See the [converters](scruse-core/src/main/java/org/tillerino/scruse/converters) package for more premade converters.

### Generics

There is some support for generics.
Suppose you have a record with a generic component:

```java
  record MyRecord<T>(T genericComponent /*, and a bunch of non-generic components  */) { }
```

Suppose you need several `@JsonInput` and `@JsonOutput` methods for `MyRecord` with different types of `T`,
e.g. `MyRecord<String>`, `MyRecord<Integer>`, and so on.
Firstly, the generics are correctly instantiated, i.e. for `MyRecord<String>`, `genericComponent` is serialized as a String.
However, simply writing several methods with different types of `T` will produce a lot of repeated code.
To avoid this, you can pass sub-(de-)serializers to a generic method.
For a sub-(de-)serializer, you need to define an interface, for example the following:

```java
interface GenericOutput<T> {
  @JsonOutput
  void write(T whatever, JsonGenerator generator) throws IOException;
}
```

Then, you can use this interface in the main (de-)serializer:

```java
interface MyRecordSerde {
  @JsonOutput
  <T> void write(MyRecord<T> record, JsonGenerator generator, GenericOutput<T> subSerializer) throws IOException;
}
```

The generated code will invoke the passed serializer for the generic component.
Once `MyRecordSerde` is used by any other (de-)serializer, any matching sub-(de-)serializer from the classes in `@JsonConfig(uses = { ... })`
will be passed as the `subSerializer` parameter,
or a _suitable lambda expression_ will be passed to instantiate the sub-(de-)serializer from any method in all available (de-)serializers.

This is a fairly involved feature. Please see the examples in
[GenericsTest](scruse-tests/scruse-tests-jackson/src/test/java/org/tillerino/scruse/tests/base/generics/GenericsTest.java)
and compare the generated code.

## Backends (Parser/formatter implementations)

Scruse supports multiple backends for reading and writing JSON.
You can choose the backend that best fits your requirements and dependencies.

You can get a clearer idea of each backend in practice by looking at the modules in the `scruse-tests` directory.
We run the entire test suite for each backend with some exceptions.
The tests are written for the `jackson-core` backend and copied/adapted to the other backends in the `generate-sources` phase.

The jar of each test module is shaded with the `minimizeJar` flag for each test module to estimate the overhead of each backend.
In many cases, this can be optimized further, but we provide this number as a baseline.

If you have trouble picking a backend, here are some ideas:
- Jackson Core if you do not want to worry about anything.
- Fastjson2 if you want speed.
- Nanojson if you want a small footprint.

### Jackson Core (streaming)

`jackson-core` provides `JsonParser` and `JsonGenerator` for reading and writing JSON.
We consider this the default backend and use it for most examples.
The required dependency is:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-core</artifactId>
  <version>${jackson.version}</version>
</dependency>
```

Overhead: 740kiB

### Jackson Databind (objects)

`jackson-databind` provides `JsonNode` for reading and writing JSON.
You would only use this instead of `jackson-core` if you have some special requirements, e.g.
you cannot guarantee that the order of fields in JSON is stable and require polymorphism.
To correctly handle visibility of discriminators and unknown properties, we need adapters for `JsonNode`.
Use `JacksonJsonNodeReaderAdapter` and `JacksonJsonNodeWriterAdapter` to write your `@JsonInput` and `@JsonOutput` methods.
The required dependency is:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-databind</artifactId>
  <version>${jackson.version}</version>
</dependency>
```

Overhead: 2100kiB

### Gson

`gson` provides `JsonParser` and `JsonWriter` for reading and writing JSON.
The required dependency is:

```xml
<dependency>
  <groupId>com.google.code.gson</groupId>
  <artifactId>gson</artifactId>
  <version>${gson.version}</version>
</dependency>
```

Overhead: 280kiB

### fastjson2

`fastjson2` provides `JSONReader` and `JSONWriter` for reading and writing JSON.
At the time of writing it is the fastest JSON library for Java
according to [some benchmarks](https://github.com/fabienrenaud/java-json-benchmark).
The required dependency is:

```xml
<dependency>
  <groupId>com.alibaba.fastjson2</groupId>
  <artifactId>fastjson2</artifactId>
  <version>${fastjson2.version}</version>
</dependency>
```

Overhead: 1920kiB

### Jakarta JSON-P

`jakarta.json-api` is an API definition which provides `JsonParser` and `JsonGenerator` for reading and writing JSON.
`JsonParser#currentToken` is fairly new and not supported by all implementations.
Additionally, `JsonParser` does not allow us to properly save the state of _the input has ended_.
This is why we wrap `JsonParserWrapper` around it.

The required dependency is:

```xml
<dependency>
  <groupId>jakarta.json</groupId>
  <artifactId>jakarta.json-api</artifactId>
  <version>${jakarta.json.version}</version>
</dependency>
```

In addition to the API, you need to include an implementation. There are several available.

```xml
<dependency>
  <groupId>org.apache.johnzon</groupId>
  <artifactId>johnzon-core</artifactId>
  <version>${johnzon.version}</version>
</dependency>
```

Johnzon and the API have an overhead of 180kiB.

```xml
<dependency>
  <groupId>org.glassfish</groupId>
  <artifactId>jakarta.json</artifactId>
  <version>${glassfish.json.version}</version>
</dependency>
```

The Glassfish implementation has an overhead of 137kiB.

### Nanojson

`nanojson` is a small JSON parser and writer.
Its `JsonParser` class just misses what we need. To read JSON, you need to create a `TokenerWrapper` instance
from an `InputStream` or `Reader`.
We use `JsonAppendableWriter` for writing JSON, which can be obtained from the `JsonWriter` factory.

The required dependency is:

```xml
<dependency>
  <groupId>com.grack</groupId>
  <artifactId>nanojson</artifactId>
  <version>${nanojson.version}</version>
</dependency>
```

Overhead: 30kiB

## Escape hatches (hacking Scruse)

Obviously, Scruse is not complete in any sense, and you will soon reach the limits of the core functionality.
We have included some _escape hatches_, a.k.a. ways to hack your way around missing functionalities.

### Custom implementation

You can always simply serializers yourself:

```java
interface CustomizedSerialization {
    @JsonOutput
    void writeMyObj(MyObj o, JsonGenerator generator) throws IOException;

    @JsonOutput
    default void writeOffsetDate(OffsetDateTime timestamp, JsonGenerator generator) throws IOException {       
        generator.writeString(timestamp.toString());
    }

    record MyObj(OffsetDateTime t) { }
}
```

This works for output and input.

### Pointing individual properties to serializers

(TODO; something like @JsonSerializer, but this annotation is in databind, so off limits)

### Custom state

(TODO; allow extending context classes, allow abstract classes as mappers, handle constructors of those)

## Alternatives

- [jackson-databind](https://github.com/FasterXML/jackson-databind):
  The definitive standard for Java JSON serialization :heart:. Jackson is the anti-Scruse:
  It is entirely based on reflection, and even includes a mechanism to write Java bytecode at runtime to boost performance.
  Jackson is so large that there is a smaller version called [jackson-jr](https://github.com/FasterXML/jackson-jr). 
- https://github.com/ngs-doo/dsl-json
  

## Compatibility

In general, Scruse tries to be compatible with Jackson's default behaviour.
Some of Jackson's annotations are supported, but not all and not each supported annotation is supported fully.

### Notable exceptions

- With polymorphism, Jackson will always write and require a discriminator, even when explicitly limiting the type to a specific subtype.
  Scruse will not write or require a discriminator when the subtype is known.
- Jackson requires `ParameterNamesModule` and compilation with the `-parameters` flag to support creator-based deserialization without
  @JsonProperty annotations. Scruse does not require this since this information is always present during annotation processing.
- Scruse will assign the default value of the property type to absent properties even when converters are used.
  Jackson will always use the converter and invoke it with its default argument - _I think_.
  An example of this is that Scruse will initialize an absent `Optional<Optional<T>>` property with `Optional.empty()`
  whereas Jackson will instead initialize it with `Optional.of(Optional.empty())`.
  I asked here: https://github.com/FasterXML/jackson-modules-java8/issues/310

### Features

- [x] Polymorphism
- [x] Reflection bridge

Escape hatches: fucky mechanisms to work around missing features, e.g. ghetto injection.
- [x] Allow extending Contexts
- [ ] Custom converters per property
- [ ] Abstract converter classes with constructors.

### Jackson annotations compatibility

The following is a rough indication of compatibility with Jackson's annotations.
A checkmark indicates _basic_ compatibility, although there can be edge cases where we are not compatible.

- [ ] JacksonInject
- [ ] JsonAlias
- [ ] JsonAnyGetter
- [ ] JsonAnySetter
- [ ] JsonAutoDetect
- [ ] JsonBackReference
- [ ] JsonClassDescription
- [X] JsonCreator - with default `mode`
- [ ] JsonEnumDefaultValue
- [ ] JsonFilter
- [ ] JsonFormat
- [ ] JsonGetter
- [ ] JsonIdentityInfo
- [ ] JsonIdentityReference
- [X] JsonIgnore
- [X] JsonIgnoreProperties (`value` and `ignoreUnknown` properties)
- [ ] JsonIgnoreType
- [ ] JsonInclude
- [ ] JsonIncludeProperties
- [ ] JsonKey
- [ ] JsonManagedReference
- [ ] JsonMerge
- [X] JsonProperty (only `value`)
- [ ] JsonPropertyDescription
- [ ] JsonPropertyOrder
- [ ] JsonRawValue
- [ ] JsonRootName
- [ ] JsonSetter
- [X] JsonSubTypes (`failOnRepeatedNames` unsupported)
- [ ] JsonTypeId
- [X] JsonTypeInfo (not `use` `CUSTOM` or `DEDUCE`, always `include` `PROPERTY`, `defaultImpl` ignored, `visible` always `false`)
- [ ] JsonTypeName
- [ ] JsonUnwrapped
- [X] JsonValue
- [ ] JsonView

## Roadmap

### Short-term:
- Sort out missing properties.
- Allow polymorphism for sealed interfaces without further annotations.

### Long-term:

- Slowly add support for more Jackson annotations, but on a need-to-have basis.
  There are so many annotations that we cannot support them all.
