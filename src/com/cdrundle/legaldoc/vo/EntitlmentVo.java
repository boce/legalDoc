package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.Entitlment;


/**
 * 赋权信息
 * @author xiaokui.li
 *
 */
public class EntitlmentVo
{

	/**
	 * 编号
	 */
	private Long id;
	
	/**
	 * 角色
	 */
	private RoleVo role;
	
	/**
	 * 菜单
	 */
	private MenuVo menu;
	
	/**
	 * 页面
	 */
	private PageVo page;
	
	/**
	 * 页面资源
	 */
	private PageSourceVo pageSource;
	
	/**
	 * 组织机构
	 */
	private OrgShortVo organization;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public RoleVo getRole()
	{
		return role;
	}

	public void setRole(RoleVo role)
	{
		this.role = role;
	}

	public MenuVo getMenu()
	{
		return menu;
	}

	public void setMenu(MenuVo menu)
	{
		this.menu = menu;
	}

	public PageVo getPage()
	{
		return page;
	}

	public void setPage(PageVo page)
	{
		this.page = page;
	}

	public PageSourceVo getPageSource()
	{
		return pageSource;
	}

	public void setPageSource(PageSourceVo pageSource)
	{
		this.pageSource = pageSource;
	}

	public OrgShortVo getOrganization()
	{
		return organization;
	}

	public void setOrganization(OrgShortVo organization)
	{
		this.organization = organization;
	}

	public static EntitlmentVo createVo(Entitlment entitlment)
	{
		EntitlmentVo entitlmentVo = new EntitlmentVo();
		if(entitlment == null)
		{
			return entitlmentVo;
		}
		entitlmentVo.setId(entitlment.getId());
		entitlmentVo.setMenu(MenuVo.createVo(entitlment.getMenu()));
		entitlmentVo.setOrganization(OrgShortVo.createVoNoChild(entitlment.getOrganization()));
		entitlmentVo.setRole(RoleVo.createVo(entitlment.getRole()));
		entitlmentVo.setPage(PageVo.createVo(entitlment.getPage()));
		entitlmentVo.setPageSource(PageSourceVo.createVo(entitlment.getPageSource()));
		return entitlmentVo;
	}
	
	public static List<EntitlmentVo> createVoList(List<Entitlment> entitlmentList)
	{
		List<EntitlmentVo> entitlmentVoList = new ArrayList<>();
		if(entitlmentList == null || entitlmentList.size() == 0)
		{
			return entitlmentVoList;
		}
		for (Iterator<Entitlment> iterator = entitlmentList.iterator(); iterator.hasNext();)
		{
			entitlmentVoList.add(createVo(iterator.next()));
		}
		return entitlmentVoList;
	}
	
}
