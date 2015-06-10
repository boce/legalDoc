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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.base.BaseController;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IAdoptCommentService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.AdoptCommentVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.security.WebPlatformUser;

/**
 * 反馈意见处理情况
 * 
 * @author xiaokui.li
 * 
 */
@Controller
public class AdoptCommentController extends BaseController {

	private final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private INormativeFileService normativeFileService;

	@Autowired
	private IAdoptCommentService adoptCommentService;

	@Autowired
	IOrganizationService organizationService;

	@ResponseBody
	@RequestMapping("/adoptComment/load")
	public Map<String, Object> load(HttpServletRequest request, Long id, String sreachType) {
		Map<String, Object> result = new HashMap<>();
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long orgId = Long.parseLong(userDetail.getOrgId());
		if (SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) && id != null) {
			AdoptCommentVo vo = new AdoptCommentVo();
			NormativeFileVo norFile = normativeFileService.findById(id);
			vo.setNormativeFile(norFile);
			String filePath = getFilePath(request, norFile);
			String names = WordUtils.getFileNames(filePath, SysUtil.STAGE_LEGAL_FEEDBACK);
			vo.setFeedbackComment(names);
			result.put("modifiable", true);
			result.put("vo", vo);
		} else if (SysUtil.SEARCH_TYPE_ADOPTCOMMENT.equals(sreachType) && id != null) {
			AdoptCommentVo vo = adoptCommentService.findById(id);
			if(vo != null){
				NormativeFileVo norFileVo = vo.getNormativeFile();
				Stage stage = norFileVo.getStage();
				if(Stage.REQUEST_COMMENT_MODIFY.equals(stage) && !vo.getIsNeedModify()){
					result.put("modifiable", true);
				}else if(!Stage.REQUEST_COMMENT_ADOPT.equals(stage)){
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
		}
		return result;
	}

	/**
	 * 保存反馈意见处理情况
	 * @param request
	 * @param vo
	 * @param isNeedModify 是否需要修改征求意见稿，如果不需要，则跳过修改流程，进入送审稿报送流程
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/adoptComment/save")
	public Map<String, Object> save(HttpServletRequest request, AdoptCommentVo vo) {
		Map<String, Object> result = new HashMap<>();
		if (vo != null) {
			String rootPath = getProjectPath(request);
			try {
				AdoptCommentVo adoptCommentVo = adoptCommentService.saveOrUpdate(vo, rootPath);
				result.put("success", true);
				result.put("msg", "保存成功！");
				result.put("vo", adoptCommentVo);
			} catch (ServiceException e) {
				log.error(e.getMessage(), e.getCause());
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/adoptComment/delete")
	public Map<String, Object> delete(HttpServletRequest request, Long id) {
		Map<String, Object> result = new HashMap<>();
		if (id != null) {
			String rootPath = getProjectPath(request);
			try {
				boolean success = adoptCommentService.delete(id, rootPath);
				if (success) {
					result.put("success", true);
					result.put("msg", "删除成功");
				} else {
					result.put("success", false);
					result.put("msg", "请先选择一个反馈意见处理情况单");
				}
			} catch (ServiceException e) {
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
		} else {
			result.put("success", false);
			result.put("msg", "请先选择一个反馈意见处理情况单");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/adoptComment/find")
	public Map<String, Object> find(String name, PageParam page) {

		Set<Long> orgIds = organizationService.findAuthOrgId();

		Page<AdoptCommentVo> filePages = adoptCommentService.findByName(name, orgIds, page.getPage(), page.getRows());

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", filePages.getTotalElements());
		result.put("rows", filePages.getContent());

		return result;
	}

	@ResponseBody
	@RequestMapping("/adoptComment/viewFeedback")
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
	@RequestMapping("/adoptComment/print")
	public String print(HttpServletRequest request, String norId) throws ServiceException {
		NormativeFileVo normativeFileVo = normativeFileService.findById(Long.valueOf(norId));
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_ADOPTCOMMENT + ")" + SysUtil.EXTENSION_NAME;
			// 获取文档内容
			val = WordUtils.readFile(filePath + File.separator + fileName);
		}
		return val;
	}

	@ResponseBody
	@RequestMapping("/adoptComment/export")
	public String export(HttpServletRequest request, HttpServletResponse response, String norId) {
		boolean downloadFlag = false;
		NormativeFileVo normativeFileVo = normativeFileService.findById(Long.valueOf(norId));
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_ADOPTCOMMENT + ")" + SysUtil.EXTENSION_NAME;
			// 下载说明文档内容
			downloadFlag = this.download(response, filePath + File.separator + fileName, fileName);
		}
		if (downloadFlag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
	}

	private String getFilePath(HttpServletRequest request, NormativeFileVo normativeFileVo) {
		String projectPath = getProjectPath(request);
		String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
		return filePath;
	}

}
