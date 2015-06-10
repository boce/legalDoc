package com.cdrundle.legaldoc.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;
import com.cdrundle.legaldoc.enums.OrgType;

/**
 * 组织机构表
 * @author xiaokui.li
 *
 */
@Entity
@Table(name = "sysmgt_organization")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Organization extends LongIdEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 名称
	 */
	@Column(length = 100)
	private String name;
	
	/**
	 * 联系电话
	 */
	@Column(length = 50)
	private String phone;
	
	/**
	 * 地址
	 */
	@Column(length = 50)
	private String address;
	
	/**
	 * 组织结构类型
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "org_type")
	private OrgType orgType;
	
	/**
	 * webservice服务地址
	 */
	@Column(name = "webservice_url")
	private String webserviceUrl;
	
	/**
	 * 是否启用
	 */
	@Column(name = "is_used")
	private Boolean isUsed;

	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE}, mappedBy = "parentOrganization")
	private List<Organization> childOrganizations = new ArrayList<Organization>();
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent")
	private Organization parentOrganization;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "district")
	private District district;
	
	/**
	 * 合法性审查单位
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rev_unit")
	private Organization reviewUnit;
	
	/**
	 * 备案审查单位
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rec_rev_unit")
	private Organization recordUnit;
	
	/**
	 * 显示顺序
	 */
	@Column(name = "display_order")
	private Integer displayOrder;
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public OrgType getOrgType()
	{
		return orgType;
	}

	public void setOrgType(OrgType orgType)
	{
		this.orgType = orgType;
	}

	public String getWebserviceUrl()
	{
		return webserviceUrl;
	}

	public void setWebserviceUrl(String webserviceUrl)
	{
		this.webserviceUrl = webserviceUrl;
	}

	public Boolean getIsUsed()
	{
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed)
	{
		this.isUsed = isUsed;
	}

	public List<Organization> getChildOrganizations()
	{
		return childOrganizations;
	}

	public void setChildOrganizations(List<Organization> childOrganizations)
	{
		this.childOrganizations = childOrganizations;
	}

	public Organization getParentOrganization()
	{
		return parentOrganization;
	}

	public void setParentOrganization(Organization parentOrganization)
	{
		this.parentOrganization = parentOrganization;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Organization getReviewUnit() {
		return reviewUnit;
	}

	public void setReviewUnit(Organization reviewUnit) {
		this.reviewUnit = reviewUnit;
	}

	public Organization getRecordUnit() {
		return recordUnit;
	}

	public void setRecordUnit(Organization recordUnit) {
		this.recordUnit = recordUnit;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
}
