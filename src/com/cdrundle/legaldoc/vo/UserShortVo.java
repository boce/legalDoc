package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.User;

/**
 * 公用用户vo
 * @author xiaokui.li
 *
 */
public class UserShortVo
{
	/**
	 * 编号
	 */
	private Long id;
	
	/**
	 * 姓名
	 */
	private String name;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public static UserShortVo createVo(User user)
	{
		UserShortVo commonUserVo = new UserShortVo();
		if(user == null)
		{
			return commonUserVo;
		}
		commonUserVo.setId(user.getId());
		commonUserVo.setName(user.getName());
		return commonUserVo;
	}
	
	public static List<UserShortVo> createVoList(List<User> userList)
	{
		List<UserShortVo> commonUserVoList = new ArrayList<>();
		if(userList == null || userList.size() == 0)
		{
			return commonUserVoList;
		}
		for (Iterator<User> iterator = userList.iterator(); iterator.hasNext();)
		{
			commonUserVoList.add(createVo(iterator.next()));
		}
		return commonUserVoList;
	}
	
	
}
