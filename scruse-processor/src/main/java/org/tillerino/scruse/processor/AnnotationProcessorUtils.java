package org.tillerino.scruse.processor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleAnnotationValueVisitor14;
import javax.lang.model.util.Types;
import org.mapstruct.ap.internal.gem.ReportingPolicyGem;
import org.mapstruct.ap.internal.model.common.Type;
import org.mapstruct.ap.internal.model.common.TypeFactory;
import org.mapstruct.ap.internal.option.Options;
import org.mapstruct.ap.internal.processor.DefaultModelElementProcessorContext;
import org.mapstruct.ap.internal.util.AnnotationProcessorContext;
import org.mapstruct.ap.internal.util.RoundContext;
import org.tillerino.scruse.api.DeserializationContext;
import org.tillerino.scruse.api.SerializationContext;
import org.tillerino.scruse.processor.features.*;
import org.tillerino.scruse.processor.util.Annotations;

public class AnnotationProcessorUtils {
    public final Elements elements;
    public final Types types;
    public final CommonTypes commonTypes;
    public final TypeFactory tf;
    public final Delegation delegation;
    public final Generics generics;
    public final Converters converters;
    public final DefaultValues defaultValues;
    public final Templates templates;
    public final Map<String, ScruseBlueprint> blueprints = new LinkedHashMap<>();
    public final Annotations annotations;
    public final Messager messager;

    public AnnotationProcessorUtils(ProcessingEnvironment processingEnv, TypeElement typeElement) {
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        commonTypes = new CommonTypes();
        delegation = new Delegation(this);
        generics = new Generics(this);
        annotations = new Annotations(this);
        converters = new Converters(this);
        defaultValues = new DefaultValues(this);
        templates = new Templates(this);
        messager = processingEnv.getMessager();

        AnnotationProcessorContext apc = new AnnotationProcessorContext(
                processingEnv.getElementUtils(),
                processingEnv.getTypeUtils(),
                processingEnv.getMessager(),
                false,
                false);
        RoundContext rc = new RoundContext(apc);
        DefaultModelElementProcessorContext dmepc = new DefaultModelElementProcessorContext(
                processingEnv,
                new Options(
                        false,
                        false,
                        ReportingPolicyGem.ERROR,
                        ReportingPolicyGem.ERROR,
                        "what",
                        "what",
                        false,
                        false,
                        false),
                rc,
                Map.of(),
                typeElement);
        tf = new TypeFactory(dmepc.getElementUtils(), dmepc.getTypeUtils(), dmepc.getMessager(), rc, Map.of(), false);
    }

    public static boolean isArrayOf(Type type, TypeKind kind) {
        return type.isArrayType() && type.getComponentType().getTypeMirror().getKind() == kind;
    }

    public static class GetAnnotationValues<R, P> extends SimpleAnnotationValueVisitor14<R, P> {
        @Override
        public R visitArray(List<? extends AnnotationValue> vals, P o) {
            vals.forEach(val -> val.accept(this, o));
            return null;
        }
    }

    public boolean isBoxed(TypeMirror type) {
        return commonTypes.boxedTypes.contains(type.toString());
    }

    public ScruseBlueprint blueprint(TypeElement element) {
        // cannot use computeIfAbsent because this can recurse
        ScruseBlueprint blueprint = blueprints.get(element.getQualifiedName().toString());
        if (blueprint == null) {
            blueprints.put(element.getQualifiedName().toString(), blueprint = ScruseBlueprint.of(element, this));
        }
        return blueprint;
    }

    public class CommonTypes {
        public final TypeMirror string =
                elements.getTypeElement(String.class.getName()).asType();

        public final TypeMirror boxedBoolean =
                elements.getTypeElement(Boolean.class.getName()).asType();
        public final TypeMirror boxedByte =
                elements.getTypeElement(Byte.class.getName()).asType();
        public final TypeMirror boxedShort =
                elements.getTypeElement(Short.class.getName()).asType();
        public final TypeMirror boxedInt =
                elements.getTypeElement(Integer.class.getName()).asType();
        public final TypeMirror boxedLong =
                elements.getTypeElement(Long.class.getName()).asType();
        public final TypeMirror boxedFloat =
                elements.getTypeElement(Float.class.getName()).asType();
        public final TypeMirror boxedDouble =
                elements.getTypeElement(Double.class.getName()).asType();
        public final TypeMirror boxedChar =
                elements.getTypeElement(Character.class.getName()).asType();
        public final TypeMirror object =
                elements.getTypeElement(Object.class.getName()).asType();
        public final TypeMirror serializationContext =
                elements.getTypeElement(SerializationContext.class.getName()).asType();
        public final TypeMirror deserializationContext =
                elements.getTypeElement(DeserializationContext.class.getName()).asType();

        public final Set<String> boxedTypes = Set.of(
                boxedBoolean.toString(),
                boxedByte.toString(),
                boxedShort.toString(),
                boxedInt.toString(),
                boxedLong.toString(),
                boxedFloat.toString(),
                boxedDouble.toString(),
                boxedChar.toString());
    }
}
