package com.cdrundle.legaldoc.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;

/**
 * 菜单信息表
 * 
 * @author xiaokui.li
 * 
 */
@Entity
@Table(name = "sysmgt_menu")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Menu extends LongIdEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * 深度
	 */
	private int level;

	/**
	 * 是否叶节点
	 */
	@Column(name = "is_leaf")
	private Boolean isLeaf;

	/**
	 * 是否启用
	 */
	@Column(name = "is_used")
	private Boolean isUsed;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 显示顺序
	 */
	@Column(name = "display_order")
	private Integer displayOrder;
	
	/**
	 * 子菜单
	 */
	@OneToMany(mappedBy = "parentMenu", cascade = { CascadeType.REMOVE })
	private List<Menu> childMenus = new ArrayList<Menu>();

	/**
	 * 父级菜单
	 */
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "parent")
	private Menu parentMenu;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
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

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public List<Menu> getChildMenus() {
		return childMenus;
	}

	public void setChildMenus(List<Menu> childMenus) {
		this.childMenus = childMenus;
	}

	public Menu getParentMenu() {
		return parentMenu;
	}

	public void setParentMenu(Menu parentMenu) {
		this.parentMenu = parentMenu;
	}

}
