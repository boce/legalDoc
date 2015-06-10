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
public class MenuVo implements Comparable<MenuVo>{

	/**
	 * 编号
	 */
	private Long id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 深度
	 */
	private int level;

	/**
	 * 是否叶节点
	 */
	private Boolean isLeaf;

	/**
	 * 是否启用
	 */
	private Boolean isUsed;

	/**
	 * 描述
	 */
	private String description;

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
	private List<PageVo> pages = new ArrayList<>();

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
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

	public List<PageVo> getPages() {
		return pages;
	}

	public void setPages(List<PageVo> pages) {
		this.pages = pages;
	}

	public static MenuVo createVo(Menu menu) {
		MenuVo menuVo = new MenuVo();
		if (menu == null) {
			return menuVo;
		}
		menuVo.setId(menu.getId());
		menuVo.setName(menu.getName());
		menuVo.setDescription(menu.getDescription());
		Menu parentMenu = menu.getParentMenu();
		if (parentMenu != null) {
			menuVo.setParent(parentMenu.getId());
		}
		menuVo.setIsLeaf(menu.getIsLeaf());
		menuVo.setLevel(menu.getLevel());
		menuVo.setIsUsed(menu.getIsUsed());
		menuVo.setDisplayOrder(menu.getDisplayOrder());
		return menuVo;
	}

	public static List<MenuVo> createVoList(List<Menu> menuList) {
		List<MenuVo> menuVoList = new ArrayList<>();
		if (menuList == null || menuList.size() == 0) {
			return menuVoList;
		}
		for (Iterator<Menu> iterator = menuList.iterator(); iterator.hasNext();) {
			menuVoList.add(createVo(iterator.next()));
		}
		return menuVoList;
	}

	@Override
	public int compareTo(MenuVo menuVo) {
		if(this.getDisplayOrder() == null){
			return 0;
		}
		return this.getDisplayOrder().compareTo(menuVo.getDisplayOrder());
	}

}
