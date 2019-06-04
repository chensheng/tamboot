package com.tamboot.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface UpdateConfig {
    
    /**
     * Version column name used to control database record version when updating a record.
     * @return
     */
    String versionColumnName() default "";
    
    /**
     * Whether to ignore intercepting update operation. 
     * Default is false which means to intercept update operation.
     * @return
     */
    boolean ignoreInterceptor() default false;
    
    /**
     * Whether to override value of existing updated columns. 
     * Default is false which means not to override value of existing updated columns.
     * @return
     */
    boolean overrideColumn() default false;
    
    /**
     * Whether to enable version lock. Default is true.
     * @return
     */
    boolean versionLock() default true;
    
    /**
     * Database column name and insert model field name mapping. Such as {"USER_NAME", "userName"}.
     * @return
     */
    String[] columnAndFiedMapping() default "";
}
