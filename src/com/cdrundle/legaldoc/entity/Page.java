package com.cdrundle.legaldoc.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;

/**
 * 页面信息
 * 
 * @author xiaokui.li
 * 
 */
@Entity
@Table(name = "sysmgt_page")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Page extends LongIdEntity {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * 地址
	 */
	private String url;

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
	 * 图标
	 */
	private String icon;
	
	/**
	 * 显示顺序
	 */
	@Column(name = "display_order")
	private Integer displayOrder;
	/**
	 * 菜单
	 */
	@ManyToOne(cascade = { CascadeType.REMOVE })
	@JoinColumn(name = "menu", nullable = false)
	private Menu menu;

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

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
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

}
