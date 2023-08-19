package org.tillerino.scruse.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName;
import org.tillerino.scruse.processor.apis.GsonJsonWriterWriterGenerator;
import org.tillerino.scruse.processor.apis.JacksonJsonGeneratorWriterGenerator;
import org.tillerino.scruse.processor.apis.JacksonJsonNodeWriterGenerator;
import org.tillerino.scruse.processor.apis.JacksonJsonParserReaderGenerator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
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
	Map<FullyQualifiedClassName, ScruseBlueprint> blueprints = new LinkedHashMap<>();

	AnnotationProcessorUtils utils;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
	}

	private void mapStructSetup(ProcessingEnvironment processingEnv, TypeElement typeElement) {
		utils = new AnnotationProcessorUtils(processingEnv, typeElement);
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
			ExecutableElement method = (ExecutableElement) element;
			TypeElement type = (TypeElement) method.getEnclosingElement();
			ScruseBlueprint blueprint = blueprint(type, true);
			blueprint.methods().add(new ScruseMethod(FullyQualifiedClassName.of(type), method.getSimpleName().toString(), method, ScruseMethod.Type.OUTPUT));
		});
		roundEnv.getElementsAnnotatedWith(JsonInput.class).forEach(element -> {
			ExecutableElement method = (ExecutableElement) element;
			TypeElement type = (TypeElement) method.getEnclosingElement();
			ScruseBlueprint blueprint = blueprint(type, true);
			blueprint.methods().add(new ScruseMethod(FullyQualifiedClassName.of(type), method.getSimpleName().toString(), method, ScruseMethod.Type.INPUT));
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
		for (ScruseMethod method : blueprint.methods()) {
			if (!method.methodElement().getModifiers().contains(Modifier.ABSTRACT)) {
				// method is implemented by user and can be used by us
				continue;
			}
			MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.methodElement().getSimpleName().toString())
				.addAnnotation(Override.class)
				.addModifiers(Modifier.PUBLIC)
				.returns(ClassName.get(method.methodElement().getReturnType()));
			method.methodElement().getParameters().forEach(param -> methodBuilder.addParameter(ClassName.get(param.asType()), param.getSimpleName().toString()));
			method.methodElement().getThrownTypes().forEach(type -> methodBuilder.addException(ClassName.get(type)));
			if (!method.methodElement().getTypeParameters().isEmpty()) {
				logError("Type parameters not yet supported", method.methodElement());
				continue;
			}
			Supplier<CodeBlock.Builder> codeGenerator = determineCodeGenerator(method);
			if (codeGenerator == null) {
				logError("Signature unknown. Please see @JsonOutput/@JsonInput for hints.", method.methodElement());
				continue;
			}
			try {
				methodBuilder.addCode(codeGenerator.get().build());
				classBuilder.addMethod(methodBuilder.build());
			} catch (Exception e) {
				logError(e.getMessage(), method.methodElement());
			}
		}
		JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(blueprint.className().fileName().replace("/", ".") + "Impl");
		try (Writer writer = sourceFile.openWriter()) {
			JavaFile file = JavaFile.builder(blueprint.className().packageName(), classBuilder.build()).build();
			file.writeTo(writer);
		}
	}

	private Supplier<CodeBlock.Builder> determineCodeGenerator(ScruseMethod method) {
		if (method.type() == ScruseMethod.Type.OUTPUT) {
			return determineOutputCodeGenerator(method);
		}
		return determineInputCodeGenerator(method);
	}

	private Supplier<CodeBlock.Builder> determineOutputCodeGenerator(ScruseMethod method) {
		if (method.methodElement().getParameters().size() == 1) {
			if (method.methodElement().getReturnType().toString().equals("com.fasterxml.jackson.databind.JsonNode")) {
				return new JacksonJsonNodeWriterGenerator(utils, method.methodElement())::build;
			}
		} else if (method.methodElement().getParameters().size() == 2 && method.methodElement().getReturnType().getKind() == TypeKind.VOID) {
			VariableElement generatorVariable = method.methodElement().getParameters().get(1);
			if (generatorVariable.asType().toString().equals("com.fasterxml.jackson.core.JsonGenerator")) {
				return new JacksonJsonGeneratorWriterGenerator(utils, method.methodElement())::build;
			} else if (generatorVariable.asType().toString().equals("com.google.gson.stream.JsonWriter")) {
				return new GsonJsonWriterWriterGenerator(utils, method.methodElement())::build;
			}
		}
		return null;
	}

	private Supplier<CodeBlock.Builder> determineInputCodeGenerator(ScruseMethod method) {
		if (method.methodElement().getReturnType().getKind() == TypeKind.VOID) {
			return null;
		}
		if (method.methodElement().getParameters().size() == 1) {
			VariableElement parserVariable = method.methodElement().getParameters().get(0);
			if (parserVariable.asType().toString().equals("com.fasterxml.jackson.core.JsonParser")) {
				return new JacksonJsonParserReaderGenerator(utils, method.methodElement())::build;
			}
		}
		return null;
	}

	private void logError(String msg, Element element) {
		processingEnv.getMessager().printMessage(ERROR, msg, element);
	}

	ScruseBlueprint blueprint(TypeElement element, boolean toBeGenerated) {
		ScruseBlueprint blueprint = blueprints.computeIfAbsent(FullyQualifiedClassName.of(element),
			name -> new ScruseBlueprint(new AtomicBoolean(toBeGenerated), name, element, new ArrayList<>(), new ArrayList<>()));
		if (toBeGenerated) {
			blueprint.toBeGenerated().set(true);
		}
		return blueprint;
	}

}