# Scruse

Scruse is an annotation-processor that maps between Java and JSON objects.
Its intended for two contexts:

1) Reflection is being avoided, e.g. when working with GraalVM native images or when static code analysis is important.
2) A tiny footprint is required, i.e. jars like jackson-databind are too big.

## Compatibility

## Features

- [ ] Polymorphism
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
- [ ] Deserialize Maps
- [ ] Deserialize record components, getters, fields

- [ ] Delegate to other readers/writers
- [ ] enums