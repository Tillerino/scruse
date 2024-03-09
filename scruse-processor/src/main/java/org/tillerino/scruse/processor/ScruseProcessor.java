package org.tillerino.scruse.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
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
			ScruseBlueprint blueprint = blueprint(type, true);
			mapStructSetup(processingEnv, type);
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
			blueprint.methods().add(new ScruseMethod(blueprint, method.getSimpleName().toString(), method, ScruseMethod.InputOutput.OUTPUT, utils));
		});
		roundEnv.getElementsAnnotatedWith(JsonInput.class).forEach(element -> {
			ExecutableElement method = (ExecutableElement) element;
			TypeElement type = (TypeElement) method.getEnclosingElement();
			mapStructSetup(processingEnv, type);
			ScruseBlueprint blueprint = blueprint(type, true);
			blueprint.methods().add(new ScruseMethod(blueprint, method.getSimpleName().toString(), method, ScruseMethod.InputOutput.INPUT, utils));
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
		GeneratedClass generatedClass = new GeneratedClass(classBuilder);
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
			Supplier<CodeBlock.Builder> codeGenerator = switch (method.type()) {
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
		if (method.parametersWithoutContext().size() == 1) {
			if (method.methodElement().getReturnType().toString().equals("com.fasterxml.jackson.databind.JsonNode")) {
				return new JacksonJsonNodeWriterGenerator(utils, method, generatedClass)::build;
			}
		} else if (method.parametersWithoutContext().size() == 2 && method.methodElement().getReturnType().getKind() == TypeKind.VOID) {
			VariableElement generatorVariable = method.methodElement().getParameters().get(1);
			if (generatorVariable.asType().toString().equals("com.fasterxml.jackson.core.JsonGenerator")) {
				return new JacksonJsonGeneratorWriterGenerator(utils, method, generatedClass)::build;
			} else if (generatorVariable.asType().toString().equals("com.google.gson.stream.JsonWriter")) {
				return new GsonJsonWriterWriterGenerator(utils, method, generatedClass)::build;
			}
		}
		return null;
	}

	private Supplier<CodeBlock.Builder> determineInputCodeGenerator(ScruseMethod method, GeneratedClass generatedClass) {
		if (method.methodElement().getReturnType().getKind() == TypeKind.VOID) {
			return null;
		}
		if (method.parametersWithoutContext().size() == 1) {
			VariableElement parserVariable = method.methodElement().getParameters().get(0);
			if (parserVariable.asType().toString().equals("com.fasterxml.jackson.core.JsonParser")) {
				return new JacksonJsonParserReaderGenerator(utils, method, generatedClass)::build;
			}
			if (parserVariable.asType().toString().equals("com.fasterxml.jackson.databind.JsonNode")) {
				return new JacksonJsonNodeReaderGenerator(utils, method, generatedClass)::build;
			}
			if (parserVariable.asType().toString().equals("com.google.gson.stream.JsonReader")) {
				return new GsonJsonReaderReaderGenerator(utils, method, generatedClass)::build;
			}
		}
		return null;
	}

	private void logError(String msg, Element element) {
		processingEnv.getMessager().printMessage(ERROR, msg, element);
	}

	ScruseBlueprint blueprint(TypeElement element, boolean toBeGenerated) {
		ScruseBlueprint blueprint = blueprints.computeIfAbsent(element.getQualifiedName().toString(),
			name -> new ScruseBlueprint(new AtomicBoolean(toBeGenerated), FullyQualifiedClassName.of(element), element, new ArrayList<>(), new ArrayList<>()));
		if (toBeGenerated) {
			blueprint.toBeGenerated().set(true);
		}
		return blueprint;
	}

}