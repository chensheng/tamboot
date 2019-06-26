package com.tamboot.security.permission;

import com.tamboot.common.tools.text.TextUtil;
import com.tamboot.security.config.TambootSecurityProperties;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRoleBasedPermissionRepository implements RoleBasedPermissionRepository {
    private ApplicationContext applicationContext;

    private volatile List<RoleBasedPermission> roleBasedPermissions;

    public InMemoryRoleBasedPermissionRepository(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public List<RoleBasedPermission> load() {
        if (roleBasedPermissions != null) {
            return roleBasedPermissions;
        }

        synchronized (this) {
            if (roleBasedPermissions == null) {
                init();
            }
        }
        return roleBasedPermissions;
    }

    @Override
    public void save(List<RoleBasedPermission> permissions) {
        throw new UnsupportedOperationException();
    }

    private void init() {
        try {
            roleBasedPermissions = new ArrayList<RoleBasedPermission>();
            TambootSecurityProperties properties = applicationContext.getBean(TambootSecurityProperties.class);
            List<TambootSecurityProperties.RoleBasedPermission> permissions = properties.getRoleBasedPermissions();
            if (CollectionUtils.isEmpty(permissions)) {
                return;
            }

            List<RoleBasedPermission> result = new ArrayList<RoleBasedPermission>();
            for (TambootSecurityProperties.RoleBasedPermission permission : permissions) {
                if (TextUtil.isEmpty(permission.getUrlAntPattern()) || TextUtil.isEmpty(permission.getUrlAntPattern())) {
                    continue;
                }
                String[] roles = TextUtil.splitByComma(permission.getRoles());
                if (roles != null && roles.length > 0) {
                    RoleBasedPermission item = new RoleBasedPermission(permission.getUrlAntPattern());
                    for (String role : roles) {
                        item.addRole(role);
                    }
                    roleBasedPermissions.add(item);
                }
            }
        } catch (BeansException e) {
        }
    }
}
