package com.cdrundle.legaldoc.controller;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.base.BaseController;
import com.cdrundle.legaldoc.enums.FileStatus;
import com.cdrundle.legaldoc.enums.OrgType;
import com.cdrundle.legaldoc.enums.OrgTypeEnumEditor;
import com.cdrundle.legaldoc.enums.Priority;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IMenuService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.service.ITaskService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.MenuShortVo;
import com.cdrundle.legaldoc.vo.NorFileShortVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.TaskVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 首页
 * 
 * @author xiaokui.li
 * 
 */
@Controller
public class HomeController extends BaseController{

	private final Log log = LogFactory.getLog(getClass());
	@Autowired
	INormativeFileService normativeFileService;

	@Autowired
	IOrganizationService organizationService;

	@Autowired
	IMenuService menuService;

	@Autowired
	ITaskService taskService;

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws IOException {
		binder.registerCustomEditor(OrgType.class, new OrgTypeEnumEditor());
	}

	@RequestMapping("/home/index")
	public String index(HttpServletRequest request, HttpServletResponse response) {

		OrgType orgType = organizationService.findUserOrgType();
		String orgTypeStr = orgType == null ? "" : orgType.name();
		request.setAttribute("orgType", orgTypeStr);
		ObjectMapper mapper = new ObjectMapper();
		String stage = "{}";
		try {
			stage = mapper.writeValueAsString(Stage.toMap());
		} catch (JsonProcessingException e) {
			log.error("阶段转json出错", e.getCause());
		}
		String priority = "{}";
		try {
			priority = mapper.writeValueAsString(Priority.toMap());
		} catch (JsonProcessingException e) {
			log.error("优先级转json出错", e.getCause());
		}
		String fileStatus = "{}";
		try {
			fileStatus = mapper.writeValueAsString(FileStatus.toMap());
		} catch (JsonProcessingException e) {
			log.error("文件状态转json出错", e.getCause());
		}
		String orgTypeEnum = "{}";
		try {
			orgTypeEnum = mapper.writeValueAsString(OrgType.toMap());
		} catch (JsonProcessingException e) {
			log.error("组织机构类型转json出错", e.getCause());
		}
		request.setAttribute("stage", stage);
		request.setAttribute("priority", priority);
		request.setAttribute("fileStatus", fileStatus);
		request.setAttribute("orgTypeEnum", orgTypeEnum);
		return "/home/homePage.wf";
	}

	/**
	 * 获取本部门起草或制定的规范性文件
	 * 
	 * @param name
	 * @param page
	 * @param rows
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/home/getOwnOrgNorFiles")
	public Map<String, Object> getOwnOrgNorFiles(String name, PageParam page) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<NorFileShortVo> nofFiles = normativeFileService.findNorFileByOwnOrg(name, page.getPage(), page.getRows());
		result.put("total", nofFiles.getTotalElements());
		result.put("rows", nofFiles.getContent());
		return result;
	}

	/**
	 * 获取已发布的规范性文件
	 * 
	 * @param page
	 * @param rows
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/home/getOrgNorFiles")
	public Map<String, Object> getOrgNorFiles(String name, OrgType orgType, PageParam page) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<NorFileShortVo> norFiles = normativeFileService.findNorFileByOrg(name, orgType, page.getPage(), page.getRows());
		if (norFiles == null) {
			result.put("total", 0);
			result.put("rows", Collections.EMPTY_LIST);
		} else {
			result.put("total", norFiles.getTotalElements());
			result.put("rows", norFiles.getContent());
		}
		return result;
	}

	/**
	 * 获取文件内容
	 * 
	 * @param request
	 * @param norId
	 * @param fileName
	 * @return
	 * @throws ServiceException 
	 */
	@ResponseBody
	@RequestMapping("/home/gainContent")
	public String gainContent(HttpServletRequest request, Long norId, String fileType, String fileName) throws ServiceException {

		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);

		String content = null;
		String filePath = null;

		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);

			if (StringUtils.isNotEmpty(fileType) && SysUtil.FILE_TYPE_LEGALBASIS.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.SETUP.toString());
				content = WordUtils.readWord(filePath + File.separator + fileName);

			} else if (StringUtils.isNotEmpty(fileType) && SysUtil.FILE_TYPE_LEGALDOC.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.PUBLISH.toString());
				content = WordUtils.readFile(filePath + File.separator + fileName);

			} else if (StringUtils.isNotEmpty(fileType) && SysUtil.FILE_TYPE_INSTRUCTION.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
				content = WordUtils.readFile(filePath + File.separator + fileName);

			}

		}
		return content;
	}

	/**
	 * 加载规范性文件详细信息
	 * 
	 * @param norId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/home/loadNorFile")
	public NormativeFileVo loadNorFile(Long norId) {
		if (norId != null) {
			NormativeFileVo norFile = normativeFileService.findById(norId);
			normativeFileService.genName(norFile);
			return norFile;
		}
		return null;
	}

	/**
	 * 根据登录用户显示对应权限的菜单
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/home/displayMenu")
	public List<MenuShortVo> displayMenu() {
		List<MenuShortVo> menuVos = menuService.displayMenu();
		return menuVos;
	}

	/**
	 * 获取待办事项
	 * 
	 * @param page
	 * @param rows
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/home/getMyTasks")
	public Map<String, Object> getMyTasks(String name, OrgType orgType, PageParam page) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<TaskVo> myTasks = taskService.findMyTasks(page.getPage(), page.getRows());
		if (myTasks == null) {
			result.put("total", 0);
			result.put("rows", Collections.EMPTY_LIST);
		} else {
			result.put("total", myTasks.getTotalElements());
			result.put("rows", myTasks.getContent());
		}
		return result;
	}
}
