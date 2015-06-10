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

import com.cdrundle.legaldoc.dao.IMenuDao;
import com.cdrundle.legaldoc.entity.Menu;
import com.cdrundle.legaldoc.service.IMenuService;
import com.cdrundle.legaldoc.vo.MenuVo;

/**
 * 菜单测试用例
 * @author xiaokui.li
 *
 */
@RunWith(SpringJUnit4ClassRunner.class) //指定测试用例的运行器 这里是指定了Junit4  
@ContextConfiguration({"applicationContext.xml"}) //指定Spring的配置文件 /为classpath下
public class MenuServiceTest
{

	@Autowired
	IMenuDao menuDao;
	
	@Autowired
	IMenuService menuService;
	
	
	@Before
	public void init() throws Exception 
	{
		Menu menu = new Menu();
		menu.setName("系统管理测试");
		menu.setDescription("系统管理测试");
		menu.setIsLeaf(false);
		menu.setLevel(1);
		menu.setIsUsed(true);
		menuDao.save(menu);
		
		Menu menu1 = new Menu();
		menu1.setName("用户管理测试");
		menu1.setDescription("用户管理测试");
		menu1.setIsLeaf(true);
		menu1.setLevel(2);
		menu1.setIsUsed(true);
		menu1.setParentMenu(menu);
		menuDao.save(menu1);
		
	}
	
	@After
	public void destory() throws Exception 
	{
		Menu menu1 = menuDao.findByName("用户管理测试");
		if(menu1 != null)
		{
			menuDao.delete(menu1);
		}
		Menu menu = menuDao.findByName("系统管理测试");
		if(menu != null)
		{
			menuDao.delete(menu);
		}
	}
	
	@Test
	@Transactional
	public void testSaveOrUpdate()
	{
		Menu parentMenu = menuDao.findByName("系统管理测试");
		Menu menu = new Menu();
		menu.setName("角色管理测试");
		menu.setDescription("角色管理测试");
		menu.setIsLeaf(true);
		menu.setLevel(2);
		menu.setIsUsed(true);
		menu.setParentMenu(parentMenu);
		boolean isSaveSuccess = menuService.saveOrUpdate(MenuVo.createVo(menu));
		Assert.assertTrue(isSaveSuccess);
	}

	@Test
	public void testDelete()
	{
		Menu menu = menuDao.findByName("用户管理测试");
		boolean isDelete = menuService.delete(MenuVo.createVo(menu));
		Assert.assertTrue(isDelete);
	}

	@Test
	public void testFindById()
	{
		Menu menu = menuDao.findByName("用户管理测试");
		MenuVo menu2 = menuService.findById(menu.getId());
		Assert.assertEquals("用户管理测试", menu2.getName());
	}

	@Test
	public void testFindAll()
	{
		List<MenuVo> menus = menuService.findAll();
		Assert.assertTrue(menus.size() >= 2);
	}

	@Test
	public void testFindByLevel()
	{
		menuService.findByLevel(1);
	}

	@Test
	public void testFindChildMenus()
	{
		Menu menu = menuDao.findByName("系统管理测试");
		List<MenuVo> childMenus = menuService.findChildMenus(menu.getId());
		Assert.assertEquals("用户管理测试", childMenus.get(0).getName());
	}

	@Test
	public void testFindParentMenu()
	{
		Menu menu = menuDao.findByName("用户管理测试");
		MenuVo parentMenu = menuService.findParentMenu(menu.getId());
		Assert.assertEquals("系统管理测试", parentMenu.getName());
	}

}
