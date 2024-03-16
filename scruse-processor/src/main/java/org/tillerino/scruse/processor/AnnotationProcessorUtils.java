package org.tillerino.scruse.processor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mapstruct.ap.internal.gem.ReportingPolicyGem;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.model.common.TypeFactory;
import org.mapstruct.ap.internal.option.Options;
import org.mapstruct.ap.internal.processor.DefaultModelElementProcessorContext;
import org.mapstruct.ap.internal.util.AnnotationProcessorContext;
import org.mapstruct.ap.internal.util.RoundContext;
import org.tillerino.scruse.api.DeserializationContext;
import org.tillerino.scruse.api.SerializationContext;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor14;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnnotationProcessorUtils {
	public final Elements elements;
	public final Types types;
	public final CommonTypes commonTypes;
	public final TypeFactory tf;
	public final PrototypeFinder prototypeFinder;

	public AnnotationProcessorUtils(ProcessingEnvironment processingEnv, TypeElement typeElement, PrototypeFinder prototypeFinder) {
		elements = processingEnv.getElementUtils();
		types = processingEnv.getTypeUtils();
		commonTypes = new CommonTypes();
		this.prototypeFinder = prototypeFinder;

		AnnotationProcessorContext apc = new AnnotationProcessorContext(processingEnv.getElementUtils(),
			processingEnv.getTypeUtils(), processingEnv.getMessager(), false, false);
		RoundContext rc = new RoundContext(apc);
		DefaultModelElementProcessorContext dmepc = new DefaultModelElementProcessorContext(processingEnv,
			new Options(false,
				false,
				ReportingPolicyGem.ERROR,
				ReportingPolicyGem.ERROR,
				"what",
				"what",
				false,

				false,
				false), rc, Map.of(),
			typeElement);
		tf = new TypeFactory(dmepc.getElementUtils(), dmepc.getTypeUtils(), dmepc.getMessager(), rc, Map.of(), false);
	}

	public static boolean isArrayOf(Type type, TypeKind kind) {
		return type.isArrayType() && type.getComponentType().getTypeMirror().getKind() == kind;
	}

	public ArrayList<TypeElement> getTypeElementsFromAnnotationValue(AnnotationValue value) {
		ArrayList<TypeElement> elements = new ArrayList<>();
		value.accept(new SimpleAnnotationValueVisitor14<Object, List<TypeElement>>() {
			@Override
			public Object visitArray(List<? extends AnnotationValue> vals, List<TypeElement> o) {
				vals.forEach(val -> val.accept(this, o));
				return null;
			}

			@Override
			public Object visitType(TypeMirror t, List<TypeElement> o) {
				o.add(AnnotationProcessorUtils.this.elements.getTypeElement(t.toString()));
				return null;
			}
		}, elements);
		return elements;
	}

	boolean isJsonIgnore(List<? extends AnnotationMirror> annotationMirrors) {
		return annotationMirrors.stream().anyMatch(a -> types.isSameType(a.getAnnotationType(), commonTypes.jsonIgnore)
			&& a.getElementValues().entrySet().stream().anyMatch(e ->
			e.getKey().getSimpleName().toString().equals("value") && e.getValue().getValue().equals("true")
		));
	}

	public boolean isBoxed(TypeMirror type) {
		return commonTypes.boxedTypes.contains(type.toString());
	}

	public class CommonTypes {
		public final TypeMirror string = elements.getTypeElement(String.class.getName()).asType();
		public final TypeMirror jsonIgnore = elements.getTypeElement(JsonIgnore.class.getName()).asType();

		public final TypeMirror boxedBoolean = elements.getTypeElement(Boolean.class.getName()).asType();
		public final TypeMirror boxedByte = elements.getTypeElement(Byte.class.getName()).asType();
		public final TypeMirror boxedShort = elements.getTypeElement(Short.class.getName()).asType();
		public final TypeMirror boxedInt = elements.getTypeElement(Integer.class.getName()).asType();
		public final TypeMirror boxedLong = elements.getTypeElement(Long.class.getName()).asType();
		public final TypeMirror boxedFloat = elements.getTypeElement(Float.class.getName()).asType();
		public final TypeMirror boxedDouble = elements.getTypeElement(Double.class.getName()).asType();
		public final TypeMirror boxedChar = elements.getTypeElement(Character.class.getName()).asType();
		public final TypeMirror object = elements.getTypeElement(Object.class.getName()).asType();
		public final TypeMirror serializationContext = elements.getTypeElement(SerializationContext.class.getName()).asType();
		public final TypeMirror deserializationContext = elements.getTypeElement(DeserializationContext.class.getName()).asType();

		public final Set<String> boxedTypes = Set.of(
			boxedBoolean.toString(),
			boxedByte.toString(),
			boxedShort.toString(),
			boxedInt.toString(),
			boxedLong.toString(),
			boxedFloat.toString(),
			boxedDouble.toString(),
			boxedChar.toString()
		);
	}
}
