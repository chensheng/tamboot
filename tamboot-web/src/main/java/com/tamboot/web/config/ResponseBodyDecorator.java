package com.tamboot.web.config;

import org.springframework.core.Ordered;

public interface ResponseBodyDecorator extends Ordered {
    /**
     *
     * @param body body to decorate
     * @return new response body
     */
    Object decorate(Object body);
}
