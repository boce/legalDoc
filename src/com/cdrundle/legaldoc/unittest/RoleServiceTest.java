package com.cdrundle.legaldoc.unittest;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
import com.cdrundle.legaldoc.entity.Role;
import com.cdrundle.legaldoc.service.IRoleService;
import com.cdrundle.legaldoc.vo.RoleVo;

/**
 * 角色测试用例
 * @author xiaokui.li
 *
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下
public class RoleServiceTest
{

	@Autowired
	IRoleDao roleDao;
	
	@Autowired
	IEntitlementDao entitlementDao;
	
	@Autowired
	IOrganizationDao organizationDao;
	
	@Autowired
	IMenuDao menuDao;
	
	@Autowired
	IPageDao pageDao;
	
	@Autowired
	IPageSourceDao pageSourceDao;
	
	@Autowired
	IRoleService roleService;
	
	@Before
	public void init() throws Exception 
	{
		Role role = new Role();
		role.setName("ROLE_TEST");
		role.setDescription("角色测试");
		role.setIsUsed(true);
		roleDao.save(role);
	}
	
	@After
	public void destory() throws Exception 
	{
		Role role = roleDao.findByName("ROLE_TEST");
		if(role != null)
		{
			roleDao.delete(role);
		}
	}
	
	@Test
	@Transactional
	public void testSaveOrUpdate()
	{
		RoleVo role = new RoleVo();
		role.setName("ROLE_TEST1");
		role.setDescription("角色测试1");
		role.setIsUsed(true);
		RoleVo savedRole = roleService.saveOrUpdate(role);
		Assert.assertEquals("ROLE_TEST1", savedRole.getName());;
	}

	@Test
	public void testDelete()
	{
		Role role = roleDao.findByName("ROLE_TEST");
		roleService.delete(RoleVo.createVo(role));
	}

	@Test
	public void testDeleteById()
	{
		Role role = roleDao.findByName("ROLE_TEST");
		roleService.deleteById(role.getId());
	}
	
//	@Test
//	@Transactional
//	public void testAddOprationAuthorization()
//	{
//		Role role = roleDao.findByName("ROLE_TEST");
//		
//		Menu menu = new Menu();
//		menu.setId(-100L);
//		menu.setName("赋权测试菜单");
//		menu.setDescription("赋权测试菜单");
//		menu.setIsLeaf(true);
//		menu.setIsUsed(true);
//		menu.setLevel(1);
//		
//		com.cdrundle.legaldoc.entity.Page page = new com.cdrundle.legaldoc.entity.Page();
//		page.setId(-100L);
//		page.setName("测试页面");
//		page.setMenu(menu);
//		page.setIsUsed(true);
//		
//		PageSource pageSource = new PageSource();
//		pageSource.setId(-100L);
//		pageSource.setName("审核");
//		pageSource.setPage(page);
//		
//		Entitlment entitlment = new Entitlment();
//		entitlment.setRole(role);
//		entitlment.setMenu(menu);
//		entitlment.setPage(page);
//		entitlment.setPageSource(pageSource);
//		
//		List<Entitlment> entitlments = new ArrayList<Entitlment>();
//		entitlments.add(entitlment);
//		
//		boolean addOprationAuthorization = roleService.addOprationAuthorization(entitlments);
//		Assert.assertTrue(addOprationAuthorization);
//	}

//	@Test
//	@Transactional
//	public void testRemoveOprationAuthorization()
//	{
//		Role role = roleDao.findByName("ROLE_TEST");
//		
//		Menu menu = new Menu();
//		menu.setName("赋权测试菜单");
//		menu.setDescription("赋权测试菜单");
//		menu.setIsLeaf(true);
//		menu.setIsUsed(true);
//		menu.setLevel(1);
//		Menu savedMenu = menuDao.save(menu);
//		
//		com.cdrundle.legaldoc.entity.Page page = new com.cdrundle.legaldoc.entity.Page();
//		page.setName("测试页面");
//		page.setMenu(savedMenu);
//		page.setIsUsed(true);
//		com.cdrundle.legaldoc.entity.Page savedPage = pageDao.save(page);
//		
//		PageSource pageSource = new PageSource();
//		pageSource.setName("审核测试");
//		pageSource.setCode("100测试");
//		pageSource.setPage(savedPage);
//		PageSource savedPageSource = pageSourceDao.save(pageSource);
//		
//		Entitlment entitlment = new Entitlment();
//		entitlment.setRole(role);
//		entitlment.setMenu(savedMenu);
//		entitlment.setPage(savedPage);
//		entitlment.setPageSource(savedPageSource);
//		
//		entitlementDao.save(entitlment);
//		List<Entitlment> entitlments = entitlementDao.findAll(role.getId());
//		boolean removedOprationAuthorization = roleService.removeOprationAuthorization(entitlments);
//		Assert.assertTrue(removedOprationAuthorization);
//	}
//	
//	@Test
//	@Transactional
//	public void testAddDataAuthorization()
//	{
//		Role role = roleDao.findByName("ROLE_TEST");
//		
//		Organization organization = new Organization();
//		organization.setName("组织机构测试");
//		organization.setId(-100L);
//		
//		Entitlment entitlment = new Entitlment();
//		entitlment.setRole(role);
//		entitlment.setOrganization(organization);
//		
//		List<Entitlment> entitlments = new ArrayList<Entitlment>();
//		entitlments.add(entitlment);
//		
//		boolean addDataAuthorization = roleService.addDataAuthorization(entitlments);
//		Assert.assertTrue(addDataAuthorization);
//	}
//
//	@Test
//	@Transactional
//	public void testRemoveDataAuthorization()
//	{
//		Role role = roleDao.findByName("ROLE_TEST");
//		
//		Organization organization = new Organization();
//		organization.setName("组织机构测试");
//		Organization savedOrganization = organizationDao.save(organization);
//		
//		Entitlment entitlment = new Entitlment();
//		entitlment.setRole(role);
//		entitlment.setOrganization(savedOrganization);
//		Entitlment savedEntitlement = entitlementDao.save(entitlment);
//		List<Entitlment> entitlments = new ArrayList<>();
//		entitlments.add(savedEntitlement);
//		
//		boolean removedDataAuthorization = roleService.removeDataAuthorization(entitlments);
//		Assert.assertTrue(removedDataAuthorization);
//	}
	
	@Test
	public void testFindByName()
	{
		RoleVo role = roleService.findByName("ROLE_TEST");
		Assert.assertEquals("角色测试", role.getDescription());
	}

	@Test
	public void testFindById()
	{
		Role role = roleDao.findByName("ROLE_TEST");
		RoleVo role1 = roleService.findById(role.getId());
		Assert.assertEquals("角色测试", role1.getDescription());
	}

	@Test
	public void testFindLikeName()
	{
		List<RoleVo> roles = roleService.findLikeName("ROLE");
		Assert.assertTrue(roles.size() >= 1);
	}

}
