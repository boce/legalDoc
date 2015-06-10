package com.cdrundle.legaldoc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.enums.OrgType;
import com.cdrundle.legaldoc.enums.OrgTypeEnumEditor;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.vo.DistrictVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.OrganizationVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.TreeVo;

/**
 * 组织机构操作类
 * 
 * @author xiaokui.li
 * 
 */
@Controller
public class OrgController {
	
	private static final String[] NODE_TYPE = {"org", "district"};
	
	@Autowired
	IOrganizationService organizationService;

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws IOException {
		binder.registerCustomEditor(OrgType.class, new OrgTypeEnumEditor());
	}

	/**
	 * 获取组织机构类型
	 * 
	 * @return
	 */
	@RequestMapping("/org/getOrgType")
	@ResponseBody
	public List<Map<String, Object>> getOrgType() {
		return OrgType.toList();
	}

	/**
	 * 获取所有区域和组织机构
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping("/org/getOrgShortReference")
	@ResponseBody
	public List<TreeVo> getOrgShortReference() {
		List<DistrictVo> voList = organizationService.findAllShort();
		List<TreeVo> trees = genTree(voList);
		return trees;
	}

	private List<TreeVo> genTree(List<DistrictVo> voList)
	{
		List<TreeVo> trees = new ArrayList<>();
		for (Iterator<DistrictVo> iterator = voList.iterator(); iterator.hasNext();) {
			DistrictVo district = iterator.next();
			TreeVo treeVo = new TreeVo();
			treeVo.setId(district.getId());
			treeVo.setText(district.getName());
			treeVo.setState("open");
			treeVo.setIconCls("icon-district");
			Map<String, Object> attrs = new HashMap<>();
			attrs.put("nodeType", NODE_TYPE[1]);
			treeVo.setAttributes(attrs);
			List<TreeVo> childs = new ArrayList<>();
			List<OrgShortVo> orgs = district.getOrganizations();
			if(orgs != null && !orgs.isEmpty())
			{
				childs.addAll(convertToTree(orgs));
			}
			List<DistrictVo> childDistricts = district.getChildDistricts();
			if(childDistricts != null && !childDistricts.isEmpty())
			{
				childs.addAll(genTree(childDistricts));
			}
			treeVo.setChildren(childs);
			trees.add(treeVo);
		}
		return trees;
	}
	private List<TreeVo> convertToTree(List<OrgShortVo> organizations)
	{
		List<TreeVo> trees = new ArrayList<>();
		for (Iterator<OrgShortVo> iterator = organizations.iterator(); iterator.hasNext();) {
			OrgShortVo orgShortVo = iterator.next();
			TreeVo tree = new TreeVo();
			tree.setId(orgShortVo.getId());
			tree.setText(orgShortVo.getText());
			tree.setState("open");
			tree.setIconCls("icon-org");
			Map<String, Object> attrs = new HashMap<>();
			attrs.put("nodeType", NODE_TYPE[0]);
			attrs.put("district", orgShortVo.getDistrict());
			tree.setAttributes(attrs);
			List<OrgShortVo> childrens = orgShortVo.getChildren();
			if(childrens != null && !childrens.isEmpty())
			{
				tree.setChildren(convertToTree(childrens));
			}
			tree.setDisplayOrder(orgShortVo.getDisplayOrder());
			trees.add(tree);
		}
		sortOrgTrees(trees);
		return trees;
	}

	private void sortOrgTrees(List<TreeVo> trees) {
		Comparator<TreeVo> comparator = new Comparator<TreeVo>() {
			@Override
			public int compare(TreeVo o1, TreeVo o2) {
				if(o1.getDisplayOrder() == null && o2.getDisplayOrder() == null){
					return 0;
				}
				if(o1.getDisplayOrder() != null && o2.getDisplayOrder() == null){
					return -1;
				}
				if(o1.getDisplayOrder() == null && o2.getDisplayOrder() != null){
					return 1;
				}
				return o1.getDisplayOrder().compareTo(o2.getDisplayOrder());
			}
		};
		Collections.sort(trees, comparator);
	}
	
	/**
	 * 根据id查找组织机构
	 * 
	 * @param req
	 * @param res
	 * @param id
	 * @return
	 */
	@RequestMapping("/org/getOrgById")
	@ResponseBody
	public OrganizationVo getOrgById(Long id) {
		OrganizationVo organizationVo = organizationService.findById(id);
		return organizationVo;
	}

	/**
	 * 根据id查找区域
	 * 
	 * @param req
	 * @param res
	 * @param id
	 * @return
	 */
	@RequestMapping("/org/getDistrictById")
	@ResponseBody
	public DistrictVo getDistrictById(Long id) {
		DistrictVo districtVo = organizationService.findDistrictById(id);
		return districtVo;
	}
	
	/**
	 * 根据名称查询组织机构
	 * 
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping("/org/findOrgShortReference")
	@ResponseBody
	public Map<String, Object> findOrgShortReference(String orgName, PageParam page) {
		Map<String, Object> result = new HashMap<>();
		Page<OrganizationVo> pages = organizationService.findLikeName(orgName, page.getPage(), page.getRows());
		result.put("total", pages.getTotalElements());
		result.put("rows", pages.getContent());
		return result;
	}

	/**
	 * 保存或者修改组织机构
	 * 
	 * @param req
	 * @param res
	 * @param orgVo
	 * @return
	 */
	@RequestMapping("/org/saveOrg")
	@ResponseBody
	public Map<String, Object> saveOrg(OrganizationVo orgVo) {
		Map<String, Object> result = new HashMap<>();
		OrganizationVo organizationVo = organizationService.saveOrUpdate(orgVo);
		if (organizationVo != null) {
			result.put("success", true);
			result.put("id", organizationVo.getId());
		} else {
			result.put("success", false);
		}
		return result;
	}

	@RequestMapping("/org/saveDistrict")
	@ResponseBody
	public Map<String, Object> saveDistrict(DistrictVo districtVo) {
		Map<String, Object> result = new HashMap<>();
		DistrictVo district = organizationService.saveDistrict(districtVo);
		if (district != null) {
			result.put("success", true);
			result.put("districtId", district.getId());
		} else {
			result.put("success", false);
		}
		return result;
	}
	
	/**
	 * 假删除组织机构
	 * 
	 * @param req
	 * @param res
	 * @param orgId
	 * @return
	 */
	@RequestMapping("/org/deleteOrgById")
	@ResponseBody
	public String deleteOrgById(Long orgId) {
		boolean isSuccess = organizationService.deleteById(orgId);
		return isSuccess ? "{\"success\":true}" : "{\"success\":false}";
	}
	
	/**
	 * 假删除区域
	 * 
	 * @param req
	 * @param res
	 * @param orgId
	 * @return
	 */
	@RequestMapping("/org/deleteDistrictById")
	@ResponseBody
	public String deleteDistrictById(Long districtId) {
		boolean isSuccess = organizationService.deleteDistrictById(districtId);
		return isSuccess ? "{\"success\":true}" : "{\"success\":false}";
	}
	
	/**
	 * 查询所有区域
	 * 
	 * @param req
	 * @param res
	 * @param orgId
	 * @return
	 */
	@RequestMapping("/org/findAllDistrictForTree")
	@ResponseBody
	public List<TreeVo> findAllDistrictForTree() {
		List<DistrictVo> districts = organizationService.findAllDistrict();
		return genTree(districts);
	}
	
	@RequestMapping("/org/findOrgByDistrict")
	@ResponseBody
	public List<TreeVo> findOrgByDistrict(Long districtId) {
		List<OrgShortVo> orgs = organizationService.findOrgByDistrict(districtId);
		return convertToTree(orgs);
	}
}
