package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.District;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.enums.OrgType;

/**
 * 公用组织结构
 * @author xiaokui.li
 *
 */
public class OrgShortVo
{
	/**
	 * 编号
	 */
	private Long id;
	
	/**
	 * 名称
	 */
	private String text;
	
	/**
	 * 组织结构类型
	 */
	private OrgType orgType;
	
	/**
	 * 子组织机构
	 */
	private List<OrgShortVo> children;
	
	/**
	 * 区域id
	 */
	private Long district;
	
	/**
	 * 显示顺序
	 */
	private Integer displayOrder;
	
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public OrgType getOrgType() {
		return orgType;
	}

	public void setOrgType(OrgType orgType) {
		this.orgType = orgType;
	}

	public List<OrgShortVo> getChildren()
	{
		return children;
	}

	public void setChildren(List<OrgShortVo> children)
	{
		this.children = children;
	}

	public Long getDistrict() {
		return district;
	}

	public void setDistrict(Long district) {
		this.district = district;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public static OrgShortVo createVo(Organization organization)
	{
		OrgShortVo orgShortVo = new OrgShortVo();
		if(organization == null)
		{
			return orgShortVo;
		}
		orgShortVo.setId(organization.getId());
		orgShortVo.setText(organization.getName());
		orgShortVo.setOrgType(organization.getOrgType());
		
		District district = organization.getDistrict();
		if(district != null)
		{
			orgShortVo.setDistrict(district.getId());
		}
		List<Organization> childOrganizations = organization.getChildOrganizations();
		if(childOrganizations != null && childOrganizations.size() > 0)
		{
			orgShortVo.setChildren(createVoList(childOrganizations));
		}
		
		orgShortVo.setDisplayOrder(organization.getDisplayOrder());
		return orgShortVo;
	}
	
	public static List<OrgShortVo> createVoList(List<Organization> organizationList)
	{
		List<OrgShortVo> orgShortVoList = new ArrayList<>();
		if(organizationList == null || organizationList.isEmpty())
		{
			return orgShortVoList;
		}
		for (Iterator<Organization> iterator = organizationList.iterator(); iterator.hasNext();)
		{
			orgShortVoList.add(createVo(iterator.next()));
		}
		return orgShortVoList;
	}
	
	public static OrgShortVo createVoNoChild(Organization organization)
	{
		OrgShortVo orgShortVo = new OrgShortVo();
		if(organization == null)
		{
			return orgShortVo;
		}
		orgShortVo.setId(organization.getId());
		orgShortVo.setText(organization.getName());
		District district = organization.getDistrict();
		if(district != null)
		{
			orgShortVo.setDistrict(district.getId());
		}
		orgShortVo.setDisplayOrder(organization.getDisplayOrder());
		return orgShortVo;
	}

	
	public static List<OrgShortVo> createVoListNoChild(List<Organization> organizationList)
	{
		List<OrgShortVo> commonOrganizationVoList = new ArrayList<>();
		if(organizationList == null || organizationList.isEmpty())
		{
			return commonOrganizationVoList;
		}
		for (Iterator<Organization> iterator = organizationList.iterator(); iterator.hasNext();)
		{
			commonOrganizationVoList.add(createVoNoChild(iterator.next()));
		}
		return commonOrganizationVoList;
	}
	
}
