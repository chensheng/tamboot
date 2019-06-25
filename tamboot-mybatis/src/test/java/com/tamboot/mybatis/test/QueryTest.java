package com.tamboot.mybatis.test;

import com.github.pagehelper.Page;
import com.tamboot.mybatis.test.dto.UserInfoDto;
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
	public void testSelectOneById() {
		UserInfoModel userInfoModel1 = userInfoMapper.selectOneById(1l);
		Assert.assertNotNull(userInfoModel1);
		Assert.assertEquals(Long.valueOf(1), userInfoModel1.getId());
		Assert.assertEquals(Long.valueOf(0), userInfoModel1.getVersion());
		Assert.assertEquals("chensheng", userInfoModel1.getUsername());
		Assert.assertEquals("123456", userInfoModel1.getPassword());
		
		UserInfoModel userInfoModel2 = userInfoMapper.selectOneById(2l);
		Assert.assertNotNull(userInfoModel2);
		Assert.assertEquals(Long.valueOf(2), userInfoModel2.getId());
		Assert.assertEquals(Long.valueOf(0), userInfoModel2.getVersion());
		Assert.assertEquals("chensheng2", userInfoModel2.getUsername());
		Assert.assertEquals("123456", userInfoModel2.getPassword());
		
		UserInfoModel userInfoModel3 = userInfoMapper.selectOneById(3l);
		Assert.assertNull(userInfoModel3);
	}
	
	@Test
	public void testSelectAllByExample() {
		List<UserInfoModel> userInfoModels = userInfoMapper.selectAllByExample(null, null);
		Assert.assertNotNull(userInfoModels);
		Assert.assertEquals(2, userInfoModels.size());
		Assert.assertEquals("chensheng", userInfoModels.get(0).getUsername());

		userInfoModels = userInfoMapper.selectAllByExample(null, new String[] {"username desc"});
        Assert.assertNotNull(userInfoModels);
        Assert.assertEquals(2, userInfoModels.size());
        Assert.assertEquals("chensheng2", userInfoModels.get(0).getUsername());

		UserInfoModel example = new UserInfoModel();
		example.setUsername("chensheng");
		example.setPassword("123456");
		userInfoModels = userInfoMapper.selectAllByExample(example, null);
		Assert.assertNotNull(userInfoModels);
		Assert.assertEquals(1, userInfoModels.size());
		Assert.assertEquals("chensheng", userInfoModels.get(0).getUsername());

		UserInfoModel example2 = new UserInfoModel();
		example2.setPassword("123456");
        userInfoModels = userInfoMapper.selectAllByExample(example2, new String[]{"username desc"});
        Assert.assertNotNull(userInfoModels);
        Assert.assertEquals(2, userInfoModels.size());
        Assert.assertEquals("chensheng2", userInfoModels.get(0).getUsername());
	}
	
	@Test
	public void testPageByEample() {
		Page<UserInfoModel> page1 = userInfoMapper.pageByExample(null, 1, 1, null);
		Assert.assertNotNull(page1);
		Assert.assertEquals(2, page1.getTotal());
		Assert.assertEquals(2, page1.getPages());
		Assert.assertEquals(1, page1.getPageNum());
		Assert.assertEquals(1, page1.getPageSize());
		Assert.assertNotNull(page1.getResult());
		Assert.assertEquals(1, page1.getResult().size());
		
		Page<UserInfoModel> page2 = userInfoMapper.pageByExample(null, 2, 1, null);
		Assert.assertNotNull(page2);
		Assert.assertEquals(2, page2.getTotal());
		Assert.assertEquals(2, page2.getPages());
		Assert.assertEquals(2, page2.getPageNum());
		Assert.assertEquals(1, page2.getPageSize());
		Assert.assertNotNull(page2.getResult());
		Assert.assertEquals(1, page2.getResult().size());
		
		Page<UserInfoModel> page3 = userInfoMapper.pageByExample(null, 3, 1, null);
		Assert.assertNotNull(page3);
		Assert.assertEquals(2, page3.getTotal());
		Assert.assertEquals(2, page3.getPages());
		Assert.assertEquals(2, page3.getPageNum());
		Assert.assertEquals(1, page3.getPageSize());
		Assert.assertNotNull(page3.getResult());
		Assert.assertEquals(1, page3.getResult().size());


		UserInfoModel example = new UserInfoModel();
		example.setUsername("chensheng2");
		page1 = userInfoMapper.pageByExample(example, 1, 10, null);
        Assert.assertNotNull(page1);
        Assert.assertEquals(1, page1.getTotal());
        Assert.assertEquals(1, page1.getPages());
        Assert.assertEquals(1, page1.getPageNum());
        Assert.assertEquals(10, page1.getPageSize());
        Assert.assertNotNull(page1.getResult());
        Assert.assertEquals(1, page1.getResult().size());
        Assert.assertEquals("chensheng2", page1.getResult().get(0).getUsername());

        page1 = userInfoMapper.pageByExample(null, 1, 10, new String[] {"username desc"});
        Assert.assertNotNull(page1);
        Assert.assertEquals(2, page1.getTotal());
        Assert.assertEquals(1, page1.getPages());
        Assert.assertEquals(1, page1.getPageNum());
        Assert.assertEquals(10, page1.getPageSize());
        Assert.assertNotNull(page1.getResult());
        Assert.assertEquals(2, page1.getResult().size());
        Assert.assertEquals("chensheng2", page1.getResult().get(0).getUsername());


        UserInfoModel example2 = new UserInfoModel();
        example2.setPassword("123456");
        page1 = userInfoMapper.pageByExample(example2, 1, 10, new String[] {"username desc"});
        Assert.assertNotNull(page1);
        Assert.assertEquals(2, page1.getTotal());
        Assert.assertEquals(1, page1.getPages());
        Assert.assertEquals(1, page1.getPageNum());
        Assert.assertEquals(10, page1.getPageSize());
        Assert.assertNotNull(page1.getResult());
        Assert.assertEquals(2, page1.getResult().size());
        Assert.assertEquals("chensheng2", page1.getResult().get(0).getUsername());
	}

	@Test
	public void testCountByExample() {
	    long count = userInfoMapper.countByExample(null);
	    Assert.assertEquals(2, count);

	    UserInfoModel example = new UserInfoModel();
	    example.setUsername("chensheng");
	    count = userInfoMapper.countByExample(example);
	    Assert.assertEquals(1, count);

	    UserInfoModel example2 = new UserInfoModel();
	    example2.setPassword("123456");
        count = userInfoMapper.countByExample(example2);
        Assert.assertEquals(2, count);
    }

    @Test
    public void testSelectAllDto() {
	    List<UserInfoDto> dtoList = userInfoMapper.selectAllDto();
	    Assert.assertNotNull(dtoList);
	    Assert.assertEquals(2, dtoList.size());
	    Assert.assertNotNull(dtoList.get(0).getId());
        Assert.assertNotNull(dtoList.get(0).getUsername());
        Assert.assertNotNull(dtoList.get(0).getPassword());
	    Assert.assertNotNull(dtoList.get(0).getRegisterTime());
    }
}
