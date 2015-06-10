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
import com.cdrundle.legaldoc.entity.RecordReview;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.ReviewResult;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IRecordReviewService;
import com.cdrundle.legaldoc.vo.RecordReviewVo;

/**
 * @author  XuBao
 *
 * 2014年6月24日
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下  
public class IRecordReviewServiceTest{
	
	@Autowired
	private  IRecordReviewService  recordReviewService;
	@Autowired
	private  INormativeFileDao  normativeFileDao;
	@Autowired
	private  IOrganizationDao  organizationDao;
	@Autowired
	private  IUserDao  userDao;
	
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
	public void destory() throws ServiceException{
		RecordReview   recordReview  =  recordReviewService.findRecordReviewByName("123");
		if( recordReview != null){
			recordReviewService.delete(recordReview.getId());
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
		RecordReviewVo   recordReviewVo  =  new RecordReviewVo();
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");
		User  user = userDao.findByUserName("lixiaokui");
		Organization  organization =  organizationDao.findByName("内江");
		
		recordReviewVo.setName("123");
		recordReviewVo.setNormativeFile(normativeFile);
		recordReviewVo.setContentOop(true);
		recordReviewVo.setDecisionMakingUnit(organization);
		recordReviewVo.setDecisionMakingUnitClerk(user);
		recordReviewVo.setDecisionMakingUnitLeader(user);
		recordReviewVo.setDecProcedureOop(false);
		recordReviewVo.setDecTechHasDefects(true);
		recordReviewVo.setDecUnitOop(false);
		recordReviewVo.setDraftingInstruction("123");
		recordReviewVo.setLegalBasis("123");
		recordReviewVo.setLegalDoc("123");
		recordReviewVo.setOthers(false);
		recordReviewVo.setRecordReport("123");
		recordReviewVo.setRecordReviewDate(new  Date ());
		recordReviewVo.setRecordUnit(organization);
		recordReviewVo.setRecordUnitClerk(user);
		recordReviewVo.setRecordUnitLeader(user);
		recordReviewVo.setReviewOpinionPaper("123");
		recordReviewVo.setReviewResult(ReviewResult.REVOKE);
		recordReviewVo.setStatus(Status.SUBMIT);
		recordReviewService.saveOrUpdate(recordReviewVo);
	}

	@Test
	public void testDelete() throws ServiceException{
		RecordReview  recordReview  =  recordReviewService.findRecordReviewByName("123");
		if( recordReview != null){
			recordReviewService.delete(recordReview.getId());
		}
	}

	@Test
	public void testFindByName(){
		Page<RecordReview> page =  recordReviewService.findByName("12", 1, 1);
		Assert.assertEquals(1, page.getSize());
		Assert.assertEquals("123", page.getContent().get(0).getName());
	}

	@Test
	public void testSubmit(){
		
	}

	@Test
	public void testApprove()
	{
	}

	@Test
	public void testUnApprove(){
		
	}

	@Test
	public void testFlow(){
		
	}

	@Test
	public void testFindAll(){
		Page<RecordReview> page =  recordReviewService.findAll(1, 1);
		Assert.assertEquals(1, page.getSize());
		Assert.assertEquals("123", page.getContent().get(0).getName());
	}

	@Test
	public void testSend(){
		
	}

	@Test
	public void testRegister() throws ServiceException{
		RecordReview   recordReview =  recordReviewService.findRecordReviewByName("123");
		if(recordReview != null ){
			recordReview.setRecordDate(new Date());
			recordReview.setRegisterCode("123");
		}
		
		recordReviewService.saveOrUpdate(RecordReviewVo.createVo(recordReview));
	}

}
