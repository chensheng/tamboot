package com.tamboot.http.core;

import feign.Contract;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpApi {
    String name() default "";

    String url();

    Class<? extends Encoder> encoder() default DefaultJacksonEncoder.class;

    Class<? extends Decoder> decoder() default DefaultJacksonDecoder.class;

    Class<? extends RequestInterceptor>[] interceptors() default {};

    Class<? extends Retryer>[] retryer() default {};

    Class<? extends Contract>[] contract() default {};
}
