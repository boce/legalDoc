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
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.base.BaseController;
import com.cdrundle.legaldoc.entity.ExaminationDraftSubmit;
import com.cdrundle.legaldoc.enums.AssessResult;
import com.cdrundle.legaldoc.enums.AssessResultEnumEditor;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.enums.StatusEnumEditor;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IExamDraftSubService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.service.IUserService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.ExamDraftSubmitVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.OrganizationVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.UserShortVo;
import com.cdrundle.security.WebPlatformUser;

/**
 * 送审稿controller
 * @author gang.li
 * 
 */
@Controller
public class ExamDraftSubmitController extends BaseController {
	@Autowired
	private IExamDraftSubService edsService;
	
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
	
	@RequestMapping("/examDraftSub/getStatus")
	@ResponseBody
	public List<Map<String, Object>> gainStatus(HttpServletRequest req, HttpServletResponse res) {
		return Status.toList();
	}
	
	/**
	 * 添加或修改送审稿
	 * @param norId
	 * @param name
	 * @param reviewUnit
	 * @param reviewUnitLeader
	 * @param reviewUnitClerk
	 * @param examinationDraft
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/examDraftSub/addExamDraftSub")
	public Map<String, Object> addExamDraftSub(HttpServletRequest request, HttpServletResponse response, 
			String norId, String name, Long reviewUnit, String reviewUnitLeader, 
			String reviewUnitClerk, String draftingInstruction, String legalBasises, String legalBasisesNoAtta) {
		Map<String, Object> result = new HashMap<>();
		String filePath = null;
		String fileName = null;
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
		
		if (null != normativeFileVo) {
			OrgShortVo shortUnit = orgService.findByIdShort(reviewUnit);
			
			UserShortVo shortLeader = userService.findByIdShort(Long.valueOf(reviewUnitLeader));
			UserShortVo shortClerk = userService.findByIdShort(Long.valueOf(reviewUnitClerk));
			
			ExamDraftSubmitVo examDraftSubmitVo = new ExamDraftSubmitVo();
			examDraftSubmitVo.setName(name);
			examDraftSubmitVo.setNormativeFile(normativeFileVo);
			examDraftSubmitVo.setDraftingUnit(normativeFileVo.getDrtUnit());
			examDraftSubmitVo.setDraftingUnitLeader(normativeFileVo.getDrtUnitLeader());
			examDraftSubmitVo.setDraftingUnitClerk(normativeFileVo.getDrtUnitClerk());
			examDraftSubmitVo.setUnionDraftingUnit(normativeFileVo.getUnionDrtUnit());
			examDraftSubmitVo.setUnionDraftingUnitLeader(normativeFileVo.getUnionDrtUnitLeader());
			examDraftSubmitVo.setUnionDraftingUnitClerk(normativeFileVo.getUnionDrtUnitClerk());
			examDraftSubmitVo.setReviewUnit(shortUnit);
			examDraftSubmitVo.setReviewUnitLeader(shortLeader);
			examDraftSubmitVo.setReviewUnitClerk(shortClerk);
			examDraftSubmitVo.setStatus(Status.APPROVE);
			examDraftSubmitVo.setDraftingInstruction(draftingInstruction);
			examDraftSubmitVo.setLegalBasises(legalBasises);
			examDraftSubmitVo.setLegalBasisesNoAtta(legalBasisesNoAtta);
			
			//生成路径
			String projectPath = getProjectPath(request);
			filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_INSTRUCTION + ")" + SysUtil.EXTENSION_NAME;
			
			//读取到送审稿
			String examDraft = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING + ")" + SysUtil.EXTENSION_NAME;
			examDraftSubmitVo.setExaminationDraft(examDraft);
			
			try {
				ExamDraftSubmitVo eds = edsService.saveOrUpdate(examDraftSubmitVo, filePath, fileName);
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
	 * 加载数据
	 * @param request
	 * @param response
	 * @param norId
	 * @param sreachType
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/examDraft/load")
	public Map<String, Object> load(HttpServletRequest request, HttpServletResponse response, Long norId, String sreachType, Model model) {
		ExamDraftSubmitVo edsVo = null;
		ExaminationDraftSubmit eds = null;
		Map<String, Object> result = new HashMap<>();
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		long drtId = 0l;
		long orgId = Long.parseLong(userDetail.getOrgId());
		if (SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) && norId != null) {
			eds = edsService.findByNorId(norId);
			if (eds != null) {
				edsVo = edsService.examDraftSubToVo(eds);
			} else {
				edsVo = new ExamDraftSubmitVo();
				NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
				//查询登录人员对应单位的审核单位
				OrganizationVo org = orgService.findById(orgId);
				normativeFileVo.setRevUnit(org.getReviewUnit());
				edsVo.setNormativeFile(normativeFileVo);
				String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING + ")" + SysUtil.EXTENSION_NAME;
				edsVo.setExaminationDraft(fileName);
			}
		} else if (SysUtil.SEARCH_TYPE_DRAFTSUB.equals(sreachType) && norId != null) {
			edsVo = edsService.findById(norId);
		}
		norService.genName(edsVo.getNormativeFile());	//处理联合起草单位名称等
		Stage stage = edsVo.getNormativeFile().getStage();
		result.put("vo", edsVo);
		drtId = edsVo.getNormativeFile().getDrtUnit().getId();
		if (!Stage.LEGAL_REVIEW_SUBMIT.equals(stage) && !Stage.REQUEST_COMMENT_MODIFY.equals(stage)) {
			result.put("modifiable", false);
		} else {
			if (orgId == drtId) {
				result.put("modifiable", true);
			} else {
				result.put("modifiable", false);
			}
		}
		
		return result;
	}
	
	/**
	 * 上传相关法律依据
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/examDraftSub/uploadLegal")
	public String uploadLegal(HttpServletRequest request, HttpServletResponse response, String fileName, String norId) {
		boolean uploadFlag = false;
		boolean repeatFlag = true;
		//获取规范性文件
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
//		ExaminationDraftSubmit eds = edsService.findByNorId(Long.valueOf(norId));
//		ExamDraftSubmitVo evo = edsService.examDraftSubToVo(eds);
		String projectPath = getProjectPath(request);		//获取到项目目录
		
		if (fileName != null && !"".equals(fileName)) {
			fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
		} 
		if (null != normativeFileVo) { //创建文件上传文件路径
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.SETUP.toString());
			uploadFlag = this.upload(request, filePath, fileName);	//上传说明文档内容
		}
		//判断是否更新数据库的字段,如果已经存在不更新，没有便更新法律依据字段
		//fileName = fileName.substring(0, fileName.lastIndexOf("."));
		String legalss = normativeFileVo.getLegalBasis();
//			try {
//				if (StringUtils.isEmpty(legalss)) {
//					evo.setLegalBasises(fileName);
//					edsService.updateLegalsis(evo);
//					repeatFlag = false;
//				} else if (legalss.indexOf(fileName) < 0) {
//					evo.setLegalBasises(legalss + ";" + fileName);
//					edsService.updateLegalsis(evo);
//					repeatFlag = false;
//				}
//			} catch (ServiceException e) {
//				uploadFlag = false;
//			}
		if (legalss.indexOf(fileName) < 0){
			repeatFlag = false;
		}
		if (uploadFlag) {
			if (!repeatFlag) {
				return "{\"msg\":\"success\", \"name\":\"" + fileName + "\"}";
			} else {
				return "{\"msg\":\"success\", \"name\":\"\"}";
			}
			
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
		
	}
	
	/**
	 * 下载起草说明
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/examDraft/downloadWord")
	public String downloadWord(HttpServletRequest request, HttpServletResponse response, String norId) {
		boolean downloadFlag = false;
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_INSTRUCTION + ")" + SysUtil.EXTENSION_NAME;
			//下载说明文档内容
			downloadFlag = this.download(response, filePath + File.separator + fileName, fileName);	
		}
		if (downloadFlag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
	}
	
	/**
	 * 删除送审稿(包括删除起草说明)
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/examDraft/delete")
	public String delete(HttpServletRequest request, HttpServletResponse response, String norId) {
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
		String filePath = null;
		String fileName = null;
		boolean dFlag = false;
		if (null != normativeFileVo) {
			//获取路径
			String projectPath = getProjectPath(request);
			filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_INSTRUCTION + ")" + SysUtil.EXTENSION_NAME;
			filePath = filePath + File.separator + fileName;
			if (null != norId && !"".equals(norId)) {
				ExaminationDraftSubmit eds = edsService.findByNorId(Long.valueOf(norId));
				if (eds != null) {
					ExamDraftSubmitVo edsVo = edsService.examDraftSubToVo(eds);
					try {
						dFlag = edsService.delete(edsVo, filePath);	//删除
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
	 * 查询送审稿
	 * @param page
	 * @param size
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/examDraft/searchDraftSubs")
	public Map<String, Object> searchDraftSubs(String name, Long draftingUnit, Long reviewUnit,
			Status status, PageParam page) {
		Page<ExamDraftSubmitVo> filePages = null;
		
		Set<Long> orgIds = orgService.findAuthOrgId();
		if (StringUtils.isEmpty(name) && draftingUnit == null && reviewUnit == null && status == null) {
			filePages = edsService.findAllByOrgAndUnion(orgIds, page.getPage(), page.getRows());
		} else {
			filePages = edsService.findAll(name, orgIds, reviewUnit, status, page.getPage(), page.getRows());
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", filePages.getTotalElements());
		List <ExamDraftSubmitVo> edsList = filePages.getContent();
		for (ExamDraftSubmitVo eds : edsList) {	//处理联合起草单位等
			norService.genName(eds.getNormativeFile());
		}
		result.put("rows", edsList);

		return result;
		
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
	@RequestMapping("/examDraftSub/gainFileContent")
	public String gainFileContent(HttpServletRequest request, HttpServletResponse response, 
			String norId, String fileType, String fileName) throws ServiceException {
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = "";
			if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_DRAFT.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			} else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_LEGALBASIS.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.SETUP.toString());
				return WordUtils.readWord(filePath + File.separator + fileName);
			} else if (!StringUtils.isEmpty(fileType) && (SysUtil.FILE_TYPE_REVIEWCOMMENT.equals(fileType) 
					|| SysUtil.FILE_TYPE_INSTRUCTION.equals(fileType))) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
				if (StringUtils.isEmpty(fileName)) {
					fileName = WordUtils.getFileNames(filePath, SysUtil.STAGE_LEGAL_INSTRUCTION);
				}
			}
			 
			if (!StringUtils.isEmpty(fileName)) {
				val = WordUtils.readFile(filePath + File.separator + fileName);	//获取文档内容
			} 
		}
		return val;
	}
}
