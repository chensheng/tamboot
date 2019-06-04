package com.tamboot.rocketmq.core;

import com.tamboot.rocketmq.annotation.RocketMQConsumer;
import com.tamboot.rocketmq.config.TambootRocketMQProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class RocketMQConsumerRegistry implements BeanFactoryPostProcessor, EnvironmentAware {
	private Log logger = LogFactory.getLog(getClass());
	
	private Environment environment;
	
	private static AtomicInteger containerId = new AtomicInteger(1);

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String[] listenerBeanNames = beanFactory.getBeanNamesForAnnotation(RocketMQConsumer.class);
		if (listenerBeanNames == null || listenerBeanNames.length == 0) {
			return;
		}
		
		for (String listenerBeanName : listenerBeanNames) {
			BeanDefinition listenerBd = beanFactory.getBeanDefinition(listenerBeanName);
			Class<?> listenerClass = this.resolveClass(listenerBd);
			if (listenerClass == null) {
				logger.error("Could not resolve class for bean ["+listenerBeanName+"], it may be factory bean.");
				continue;
			}
			
			if (!MessageListener.class.isAssignableFrom(listenerClass)) {
				throw new IllegalArgumentException("Class [" + listenerClass + "] should implement [" + MessageListener.class +"]");
			}
			
			RocketMQConsumer config = AnnotationUtils.findAnnotation(listenerClass, RocketMQConsumer.class);
			String[] rocketMQPropsBeanNames = beanFactory.getBeanNamesForType(TambootRocketMQProperties.class);
			
			BeanDefinition containerBd = this.buildListenerContainerBeanDefinition(listenerBeanName, rocketMQPropsBeanNames[0], config);
			String containerBeanName = this.generateContainerBeanName();
			
			DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory) beanFactory;
			listableBeanFactory.registerBeanDefinition(containerBeanName, containerBd);
		}
	}
	
	private BeanDefinition buildListenerContainerBeanDefinition(String listenerBeanName, String rocketMQPropsBeanName, RocketMQConsumer config) {
		BeanDefinitionBuilder bdBuilder = BeanDefinitionBuilder.rootBeanDefinition(RocketMQMessageListenerContainer.class);
		this.addPropertyReference(bdBuilder, "rocketMQProps", rocketMQPropsBeanName);
		this.addPropertyReference(bdBuilder, "listener", listenerBeanName);
		bdBuilder.addPropertyValue("config", config);
		
		return bdBuilder.getBeanDefinition();
	}
	
	private void addPropertyReference(BeanDefinitionBuilder builder, String propertyName, String beanName) {
        String resolvedBeanName = environment.resolvePlaceholders(beanName);
        builder.addPropertyReference(propertyName, resolvedBeanName);
    }
	
	private String generateContainerBeanName() {
		return "rocketMQMessageListenerContainer" + containerId.getAndIncrement();
	}

	private Class<?> resolveClass(BeanDefinition bd) {
		String className = bd.getBeanClassName();
		return ClassUtils.resolveClassName(className, null);
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
