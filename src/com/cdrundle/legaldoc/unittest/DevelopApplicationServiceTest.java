package com.cdrundle.legaldoc.unittest;

import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IDevelopApplicationDao;
import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.DevelopApplication;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.service.IDevelopApplicationService;
import com.cdrundle.legaldoc.util.MD5Util;
import com.cdrundle.legaldoc.vo.DevelopApplicationVo;

/**
 * 立项申请测试用例
 * @author xiaokui.li
 *
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下
public class DevelopApplicationServiceTest
{

	@Autowired
	IDevelopApplicationService developApplicationService;
	
	@Autowired
	private   INormativeFileDao   normativeFileDao;
	
	@Autowired
	IDevelopApplicationDao developApplicationDao;
	
	@Autowired
	IOrganizationDao organizationDao;
	
	@Autowired
	IUserDao userDao;
	
	private void init() throws Exception
	{
		Organization organization = new Organization();
		organization.setName("内江测试-立项");
		organization.setIsUsed(true);
		Organization savedOrganization = organizationDao.save(organization);
		
		User user = new User();
		user.setName("许报");
		user.setUserName("xubao");
		user.setPassword(MD5Util.getMd5Str("1"));
		user.setOrganization(organization);
		User savedUser = userDao.save(user);
		
		NormativeFile  normativeFile  =new  NormativeFile();
		normativeFile.setName("规范性文件测试-立项");
		
		DevelopApplication developApplication = new DevelopApplication();
		developApplication.setName("立项申请测试");
		developApplication.setNormativeFile(normativeFile);
		developApplication.setApplyOrg(savedOrganization);
		developApplication.setApplyLeader(savedUser);
		developApplication.setApplyClerk(savedUser);
		developApplication.setApprovalLeader(savedUser);
		developApplication.setApplyDate(new SimpleDateFormat("yyyy-MM-dd").parse("2014-06-25"));
		developApplication.setValidDate(2);
		developApplication.setStatus(Status.OPEN);
		developApplication.setLegalBasises(null);
		developApplication.setLegalBasisAttachment("url");
		developApplication.setNecessityLegalAndRisk("制定的必要性、合法性，以及社会稳定性风险评估");
		developApplication.setNecessityLegalAndRiskAttachment("制定的必要性附件");
		developApplication.setMainProblem("拟解决的主要问题");
		developApplication.setMainProblemAttachment("拟解决的主要问题附件");
		developApplication.setPlanRegulationMeasureAndFeasibility("拟确定的制度或措施，以及可行性论证");
		developApplication.setPlanRegulationMeasureAndFeasibilityAtta("拟确定的制度或措施，以及可行性论证附件");
		developApplicationDao.save(developApplication);
	}
	
	@Test
	@Transactional
	public void testSave() throws Exception
	{
		init();
		Organization organization = organizationDao.findByName("内江测试-立项");
		User user = userDao.findByUserName("xubao");
		NormativeFile  normativeFile  = new  NormativeFile();
		normativeFile.setName("规范性文件测试-立项");
		DevelopApplication developApplication = new DevelopApplication();
		developApplication.setName("立项申请测试1111");
		developApplication.setNormativeFile(normativeFile);
		developApplication.setApplyOrg(organization);
		developApplication.setApplyLeader(user);
		developApplication.setApplyClerk(user);
		developApplication.setApprovalLeader(user);
		developApplication.setApplyDate(new SimpleDateFormat("yyyy-MM-dd").parse("2014-06-25"));
		developApplication.setValidDate(2);
		developApplication.setStatus(Status.OPEN);
		developApplication.setLegalBasises(null);
		developApplication.setLegalBasisAttachment("url");
		developApplication.setNecessityLegalAndRisk("制定的必要性、合法性，以及社会稳定性风险评估");
		developApplication.setNecessityLegalAndRiskAttachment("制定的必要性附件");
		developApplication.setMainProblem("拟解决的主要问题");
		developApplication.setMainProblemAttachment("拟解决的主要问题附件");
		developApplication.setPlanRegulationMeasureAndFeasibility("拟确定的制度或措施，以及可行性论证");
		developApplication.setPlanRegulationMeasureAndFeasibilityAtta("拟确定的制度或措施，以及可行性论证附件");
		DevelopApplicationVo developApplicationVo = developApplicationService.saveOrUpdate(DevelopApplicationVo.createVo(developApplication), "","");
		Assert.assertEquals("规范性文件测试-立项", developApplicationVo.getName());;
	}
	
	@Test
	@Transactional
	public void testUpdate() throws Exception
	{
		init();
		DevelopApplicationVo developApplicationVo = developApplicationService.findByName("立项申请测试");
		developApplicationVo.setName("规范性文件测试-立项-更新");
		developApplicationVo.getNormativeFile().setName("规范性文件测试-立项-更新");
		DevelopApplicationVo vo = developApplicationService.saveOrUpdate(developApplicationVo, "", "");
		Assert.assertEquals("规范性文件测试-立项-更新", vo.getName());
	}

	@Test
	@Transactional
	public void testDeleteDevelopApplication() throws Exception
	{
		init();
		DevelopApplication developApplication = developApplicationDao.findByName("立项申请测试");
		boolean isSuccess = developApplicationService.delete(DevelopApplicationVo.createVo(developApplication));
		Assert.assertTrue(isSuccess);
	}

	@Test
	@Transactional
	public void testDeleteLong() throws Exception
	{
		init();
		DevelopApplication developApplication = developApplicationDao.findByName("立项申请测试");
		boolean isSuccess = developApplicationService.delete(developApplication.getId());
		Assert.assertTrue(isSuccess);
	}

	@Test
	public void testSubmit()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testApprove()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testFlow()
	{
		fail("Not yet implemented");
	}

	@Test
	@Transactional
	public void testFind() throws Exception
	{
		init();
		DevelopApplication developApplication = developApplicationDao.findByName("立项申请测试");
		DevelopApplicationVo developApplication2 = developApplicationService.findById(developApplication.getId());
		Assert.assertEquals("立项申请测试", developApplication2.getName());
	}

	@Test
	public void testFindByNamePage()
	{
		fail("Not yet implemented");
	}

	@Test
	@Transactional
	public void testFindByName() throws Exception
	{
		init();
		DevelopApplicationVo developApplication = developApplicationService.findByName("立项申请测试");
		Assert.assertEquals("立项申请测试", developApplication.getName());
	}

}
