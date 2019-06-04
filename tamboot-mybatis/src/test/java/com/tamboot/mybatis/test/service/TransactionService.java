package com.tamboot.mybatis.test.service;

import com.tamboot.mybatis.test.model.UserInfoModel;

public interface TransactionService {
	UserInfoModel saveWithException(String username, String password);
	
	UserInfoModel save(String username, String password);
}
