package com.tamboot.mybatis.utils;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

public class PluginUtils {
	private static final Logger logger = LoggerFactory.getLogger(PluginUtils.class);
	
    public static Object processTarget(Object target) {
        if (target == null) {
            return null;
        }
        
        if (!Proxy.isProxyClass(target.getClass())) {
            return target;
        }
        
        MetaObject mo = MetaObjectUtils.forObject(target);
        if (mo == null) {
            return target;
        }
        
        return processTarget(mo.getValue("h.target"));
    }
    
    public static <A extends Annotation> A findMapperClassAnnotation(MappedStatement ms, Class<A> annotationClass) {
        Class<?> mapperClass = getMapperClass(ms);
        if (mapperClass == null) {
            return null;
        }
        
        return mapperClass.getAnnotation(annotationClass);
    }
    
    public static <A extends Annotation> A findMapperMethodAnnotation(MappedStatement ms, Class<A> annotationClass, Object paramObj) {
        Class<?> mapperClass = getMapperClass(ms);
        String mapperMethodName = getMapperMethodName(ms);
        
        if (mapperClass == null || mapperMethodName == null) {
            return null;
        }
        
        Class<?>[] methodArgTypes = getMethodArgTypes(paramObj);
        
        try {
            Method mapperMethod = mapperClass.getMethod(mapperMethodName, methodArgTypes);
            return mapperMethod.getAnnotation(annotationClass);
        } catch (NoSuchMethodException e) {
        } catch (SecurityException e) {
        }
        
        return null;
    }
    
    public static Class<?> getMapperClass(MappedStatement ms) {
        if (ms == null) {
            return null;
        }
        
        String mapperClassName = getMapperClassName(ms);
        if (mapperClassName == null) {
            return null;
        }
        
        Configuration conf = ms.getConfiguration();
        MetaObject confMetaObject = MetaObjectUtils.forObject(conf);
        if (confMetaObject == null) {
            return null;
        }
        
        MapperRegistry registry = (MapperRegistry) confMetaObject.getValue("mapperRegistry");
        if (registry == null) {
            return null;
        }
        
        Collection<Class<?>> mappers = registry.getMappers();
        for(Class<?> clazz : mappers){
            if(clazz.getName().equals(mapperClassName)){
                return clazz;
            }
        }
        
        return null;
    }
    
    public static String getMapperClassName(MappedStatement ms) {
        if (ms == null) {
            return null;
        }
        
        String id = ms.getId();
        int pos = id.lastIndexOf(".");
        if (pos < 1) {
            return null;
        }
        
        return id.substring(0, pos);
    }
    
    public static String getMapperMethodName(MappedStatement ms) {
        if (ms == null) {
            return null;
        }
        
        String id = ms.getId();
        int pos = id.lastIndexOf(".");
        if (pos + 1 >= id.length()) {
            return null;
        }
        
        return id.substring(pos + 1);
    }
    
    public static Class<?>[] getMethodArgTypes(Object paramObj) {
        Class<?>[] methodArgTypes = null;
        
        if (paramObj == null) {
            return methodArgTypes;
        }

        if(paramObj instanceof ParamMap<?>) {
            ParamMap<?> mmp = (ParamMap<?>) paramObj;
            if (mmp == null || mmp.isEmpty()) {
                return methodArgTypes;
            }
            
            methodArgTypes = new Class<?>[mmp.size() / 2];
            int mmpLen = mmp.size() / 2;
            for(int i = 0; i < mmpLen; i++) {
                Object index = mmp.get("param" + (i + 1));
                methodArgTypes[i] = index.getClass();
            }
        } else {
            methodArgTypes = new Class<?>[] {paramObj.getClass()};
        }
        
        return methodArgTypes;
    }
    
    public static String underscoreToCamel(String str) {
        if (str == null) {
            return null;
        }
        
        StringBuilder result = new StringBuilder();
        String[] unitArr = str.toLowerCase().split("_");
        for (int i = 0; i < unitArr.length; i++) {
            String unit = unitArr[i];
            
            if (i == 0) {
                result.append(unit);
                continue;
            }
            
            char firstChar = unit.charAt(0);
            String formattedUnit = String.valueOf(firstChar).toUpperCase();
            if (unit.length() > 1) {
                formattedUnit += unit.substring(1);
            }
            result.append(formattedUnit);
        }
        
        return result.toString();
    }
}
