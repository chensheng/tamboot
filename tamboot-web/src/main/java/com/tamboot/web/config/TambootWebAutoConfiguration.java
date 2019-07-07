package com.tamboot.web.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.pagehelper.Page;
import com.tamboot.web.core.*;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.DispatcherServlet;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

@Configuration
@ConditionalOnClass({DispatcherServlet.class})
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
public class TambootWebAutoConfiguration {

    @Bean
    public TambootResponseBodyAdvice tambootResponseBodyAdvice(ResponseBodyDecorateCenter responseBodyDecorateCenter) {
        return new TambootResponseBodyAdvice(responseBodyDecorateCenter);
    }

    @Bean
    public TambootResponseEntityExceptionHandler tambootResponseEntityExceptionHandler() {
        return new TambootResponseEntityExceptionHandler();
    }

    @Bean
    public SmartStringToDateConverter smartStringToDateConverter() {
        return new SmartStringToDateConverter();
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .json()
                .failOnUnknownProperties(false)
                .serializerByType(Long.class, ToStringSerializer.instance)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return new MappingJackson2HttpMessageConverter(builder.build());
    }

    @Bean
    public MappingJackson2XmlHttpMessageConverter xmlConverter() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .xml()
                .failOnUnknownProperties(false)
                .dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return new MappingJackson2XmlHttpMessageConverter(builder.createXmlMapper(true).build());
    }

    @Bean
    public StringHttpMessageConverter stringConverter() {
        return new StringHttpMessageConverter(Charset.forName("UTF-8"));
    }

    @Bean
    public ResponseBodyDecorateCenter responseBodyDecorateCenter(ApplicationContext applicationContext) {
        return new ResponseBodyDecorateCenter(applicationContext);
    }

    @Bean
    @ConditionalOnClass(name = "com.github.pagehelper.Page")
    public PaginationResponseBodyDecorator paginationResponseBodyDecorator() {
        return new PaginationResponseBodyDecorator();
    }
}
