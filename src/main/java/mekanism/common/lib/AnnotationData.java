package mekanism.common.lib;

import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.Map;

public record AnnotationData<T extends Annotation>(Type annotationType, ElementType targetType, Type clazz, String memberName, T annotationData) {}