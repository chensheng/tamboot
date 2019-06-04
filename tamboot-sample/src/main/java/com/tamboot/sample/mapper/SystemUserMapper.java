package com.tamboot.sample.mapper;

import com.tamboot.sample.model.SystemUserModel;

public interface SystemUserMapper {
    SystemUserModel selectOneById(Long id);

    SystemUserModel selectOneByUsername(String username);

    int updateById(SystemUserModel model);
}
