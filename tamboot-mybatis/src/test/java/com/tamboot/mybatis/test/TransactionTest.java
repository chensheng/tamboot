package com.tamboot.mybatis.test;

import com.tamboot.mybatis.test.mapper.UserInfoMapper;
import com.tamboot.mybatis.test.model.UserInfoModel;
import com.tamboot.mybatis.test.service.TransactionService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionTest {
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private UserInfoMapper userInfoMapper;
	
	@Test
	public void testSaveWithException() {
		Exception except = null;
		try {
			transactionService.saveWithException("testSaveWithException", "123456");
		} catch (Exception e) {
			except = e;
		}
		Assert.assertNotNull(except);

		int result = userInfoMapper.deleteByUsername("testSaveWithException");
		Assert.assertEquals(0, result);
	}
	
	@Test
	public void testSave() {
		UserInfoModel model = transactionService.save("testTransactionUsername", "123456");
		Assert.assertNotNull(model);
		Assert.assertNotNull(model.getId());
		
		UserInfoModel savedModel = userInfoMapper.selectOneById(model.getId());
		Assert.assertNotNull(savedModel);
	}
	
	@After
	public void after() {
		userInfoMapper.deleteByUsername("testTransactionUsername");
	}
}
