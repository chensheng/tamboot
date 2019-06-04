package com.tamboot.mybatis.id;

import org.springframework.util.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class SnowFlakeIdGeneratorFactory {
	private final ConcurrentHashMap<String, SnowFlakeIdGenerator> generatorMap = new ConcurrentHashMap<String, SnowFlakeIdGenerator>();

	private long dataCenterId;

	private long generatorStartTime;

	public SnowFlakeIdGeneratorFactory(long dataCenterId, long generatorStartTime) {
	    this.dataCenterId = dataCenterId;
	    this.generatorStartTime = generatorStartTime;
    }

	public long nextId(String generatorName) {
		String resolvedGeneratorName = resolveGeneratorName(generatorName);
		SnowFlakeIdGenerator generator = generatorMap.computeIfAbsent(resolvedGeneratorName, new Function<String, SnowFlakeIdGenerator>() {

			@Override
			public SnowFlakeIdGenerator apply(String t) {
				return new SnowFlakeIdGenerator(dataCenterId, generatorStartTime);
			}
			
		});
		
		return generator.nextId();
	}
	
	private String resolveGeneratorName(String generatorName) {
		if (StringUtils.isEmpty(generatorName)) {
			generatorName = "DEFAULT_SNOW_FLAKE_ID_GENERATOR";
		}
		
		return generatorName;
	}
}
