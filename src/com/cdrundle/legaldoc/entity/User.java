package com.cdrundle.legaldoc.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;

/**
 * 用户信息表
 * 
 * @author xiaokui.li
 * 
 */
@Entity
@Table(name = "sysmgt_user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends LongIdEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户姓名
	 */
	@Column(name = "name", length = 50, nullable = false)
	private String name;

	/**
	 * 登录用户名
	 */
	@Column(name = "login_name", length = 50, nullable = false)
	private String userName;

	/**
	 * 密码
	 */
	@Column(length = 50, nullable = false)
	private String password;

	/**
	 * 固定电话
	 */
	@Column(length = 50)
	private String phone;

	/**
	 * 移动电话
	 */
	@Column(length = 50)
	private String mobile;

	/**
	 * 邮件地址
	 */
	@Column(length = 50)
	private String email;

	/**
	 * 是否负责人
	 */
	@Column(name = "is_incharge")
	private Boolean isIncharge;

	/**
	 * 是否启用
	 */
	@Column(name = "is_used")
	private Boolean isUsed;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
	@JoinColumn(name = "sysmgt_org_org")
	private Organization organization;

	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "sysmgt_user_role", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "role_id") })
	private List<Role> roles = new ArrayList<Role>();

	public User() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getIsIncharge() {
		return isIncharge;
	}

	public void setIsIncharge(Boolean isIncharge) {
		this.isIncharge = isIncharge;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

}
