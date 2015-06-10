package com.cdrundle.legaldoc.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import com.cdrundle.legaldoc.enums.DraftingMode;
import com.cdrundle.legaldoc.enums.DraftingModeEnumEditor;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.enums.StatusEnumEditor;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IDraftService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.DraftVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.security.WebPlatformUser;

/**
 * 起草
 * 
 * @author xiaokui.li
 * 
 */
@Controller
public class DraftController extends BaseController {

	private final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	IDraftService draftService;

	@Autowired
	INormativeFileService normativeFileService;
	
	@Autowired
	IOrganizationService organizationService;

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws IOException {
		binder.registerCustomEditor(Status.class, new StatusEnumEditor());
		binder.registerCustomEditor(DraftingMode.class, new DraftingModeEnumEditor());
		SimpleDateFormat dateFormat = new SimpleDateFormat(SysUtil.DATE_FORMAT_NORMAL);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));
	}

	@ResponseBody
	@RequestMapping("/draft/load")
	public Map<String, Object> load(HttpServletRequest request, Long id, String sreachType) {
		Map<String, Object> result = new HashMap<>();
		DraftVo vo = new DraftVo();
		if (SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) && id != null) {
			vo = draftService.findByNorFileId(id);
			if (vo.getId() == null){
				NormativeFileVo norFile = normativeFileService.findById(id);
				vo = new DraftVo();
				vo.setNormativeFile(norFile);
				String involvedOrgesName = draftService.getInvolvedOrgesName(norFile.getInvolvedOrges());
				vo.setInvolvedOrgNames(involvedOrgesName);
			}
			result.put("modifiable", true);
			result.put("vo", vo);
		} else if (SysUtil.SEARCH_TYPE_DRAFT.equals(sreachType) && id != null) {
			vo = draftService.findById(id);
			Stage stage = vo.getNormativeFile().getStage();
			WebPlatformUser userDetail = SysUtil.getLoginInfo();
			Long orgId = Long.parseLong(userDetail.getOrgId());
			if(Stage.REQUEST_COMMENT_FEEDBACK.equals(stage) && StringUtils.isEmpty(vo.getNormativeFile().getInvolvedOrges())){
				result.put("modifiable", true);
			}else if(!Stage.DRAFTING.equals(stage)){
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

	/**
	 * 获取起草方式列表
	 * 
	 * @return
	 */
	@RequestMapping("/org/getDraftMode")
	@ResponseBody
	public List<Map<String, Object>> getDraftMode() {
		return DraftingMode.toList();
	}
	
	@ResponseBody
	@RequestMapping("/draft/find")
	public Map<String, Object> find(String name, PageParam page) {
		Set<Long> orgIds = organizationService.findAuthOrgId();
		Page<DraftVo> filePages = draftService.findByName(name, orgIds, page.getPage(), page.getRows());

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", filePages.getTotalElements());
		result.put("rows", filePages.getContent());

		return result;
	}

	@ResponseBody
	@RequestMapping("/draft/save")
	public Map<String, Object> save(HttpServletRequest request, DraftVo vo, Boolean isConfirm){
		Map<String, Object> result = new HashMap<>();
		if (vo != null) {
			String rootPath = getProjectPath(request);
			try {
				DraftVo draftVo = draftService.saveOrUpdate(vo, rootPath, isConfirm);
				result.put("success", true);
				result.put("msg", "保存成功！");
				result.put("vo", draftVo);
			} catch (ServiceException e) {
				log.error(e.getMessage(), e.getCause());
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/draft/delete")
	public Map<String, Object> delete(HttpServletRequest request, Long id) {
		Map<String, Object> result = new HashMap<>();
		if (id != null) {
			String rootPath = getProjectPath(request);
			try {
				draftService.delete(id, rootPath);
				result.put("success", true);
				result.put("msg", "删除成功");
			} catch (ServiceException e) {
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
		}else{
			result.put("success", false);
			result.put("msg", "请先选择一个起草单");
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/draft/print")
	public String print(HttpServletRequest request, Long norId) throws ServiceException {
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DRAFTING.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTCOMMENT + ")"+SysUtil.EXTENSION_NAME;
			File file = new File(filePath + File.separator + fileName);
			if(!file.exists()){
				fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_DRAFT + ")"+SysUtil.EXTENSION_NAME;
			}
			//获取文档内容
			val = WordUtils.readFile(filePath + File.separator + fileName);
		}
		return val;
	}
	
	@ResponseBody
	@RequestMapping("/draft/export")
	public String export(HttpServletRequest request,HttpServletResponse response, Long norId) {
		boolean downloadFlag = false;
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DRAFTING.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTCOMMENT + ")"+SysUtil.EXTENSION_NAME;
			File file = new File(filePath + File.separator + fileName);
			if(!file.exists()){
				fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_DRAFT + ")"+SysUtil.EXTENSION_NAME;
			}
			//下载起草文档内容
			downloadFlag = this.download(response, filePath + File.separator + fileName, fileName);	
		}
		if (downloadFlag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
	}
	
}
