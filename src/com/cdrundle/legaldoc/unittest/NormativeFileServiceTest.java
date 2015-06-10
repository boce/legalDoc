package com.cdrundle.legaldoc.unittest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.vo.NorFileQueryVo;

/**
 * @author XuBao
 * 
 *         2014年6月23日
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 指定测试用例的运行器 这里是指定了Junit4
@ContextConfiguration("applicationContext.xml")
// 指定Spring的配置文件 /为classpath下
public class NormativeFileServiceTest {

	@Autowired
	private INormativeFileService normativeFileService;
	@Autowired
	private INormativeFileDao normativeFileDao;

	@Autowired
	private IOrganizationDao organizationDao;

	@Autowired
	private IUserDao userDao;

	@Before
	public void init() throws Exception {
//		NormativeFile normativeFile = new NormativeFile();
//		normativeFile.setName("123");
//		normativeFileDao.save(normativeFile);

		User user = new User();
		user.setName("xubao");
		user.setUserName("xubao");
		user.setPassword("110");
		userDao.save(user);

		User user1 = new User();
		user1.setName("lixiaokui");
		user1.setUserName("lixiaokui");
		user1.setPassword("110");
		userDao.save(user1);

		Organization organization = new Organization();
		organization.setName("内江");
		organizationDao.save(organization);

		Organization organization1 = new Organization();
		organization1.setName("成都");
		organizationDao.save(organization1);

		for (int i = 0; i < 10; i++) {
			NormativeFile normativeFile = new NormativeFile();
			normativeFile.setName("123" + String.valueOf(i));
			normativeFile.setDrtUnit(organization);
			normativeFile.setDrtUnitClerk(user);
			normativeFile.setDrtUnitLeader(user1);
			normativeFile.setDraftDate(new SimpleDateFormat("yyyy-MM-dd")
					.parse("2014-06-24"));
			normativeFile.setInvalidDate(new SimpleDateFormat("yyyy-MM-dd")
					.parse("2014-07-01"));

			normativeFileDao.save(normativeFile);
		}
	}

	@After
	public void destory() throws Exception {
//		NormativeFile normativeFile = normativeFileDao.findByNorFileName("123");
//		normativeFileDao.delete(normativeFile);

		for (int i = 0; i < 10; i++) {
			NormativeFile normativeFile = normativeFileDao
					.findByNorFileName("123" + String.valueOf(i));
			normativeFileDao.delete(normativeFile);
		}

		User user = userDao.findByUserName("xubao");
		userDao.delete(user);
		User user1 = userDao.findByUserName("lixiaokui");
		userDao.delete(user1);

		Organization organization = organizationDao.findByName("内江");
		organizationDao.delete(organization);
		Organization organization1 = organizationDao.findByName("成都");
		organizationDao.delete(organization1);
	}

	@Test
	public void testFindNorFileByStageAndDrtUnit() throws Exception {
		Page<NormativeFile> page = normativeFileService
				.findNorFileByStageAndDrtUnit(Stage.DRAFTING, 1, 10);
		Assert.assertEquals(Stage.DRAFTING, page.getContent().get(0).getStage());
	}

	@Test
	public void testFindNorFileByName() throws Exception {
		Page<NormativeFile> page = normativeFileService.findNorFileByName("12", Stage.DRAFTING, 1, 1);
		Assert.assertEquals("123", page.getContent().get(0).getName());
	}

	@Test
	public void testfindAllForCleanup() throws ParseException {

		Organization organization = organizationDao.findByName("内江");

		NorFileQueryVo dto = new NorFileQueryVo();
		dto.setName("123");
		dto.setDrtUnit(organization);
		dto.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse("2014-06-23"));
		dto.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse("2014-06-25"));

		Pageable pageable = new PageRequest(1, 5);

		Page<NormativeFile> page = normativeFileService.findAllForCleanup(dto,
				pageable);

		Assert.assertEquals(page.getSize(), 5);
		Assert.assertEquals(page.getContent().size(), 5);
		Assert.assertEquals(page.getContent().get(0).getDrtUnit().getName(),"内江");

	}
	
}
