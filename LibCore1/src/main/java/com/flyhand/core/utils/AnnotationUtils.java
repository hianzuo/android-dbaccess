package com.flyhand.core.utils;

import java.lang.annotation.Annotation;

/**
 * @author Ryan
 * @date 2018/2/24.
 */
public class AnnotationUtils {
    public static <T> T getDeclaredAnnotation(Class<?> clz, Class<? extends T> annotationClass) {
        Annotation[] declaredAnnotations = clz.getDeclaredAnnotations();
        if (null != declaredAnnotations) {
            for (Annotation declaredAnnotation : declaredAnnotations) {
                if (declaredAnnotation.annotationType() == annotationClass) {
                    return (T) declaredAnnotation;
                }
            }
        }
        return null;
    }
}
