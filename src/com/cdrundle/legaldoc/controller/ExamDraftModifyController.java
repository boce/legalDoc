package com.cdrundle.legaldoc.controller;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.base.BaseController;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IExamDraftModifyService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IUserService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.ExamDraftModifyVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.security.WebPlatformUser;

/**
 * 送审稿审查controller
 * @author gang.li
 * 
 */
@Controller
public class ExamDraftModifyController extends BaseController {
	@Autowired
	private IExamDraftModifyService edmService;
	
	@Autowired
	private INormativeFileService norService;
	
	@Autowired
	private IOrganizationDao orgDao;

	@Autowired
	private IUserDao userDao;
	
	@Autowired
	private IUserService userService;
	
	/**
	 * 查询送审稿审查
	 * @param page
	 * @param size
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/examDraftMod/searchDraftMods")
	public Map<String, Object> searchDraftRevs(String name, PageParam page) {
		Page<ExamDraftModifyVo> filePages = null;
		
		if (!StringUtils.isEmpty(name)) {
			filePages = edmService.findAllByName(name, page.getPage(), page.getRows());
		} else {
			filePages = edmService.findAll(page.getPage(), page.getRows());
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", filePages.getTotalElements());
		result.put("rows", filePages.getContent());

		return result;
		
	}
	
	@ResponseBody
	@RequestMapping("/examDraftMod/load")
	public Map<String, Object> load(HttpServletRequest request, HttpServletResponse response, Long id, String sreachType) {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		ExamDraftModifyVo edmVo = new ExamDraftModifyVo(); 
		Map<String, Object> result = new HashMap<>();
		long drtId = 0l;
		long orgId = Long.parseLong(userDetail.getOrgId());
		if (SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) && id != null) {
			edmVo = edmService.findByNorFileId(id);
			if (edmVo == null) {
				NormativeFileVo norFile = norService.findById(id);
				edmVo = new ExamDraftModifyVo();
				edmVo.setNormativeFile(norFile);
				String filePath = WordUtils.getFilePath(getProjectPath(request), 
						norFile, Stage.LEGAL_REVIEW.toString());
				String reviewComment = WordUtils.getFileNames(filePath, SysUtil.STAGE_LEGAL_REVCOMMENT);	//获取到审核意见名称
				edmVo.setReviewComment(reviewComment);
			}

		} else if (SysUtil.SEARCH_TYPE_DRAFTMODIFY.equals(sreachType)
				&& id != null) {
			edmVo = edmService.findById(id);
		}
		norService.genName(edmVo.getNormativeFile());	//处理联合起草单位名称等
		Stage stage = edmVo.getNormativeFile().getStage();
		result.put("vo", edmVo);
		drtId = edmVo.getNormativeFile().getDrtUnit().getId();	//单位为起草单位才具有操作权限
		if (!Stage.LEGAL_REVIEW_REVIEW.equals(stage) && !Stage.LEGAL_REVIEW_MODIFY.equals(stage)) {
			result.put("modifiable", false);
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
	 * 添加送审稿修改
	 * @param norId
	 * @param name
	 * @param reviewUnit
	 * @param reviewComment
	 * @param reviewDate
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/examDraftMod/addExamDraftMod")
	public Map<String, Object> addExamDraftMod(HttpServletRequest request, HttpServletResponse response, 
			String norId, String content, String drfcontent, Boolean isConfirm) {
		Map<String, Object> result = new HashMap<>();
		String filePath = null;
		String fileName = null;
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
		
		if (null != normativeFileVo) {
			ExamDraftModifyVo examDraftModifyVo = new ExamDraftModifyVo();
			examDraftModifyVo.setName(normativeFileVo.getName());
			examDraftModifyVo.setNormativeFile(normativeFileVo);
			examDraftModifyVo.setDraftingUnit(normativeFileVo.getDrtUnit());
			examDraftModifyVo.setDraftingUnitLeader(normativeFileVo.getDrtUnitLeader());
			examDraftModifyVo.setDraftingUnitClerk(normativeFileVo.getDrtUnitClerk());
			examDraftModifyVo.setContent(content);
			
			//生成路径
			String projectPath = getProjectPath(request);
			filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			if(!isConfirm){
				fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING_MODIFY + ")" + SysUtil.EXTENSION_NAME;
			}else{
				fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_PROTOCOL + ")" + SysUtil.EXTENSION_NAME;
			}
			
			String drfPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			String drfName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_INSTRUCTION + ")" + SysUtil.EXTENSION_NAME;
			WordUtils.htmlToWord(drfPath, drfName, drfcontent);	//保存修改的起草说明
			
			//读取到审核意见并保存
			String reviewComment = WordUtils.getFileNames(filePath, SysUtil.STAGE_LEGAL_REVCOMMENT);
			examDraftModifyVo.setReviewComment(reviewComment);
			
			try {
				ExamDraftModifyVo eds = edmService.saveOrUpdate(examDraftModifyVo, filePath, fileName);
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
	 * 删除送审稿修改
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/examDraftMod/delete")
	public String delete(HttpServletRequest request, HttpServletResponse response, String norId) {
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
		String filePath = null;
		boolean dFlag = false;
		if (null != normativeFileVo) {
			//获取路径
			String projectPath = getProjectPath(request);
			filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			if (null != norId && !"".equals(norId)) {
				ExamDraftModifyVo edrVo = edmService.findByNorFileId(Long.valueOf(norId));
				if (edrVo != null) {
					try {
						dFlag = edmService.delete(edrVo, filePath);	//删除
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
	 * 下载草案
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/examDraftMod/downloadWord")
	public String downloadWord(HttpServletRequest request, HttpServletResponse response, String norId) {
		boolean downloadFlag = false;
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_PROTOCOL + ")" + SysUtil.EXTENSION_NAME;
			File file = new File(filePath + File.separator + fileName);
			if(!file.exists()){
				fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING_MODIFY + ")" + SysUtil.EXTENSION_NAME;
			}
			downloadFlag = this.download(response, filePath + File.separator + fileName, fileName);	//下载草案
		}
		if (downloadFlag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
	}
	
	/**
	 * 定稿
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/examDraftMod/finalDraft")
	public String finalDraft(HttpServletRequest request, HttpServletResponse response, String norId, String content) {
		boolean flag = false;
		NormativeFileVo normativeFileVo = norService.findById(Long.valueOf(norId));
		if (normativeFileVo != null) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_PROTOCOL + ")" + SysUtil.EXTENSION_NAME;
			WordUtils.htmlToWord(filePath, fileName, content);//生成文档
			flag = true;
		}
		if (flag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
	}
	
	/**
	 * 读取相关文件的内容
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 * @throws ServiceException 
	 */
	@ResponseBody
	@RequestMapping("/examDraftMod/gainFileContent")
	public String gainFileContent(HttpServletRequest request, HttpServletResponse response, 
			Long id, String fileType, String fileName, String sreachType) throws ServiceException {
		NormativeFileVo normativeFileVo = null;
		if (SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) && id != null) {
			normativeFileVo = norService.findById(id);
			
		} else if (SysUtil.SEARCH_TYPE_DRAFTMODIFY.equals(sreachType) && id != null) {
			ExamDraftModifyVo edmVo = edmService.findById(id);
			normativeFileVo = norService.findById(edmVo.getNormativeFile().getId());
		}
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = "";
			if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_DRAFT.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
				if (StringUtils.isEmpty(fileName)) {
					fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING + ")" + SysUtil.EXTENSION_NAME;
				}
			} else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_INSTRUCTION.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
				if (StringUtils.isEmpty(fileName)) {
					fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_INSTRUCTION + ")" + SysUtil.EXTENSION_NAME;
				}
			}
			 
			if (!StringUtils.isEmpty(fileName)) {
				val = WordUtils.readFile(filePath + File.separator + fileName);	//获取文档内容
			} 
		}
		return val;
	}

	@ResponseBody
	@RequestMapping("/examDraftMod/print")
	public String print(HttpServletRequest request, Long norId) throws ServiceException {
		NormativeFileVo normativeFileVo = norService.findById(norId);
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_PROTOCOL + ")" + SysUtil.EXTENSION_NAME;
			File file = new File(filePath + File.separator + fileName);
			if(!file.exists()){
				fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING_MODIFY + ")" + SysUtil.EXTENSION_NAME;
			}
			//获取文档内容
			val = WordUtils.readFile(filePath + File.separator + fileName);
		}
		return val;
	}
}
