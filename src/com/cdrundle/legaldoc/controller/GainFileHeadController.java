package com.cdrundle.legaldoc.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.vo.DistrictVo;
import com.cdrundle.legaldoc.vo.OrganizationVo;
import com.cdrundle.security.WebPlatformUser;

/**
 * @author  XuBao
 *
 * 2014年8月5日
 */
@Controller
public class GainFileHeadController{
	
	@Autowired
	private IOrganizationService  organizationService;
	
	@ResponseBody
	@RequestMapping("gainFileHead/gainFileHeadName")
	public Map<String, Object> gainFileHeadName(){
		String districtName = "";
		String organizationName = "";
		Map<String, Object> result = new HashMap<String, Object>();
		//得到当前登录用户信息
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		long orgId = Long.parseLong(userDetail.getOrgId());
		
		OrganizationVo  organizationVo =  organizationService.findById(orgId);
		organizationName = organizationVo.getName();
		DistrictVo districtVo = organizationVo.getDistrict();
		districtName  = districtVo.getName();
		result.put("organizationName", organizationName);
		result.put("districtName", districtName);
		return result;
	}
	
}
