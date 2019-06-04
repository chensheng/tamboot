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
public class UpdateTest {
	@Autowired
	private UserInfoMapper userInfoMapper;
	
	@Test
	public void testUpdate() {
		UserInfoModel newModel = new UserInfoModel();
		newModel.setUsername("testUpdateUsername");
		newModel.setPassword("123456");
		userInfoMapper.insert(newModel);
		
		UserInfoModel model = userInfoMapper.selectOne(newModel.getId());
		Assert.assertEquals("testUpdateUsername", model.getUsername());
		Assert.assertEquals("123456", model.getPassword());
		Assert.assertEquals(Long.valueOf(333l), model.getModifier());
		Assert.assertEquals(Long.valueOf(0), model.getVersion());
		
		model.setUsername("updatedUsername");
		model.setPassword("654321");
		int result = userInfoMapper.update(model);
		Assert.assertEquals(1, result);
		
		UserInfoModel updatedModel = userInfoMapper.selectOne(newModel.getId());
		Assert.assertEquals("updatedUsername", updatedModel.getUsername());
		Assert.assertEquals("654321", updatedModel.getPassword());
		Assert.assertEquals(Long.valueOf(444), updatedModel.getModifier());
		Assert.assertEquals(Long.valueOf(1), updatedModel.getVersion());
	}
	
	@After
	public void after() {
		userInfoMapper.deleteByUsername("testUpdateUsername");
		userInfoMapper.deleteByUsername("updatedUsername");
	}
}
