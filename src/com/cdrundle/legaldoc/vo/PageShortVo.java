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
public class PageShortVo implements Comparable<PageShortVo> {

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

	private List<PageSourceShortVo> pageSources = new ArrayList<>();

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

	public List<PageSourceShortVo> getPageSources() {
		return pageSources;
	}

	public void setPageSources(List<PageSourceShortVo> pageSources) {
		this.pageSources = pageSources;
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

	public static PageShortVo createVo(Page page) {
		PageShortVo pageVo = new PageShortVo();
		if (page == null) {
			return pageVo;
		}
		pageVo.setId(page.getId());
		pageVo.setName(page.getName());
		pageVo.setUrl(page.getUrl());
		pageVo.setIcon(page.getIcon());
		pageVo.setDisplayOrder(page.getDisplayOrder());
		return pageVo;
	}

	public static List<PageShortVo> createVoList(List<Page> pageList) {
		List<PageShortVo> pageVoList = new ArrayList<>();
		if (pageList == null || pageList.size() == 0) {
			return pageVoList;
		}
		for (Iterator<Page> iterator = pageList.iterator(); iterator.hasNext();) {
			pageVoList.add(createVo(iterator.next()));
		}
		return pageVoList;
	}

	@Override
	public int compareTo(PageShortVo pageVo) {
		if(this.getDisplayOrder() == null){
			return 0;
		}
		return this.getDisplayOrder().compareTo(pageVo.getDisplayOrder());
	}

}
