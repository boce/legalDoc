package com.cdrundle.legaldoc.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.cdrundle.legaldoc.base.LongIdEntity;

/**
 * 用户角色
 * 
 * @author xiaokui.li
 * 
 */
@Entity
@Table(name = "sysmgt_role")
public class Role extends LongIdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 是否启用
	 */
	@Column(name = "is_used")
	private Boolean isUsed;

	// bi-directional many-to-many association to User
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "roles")
	private List<User> users = new ArrayList<User>();

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

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
