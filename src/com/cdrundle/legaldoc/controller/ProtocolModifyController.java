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
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.service.IProtocolModifyService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.ProtocolModifyVo;
import com.cdrundle.security.WebPlatformUser;

/**
 * @author  XuBao
 *
 * 2014年7月4日
 */
@Controller
public class ProtocolModifyController extends  BaseController{
		
	private final Log log = LogFactory.getLog(getClass());
	@Autowired
	private  IProtocolModifyService   protocolModifyService;
	@Autowired
	private  INormativeFileService  normativeFileService;
	@Autowired
	private IOrganizationService organizationService;
	

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws IOException
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));

	}
	
	/**
	 * 弹出规范性文件时候加载数据
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@RequestMapping("protocolModify/load")
	@ResponseBody
	public Map<String, Object> load(HttpServletRequest request, HttpServletResponse response, Long id,String sreachType) {
		
		Map<String, Object>  result = new HashMap<String, Object>();
		// 得到当前登录用户信息
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long orgId = Long.parseLong(userDetail.getOrgId());
		
		if( SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) &&  id != null){
			ProtocolModifyVo protocolModifyVo  = protocolModifyService.findByNorId(id);
			NormativeFileVo  normativeFileVo  =   normativeFileService.findById(id);
			protocolModifyVo.setNormativeFile(normativeFileVo);
			
			String projectPath = getProjectPath(request);
			//读取到审议意见
			String deliberationCommentPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DELIBERATION.toString());
			String deliberationCommentName = WordUtils.getFileNames(deliberationCommentPath, SysUtil.STAGE_LEGAL_DELIBERATIONCOMMENT);
			protocolModifyVo.setDeliberationComment(deliberationCommentName);
			
			result.put("vo", protocolModifyVo);
			result.put("modifiable", true);
			  return result;
		} else if (SysUtil.SEARCH_TYPE_PMO.equals(sreachType) && id != null){
			ProtocolModifyVo  protocolModifyVo = protocolModifyService.findById(id);
			Stage stage = protocolModifyVo.getNormativeFile().getStage();
			if(!stage.equals(Stage.DELIBERATION_MODIFY)){
				result.put("modifiable", false);
			}else{
				if(orgId.equals(protocolModifyVo.getDraftingUnit().getId()) ){
					result.put("modifiable", true);
				}else{
					result.put("modifiable", false);
				}
			}
			
			result.put("vo", protocolModifyVo);
			 return result;
		}
		return null;
	}
	
	/**
	 * 保存草案审议
	 * @param request
	 * @param response
	 * @param deliberationRequestVo
	 * @param norId
	 * @param name
	 * @param requestUint
	 * @param requestDate
	 * @return
	 */
	@RequestMapping("protocolModify/save")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request, HttpServletResponse response,
			ProtocolModifyVo protocolModifyVo, Boolean isConfirm, String content, String drfcontent) {
		Map<String, Object> result = new HashMap<>();
		if(protocolModifyVo != null){
			
			NormativeFileVo normativeFileVo = normativeFileService.findById(protocolModifyVo.getNormativeFile().getId());
			
			protocolModifyVo.setName(normativeFileVo.getName());
			protocolModifyVo.setDraftingUnit(normativeFileVo.getDrtUnit());
			protocolModifyVo.setDraftingUnitClerk(normativeFileVo.getDrtUnitClerk());
			protocolModifyVo.setDraftingUnitLeader(normativeFileVo.getDrtUnitLeader());
			
			//生成路径
			String projectPath = getProjectPath(request);
			
			//读取到审议意见
			String  deliberationCommentPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DELIBERATION.toString());
			String  deliberationCommentFilter = SysUtil.STAGE_LEGAL_DELIBERATIONCOMMENT;
			String deliberationCommentName = WordUtils.getFileNames(deliberationCommentPath,deliberationCommentFilter);
			protocolModifyVo.setDeliberationComment(deliberationCommentName);
			
			//保存草案
//			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_PROTOCOL + ")" + SysUtil.EXTENSION_NAME;
//			WordUtils.htmlToWord(filePath, fileName, content);	//保存修改后的草案
			
			//保存起草说明
			String drfPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			String drfName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_INSTRUCTION + ")" + SysUtil.EXTENSION_NAME;
			WordUtils.htmlToWord(drfPath, drfName, drfcontent);	//保存修改的起草说明
			
			ProtocolModifyVo vo = null;
			try {
				vo = protocolModifyService.saveOrUpdate(protocolModifyVo, projectPath, fileName, isConfirm);
				result.put("success", true);
				result.put("msg", "保存成功！");
				result.put("vo", vo);
			} catch (ServiceException e) {
				log.error(e.getMessage(), e.getCause());
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
		}
		return result;
	}
	
	
	/**
	 * 删除草案审议
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@RequestMapping("protocolModify/delete")
	@ResponseBody
	public String delete(HttpServletRequest request, HttpServletResponse response,Long id) {
		
		String message = "";
		
		//获取路径
		String projectPath = getProjectPath(request);
		try {
			boolean flag = protocolModifyService.delete(id, projectPath);
			if ( flag ) {
				return SysUtil.JSON_MSG_SUCCESS;
			} else {
				return SysUtil.JSON_MSG_FAIL;
			}
		} catch (ServiceException e) {
			message = "{\"message\" : \"" +e.getMessage()+"\"}";
		}
		return message;
	}
	
	/**
	 * 查询草案审议
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping("protocolModify/find.do")
	@ResponseBody
	public Map<String, Object> find(String name, PageParam page) {
		Page<ProtocolModifyVo> filePages = null;
		Set<Long> orgIds = organizationService.findAuthOrgId();
		filePages = protocolModifyService.find(page.getPage(), page.getRows(), name, orgIds);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", filePages.getTotalElements());
		result.put("rows", filePages.getContent());

		return result;
	}
	
	/**
	 * 获取文件内容
	 * @param request
	 * @param norId
	 * @param fileName
	 * @return
	 * @throws ServiceException 
	 */
	@ResponseBody
	@RequestMapping("protocolModify/gainContent")
	public String gainContent(HttpServletRequest request, Long norId, String fileName) throws ServiceException {
		
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		String val = null;
		if (normativeFileVo != null) {
			String filePath=getFilePath(request,normativeFileVo);
			// 获取文档内容
			val = WordUtils.readFile(filePath + File.separator + fileName);
		}

		return val;
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
	@RequestMapping("/protocolModify/gainFileContent")
	public String gainFileContent(HttpServletRequest request, HttpServletResponse response, 
			Long id, String fileType, String fileName, String sreachType) throws ServiceException {
		NormativeFileVo normativeFileVo = null;
		if (SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) && id != null) {
			normativeFileVo = normativeFileService.findById(id);
			
		} else if (SysUtil.SEARCH_TYPE_PMO.equals(sreachType) && id != null) {
			ProtocolModifyVo proVo = protocolModifyService.findById(id);
			normativeFileVo = normativeFileService.findById(proVo.getNormativeFile().getId());
		}
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = "";
			if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_PROTOCOL.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
				if (StringUtils.isEmpty(fileName)) {
					fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_PROTOCOL + ")" + SysUtil.EXTENSION_NAME;
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
	
	/**
	 * 打印
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 * @throws ServiceException 
	 */
	@ResponseBody
	@RequestMapping("protocolModify/print")
	public String print(HttpServletRequest request, Long norId) throws ServiceException {
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DELIBERATION.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_CONTENT + ")"+SysUtil.EXTENSION_NAME;
			//获取文档内容
			val = WordUtils.readFile(filePath + File.separator + fileName);
		}
		return val;
	}
	
	/**
	 * 导出
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("protocolModify/export")
	public String export(HttpServletRequest request,HttpServletResponse response, Long norId) {
		boolean downloadFlag = false;
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		if ( normativeFileVo != null) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DELIBERATION.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_CONTENT+ ")"+SysUtil.EXTENSION_NAME;
			//下载说明文档内容
			downloadFlag = this.download(response, filePath + File.separator + fileName, fileName);	
		}
		if (downloadFlag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
	}
	
	private String getFilePath(HttpServletRequest request,NormativeFileVo normativeFileVo)
	{
		String projectPath = getProjectPath(request);
		String filePath = WordUtils.getFilePath(projectPath,
				normativeFileVo, Stage.DELIBERATION.toString());
		return filePath;
	}
	

}
