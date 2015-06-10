package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.Role;


/**
 * 用户角色
 * 
 * @author xiaokui.li
 * 
 */
public class RoleVo
{

	/**
	 * 编号
	 */
	private Long id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 是否启用
	 */
	private Boolean isUsed;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Boolean getIsUsed()
	{
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed)
	{
		this.isUsed = isUsed;
	}

	public static RoleVo createVo(Role role)
	{
		RoleVo roleVo = new RoleVo();
		if(role == null)
		{
			return roleVo;
		}
		roleVo.setId(role.getId());
		roleVo.setName(role.getName());
		roleVo.setDescription(role.getDescription());
		roleVo.setIsUsed(role.getIsUsed());
		return roleVo;
	}
	
	public static List<RoleVo> createVoList(List<Role> roleList)
	{
		List<RoleVo> roleVoList = new ArrayList<>();
		if(roleList == null || roleList.size() == 0)
		{
			return roleVoList;
		}
		for (Iterator<Role> iterator = roleList.iterator(); iterator.hasNext();)
		{
			roleVoList.add(createVo(iterator.next()));
		}
		return roleVoList;
	}
	
}
