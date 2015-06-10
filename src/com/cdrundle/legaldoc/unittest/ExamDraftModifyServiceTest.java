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
import com.cdrundle.legaldoc.entity.ExaminationDraftModify;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IExamDraftModifyService;

/**
 * @author  gang.li
 *
 * 送审稿修改测试
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下  
public class ExamDraftModifyServiceTest {
	
	@Autowired
	private IExamDraftModifyService examDraftModifyService;
	
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
		
	}

	@After
	public void destory() throws Exception {
		List<ExaminationDraftModify> edmList = examDraftModifyService.findByName("送审稿修改Name");
		for (int index = 0; index < edmList.size(); index++) {
			ExaminationDraftModify edm = edmList.get(index);
			if (edm != null && edm.getName().equals("送审稿修改Name")) {
				examDraftModifyService.delete(edm.getId(), "D:\\test1.doc");
			}
		}
			
		NormativeFile  normativeFile = normativeFileDao.findByNorFileName("123");
		normativeFileDao.delete(normativeFile);
		
		User  user = userDao.findByUserName("xubao");
		userDao.delete(user);
		User  user1 = userDao.findByUserName("lixiaokui");
		userDao.delete(user1);
		
		Organization organization = organizationDao.findByName("内江");
		organizationDao.delete(organization);
		
	}

	@Test
	public void testSaveOrUpdate() throws ServiceException {
		
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		ExaminationDraftModify eds = new ExaminationDraftModify();	//定义送审稿报送
		eds.setDraftingUnit(organization);
		eds.setDraftingUnitClerk(user);
		eds.setDraftingUnitLeader(user1);
		eds.setName("送审稿修改Name");
		eds.setNormativeFile(normativeFile);
		eds.setContent("修改内容");
		eds.setReviewComment("审查意见");
		
		//测试add方法
		examDraftModifyService.saveOrUpdate(eds, "D:\\test1.doc", "");
		
		
	}

	@Test
	public void testDelete() throws ServiceException {
		
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		ExaminationDraftModify eds = new ExaminationDraftModify();	//定义送审稿报送
		eds.setDraftingUnit(organization);
		eds.setDraftingUnitClerk(user);
		eds.setDraftingUnitLeader(user1);
		eds.setName("送审稿修改NameDelete");
		eds.setNormativeFile(normativeFile);
		eds.setContent("修改内容");
		eds.setReviewComment("审查意见");
		
		//测试add方法
		examDraftModifyService.saveOrUpdate(eds, "D:\\test1.doc", "");
		
		//测试删除功能
		examDraftModifyService.delete(eds.getId(), "D:\\test1.doc");
	}

	@Test
	public void testFind() throws ServiceException {
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		ExaminationDraftModify eds = new ExaminationDraftModify();	//定义送审稿报送
		eds.setDraftingUnit(organization);
		eds.setDraftingUnitClerk(user);
		eds.setDraftingUnitLeader(user1);
		eds.setName("送审稿修改NameFind");
		eds.setNormativeFile(normativeFile);
		eds.setContent("修改内容");
		eds.setReviewComment("审查意见");
		
		//测试add方法
		examDraftModifyService.saveOrUpdate(eds, "D:\\test1.doc", "");
		
		//测试查询功能
		ExaminationDraftModify findEds = examDraftModifyService.find(eds.getId());
		
		System.out.println(findEds.getName());
		
		examDraftModifyService.delete(findEds.getId(), "D:\\test1.doc");//删除
	}
	
	@Test
	public void testFindByName() throws ServiceException {
		
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		ExaminationDraftModify eds = new ExaminationDraftModify();	//定义送审稿报送
		eds.setDraftingUnit(organization);
		eds.setDraftingUnitClerk(user);
		eds.setDraftingUnitLeader(user1);
		eds.setName("送审稿修改NameFindByName");
		eds.setNormativeFile(normativeFile);
		eds.setContent("修改内容");
		eds.setReviewComment("审查意见");
		
		//测试add方法
		examDraftModifyService.saveOrUpdate(eds, "D:\\test1.doc", "");
		
		//测试查询功能
		List<ExaminationDraftModify> edmList = examDraftModifyService.findByName("送审稿修改NameFindByName");
		for (int index = 0; index < edmList.size(); index++) {
			ExaminationDraftModify edm = edmList.get(index);
			if (edm != null && edm.getName().equals("送审稿修改NameFindByName")) {
				examDraftModifyService.delete(edm.getId(), "D:\\test1.doc");
			}
		}
	}
	
	@Test
	public void testEdit() throws ServiceException {
		
		NormativeFile  normativeFile  =  normativeFileDao.findByNorFileName("123");	//定义规范性文件
		
		Organization organization = organizationDao.findByName("内江");	//部门
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		ExaminationDraftModify eds = new ExaminationDraftModify();	//定义送审稿报送
		eds.setDraftingUnit(organization);
		eds.setDraftingUnitClerk(user);
		eds.setDraftingUnitLeader(user1);
		eds.setName("送审稿修改NameEdit");
		eds.setNormativeFile(normativeFile);
		eds.setContent("修改内容");
		eds.setReviewComment("审查意见");
		
		//测试add方法
		examDraftModifyService.saveOrUpdate(eds, "D:\\test1.doc", "");
		
		eds.setName("送审稿修改NameEditLast");
		
		//测试查询功能
		ExaminationDraftModify findEds = examDraftModifyService.edit(eds, "D:\\test1.doc", "");
		
		System.out.println(findEds.getId());
		System.out.println(findEds.getName());
		
		examDraftModifyService.delete(findEds.getId(), "D:\\test1.doc");//删除
	}
	
}
