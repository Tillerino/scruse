package org.tillerino.scruse.tests;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;
import net.bytebuddy.implementation.MethodCall;
import org.apache.commons.lang3.function.Failable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeAssertions {
	private static final Map<String, CompileUnitAssert> compilationUnitCache = new LinkedHashMap<>();

	public static CompileUnitAssert assertThatCode(Class<?> clazz) throws Exception {
		if (!compilationUnitCache.containsKey(clazz.getCanonicalName())) {
			compilationUnitCache.put(clazz.getCanonicalName(), parseClass(clazz));
		}
		return compilationUnitCache.get(clazz.getCanonicalName());
	}

	private static CompileUnitAssert parseClass(Class<?> clazz) throws Exception {
		SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(clazz)
			.resolve("target/generated-sources/annotations"));
		sourceRoot.setParserConfiguration(new ParserConfiguration()
			.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_8));
		ParseResult<CompilationUnit> result = sourceRoot.tryToParse(clazz.getPackageName(), clazz.getSimpleName() + ".java");
		CompilationUnit compilationUnit = result.getResult().orElseGet(() -> {
			throw Failable.rethrow(result.getProblem(0).getCause().get());
		});
		CompilationUnit.Storage storage = compilationUnit.getStorage()
			.orElseThrow(() -> new AssertionError("No storage in " + clazz.getCanonicalName()));
		TypeDeclaration<?> primaryType = compilationUnit.getPrimaryType()
			.orElseThrow(() -> new AssertionError("No primary type in " + storage.getFileName()));
		return new CompileUnitAssert(compilationUnit, primaryType, storage);
	}

	public record CompileUnitAssert(CompilationUnit cu, TypeDeclaration<?> primaryType, CompilationUnit.Storage storage) {
		public MethodAssert method(String methodName) {
			List<MethodDeclaration> methods = primaryType.getMethodsByName(methodName);
			assertThat(methods).as("Methods named %s in %s", methodName, storage.getFileName()).hasSize(1);
			return new MethodAssert(this, methods.get(0));
		}
	}

	public record MethodAssert(CompileUnitAssert cu, MethodDeclaration decl) {
		public void calls(String name) {
			BlockStmt blockStmt = decl.getBody().orElseThrow(() -> new AssertionError("No body in " + decl.getNameAsString()));
			assertThat(blockStmt.findAll(MethodCallExpr.class))
				.extracting(MethodCallExpr::getNameAsString)
				.contains(name);
		}
	}
}
