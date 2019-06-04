package com.tamboot.security.permission;

import java.util.List;

public interface RoleBasedPermissionRepository {
    List<RoleBasedPermission> load();

     void save(List<RoleBasedPermission> permissions);
}
