package com.cdrundle.legaldoc.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.Menu;

public interface IMenuDao extends Dao<Menu>
{
	/**
	 * 根据名称查询菜单
	 * @param level
	 * @return
	 */
	@Query("select m from Menu m where m.name = ?1")
	public Menu findByName(String name);
	
	/**
	 * 根据深度查询菜单
	 * @param level
	 * @return
	 */
	@Query("select m from Menu m where m.level = ?1")
	public List<Menu> findByLevel(int level);
	
	/**
	 * 查询子菜单
	 * @param id
	 * @return
	 */
	@Query("select m from Menu m where m.parentMenu.id = ?1")
	public List<Menu> findChidMenus(long id);
	
	/**
	 * 查询所有启用的菜单
	 * @return
	 */
	@Query("select m from Menu m where m.isUsed = true")
	public List<Menu> findAllEnable();
}
