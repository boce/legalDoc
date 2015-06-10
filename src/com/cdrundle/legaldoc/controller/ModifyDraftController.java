package com.cdrundle.legaldoc.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.base.BaseController;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IModifyDraftService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.ModifyDraftVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.security.WebPlatformUser;

@Controller
public class ModifyDraftController extends BaseController {
	
	private final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private INormativeFileService normativeFileService;

	@Autowired
	private IModifyDraftService modifyDraftService;

	@Autowired
	IOrganizationService organizationService;

	@ResponseBody
	@RequestMapping("/modifyDraft/load")
	public Map<String, Object> load(HttpServletRequest request, Long id, String sreachType) {
		Map<String, Object> result = new HashMap<>();
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long orgId = Long.parseLong(userDetail.getOrgId());
		if (SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) && id != null) {
			NormativeFileVo norFile = normativeFileService.findById(id);
			ModifyDraftVo vo = new ModifyDraftVo();
			vo.setNormativeFile(norFile);
			String filePath = getFilePath(request, norFile);
			String names = WordUtils.getFileNames(filePath, SysUtil.STAGE_LEGAL_ADOPTCOMMENT);
			vo.setFeedbackProcess(names);
			result.put("modifiable", true);
			result.put("vo", vo);
		} else if (SysUtil.SEARCH_TYPE_MODIFYDRAFT.equals(sreachType) && id != null) {
			ModifyDraftVo vo = modifyDraftService.findById(id);
			NormativeFileVo norFileVo = vo.getNormativeFile();
			Stage stage = norFileVo.getStage();
			if(!Stage.REQUEST_COMMENT_MODIFY.equals(stage)){
				result.put("modifiable", false);
			}else{
				if(orgId.equals(vo.getDraftingUnit().getId())){
					result.put("modifiable", true);
				}else{
					result.put("modifiable", false);
				}
			}
			result.put("vo", vo);
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/modifyDraft/save")
	public Map<String, Object> save(HttpServletRequest request, ModifyDraftVo modifyvo, Boolean isConfirm) {
		Map<String, Object> result = new HashMap<>();
		if (modifyvo != null) {
			String rootPath = getProjectPath(request);
			try {
				ModifyDraftVo modifyDraftVo = modifyDraftService.saveOrUpdate(modifyvo, rootPath, isConfirm);
				result.put("success", true);
				result.put("msg", "保存成功！");
				result.put("vo", modifyDraftVo);
			} catch (ServiceException e) {
				log.error(e.getMessage(), e.getCause());
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/modifyDraft/delete")
	public Map<String, Object> delete(HttpServletRequest request, Long id) {
		Map<String, Object> result = new HashMap<>();
		if (id != null) {
			String rootPath = getProjectPath(request);
			try {
				boolean success = modifyDraftService.delete(id, rootPath);
				if (success) {
					result.put("success", true);
					result.put("msg", "删除成功");
				} else {
					result.put("success", false);
					result.put("msg", "请先选择一个修改《征求意见稿》");
				}
			} catch (ServiceException e) {
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
		} else {
			result.put("success", false);
			result.put("msg", "请先选择一个修改《征求意见稿》");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/modifyDraft/find")
	public Map<String, Object> find(String name, PageParam page) {
		Page<ModifyDraftVo> filePages = null;

		Set<Long> orgIds = organizationService.findAuthOrgId();

		filePages = modifyDraftService.findByName(name, orgIds, page.getPage(), page.getRows());

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", filePages.getTotalElements());
		result.put("rows", filePages.getContent());

		return result;
	}

	@ResponseBody
	@RequestMapping("/modifyDraft/viewFeedback")
	public String viewFeedback(HttpServletRequest request, Long norId, String fileName) throws ServiceException {
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		String val = null;
		if (normativeFileVo != null) {
			String filePath = getFilePath(request, normativeFileVo);
			// 获取文档内容
			val = WordUtils.readFile(filePath + File.separator + fileName);
		}

		return val;
	}

	@ResponseBody
	@RequestMapping("/modifyDraft/print")
	public String print(HttpServletRequest request, String norId) throws ServiceException {
		NormativeFileVo normativeFileVo = normativeFileService.findById(Long.valueOf(norId));
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQCOMMENTREVISE + ")" + SysUtil.EXTENSION_NAME;
			// 获取文档内容
			File file = new File(filePath + File.separator + fileName);
			if(!file.exists()){
				fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_DRAFT + ")" + SysUtil.EXTENSION_NAME;
			}
			val = WordUtils.readFile(filePath + File.separator + fileName);
		}
		return val;
	}

	@ResponseBody
	@RequestMapping("/modifyDraft/export")
	public String export(HttpServletRequest request, HttpServletResponse response, String norId) {
		boolean downloadFlag = false;
		NormativeFileVo normativeFileVo = normativeFileService.findById(Long.valueOf(norId));
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQCOMMENTREVISE + ")" + SysUtil.EXTENSION_NAME;
			File file = new File(filePath + File.separator + fileName);
			if(!file.exists()){
				fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_DRAFT + ")" + SysUtil.EXTENSION_NAME;
			}
			// 下载说明文档内容
			downloadFlag = this.download(response, filePath + File.separator + fileName, fileName);
		}
		if (downloadFlag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
	}
	
	/**
	 * 加载相关文件的内容
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 * @throws ServiceException 
	 */
	@ResponseBody
	@RequestMapping("/modifyDraft/gainFileContent")
	public String gainFileContent(HttpServletRequest request, HttpServletResponse response, 
			Long id, String fileType, String fileName, String searchType) throws ServiceException {
		NormativeFileVo normativeFileVo = null;
		if (SysUtil.SEARCH_TYPE_NORFILE.equals(searchType) && id != null) {
			normativeFileVo = normativeFileService.findById(id);
			
		} else if (SysUtil.SEARCH_TYPE_MODIFYDRAFT.equals(searchType) && id != null) {
			ModifyDraftVo modifyDraftVo = modifyDraftService.findById(id);
			normativeFileVo = normativeFileService.findById(modifyDraftVo.getNormativeFile().getId());
		}
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = "";
			if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_DRAFT.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DRAFTING.toString());
				if (StringUtils.isEmpty(fileName)) {
					fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTCOMMENT + ")" + SysUtil.EXTENSION_NAME;
				}
			} 
			 
			if (!StringUtils.isEmpty(fileName)) {
				val = WordUtils.readFile(filePath + File.separator + fileName);	//获取文档内容
			} 
		}
		return val;
	}

	private String getFilePath(HttpServletRequest request, NormativeFileVo normativeFileVo) {
		String projectPath = getProjectPath(request);
		String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
		return filePath;
	}
}
