package com.cdrundle.legaldoc.unittest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.ISysConfigDao;
import com.cdrundle.legaldoc.entity.SysConfig;
import com.cdrundle.legaldoc.service.ISysConfigService;

/**
 * 系统参数配置测试用例
 * @author xiaokui.li
 *
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下
public class SysConfigServiceTest
{

	@Autowired
	ISysConfigService sysConfigService;
	
	@Autowired
	ISysConfigDao sysConfigDao;
	
	private void init()
	{
		SysConfig sysConfig = new SysConfig();
		sysConfig.setCode("CONFIG_TEST");
		sysConfig.setName("配置测试");
		sysConfig.setValue("1");
		sysConfig.setDescription("配置测试");
		sysConfig.setIsUsed(true);
		sysConfigDao.save(sysConfig);
	}
	@Test
	@Transactional
	public void testSaveOrUpdate()
	{
		SysConfig sysConfig = new SysConfig();
		sysConfig.setCode("CONFIG_TEST1");
		sysConfig.setName("配置测试1");
		sysConfig.setValue("11");
		sysConfig.setDescription("配置测试1");
		sysConfig.setIsUsed(true);
		boolean isSuccess = sysConfigService.saveOrUpdate(sysConfig);
		Assert.assertTrue(isSuccess);
	}

	@Test
	@Transactional
	public void testDeleteSysConfig()
	{
		init();
		SysConfig sysConfig = sysConfigDao.findByCode("CONFIG_TEST");
		boolean isSuccess = sysConfigService.delete(sysConfig);
		Assert.assertTrue(isSuccess);
	}

	@Test
	@Transactional
	public void testDeleteLong()
	{
		init();
		SysConfig sysConfig = sysConfigDao.findByCode("CONFIG_TEST");
		boolean isSuccess = sysConfigService.delete(sysConfig.getId());
		Assert.assertTrue(isSuccess);
	}

	@Test
	@Transactional
	public void testFindById()
	{
		init();
		SysConfig sysConfig = sysConfigDao.findByCode("CONFIG_TEST");
		SysConfig sysConfig2 = sysConfigService.findById(sysConfig.getId());
		Assert.assertEquals("配置测试", sysConfig2.getName());
	}
	
	@Test
	@Transactional
	public void testFindByCode()
	{
		init();
		SysConfig sysConfig = sysConfigService.findByCode("CONFIG_TEST");
		Assert.assertEquals("配置测试", sysConfig.getName());
	}

	@Test
	@Transactional
	public void testFindAll()
	{
		init();
		List<SysConfig> sysConfigs = sysConfigService.findAll();
		Assert.assertEquals("配置测试", sysConfigs.get(0).getName());
	}

}
