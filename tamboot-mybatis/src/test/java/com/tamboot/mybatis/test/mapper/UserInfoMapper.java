package com.tamboot.mybatis.test.mapper;

import com.tamboot.mybatis.provider.CommonMapper;
import com.tamboot.mybatis.test.dto.UserInfoDto;
import com.tamboot.mybatis.test.model.UserInfoModel;

import java.util.List;

public interface UserInfoMapper extends CommonMapper<UserInfoModel, Long> {
	int deleteByUsername(String username);

	List<UserInfoDto> selectAllDto();
}
