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
public class InsertTest {
	@Autowired
	private UserInfoMapper userInfoMapper;
	
	@Test
	public void testInsert() {
		UserInfoModel model = new UserInfoModel();
		model.setUsername("testInsertUsername");
		model.setPassword("654321");
		int result = userInfoMapper.insert(model);
		Assert.assertEquals(1, result);
		Assert.assertNotNull(model.getId());
		Assert.assertNotNull(model.getCreateTime());
		Assert.assertNotNull(model.getModifyTime());
		Assert.assertEquals(Long.valueOf(333l), model.getCreator());
		Assert.assertEquals(Long.valueOf(333l), model.getModifier());
		Assert.assertEquals("testInsertUsername", model.getUsername());
		Assert.assertEquals("654321", model.getPassword());
		
		UserInfoModel insertedModel = userInfoMapper.selectOne(model.getId());
		Assert.assertNotNull(insertedModel);
	}
	
	@After
	public void after() {
		userInfoMapper.deleteByUsername("testInsertUsername");
	}
}
