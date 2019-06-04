package com.tamboot.mybatis.id;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * Generate long type ID. The composition of id is 1bit-41bit-10bit-12bit. 
 * The 1bit is 0 which means to generate positive id value. 
 * The 41bit is the diff between current time millis and id generating start time millis.
 * The 10bit is the data center id.
 * The 12bit is the auto increment sequence.
 *
 */
public class SnowFlakeIdGenerator {
	private final long generatorStartTime;
	
	private final long dataCenterId;
	
	private final AtomicLong sequence = new AtomicLong(0);
	
	private final long dataCenterIdBits = 10l;
	
	private final long sequenceBits = 12l;
	
	private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);
	
	private final long sequenceMask = -1L ^ (-1L << sequenceBits);
	
	private final long dataCenterIdShift = sequenceBits;
	
	private final long timeDiffShift = dataCenterIdBits + sequenceBits;
	
	
	public SnowFlakeIdGenerator(long dataCenterId, long generatorStartTime) {
		if (dataCenterId < 0) {
			throw new IllegalArgumentException("dataCenterId must be positive");
		}
		
		if (dataCenterId > maxDataCenterId) {
			throw new IllegalArgumentException("dataCenterId must be less than " + (maxDataCenterId + 1));
		}
		
		if (generatorStartTime >= System.currentTimeMillis()) {
			generatorStartTime = 1493737860828L;
		}
		
		this.dataCenterId = dataCenterId;
		this.generatorStartTime = generatorStartTime;
	}
	
	public long nextId() {
		long timeDiff = getTimeDiff();
		long seq = this.getSequence();
		
		long id = (timeDiff << timeDiffShift) 
				| (dataCenterId << dataCenterIdShift)
				| seq;
		
		return id;
	}
	
	private long getTimeDiff() {
		return System.currentTimeMillis() - generatorStartTime;
	}
	
	private long getSequence() {
		long next = sequence.getAndIncrement();
		return next & sequenceMask;
	}
}
