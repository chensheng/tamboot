package com.tamboot.mybatis.test.service.impl;

import com.tamboot.mybatis.test.mapper.UserInfoMapper;
import com.tamboot.mybatis.test.model.UserInfoModel;
import com.tamboot.mybatis.test.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {
	@Autowired
	private UserInfoMapper userInfoMapper;

	@Override
	@Transactional(readOnly = false, rollbackFor = {Exception.class})
	public UserInfoModel saveWithException(String username, String password) {
		UserInfoModel userInfoModel = new UserInfoModel();
		userInfoModel.setUsername(username);
		userInfoModel.setPassword(password);
		userInfoMapper.insert(userInfoModel);
		throw new RuntimeException("rollback");
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = {Exception.class})
	public UserInfoModel save(String username, String password) {
		UserInfoModel model = new UserInfoModel();
		model.setUsername(username);
		model.setPassword(password);
		userInfoMapper.insert(model);
		return model;
	}

}
