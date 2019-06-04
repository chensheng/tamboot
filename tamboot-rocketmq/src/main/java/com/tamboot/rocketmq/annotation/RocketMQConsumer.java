package com.tamboot.rocketmq.annotation;

import com.tamboot.rocketmq.core.SelectorType;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RocketMQConsumer {
	String consumerGroup() default "";
	
	String topic() default "";
	
	SelectorType selectorType() default SelectorType.TAG;
	
	String selectorExpression() default "*";
	
	int consumerThreadMin() default 4;
	
	int consumerThreadMax() default 4;
}
