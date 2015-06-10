package com.cdrundle.legaldoc.unittest;



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
import com.cdrundle.legaldoc.entity.ProtocolModify;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IProtocolModifyService;
import com.cdrundle.legaldoc.vo.ProtocolModifyVo;
/**
 * @author  XuBao
 *
 * 2014年6月23日
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下  
public class ProtocolModifyServiceTest{
	
	@Autowired
	private  	IProtocolModifyService   protocolModifyService;
	@Autowired
	private   IOrganizationDao		organizationDao;
	@Autowired
	private   IUserDao   				userDao;
	@Autowired
	private  INormativeFileDao  normativeFileDao;
	
	@Before
	public  void   init(){
		
		NormativeFile  normativeFile  =new  NormativeFile();
		normativeFile.setName("123");
		normativeFileDao.save(normativeFile);
		
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
	public  void destory() throws ServiceException{
		ProtocolModify  protocolModify  =  protocolModifyService.findProtocolModifyByName("123");
		if( protocolModify != null){
			protocolModifyService.delete(protocolModify.getId());
		}
		
		NormativeFile  normativeFile=normativeFileDao.findByNorFileName("123");
		normativeFileDao.delete(normativeFile);
		
		User  user1 = userDao.findByUserName("lixiaokui");
		userDao.delete(user1);
		
		Organization		organization = organizationDao.findByName("内江");
		organizationDao.delete(organization);
		
				
		
	}

	@Test
	public void testSaveOrUpdate() throws ServiceException{
		ProtocolModifyVo   protocolModifyVo  =  new  ProtocolModifyVo();
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");
		Organization		organization = organizationDao.findByName("内江");
		User  user1 = userDao.findByUserName("lixiaokui");
		
		protocolModifyVo.setName("123");
		protocolModifyVo.setContent("123");
		protocolModifyVo.setDeliberationComment("123");
		protocolModifyVo.setNormativeFile(normativeFile);
		protocolModifyVo.setDraftingUnit(organization);
		protocolModifyVo.setDraftingUnitClerk(user1);
		protocolModifyVo.setDraftingUnitLeader(user1);
		
		protocolModifyService.saveOrUpdate(protocolModifyVo);
	}

	@Test
	public void testDelete() throws ServiceException{
		ProtocolModify  protocolModify = protocolModifyService.findProtocolModifyByName("123");
		protocolModifyService.delete(protocolModify.getId());
	}

	@Test
	public void testFindAll(){
		Page<ProtocolModify> page =protocolModifyService.findAll(1, 1);
		Assert.assertEquals(1, page.getSize());
		Assert.assertEquals("123", page.getContent().get(0).getName());
	}

	@Test
	public void testConfirm(){
		
	}

	@Test
	public void testFindByName(){
		Page<ProtocolModify> page =protocolModifyService.findByName("123", 1, 1);
		Assert.assertEquals("123", page.getContent().get(0).getName());
	}

	@Test
	public void testFindProtocolModifyByName(){
		
	}

}
