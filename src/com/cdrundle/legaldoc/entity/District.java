package com.cdrundle.legaldoc.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;

/**
 * 地区
 * @author xiaokui.li
 *
 */
@Entity
@Table(name = "sysmgt_district")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class District extends LongIdEntity{

	
	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	private String name;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE}, mappedBy = "parent")
	private List<District> childDistricts = new ArrayList<District>();
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent")
	private District parent;
	
	/**
	 * 是否启用
	 */
	@Column(name="is_used")
	private Boolean isUsed;

	/**
	 * 对应的组织机构
	 */
	@OneToMany
	private List<Organization> organizations;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public District getParent() {
		return parent;
	}

	public void setParent(District parent) {
		this.parent = parent;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public List<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
	}

	public List<District> getChildDistricts() {
		return childDistricts;
	}

	public void setChildDistricts(List<District> childDistricts) {
		this.childDistricts = childDistricts;
	}
	
}
