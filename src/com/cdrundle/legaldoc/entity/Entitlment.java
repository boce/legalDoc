package com.cdrundle.legaldoc.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;

/**
 * 赋权信息
 * @author xiaokui.li
 *
 */
@Entity
@Table(name = "sysmgt_entitlement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Entitlment extends LongIdEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 角色
	 */
	@ManyToOne
	@JoinColumn(name = "role")
	private Role role;
	
	/**
	 * 菜单
	 */
	@ManyToOne
	@JoinColumn(name = "menu")
	private Menu menu;
	
	/**
	 * 页面
	 */
	@ManyToOne
	@JoinColumn(name = "page")
	private Page page;
	
	/**
	 * 页面资源
	 */
	@ManyToOne
	@JoinColumn(name = "page_source")
	private PageSource pageSource;
	
	/**
	 * 组织机构
	 */
	@ManyToOne
	@JoinColumn(name = "organization")
	private Organization organization;

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public Menu getMenu()
	{
		return menu;
	}

	public void setMenu(Menu menu)
	{
		this.menu = menu;
	}

	public Page getPage()
	{
		return page;
	}

	public void setPage(Page page)
	{
		this.page = page;
	}

	public PageSource getPageSource()
	{
		return pageSource;
	}

	public void setPageSource(PageSource pageSource)
	{
		this.pageSource = pageSource;
	}

	public Organization getOrganization()
	{
		return organization;
	}

	public void setOrganization(Organization organization)
	{
		this.organization = organization;
	}
	
}
