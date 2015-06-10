package com.cdrundle.legaldoc.controller;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.base.BaseController;
import com.cdrundle.legaldoc.enums.AssessResult;
import com.cdrundle.legaldoc.enums.AssessResultEnumEditor;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.enums.StatusEnumEditor;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IDeferredAssessService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IUserService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.DeferredAssessmentVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;

/**
 * 满期评估controller
 * @author gang.li
 * 
 */
@Controller
public class DeferredAssessController extends BaseController {
	@Autowired
	private IDeferredAssessService daService;
	
	@Autowired
	private INormativeFileService norService;

	@Autowired
	private IUserService userService;
	
	@InitBinder
	protected void initBinder(HttpServletRequest request, 
			ServletRequestDataBinder binder) throws IOException {
		binder.registerCustomEditor(Status.class,
				new StatusEnumEditor());
		binder.registerCustomEditor(AssessResult.class,
				new AssessResultEnumEditor());
		SimpleDateFormat dateFormat = new SimpleDateFormat(SysUtil.DATE_FORMAT_NORMAL);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));
	}
	
	@RequestMapping("/da/getStatus")
	@ResponseBody
	public List<Map<String, Object>> gainStatus(HttpServletRequest req, HttpServletResponse res) {
		return Status.toList();
	}
	
	@RequestMapping("/da/getResultStatus")
	@ResponseBody
	public List<Map<String, Object>> gainResultStatus(HttpServletRequest req, HttpServletResponse res) {
		return AssessResult.toList();
	}
	
	/**
	 * 查询本部门所有的文件清理
	 * @param page
	 * @param size
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/da/searchAll")
	public Map<String, Object> searchAll(String name, Long draftingUnit, 
			Long decisionUnit, Status status, PageParam page) {
		Page<DeferredAssessmentVo> pages = daService.findAll(name,
				draftingUnit, decisionUnit, status, page.getPage(), page.getRows());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", pages.getTotalElements());
		result.put("rows", pages.getContent());
		return result;
	}
	
	/**
	 * 加载
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/da/load")
	public DeferredAssessmentVo load(HttpServletRequest request, HttpServletResponse response, Long id, String sreachType) {
		if (SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) && id != null) {
			DeferredAssessmentVo vo = daService.findByNorFileId(id);
			if (vo != null)
				return vo;
			else {
				NormativeFileVo norFile = norService.findById(id);
				DeferredAssessmentVo daVo = new DeferredAssessmentVo();
				daVo.setNormativeFile(norFile);
				return daVo;
			}

		} else if (SysUtil.SEARCH_TYPE_DEFERRED.equals(sreachType)
				&& id != null) {
			DeferredAssessmentVo vo = daService.findById(id);
			return vo;
		}
		return null;
	}
	
	/**
	 * 添加满期评估
	 * @param request
	 * @param response
	 * @param daVo
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/da/save")
	public String saveOrUpdate(HttpServletRequest request, HttpServletResponse response, 
			DeferredAssessmentVo daVo) {
		try {
			DeferredAssessmentVo vo= daService.saveOrUpdate(daVo);
			if (vo != null) {
				return "{\"msg\":\"success\", \"id\":\"" + vo.getId() + "\"}";
			}
		} catch (ServiceException e) {
			return SysUtil.JSON_MSG_FAIL;
		}
		return SysUtil.JSON_MSG_FAIL;
	}
	
	/**
	 * 删除满期评估
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/da/delete")
	public String delete(HttpServletRequest request, HttpServletResponse response, String id) {
		boolean dFlag = false;
		if (!StringUtils.isEmpty(id)) {
			DeferredAssessmentVo daVo = daService.findById(Long.valueOf(id));
			try {
				dFlag = daService.delete(daVo);
			} catch (ServiceException e) {
				dFlag = false;
			}
		} else {
			dFlag = false;
		}
		if (dFlag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
		
	}
	
	/**
	 * 读取相关文件的内容(包括页面浏览、文档打印的读取文件内容)
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 * @throws ServiceException 
	 */
	@ResponseBody
	@RequestMapping("/da/gainFileContent")
	public String gainFileContent(HttpServletRequest request, HttpServletResponse response, 
			String norId, String fileType, String fileName) throws ServiceException {
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = "";
			if (SysUtil.FILE_TYPE_LEGALDOC.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.PUBLISH.toString());
			} else if (SysUtil.FILE_TYPE_INSTRUCTION.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			} else if (SysUtil.FILE_TYPE_LEGALBASIS.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.SETUP.toString());
			}
			 
			if (!StringUtils.isEmpty(fileName)) {
				if (SysUtil.FILE_TYPE_LEGALBASIS.equals(fileType)){
					val = WordUtils.readWord(filePath + File.separator + fileName);
				}else{
					val = WordUtils.readFile(filePath + File.separator + fileName);	//获取文档内容
				}
			} 
		}
		return val;
	}

}
