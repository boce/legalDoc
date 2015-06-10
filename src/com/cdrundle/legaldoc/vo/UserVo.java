package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.User;

/**
 * 用户信息表
 * 
 * @author xiaokui.li
 * 
 */
public class UserVo
{

	/**
	 * 编号
	 */
	private Long id;

	/**
	 * 用户姓名
	 */
	private String name;

	/**
	 * 登录用户名
	 */
	private String userName;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 固定电话
	 */
	private String phone;

	/**
	 * 移动电话
	 */
	private String mobile;

	/**
	 * 邮件地址
	 */
	private String email;

	/**
	 * 是否负责人
	 */
	private Boolean isIncharge;

	/**
	 * 是否启用
	 */
	private Boolean isUsed;

	private OrgShortVo organization;

	private List<RoleVo> roles = new ArrayList<RoleVo>();

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

	public OrgShortVo getOrganization()
	{
		return organization;
	}

	public void setOrganization(OrgShortVo organization)
	{
		this.organization = organization;
	}

	public List<RoleVo> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleVo> roles) {
		this.roles = roles;
	}

	public static UserVo createVo(User user)
	{
		UserVo userVo = new UserVo();
		if(user == null)
		{
			return userVo;
		}
		userVo.setId(user.getId());
		userVo.setName(user.getName());
		userVo.setUserName(user.getUserName());
		userVo.setPassword(user.getPassword());
		userVo.setPhone(user.getPhone());
		userVo.setEmail(user.getEmail());
		userVo.setMobile(user.getMobile());
		userVo.setIsIncharge(user.getIsIncharge());
		userVo.setIsUsed(user.getIsUsed());
		userVo.setOrganization(OrgShortVo.createVoNoChild(user.getOrganization()));
		return userVo;
	}
	
	public static List<UserVo> createVoList(List<User> userList)
	{
		List<UserVo> userVoList = new ArrayList<>();
		if(userList == null || userList.isEmpty())
		{
			return userVoList;
		}
		for (Iterator<User> iterator = userList.iterator(); iterator.hasNext();)
		{
			userVoList.add(createVo(iterator.next()));
		}
		return userVoList;
	}
	
}
