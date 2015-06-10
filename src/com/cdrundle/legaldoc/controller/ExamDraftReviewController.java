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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
import com.cdrundle.legaldoc.service.IExamDraftReviewService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.service.IUserService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.ExamDraftReviewVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.security.WebPlatformUser;

/**
 * 送审稿审查controller
 * @author gang.li
 * 
 */
@Controller
public class ExamDraftReviewController extends BaseController {
	@Autowired
	private IExamDraftReviewService edrService;
	
	@Autowired
	private INormativeFileService norService;
	
	@Autowired
	private IOrganizationService orgService;
	
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
	
	@RequestMapping("/examDraftRev/getStatus")
	@ResponseBody
	public List<Map<String, Object>> gainStatus(HttpServletRequest req, HttpServletResponse res) {
		return Status.toList();
	}
	
	/**
	 * 查询送审稿审查
	 * @param page
	 * @param size
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/examDraftRev/searchDraftRevs")
	public Map<String, Object> searchDraftRevs(String name, Long draftingUnit, Long reviewUnit,
			Status status, PageParam page) {
		Page<ExamDraftReviewVo> filePages = null;
		Set<Long> orgIds = orgService.findAuthOrgId();
		if (StringUtils.isEmpty(name) && draftingUnit == null && reviewUnit == null && status == null) {
			filePages = edrService.findAllByUnit(orgIds, page.getPage(), page.getRows());
		} else {
			filePages = edrService.findAll(name, orgIds, reviewUnit, status, page.getPage(), page.getRows());
		}
		Map<String, Object> result = new HashMap<String, Object>();
		List <ExamDraftReviewVo> edsList = filePages.getContent();
		for (ExamDraftReviewVo eds : edsList) {		//处理联合起草单位等
			norService.genName(eds.getNormativeFile());
		}
		result.put("total", filePages.getTotalElements());
		result.put("rows", filePages.getContent());

		return result;
		
	}
	
	@ResponseBody
	@RequestMapping("/examDraftRev/load")
	public Map<String, Object> load(HttpServletRequest request, HttpServletResponse response, Long id, String sreachType) {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Map<String, Object> result = new HashMap<>();
		ExamDraftReviewVo edrVo = new ExamDraftReviewVo();
		long drtId = 0l;
		long orgId = Long.parseLong(userDetail.getOrgId());
		if (SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) && id != null) {
			edrVo = edrService.findByNorFileId(id);
			if (edrVo == null) {
				NormativeFileVo norFile = norService.findById(id);
				edrVo = new ExamDraftReviewVo();
				edrVo.setNormativeFile(norFile);
				String examDraft = norFile.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING + ")" + SysUtil.EXTENSION_NAME;
				edrVo.setExaminationDraft(examDraft);
			}

		} else if (SysUtil.SEARCH_TYPE_DRAFTREVIEW.equals(sreachType) && id != null) {
			edrVo = edrService.findById(id);
		}
		norService.genName(edrVo.getNormativeFile());	//处理联合起草单位名称等
		Stage stage = edrVo.getNormativeFile().getStage();
		result.put("vo", edrVo);
		drtId = edrVo.getNormativeFile().getRevUnit().getId();	//单位为审查单位才具有操作权限
		if (!Stage.LEGAL_REVIEW_REVIEW.equals(stage) && !Stage.LEGAL_REVIEW_SUBMIT.equals(stage)) {
			if(Stage.LEGAL_REVIEW_MODIFY.equals(stage) && !edrVo.getIsNeedModify()){
				result.put("modifiable", true);
			}else{
				result.put("modifiable", false);
			}
		} else {
			if(orgId == drtId) {
				result.put("modifiable", true);
			} else {
				result.put("modifiable", false);
			}
		}
		return result;
	}
	
	/**
	 * 添加送审稿审查
	 * @param norId
	 * @param name
	 * @param reviewUnit
	 * @param reviewComment
	 * @param reviewDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/examDraftRev/addExamDraftRev")
	public Map<String, Object> addExamDraftRev(HttpServletRequest request, HttpServletResponse response, 
			String norId, String name, String reviewComment, Date reviewDate, Boolean isNeedModify) {
		Map<String, Object> result = new HashMap<>();
		String fileName = null;
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
		
		if (null != normativeFileVo) {
			ExamDraftReviewVo examDraftReviewVo = new ExamDraftReviewVo();
			examDraftReviewVo.setName(name);
			examDraftReviewVo.setNormativeFile(normativeFileVo);
			examDraftReviewVo.setDraftingUnit(normativeFileVo.getDrtUnit());
			examDraftReviewVo.setDraftingUnitLeader(normativeFileVo.getDrtUnitLeader());
			examDraftReviewVo.setDraftingUnitClerk(normativeFileVo.getDrtUnitClerk());
			examDraftReviewVo.setUnionDraftingUnit(normativeFileVo.getUnionDrtUnit());
			examDraftReviewVo.setUnionDraftingUnitLeader(normativeFileVo.getUnionDrtUnitLeader());
			examDraftReviewVo.setUnionDraftingUnitClerk(normativeFileVo.getUnionDrtUnitClerk());
			examDraftReviewVo.setReviewUnit(normativeFileVo.getRevUnit());
			examDraftReviewVo.setReviewUnitLeader(normativeFileVo.getRevUnitLeader());
			examDraftReviewVo.setReviewUnitClerk(normativeFileVo.getRevUnitClerk());
			examDraftReviewVo.setStatus(Status.APPROVE);
			examDraftReviewVo.setDraftingInstruction(normativeFileVo.getDraftInstruction());
			examDraftReviewVo.setLegalBasises(normativeFileVo.getLegalBasis());
			examDraftReviewVo.setReviewComment(reviewComment);
			examDraftReviewVo.setReviewDate(reviewDate);
			examDraftReviewVo.setIsNeedModify(isNeedModify);
			
			//生成路径
			String projectPath = getProjectPath(request);
			fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REVCOMMENT + ")" + SysUtil.EXTENSION_NAME;
			
			//读取到送审稿
			String examDraft = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING + ")" + SysUtil.EXTENSION_NAME;
			examDraftReviewVo.setExaminationDraft(examDraft);
			
			try {
				ExamDraftReviewVo eds = edrService.saveOrUpdate(examDraftReviewVo, projectPath, fileName);
				if (eds != null) {
					result.put("id", eds.getId());
					result.put("msg", "success");
				} else {
					result.put("msg", "fail");
				}
			} catch (ServiceException e) {
				result.put("msg", e.getMessage());
			}
		} else {
			result.put("msg", "fail");
		}
		return result;
	}
	
	/**
	 * 删除送审稿
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/examDraftRev/delete")
	public String delete(HttpServletRequest request, HttpServletResponse response, String norId) {
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
		String filePath = null;
		String fileName = null;
		boolean dFlag = false;
		if (null != normativeFileVo) {
			//获取路径
			String projectPath = getProjectPath(request);
			filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REVCOMMENT + ")" + SysUtil.EXTENSION_NAME;
			filePath = filePath + File.separator + fileName;
			if (null != norId && !"".equals(norId)) {
				ExamDraftReviewVo edrVo = edrService.findByNorFileId(Long.valueOf(norId));
				if (edrVo != null) {
					try {
						dFlag = edrService.delete(edrVo, filePath);	//删除
					} catch (ServiceException e) {
						dFlag = false;
					}
				}
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
	@RequestMapping("/examDraftRev/gainFileContent")
	public String gainFileContent(HttpServletRequest request, HttpServletResponse response, 
			String norId, String fileType, String fileName) throws ServiceException {
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = "";
			if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_DRAFT.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
				if (StringUtils.isEmpty(fileName)) {
					fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING + ")" + SysUtil.EXTENSION_NAME;
				}
			} else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_LEGALBASIS.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.SETUP.toString());
			} else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_REVIEWCOMMENT.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
				if (StringUtils.isEmpty(fileName)) {
					fileName = WordUtils.getFileNames(filePath, SysUtil.STAGE_LEGAL_REVCOMMENT);	//打印时
				}
			}else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_PROTOCOL.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
				if (StringUtils.isEmpty(fileName)) {
					fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_PROTOCOL + ")" + SysUtil.EXTENSION_NAME;
				}
			} 
			 
			if (!StringUtils.isEmpty(fileName)) {
				val = WordUtils.readFile(filePath + "\\" + fileName);	//获取文档内容
			} 
		}
		return val;
	}
	
	/**
	 * 下载申请说明
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/examDraftRev/downloadWord")
	public String downloadWord(HttpServletRequest request, HttpServletResponse response, String norId) {
		boolean downloadFlag = false;
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_INSTRUCTION + ")" + SysUtil.EXTENSION_NAME;
			downloadFlag = this.download(response, filePath + File.separator + fileName, fileName);	//下载审核意见
		}
		if (downloadFlag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
	}

}
