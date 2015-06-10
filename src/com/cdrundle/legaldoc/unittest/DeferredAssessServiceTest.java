package com.cdrundle.legaldoc.unittest;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.DeferredAssessment;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.AssessResult;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IDeferredAssessService;

/**
 * @author  gang.li
 *
 * 满期评估测试
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下  
public class DeferredAssessServiceTest {
	
	@Autowired
	private IDeferredAssessService deferredAssessService;
	
	@Autowired
	private INormativeFileDao normativeFileDao;
	
	@Autowired
	private IOrganizationDao organizationDao;
	
	@Autowired
	private IUserDao userDao;
	
	@Before
	public void init() throws Exception {
		
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
		
		Organization organization  =   new  Organization();
		organization.setName("内江");
		organizationDao.save(organization);
		
		Organization organization1  =   new  Organization();
		organization1.setName("成都");
		organizationDao.save(organization1);
		
	}

	@After
	public void destory() throws Exception {
		List<DeferredAssessment> daList = deferredAssessService.findByName("满期评估Name");
		for (int index = 0; index < daList.size(); index++) {
			DeferredAssessment da = daList.get(index);
			if(da != null && da.getName().equals("满期评估Name"))
				deferredAssessService.delete(da.getId());
		}
		
		NormativeFile  normativeFile = normativeFileDao.findByNorFileName("123");
		normativeFileDao.delete(normativeFile);
		
		User  user = userDao.findByUserName("xubao");
		userDao.delete(user);
		User  user1 = userDao.findByUserName("lixiaokui");
		userDao.delete(user1);
		
		Organization organization = organizationDao.findByName("内江");
		organizationDao.delete(organization);
		Organization organization1 = organizationDao.findByName("成都");
		organizationDao.delete(organization1);
		
	}

	@Test
	public void testSaveOrUpdate() throws ServiceException {
		
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		Organization organization1 = organizationDao.findByName("成都");
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		DeferredAssessment da = new DeferredAssessment();	//定义满期评估
		
		da.setAssessComment("评估意见。。。。");
		da.setAssessDate(new Date());
		da.setAssessResult(AssessResult.DIRECT_DELAY);
		da.setDecisionUnit(organization);
		da.setDecisionUnitClerk(user);
		da.setDecisionUnitLeader(user1);
		da.setDraftingInstruction("起草说明起草说明");
		da.setDraftingUnit(organization1);
		da.setDraftingUnitClerk(user1);
		da.setDraftingUnitLeader(user);
		da.setLegalBasis("法律依据");
		da.setLegalDoc("规范性文档规范性文档");
		da.setName("满期评估Name");
		da.setNormativeFile(normativeFile);
		da.setValidDate(4);
		da.setStatus(Status.OPEN);
		//测试add方法
		deferredAssessService.saveOrUpdate(da);
		
		
	}

	@Test
	public void testDelete() throws ServiceException {
		
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		Organization organization1 = organizationDao.findByName("成都");
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		DeferredAssessment da = new DeferredAssessment();	//定义满期评估
		
		da.setAssessComment("评估意见。。。。");
		da.setAssessDate(new Date());
		da.setAssessResult(AssessResult.DIRECT_DELAY);
		da.setDecisionUnit(organization);
		da.setDecisionUnitClerk(user);
		da.setDecisionUnitLeader(user1);
		da.setDraftingInstruction("起草说明起草说明");
		da.setDraftingUnit(organization1);
		da.setDraftingUnitClerk(user1);
		da.setDraftingUnitLeader(user);
		da.setLegalBasis("法律依据");
		da.setLegalDoc("规范性文档规范性文档");
		da.setName("满期评估NameDelete");
		da.setNormativeFile(normativeFile);
		da.setValidDate(4);
		da.setStatus(Status.OPEN);
		//添加方法
		deferredAssessService.saveOrUpdate(da);
		
		//测试删除功能
		deferredAssessService.delete(da.getId());
	}

	@Test
	public void testFind() throws ServiceException {
		
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		Organization organization1 = organizationDao.findByName("成都");
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		DeferredAssessment da = new DeferredAssessment();	//定义满期评估
		
		da.setAssessComment("评估意见。。。。");
		da.setAssessDate(new Date());
		da.setAssessResult(AssessResult.DIRECT_DELAY);
		da.setDecisionUnit(organization);
		da.setDecisionUnitClerk(user);
		da.setDecisionUnitLeader(user1);
		da.setDraftingInstruction("起草说明起草说明");
		da.setDraftingUnit(organization1);
		da.setDraftingUnitClerk(user1);
		da.setDraftingUnitLeader(user);
		da.setLegalBasis("法律依据");
		da.setLegalDoc("规范性文档规范性文档");
		da.setName("满期评估NameFind");
		da.setNormativeFile(normativeFile);
		da.setValidDate(4);
		da.setStatus(Status.OPEN);
		//添加方法
		deferredAssessService.saveOrUpdate(da);
		
		//测试查询功能
		DeferredAssessment findEds = deferredAssessService.find(da.getId());
		
		System.out.println(findEds.getName());
		
		deferredAssessService.delete(findEds.getId());//删除
	}
	
	@Test
	public void testFindByName() throws ServiceException {
		
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		Organization organization1 = organizationDao.findByName("成都");
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		DeferredAssessment da = new DeferredAssessment();	//定义满期评估
		
		da.setAssessComment("评估意见。。。。");
		da.setAssessDate(new Date());
		da.setAssessResult(AssessResult.DIRECT_DELAY);
		da.setDecisionUnit(organization);
		da.setDecisionUnitClerk(user);
		da.setDecisionUnitLeader(user1);
		da.setDraftingInstruction("起草说明起草说明");
		da.setDraftingUnit(organization1);
		da.setDraftingUnitClerk(user1);
		da.setDraftingUnitLeader(user);
		da.setLegalBasis("法律依据");
		da.setLegalDoc("规范性文档规范性文档");
		da.setName("满期评估NameFindByName");
		da.setNormativeFile(normativeFile);
		da.setValidDate(4);
		da.setStatus(Status.OPEN);
		//添加方法
		deferredAssessService.saveOrUpdate(da);
		
		//测试查询功能
		List<DeferredAssessment> daList = deferredAssessService.findByName("满期评估NameFindByName");
		for (int index = 0; index < daList.size(); index++) {
			DeferredAssessment indexDa = daList.get(index);
			System.out.println(indexDa.getName());
			deferredAssessService.delete(indexDa.getId());//删除
		}
		
	}
	
	@Test
	public void testEdit() throws ServiceException {
		
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		Organization organization1 = organizationDao.findByName("成都");
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		DeferredAssessment da = new DeferredAssessment();	//定义满期评估
		
		da.setAssessComment("评估意见。。。。");
		da.setAssessDate(new Date());
		da.setAssessResult(AssessResult.DIRECT_DELAY);
		da.setDecisionUnit(organization);
		da.setDecisionUnitClerk(user);
		da.setDecisionUnitLeader(user1);
		da.setDraftingInstruction("起草说明起草说明");
		da.setDraftingUnit(organization1);
		da.setDraftingUnitClerk(user1);
		da.setDraftingUnitLeader(user);
		da.setLegalBasis("法律依据");
		da.setLegalDoc("规范性文档规范性文档");
		da.setName("满期评估NameFindByName");
		da.setNormativeFile(normativeFile);
		da.setValidDate(4);
		da.setStatus(Status.OPEN);
		//添加方法
		deferredAssessService.saveOrUpdate(da);
		
		da.setName("满期评估NameEditLast");
		
		//测试查询功能
		DeferredAssessment findEds = deferredAssessService.edit(da);
		
		System.out.println(findEds.getId());
		System.out.println(findEds.getName());
		
		deferredAssessService.delete(findEds.getId());//删除
	}
	
}
