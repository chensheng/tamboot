package com.tamboot.security.config;

import com.tamboot.security.core.*;
import com.tamboot.security.permission.RoleBasedPermissionMetadataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TambootWebSecurityConfigurer extends WebSecurityConfigurerAdapter {
    private FilterComparator filterComparator = new FilterComparator();

    private TambootSecurityProperties properties;

    private List<Filter> filters = new ArrayList<Filter>();

    private ObjectProvider<List<AuthenticationProvider>> authProviders;

    public TambootWebSecurityConfigurer(TambootSecurityProperties properties, ObjectProvider<List<AuthenticationProvider>> authProviders) {
        super(true);
        Assert.notNull(properties, "properties must not be null");
        this.properties = properties;
        this.authProviders = authProviders;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        if (properties.getIgnoringAntMatchers() != null && properties.getIgnoringAntMatchers().length > 0) {
            web.ignoring().antMatchers(properties.getIgnoringAntMatchers());
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (!StringUtils.isEmpty(properties.getInterceptAntMatcher())) {
            http.antMatcher(properties.getInterceptAntMatcher());
        }

        http.addFilter(securityContextPersistenceFilter());
        if (!StringUtils.isEmpty(properties.getLoginPath())) {
            http.addFilter(usernamePasswordAuthenticationFilter());
        }
        http.addFilter(exceptionTranslationFilter());
        http.addFilter(filterSecurityInterceptor());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        List<AuthenticationProvider> availableAuthProviders = authProviders.getIfAvailable();
        if (!CollectionUtils.isEmpty(availableAuthProviders)) {
            for (AuthenticationProvider authProvider : availableAuthProviders) {
                auth.authenticationProvider(authProvider);
            }
        }
    }

    private Filter securityContextPersistenceFilter() {
        return new SecurityContextPersistenceFilter(new LazySecurityContextRepository(getApplicationContext()));
    }

    private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() {
        UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
        filter.setFilterProcessesUrl(properties.getLoginPath());
        filter.setAuthenticationManager(new LazyAuthenticationManager(getApplicationContext()));
        filter.setAuthenticationSuccessHandler(new LazyAuthenticationSuccessHandler(getApplicationContext()));
        filter.setAuthenticationFailureHandler(new LazyAuthenticationFailureHandler(getApplicationContext()));
        return filter;
    }

    private ExceptionTranslationFilter exceptionTranslationFilter() {
        ExceptionTranslationFilter filter = new ExceptionTranslationFilter(new LazyAuthenticationEntryPoint(getApplicationContext()), new EmptyRequestCache());
        filter.setAccessDeniedHandler(new LazyAccessDeniedHandler(getApplicationContext()));
        return filter;
    }

    private FilterSecurityInterceptor filterSecurityInterceptor() {
        FilterSecurityInterceptor filter = new FilterSecurityInterceptor();
        filter.setRejectPublicInvocations(properties.isRejectPublicInvocations());
        filter.setValidateConfigAttributes(properties.isValidateConfigAttributes());
        filter.setAuthenticationManager(new LazyAuthenticationManager(getApplicationContext()));
        filter.setSecurityMetadataSource(new RoleBasedPermissionMetadataSource(getApplicationContext()));
        filter.setAccessDecisionManager(new AffirmativeBased(Arrays.asList(new RoleVoter())));
        return filter;
    }

}
