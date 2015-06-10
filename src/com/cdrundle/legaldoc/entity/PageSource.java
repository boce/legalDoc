package com.cdrundle.legaldoc.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.cdrundle.legaldoc.base.LongIdEntity;

/**
 * 页面资源信息
 * 
 * @author xiaokui.li
 * 
 */
@Entity
@Table(name = "sysmgt_pagesource")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PageSource extends LongIdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	@Column(name = "is_used")
	private Boolean isUsed;

	/**
	 * 页面
	 */
	@ManyToOne(cascade = { CascadeType.REMOVE })
	@JoinColumn(name = "page")
	private Page page;

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

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "sysmgt_pagesource_url", joinColumns = @JoinColumn(name = "pagesource_id", nullable = false))
	@Column(name = "url", length = 255, nullable = false)
	@OrderColumn(name = "ordinal")
	@Fetch(FetchMode.SUBSELECT)
	private List<String> urls;

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

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
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

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

}
