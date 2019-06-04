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

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeleteTest {
	@Autowired
	private UserInfoMapper userInfoMapper;
	
	@Test
	public void testDelete() {
		UserInfoModel newModel = new UserInfoModel();
		newModel.setUsername("testDeleteUsername");
		newModel.setPassword("123456");
		userInfoMapper.insert(newModel);
		
		UserInfoModel model = userInfoMapper.selectOne(newModel.getId());
		Assert.assertNotNull(model);
		List<UserInfoModel> models = userInfoMapper.selectAll();
		Assert.assertEquals(3, models.size());
		
		userInfoMapper.delete(newModel.getId());
		
		UserInfoModel deletedModel = userInfoMapper.selectOne(newModel.getId());
		Assert.assertNull(deletedModel);
		models = userInfoMapper.selectAll();
		Assert.assertEquals(2, models.size());
	}
	
	@After
	public void after() {
		userInfoMapper.deleteByUsername("testDeleteUsername");
	}
}
