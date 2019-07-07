package com.tamboot.web.core;

import com.github.pagehelper.Page;
import com.tamboot.common.web.Pagination;

public class PaginationResponseBodyDecorator implements ResponseBodyDecorator {
    @Override
    public Object decorate(Object body) {
        if (body.getClass() != Page.class) {
            return body;
        }

        Page page = (Page) body;
        return new Pagination<Object>(page.getPageNum(), page.getPageSize(), page.getTotal(), page.getPages(), page.getResult());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
