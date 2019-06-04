package com.tamboot.security.token;

import org.springframework.security.core.context.SecurityContext;

public interface TokenRepository {
    boolean contains(String token, int expirySeconds);
	
    SecurityContext load(String token, int expirySeconds);
    
	void save(String token, SecurityContext securityContext, int expirySeconds);
	
	void delete(String token);
}
