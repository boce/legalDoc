package com.cdrundle.legaldoc.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IFeedbackCommentService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.service.IRequestCommentService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.FeedbackCommentVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.RequestCommentVo;
import com.cdrundle.security.WebPlatformUser;

/**
 * 征求意见单
 * @author xiaokui.li
 *
 */
@Controller
public class RequestCommentController extends BaseController {
	
	private final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private INormativeFileService normativeFileService;

	@Autowired
	private IRequestCommentService requestCommentService;

	@Autowired
	IOrganizationService organizationService;

	@Autowired
	IFeedbackCommentService feedbackCommentService;
	
	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws IOException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));
	}

	@ResponseBody
	@RequestMapping("/requestComment/load")
	public Map<String, Object> load(HttpServletRequest request, Long id, String sreachType) {
		Map<String, Object> result = new HashMap<>();
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long orgId = Long.parseLong(userDetail.getOrgId());
		if (SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) && id != null) {
			RequestCommentVo vo = new RequestCommentVo();
			NormativeFileVo norFile = normativeFileService.findById(id);
			vo.setNormativeFile(norFile);
			String fileName = norFile.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTCOMMENT + ")" + SysUtil.EXTENSION_NAME;
			vo.setRequestingDraft(fileName);
			result.put("modifiable", true);
			result.put("vo", vo);
		} else if (SysUtil.SEARCH_TYPE_REQUESTCOMMENT.equals(sreachType) && id != null) {
			RequestCommentVo vo = requestCommentService.findById(id);
			NormativeFileVo norFile = vo.getNormativeFile();
			String fileName = norFile.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTCOMMENT + ")" + SysUtil.EXTENSION_NAME;
			vo.setRequestingDraft(fileName);
			List<FeedbackCommentVo> feedbackCommentVos = feedbackCommentService.findByNorFile(norFile.getId());
			if(feedbackCommentVos != null && !feedbackCommentVos.isEmpty()){
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
	@RequestMapping("/requestComment/save")
	public Map<String, Object> save(HttpServletRequest request, RequestCommentVo vo){
		Map<String, Object> result = new HashMap<>();
		if (vo != null) {
			String rootPath = getProjectPath(request);
			try {
				RequestCommentVo requestCommentVo = requestCommentService.saveOrUpdate(vo, rootPath);
				result.put("success", true);
				result.put("msg", "保存成功！");
				result.put("vo", requestCommentVo);
			} catch (ServiceException e) {
				log.error(e.getMessage(), e.getCause());
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/requestComment/delete")
	public Map<String, Object> delete(HttpServletRequest request, Long id) {
		Map<String, Object> result = new HashMap<>();
		if (id != null) {
			String rootPath = getProjectPath(request);
			try {
				boolean success = requestCommentService.delete(id, rootPath);
				if(success){
					result.put("success", true);
					result.put("msg", "删除成功");
				}else{
					result.put("success", false);
					result.put("msg", "请先选择一个征求意见单");
				}
			} catch (ServiceException e) {
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
		}else{
			result.put("success", false);
			result.put("msg", "请先选择一个征求意见单");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/requestComment/find")
	public Map<String, Object> find(String name, PageParam page) {
		Page<RequestCommentVo> filePages = null;

		Set<Long> orgIds = organizationService.findAuthOrgId();
		filePages = requestCommentService.findByName(name, orgIds, page.getPage(), page.getRows());

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", filePages.getTotalElements());
		result.put("rows", filePages.getContent());

		return result;
	}

	@ResponseBody
	@RequestMapping("/requestComment/viewFeedback")
	public String viewFeedback(HttpServletRequest request, Long norId, String fileName) throws ServiceException {
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		String val = null;
		if (normativeFileVo != null) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DRAFTING.toString());
			// 获取文档内容
			val = WordUtils.readFile(filePath + File.separator + fileName);
		}

		return val;
	}

	@ResponseBody
	@RequestMapping("/requestComment/print")
	public String print(HttpServletRequest request, String id) throws ServiceException {
		RequestCommentVo requestCommentVo = requestCommentService.findById(Long.parseLong(id));
		NormativeFileVo normativeFileVo = requestCommentVo.getNormativeFile();
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTOPINIONLETTER + "-"
					+ requestCommentVo.getRequestFromUnit().getText() + ")" + SysUtil.EXTENSION_NAME;
			// 获取文档内容
			val = WordUtils.readFile(filePath + File.separator + fileName);
		}
		return val;
	}

	@ResponseBody
	@RequestMapping("/requestComment/export")
	public String export(HttpServletRequest request, HttpServletResponse response, String id) {
		RequestCommentVo requestCommentVo = requestCommentService.findById(Long.parseLong(id));
		NormativeFileVo normativeFileVo = requestCommentVo.getNormativeFile();
		boolean downloadFlag = false;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTOPINIONLETTER + "-"
					+ requestCommentVo.getRequestFromUnit().getText() + ")" + SysUtil.EXTENSION_NAME;
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
	 * 获取反馈单位
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/requestComment/getRequestFromUnit")
	public List<OrgShortVo> getRequestFromUnit(Long id, Long norId){
		NormativeFileVo norFileVo = normativeFileService.findById(norId);
		String involvedOrges = norFileVo.getInvolvedOrges();
		List<OrgShortVo> orgs = new ArrayList<>();
		if(StringUtils.isNotEmpty(involvedOrges)){
			List<RequestCommentVo> vos = requestCommentService.findByNorFile(norId);
			involvedOrges = involvedOrges.replaceAll("\"", "");
			String[] involvedOrgArray = involvedOrges.split(",");
			Set<Long> involvedOrgSet = new LinkedHashSet<>();
			for (int i = 0; i < involvedOrgArray.length; i++) {
				Long involvedOrgId = Long.parseLong(involvedOrgArray[i]);
				boolean isNeed = true;
				for (Iterator<RequestCommentVo> iterator = vos.iterator(); iterator.hasNext();) {
					RequestCommentVo vo = iterator.next();
					if(involvedOrgId.equals(vo.getRequestFromUnit().getId())){
						if(id != null && id.equals(vo.getId())){
							isNeed = true;
						}else{
							isNeed = false;
						}
						break;
					}
				}
				if(isNeed){
					involvedOrgSet.add(involvedOrgId);
				}
			}
			if(!involvedOrgSet.isEmpty()){
				orgs = organizationService.findByIdsShort(involvedOrgSet);
			}
		}
		return orgs;
	}
	@ResponseBody
	@RequestMapping("/requestComment/completeRequestback")
	public Map<String, Object> completeRequestback(Long id){
		Map<String, Object> result = new HashMap<>();
		boolean success;
		try {
			success = requestCommentService.completeRequestback(id);
			result.put("success", success);
			if(success){
				result.put("msg", "操作成功！");
			}else{
				result.put("msg", "还未到最晚反馈时间，不能结束！");
			}
		} catch (ServiceException e) {
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
}
