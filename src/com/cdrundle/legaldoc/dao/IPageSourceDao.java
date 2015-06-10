package com.cdrundle.legaldoc.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.PageSource;

public interface IPageSourceDao extends Dao<PageSource>
{
	/**
	 * 根据编码和页面id查找页面资源
	 * @param userName
	 * @return
	 */
	@Query("select p from PageSource p where p.code = ?1 and p.page.id = ?2")
	public PageSource findByCodeAndPage(String code, long pageId);
	
	/**
	 * 根据页面id查找页面资源
	 * @param userName
	 * @return
	 */
	@Query("select p from PageSource p where p.page.id = ?1")
	public List<PageSource> findByPage(long pageId);
	
	/**
	 * 查找所有可用的页面资源
	 * @return
	 */
	@Query("select p from PageSource p where p.isUsed = true")
	public List<PageSource> findAllEnable();
}
