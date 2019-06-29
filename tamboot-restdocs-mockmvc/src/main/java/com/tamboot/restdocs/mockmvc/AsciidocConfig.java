package com.tamboot.restdocs.mockmvc;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AsciidocConfig {
   String QUERY_PARAMS_SNIPPETS = "curl-request,request-parameters,http-response,response-fields";

   String BODY_PARAMS_SNIPPETS = "curl-request,request-fields,http-response,response-fields";

   String PATH_PARAMS_SNIPPETS = "curl-request,path-parameters,http-response,response-fields";

    String id() default "";

    String title() default "";

    String snippets() default QUERY_PARAMS_SNIPPETS;

    boolean ignore() default false;

    int orderIndex() default 0;
}
