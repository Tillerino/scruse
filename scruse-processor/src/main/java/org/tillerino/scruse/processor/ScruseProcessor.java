package org.tillerino.scruse.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import org.tillerino.scruse.annotations.*;
import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName;
import org.tillerino.scruse.processor.apis.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static javax.tools.Diagnostic.Kind.ERROR;

@SupportedAnnotationTypes({
	"org.tillerino.scruse.annotations.JsonOutput",
	"org.tillerino.scruse.annotations.JsonInput",
	"org.tillerino.scruse.annotations.JsonConfig",
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class ScruseProcessor extends AbstractProcessor {
	Map<String, ScruseBlueprint> blueprints = new LinkedHashMap<>();

	AnnotationProcessorUtils utils;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
	}

	private void mapStructSetup(ProcessingEnvironment processingEnv, TypeElement typeElement) {
		if (utils == null) {
			// AFAICT, the typeElement is only used for type resolution, so the first processed type should do fine
			utils = new AnnotationProcessorUtils(processingEnv, typeElement, new PrototypeFinder(processingEnv.getTypeUtils(), blueprints));
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			collectElements(roundEnv);
		} else {
			generateCode();
		}
		return false;
	}

	private void collectElements(RoundEnvironment roundEnv) {
		roundEnv.getElementsAnnotatedWith(JsonConfig.class).forEach(element -> {
			TypeElement type = (TypeElement) element;
			mapStructSetup(processingEnv, type);
			ScruseBlueprint blueprint = blueprint(type, true);
			for (AnnotationMirror annotation : type.getAnnotationMirrors()) {
				if (FullyQualifiedClassName.of((TypeElement) annotation.getAnnotationType().asElement()).importName().equals(JsonConfig.class.getName())) {
					annotation.getElementValues().forEach((name, value) -> {
						if (name.getSimpleName().toString().equals("uses")) {
							utils.getTypeElementsFromAnnotationValue(value).forEach(e -> blueprint.uses().add(blueprint(e, false)));
						}
					});
				}
			}
		});
		roundEnv.getElementsAnnotatedWith(JsonOutput.class).forEach(element -> {
			if (element.getEnclosingElement().getAnnotation(JsonInterface.class) != null) {
				return;
			}
			ExecutableElement method = (ExecutableElement) element;
			TypeElement type = (TypeElement) method.getEnclosingElement();
			mapStructSetup(processingEnv, type);
			ScruseBlueprint blueprint = blueprint(type, true);
			blueprint.methods().add(ScruseMethod.of(blueprint, method, ScruseMethod.InputOutput.OUTPUT, utils));
		});
		roundEnv.getElementsAnnotatedWith(JsonInput.class).forEach(element -> {
			if (element.getEnclosingElement().getAnnotation(JsonInterface.class) != null) {
				return;
			}
			ExecutableElement method = (ExecutableElement) element;
			TypeElement type = (TypeElement) method.getEnclosingElement();
			mapStructSetup(processingEnv, type);
			ScruseBlueprint blueprint = blueprint(type, true);
			blueprint.methods().add(ScruseMethod.of(blueprint, method, ScruseMethod.InputOutput.INPUT, utils));
		});
		roundEnv.getElementsAnnotatedWith(JsonImpl.class).forEach(element -> {
			TypeElement type = (TypeElement) element;
			ScruseBlueprint blueprint = blueprint(type, true);
			mapStructSetup(processingEnv, type);
			for (Element enclosedElement : utils.elements.getAllMembers((TypeElement) element)) {
				if (enclosedElement instanceof ExecutableElement exec && exec.getAnnotation(JsonOutput.class) != null) {
					blueprint.methods().add(ScruseMethod.of(blueprint, exec, ScruseMethod.InputOutput.OUTPUT, utils));
				}
			}
		});
	}

	private void generateCode() {
		for (ScruseBlueprint blueprint : blueprints.values()) {
			if (blueprint.toBeGenerated().get()) {
				try {
					mapStructSetup(processingEnv, blueprint.typeElement());
					generateCode(blueprint);
				} catch (IOException e) {
					e.printStackTrace();
					processingEnv.getMessager().printMessage(ERROR, e.getMessage());
				}
			}
		}
	}

	private void generateCode(ScruseBlueprint blueprint) throws IOException {
		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(blueprint.className().nameInCompilationUnit() + "Impl")
			.addModifiers(Modifier.PUBLIC)
			.addSuperinterface(blueprint.typeElement().asType());
		List<MethodSpec> methods = new ArrayList<>();
		GeneratedClass generatedClass = new GeneratedClass(classBuilder, utils, blueprint);
		for (ScruseMethod method : blueprint.methods()) {
			if (!method.methodElement().getModifiers().contains(Modifier.ABSTRACT)) {
				// method is implemented by user and can be used by us
				continue;
			}
			MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.methodElement().getSimpleName().toString())
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC)
				.addTypeVariables(method.methodElement().getTypeParameters().stream().map(TypeParameterElement::getSimpleName).map(name -> TypeVariableName.get(name.toString())).toList())
				.returns(ClassName.get(method.methodElement().getReturnType()));
			method.instantiatedParameters().forEach(param -> methodBuilder.addParameter(ClassName.get(param.type()), param.name()));
			method.methodElement().getThrownTypes().forEach(type -> methodBuilder.addException(ClassName.get(type)));
			Supplier<CodeBlock.Builder> codeGenerator = switch (method.direction()) {
				case INPUT -> determineInputCodeGenerator(method, generatedClass);
				case OUTPUT -> determineOutputCodeGenerator(method, generatedClass);
			};
			if (codeGenerator == null) {
				logError("Signature unknown. Please see @JsonOutput/@JsonInput for hints.", method.methodElement());
				continue;
			}
			try {
				methodBuilder.addCode(codeGenerator.get().build());
				methods.add(methodBuilder.build());
			} catch (Exception e) {
				e.printStackTrace();
				logError(e.getMessage(), method.methodElement());
			}
		}
		generatedClass.buildFields(classBuilder);
		methods.forEach(classBuilder::addMethod);
		JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(blueprint.className().fileName().replace("/", ".") + "Impl");
		try (Writer writer = sourceFile.openWriter()) {
			JavaFile.Builder builder = JavaFile.builder(blueprint.className().packageName(), classBuilder.build());
			generatedClass.fileBuilderMods.forEach(mod -> mod.accept(builder));
			JavaFile file = builder.build();
			file.writeTo(writer);
		}
	}

	private Supplier<CodeBlock.Builder> determineOutputCodeGenerator(ScruseMethod method, GeneratedClass generatedClass) {
		if (!method.methodElement().getParameters().isEmpty()
				&& method.methodElement().getReturnType().toString().equals("com.fasterxml.jackson.databind.JsonNode")) {
			return new JacksonJsonNodeWriterGenerator(utils, method, generatedClass)::build;
		}
		if (method.methodElement().getParameters().size() < 2 || method.methodElement().getReturnType().getKind() != TypeKind.VOID) {
			return null;
		}
		return switch (method.methodElement().getParameters().get(1).asType().toString()) {
			case "com.fasterxml.jackson.core.JsonGenerator" ->
					new JacksonJsonGeneratorWriterGenerator(utils, method, generatedClass)::build;
			case "com.google.gson.stream.JsonWriter" ->
					new GsonJsonWriterWriterGenerator(utils, method, generatedClass)::build;
			case "com.alibaba.fastjson2.JSONWriter" ->
					new Fastjson2WriterGenerator(utils, method, generatedClass)::build;
			default -> null;
		};
	}

	private Supplier<CodeBlock.Builder> determineInputCodeGenerator(ScruseMethod method, GeneratedClass generatedClass) {
		if (method.methodElement().getParameters().isEmpty()) {
			return null;
		}
		return switch (method.methodElement().getParameters().get(0).asType().toString()) {
			case "com.fasterxml.jackson.core.JsonParser" ->
					new JacksonJsonParserReaderGenerator(utils, method, generatedClass)::build;
			case "com.fasterxml.jackson.databind.JsonNode" ->
					new JacksonJsonNodeReaderGenerator(utils, method, generatedClass)::build;
			case "com.google.gson.stream.JsonReader" ->
					new GsonJsonReaderReaderGenerator(utils, method, generatedClass)::build;
			case "com.alibaba.fastjson2.JSONReader" ->
					new Fastjson2ReaderGenerator(utils, method, generatedClass)::build;
			default -> null;
		};
	}

	private void logError(String msg, Element element) {
		processingEnv.getMessager().printMessage(ERROR, msg != null ? msg : "(null)", element);
	}

	ScruseBlueprint blueprint(TypeElement element, boolean toBeGenerated) {
		ScruseBlueprint blueprint = blueprints.computeIfAbsent(element.getQualifiedName().toString(),
			name -> ScruseBlueprint.of(new AtomicBoolean(toBeGenerated), element, new ArrayList<>(), new ArrayList<>(), utils));
		if (toBeGenerated) {
			blueprint.toBeGenerated().set(true);
		}
		return blueprint;
	}

}