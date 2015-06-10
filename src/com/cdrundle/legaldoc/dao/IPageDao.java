package com.cdrundle.legaldoc.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.Page;

public interface IPageDao extends Dao<Page>
{
	/**
	 * 根据名称查找页面
	 * @param userName
	 * @return
	 */
	@Query("select p from Page p where p.name = ?1")
	public Page findByName(String name);
	
	/**
	 * 根据菜单id查找页面
	 * @param userName
	 * @return
	 */
	@Query("select p from Page p where p.menu.id = ?1")
	public List<Page> findByMenu(long id);
	
	/**
	 * 查找所有启用的页面
	 * @return
	 */
	@Query("select p from Page p where p.isUsed = true")
	public List<Page> findAllEnable();
}
