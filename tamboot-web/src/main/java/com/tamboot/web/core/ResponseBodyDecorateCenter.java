package com.tamboot.web.core;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class ResponseBodyDecorateCenter {
    private Log logger = LogFactory.getLog(getClass());

    private ApplicationContext applicationContext;

    public ResponseBodyDecorateCenter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Object doDecorate(Object body) {
        if (body == null) {
            return null;
        }

        Collection<ResponseBodyDecorator> decorators = getDecorators();
        if (CollectionUtils.isEmpty(decorators)) {
            return body;
        }

        for (ResponseBodyDecorator decorator : decorators) {
            try {
                body = decorator.decorate(body);
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return body;
    }

    private Collection<ResponseBodyDecorator> getDecorators() {
        try {
            Map<String, ResponseBodyDecorator> decoratorMap = applicationContext.getBeansOfType(ResponseBodyDecorator.class);
            if (decoratorMap != null) {
                Collection<ResponseBodyDecorator> decorators = decoratorMap.values();
                List<ResponseBodyDecorator> sortedDecorators = new ArrayList<ResponseBodyDecorator>(decorators);
                sortedDecorators.sort(new Comparator<ResponseBodyDecorator>() {
                    @Override
                    public int compare(ResponseBodyDecorator o1, ResponseBodyDecorator o2) {
                        if (o1.getOrder() < o2.getOrder()) {
                            return -1;
                        }

                        if (o1.getOrder() > o2.getOrder()) {
                            return 1;
                        }

                        return 0;
                    }
                });
                return sortedDecorators;
            }
        } catch (BeansException e) {
        }
        return null;
    }
}
