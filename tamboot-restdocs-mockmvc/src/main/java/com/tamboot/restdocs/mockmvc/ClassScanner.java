package com.tamboot.restdocs.mockmvc;

import com.tamboot.common.tools.base.ExceptionUtil;
import com.tamboot.common.tools.text.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ClassScanner {
    private static final Logger logger = LoggerFactory.getLogger(ClassScanner.class);

    public static Set<Class<?>> scan(WebApplicationContext context, String scanPackage) {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        if (context == null || TextUtil.isEmpty(scanPackage)) {
            return classes;
        }

        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(context);
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                .concat(ClassUtils.convertClassNameToResourcePath(scanPackage))
                .concat("/**/*.class");

        Resource[] resources = null;
        try {
            resources = context.getResources(packageSearchPath);
        } catch (IOException e) {
            logger.error(ExceptionUtil.stackTraceText(e));
            return classes;
        }

        MetadataReader metadataReader = null;
        for (Resource resource : resources) {
            if (!resource.isReadable()) {
                continue;
            }

            try {
                metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (metadataReader.getClassMetadata().isConcrete()) {
                    Class<?> targetClass = Class.forName(metadataReader.getClassMetadata().getClassName());
                    classes.add(targetClass);
                }
            } catch (Exception e) {
                logger.error(ExceptionUtil.stackTraceText(e));
            }
        }
        return classes;
    }
}
