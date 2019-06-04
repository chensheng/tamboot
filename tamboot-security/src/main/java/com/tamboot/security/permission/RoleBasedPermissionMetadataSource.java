package com.tamboot.security.permission;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class RoleBasedPermissionMetadataSource implements FilterInvocationSecurityMetadataSource {
    private LazyRoleBasedPermissionRepository permissionRepository;

    public RoleBasedPermissionMetadataSource(ApplicationContext applicationContext) {
        this.permissionRepository = new LazyRoleBasedPermissionRepository(applicationContext);
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        Map<AntPathRequestMatcher, Collection<ConfigAttribute>> requestMap = this.getRequestMap();
        if (CollectionUtils.isEmpty(requestMap)) {
            return null;
        }

        AntPathRequestMatcher matched = null;
        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        for (Map.Entry<AntPathRequestMatcher, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
            AntPathRequestMatcher matcher = entry.getKey();
            if (!matcher.matches(request)) {
                continue;
            }

            if (matched == null || matcher.getPattern().length() > matched.getPattern().length()) {
                matched = matcher;
            }
        }
        if (matched != null) {
            return requestMap.get(matched);
        }

        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Map<AntPathRequestMatcher, Collection<ConfigAttribute>> requestMap = getRequestMap();
        if (CollectionUtils.isEmpty(requestMap)) {
            return null;
        }

        Set<ConfigAttribute> allAttributes = new HashSet<ConfigAttribute>();
        for (Map.Entry<AntPathRequestMatcher, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
            allAttributes.addAll(entry.getValue());
        }
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    private Map<AntPathRequestMatcher, Collection<ConfigAttribute>> getRequestMap() {
        List<RoleBasedPermission> permissions = permissionRepository.load();
        if (CollectionUtils.isEmpty(permissions)) {
            return null;
        }

        Map<AntPathRequestMatcher, Collection<ConfigAttribute>> requestMap = new HashMap<AntPathRequestMatcher, Collection<ConfigAttribute>>();
        for (RoleBasedPermission permission : permissions) {
            if (CollectionUtils.isEmpty(permission.getRoles())) {
                continue;
            }

            AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher(permission.getUrlAntPattern());
            List<ConfigAttribute> configAttrs = new ArrayList<ConfigAttribute>(permission.getRoles().size());
            for (String role : permission.getRoles()) {
                configAttrs.add(new SecurityConfig(role));
            }

            requestMap.put(requestMatcher, configAttrs);
        }
        return requestMap;
    }
}
