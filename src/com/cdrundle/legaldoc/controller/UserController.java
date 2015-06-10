package com.cdrundle.legaldoc.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.service.IUserService;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.RoleVo;
import com.cdrundle.legaldoc.vo.UserVo;

/**
 * 用户管理类
 * 
 * @author xiaokui.li
 * 
 */
@Controller
public class UserController {
	@Autowired
	IUserService userService;

	/**
	 * 根据组织机构id查询用户
	 * 
	 * @param req
	 * @param res
	 * @param orgId
	 * @return
	 */
	@RequestMapping("/user/getUserByOrg")
	@ResponseBody
	public List<UserVo> getUserByOrg(Long orgId) {
		List<UserVo> userVos = userService.findByOrgId(orgId);
		return userVos;
	}

	/**
	 * 根据组织机构id查询对应上级组织机构用户
	 * 
	 * @param req
	 * @param res
	 * @param orgId
	 * @return
	 */
	@RequestMapping("/user/getSuperUserByOrg")
	@ResponseBody
	public List<UserVo> getSuperUserByOrg(Long orgId) {
		List<UserVo> userVos = userService.findSuperByOrgId(orgId);
		return userVos;
	}
	
	/**
	 * 根据名称或者组织机构查询用户表格数据
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/user/getUserRef")
	public Map<String, Object> getUserRef(String name, Long orgId, PageParam page) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<UserVo> userRefs = userService.findUserRef(name, orgId, page.getPage(), page.getRows());
		result.put("total", userRefs.getTotalElements());
		result.put("rows", userRefs.getContent());
		return result;
	}

	/**
	 * 保存或者修改用户
	 * 
	 * @param req
	 * @param res
	 * @param orgVo
	 * @return
	 */
	@RequestMapping("/user/saveOrUpdate")
	@ResponseBody
	public String saveOrUpdate(UserVo userVo) {
		String result = "";
		try {
			UserVo savedUser = userService.saveOrUpdate(userVo);
			if (savedUser != null) {
				result = "{\"success\":true}";
			} else {
				result = "{\"success\":false}";
			}
		} catch (NoSuchAlgorithmException e) {
			result = "{\"success\":false}";
		}
		return result;
	}

	/**
	 * 假删除用户
	 * 
	 * @param orgId
	 * @return
	 */
	@RequestMapping("/user/deleteUserById")
	@ResponseBody
	public String deleteUserById(Long userId) {
		boolean isSuccess = userService.deleteById(userId);
		return isSuccess ? "{\"success\":true}" : "{\"success\":false}";
	}

	/**
	 * 根据用户id查询对应的角色
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping("/user/getRoleRef")
	@ResponseBody
	public Map<String, Object> getRoleRef(Long userId) {
		Map<String, Object> result = new HashMap<>();
		List<RoleVo> roles = userService.findRoleById(userId);
		result.put("total", roles.size());
		result.put("rows", roles);
		return result;
	}

	/**
	 * 根据用户id查询对应的角色
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping("/user/authorize")
	@ResponseBody
	public String authorize(@RequestParam(value = "authRoles[]", required = false) Long[] authRoles, Long userId) {
		Set<Long> roleIds = new HashSet<>();
		if (authRoles != null) {
			Collections.addAll(roleIds, authRoles);
		}
		boolean isSuccess = userService.authorize(roleIds, userId);
		return isSuccess ? "{\"success\":true}" : "{\"success\":false}";
	}

	/**
	 * 修改当前登录用户密码
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping("/user/modifyPasswordForUser")
	@ResponseBody
	public String modifyPasswordForUser(String oldPassword, String newPassword) {
		String msg = "";
		try {
			boolean isCorrected = userService.judgePassword(oldPassword);
			if (isCorrected) {
				boolean isSuccess = userService.updatePassword(newPassword);
				if (isSuccess) {
					msg = "{\"success\":true}";
				} else {
					msg = "{\"success\":false}";
				}
			}else{
				msg = "{\"success\":false,msg:\"输入的密码不正确！\"}";
			}
		} catch (NoSuchAlgorithmException e) {
			msg = "{\"success\":false}";
		}
		return msg;
	}
}
