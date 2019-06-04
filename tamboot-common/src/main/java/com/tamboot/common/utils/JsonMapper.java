package com.tamboot.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;

public class JsonMapper {
	private static final Logger logger = LoggerFactory.getLogger(JsonMapper.class);
	
	private static final JsonMapper SIMPLE_INSTANCE = new JsonMapper();
	
	private static final JsonMapper NON_NULL_INSTANCE = new JsonMapper(Include.NON_NULL);
	
	private static final JsonMapper NON_EMPTY_INSTANCE = new JsonMapper(Include.NON_EMPTY);
	
	private static final JsonMapper NON_DEFAULT_INSTANCE = new JsonMapper(Include.NON_DEFAULT);
	
	public static JsonMapper simpleMapper() {
		return SIMPLE_INSTANCE;
	}
	
	public static JsonMapper nonNullMapper() {
		return NON_NULL_INSTANCE;
	}

	public static JsonMapper nonEmptyMapper() {
		return NON_EMPTY_INSTANCE;
	}

	public static JsonMapper nonDefaultMapper() {
		return NON_DEFAULT_INSTANCE;
	}
	
	private ObjectMapper mapper;

	private JsonMapper() {
		this(null);
	}

	private JsonMapper(Include include) {
		Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder
				.json()
				.failOnUnknownProperties(false)
				.dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		
		if (include != null) {
			builder.serializationInclusion(include);
		}
		
		mapper = builder.build();
		
		SimpleModule longToStringModule = new SimpleModule();
		longToStringModule.addSerializer(Long.class, ToStringSerializer.instance);
		longToStringModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        mapper.registerModule(longToStringModule);
		this.enableEnumUseToString();
	}
	
	public String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			logger.error(ExceptionUtils.getStackTraceAsString(e));
			return null;
		}
	}

	public <T> T fromJson(String jsonString, Class<T> clazz) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			return mapper.readValue(jsonString, clazz);
		} catch (Throwable e) {
			logger.error(ExceptionUtils.getStackTraceAsString(e));
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T fromJson(String jsonString, JavaType javaType) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			return (T) (mapper.readValue(jsonString, javaType));
		} catch (IOException e) {
			logger.error(ExceptionUtils.getStackTraceAsString(e));
			return null;
		}
	}

	public JavaType contructParametricType(Class<?> parametrized, Class<?> parameterClasses) {
		return mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
	}
	
	public JavaType contructArrayType(Class<?> elementClass) {
		return mapper.getTypeFactory().constructArrayType(elementClass);
	}
	
	public JavaType contructCollectionType(Class<? extends Collection<?>> collectionClass, Class<?> elementClass) {
		return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
	}

	public JavaType contructMapType(Class<? extends Map<?,?>> mapClass, Class<?> keyClass, Class<?> valueClass) {
		return mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
	}

	public void update(String jsonString, Object object) {
		try {
			mapper.readerForUpdating(object).readValue(jsonString);
		} catch (JsonProcessingException e) {
			logger.error(ExceptionUtils.getStackTraceAsString(e));
		} catch (IOException e) {
			logger.error(ExceptionUtils.getStackTraceAsString(e));
		}
	}

	public String toJsonP(String functionName, Object object) {
		return toJson(new JSONPObject(functionName, object));
	}
	
	public ObjectMapper getMapper() {
		return mapper;
	}

	private void enableEnumUseToString() {
		mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
	}
}
