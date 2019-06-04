package com.tamboot.security.test.custom;

import com.tamboot.security.permission.RoleBasedPermission;
import com.tamboot.security.permission.RoleBasedPermissionRepository;

import java.util.ArrayList;
import java.util.List;

public class CustomRoleBasedPermissionRepository implements RoleBasedPermissionRepository {
    @Override
    public List<RoleBasedPermission> load() {
        List<RoleBasedPermission> permissions = new ArrayList<>();
        permissions.add(new RoleBasedPermission("/**").addRole("MANAGER").addRole("USER"));
        permissions.add(new RoleBasedPermission("/manager/**").addRole("MANAGER"));
        return permissions;
    }

    @Override
    public void save(List<RoleBasedPermission> permissions) {
    }
}
