package com.tamboot.common.tools.base;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class BeanUtil extends BeanUtils {
    private static final Logger log = LoggerFactory.getLogger(BeanUtil.class);

    public static void copyNotNullProperties(Object dest, Object orig) {
        try {
            doCopyNotNullProperties(dest, orig);
        } catch (InvocationTargetException e) {
            log.warn(ExceptionUtil.stackTraceText(e));
        } catch (IllegalAccessException e) {
            log.warn(ExceptionUtil.stackTraceText(e));
        }
    }

    private static void doCopyNotNullProperties(Object dest, Object orig) throws InvocationTargetException, IllegalAccessException {
        if (dest == null) {
            throw new IllegalArgumentException("No destination bean specified");
        }
        if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        }

        if (log.isDebugEnabled()) {
            log.debug("BeanUtils.copyProperties(" + dest + ", " + orig + ")");
        }

        if (orig instanceof DynaBean) {
            DynaProperty[] origDescriptors = ((DynaBean) orig).getDynaClass().getDynaProperties();
            for (DynaProperty descriptor : origDescriptors) {
                String name = descriptor.getName();
                if (!BeanUtilsBean.getInstance().getPropertyUtils().isReadable(orig, name) ||
                        !BeanUtilsBean.getInstance().getPropertyUtils().isWriteable(dest, name)) {
                   continue;
                }

                Object value = ((DynaBean) orig).get(name);
                if (value != null) {
                    copyProperty(dest, name, value);
                }
            }
            return;
        }

        if(orig instanceof Map) {
            @SuppressWarnings("unchecked")
            // Map properties are always of type <String, Object>
            Map<String, Object> propMap = (Map<String, Object>) orig;
            for (Map.Entry<String, Object> entry : propMap.entrySet()) {
                String name = entry.getKey();
                if (!BeanUtilsBean.getInstance().getPropertyUtils().isWriteable(dest, name)) {
                    continue;
                }

                Object value = entry.getValue();
                if (value != null) {
                    copyProperty(dest, name, entry.getValue());
                }
            }
            return;
        }

        PropertyDescriptor[] origDescriptors = BeanUtilsBean.getInstance().getPropertyUtils().getPropertyDescriptors(orig);
        for (PropertyDescriptor descriptor : origDescriptors) {
            String name = descriptor.getName();
            if ("class".equals(name)) {
                continue; // No point in trying to set an object's class
            }

            if (!BeanUtilsBean.getInstance().getPropertyUtils().isReadable(orig, name) ||
                    !BeanUtilsBean.getInstance().getPropertyUtils().isWriteable(dest, name)) {
                continue;
            }

            try {
                Object value = BeanUtilsBean.getInstance().getPropertyUtils().getSimpleProperty(orig, name);
                if (value != null) {
                    copyProperty(dest, name, value);
                }
            } catch (NoSuchMethodException e) {
                log.warn(ExceptionUtil.stackTraceText(e));
            } catch (IllegalAccessException e) {
                log.warn(ExceptionUtil.stackTraceText(e));
            } catch (InvocationTargetException e) {
                log.warn(ExceptionUtil.stackTraceText(e));
            }
        }
    }
}
