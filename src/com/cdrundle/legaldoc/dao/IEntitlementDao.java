package com.cdrundle.legaldoc.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.Entitlment;
import com.cdrundle.legaldoc.entity.Menu;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.Page;
import com.cdrundle.legaldoc.entity.PageSource;

public interface IEntitlementDao extends Dao<Entitlment>
{
	/**
	 * 查找赋权
	 * @return
	 */
	@Query("select e from Entitlment e where e.role.id = :roleId")
	public List<Entitlment> findAll(@Param("roleId")Long roleId);
	
	/**
	 * 查找对应角色的菜单
	 * @return
	 */
	@Query("select distinct e.menu from Entitlment e where e.role.id = :roleId")
	public List<Menu> findMenuAuth(@Param("roleId")Long roleId);
	
	/**
	 * 查找对应角色的启用菜单
	 * @return
	 */
	@Query("select distinct e.menu from Entitlment e where e.role.id in(:roleIds) and e.menu.isUsed = true")
	public List<Menu> findMenuAuthEnable(@Param("roleIds")Set<Long> roleIds);
	
	/**
	 * 查找对应角色的页面
	 * @return
	 */
	@Query("select distinct e.page from Entitlment e where e.role.id = :roleId")
	public List<Page> findPageAuth(@Param("roleId")Long roleId);
	
	/**
	 * 查找对应角色的启用页面
	 * @return
	 */
	@Query("select distinct e.page from Entitlment e where e.role.id in(:roleIds) and e.page.isUsed = true")
	public List<Page> findPageAuthEnable(@Param("roleIds")Set<Long> roleIds);
	
	/**
	 * 查找对应角色的页面资源
	 * @return
	 */
	@Query("select distinct e.pageSource from Entitlment e where e.role.id = :roleId")
	public List<PageSource> findPageSourceAuth(@Param("roleId")Long roleId);
	
	/**
	 * 查找对应角色的启用的页面资源
	 * @return
	 */
	@Query("select distinct e.pageSource from Entitlment e where e.role.id in(:roleIds) and e.pageSource.isUsed = true")
	public List<PageSource> findPageSourceAuthEnable(@Param("roleIds")Set<Long> roleIds);
	
	/**
	 * 查找对应角色的组织机构
	 * @return
	 */
	@Query("select e.organization from Entitlment e where e.role.id = :roleId")
	public List<Organization> findOrgAuth(@Param("roleId")Long roleId);
	
	/**
	 * 查找对应角色的组织机构
	 * @return
	 */
	@Query("select distinct e.organization from Entitlment e where e.role.id in(:roleIds)")
	public List<Organization> findOrgAuth(@Param("roleIds")Set<Long> roleIds);
	
	/**
	 * 根据角色id删除对应的页面资源权限
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("delete from Entitlment e where e.role.id = :roleId and e.organization is NULL")
	public void deleteByRole(@Param("roleId")Long roleId);
	
	/**
	 * 根据角色id删除对应的页面资源权限
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("delete from Entitlment e where e.role.id = :roleId and e.organization is not NULL")
	public void deleteDataAuthByRole(@Param("roleId")Long roleId);
}
