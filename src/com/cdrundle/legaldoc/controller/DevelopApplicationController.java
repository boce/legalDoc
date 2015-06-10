package com.cdrundle.legaldoc.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.base.BaseController;
import com.cdrundle.legaldoc.enums.LegalBasisType;
import com.cdrundle.legaldoc.enums.Priority;
import com.cdrundle.legaldoc.enums.PriorityEnumEditor;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IDevelopApplicationService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.service.IUserService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.UUIDGenerator;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.DevelopApplicationVo;
import com.cdrundle.legaldoc.vo.LegalBasisVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.UserVo;
import com.cdrundle.security.WebPlatformUser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 立项
 * 
 * @author xiaokui.li
 * 
 */
@Controller
public class DevelopApplicationController extends BaseController {

	private final Log log = LogFactory.getLog(getClass());
	@Autowired
	INormativeFileService normativeFileService;

	@Autowired
	IDevelopApplicationService developApplicationService;

	@Autowired
	IOrganizationService organizationService;

	@Autowired
	IUserService userService;

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws IOException {
		binder.registerCustomEditor(Priority.class, new PriorityEnumEditor());
		SimpleDateFormat dateFormat = new SimpleDateFormat(SysUtil.DATE_FORMAT_NORMAL);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));
	}

	/**
	 * 获取初始化数据
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/developApplication/init")
	public Map<String, Object> init() {
		Map<String, Object> result = new HashMap<>();
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long userId = Long.parseLong(userDetail.getUserId());
		long orgId = Long.parseLong(userDetail.getOrgId());
		List<UserVo> users = userService.findChargeByOrg(orgId);
		result.put("applyOrgId", orgId);
		if (users != null && !users.isEmpty()) {
			result.put("applyLeaderId", users.get(0).getId());
		}
		result.put("applyClerkId", userId);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(SysUtil.DATE_FORMAT_NORMAL);
		String currentDate = sdf.format(c.getTime());
		result.put("applyDate", currentDate);
		return result;
	}

	@ResponseBody
	@RequestMapping("/developApplication/load")
	public Map<String, Object> load(HttpServletRequest request, Long id, String sreachType) {
		if (SysUtil.SEARCH_TYPE_DEVELOPAPPLICATION.equals(sreachType) && id != null) {
			DevelopApplicationVo vo = developApplicationService.findById(id);
			Stage stage = vo.getNormativeFile().getStage();
			WebPlatformUser userDetail = SysUtil.getLoginInfo();
			Long orgId = Long.parseLong(userDetail.getOrgId());
			Map<String, Object> result = new HashMap<>();
			if (!Stage.SETUP.equals(stage)) {
				result.put("modifiable", false);
			} else {
				if (orgId.equals(vo.getApplyOrg().getId())) {
					result.put("modifiable", true);
				} else {
					result.put("modifiable", false);
				}
			}
			result.put("vo", vo);
			return result;
		}
		return null;
	}

	@ResponseBody
	@RequestMapping("/developApplication/find")
	public Map<String, Object> find(String name, PageParam page) {
		Set<Long> orgIds = organizationService.findAuthOrgId();
		Page<DevelopApplicationVo> filePages = developApplicationService.findByName(name, orgIds, page.getPage(), page.getRows());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", filePages.getTotalElements());
		result.put("rows", filePages.getContent());

		return result;
	}

	/**
	 * 获取组织机构类型
	 * 
	 * @return
	 */
	@RequestMapping("/developApplication/getPriority")
	@ResponseBody
	public List<Map<String, Object>> getPriority() {
		return Priority.toList();
	}

	@ResponseBody
	@RequestMapping("/developApplication/save")
	public Map<String, Object> save(HttpServletRequest request, DevelopApplicationVo vo, String tempFileId) {
		Map<String, Object> result = new HashMap<>();
		if (vo != null) {
			String rootPath = getProjectPath(request);
			try {
				String legalBasis = vo.getLegalBasis();
				ObjectMapper mapper = new ObjectMapper();
				JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, LegalBasisVo.class);
				List<LegalBasisVo> legalBasisVoList = mapper.readValue(legalBasis, javaType);
				vo.setLegalBasises(legalBasisVoList);
				DevelopApplicationVo developApplicationVo = developApplicationService.saveOrUpdate(vo, rootPath, tempFileId);
				result.put("success", true);
				result.put("msg", "保存成功！");
				result.put("vo", developApplicationVo);
			} catch (ServiceException e) {
				log.error(e.getMessage(), e.getCause());
				result.put("success", false);
				result.put("msg", e.getMessage());
			} catch (Exception e) {
				log.error(e);
				result.put("success", false);
				result.put("msg", "保存失败！");
			}
		}
		return result;
	}

	/**
	 * 上传文件
	 * 
	 * @param request
	 * @param fileName
	 * @param id
	 * @param tempFileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/developApplication/uploadFile")
	public Map<String, Object> uploadFile(HttpServletRequest request, String fileName, Long id, String tempFileId, String fileType) {
		Map<String, Object> result = new HashMap<>();
		boolean uploadFlag = false;
		// 获取文件名称
		if (StringUtils.isNotEmpty(fileName)) {
			fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
		}
		// 生成唯一标识
		String uuid;
		if (StringUtils.isNotEmpty(tempFileId)) {
			uuid = tempFileId;
		} else {
			uuid = UUIDGenerator.getUUID();
		}
		String filePath = getProjectPath(request) + File.separator + SysUtil.FILE_PATH_TEMP + File.separator + uuid;
		uploadFlag = upload(request, filePath, fileName); // 上传文件到临时目录
		if (uploadFlag) {
			result.put("success", true);
			result.put("tempFileId", uuid);
			result.put("fileName", fileName);
		} else {
			result.put("success", false);
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/developApplication/delete")
	public Map<String, Object> delete(HttpServletRequest request, Long id) {
		Map<String, Object> result = new HashMap<>();
		DevelopApplicationVo developApplicationVo = developApplicationService.findById(id);
		NormativeFileVo norFileVo = developApplicationVo.getNormativeFile();
		if (norFileVo != null) {
			// 获取路径
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, norFileVo, Stage.SETUP.toString());
			String fileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_DEVAPP + ")" + SysUtil.EXTENSION_NAME;
			try {
				developApplicationService.deleteDevAndFile(developApplicationVo, filePath, fileName);
				result.put("success", true);
				result.put("msg", "删除成功");
			} catch (ServiceException e) {
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
		} else {
			result.put("success", false);
			result.put("msg", "请先选择一个制定申请单");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/developApplication/print")
	public String print(HttpServletRequest request, Long id) throws ServiceException {
		DevelopApplicationVo developApplicationVo = developApplicationService.findById(id);
		NormativeFileVo normativeFileVo = developApplicationVo.getNormativeFile();
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.SETUP.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_DEVAPP + ")" + SysUtil.EXTENSION_NAME;
			// 获取文档内容
			val = WordUtils.readFile(filePath + File.separator + fileName);
		}
		return val;
	}

	@ResponseBody
	@RequestMapping("/developApplication/export")
	public String export(HttpServletRequest request, HttpServletResponse response, Long id) {
		DevelopApplicationVo developApplicationVo = developApplicationService.findById(id);
		NormativeFileVo normativeFileVo = developApplicationVo.getNormativeFile();
		boolean downloadFlag = false;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.SETUP.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_DEVAPP + ")" + SysUtil.EXTENSION_NAME;
			// 下载说明文档内容
			downloadFlag = this.download(response, filePath + File.separator + fileName, fileName);
		}
		if (downloadFlag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
	}

	@ResponseBody
	@RequestMapping("/developApplication/viewFile")
	public String viewFile(HttpServletRequest request, Long id, String fileName, String tempFileId) {
		String val = null;
		if (tempFileId != null) {
			// 获取到项目目录
			String projectPath = getProjectPath(request);
			String filePath = projectPath + File.separator + SysUtil.FILE_PATH_TEMP + File.separator + tempFileId + File.separator + fileName;
			File file = new File(filePath);
			if(file.exists()){
				val = WordUtils.readWord(filePath);
			}
		}
		if(val == null){
			DevelopApplicationVo developApplicationVo = developApplicationService.findById(id);
			NormativeFileVo normativeFileVo = developApplicationVo.getNormativeFile();
			if (normativeFileVo != null) {
				String filePath = getFilePath(request, normativeFileVo);
				// 获取文档内容
				val = WordUtils.readWord(filePath + File.separator + fileName);
			}
		}
		return val;
	}

	/**
	 * 获取制定依据文件类型
	 * 
	 * @return
	 */
	@RequestMapping("/developApplication/getLegalBasisType")
	@ResponseBody
	public List<Map<String, Object>> getLegalBasisType() {
		return LegalBasisType.toList();
	}

	/**
	 * 获取制定依据文件类型
	 * 
	 * @return
	 */
	@RequestMapping("/developApplication/getLegalBasisTypeData")
	@ResponseBody
	public Map<String, String> getLegalBasisTypeData() {
		return LegalBasisType.toMap();
	}

	private String getFilePath(HttpServletRequest request, NormativeFileVo normativeFileVo) {
		String projectPath = getProjectPath(request);
		String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.SETUP.toString());
		return filePath;
	}
}
