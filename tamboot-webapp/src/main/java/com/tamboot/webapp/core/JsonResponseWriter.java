package com.tamboot.webapp.core;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonResponseWriter {
    private MappingJackson2HttpMessageConverter converter;

    public JsonResponseWriter(MappingJackson2HttpMessageConverter converter) {
        this.converter = converter;
    }

    public void write(HttpServletResponse response, Object body) throws IOException {
        ServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
        converter.write(body, MediaType.APPLICATION_JSON_UTF8, outputMessage);
    }
}
