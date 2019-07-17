package com.tamboot.mybatis.id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class SnowFlakeIdGeneratorFactory {
	private static final Logger logger = LoggerFactory.getLogger(SnowFlakeIdGeneratorFactory.class);

	private final ConcurrentHashMap<String, SnowFlakeIdGenerator> generatorMap = new ConcurrentHashMap<String, SnowFlakeIdGenerator>();

	private long machineId;

	private long twepoch;

	public SnowFlakeIdGeneratorFactory(long machineId, long twepoch) {
	    this.machineId = machineId;
	    this.twepoch = twepoch;
	    logger.info("Create SnowFakeIdGeneratorFactory: machineId[{}], twepoch[{}]", machineId, twepoch);
    }

	public long nextId(String generatorName) {
		String resolvedGeneratorName = resolveGeneratorName(generatorName);
		SnowFlakeIdGenerator generator = generatorMap.computeIfAbsent(resolvedGeneratorName, new Function<String, SnowFlakeIdGenerator>() {

			@Override
			public SnowFlakeIdGenerator apply(String t) {
				return new SnowFlakeIdGenerator(machineId, twepoch);
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
