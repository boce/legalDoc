package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.Page;

/**
 * 页面信息
 * 
 * @author xiaokui.li
 * 
 */
public class PageVo implements Comparable<PageVo> {

	/**
	 * 编号
	 */
	private Long id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 地址
	 */
	private String url;

	/**
	 * 是否启用
	 */
	private Boolean isUsed;

	/**
	 * 描述
	 */
	private String description;

	private MenuVo menu;

	private List<PageSourceVo> pageSources = new ArrayList<>();

	/**
	 * 图标
	 */
	private String icon;

	/**
	 * 显示顺序
	 */
	private Integer displayOrder;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public MenuVo getMenu() {
		return menu;
	}

	public void setMenu(MenuVo menu) {
		this.menu = menu;
	}

	public List<PageSourceVo> getPageSources() {
		return pageSources;
	}

	public void setPageSources(List<PageSourceVo> pageSources) {
		this.pageSources = pageSources;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public static PageVo createVo(Page page) {
		PageVo pageVo = new PageVo();
		if (page == null) {
			return pageVo;
		}
		pageVo.setId(page.getId());
		pageVo.setName(page.getName());
		pageVo.setDescription(page.getDescription());
		pageVo.setMenu(MenuVo.createVo(page.getMenu()));
		pageVo.setIsUsed(page.getIsUsed());
		pageVo.setUrl(page.getUrl());
		pageVo.setIcon(page.getIcon());
		pageVo.setDisplayOrder(page.getDisplayOrder());
		return pageVo;
	}

	public static List<PageVo> createVoList(List<Page> pageList) {
		List<PageVo> pageVoList = new ArrayList<>();
		if (pageList == null || pageList.size() == 0) {
			return pageVoList;
		}
		for (Iterator<Page> iterator = pageList.iterator(); iterator.hasNext();) {
			pageVoList.add(createVo(iterator.next()));
		}
		return pageVoList;
	}

	@Override
	public int compareTo(PageVo pageVo) {
		if(this.getDisplayOrder() == null){
			return 0;
		}
		return this.getDisplayOrder().compareTo(pageVo.getDisplayOrder());
	}

}
