package com.cdrundle.legaldoc.unittest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IEntitlementDao;
import com.cdrundle.legaldoc.dao.IMenuDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IPageDao;
import com.cdrundle.legaldoc.dao.IPageSourceDao;
import com.cdrundle.legaldoc.dao.IRoleDao;
import com.cdrundle.legaldoc.entity.Entitlment;
import com.cdrundle.legaldoc.entity.Menu;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.Page;
import com.cdrundle.legaldoc.entity.PageSource;
import com.cdrundle.legaldoc.entity.Role;
import com.cdrundle.legaldoc.service.IEntitlementService;
import com.cdrundle.legaldoc.vo.EntitlmentVo;
import com.cdrundle.legaldoc.vo.MenuVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.PageSourceVo;
import com.cdrundle.legaldoc.vo.PageVo;

/**
 * 赋权管理测试用例
 * @author xiaokui.li
 *
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下
public class EntitlementServiceTest
{

	@Autowired
	IEntitlementDao entitlementDao;
	
	@Autowired
	IRoleDao roleDao;
	
	@Autowired
	IMenuDao menuDao;
	
	@Autowired
	IPageDao pageDao;
	
	@Autowired
	IPageSourceDao pageSourceDao;
	
	@Autowired
	IOrganizationDao organizationDao;
	
	@Autowired
	IEntitlementService entitlementService;
	
	private void init()
	{
		Role role = new Role();
		role.setName("ROLE_TEST");
		Role savedRole = roleDao.save(role);
		
		Menu menu = new Menu();
		menu.setName("测试菜单");
		Menu savedMenu = menuDao.save(menu);
		
		Page page = new Page();
		page.setName("测试页面");
		page.setMenu(savedMenu);
		page.setIsUsed(true);
		Page savedPage = pageDao.save(page);
		
		PageSource pageSource = new PageSource();
		pageSource.setName("审核测试");
		pageSource.setCode("100测试");
		pageSource.setPage(savedPage);
		PageSource savedPageSource = pageSourceDao.save(pageSource);
		
		Organization organization = new Organization();
		organization.setName("组织结构测试");
		Organization savedOrganization = organizationDao.save(organization);
		
		Entitlment entitlment = new Entitlment();
		entitlment.setRole(savedRole);
		entitlment.setMenu(savedMenu);
		entitlment.setPage(savedPage);
		entitlment.setPageSource(savedPageSource);
		entitlment.setOrganization(savedOrganization);
		entitlementDao.save(entitlment);
		
	}
	
	@Test
	@Transactional
	public void testFindAll()
	{
		init();
		Role role = roleDao.findByName("ROLE_TEST");
		List<EntitlmentVo> entitlments = entitlementService.findByRoleId(role.getId());
		Assert.assertEquals("ROLE_TEST", entitlments.get(0).getRole().getName());
	}

	@Test
	@Transactional
	public void testFindMenuAuth()
	{
		init();
		Role role = roleDao.findByName("ROLE_TEST");
		List<MenuVo> menuAuths = entitlementService.findMenuAuth(role.getId());
		Assert.assertEquals("测试菜单", menuAuths.get(0).getName());
	}

	@Test
	@Transactional
	public void testFindPageAuth()
	{
		init();
		Role role = roleDao.findByName("ROLE_TEST");
		List<PageVo> pageAuths = entitlementService.findPageAuth(role.getId());
		Assert.assertEquals("测试页面", pageAuths.get(0).getName());
	}

	@Test
	@Transactional
	public void testFindPageSourceAuth()
	{
		init();
		Role role = roleDao.findByName("ROLE_TEST");
		List<PageSourceVo> pageSourceAuths = entitlementService.findPageSourceAuth(role.getId());
		Assert.assertEquals("审核测试", pageSourceAuths.get(0).getName());
	}

	@Test
	@Transactional
	public void testFindOrgAuth()
	{
		init();
		Role role = roleDao.findByName("ROLE_TEST");
		List<OrgShortVo> orgAuths = entitlementService.findOrgAuth(role.getId());
		Assert.assertEquals("组织结构测试", orgAuths.get(0).getText());
	}

	@Test
	@Transactional
	public void testSaveOrUpdate()
	{
		Role role = new Role();
		role.setName("ROLE_TEST");
		Role savedRole = roleDao.save(role);
		
		Menu menu = new Menu();
		menu.setName("测试菜单");
		Menu savedMenu = menuDao.save(menu);
		
		Page page = new Page();
		page.setName("测试页面");
		page.setMenu(savedMenu);
		page.setIsUsed(true);
		Page savedPage = pageDao.save(page);
		
		PageSource pageSource = new PageSource();
		pageSource.setName("审核测试");
		pageSource.setCode("100测试");
		pageSource.setPage(savedPage);
		PageSource savedPageSource = pageSourceDao.save(pageSource);
		
		Organization organization = new Organization();
		organization.setName("组织结构测试");
		Organization savedOrganization = organizationDao.save(organization);
		
		Entitlment entitlment = new Entitlment();
		entitlment.setRole(savedRole);
		entitlment.setMenu(savedMenu);
		entitlment.setPage(savedPage);
		entitlment.setPageSource(savedPageSource);
		entitlment.setOrganization(savedOrganization);
		
		entitlementService.saveOrUpdate(EntitlmentVo.createVo(entitlment));
	}

	@Test
	@Transactional
	public void testDeleteEntitlment()
	{
		init();
		Role role = roleDao.findByName("ROLE_TEST");
		List<Entitlment> entitlments = entitlementDao.findAll(role.getId());
		boolean isDeleteSuccess = entitlementService.delete(EntitlmentVo.createVo(entitlments.get(0)));
		Assert.assertTrue(isDeleteSuccess);
	}

	@Test
	@Transactional
	public void testDeleteLong()
	{
		init();
		Role role = roleDao.findByName("ROLE_TEST");
		List<Entitlment> entitlments = entitlementDao.findAll(role.getId());
		boolean isDeleteSuccess = entitlementService.delete(entitlments.get(0).getId());
		Assert.assertTrue(isDeleteSuccess);
	}

}
