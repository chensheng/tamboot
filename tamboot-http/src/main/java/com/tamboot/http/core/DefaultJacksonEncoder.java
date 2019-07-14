package com.tamboot.http.core;

import com.tamboot.common.tools.mapper.JsonMapper;
import feign.Request;
import feign.RequestTemplate;
import feign.Util;
import feign.codec.EncodeException;
import feign.codec.Encoder;

import java.lang.reflect.Type;

public class DefaultJacksonEncoder implements Encoder {
    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        String bodyTemplate = JsonMapper.nonNullMapper().toJson(object);
        template.body(Request.Body.bodyTemplate(bodyTemplate, Util.UTF_8));
    }
}
