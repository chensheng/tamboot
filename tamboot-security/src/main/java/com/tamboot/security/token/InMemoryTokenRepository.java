package com.tamboot.security.token;

import org.springframework.security.core.context.SecurityContext;

import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTokenRepository implements TokenRepository {
	private ConcurrentHashMap<String, SecurityContext> contextMap = new ConcurrentHashMap<String, SecurityContext>();
	
	private ConcurrentHashMap<String, Long> putTimeMap = new ConcurrentHashMap<String, Long>();

	@Override
	public boolean contains(String token, int expirySeconds) {
		return load(token, expirySeconds) != null;
	}

	@Override
	public SecurityContext load(String token, int expirySeconds) {
		if (expirySeconds < 0) {
			return contextMap.get(token);
		}
		
		if (!putTimeMap.containsKey(token)) {
			return null;
		}
		
		if (System.currentTimeMillis() - putTimeMap.get(token) > expirySeconds * 1000) {
			contextMap.remove(token);
			return null;
		}
		
		return contextMap.get(token);
	}

	@Override
	public void save(String token, SecurityContext securityContext, int expirySeconds) {
		contextMap.put(token, securityContext);
		putTimeMap.put(token, System.currentTimeMillis());
	}

	@Override
	public void delete(String token) {
		contextMap.remove(token);
	    putTimeMap.remove(token);
	}

}
