package com.cdrundle.legaldoc.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.vo.NorFileShortVo;
import com.cdrundle.legaldoc.vo.PageParam;

@Controller
public class NormativeFileController {

	@Autowired
	private INormativeFileService normativeFileService;

	@Autowired
	IOrganizationService organizationService;
	
	/**
	 * 查询规范性文件
	 * 
	 * @param nFileName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/pages/norFile/getNorFileReference.do")
	public Map<String, Object> gainNorFileReference(String name, String stage, PageParam page) {
		Page<NorFileShortVo> filePages = null;
		// 获取到规范性文件
		if (!StringUtils.isEmpty(name)) {
			filePages = normativeFileService.findNorFileByName(name, Stage.getByName(stage), page.getPage(), page.getRows());
		} else {
			filePages = normativeFileService.findNorFileForReference(Stage.getByName(stage), page.getPage(), page.getRows());
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", filePages.getTotalElements());
		result.put("rows", filePages.getContent());

		return result;
	}

}
