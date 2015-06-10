package com.cdrundle.legaldoc.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.service.ISysConfigService;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.SysConfigVo;

/**
 * 系统参数配置
 * @author xiaokui.li
 *
 */
@Controller
public class SysConfigController {

	@Autowired
	ISysConfigService sysConfigService;
	
	
	/**
	 * 根据名称或者编码查询系统参数配置信息，参数可以为空
	 * @param code
	 * @param name
	 * @param page
	 * @param rows
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/sysConfig/getSysConfigRef")
	public Map<String, Object> getSysConfigRef(String code, String name, PageParam page)
	{
		Map<String, Object> result = new HashMap<String, Object>();
		Page<SysConfigVo> sysConfigRefs = sysConfigService.findSysConfigRef(code, name, page.getPage(), page.getRows());
		result.put("total", sysConfigRefs.getTotalElements());
		result.put("rows", sysConfigRefs.getContent());
		return result;
	}
	
	/**
	 * 保存或者修改用户
	 * @param sysConfigVo
	 * @return
	 */
	@RequestMapping("/sysConfig/saveOrUpdate")
	@ResponseBody
	public String saveOrUpdate(SysConfigVo sysConfigVo)
	{
		String result = "";
		SysConfigVo savedSysConfig = sysConfigService.saveOrUpdate(sysConfigVo);
		if (savedSysConfig != null)
		{
			result = "{\"success\":true}";
		} else
		{
			result = "{\"success\":false}";
		}
		return result;
	}
	
	/**
	 * 假删除用户
	 * @param id
	 * @return
	 */
	@RequestMapping("/sysConfig/deleteSysConfigById")
	@ResponseBody
	public String deleteUserById(Long id)
	{
		boolean isSuccess = sysConfigService.deleteById(id);
		return isSuccess ? "{\"success\":true}" : "{\"success\":false}";
	}
}
