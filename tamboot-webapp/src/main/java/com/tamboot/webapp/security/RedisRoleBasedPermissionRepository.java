package com.tamboot.webapp.security;

import com.tamboot.security.permission.RoleBasedPermission;
import com.tamboot.security.permission.RoleBasedPermissionRepository;
import com.tamboot.webapp.core.SecurityConfigKeys;
import com.tamboot.webapp.core.SecurityRedisNamespace;
import com.tamboot.webapp.core.SecurityRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class RedisRoleBasedPermissionRepository implements RoleBasedPermissionRepository {

    private SecurityRedisTemplate redisTemplate;

    public RedisRoleBasedPermissionRepository(SecurityRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<RoleBasedPermission> load() {
        Object permissions = redisTemplate.get(SecurityRedisNamespace.CONFIG, SecurityConfigKeys.ROLE_BASED_PERMISSIONS);
        if (permissions == null) {
            return null;
        }

        return (List<RoleBasedPermission>) permissions;
    }

    @Override
    public void save(List<RoleBasedPermission> permissions) {
        if (CollectionUtils.isEmpty(permissions)) {
            return;
        }

        redisTemplate.set(SecurityRedisNamespace.CONFIG, SecurityConfigKeys.ROLE_BASED_PERMISSIONS, permissions);
    }
}
