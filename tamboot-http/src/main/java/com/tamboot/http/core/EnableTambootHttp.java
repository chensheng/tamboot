package com.tamboot.http.core;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HttpApiRegistrar.class)
public @interface EnableTambootHttp {
}
