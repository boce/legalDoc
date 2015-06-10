package com.cdrundle.legaldoc.unittest;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.OrganizationVo;

/**
 * 组织机构测试用例
 * @author xiaokui.li
 *
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下
public class OrganizationServiceTest
{

	@Autowired
	IOrganizationService organizationService;
	
	@Autowired
	IOrganizationDao organizationDao;
	
	@Before
	public void init() throws Exception 
	{
		Organization organization = new Organization();
		organization.setName("省政府测试");
		organization.setPhone("15822678005");
		organization.setIsUsed(true);
		OrganizationVo savedOrg = organizationService.saveOrUpdate(OrganizationVo.createVo(organization));
		
		Organization organization1 = new Organization();
		organization1.setName("内江测试");
		organization1.setParentOrganization(organizationDao.findOne(savedOrg.getId()));
		organization1.setPhone("15822678005");
		organization1.setIsUsed(true);
		organizationService.saveOrUpdate(OrganizationVo.createVo(organization1));
	}

	@After
	public void destory() throws Exception 
	{
		OrganizationVo organization1 = organizationService.find("内江测试");
		if(organization1 != null)
		{
			organizationService.delete(organization1);
		}
		OrganizationVo organization = organizationService.find("省政府测试");
		if(organization != null)
		{
			organizationService.delete(organization);
		}
		
	}
	
	@Test
	@Transactional
	public void testSaveOrUpdate()
	{
		Organization parentOrganization = organizationDao.findByName("省政府测试");
		
		OrganizationVo organization1 = new OrganizationVo();
		organization1.setName("内江测试1");
		organization1.setParentOrganization(OrgShortVo.createVo(parentOrganization));
		organization1.setPhone("15822678005");
		organization1.setIsUsed(true);
		
		OrganizationVo savedOrganization = organizationService.saveOrUpdate(organization1);
		Assert.assertEquals("内江测试1", savedOrganization.getName());
	}

	@Test
	@Transactional
	public void testDelete()
	{
		OrganizationVo organization = organizationService.find("省政府测试");
		boolean isDeleteSuccess = organizationService.delete(organization);
		Assert.assertTrue(isDeleteSuccess);
	}

	@Test
	public void testFind()
	{
		OrganizationVo organization = organizationService.find("省政府测试");
		Assert.assertEquals("省政府测试", organization.getName());
	}

	@Test
	public void testFindById()
	{
		OrganizationVo organization = organizationService.find("省政府测试");
		OrganizationVo organization2 = organizationService.findById(organization.getId());
		Assert.assertEquals("省政府测试", organization2.getName());
	}
	
	@Test
	public void testFindLikeName()
	{
		Page<OrganizationVo> organizations = organizationService.findLikeName("省政府", 1, 1);
		Assert.assertEquals(1, organizations.getTotalPages());
	}

	@Test
	public void testFindChildren()
	{
		OrganizationVo parentOrganization = organizationService.find("省政府测试");
		List<OrganizationVo> childrenOrganzations = organizationService.findChildren(parentOrganization.getId());
		Assert.assertEquals("内江测试", childrenOrganzations.get(0).getName());
	}

	@Test
	public void testFindParent()
	{
		OrganizationVo organization = organizationService.find("内江测试");
		OrganizationVo parentOrganization = organizationService.findParent(organization.getId());
		Assert.assertEquals("省政府测试", parentOrganization.getName());
		
	}

}
