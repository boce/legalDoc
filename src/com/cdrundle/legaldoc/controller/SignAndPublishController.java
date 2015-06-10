package com.cdrundle.legaldoc.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.base.BaseController;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.enums.StatusEnumEditor;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.service.ISignAndPublishService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.SignAndPublishVo;
import com.cdrundle.security.WebPlatformUser;

/**
 * @author XuBao
 * 
 *         2014年7月9日
 */
@Controller
public class SignAndPublishController extends BaseController {

	private final Log log = LogFactory.getLog(getClass());
	@Autowired
	private ISignAndPublishService signAndPublishService;
	@Autowired
	private INormativeFileService normativeFileService;
	@Autowired
	private IOrganizationService organizationService;
	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws IOException {
		binder.registerCustomEditor(Status.class, new StatusEnumEditor());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));

	}

	/**
	 * 弹出规范性文件时候加载数据
	 * 
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@RequestMapping("signAndPublish/load")
	@ResponseBody
	public Map<String, Object> load(HttpServletRequest request, HttpServletResponse response, Long id, String sreachType) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 得到当前登录用户信息
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long orgId = Long.parseLong(userDetail.getOrgId());

		if (SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) && id != null) {
			SignAndPublishVo signAndPublishVo = signAndPublishService.findByNorId(id);
			NormativeFileVo normativeFileVo = normativeFileService.findById(id);
			signAndPublishVo.setNormativeFile(normativeFileVo);

			result.put("vo", signAndPublishVo);
			result.put("modifiable", true);

			return result;
		} else if (SysUtil.SEARCH_TYPE_SIGNANDPUBLISH.equals(sreachType) && id != null) {
			SignAndPublishVo signAndPublishVo = signAndPublishService.findById(id);
			Stage stage = signAndPublishVo.getNormativeFile().getStage();
			if (!stage.equals(Stage.PUBLISH)) {
				result.put("modifiable", false);
			} else {
				if (orgId.equals(signAndPublishVo.getDecisionMakingUnit().getId()) || orgId.equals(signAndPublishVo.getDraftingUnit().getId())) {
					result.put("modifiable", true);
				} else {
					result.put("modifiable", false);
				}
			}
			result.put("vo", signAndPublishVo);

			return result;
		}
		return null;
	}

	/**
	 * 保存签署与发布
	 * 
	 * @param request
	 * @param response
	 * @param signAndPublishVo
	 * @param legalDoc
	 * @return
	 */
	@RequestMapping("signAndPublish/save")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request, HttpServletResponse response, SignAndPublishVo signAndPublishVo, String legalDoc) {
		Map<String, Object> result = new HashMap<>();
		String fileName = "";
		NormativeFileVo normativeFileVo = normativeFileService.findById(signAndPublishVo.getNormativeFile().getId());

		signAndPublishVo.setName(normativeFileVo.getName());
		signAndPublishVo.setDraftingUnit(normativeFileVo.getDrtUnit());

		// 生成路径
		String projectPath = getProjectPath(request);

		SignAndPublishVo vo = null;

		try {
			vo = signAndPublishService.saveOrUpdate(signAndPublishVo, projectPath, fileName);
			result.put("success", true);
			result.put("msg", "保存成功！");
			result.put("vo", vo);
		} catch (ServiceException e) {
			log.error(e.getMessage(), e.getCause());
			result.put("success", false);
			result.put("msg", e.getMessage());
		} catch (SchedulerException e) {
			log.error(e.getMessage(), e.getCause());
			result.put("success", false);
			result.put("msg", "保存出错！");
		}
		return result;
	}

	/**
	 * 删除签署发布
	 * 
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@RequestMapping("signAndPublish/delete")
	@ResponseBody
	public String delete(HttpServletRequest request, HttpServletResponse response, Long id) {

		String message = "";
		// 获取路径
		String projectPath = getProjectPath(request);

		try {
			boolean flag = signAndPublishService.delete(id, projectPath);
			if (flag) {
				return SysUtil.JSON_MSG_SUCCESS;
			} else {
				return SysUtil.JSON_MSG_FAIL;
			}
		} catch (ServiceException e) {
			message = "{\"message\" : \"" + e.getMessage() + "\"}";
		} catch (SchedulerException e) {
			log.error(e);
			return SysUtil.JSON_MSG_FAIL;
		}
		return message;
	}

	/**
	 * 查询签署与发布
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping("signAndPublish/find.do")
	@ResponseBody
	public Map<String, Object> find(String name, PageParam page) {
		Page<SignAndPublishVo> filePages = null;
		Set<Long> orgIds = organizationService.findAuthOrgId();
		// 获取到规范性文件
		filePages = signAndPublishService.find(page.getPage(), page.getRows(), name, orgIds);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", filePages.getTotalElements());
		result.put("rows", filePages.getContent());

		return result;
	}

	/**
	 * 打印
	 * 
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 * @throws ServiceException
	 */
	@ResponseBody
	@RequestMapping("signAndPublish/print")
	public String print(HttpServletRequest request, Long norId) throws ServiceException {
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.PUBLISH.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_LEGALDOC + ")" + SysUtil.EXTENSION_NAME;
			// 获取文档内容
			val = WordUtils.readFile(filePath + File.separator + fileName);
		}
		return val;
	}

	/**
	 * 导出
	 * 
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("signAndPublish/export")
	public String export(HttpServletRequest request, HttpServletResponse response, Long norId) {
		boolean downloadFlag = false;
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		if (normativeFileVo != null) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.PUBLISH.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_LEGALDOC + ")" + SysUtil.EXTENSION_NAME;
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
	 * 加载文件内容
	 * 
	 * @param request
	 * @param norId
	 * @param fileName
	 * @return
	 * @throws ServiceException
	 */
	@ResponseBody
	@RequestMapping("signAndPublish/gainContent")
	public String gainContent(HttpServletRequest request, Long norId) throws ServiceException {

		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);

		String content = null;
		String filePath = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);

			filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DELIBERATION.toString());
			content = WordUtils.readFile(filePath + File.separator + normativeFileVo.getName() + SysUtil.EXTENSION_NAME); // 获取文档内容
		}
		return content;
	}

}
