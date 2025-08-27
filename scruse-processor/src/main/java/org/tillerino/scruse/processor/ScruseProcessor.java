package org.tillerino.scruse.processor;

import static javax.tools.Diagnostic.Kind.ERROR;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.annotations.JsonTemplate;
import org.tillerino.scruse.processor.apis.*;
import org.tillerino.scruse.processor.config.ConfigProperty;
import org.tillerino.scruse.processor.config.ConfigProperty.LocationKind;
import org.tillerino.scruse.processor.util.Exceptions;
import org.tillerino.scruse.processor.util.InstantiatedMethod;
import org.tillerino.scruse.processor.util.PrototypeKind;

@SupportedAnnotationTypes({
    "org.tillerino.scruse.annotations.JsonOutput",
    "org.tillerino.scruse.annotations.JsonInput",
    "org.tillerino.scruse.annotations.JsonConfig",
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class ScruseProcessor extends AbstractProcessor {

    AnnotationProcessorUtils utils;

    Set<String> generatedClasses = new LinkedHashSet<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    private void mapStructSetup(ProcessingEnvironment processingEnv, TypeElement typeElement) {
        if (utils == null) {
            // AFAICT, the typeElement is only used for type resolution, so the first processed type should do fine
            utils = new AnnotationProcessorUtils(processingEnv, typeElement);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        collectElements(roundEnv);
        generateCode();
        return true;
    }

    private void collectElements(RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(JsonConfig.class).forEach(element -> {
            if (!(element instanceof TypeElement type)) {
                return;
            }
            mapStructSetup(processingEnv, type);
            Exceptions.runWithContext(
                    () -> {
                        ScruseBlueprint blueprint = utils.blueprint(type);
                        for (ExecutableElement exec :
                                ElementFilter.methodsIn(utils.elements.getAllMembers((TypeElement) element))) {
                            if (!exec.getEnclosingElement().equals(element)) {
                                InstantiatedMethod instantiated = utils.generics.instantiateMethod(
                                        exec, blueprint.typeBindings, LocationKind.PROTOTYPE);
                                PrototypeKind.of(instantiated, utils).ifPresent(kind -> {
                                    ScrusePrototype method =
                                            ScrusePrototype.of(blueprint, instantiated, kind, utils, true);
                                    // should actually check if super method is not being generated and THIS is being
                                    // generated
                                    if (method.config()
                                            .resolveProperty(ConfigProperty.IMPLEMENT)
                                            .value()
                                            .shouldImplement()) {
                                        blueprint.prototypes.add(method);
                                    }
                                });
                            }
                        }
                    },
                    "config",
                    element);
        });
        roundEnv.getElementsAnnotatedWith(JsonTemplate.class).forEach(element -> {
            if (!(element instanceof TypeElement type)) {
                return;
            }
            mapStructSetup(processingEnv, type);
            ScruseBlueprint blueprint = utils.blueprint(type);
            blueprint.prototypes.addAll(utils.templates.instantiateTemplatedPrototypes(blueprint));
        });
        roundEnv.getElementsAnnotatedWith(JsonOutput.class).forEach(element -> {
            ExecutableElement exec = (ExecutableElement) element;
            TypeElement type = (TypeElement) exec.getEnclosingElement();
            mapStructSetup(processingEnv, type);
            ScruseBlueprint blueprint = utils.blueprint(type);
            InstantiatedMethod instantiated =
                    utils.generics.instantiateMethod(exec, blueprint.typeBindings, LocationKind.PROTOTYPE);
            PrototypeKind.of(instantiated, utils)
                    .ifPresentOrElse(
                            kind -> {
                                ScrusePrototype method = ScrusePrototype.of(blueprint, instantiated, kind, utils, true);
                                blueprint.prototypes.add(method);
                            },
                            () -> {
                                logError("Signature unknown. Please see @JsonOutput for hints.", exec);
                            });
        });
        roundEnv.getElementsAnnotatedWith(JsonInput.class).forEach(element -> {
            ExecutableElement exec = (ExecutableElement) element;
            TypeElement type = (TypeElement) exec.getEnclosingElement();
            mapStructSetup(processingEnv, type);
            ScruseBlueprint blueprint = utils.blueprint(type);
            InstantiatedMethod instantiated =
                    utils.generics.instantiateMethod(exec, blueprint.typeBindings, LocationKind.PROTOTYPE);
            PrototypeKind.of(instantiated, utils)
                    .ifPresentOrElse(
                            kind -> {
                                ScrusePrototype method = ScrusePrototype.of(blueprint, instantiated, kind, utils, true);
                                blueprint.prototypes.add(method);
                            },
                            () -> {
                                logError("Signature unknown. Please see @JsonInput for hints.", exec);
                            });
        });
    }

    private void generateCode() {
        for (ScruseBlueprint blueprint : utils.blueprints.values()) {
            if (!generatedClasses.add(blueprint.generatedClassName())) {
                continue;
            }
            if (blueprint.prototypes.stream().anyMatch(method -> method.config()
                    .resolveProperty(ConfigProperty.IMPLEMENT)
                    .value()
                    .shouldImplement())) {
                try {
                    mapStructSetup(processingEnv, blueprint.typeElement);
                    generateCode(blueprint);
                } catch (Exception e) {
                    if (e instanceof ContextedRuntimeException) {
                        System.out.println(((ContextedRuntimeException) e).getContextEntries());
                    }
                    e.printStackTrace();
                    processingEnv.getMessager().printMessage(ERROR, e.getMessage());
                }
            }
        }
    }

    private void generateCode(ScruseBlueprint blueprint) throws IOException {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(blueprint.className.nameInCompilationUnit() + "Impl")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(blueprint.typeElement.asType());
        List<MethodSpec> methods = new ArrayList<>();
        GeneratedClass generatedClass = new GeneratedClass(classBuilder, utils, blueprint);
        for (ScrusePrototype method : blueprint.prototypes) {
            if (!method.methodElement().getModifiers().contains(Modifier.ABSTRACT)
                    || !method.config()
                            .resolveProperty(ConfigProperty.IMPLEMENT)
                            .value()
                            .shouldImplement()) {
                // method is implemented by user and can be used by us
                continue;
            }
            Exceptions.runWithContext(
                    () -> {
                        MethodSpec e = generateMethod(method, generatedClass);
                        if (e != null) {
                            methods.add(e);
                        }
                    },
                    "specification",
                    method);
        }
        generatedClass.buildFields(classBuilder);
        for (MethodSpec method : methods) {
            classBuilder.addMethod(method);
        }
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(blueprint.generatedClassName());
        try (Writer writer = sourceFile.openWriter()) {
            JavaFile.Builder builder = JavaFile.builder(blueprint.className.packageName(), classBuilder.build());
            generatedClass.fileBuilderMods.forEach(mod -> mod.accept(builder));
            JavaFile file = builder.build();
            file.writeTo(writer);
        }
    }

    private @Nullable MethodSpec generateMethod(ScrusePrototype method, GeneratedClass generatedClass) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.name());
        if (method.overrides()) {
            methodBuilder.addAnnotation(Override.class);
        }
        methodBuilder
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariables(method.methodElement().getTypeParameters().stream()
                        .map(TypeParameterElement::getSimpleName)
                        .map(name -> TypeVariableName.get(name.toString()))
                        .toList())
                .returns(ClassName.get(method.instantiatedReturnType()));
        method.instantiatedParameters()
                .forEach(param -> methodBuilder.addParameter(ClassName.get(param.type()), param.name()));
        method.methodElement().getThrownTypes().forEach(type -> methodBuilder.addException(ClassName.get(type)));
        Supplier<CodeBlock.Builder> codeGenerator =
                switch (method.kind().direction()) {
                    case INPUT -> determineInputCodeGenerator(method, generatedClass);
                    case OUTPUT -> determineOutputCodeGenerator(method, generatedClass);
                };
        if (codeGenerator == null) {
            logError("Signature unknown. Please see @JsonOutput/@JsonInput for hints.", method.methodElement());
            return null;
        }
        methodBuilder.addCode(codeGenerator.get().build());
        return methodBuilder.build();
    }

    private Supplier<CodeBlock.Builder> determineOutputCodeGenerator(
            ScrusePrototype method, GeneratedClass generatedClass) {
        return switch (method.kind().jsonType().toString()) {
            case PrototypeKind.JACKSON_JSON_GENERATOR -> new JacksonJsonGeneratorWriterGenerator(
                    utils, method, generatedClass)::build;
            case PrototypeKind.GSON_JSON_WRITER -> new GsonJsonWriterWriterGenerator(utils, method, generatedClass)
                    ::build;
            case PrototypeKind.FASTJSON_2_JSONWRITER -> new Fastjson2WriterGenerator(utils, method, generatedClass)
                    ::build;
            case PrototypeKind.JAKARTA_JSON_GENERATOR -> new JakartaJsonGeneratorGenerator(
                    utils, method, generatedClass)::build;
            case PrototypeKind.NANOJSON_JSON_WRITER -> new NanojsonWriterGenerator(utils, method, generatedClass)
                    ::build;
            case PrototypeKind.SCRUSE_WRITER -> new ScruseWriterGenerator(utils, method, generatedClass)::build;
            default -> throw new ContextedRuntimeException(
                    "Unknown output type: " + method.kind().jsonType());
        };
    }

    private Supplier<CodeBlock.Builder> determineInputCodeGenerator(
            ScrusePrototype method, GeneratedClass generatedClass) {
        return switch (method.kind().jsonType().toString()) {
            case PrototypeKind.JACKSON_JSON_PARSER -> new JacksonJsonParserReaderGenerator(
                    utils, method, generatedClass)::build;
            case PrototypeKind.GSON_JSON_READER -> new GsonJsonReaderReaderGenerator(utils, method, generatedClass)
                    ::build;
            case PrototypeKind.FASTJSON_2_JSONREADER -> new Fastjson2ReaderGenerator(utils, method, generatedClass)
                    ::build;
            case PrototypeKind.JAKARTA_JSON_PARSER -> new JakartaJsonParserGenerator(utils, method, generatedClass)
                    ::build;
            case PrototypeKind.SCRUSE_READER -> new ScruseReaderGenerator(utils, method, generatedClass)::build;
            default -> throw new ContextedRuntimeException(
                    "Unknown input type: " + method.kind().jsonType());
        };
    }

    private void logError(String msg, Element element) {
        processingEnv.getMessager().printMessage(ERROR, msg != null ? msg : "(null)", element);
    }
}
