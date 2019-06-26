package com.tamboot.webapp.security;

import com.tamboot.common.tools.text.RedisKeyFactory;
import com.tamboot.security.permission.RoleBasedPermission;
import com.tamboot.security.permission.RoleBasedPermissionRepository;
import com.tamboot.webapp.core.RedisNamespace;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class RedisRoleBasedPermissionRepository implements RoleBasedPermissionRepository {
    private RedisTemplate redisTemplate;

    public RedisRoleBasedPermissionRepository(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<RoleBasedPermission> load() {
        String key = createKey();
        Object permissions = redisTemplate.opsForValue().get(key);
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

        String key = createKey();
        redisTemplate.opsForValue().set(key, permissions);
    }

    private String createKey() {
        return RedisKeyFactory.create(RedisNamespace.CONFIG.value(), "roleBasedPermissions");
    }
}
