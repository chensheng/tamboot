package com.tamboot.security.token;

import com.tamboot.common.utils.MD5Utils;
import com.tamboot.common.utils.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class TokenPresenter {
    private static final String TOKEN_SEPARATOR = "::";

    public final String generate(HttpServletRequest request, Authentication authentication) {
        if (authentication == null) {
            return StringUtils.EMPTY_STRING;
        }

        String remoteAddr = request.getRemoteAddr();
        String userId;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            userId = userDetails.getUsername();
        } else {
            userId = principal.toString();
        }

        StringBuilder rawToken = new StringBuilder()
                .append(userId)
                .append(TOKEN_SEPARATOR)
                .append(remoteAddr)
                .append(TOKEN_SEPARATOR)
                .append(System.currentTimeMillis())
                .append(TOKEN_SEPARATOR)
                .append(StringUtils.getRandomStr());
        return MD5Utils.md5With32(rawToken.toString());
    }

	public abstract String getName();

	public abstract String readFromRequest(HttpServletRequest request);
	
	public abstract String readFromResponse(HttpServletRequest request, HttpServletResponse response);
	
	public abstract void write(HttpServletRequest request, HttpServletResponse response, String token);
	
	public abstract void delete(HttpServletRequest request, HttpServletResponse response);
}
