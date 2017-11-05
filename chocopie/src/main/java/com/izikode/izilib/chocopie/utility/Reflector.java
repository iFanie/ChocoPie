package com.izikode.izilib.chocopie.utility;

/*
 * Copyright 2017 Fanie Veizis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import android.support.annotation.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Reflector {

    public static class ReflectorException extends Exception {
        public ReflectorException() {
            super();
        }

        public ReflectorException(@NonNull String message) {
            super(message);
        }

        public ReflectorException(@NonNull Throwable throwable) {
            super(throwable);
        }

        public ReflectorException(@NonNull String message, @NonNull Throwable throwable) {
            super(message, throwable);
        }
    }

    public static <R> Class getMatchingSuperclass(Object instance, Class<R> matchingClass) throws ReflectorException {
        Class objectClass = instance.getClass();
        while (objectClass != null && !matchingClass.equals(objectClass)) {
            objectClass = objectClass.getSuperclass();
        }

        if (objectClass != null) {
            return objectClass;
        } else {
            throw new ReflectorException(new NoClassDefFoundError("No matching superclass found."));
        }
    }

    public static <R> Field locateFieldInSuperclass(Object instance, Class<R> superClass, String fieldName) throws ReflectorException, NoSuchFieldException {
        Class superclass = getMatchingSuperclass(instance, superClass);
        Field field = superclass.getDeclaredField(fieldName);

        if (field != null) {
            return field;
        } else {
            throw new ReflectorException(new NoSuchFieldException("No matching field found."));
        }
    }

    public static <R> Method locateMethodInSuperclass(Object instance, Class<R> superType, String methodName) throws ReflectorException, NoSuchMethodException {
        Class superclass = getMatchingSuperclass(instance, superType);
        Method method = superclass.getDeclaredMethod(methodName);

        if (method != null) {
            return method;
        } else {
            throw new ReflectorException(new NoSuchFieldException("No matching method found."));
        }
    }

    public static void setValue(Object instance, Field field, Object value) throws ReflectorException {
        boolean originalAccessibility = field.isAccessible();
        field.setAccessible(true);

        try {
            field.set(instance, value != null ? field.getType().cast(value) : null);
        } catch (IllegalAccessException e) {
            throw new ReflectorException("Unable to manipulate field accessibility.", e);
        } finally {
            field.setAccessible(originalAccessibility);
        }
    }

    public static Object readValue(Object instance, Field field) throws ReflectorException {
        if (instance == null) {
            return null;
        }

        boolean originalAccessibility = field.isAccessible();
        field.setAccessible(true);

        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new ReflectorException("Unable to manipulate field accessibility.", e);
        } finally {
            field.setAccessible(originalAccessibility);
        }
    }

    public static Object getValue(Object object, Method method, Object... params) throws ReflectorException, IllegalAccessException {
        boolean originalAccessibility = method.isAccessible();
        method.setAccessible(true);

        try {
            Object value = null;

            if (params != null && params.length > 0) {
                value = method.invoke(object, params);
            } else {
                value = method.invoke(object);
            }

            return value;
        } catch (InvocationTargetException e) {
            throw new ReflectorException("Unable to invoke selected method.", e);
        } finally {
            method.setAccessible(originalAccessibility);
        }
    }

    public static <R extends Annotation> Field[] getAnnotatedFields(Object instance, Class<R> annotationClass) {
        List<Field> fields = new ArrayList<>();

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationClass)) {
                fields.add(field);
            }
        }

        return fields.toArray(new Field[fields.size()]);
    }

    public static <R extends Annotation> Field[] getNonAnnotatedFields(Object instance, Class<R> annotationClass) {
        List<Field> fields = new ArrayList<>();

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(annotationClass)) {
                fields.add(field);
            }
        }

        return fields.toArray(new Field[fields.size()]);
    }

    public static List<Field> getDeclaredFieldsByType(Object instance, Class type) {
        List<Field> fields = new ArrayList<>();

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (type.isAssignableFrom(field.getType())) {
                fields.add(field);
            }
        }

        return fields;
    }

    public static Field[] getDeclaredFieldsArrayByType(Object instance, Class type) {
        List<Field> fields = getDeclaredFieldsByType(instance, type);
        return fields.toArray(new Field[fields.size()]);
    }

    public static Object getFieldValue(Object instance, String fieldName) throws ReflectorException, NoSuchFieldException {
        Field field = instance.getClass().getField(fieldName);
        return Reflector.readValue(instance, field);
    }

    public static Object getMethodExecutionValue(Object instance, String methodName, Object... params) throws IllegalAccessException, ReflectorException, NoSuchMethodException {
        Method method = instance.getClass().getMethod(methodName);
        return Reflector.getValue(instance, method, params);
    }
}
