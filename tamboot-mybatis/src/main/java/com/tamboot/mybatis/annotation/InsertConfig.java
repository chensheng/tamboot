package com.tamboot.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface InsertConfig {
    /**
     * True to auto insert id, false to insert id manually. 
     * Default is true.
     * @return 
     */
    boolean autoInsertId() default true;
    
    /**
     * ID column name. Default is an empty string which means to get ID column name from insert strategy. 
     * @return
     */
    String idColumnName() default "";
    
    /**
     * Whether to override value of existing inserted columns. 
     * Default is false which means not to override value of existing inserted columns.
     * @return
     */
    boolean overrideColumn() default false;
    
    /**
     * Whether to ignore intercepting insert operation. 
     * Default is false which means to intercept insert operation.
     * @return
     */
    boolean ignoreInterceptor() default false;
    
    /**
     * Database column name and insert model field name mapping. Such as {"USER_NAME", "userName"}.
     * @return
     */
    String[] columnAndFiedMapping() default "";
    
}
