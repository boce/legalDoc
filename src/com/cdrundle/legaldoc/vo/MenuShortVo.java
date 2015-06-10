package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.Menu;

/**
 * 菜单信息表
 * 
 * @author xiaokui.li
 * 
 */
public class MenuShortVo implements Comparable<MenuShortVo>{

	/**
	 * 编号
	 */
	private Long id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 是否叶节点
	 */
	private Boolean isLeaf;

	/**
	 * 显示顺序
	 */
	private Integer displayOrder;
	
	/**
	 * 父级菜单
	 */
	private Long parent;

	/**
	 * 菜单对应的页面资源
	 */
	private List<PageShortVo> pages = new ArrayList<>();

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

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Long getParent() {
		return parent;
	}

	public void setParent(Long parent) {
		this.parent = parent;
	}

	public List<PageShortVo> getPages() {
		return pages;
	}

	public void setPages(List<PageShortVo> pages) {
		this.pages = pages;
	}

	public static MenuShortVo createVo(Menu menu) {
		MenuShortVo menuVo = new MenuShortVo();
		if (menu == null) {
			return menuVo;
		}
		menuVo.setId(menu.getId());
		menuVo.setName(menu.getName());
		Menu parentMenu = menu.getParentMenu();
		if (parentMenu != null) {
			menuVo.setParent(parentMenu.getId());
		}
		menuVo.setIsLeaf(menu.getIsLeaf());
		menuVo.setDisplayOrder(menu.getDisplayOrder());
		return menuVo;
	}

	public static List<MenuShortVo> createVoList(List<Menu> menuList) {
		List<MenuShortVo> menuVoList = new ArrayList<>();
		if (menuList == null || menuList.size() == 0) {
			return menuVoList;
		}
		for (Iterator<Menu> iterator = menuList.iterator(); iterator.hasNext();) {
			menuVoList.add(createVo(iterator.next()));
		}
		return menuVoList;
	}

	@Override
	public int compareTo(MenuShortVo menuVo) {
		if(this.getDisplayOrder() == null){
			return 0;
		}
		return this.getDisplayOrder().compareTo(menuVo.getDisplayOrder());
	}

}
