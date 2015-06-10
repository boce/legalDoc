package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.District;

/**
 * 地区
 * @author xiaokui.li
 *
 */
public class DistrictVo{

	/**
	 * 编号
	 */
	private Long id;
	
	/**
	 * 名称
	 */
	private String name;
	
	private DistrictVo parent;
	
	private List<DistrictVo> childDistricts;
	
	/**
	 * 是否启用
	 */
	private Boolean isUsed;

	private List<OrgShortVo> organizations;
	
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

	public DistrictVo getParent() {
		return parent;
	}

	public void setParent(DistrictVo parent) {
		this.parent = parent;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public List<OrgShortVo> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<OrgShortVo> organizations) {
		this.organizations = organizations;
	}
	
	public List<DistrictVo> getChildDistricts() {
		return childDistricts;
	}

	public void setChildDistricts(List<DistrictVo> childDistricts) {
		this.childDistricts = childDistricts;
	}

	public static DistrictVo createVo(District district)
	{
		DistrictVo districtVo = new DistrictVo();
		if(district == null)
		{
			return districtVo;
		}
		districtVo.setId(district.getId());
		districtVo.setName(district.getName());
		districtVo.setIsUsed(district.getIsUsed());
		District parent = district.getParent();
		if(parent != null)
		{
			DistrictVo parentDistrictVo = new DistrictVo();
			parentDistrictVo.setId(parent.getId());
			parentDistrictVo.setName(parent.getName());
			districtVo.setParent(parentDistrictVo);
		}
		List<District> childDistricts = district.getChildDistricts();
		if(childDistricts != null && !childDistricts.isEmpty())
		{
			districtVo.setChildDistricts(createVoList(childDistricts));
			
		}
		return districtVo;
	}
	
	public static List<DistrictVo> createVoList(List<District> districts)
	{
		List<DistrictVo> districtVos = new ArrayList<>();
		if(districts == null || districts.isEmpty())
		{
			return districtVos;
		}
		for (Iterator<District> iterator = districts.iterator(); iterator.hasNext();) {
			districtVos.add(createVo(iterator.next()));
		}
		return districtVos;
	}
	
	public static DistrictVo createVoNoChild(District district)
	{
		DistrictVo districtVo = new DistrictVo();
		if(district == null)
		{
			return districtVo;
		}
		districtVo.setId(district.getId());
		districtVo.setName(district.getName());
		districtVo.setIsUsed(district.getIsUsed());
		District parent = district.getParent();
		if(parent != null)
		{
			DistrictVo parentDistrictVo = new DistrictVo();
			parentDistrictVo.setId(parent.getId());
			parentDistrictVo.setName(parent.getName());
			districtVo.setParent(parentDistrictVo);
		}
		return districtVo;
	}
}
