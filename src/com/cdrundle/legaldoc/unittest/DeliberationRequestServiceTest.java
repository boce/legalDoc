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
import com.cdrundle.legaldoc.entity.DeliberationRequest;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IDeliberationRequestService;
import com.cdrundle.legaldoc.vo.DeliberationRequestVo;

/**
 * @author  XuBao
 *
 * 2014年6月17日
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下  
public class DeliberationRequestServiceTest  {
	
	@Autowired
	private   IDeliberationRequestService    deliberationRequestService;
	
	@Autowired
	private   INormativeFileDao   normativeFileDao;
	
	@Autowired
	private   IOrganizationDao		organizationDao;
	
	@Autowired
	private   IUserDao   				userDao;
	
	@Before
	public    void init() throws Exception {
		
		NormativeFile  normativeFile  =new  NormativeFile();
		normativeFile.setName("123");
		normativeFileDao.save(normativeFile);
		
		User  user  =  new  User();
		user.setName("xubao");
		user.setUserName("xubao");
		user.setPassword("110");
		userDao.save(user);
		
		User  user1  =  new  User();
		user1.setName("lixiaokui");
		user1.setUserName("lixiaokui");
		user1.setPassword("110");
		userDao.save(user1);
		
		Organization		organization  =   new  Organization();
		organization.setName("内江");
		organizationDao.save(organization);
		
	}

	@After
	public   void destory() throws Exception {
		DeliberationRequest deliberationRequest = deliberationRequestService.findDeliberationRequestByName("123").coverToDeliberationRequest();
		if( deliberationRequest != null)
				deliberationRequestService.delete(deliberationRequest.getId(),"");
		
		NormativeFile  normativeFile=normativeFileDao.findByNorFileName("123");
		normativeFileDao.delete(normativeFile);
		
		User  user = userDao.findByUserName("xubao");
		userDao.delete(user);
		User  user1 = userDao.findByUserName("lixiaokui");
		userDao.delete(user1);
		
		Organization		organization = organizationDao.findByName("内江");
		organizationDao.delete(organization);
		
	}

	@Test
	public void test1SaveOrUpdate() throws ServiceException {
		
		DeliberationRequestVo deliberationRequestVo = new DeliberationRequestVo();
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");
		Organization		organization = organizationDao.findByName("内江");
		User  user = userDao.findByUserName("xubao");
		User  user1 = userDao.findByUserName("lixiaokui");
		deliberationRequestVo.setName("123"); 
		deliberationRequestVo.setNormativeFile(normativeFile);
		deliberationRequestVo.setDraftingUnitClerk(user);
		deliberationRequestVo.setDraftingUnit(organization);
		deliberationRequestVo.setDraftingUnitLeader(user1);
		deliberationRequestVo.setStatus(Status.OPEN);
		deliberationRequestVo.setProtocol("123");
		deliberationRequestVo.setRequestDate(new Date());
		deliberationRequestVo.setReviewComments("123");
		deliberationRequestVo.setDraftingInstruction("123");
		deliberationRequestVo.setRequestComments("123");
		deliberationRequestVo.setDeliberationUnit("广安");
		deliberationRequestService.saveOrUpdate(deliberationRequestVo);
		
	}

	@Test
	public void test4Delete() throws ServiceException {
		DeliberationRequest deliberationRequest = deliberationRequestService.findDeliberationRequestByName("123").coverToDeliberationRequest();
		deliberationRequestService.delete(deliberationRequest.getId(),"");
	}

	@Test
	public void test2FindAll() {
		Page<DeliberationRequest> page =deliberationRequestService.findAll(1, 1);
		Assert.assertEquals(1, page.getSize());
		Assert.assertEquals("123", page.getContent().get(0).getName());
	}

	@Test
	public void test3FindByName() {
		Page<DeliberationRequest> page =deliberationRequestService.findByName("123", 1, 1);
		Assert.assertEquals("123", page.getContent().get(0).getName());
	}

//	@Test
//	public void testSubmit() {
//	}
//
//	@Test
//	public void testApprove() {
//	}
//
//	@Test
//	public void testUnApprove() {
//	}
//
//	@Test
//	public void testFlow() {
//	}

}
