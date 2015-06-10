package com.cdrundle.legaldoc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IEntitlementService;
import com.cdrundle.legaldoc.service.IMenuService;
import com.cdrundle.legaldoc.service.IRoleService;
import com.cdrundle.legaldoc.vo.MenuVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.PageSourceVo;
import com.cdrundle.legaldoc.vo.PageVo;
import com.cdrundle.legaldoc.vo.RoleVo;
import com.cdrundle.security.IMySecurityMetadataSource;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 角色管理类
 * 
 * @author xiaokui.li
 * 
 */
@Controller
public class RoleController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	// 树节点类型(菜单，页面，页面资源)
	private static final String[] NODE_TYPE = { "menu", "page", "pageSource" };

	@Autowired
	IRoleService roleService;

	@Autowired
	IMenuService menuService;

	@Autowired
	IEntitlementService entitlementService;

	@Autowired
	IMySecurityMetadataSource mySecurityMetadataSource;
	
	/**
	 * 根据名称或者组织机构查询用户表格数据
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/role/getRoleRef")
	public Map<String, Object> getRoleRef(String description, PageParam page) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<RoleVo> userRefs = roleService.findRoleRef(description, page.getPage(), page.getRows());
		result.put("total", userRefs.getTotalElements());
		result.put("rows", userRefs.getContent());
		return result;
	}

	/**
	 * 获取所有启用的角色
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/role/getAllRoleEnable")
	public Map<String, Object> getAllRoleEnable() {
		Map<String, Object> result = new HashMap<String, Object>();
		List<RoleVo> roles = roleService.findAllEnable();
		result.put("total", roles.size());
		result.put("rows", roles);
		return result;
	}

	/**
	 * 获取所有角色
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/role/getAllRole")
	public Map<String, Object> getAllRole() {
		Map<String, Object> result = new HashMap<String, Object>();
		List<RoleVo> roles = roleService.findLikeName("");
		result.put("total", roles.size());
		result.put("rows", roles);
		return result;
	}

	/**
	 * 保存或者修改角色
	 * 
	 * @param req
	 * @param res
	 * @param orgVo
	 * @return
	 */
	@RequestMapping("/role/saveOrUpdate")
	@ResponseBody
	public String saveOrUpdate(RoleVo roleVo) {
		RoleVo savedRole = roleService.saveOrUpdate(roleVo);
		String result = "";
		if (savedRole != null) {
			result = "{\"success\":true}";
		} else {
			result = "{\"success\":false}";
		}
		return result;
	}

	/**
	 * 假删除角色
	 * 
	 * @param req
	 * @param res
	 * @param orgId
	 * @return
	 */
	@RequestMapping("/role/deleteRoleById")
	@ResponseBody
	public String deleteRoleById(Long roleId) {
		boolean isSuccess;
		try {
			isSuccess = roleService.deleteByIdVirtual(roleId);
		} catch (ServiceException e) {
			isSuccess = false;
			logger.warn(e.getMessage(), e.getCause());
		}
		return isSuccess ? "{\"success\":true}" : "{\"success\":false}";
	}

	/**
	 * 获取所有页面资源
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/role/getAllMenu")
	public List<Map<String, Object>> getAllMenu() {
		List<MenuVo> voList = menuService.displayAllMenu();
		List<Map<String, Object>> treeList = convertListToTree(voList);
		// 将menu转换成树形结构所需数据格式
		List<Map<String, Object>> menuTree = convertMenuToTree(treeList, null);
		return menuTree;
	}

	/**
	 * 把list转换成easyui树所需数据结构
	 * 
	 * @param voList
	 * @return
	 */
	private List<Map<String, Object>> convertListToTree(List<MenuVo> voList) {
		List<Map<String, Object>> treeList = new ArrayList<>();
		for (Iterator<MenuVo> iterator = voList.iterator(); iterator.hasNext();) {
			MenuVo menuVo = iterator.next();
			Map<String, Object> menuMap = new HashMap<>();
			menuMap.put("id", menuVo.getId());
			menuMap.put("text", menuVo.getName());
			menuMap.put("pid", menuVo.getParent());
			menuMap.put("isLeaf", menuVo.getIsLeaf());
			Map<String, Object> menuAttrs = new HashMap<>();
			menuAttrs.put("nodeType", NODE_TYPE[0]);
			menuMap.put("attributes", menuAttrs);
			List<Map<String, Object>> pageList = new ArrayList<>();
			List<PageVo> pages = menuVo.getPages();
			for (Iterator<PageVo> iterator2 = pages.iterator(); iterator2.hasNext();) {
				PageVo pageVo = iterator2.next();
				Map<String, Object> pageMap = new HashMap<>();
				pageMap.put("id", pageVo.getId());
				pageMap.put("text", pageVo.getName());
				Map<String, Object> pageAttrs = new HashMap<>();
				pageAttrs.put("nodeType", NODE_TYPE[1]);
				pageMap.put("attributes", pageAttrs);
				List<Map<String, Object>> pageSourceList = new ArrayList<>();
				List<PageSourceVo> pageSources = pageVo.getPageSources();
				for (Iterator<PageSourceVo> iterator3 = pageSources.iterator(); iterator3.hasNext();) {
					PageSourceVo pageSourceVo = iterator3.next();
					Map<String, Object> pageSourceMap = new HashMap<>();
					pageSourceMap.put("id", pageSourceVo.getId());
					pageSourceMap.put("text", pageSourceVo.getName());
					Map<String, Object> pageSourceAttrs = new HashMap<>();
					pageSourceAttrs.put("nodeType", NODE_TYPE[2]);
					pageSourceMap.put("attributes", pageSourceAttrs);
					pageSourceList.add(pageSourceMap);
				}
				pageMap.put("children", pageSourceList);
				pageList.add(pageMap);
			}
			menuMap.put("children", pageList);
			treeList.add(menuMap);
		}
		return treeList;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> convertMenuToTree(List<Map<String, Object>> list, Long parentId) {
		List<Map<String, Object>> treeList = new ArrayList<>();
		for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			Long id = Long.parseLong(String.valueOf(map.get("id")));
			Long pId = (map.get("pid") == null) ? null : Long.parseLong(String.valueOf(map.get("pid")));
			if (parentId == pId) {
				Boolean isLeaf = (map.get("isLeaf") == null) ? true : Boolean.parseBoolean(map.get("isLeaf").toString());
				if (!isLeaf) {
					Object childObj = map.get("children");
					if(childObj != null){
						List<Map<String, Object>> pageList = (List<Map<String, Object>>) childObj;
						pageList.addAll(convertMenuToTree(list, id));
						map.put("children", pageList);
					}
				}
				treeList.add(map);
			}
		}
		return treeList;
	}

	/**
	 * 获取角色id查找该角色所有页面资源
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/role/getAllAuthMenu")
	public List<Map<String, Object>> getAllAuthMenu(Long roleId) {
		List<MenuVo> voList = roleService.getAllAuthMenu(roleId);
		List<Map<String, Object>> treeList = convertListToTree(voList);
		// 将menu转换成树形结构所需数据格式
		List<Map<String, Object>> menuTree = convertMenuToTree(treeList, null);
		return menuTree;
	}

	/**
	 * 根据角色对操作授权
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/role/optAuth")
	public String optAuth(String nodes, Long roleId) {
		String msg = "";
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Long[][] auths = objectMapper.readValue(nodes, Long[][].class);
			roleService.optAuthorize(auths, roleId);
			mySecurityMetadataSource.reloadResourceDefine();
			msg = "{\"success\":true}";
		} catch (IOException e) {
			msg = "{\"success\":false}";
		}
		return msg;
	}
	
	/**
	 * 获取角色id查找该角色所有授权组织机构
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/role/getAllAuthOrg")
	public List<OrgShortVo> getAllAuthOrg(Long roleId) {
		List<OrgShortVo> orgs = roleService.findAllAuthOrg(roleId);
		return orgs;
	}
	
	/**
	 * 根据角色对操作授权
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/role/dataAuth")
	public String dataAuth(@RequestParam(value="orgs[]",required=false) Long[] orgs, Long roleId) {
		boolean isSuccess = roleService.dataAuthorize(orgs, roleId);
		return isSuccess ? "{\"success\":true}" : "{\"success\":false}";
	}
}
