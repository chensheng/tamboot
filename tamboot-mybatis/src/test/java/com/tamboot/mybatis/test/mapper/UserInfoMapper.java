package com.tamboot.mybatis.test.mapper;

import com.github.pagehelper.Page;
import com.tamboot.mybatis.test.model.UserInfoModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserInfoMapper {
	UserInfoModel selectOne(Long id);
	
	List<UserInfoModel> selectAll();
	
	Page<UserInfoModel> page(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize);
	
	int insert(UserInfoModel model);
	
	int delete(Long id);
	
	int deleteByUsername(String username);
	
	int update(UserInfoModel model);
}
