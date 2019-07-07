package com.tamboot.web.core;

import com.tamboot.common.web.ApiResponse;
import com.tamboot.web.annotation.IgnoreResponseWrapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class TambootResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private ResponseBodyDecorateCenter responseBodyDecorateCenter;

    public TambootResponseBodyAdvice(ResponseBodyDecorateCenter responseBodyDecorateCenter) {
        this.responseBodyDecorateCenter = responseBodyDecorateCenter;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        IgnoreResponseWrapper ignoreResponseWrapper = returnType.getMethodAnnotation(IgnoreResponseWrapper.class);
        if (ignoreResponseWrapper != null) {
            return body;
        }

        if (selectedConverterType.isAssignableFrom(StringHttpMessageConverter.class)) {
            return body;
        }

        body = responseBodyDecorateCenter.doDecorate(body);

        if (body != null && ApiResponse.class.isAssignableFrom(body.getClass())) {
            return body;
        }

        TambootResponse bodyWrapper = TambootResponse.success(body);
        return bodyWrapper;
    }
}
