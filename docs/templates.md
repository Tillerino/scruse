# Templates

## Basics
Writing `@JsonInput` and `@JsonOutput` methods can be repetitive.
If you use extra arguments (see [Delegators](delegators.md)), it can be especially error-prone.

The `@JsonTemplate` annotation allows you to generate many prototypes based on simple templates.

Consider these generic interfaces:

```java
//../scruse-tests/scruse-tests-jackson/src/main/java/org/tillerino/scruse/tests/base/features/GenericsSerde.java#L16-L20

@JsonConfig(implement = JsonConfig.ImplementationMode.DO_NOT_IMPLEMENT)
interface GenericInputSerde<V> {
    @JsonInput
    V readOnGenericInterface(JsonParser parser) throws Exception;
}
```
```java
//../scruse-tests/scruse-tests-jackson/src/main/java/org/tillerino/scruse/tests/base/features/GenericsSerde.java#L23-L27

@JsonConfig(implement = JsonConfig.ImplementationMode.DO_NOT_IMPLEMENT)
interface GenericOutputSerde<U> {
    @JsonOutput
    void writeOnGenericInterface(U obj, JsonGenerator gen) throws Exception;
}
```

Annotating your blueprint with
```java
// ../scruse-tests/scruse-tests-jackson/src/main/java/org/tillerino/scruse/tests/base/features/TemplatesSerde.java#L12-L14

@JsonTemplate(
        templates = {GenericInputSerde.class, GenericOutputSerde.class},
        types = {double.class, AnEnum.class, double[].class, AnEnum[].class})
```
will automatically generate eight methods - one per template and type combination.
The generated methods will have the exact signature of the respective template with the type substituted for the single type variable.
The name of the generated methods is derived from the type.

For example:
```java
//../scruse-tests/scruse-tests-jackson/target/generated-sources/annotations/org/tillerino/scruse/tests/base/features/TemplatesSerde$TemplatedSerdeImpl.java#L73-L80

public void writeAnEnum(AnEnum obj, JsonGenerator gen) throws Exception {
  if (obj != null) {
    String root$1$string = obj.name();
    gen.writeString(root$1$string);
  } else {
    gen.writeNull();
  }
}
```

## Integration with other features

The generated methods behave exactly as if they were fully specified. In this blueprint:
```java
// ../scruse-tests/scruse-tests-jackson/src/main/java/org/tillerino/scruse/tests/base/features/TemplatesSerde.java#L11-L17

public interface TemplatesSerde {
    @JsonTemplate(
            templates = {GenericInputSerde.class, GenericOutputSerde.class},
            types = {double.class, AnEnum.class, double[].class, AnEnum[].class})
    interface TemplatedSerde {
        @JsonOutput
        <T> void writeGenericArray(T[] ts, JsonGenerator gen, GenericOutputSerde<T> serde) throws Exception;
```

`writeAnEnum` works with generics and delegates serialization of the enum itself to the specialized method:
```java
//../scruse-tests/scruse-tests-jackson/target/generated-sources/annotations/org/tillerino/scruse/tests/base/features/TemplatesSerde$TemplatedSerdeImpl.java#L142-L144

public void writeArrayOfAnEnum(AnEnum[] obj, JsonGenerator gen) throws Exception {
  this.writeGenericArray(obj, gen, this::writeAnEnum);
}
```