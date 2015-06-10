package com.cdrundle.legaldoc.unittest;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.SignAndPublish;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.ISignAndPublishService;
import com.cdrundle.legaldoc.vo.SignAndPublishVo;

/**
 * @author XuBao
 * 
 *         2014年6月24日
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 指定测试用例的运行器 这里是指定了Junit4
@ContextConfiguration({ "applicationContext.xml" })
// 指定Spring的配置文件 /为classpath下
public class SignAndPublishServiceTest
{

	@Autowired
	private ISignAndPublishService signAndPublishService;
	@Autowired
	private INormativeFileDao normativeFileDao;
	@Autowired
	private IOrganizationDao organizationDao;
	@Autowired
	private IUserDao userDao;

	@Before
	public void init()
	{
		NormativeFile normativeFile = new NormativeFile();
		normativeFile.setName("123");
		normativeFileDao.save(normativeFile);

		User user = userDao.findByUserName("lixiaokui");
		if (user == null)
		{
			User user1 = new User();
			user1.setName("lixiaokui");
			user1.setUserName("lixiaokui");
			user1.setPassword("110");
			userDao.save(user1);
		}

		Organization organization = organizationDao.findByName("内江");
		if (organization == null)
		{
			organization = new Organization();
			organization.setName("内江");
			organizationDao.save(organization);
		}
	}

	@After
	public void destory() throws ServiceException
	{
		SignAndPublish signAndPublish = signAndPublishService
				.findSignAndPublishByName("123");
		if (signAndPublish != null)
		{
			signAndPublishService.delete(signAndPublish.getId());
		}
		NormativeFile normativeFile = normativeFileDao.findByNorFileName("123");
		normativeFileDao.delete(normativeFile);

		User user1 = userDao.findByUserName("lixiaokui");
		userDao.delete(user1);

		Organization organization = organizationDao.findByName("内江");
		organizationDao.delete(organization);

	}

	@Test
	public void testSaveOrUpdate() throws ServiceException
	{
		SignAndPublishVo signAndPublishVo = new SignAndPublishVo();
		NormativeFile normativeFile = normativeFileDao.findByNorFileName("123");
		Organization organization = organizationDao.findByName("内江");
		User user1 = userDao.findByUserName("lixiaokui");

		signAndPublishVo.setName("123");
		signAndPublishVo.setNormativeFile(normativeFile);
		signAndPublishVo.setDecisionMakingUnit(organization);
		signAndPublishVo.setDecisionMakingUnitClerk(user1);
		signAndPublishVo.setDecisionMakingUnitLeader(user1);
		signAndPublishVo.setInvalidDate(new Date());
		signAndPublishVo.setPublishDate(new Date());
		signAndPublishVo.setPublishNo("123");
		signAndPublishVo.setSignDate(new Date());
		signAndPublishVo.setSignLeaders("张三");
		signAndPublishVo.setValidDate(2);
		signAndPublishVo.setLegalDoc("test");

		signAndPublishService.saveOrUpdate(signAndPublishVo);

	}

	@Test
	public void testDelete() throws ServiceException{
		SignAndPublish  signAndPublish  =  signAndPublishService.findSignAndPublishByName("123");
		if(  signAndPublish != null){
			signAndPublishService.delete(signAndPublish.getId());
		}
	}

	@Test
	public void testSubmit(){

	}

	@Test
	public void testApprove(){

	}

	@Test
	public void testUnApprove(){

	}

	@Test
	public void testFlow(){

	}

	@Test
	public void testFindAll(){
		Page<SignAndPublish>  page  = signAndPublishService.findAll(1, 1);
		Assert.assertEquals(1, page.getSize());
		Assert.assertEquals("123", page.getContent().get(0).getName());
	}

	@Test
	public void testPublish(){

	}

	@Test
	public void testFindByName(){
		Page< SignAndPublish> page = signAndPublishService.findByName("12", 1, 1);
		Assert.assertEquals(1, page.getSize());
		Assert.assertEquals("123", page.getContent().get(0).getName());
	}

}
