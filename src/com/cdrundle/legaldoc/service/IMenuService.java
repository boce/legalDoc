package com.cdrundle.legaldoc.service;

import java.util.List;

import com.cdrundle.legaldoc.vo.MenuShortVo;
import com.cdrundle.legaldoc.vo.MenuVo;

/**
 * 菜单信息操作
 * @author xiaokui.li
 *
 */
public interface IMenuService
{
	/**
	 * 插入或者更新菜单对象
	 * @param menu
	 * @return
	 */
	public boolean saveOrUpdate(MenuVo menuVo);
	
	/**
	 * 删除菜单对象
	 * @param menu
	 * @return
	 */
	public boolean delete(MenuVo menuVo);
	
	/**
	 * 根据id查找菜单对象
	 * @param id
	 * @return
	 */
	public MenuVo findById(long id);
	
	/**
	 * 查找菜单子对象
	 * @param id
	 * @return
	 */
	public List<MenuVo> findChildMenus(long id);
	
	/**
	 * 查找菜单父对象
	 * @param id
	 * @return
	 */
	public MenuVo findParentMenu(long id);
	
	/**
	 * 查找所有菜单对象
	 * @return
	 */
	public List<MenuVo> findAll();
	
	/**
	 * 查找对应深度的菜单对象
	 * @param level
	 * @return
	 */
	public List<MenuVo> findByLevel(int level);
	
	/**
	 * 根据登录用户权限查询对应的菜单资源
	 * @return
	 */
	public List<MenuShortVo> displayMenu();
	
	/**
	 * 查询所有菜单资源
	 * @return
	 */
	public List<MenuVo> displayAllMenu();
	
}
