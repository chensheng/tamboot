package com.tamboot.security.core;

import com.tamboot.common.tools.text.TextUtil;
import com.tamboot.security.config.TambootSecurityProperties;
import com.tamboot.security.token.TokenPresenterFactory;
import com.tamboot.security.token.TokenRepositoryFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TambootAuthenticationService {
    private TambootSecurityProperties properties;

    private UserDetailsService userDetailsService;

    private TokenPresenterFactory tokenPresenterFactory;

    private TokenRepositoryFactory tokenRepositoryFactory;

    public TambootAuthenticationService(ApplicationContext applicationContext, TambootSecurityProperties properties, PasswordEncoderFactory passwordEncoderFactory, TokenPresenterFactory tokenPresenterFactory, TokenRepositoryFactory tokenRepositoryFactory) {
        this.properties = properties;
        this.tokenPresenterFactory = tokenPresenterFactory;
        this.tokenRepositoryFactory = tokenRepositoryFactory;
        this.userDetailsService = new LazyUserDetailsService(applicationContext, passwordEncoderFactory);
    }

    /**
     * Login by username manually and generate a token.
     * @param username
     * @param request
     * @param response
     * @return token
     */
    public final String login(String username, HttpServletRequest request, HttpServletResponse response) {
        if (username == null || request == null || response == null) {
            return TextUtil.EMPTY;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new RunAsUserToken(userDetails.getUsername(), userDetails,
                userDetails.getPassword(), userDetails.getAuthorities(), null);
        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);
        String token = tokenPresenterFactory.get(request).generate(request, authentication);
        tokenRepositoryFactory.get().save(token, securityContext, properties.getTokenExpirySeconds());
        tokenPresenterFactory.get(request).write(request, response, token);
        return token;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String token = tokenPresenterFactory.get(request).readFromRequest(request);
        tokenPresenterFactory.get(request).delete(request, response);
        tokenRepositoryFactory.get().delete(token);
    }
}
