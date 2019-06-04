package com.tamboot.mybatis.utils;

import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Method;

public class MetaObjectUtils {
    public static Method method;

    public static MetaObject forObject(Object object) {
        try {
            return ((MetaObject) method.invoke(null, new Object[] { object }));
        } catch (Exception e) {
        }
        
        return null;
    }

    static {
        try {
            Class<?> metaClass = Class.forName("org.apache.ibatis.reflection.SystemMetaObject");
            method = metaClass.getDeclaredMethod("forObject", new Class[] { Object.class });
        } catch (Exception e1) {
            try {
                Class<?> metaClass = Class.forName("org.apache.ibatis.reflection.MetaObject");
                method = metaClass.getDeclaredMethod("forObject", new Class[] { Object.class });
            } catch (Exception e2) {
            }
        }
    }
}