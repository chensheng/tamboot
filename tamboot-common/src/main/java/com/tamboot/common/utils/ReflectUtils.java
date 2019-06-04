package com.tamboot.common.utils;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.*;

public class ReflectUtils {
	
	public static Class<?> findGenericType(Object object, Class<?> parameterizedSuperclass, String typeParamName) {
		if (object == null) {
			return null;
		}
	
		Class<?> thisClass = object.getClass();
        Class<?> currentClass = thisClass;
        for (;;) {
            if (currentClass.getSuperclass() == parameterizedSuperclass) {
                int typeParamIndex = -1;
                TypeVariable<?>[] typeParams = currentClass.getSuperclass().getTypeParameters();
                for (int i = 0; i < typeParams.length; i ++) {
                    if (typeParamName.equals(typeParams[i].getName())) {
                        typeParamIndex = i;
                        break;
                    }
                }

                if (typeParamIndex < 0) {
                    throw new IllegalStateException(
                            "unknown type parameter '" + typeParamName + "': " + parameterizedSuperclass);
                }

                Type genericSuperType = currentClass.getGenericSuperclass();
                if (!(genericSuperType instanceof ParameterizedType)) {
                    return Object.class;
                }

                Type[] actualTypeParams = ((ParameterizedType) genericSuperType).getActualTypeArguments();

                Type actualTypeParam = actualTypeParams[typeParamIndex];
                if (actualTypeParam instanceof ParameterizedType) {
                    actualTypeParam = ((ParameterizedType) actualTypeParam).getRawType();
                }
                if (actualTypeParam instanceof Class) {
                    return (Class<?>) actualTypeParam;
                }
                if (actualTypeParam instanceof GenericArrayType) {
                    Type componentType = ((GenericArrayType) actualTypeParam).getGenericComponentType();
                    if (componentType instanceof ParameterizedType) {
                        componentType = ((ParameterizedType) componentType).getRawType();
                    }
                    if (componentType instanceof Class) {
                        return Array.newInstance((Class<?>) componentType, 0).getClass();
                    }
                }
                if (actualTypeParam instanceof TypeVariable) {
                    // Resolved type parameter points to another type parameter.
                    TypeVariable<?> v = (TypeVariable<?>) actualTypeParam;
                    currentClass = thisClass;
                    if (!(v.getGenericDeclaration() instanceof Class)) {
                        return Object.class;
                    }

                    parameterizedSuperclass = (Class<?>) v.getGenericDeclaration();
                    typeParamName = v.getName();
                    if (parameterizedSuperclass.isAssignableFrom(thisClass)) {
                        continue;
                    } else {
                        return Object.class;
                    }
                }

                return null;
            }
            currentClass = currentClass.getSuperclass();
            if (currentClass == null) {
                return null;
            }
        }
    }
	
	private static final String SETTER_PREFIX = "set";

	private static final String GETTER_PREFIX = "get";

	private static final String CGLIB_CLASS_SEPARATOR = "$$";

	public static Class<?> getClass(Object instance) {
		Class<?> clazz = instance.getClass();
		if ((clazz != null) && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
			Class<?> superClass = clazz.getSuperclass();
			if ((superClass != null) && !Object.class.equals(superClass)) {
				return superClass;
			}
		}
		return clazz;
	}

	public static Class<?> getClass(String className) {
		Validate.notNull(className, "className must not be null");
		try {
			Class<?> clazz = Class.forName(className);
			if ((clazz != null) && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
				Class<?> superClass = clazz.getSuperclass();
				if ((superClass != null) && !Object.class.equals(superClass)) {
					return superClass;
				}
			}
			return clazz;
		} catch (ClassNotFoundException e) {
			throw ExceptionUtils.unchecked(e);
		}
	}

	public static Object newInstanceSimpleClass(String className) {
		Class<?> clazz = getClass(className);
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw ExceptionUtils.unchecked(e);
		} catch (IllegalAccessException e) {
			throw ExceptionUtils.unchecked(e);
		}
	}

	public static Object newInstanceInnerClass(String outerClassName, String innerClassName) {
		Class<?> outerClass = getClass(outerClassName);
		Class<?> innerClass = getClass(innerClassName);
		Constructor<?>[] innerCons = innerClass.getDeclaredConstructors();
		try {
			return innerCons[0].newInstance(outerClass.newInstance());
		} catch (IllegalArgumentException e) {
			throw ExceptionUtils.unchecked(e);
		} catch (InstantiationException e) {
			throw ExceptionUtils.unchecked(e);
		} catch (IllegalAccessException e) {
			throw ExceptionUtils.unchecked(e);
		} catch (InvocationTargetException e) {
			throw ExceptionUtils.unchecked(e);
		}
	}

	public static Object invokeGetter(Object obj, String propertyName) {
		String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(propertyName);
		return invokeMethod(obj, getterMethodName, new Class[] {}, new Object[] {});
	}

	public static void invokeSetter(Object obj, String propertyName, Object value) {
		String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(propertyName);
		invokeMethodByName(obj, setterMethodName, new Object[] { value });
	}

	public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
			final Object[] args) {
		Method method = getAccessibleMethod(obj, methodName, parameterTypes);
		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
		Method method = getAccessibleMethodByName(obj, methodName);
		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	public static Object getFieldValue(final Object obj, final String fieldName) {
		Field field = getAccessibleField(obj, fieldName);
		Object result = null;
		try {
			result = field.get(obj);
		} catch (IllegalAccessException e) {
			throw ExceptionUtils.unchecked(e);
		}
		return result;
	}

	public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
		Field field = getAccessibleField(obj, fieldName);
		try {
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			throw ExceptionUtils.unchecked(e);
		}
	}

	public static Field getAccessibleField(final Object obj, final String fieldName) {
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				Field field = superClass.getDeclaredField(fieldName);
				makeAccessible(field);
				return field;
			} catch (NoSuchFieldException e) {
				if (superClass == Object.class) {
					throw ExceptionUtils.unchecked(e);
				}
			}
		}
		return null;
	}

	public static Method getAccessibleMethod(final Object obj, final String methodName,
			final Class<?>... parameterTypes) {
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				Method method = superClass.getDeclaredMethod(methodName, parameterTypes);
				makeAccessible(method);
				return method;
			} catch (NoSuchMethodException e) {
				if (superClass == Object.class) {
					throw ExceptionUtils.unchecked(e);
				}
			}
		}
		return null;
	}

	public static Method getAccessibleMethodByName(final Object obj, final String methodName) {
		for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType
				.getSuperclass()) {
			Method[] methods = searchType.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					makeAccessible(method);
					return method;
				}
			}
		}
		return null;
	}

	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
				|| Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClassGenricType(final Class<?> clazz) {
		return (Class<T>) getClassGenricType(clazz, 0);
	}

	public static Class<?> getClassGenricType(final Class<?> clazz, final int index) {

		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if ((index >= params.length) || (index < 0)) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}
		return (Class<?>) params[index];
	}

	public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
		if ((e instanceof IllegalAccessException) || (e instanceof IllegalArgumentException)
				|| (e instanceof NoSuchMethodException)) {
			return new IllegalArgumentException(e);
		} else if (e instanceof InvocationTargetException) {
			return new RuntimeException(((InvocationTargetException) e).getTargetException());
		} else if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new RuntimeException("Unexpected Checked Exception.", e);
	}
	
	public static Class<?>[] getGenericTypesForField(Field field) {
		if (field == null) {
			return null;
		}
		
		Type gType = field.getGenericType();
		if (!ParameterizedType.class.isAssignableFrom(gType.getClass())) {
			return null;
		}
		
		ParameterizedType pt = (ParameterizedType) gType;
		Type[] typeArgs = pt.getActualTypeArguments();
		if (typeArgs == null || typeArgs.length == 0) {
			return null;
		}
		
		Class<?>[] result = new Class<?>[typeArgs.length];
		for (int i=0; i<typeArgs.length; i++) {
			try {
				result[i] = ClassUtils.getClass(typeArgs[i].getTypeName());
			} catch (ClassNotFoundException e) {
				result[i] = Object.class;
			}
		}
		return result;
	}
}
