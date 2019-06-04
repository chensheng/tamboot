package com.tamboot.mybatis.test;

import com.github.pagehelper.Page;
import com.tamboot.mybatis.test.mapper.UserInfoMapper;
import com.tamboot.mybatis.test.model.UserInfoModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QueryTest {
	@Autowired
	private UserInfoMapper userInfoMapper;
	
	@Test
	public void testSelectOne() {
		UserInfoModel userInfoModel1 = userInfoMapper.selectOne(1l);
		Assert.assertNotNull(userInfoModel1);
		Assert.assertEquals(Long.valueOf(1), userInfoModel1.getId());
		Assert.assertEquals(Long.valueOf(0), userInfoModel1.getVersion());
		Assert.assertEquals("chensheng", userInfoModel1.getUsername());
		Assert.assertEquals("123456", userInfoModel1.getPassword());
		
		UserInfoModel userInfoModel2 = userInfoMapper.selectOne(2l);
		Assert.assertNotNull(userInfoModel2);
		Assert.assertEquals(Long.valueOf(2), userInfoModel2.getId());
		Assert.assertEquals(Long.valueOf(0), userInfoModel2.getVersion());
		Assert.assertEquals("chensheng2", userInfoModel2.getUsername());
		Assert.assertEquals("123456", userInfoModel2.getPassword());
		
		UserInfoModel userInfoModel3 = userInfoMapper.selectOne(3l);
		Assert.assertNull(userInfoModel3);
	}
	
	@Test
	public void testSelectAll() {
		List<UserInfoModel> userInfoModels = userInfoMapper.selectAll();
		Assert.assertNotNull(userInfoModels);
		Assert.assertEquals(2, userInfoModels.size());
	}
	
	@Test
	public void testPage() {
		Page<UserInfoModel> page1 = userInfoMapper.page(1, 1);
		Assert.assertNotNull(page1);
		Assert.assertEquals(2, page1.getTotal());
		Assert.assertEquals(2, page1.getPages());
		Assert.assertEquals(1, page1.getPageNum());
		Assert.assertEquals(1, page1.getPageSize());
		Assert.assertNotNull(page1.getResult());
		Assert.assertEquals(1, page1.getResult().size());
		
		Page<UserInfoModel> page2 = userInfoMapper.page(2, 1);
		Assert.assertNotNull(page2);
		Assert.assertEquals(2, page2.getTotal());
		Assert.assertEquals(2, page2.getPages());
		Assert.assertEquals(2, page2.getPageNum());
		Assert.assertEquals(1, page2.getPageSize());
		Assert.assertNotNull(page2.getResult());
		Assert.assertEquals(1, page2.getResult().size());
		
		Page<UserInfoModel> page3 = userInfoMapper.page(3, 1);
		Assert.assertNotNull(page3);
		Assert.assertEquals(2, page3.getTotal());
		Assert.assertEquals(2, page3.getPages());
		Assert.assertEquals(2, page3.getPageNum());
		Assert.assertEquals(1, page3.getPageSize());
		Assert.assertNotNull(page3.getResult());
		Assert.assertEquals(1, page3.getResult().size());
	}
}
