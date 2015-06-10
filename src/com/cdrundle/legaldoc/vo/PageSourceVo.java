package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.PageSource;

/**
 * 页面资源信息
 * 
 * @author xiaokui.li
 * 
 */
public class PageSourceVo implements Comparable<PageSourceVo> {

	/**
	 * 编号
	 */
	private Long id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 编码
	 */
	private String code;

	/**
	 * 是否启用
	 */
	private Boolean isUsed;

	/**
	 * 页面
	 */
	private PageVo page;

	/**
	 * 图标
	 */
	private String icon;

	/**
	 * 显示顺序
	 */
	private Integer displayOrder;

	/**
	 * 访问的url
	 */
	private List<String> urls;
	
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public PageVo getPage() {
		return page;
	}

	public void setPage(PageVo page) {
		this.page = page;
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

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public static PageSourceVo createVo(PageSource pageSource) {
		PageSourceVo pageSourceVo = new PageSourceVo();
		if (pageSource == null) {
			return pageSourceVo;
		}
		pageSourceVo.setId(pageSource.getId());
		pageSourceVo.setCode(pageSource.getCode());
		pageSourceVo.setName(pageSource.getName());
		pageSourceVo.setIsUsed(pageSource.getIsUsed());
		pageSourceVo.setPage(PageVo.createVo(pageSource.getPage()));
		pageSourceVo.setIcon(pageSource.getIcon());
		pageSourceVo.setDisplayOrder(pageSource.getDisplayOrder());
		pageSourceVo.setUrls(pageSource.getUrls());
		return pageSourceVo;
	}

	public static List<PageSourceVo> createVoList(List<PageSource> pageSourceList) {
		List<PageSourceVo> pageSourceVoList = new ArrayList<>();
		if (pageSourceList == null || pageSourceList.size() == 0) {
			return pageSourceVoList;
		}
		for (Iterator<PageSource> iterator = pageSourceList.iterator(); iterator.hasNext();) {
			pageSourceVoList.add(createVo(iterator.next()));
		}
		return pageSourceVoList;
	}

	@Override
	public int compareTo(PageSourceVo o) {
		if(this.getDisplayOrder() == null){
			return 0;
		}
		return this.getDisplayOrder().compareTo(o.getDisplayOrder());
	}

}
