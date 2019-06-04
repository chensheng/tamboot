package com.tamboot.security.permission;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.List;

class LazyRoleBasedPermissionRepository implements RoleBasedPermissionRepository {
    private RoleBasedPermissionRepository defaultRepository;

    private ApplicationContext applicationContext;

    LazyRoleBasedPermissionRepository(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        defaultRepository = new InMemoryRoleBasedPermissionRepository(applicationContext);
    }

    @Override
    public List<RoleBasedPermission> load() {
        return get().load();
    }

    @Override
    public void save(List<RoleBasedPermission> permissions) {
        get().save(permissions);
    }

    private RoleBasedPermissionRepository get() {
        try {
            return applicationContext.getBean(RoleBasedPermissionRepository.class);
        } catch (BeansException e) {
            return defaultRepository;
        }
    }
}
