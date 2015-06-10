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
import com.cdrundle.legaldoc.service.IAdoptCommentService;
import com.cdrundle.legaldoc.service.IFeedbackCommentService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.service.IRequestCommentService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.AdoptCommentVo;
import com.cdrundle.legaldoc.vo.FeedbackCommentVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.RequestCommentVo;
import com.cdrundle.security.WebPlatformUser;

@Controller
public class FeedbackCommentController extends BaseController {
	
	private final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private INormativeFileService normativeFileService;

	@Autowired
	private IFeedbackCommentService feedbackCommentService;
	
	@Autowired
	IOrganizationService organizationService;
	
	@Autowired
	IRequestCommentService requestCommentService;
	
	@Autowired
	IAdoptCommentService adoptCommentService;

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws IOException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));
	}

	/**
	 * 获取初始化数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/feedbackComment/init")
	public Map<String, Object> init(Long norId) {
		Map<String, Object> result = new HashMap<>();
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long userId = Long.parseLong(userDetail.getUserId());
		Long orgId = Long.parseLong(userDetail.getOrgId());
		RequestCommentVo requestCommentVo = requestCommentService.findByNorFileId(norId, orgId);
		if(requestCommentVo != null){
			Date latestFeedbackDate = requestCommentVo.getLatestFeedbackDate();
			result.put("latestFeedbackDate", latestFeedbackDate);
		}
		NormativeFileVo norFileVo = normativeFileService.findById(norId);
		String involvedOrges = norFileVo.getInvolvedOrges();
		if(StringUtils.isNotEmpty(involvedOrges)){
			involvedOrges = involvedOrges.replaceAll("\"", "");
			String[] involvedOrgArray = involvedOrges.split(",");
			for (int i = 0; i < involvedOrgArray.length; i++) {
				Long involvedOrg = Long.parseLong(involvedOrgArray[i]);
				if(orgId.equals(involvedOrg)){
					result.put("feedbackUnit", orgId);
					result.put("feedbackUnitClerk", userId);
				}
			}
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping("/feedbackComment/load")
	public Map<String, Object> load(HttpServletRequest request, Long id, String sreachType) {
		Map<String, Object> result = new HashMap<>();
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long orgId = Long.parseLong(userDetail.getOrgId());
		if (SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) && id != null) {
			FeedbackCommentVo vo = new FeedbackCommentVo();
			NormativeFileVo norFile = normativeFileService.findById(id);
			vo.setNormativeFile(norFile);
			String fileName = norFile.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTCOMMENT + ")" + SysUtil.EXTENSION_NAME;
			vo.setRequestingDraft(fileName);
			result.put("modifiable", true);
			result.put("vo", vo);
		} else if (SysUtil.SEARCH_TYPE_FEEDBACKCOMMENT.equals(sreachType) && id != null) {
			FeedbackCommentVo vo = feedbackCommentService.findById(id);
			NormativeFileVo norFile = vo.getNormativeFile();
			String fileName = norFile.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTCOMMENT + ")" + SysUtil.EXTENSION_NAME;
			vo.setRequestingDraft(fileName);
			AdoptCommentVo adoptCommentVo = adoptCommentService.findByNorFileId(norFile.getId());
			if(adoptCommentVo != null && adoptCommentVo.getId() != null){
				result.put("modifiable", false);
			}else{
				if(orgId.equals(vo.getFeedbackUnit().getId())){
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
	@RequestMapping("/feedbackComment/save")
	public Map<String, Object> save(HttpServletRequest request, FeedbackCommentVo vo){
		Map<String, Object> result = new HashMap<>();
		if (vo != null) {
			String rootPath = getProjectPath(request);

			try {
				FeedbackCommentVo feedbackCommentVo = feedbackCommentService.saveOrUpdate(vo, rootPath);
				result.put("success", true);
				result.put("msg", "保存成功！");
				result.put("vo", feedbackCommentVo);
			} catch (ServiceException e) {
				log.error(e.getMessage(), e.getCause());
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/feedbackComment/delete")
	public Map<String, Object> delete(HttpServletRequest request, Long id) {
		Map<String, Object> result = new HashMap<>();
		if (id != null) {
			String rootPath = getProjectPath(request);
			try {
				boolean success = feedbackCommentService.delete(id, rootPath);
				if(success){
					result.put("success", true);
					result.put("msg", "删除成功");
				}else{
					result.put("success", false);
					result.put("msg", "请先选择一个反馈意见单");
				}
			} catch (ServiceException e) {
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
		}else{
			result.put("success", false);
			result.put("msg", "请先选择一个反馈意见单");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/feedbackComment/find")
	public Map<String, Object> find(String name, PageParam page) {

		Set<Long> orgIds = organizationService.findAuthOrgId();
		Page<FeedbackCommentVo> filePages = feedbackCommentService.findByName(name, orgIds, page.getPage(), page.getRows());

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", filePages.getTotalElements());
		result.put("rows", filePages.getContent());

		return result;
	}

	@ResponseBody
	@RequestMapping("/feedbackComment/viewFeedback")
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
	@RequestMapping("/feedbackComment/print")
	public String print(HttpServletRequest request, String id) throws ServiceException {
		FeedbackCommentVo feedbackCommentVo = feedbackCommentService.findById(Long.parseLong(id));
		NormativeFileVo normativeFileVo = feedbackCommentVo.getNormativeFile();
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_FEEDBACK + "-" + feedbackCommentVo.getFeedbackUnit().getText()
					+ ")" + SysUtil.EXTENSION_NAME;
			// 获取文档内容
			val = WordUtils.readFile(filePath + File.separator + fileName);
		}
		return val;
	}

	@ResponseBody
	@RequestMapping("/feedbackComment/export")
	public String export(HttpServletRequest request, HttpServletResponse response, String id) {
		FeedbackCommentVo feedbackCommentVo = feedbackCommentService.findById(Long.parseLong(id));
		NormativeFileVo normativeFileVo = feedbackCommentVo.getNormativeFile();
		boolean downloadFlag = false;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_FEEDBACK + "-" + feedbackCommentVo.getFeedbackUnit().getText()
					+ ")" + SysUtil.EXTENSION_NAME;
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
	 * 获取最晚反馈时间
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/feedbackComment/getLatestFeedbackDate")
	public Map<String, Object> getLatestFeedbackDate(Long norId, Long reqFromUnitId) {
		Map<String, Object> result = new HashMap<>();
		RequestCommentVo requestCommentVo = requestCommentService.findByNorFileId(norId, reqFromUnitId);
		if(requestCommentVo != null){
			Date latestFeedbackDate = requestCommentVo.getLatestFeedbackDate();
			result.put("latestFeedbackDate", latestFeedbackDate);
		}
		return result;
	}
	
	/**
	 * 获取反馈单位
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/feedbackComment/getFeedbackUnit")
	public List<OrgShortVo> getFeedbackUnit(Long id, Long norId){
		NormativeFileVo norFileVo = normativeFileService.findById(norId);
		String involvedOrges = norFileVo.getInvolvedOrges();
		List<OrgShortVo> orgs = new ArrayList<>();
		if(StringUtils.isNotEmpty(involvedOrges)){
			List<FeedbackCommentVo> vos = feedbackCommentService.findByNorFile(norId);
			involvedOrges = involvedOrges.replaceAll("\"", "");
			String[] involvedOrgArray = involvedOrges.split(",");
			Set<Long> involvedOrgSet = new LinkedHashSet<>();
			WebPlatformUser userDetail = SysUtil.getLoginInfo();
			long logInOrgId = Long.parseLong(userDetail.getOrgId());
			for (int i = 0; i < involvedOrgArray.length; i++) {
				Long involvedOrgId = Long.parseLong(involvedOrgArray[i]);
				boolean isNeed = true;
				for (Iterator<FeedbackCommentVo> iterator = vos.iterator(); iterator.hasNext();) {
					FeedbackCommentVo vo = iterator.next();
					if(involvedOrgId.equals(vo.getFeedbackUnit().getId())){
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
				if(logInOrgId == involvedOrgId.longValue()){
					involvedOrgSet.add(logInOrgId);
				}
			}
			orgs = organizationService.findByIdsShort(involvedOrgSet);
		}
		return orgs;
	}
}
