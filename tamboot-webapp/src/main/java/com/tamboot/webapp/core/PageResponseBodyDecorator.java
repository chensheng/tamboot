package com.tamboot.webapp.core;

import com.github.pagehelper.Page;
import com.tamboot.web.config.ResponseBodyDecorator;

public class PageResponseBodyDecorator implements ResponseBodyDecorator {
    @Override
    public Object decorate(Object body) {
        if (body.getClass() != Page.class) {
            return body;
        }

        return new PageAdapter((Page) body);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
