package org.tillerino.scruse.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName;
import org.tillerino.scruse.processor.apis.AbstractWriterCodeGenerator;
import org.tillerino.scruse.processor.apis.GsonJsonWriterCodeGenerator;
import org.tillerino.scruse.processor.apis.JacksonJsonGeneratorCodeGenerator;
import org.tillerino.scruse.processor.apis.JacksonJsonNodeCodeGenerator;

import javax.annotation.processing.*;
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

import static javax.tools.Diagnostic.Kind.ERROR;

@SupportedAnnotationTypes({
	"org.tillerino.scruse.annotations.JsonOutput",
	"org.tillerino.scruse.annotations.JsonInput",
	"org.tillerino.scruse.annotations.JsonConfig",
})
@SupportedSourceVersion(javax.lang.model.SourceVersion.RELEASE_8)
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
			generateCode(roundEnv);
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
			ExecutableElement method = (ExecutableElement) element;
			TypeElement type = (TypeElement) method.getEnclosingElement();
			mapStructSetup(processingEnv, type);
			ScruseBlueprint blueprint = blueprint(type, true);
			blueprint.methods().add(new ScruseMethod(FullyQualifiedClassName.of(type), method.getSimpleName().toString(), method));
		});
	}

	private void generateCode(RoundEnvironment roundEnv) {
		for (ScruseBlueprint blueprint : blueprints.values()) {
			if (blueprint.toBeGenerated().get()) {
				try {
					generateCode(blueprint, roundEnv);
				} catch (IOException e) {
					e.printStackTrace();
					processingEnv.getMessager().printMessage(ERROR, e.getMessage());
				}
			}
		}
	}

	private void generateCode(ScruseBlueprint blueprint, RoundEnvironment roundEnv) throws IOException {
		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(blueprint.className().nameInCompilationUnit() + "Impl")
			.addModifiers(Modifier.PUBLIC)
			.addSuperinterface(blueprint.typeMirror());
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
			AbstractWriterCodeGenerator<?> codeGenerator = null;
			if (method.methodElement().getParameters().size() == 1) {
				if (method.methodElement().getReturnType().toString().equals("com.fasterxml.jackson.databind.JsonNode")) {
					codeGenerator = new JacksonJsonNodeCodeGenerator(utils, method.methodElement());
				}
			} else if (method.methodElement().getParameters().size() == 2 && method.methodElement().getReturnType().getKind() == TypeKind.VOID) {
				VariableElement generatorVariable = method.methodElement().getParameters().get(1);
				if (generatorVariable.asType().toString().equals("com.fasterxml.jackson.core.JsonGenerator")) {
					codeGenerator = new JacksonJsonGeneratorCodeGenerator(utils, method.methodElement());
				} else if (generatorVariable.asType().toString().equals("com.google.gson.stream.JsonWriter")) {
					codeGenerator = new GsonJsonWriterCodeGenerator(utils, method.methodElement());
				}
			}
			if (codeGenerator == null) {
				logError("Signature unknown. Please see @JsonOutput for hints.", method.methodElement());
				continue;
			}
			try {
				methodBuilder.addCode(codeGenerator.build().build());
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

	private void logError(String msg, Element element) {
		processingEnv.getMessager().printMessage(ERROR, msg, element);
	}

	ScruseBlueprint blueprint(TypeElement element, boolean toBeGenerated) {
		ScruseBlueprint blueprint = blueprints.computeIfAbsent(FullyQualifiedClassName.of(element),
			name -> new ScruseBlueprint(new AtomicBoolean(toBeGenerated), name, element.asType(), new ArrayList<>(), new ArrayList<>()));
		if (toBeGenerated) {
			blueprint.toBeGenerated().set(true);
		}
		return blueprint;
	}

}