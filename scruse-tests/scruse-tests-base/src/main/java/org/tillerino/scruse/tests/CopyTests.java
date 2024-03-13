package org.tillerino.scruse.tests;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.configuration.DefaultConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration.ConfigOption;
import com.github.javaparser.printer.configuration.Indentation;
import com.github.javaparser.printer.configuration.Indentation.IndentType;
import com.github.javaparser.printer.configuration.PrinterConfiguration;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * We only write all tests for {@link com.fasterxml.jackson.core.JsonGenerator} and {@link com.fasterxml.jackson.core.JsonParser}.
 * These tests are then translated for other libraries.
 */
public class CopyTests {
	private static final String originalPackage = "org.tillerino.scruse.tests.base";
	private static final PrinterConfiguration configuration = new DefaultPrinterConfiguration()
		.addOption(new DefaultConfigurationOption(ConfigOption.INDENTATION, new Indentation(IndentType.TABS, 1)));
	private static final DefaultPrettyPrinter printer = new DefaultPrettyPrinter(configuration);

	public static void main(String[] args) throws Exception {
		Path targetRoot = Paths.get(args[0]);
		String targetPackage = args[1];
		Class<?> writer = Class.forName(args[2]);
		Class<?> reader = Class.forName(args[3]);
		WriterMode writerMode = WriterMode.valueOf(args[4]);
		copy(targetRoot, targetPackage, writer, reader, Map.of(), writerMode);
	}

	/**
	 * Copies all the original tests to a new package, replacing the writer and reader classes.
	 *
	 * @param targetRoot if null, same as original
	 * @param targetPackage e.g. "org.tillerino.scruse.tests.alt.gson"
	 * @param writer class of the writer, e.g. {@link JsonWriter}
	 * @param reader class of the reader, e.g. {@link JsonReader}
	 * @param methodReplacements replacements for the methods in InputUtils and OutputUtils
	 * @param writerMode see {@link WriterMode}
	 */
	public static void copy(Path targetRoot, String targetPackage, Class<?> writer, Class<?> reader, Map<String, String> methodReplacements, WriterMode writerMode) throws IOException {
		for (String p : List.of("src/main/java", "src/test/java")) {
			SourceRoot sourceRoot = new SourceRoot(targetRoot.resolve("../scruse-tests-jackson/" + p));
			sourceRoot.setParserConfiguration(new ParserConfiguration()
				.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17));

			for (ParseResult<CompilationUnit> parseResult : sourceRoot.tryToParse(originalPackage)) {
				CompilationUnit cu = parseResult.getResult().orElseThrow(() -> new RuntimeException("failed to parse" + parseResult.getProblems()));

				AtomicReference<String> packageBefore = new AtomicReference<>();

				cu.accept(new ModifierVisitor<Void>() {
					@Override
					public Visitable visit(ClassOrInterfaceDeclaration n, Void arg) {
						if (packageBefore.get() != null) {
							n.setComment(new BlockComment(" CARE! This file was copied from " + packageBefore.get() + ".\n" +
								"   This was performed by " + CopyTests.class.getCanonicalName() + ".\n" +
								"   Do not modify by hand. Run CopyTests to update this file. "));
						}
						return super.visit(n, arg);
					}

					@Override
					public Visitable visit(PackageDeclaration n, Void arg) {
						packageBefore.set(n.getNameAsString());
						n.setName(n.getName().asString().replace(originalPackage, targetPackage));
						cu.setStorage(fileName(targetRoot != null ? targetRoot.resolve(p) : sourceRoot.getRoot(), n.getNameAsString(), cu.getPrimaryTypeName().get()));
						return super.visit(n, arg);
					}

					@Override
					public Node visit(ImportDeclaration n, Void arg) {
						if (n.getNameAsString().equals("com.fasterxml.jackson.core.JsonGenerator")) {
							n.setName(writer.getCanonicalName());
						} else if (n.getNameAsString().equals("com.fasterxml.jackson.core.JsonParser")) {
							n.setName(reader.getCanonicalName());
						} else {
							n.setName(n.getName().asString().replace(originalPackage, targetPackage));
							// for static imports
							methodReplacements.forEach((o, r) -> n.setName(n.getName().asString().replace(o, r)));
						}
						return super.visit(n, arg);
					}

					@Override
					public Visitable visit(MethodCallExpr n, Void arg) {
						String replacement = methodReplacements.get(n.getNameAsString());
						if (replacement != null) {
							n.setName(replacement);
						}
						return super.visit(n, arg);
					}

					@Override
					public Visitable visit(MethodDeclaration n, Void arg) {
						if (n.getAnnotationByClass(JsonInput.class).isPresent()) {
							for (Parameter parameter : n.getParameters()) {
								if (parameter.getType().toString().equals("JsonParser")) {
									parameter.setType(reader);
								}
							}
						}

						if (n.getAnnotationByClass(JsonOutput.class).isPresent()) {
							if (writerMode == WriterMode.RETURN) {
								n.setType(writer);
								NodeList<Parameter> parameters = n.getParameters();
								for (int i = 0; i < parameters.size(); i++) {
									Parameter parameter = parameters.get(i);
									if (parameter.getType().toString().equals("JsonGenerator")) {
										parameters.remove(i);
										break;
									}
								}
							} else {
								for (Parameter parameter : n.getParameters()) {
									if (parameter.getType().toString().equals("JsonGenerator")) {
										parameter.setType(writer);
									}
								}
							}
						}
						return super.visit(n, arg);
					}
				}, null);
				cu.getStorage().get().save(printer::print);
				System.out.println("Wrote test copy " + cu.getStorage().get().getPath());
			}
		}
	}

	static Path fileName(Path targetRoot, String targetPackage, String className) {
		for (String s : targetPackage.split("\\.")) {
			targetRoot = targetRoot.resolve(s);
		}
		return targetRoot.resolve(className + ".java");
	}

	enum WriterMode {
		ARGUMENT,
		RETURN
	}
}
