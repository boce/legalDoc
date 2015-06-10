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
import com.cdrundle.legaldoc.entity.RecordRequest;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IRecordRequestService;
import com.cdrundle.legaldoc.vo.RecordRequestVo;

/**
 * @author  XuBao
 *
 * 2014年6月24日
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下  
public class RecordRequestServiceTest{
	
	@Autowired
	private  IRecordRequestService  recordRequestService;
	@Autowired
	private  INormativeFileDao normativeFileDao;
	@Autowired
	private  IUserDao  userDao;
	@Autowired
	private  IOrganizationDao  organizationDao;
	
	@Before
	public  void  init(){
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
	public void  destory(){
		RecordRequest   recordRequest  =  recordRequestService.findRecordRequestByName("123");
		if( recordRequest != null){
			recordRequestService.delete(recordRequest.getId());
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
		RecordRequestVo  recordRequestVo  = new RecordRequestVo();
		NormativeFile  normativeFile = normativeFileDao.findByNorFileName("123");
		Organization		organization = organizationDao.findByName("内江");
		User  user1 = userDao.findByUserName("lixiaokui");
		
		recordRequestVo.setName("123");
		recordRequestVo.setNormativeFile(normativeFile);
		recordRequestVo.setDecisionMakingUnit(organization);
		recordRequestVo.setDecisionMakingUnitClerk(user1);
		recordRequestVo.setDecisionMakingUnitLeader(user1);
		recordRequestVo.setDraftingInstruction("123");
		recordRequestVo.setLegalBasis("123");
		recordRequestVo.setLegalDoc("123");
		recordRequestVo.setPhone("123");
		recordRequestVo.setRecordReport("123");
		recordRequestVo.setRecordRequestDate(new  Date());
		recordRequestVo.setRecordUnit(organization);
		recordRequestVo.setRecordUnitClerk(user1);
		recordRequestVo.setRecordUnitLeader(user1);
		recordRequestVo.setStatus(Status.OPEN);
		recordRequestService.saveOrUpdate(recordRequestVo);
		
	}

	@Test
	public void testDelete(){
		RecordRequest  recordRequest  = recordRequestService.findRecordRequestByName("123");
		recordRequestService.delete(recordRequest.getId());
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
		
		Page<RecordRequest> page = recordRequestService.findAll(1, 1);
		Assert.assertEquals(1, page.getSize());
		Assert.assertEquals("123", page.getContent().get(0).getName());
	}

	@Test
	public void testSend(){
		
	}

	@Test
	public void testFindByName(){
		Page<RecordRequest> page = recordRequestService.findByName("123", 1, 1);
		Assert.assertEquals(1, page.getSize());
		Assert.assertEquals("123", page.getContent().get(0).getName());
	}

}
