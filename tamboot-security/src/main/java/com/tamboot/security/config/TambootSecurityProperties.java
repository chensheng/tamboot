package com.tamboot.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "spring.security")
public class TambootSecurityProperties {
    private String[] ignoringAntMatchers;

    private String interceptAntMatcher;

    private int tokenExpirySeconds = 30 * 24 * 60 * 60;

    private String loginPath;

    private boolean rejectPublicInvocations = true;

    private boolean validateConfigAttributes;

    private List<RoleBasedPermission> roleBasedPermissions;

    private boolean useRedisRepo;

    public String[] getIgnoringAntMatchers() {
        return ignoringAntMatchers;
    }

    public void setIgnoringAntMatchers(String[] ignoringAntMatchers) {
        this.ignoringAntMatchers = ignoringAntMatchers;
    }

    public String getInterceptAntMatcher() {
        return interceptAntMatcher;
    }

    public void setInterceptAntMatcher(String interceptAntMatcher) {
        this.interceptAntMatcher = interceptAntMatcher;
    }

    public int getTokenExpirySeconds() {
        return tokenExpirySeconds;
    }

    public void setTokenExpirySeconds(int tokenExpirySeconds) {
        this.tokenExpirySeconds = tokenExpirySeconds;
    }

    public String getLoginPath() {
        return loginPath;
    }

    public void setLoginPath(String loginPath) {
        this.loginPath = loginPath;
    }

    public boolean isRejectPublicInvocations() {
        return rejectPublicInvocations;
    }

    public void setRejectPublicInvocations(boolean rejectPublicInvocations) {
        this.rejectPublicInvocations = rejectPublicInvocations;
    }

    public boolean isValidateConfigAttributes() {
        return validateConfigAttributes;
    }

    public void setValidateConfigAttributes(boolean validateConfigAttributes) {
        this.validateConfigAttributes = validateConfigAttributes;
    }

    public List<RoleBasedPermission> getRoleBasedPermissions() {
        return roleBasedPermissions;
    }

    public void setRoleBasedPermissions(List<RoleBasedPermission> roleBasedPermissions) {
        this.roleBasedPermissions = roleBasedPermissions;
    }

    public boolean getUseRedisRepo() {
        return useRedisRepo;
    }

    public void setUseRedisRepo(boolean useRedisRepo) {
        this.useRedisRepo = useRedisRepo;
    }

    public static class RoleBasedPermission {
        private String urlAntPattern;

        private String roles;

        public String getUrlAntPattern() {
            return urlAntPattern;
        }

        public void setUrlAntPattern(String urlAntPattern) {
            this.urlAntPattern = urlAntPattern;
        }

        public String getRoles() {
            return roles;
        }

        public void setRoles(String roles) {
            this.roles = roles;
        }
    }
}
