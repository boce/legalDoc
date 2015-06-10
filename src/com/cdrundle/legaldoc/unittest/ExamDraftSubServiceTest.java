package com.cdrundle.legaldoc.unittest;

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
import com.cdrundle.legaldoc.entity.ExaminationDraftSubmit;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IExamDraftSubService;
import com.cdrundle.legaldoc.vo.ExamDraftSubmitVo;

/**
 * @author  gang.li
 *
 * 送审稿报送测试
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下  
public class ExamDraftSubServiceTest {
	
	@Autowired
	private IExamDraftSubService examDraftSubService;
	
	@Autowired
	private INormativeFileDao normativeFileDao;
	
	@Autowired
	private IOrganizationDao organizationDao;
	
	@Autowired
	private IUserDao userDao;
	
//	@Before
//	public void init() throws Exception {
//		Organization organization  = new Organization();
//		organization.setName("内江");
//		organizationDao.save(organization);
//		
//		Organization organization1 = new Organization();
//		organization1.setName("成都");
//		organizationDao.save(organization1);
//		
//		NormativeFile normativeFile  = new  NormativeFile();
//		normativeFile.setName("123");
//		normativeFileDao.save(normativeFile);
//		
//		User  user  =  new  User();
//		user.setName("xubao");
//		user.setUserName("xubao");
//		user.setPassword("110");
//		userDao.save(user);
//		
//		User user1 = new User();
//		user1.setName("lixiaokui");
//		user1.setUserName("lixiaokui");
//		user1.setPassword("110");
//		userDao.save(user1);
//		
//	}
//
//	@After
//	public void destory() throws Exception {
//		List<ExaminationDraftSubmit> edsList = examDraftSubService.findByName("送审稿Name");
//		for (int index = 0; index < edsList.size(); index++) {
//			ExaminationDraftSubmit da = edsList.get(index);
//			if(da != null && da.getName().equals("送审稿Name"))
//				examDraftSubService.delete(da.getId());
//		}
//		
//		NormativeFile  normativeFile = normativeFileDao.findByNorFileName("123");
//		normativeFileDao.delete(normativeFile);
//		
//		User  user = userDao.findByUserName("xubao");
//		userDao.delete(user);
//		User  user1 = userDao.findByUserName("lixiaokui");
//		userDao.delete(user1);
//		
//		Organization organization = organizationDao.findByName("内江");
//		organizationDao.delete(organization);
//		Organization organization1 = organizationDao.findByName("成都");
//		organizationDao.delete(organization1);
//		
//	}
//
//	@Test
//	public void testSaveOrUpdate() throws ServiceException {
//		
//		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
//		
//		Organization organization = organizationDao.findByName("内江");	//部门
//		Organization organization1 = organizationDao.findByName("成都");
//		
//		User  user = userDao.findByUserName("xubao");	//定义审查员
//		User  user1 = userDao.findByUserName("lixiaokui");
//		
//		ExamDraftSubmitVo eds = new ExamDraftSubmitVo();	//定义送审稿报送
//		eds.setDraftingInstruction("送审稿测试");
//		eds.setDraftingUnit(organization);
//		eds.setDraftingUnitClerk(user);
//		eds.setDraftingUnitLeader(user1);
//		eds.setExaminationDraft("送审稿报送内容");
//		eds.setLegalBasises("送审稿legalBasises");
//		eds.setName("送审稿Name");
//		eds.setNormativeFile(normativeFile);
//		eds.setReviewUnit(organization1);
//		eds.setReviewUnitClerk(user);
//		eds.setReviewUnitLeader(user1);
//		eds.setStatus(Status.OPEN);
//		eds.setUnionDraftingUnit("organization1");
//		eds.setUnionDraftingUnitClerk("user");
//		eds.setUnionDraftingUnitLeader("user1");
//		
//		//测试add方法
//		examDraftSubService.saveOrUpdate(eds);
//		
//		
//	}
//
//	@Test
//	public void testDelete() throws ServiceException {
//		
//		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
//		
//		Organization organization = organizationDao.findByName("内江");	//部门
//		Organization organization1 = organizationDao.findByName("成都");
//		
//		User  user = userDao.findByUserName("xubao");	//定义审查员
//		User  user1 = userDao.findByUserName("lixiaokui");
//		
//		ExamDraftSubmitVo eds = new ExamDraftSubmitVo();	//定义送审稿报送
//		eds.setDraftingInstruction("送审稿测试");
//		eds.setDraftingUnit(organization);
//		eds.setDraftingUnitClerk(user);
//		eds.setDraftingUnitLeader(user1);
//		eds.setExaminationDraft("送审稿报送内容");
//		eds.setLegalBasises("送审稿legalBasises");
//		eds.setName("送审稿NameDelete");
//		eds.setNormativeFile(normativeFile);
//		eds.setReviewUnit(organization1);
//		eds.setReviewUnitClerk(user);
//		eds.setReviewUnitLeader(user1);
//		eds.setStatus(Status.OPEN);
//		eds.setUnionDraftingUnit("organization1");
//		eds.setUnionDraftingUnitClerk("user");
//		eds.setUnionDraftingUnitLeader("user1");
//		
//		//添加
//		examDraftSubService.saveOrUpdate(eds);
//		
//		//测试删除功能
//		examDraftSubService.delete(eds.getId());
//	}
//
//	@Test
//	public void testFind() throws ServiceException {
//		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
//		
//		Organization organization = organizationDao.findByName("内江");	//部门
//		Organization organization1 = organizationDao.findByName("成都");
//		
//		User  user = userDao.findByUserName("xubao");	//定义审查员
//		User  user1 = userDao.findByUserName("lixiaokui");
//		
//		ExamDraftSubmitVo eds = new ExamDraftSubmitVo();	//定义送审稿报送
//		eds.setDraftingInstruction("送审稿测试");
//		eds.setDraftingUnit(organization);
//		eds.setDraftingUnitClerk(user);
//		eds.setDraftingUnitLeader(user1);
//		eds.setExaminationDraft("送审稿报送内容");
//		eds.setLegalBasises("送审稿legalBasises");
//		eds.setName("送审稿NameFind");
//		eds.setNormativeFile(normativeFile);
//		eds.setReviewUnit(organization1);
//		eds.setReviewUnitClerk(user);
//		eds.setReviewUnitLeader(user1);
//		eds.setStatus(Status.OPEN);
//		eds.setUnionDraftingUnit("organization1");
//		eds.setUnionDraftingUnitClerk("user");
//		eds.setUnionDraftingUnitLeader("user1");
//		
//		//添加
//		examDraftSubService.saveOrUpdate(eds);
//		
//		//测试查询功能
//		ExaminationDraftSubmit findEds = examDraftSubService.find(eds.getId());
//		
//		System.out.println(findEds.getName());
//		
//		examDraftSubService.delete(findEds.getId());//删除
//	}
//	
//	@Test
//	public void testFindByName() throws ServiceException {
//		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
//		
//		Organization organization = organizationDao.findByName("内江");	//部门
//		Organization organization1 = organizationDao.findByName("成都");
//		
//		User  user = userDao.findByUserName("xubao");	//定义审查员
//		User  user1 = userDao.findByUserName("lixiaokui");
//		
//		ExamDraftSubmitVo eds = new ExamDraftSubmitVo();	//定义送审稿报送
//		eds.setDraftingInstruction("送审稿测试");
//		eds.setDraftingUnit(organization);
//		eds.setDraftingUnitClerk(user);
//		eds.setDraftingUnitLeader(user1);
//		eds.setExaminationDraft("送审稿报送内容");
//		eds.setLegalBasises("送审稿legalBasises");
//		eds.setName("送审稿NameFindByName");
//		eds.setNormativeFile(normativeFile);
//		eds.setReviewUnit(organization1);
//		eds.setReviewUnitClerk(user);
//		eds.setReviewUnitLeader(user1);
//		eds.setStatus(Status.OPEN);
//		eds.setUnionDraftingUnit("organization1");
//		eds.setUnionDraftingUnitClerk("user");
//		eds.setUnionDraftingUnitLeader("user1");
//		
//		//添加
//		examDraftSubService.saveOrUpdate(eds);
//		
//		//测试查询功能
//		List<ExaminationDraftSubmit> edsList = examDraftSubService.findByName("送审稿NameFindByName");
//		for (int index = 0; index < edsList.size(); index++) {
//			ExaminationDraftSubmit da = edsList.get(index);
//			if(da != null && da.getName().equals("送审稿NameFindByName"))
//				System.out.println(da.getName());
//				examDraftSubService.delete(da.getId());
//		}
//		
//	}
//	
//	@Test
//	public void testEdit() throws ServiceException {
//		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
//		
//		Organization organization = organizationDao.findByName("内江");	//部门
//		Organization organization1 = organizationDao.findByName("成都");
//		
//		User  user = userDao.findByUserName("xubao");	//定义审查员
//		User  user1 = userDao.findByUserName("lixiaokui");
//		
//		ExamDraftSubmitVo eds = new ExamDraftSubmitVo();	//定义送审稿报送
//		eds.setDraftingInstruction("送审稿测试");
//		eds.setDraftingUnit(organization);
//		eds.setDraftingUnitClerk(user);
//		eds.setDraftingUnitLeader(user1);
//		eds.setExaminationDraft("送审稿报送内容");
//		eds.setLegalBasises("送审稿legalBasises");
//		eds.setName("送审稿审查NameEdit");
//		eds.setNormativeFile(normativeFile);
//		eds.setReviewUnit(organization1);
//		eds.setReviewUnitClerk(user);
//		eds.setReviewUnitLeader(user1);
//		eds.setStatus(Status.OPEN);
//		eds.setUnionDraftingUnit("organization1");
//		eds.setUnionDraftingUnitClerk("user");
//		eds.setUnionDraftingUnitLeader("user1");
//		
//		//添加
//		examDraftSubService.saveOrUpdate(eds);
//		
//		eds.setName("送审稿审查NameEditLast");
//		
//		//测试查询功能
//		ExaminationDraftSubmit findEds = examDraftSubService.edit(eds);
//		
//		System.out.println(findEds.getId());
//		System.out.println(findEds.getName());
//		
//		examDraftSubService.delete(findEds.getId());//删除
//	}
	
	@Test
	public void testFindByNorId() throws ServiceException {
		ExaminationDraftSubmit eds = examDraftSubService.findByNorId(Long.valueOf("1"));
		System.out.println(eds.getDraftingInstruction()+"==========");
	}
}
