package com.cdrundle.legaldoc.unittest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.cdrundle.legaldoc.entity.FileCleanUp;
import com.cdrundle.legaldoc.entity.FileCleanUpLine;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.FileStatus;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IFileCleanUpService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.vo.FileCleanupVo;

/**
 * @author  gang.li
 *
 * 满期评估测试
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下  
public class FileCleanUpServiceTest {
	
	@Autowired
	private IFileCleanUpService fileCleanUpService;
	
	@Autowired
	private INormativeFileDao normativeFileDao;
	
	@Autowired
	private IOrganizationDao organizationDao;
	
	@Autowired
	private IUserDao userDao;
	
	@Before
	public void init() throws Exception {
		
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
		
		Organization organization = organizationDao.findByName("内江");	//部门
		Organization organization1 = organizationDao.findByName("成都");
		
		User  user = userDao.findByUserName("xubao");	//定义审查员
		User  user1 = userDao.findByUserName("lixiaokui");
		
		FileCleanUp fcu = new FileCleanUp();	//定义清理文件
		fcu.setApprovalUnit(organization);
		fcu.setApprovalUnitClerk(user);
		fcu.setApprovalUnitLeader(user1);
		fcu.setCleanupDate(new Date());
		fcu.setCleanupUnit(organization1);
		fcu.setCleanupUnitClerk(user);
		fcu.setCleanupUnitLeader(user1);
		fcu.setMainLeaders("mianleaders");
		fcu.setStatus(FileStatus.INVALID);
		
		//创建fileCleanUpLineList
		FileCleanUpLine fileCleanUpLine = new FileCleanUpLine();
		fileCleanUpLine.setCleanupResult(FileStatus.INVALID);
		fileCleanUpLine.setDecisionUnit(organization1);
		fileCleanUpLine.setName("子文件清理");
		fileCleanUpLine.setPublishDate(new Date());
		fileCleanUpLine.setPublishNo("no.1231231");
		fileCleanUpLine.setRemark("remark");
		
		//创建List
		List<FileCleanUpLine> list = new ArrayList<FileCleanUpLine>();
		list.add(fileCleanUpLine);
		
		//测试add方法
//		fileCleanUpService.saveOrUpdate(fcu, list);
		
//		fileCleanUpService.delete(fcu.getId());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testFindNormativeFiles() throws ServiceException {
		
		Organization organization = organizationDao.findByName("内江");	//部门
		Organization organization1 = organizationDao.findByName("成都");
		
		//创建规范性文件
		NormativeFile n1 = new NormativeFile();
		n1.setName("name1");
		n1.setPublishDate(new Date(2010, 7, 7));
		n1.setLegalBasis("legalBasis1");
		n1.setDrtUnit(organization);
		
		//创建规范性文件
		NormativeFile n2 = new NormativeFile();
		n2.setName("name2");
		n2.setPublishDate(new Date(2012, 7, 7));
		n2.setLegalBasis("legalBasis2");
		n2.setDrtUnit(organization1);
		
		//创建规范性文件
		NormativeFile n3 = new NormativeFile();
		n3.setName("name3");
		n3.setPublishDate(new Date(2013, 7, 7));
		n3.setLegalBasis("legalBasis3");
		n3.setDrtUnit(organization);
		
		normativeFileDao.save(n1);
		normativeFileDao.save(n2);
		normativeFileDao.save(n3);
		
		//从normativeFile中模糊查询规范性文件
		
//		List<NormativeFile> nList = fileCleanUpService.findNormativeFiles(organization.getId(), null, null, "name", null);
//		
//		for (int index = 0; index < nList.size(); index++) {
//			NormativeFile normativeFile = nList.get(index);
//			System.out.println(normativeFile.getName());
//		}
		
		normativeFileDao.delete(n1);
		normativeFileDao.delete(n2);
		normativeFileDao.delete(n3);
		
	}
}
