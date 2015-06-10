package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.enums.OrgType;

/**
 * 组织机构表
 * 
 * @author xiaokui.li
 * 
 */
public class OrganizationVo {

	/**
	 * 编号
	 */
	private Long id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 联系电话
	 */
	private String phone;

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 组织结构类型
	 */
	private OrgType orgType;

	/**
	 * webservice服务地址
	 */
	private String webserviceUrl;

	/**
	 * 是否启用
	 */
	private Boolean isUsed;

	private OrgShortVo parentOrganization;

	private List<OrganizationVo> childOrganization;

	/**
	 * 区域
	 */
	private DistrictVo district;

	/**
	 * 合法性审查单位
	 */
	private OrgShortVo reviewUnit;

	/**
	 * 备案审查单位
	 */
	private OrgShortVo recordUnit;

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public OrgType getOrgType() {
		return orgType;
	}

	public void setOrgType(OrgType orgType) {
		this.orgType = orgType;
	}

	public String getWebserviceUrl() {
		return webserviceUrl;
	}

	public void setWebserviceUrl(String webserviceUrl) {
		this.webserviceUrl = webserviceUrl;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public OrgShortVo getParentOrganization() {
		return parentOrganization;
	}

	public void setParentOrganization(OrgShortVo parentOrganization) {
		this.parentOrganization = parentOrganization;
	}

	public List<OrganizationVo> getChildOrganization() {
		return childOrganization;
	}

	public void setChildOrganization(List<OrganizationVo> childOrganization) {
		this.childOrganization = childOrganization;
	}

	public DistrictVo getDistrict() {
		return district;
	}

	public void setDistrict(DistrictVo district) {
		this.district = district;
	}

	public OrgShortVo getReviewUnit() {
		return reviewUnit;
	}

	public void setReviewUnit(OrgShortVo reviewUnit) {
		this.reviewUnit = reviewUnit;
	}

	public OrgShortVo getRecordUnit() {
		return recordUnit;
	}

	public void setRecordUnit(OrgShortVo recordUnit) {
		this.recordUnit = recordUnit;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public static OrganizationVo createVo(Organization organization) {
		OrganizationVo organizationVo = new OrganizationVo();
		if (organization == null) {
			return organizationVo;
		}
		organizationVo.setId(organization.getId());
		organizationVo.setName(organization.getName());
		organizationVo.setAddress(organization.getAddress());
		organizationVo.setPhone(organization.getPhone());
		organizationVo.setWebserviceUrl(organizationVo.getWebserviceUrl());
		organizationVo.setIsUsed(organization.getIsUsed());
		organizationVo.setOrgType(organization.getOrgType());
		organizationVo.setParentOrganization(OrgShortVo.createVo(organization.getParentOrganization()));
		organizationVo.setDistrict(DistrictVo.createVo(organization.getDistrict()));
		organizationVo.setWebserviceUrl(organization.getWebserviceUrl());
		organizationVo.setReviewUnit(OrgShortVo.createVo(organization.getReviewUnit()));
		organizationVo.setRecordUnit(OrgShortVo.createVo(organization.getRecordUnit()));
		organizationVo.setDisplayOrder(organization.getDisplayOrder());
		return organizationVo;
	}

	public static List<OrganizationVo> createVoList(List<Organization> organizationList) {
		List<OrganizationVo> organizationVoList = new ArrayList<>();
		if (organizationList == null || organizationList.size() == 0) {
			return organizationVoList;
		}
		for (Iterator<Organization> iterator = organizationList.iterator(); iterator.hasNext();) {
			organizationVoList.add(createVo(iterator.next()));

		}
		return organizationVoList;
	}
}
