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
public class PageSourceShortVo implements Comparable<PageSourceShortVo> {

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

	public static PageSourceShortVo createVo(PageSource pageSource) {
		PageSourceShortVo pageSourceVo = new PageSourceShortVo();
		if (pageSource == null) {
			return pageSourceVo;
		}
		pageSourceVo.setId(pageSource.getId());
		pageSourceVo.setCode(pageSource.getCode());
		pageSourceVo.setName(pageSource.getName());
		pageSourceVo.setIcon(pageSource.getIcon());
		pageSourceVo.setDisplayOrder(pageSource.getDisplayOrder());
		pageSourceVo.setUrls(pageSource.getUrls());
		return pageSourceVo;
	}

	public static List<PageSourceShortVo> createVoList(List<PageSource> pageSourceList) {
		List<PageSourceShortVo> pageSourceVoList = new ArrayList<>();
		if (pageSourceList == null || pageSourceList.size() == 0) {
			return pageSourceVoList;
		}
		for (Iterator<PageSource> iterator = pageSourceList.iterator(); iterator.hasNext();) {
			pageSourceVoList.add(createVo(iterator.next()));
		}
		return pageSourceVoList;
	}

	@Override
	public int compareTo(PageSourceShortVo o) {
		if(this.getDisplayOrder() == null){
			return 0;
		}
		return this.getDisplayOrder().compareTo(o.getDisplayOrder());
	}

}
