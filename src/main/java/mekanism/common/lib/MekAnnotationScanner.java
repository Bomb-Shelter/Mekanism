package mekanism.common.lib;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.sync.dynamic.SyncMapper;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.reflections.Reflections;

public class MekAnnotationScanner {

    public static void collectScanData(String modId, String packageName) {
        Map<String, Class<?>> classNameCache = new Object2ObjectOpenHashMap<>();
        Map<BaseAnnotationScanner, ScanData> scanners = new Object2ObjectArrayMap<>();
        Map<ElementType, List<ScanData>> elementBasedScanData = new EnumMap<>(ElementType.class);
        addScanningSupport(scanners, elementBasedScanData, SyncMapper.INSTANCE);

        for (Map.Entry<BaseAnnotationScanner, ScanData> entry : scanners.entrySet()) {
            ScanData scannerData = entry.getValue();
            Map<Class<?>, List<AnnotationData<?>>> knownClasses = scannerData.knownClasses;
            if (!knownClasses.isEmpty()) {
                try {
                    Reflections reflections = new Reflections(packageName);

                    for (Entry<ElementType, Type[]> elementTypeEntry : entry.getKey().getSupportedTypes().entrySet()) {
                        var elementType = elementTypeEntry.getKey();
                        var types = elementTypeEntry.getValue();

                        for (Type type : types) {
                            Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) Class.forName(type.getClassName());

                            if (elementType == ElementType.TYPE) {
                                var annotated = reflections.getTypesAnnotatedWith(annotationClass);
                                for (Class<?> clazz : annotated) {
                                    knownClasses.computeIfAbsent(clazz, key -> new ArrayList<>())
                                        .add(new AnnotationData<>(type, elementType, Type.getType(clazz), null, clazz.getAnnotation(annotationClass)));
                                }
                            } else if (elementType == ElementType.FIELD) {
                                var annotated = reflections.getFieldsAnnotatedWith(annotationClass);
                                for (Field field : annotated) {
                                    knownClasses.computeIfAbsent(field.getDeclaringClass(), key -> new ArrayList<>())
                                        .add(new AnnotationData<>(type, elementType, Type.getType(field.getType()), field.getName(), field.getAnnotation(annotationClass)));
                                }
                            } else if (elementType == ElementType.METHOD) {
                                var annotated = reflections.getMethodsAnnotatedWith(annotationClass);
                                for (Method method : annotated) {
                                    knownClasses.computeIfAbsent(method.getDeclaringClass(), key -> new ArrayList<>())
                                        .add(new AnnotationData<>(type, elementType, Type.getType(method.getReturnType()), method.getName(), method.getAnnotation(annotationClass)));
                                }
                            }
                        }
                    }

                    entry.getKey().collectScanData(classNameCache, knownClasses);
                } catch (Throwable throwable) {
                    //Should never really happen unless something goes drastically wrong
                    Mekanism.logger.error("Failed to collect scan data", throwable);
                }
            }
        }
    }

    private static void addScanningSupport(Map<BaseAnnotationScanner, ScanData> scanners, Map<ElementType, List<ScanData>> elementBasedScanData,
          BaseAnnotationScanner... baseScanners) {
        for (BaseAnnotationScanner baseScanner : baseScanners) {
            if (baseScanner.isEnabled()) {
                ScanData scanData = new ScanData(baseScanner);
                scanners.put(baseScanner, scanData);
                for (ElementType elementType : scanData.supportedTypes.keySet()) {
                    elementBasedScanData.computeIfAbsent(elementType, type -> new ArrayList<>()).add(scanData);
                }
            }
        }
    }

    @Nullable
    private static Class<?> getClassForName(Map<String, Class<?>> classNameCache, String className) {
        if (classNameCache.containsKey(className)) {
            //Note: We have to check if it is contained, as we keep track of failed classes as null values
            return classNameCache.get(className);
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            Mekanism.logger.error("Failed to find class '{}'", className);
            clazz = null;
        } catch (NoClassDefFoundError e) {
            Mekanism.logger.error("Failed to load class '{}'", className);
            throw e;
        }
        classNameCache.put(className, clazz);
        return clazz;
    }

    private static class ScanData {

        private final Map<Class<?>, List<AnnotationData<?>>> knownClasses = new Object2ObjectOpenHashMap<>();
//        private final Set<IModFileInfo> modFileData = new HashSet<>();
        private final Map<ElementType, Type[]> supportedTypes;

        public ScanData(BaseAnnotationScanner scanner) {
            supportedTypes = scanner.getSupportedTypes();
        }
    }

    public abstract static class BaseAnnotationScanner {

        protected boolean isEnabled() {
            return true;
        }

        protected abstract Map<ElementType, Type[]> getSupportedTypes();

        protected abstract void collectScanData(Map<String, Class<?>> classNameCache, Map<Class<?>, List<AnnotationData<?>>> knownClasses);

        @Nullable
        protected static Field getField(Class<?> annotatedClass, String fieldName) {
            Field field;
            try {
                field = annotatedClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                Mekanism.logger.error("Failed to find field '{}' for class '{}'", fieldName, annotatedClass.getSimpleName());
                return null;
            }
            field.setAccessible(true);
            return field;
        }

        @Nullable
        protected static Method getMethod(Class<?> annotatedClass, String methodName, String methodDescriptor) {
            MethodType methodType;
            try {
                methodType = MethodType.fromMethodDescriptorString(methodDescriptor, annotatedClass.getClassLoader());
            } catch (IllegalArgumentException | TypeNotPresentException e) {
                Mekanism.logger.error("Failed to generate method type. {}", e.getMessage());
                return null;
            }
            Method method;
            try {
                method = annotatedClass.getDeclaredMethod(methodName, methodType.parameterList().toArray(new Class[0]));
            } catch (NoSuchMethodException e) {
                Mekanism.logger.error("Failed to find method '{}' with descriptor '{}' for class '{}'", methodName, methodDescriptor,
                      annotatedClass.getSimpleName());
                return null;
            }
            method.setAccessible(true);
            return method;
        }

        /**
         * Goes up the various parent classes until we find a cache that matches it and returns the reference to it.
         *
         * @apiNote This should only be used really for read only purposes as changing it will then also adjust the parent's data.
         */
        protected static <DATA> DATA getData(Map<Class<?>, DATA> map, Class<?> clazz, DATA empty) {
            Class<?> current = clazz;
            while (current.getSuperclass() != null) {
                current = current.getSuperclass();
                DATA superCache = map.get(current);
                if (superCache != null) {
                    //If we already have an overall cache for the super class, return a reference to it and break out of checking super classes
                    // We don't need to copy it as we only use it for readonly purposes
                    return superCache;
                }
                //Otherwise, continue going up to the root superclass
            }
            return empty;
        }

        /**
         * Gathers all info's into a list sorted by class name and adds any info from parent classes to it as well.
         */
        protected static <INFO> List<ClassBasedInfo<INFO>> combineWithParents(Map<Class<?>, List<INFO>> flatMap) {
            Map<Class<?>, List<INFO>> map = new Object2ObjectOpenHashMap<>();
            for (Entry<Class<?>, List<INFO>> entry : flatMap.entrySet()) {
                Class<?> clazz = entry.getKey();
                List<INFO> info = entry.getValue();
                Class<?> current = clazz;
                while (current.getSuperclass() != null) {
                    current = current.getSuperclass();
                    List<INFO> superInfo = map.get(current);
                    if (superInfo != null) {
                        //If we already have an overall cache for the super class, add from it and break out of checking super classes
                        info.addAll(superInfo);
                        break;
                    }
                    //Otherwise, continue building up the cache, collecting all the class names up to the root superclass
                    superInfo = flatMap.get(current);
                    if (superInfo != null) {
                        //If the map has the super class, grab the fields that correspond to it
                        //Note: We keep going here as it may have super classes higher up
                        info.addAll(superInfo);
                    }
                }
                map.put(clazz, info);
            }
            return map.entrySet().stream().map(entry -> new ClassBasedInfo<>(entry.getKey(), entry.getValue()))
                  .sorted(Comparator.comparing(ClassBasedInfo::className)).toList();
        }

        protected record ClassBasedInfo<INFO>(Class<?> clazz, String className, List<INFO> infoList) {

            public ClassBasedInfo(Class<?> clazz, List<INFO> infoList) {
                this(clazz, clazz.getName(), infoList);
            }
        }
    }
}