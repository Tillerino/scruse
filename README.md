# Scruse

Scruse is an annotation-processor that maps between Java and JSON objects.
It is intended for two contexts:

1) Reflection is not possible or is discouraged, e.g. when working with GraalVM native images or when static code analysis is important.
2) A tiny footprint is required, i.e. jars like jackson-databind are too big.

Scruse does not include a JSON parser or formatter and requires an external one.
Various JSON libraries are supported out-of-the-box, including:
- Jackson streaming (`JsonParser` and `JsonGenerator`) - note that this gives you support of many additional input and output formats
  through existing extensions of these classes including YAML, CBOR, and Smile.
- Jackson objects (`JsonNode`)
- Gson (`JsonParser` and `JsonWriter`)

## Usage

If you have ever used [Mapstruct](https://github.com/mapstruct/mapstruct), you will feel right at home with Scruse.

Include the following in your POM:

```xml
<dependency>
    <groupId>org.tillerino.scruse</groupId>
    <artifactId>scruse-core</artifactId>
    <version>${scruse.version}</version>
</dependency>
<dependency>
    <groupId>org.tillerino.scruse</groupId>
    <artifactId>scruse-processor</artifactId>
    <version>${scruse.version}</version>
    <scope>provided</scope>
</dependency>
```

To generate readers and writers, create an interface and annotate a method with `@JsonInput` or `@JsonOutput`:

```java
interface MyJsonMapper {
    @JsonInput
    MyObject read(JsonParser parser, DeserializationContext context);

    @JsonOutput
    void write(MyObject object, JsonGenerator generator, SerializationContext context);
}
```

The example above is based on Jackson streaming, which provides `JsonParser` for parsing and `JsonGenerator` for writing JSON.
The Scruse annotation processor will generate `MyJsonMapperImpl`, which implements the interface.
The context parameters can be omitted if they are not explicitly needed.

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

## Features

- [x] Polymorphism
- [x] Reflection bridge
- [ ] Escape hatches: fucky mechanisms to work around missing features, e.g. ghetto injection.
  Allow extending Contexts, pointing to arbitrary serializers, abstract classes with constructors.
- [ ] Custom converters
- [ ] References


- [ ] JacksonInject
- [ ] JsonAlias
- [ ] JsonAnyGetter
- [ ] JsonAnySetter
- [ ] JsonAutoDetect
- [ ] JsonBackReference
- [ ] JsonClassDescription
- [ ] JsonCreator
- [ ] JsonEnumDefaultValue
- [ ] JsonFilter
- [ ] JsonFormat
- [ ] JsonGetter
- [ ] JsonIdentityInfo
- [ ] JsonIdentityReference
- [ ] JsonIgnore
- [ ] JsonIgnoreProperties
- [ ] JsonIgnoreType
- [ ] JsonInclude
- [ ] JsonIncludeProperties
- [ ] JsonKey
- [ ] JsonManagedReference
- [ ] JsonMerge
- [ ] JsonProperty
- [ ] JsonPropertyDescription
- [ ] JsonPropertyOrder
- [ ] JsonRawValue
- [ ] JsonRootName
- [ ] JsonSetter
- [ ] JsonSubTypes
- [ ] JsonTypeId
- [ ] JsonTypeInfo
- [ ] JsonTypeName
- [ ] JsonUnwrapped
- [ ] JsonValue
- [ ] JsonView

## Checkboxes

- [x] Serialize all eight primitive types raw and boxed
- [x] Serialize String (includes char[])
- [x] Serialize Arrays, Lists (generalized as Iterables)
- [x] Serialize byte[]
- [x] Serialize Maps
- [x] Serialize record components, getters, fields

- [x] Deserialize all eight primitive types raw and boxed
- [x] Deserialize String (includes char[])
- [x] Deserialize Arrays, Lists (generalized as Iterables)
- [x] Deserialize byte[]
- [x] Deserialize Maps
- [x] Deserialize record components, setters, fields

- [x] Delegate to other readers/writers
- [ ] enums