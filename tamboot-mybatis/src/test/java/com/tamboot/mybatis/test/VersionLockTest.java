package com.tamboot.mybatis.test;

import com.tamboot.mybatis.test.mapper.UserInfoMapper;
import com.tamboot.mybatis.test.model.UserInfoModel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VersionLockTest {
	@Autowired
	private UserInfoMapper userInfoMapper;
	
	@Test
	public void testVersionLock() {
		UserInfoModel model = new UserInfoModel();
		model.setUsername("testVersionLockUsername");
		model.setPassword("123456");
		int insertResult = userInfoMapper.insert(model);
		Assert.assertEquals(1, insertResult);
		
		model.setVersion(8L);
		model.setPassword("654321");
		int updateResult = userInfoMapper.updateById(model);
		Assert.assertEquals(0, updateResult);
	}
	
	@After
	public void after() {
		userInfoMapper.deleteByUsername("testVersionLockUsername");
	}
}
