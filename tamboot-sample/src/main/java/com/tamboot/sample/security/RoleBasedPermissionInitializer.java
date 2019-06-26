package com.tamboot.sample.security;

import com.tamboot.common.tools.text.TextUtil;
import com.tamboot.sample.mapper.SystemPermissionMapper;
import com.tamboot.sample.model.SystemPermissionModel;
import com.tamboot.sample.utils.ApplicationContextHolder;
import com.tamboot.security.permission.RoleBasedPermission;
import com.tamboot.webapp.security.RedisRoleBasedPermissionRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class RoleBasedPermissionInitializer implements ApplicationListener<ApplicationReadyEvent> {


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SystemPermissionMapper permissionMapper = ApplicationContextHolder.get().getBean(SystemPermissionMapper.class);
        List<SystemPermissionModel> permissions =  permissionMapper.selectAll();
        if (CollectionUtils.isEmpty(permissions)) {
            return;
        }

        List<RoleBasedPermission> roleBasedPermissions = new ArrayList<RoleBasedPermission>();
        for (SystemPermissionModel permission : permissions) {
            roleBasedPermissions.add(
                    new RoleBasedPermission(permission.getUrlAntPattern())
                            .addRoles(TextUtil.splitByComma(permission.getRoles())));
        }

        RedisRoleBasedPermissionRepository permissionRepository = ApplicationContextHolder.get().getBean(RedisRoleBasedPermissionRepository.class);
        permissionRepository.save(roleBasedPermissions);
    }
}
