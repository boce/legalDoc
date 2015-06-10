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
import com.cdrundle.legaldoc.entity.ExaminationDraftReview;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IExamDraftReviewService;

/**
 * @author  gang.li
 *
 * 送审稿报送测试
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下  
public class ExamDraftReviewServiceTest {

	
	@Autowired
	private IExamDraftReviewService examDraftReviewService;
	
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
		List<ExaminationDraftReview> edsList = examDraftReviewService.findByName("送审稿审查Name");
		for (int index = 0; index < edsList.size(); index++) {
			ExaminationDraftReview da = edsList.get(index);
			if(da != null && da.getName().equals("送审稿审查Name"))
				System.out.println(da.getName());
				examDraftReviewService.delete(da.getId(), "D:\\test1.doc");
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
		
		ExaminationDraftReview eds = new ExaminationDraftReview();	//定义送审稿报送
		eds.setDraftingInstruction("送审稿测试");
		eds.setDraftingUnit(organization);
		eds.setDraftingUnitClerk(user);
		eds.setDraftingUnitLeader(user1);
		eds.setExaminationDraft("送审稿报送内容");
		eds.setLegalBasises("送审稿legalBasises");
		eds.setName("送审稿审查Name");
		eds.setNormativeFile(normativeFile);
		eds.setReviewUnit(organization1);
		eds.setReviewUnitClerk(user);
		eds.setReviewUnitLeader(user1);
		eds.setReviewDate(new Date());
		eds.setStatus(Status.OPEN);
		eds.setUnionDraftingUnit("organization1");
		eds.setUnionDraftingUnitClerk("user");
		eds.setUnionDraftingUnitLeader("user1");
		eds.setReviewComment("审查意见。。。。。。");
		
		//测试add方法
		examDraftReviewService.saveOrUpdate(eds, "D:\\test.doc", "<html><body>123汉字eeee123</body></html>");
		
		
	}

	@Test
	public void testDelete() throws ServiceException {
		
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		Organization organization1 = organizationDao.findByName("成都");
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		ExaminationDraftReview eds = new ExaminationDraftReview();	//定义送审稿报送
		eds.setDraftingInstruction("送审稿测试");
		eds.setDraftingUnit(organization);
		eds.setDraftingUnitClerk(user);
		eds.setDraftingUnitLeader(user1);
		eds.setExaminationDraft("送审稿报送内容");
		eds.setLegalBasises("送审稿legalBasises");
		eds.setName("送审稿审查NameDelete");
		eds.setNormativeFile(normativeFile);
		eds.setReviewUnit(organization1);
		eds.setReviewUnitClerk(user);
		eds.setReviewUnitLeader(user1);
		eds.setReviewDate(new Date());
		eds.setStatus(Status.OPEN);
		eds.setUnionDraftingUnit("organization1");
		eds.setUnionDraftingUnitClerk("user");
		eds.setUnionDraftingUnitLeader("user1");
		eds.setReviewComment("审查意见。。。。。。");
		
		//添加方法
		examDraftReviewService.saveOrUpdate(eds, "D:\\test1.doc", "");
		
		//测试删除功能
		examDraftReviewService.delete(eds.getId(), "D:\\test1.doc");
	}

	@Test
	public void testFind() throws ServiceException {
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		Organization organization1 = organizationDao.findByName("成都");
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		ExaminationDraftReview eds = new ExaminationDraftReview();	//定义送审稿报送
		eds.setDraftingInstruction("送审稿测试");
		eds.setDraftingUnit(organization);
		eds.setDraftingUnitClerk(user);
		eds.setDraftingUnitLeader(user1);
		eds.setExaminationDraft("送审稿报送内容");
		eds.setLegalBasises("送审稿legalBasises");
		eds.setName("送审稿审查NameFind");
		eds.setNormativeFile(normativeFile);
		eds.setReviewUnit(organization1);
		eds.setReviewUnitClerk(user);
		eds.setReviewUnitLeader(user1);
		eds.setReviewDate(new Date());
		eds.setStatus(Status.OPEN);
		eds.setUnionDraftingUnit("organization1");
		eds.setUnionDraftingUnitClerk("user");
		eds.setUnionDraftingUnitLeader("user1");
		eds.setReviewComment("审查意见。。。。。。");
		
		//添加方法
		examDraftReviewService.saveOrUpdate(eds, "D:\\test1.doc", "");
		
		//测试查询功能
		ExaminationDraftReview findEds = examDraftReviewService.find(eds.getId());
		
		System.out.println(findEds.getName());
		
		examDraftReviewService.delete(findEds.getId(), "D:\\test1.doc");//删除
	}
	
	@Test
	public void testFindByName() throws ServiceException {
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		Organization organization1 = organizationDao.findByName("成都");
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		ExaminationDraftReview eds = new ExaminationDraftReview();	//定义送审稿报送
		eds.setDraftingInstruction("送审稿测试");
		eds.setDraftingUnit(organization);
		eds.setDraftingUnitClerk(user);
		eds.setDraftingUnitLeader(user1);
		eds.setExaminationDraft("送审稿报送内容");
		eds.setLegalBasises("送审稿legalBasises");
		eds.setName("送审稿审查NameFindByName");
		eds.setNormativeFile(normativeFile);
		eds.setReviewUnit(organization1);
		eds.setReviewUnitClerk(user);
		eds.setReviewUnitLeader(user1);
		eds.setReviewDate(new Date());
		eds.setStatus(Status.OPEN);
		eds.setUnionDraftingUnit("organization1");
		eds.setUnionDraftingUnitClerk("user");
		eds.setUnionDraftingUnitLeader("user1");
		eds.setReviewComment("审查意见。。。。。。");
		
		//添加方法
		examDraftReviewService.saveOrUpdate(eds, "D:\\test1.doc", "");
		
		//测试查询功能
		List<ExaminationDraftReview> edsList = examDraftReviewService.findByName("送审稿审查NameFindByName");
		for (int index = 0; index < edsList.size(); index++) {
			ExaminationDraftReview da = edsList.get(index);
			if(da != null && da.getName().equals("送审稿审查NameFindByName"))
				System.out.println(da.getName());
				examDraftReviewService.delete(da.getId(), "D:\\test1.doc");
		}
	}
	
	@Test
	public void testEdit() throws ServiceException {
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		Organization organization1 = organizationDao.findByName("成都");
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		ExaminationDraftReview eds = new ExaminationDraftReview();	//定义送审稿报送
		eds.setDraftingInstruction("送审稿测试");
		eds.setDraftingUnit(organization);
		eds.setDraftingUnitClerk(user);
		eds.setDraftingUnitLeader(user1);
		eds.setExaminationDraft("送审稿报送内容");
		eds.setLegalBasises("送审稿legalBasises");
		eds.setName("送审稿审查NameEdit");
		eds.setNormativeFile(normativeFile);
		eds.setReviewUnit(organization1);
		eds.setReviewUnitClerk(user);
		eds.setReviewUnitLeader(user1);
		eds.setReviewDate(new Date());
		eds.setStatus(Status.OPEN);
		eds.setUnionDraftingUnit("organization1");
		eds.setUnionDraftingUnitClerk("user");
		eds.setUnionDraftingUnitLeader("user1");
		eds.setReviewComment("审查意见。。。。。。");
		
		//添加方法
		examDraftReviewService.saveOrUpdate(eds, "D:\\test1.doc", "");
		
		eds.setName("送审稿NameEditLast");
		
		//测试查询功能
		ExaminationDraftReview findEds = examDraftReviewService.edit(eds, "D:\\test1.doc", "");
		
		System.out.println(findEds.getId());
		System.out.println(findEds.getName());
		
		examDraftReviewService.delete(findEds.getId(), "D:\\test1.doc");//删除
	}
	
}
