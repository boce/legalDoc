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
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.enums.StatusEnumEditor;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IDeliberationRequestService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.service.IUserService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.DeliberationRequestVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.security.WebPlatformUser;

/**
 * @author XuBao
 * 
 *         2014年6月11日
 */
@Controller
public class DeliberationRequestController  extends  BaseController{
	private final Log log = LogFactory.getLog(getClass());
	@Autowired
	private IDeliberationRequestService deliberationRequestService;
	@Autowired
	private INormativeFileService normativeFileService;
	@Autowired
	private IUserService  userService;
	@Autowired
	private IOrganizationService  organizationService;
	
	
	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws IOException
	{
		binder.registerCustomEditor(Status.class, new StatusEnumEditor());
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
	@RequestMapping("deliberationRequest/load")
	@ResponseBody
	public Map<String, Object> load(HttpServletRequest request, HttpServletResponse response, Long id,String sreachType) {
		
		Map<String, Object>  result = new HashMap<String, Object>();
		// 得到当前登录用户信息
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long orgId = Long.parseLong(userDetail.getOrgId());
		
		if( SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) &&  id != null){
			
			DeliberationRequestVo deliberationRequestVo  = deliberationRequestService.findByNorId(id);
			
			NormativeFileVo  normativeFileVo  =   normativeFileService.findById(id);
			//得到联合起草单位已经联合起草单位负责人名称
			normativeFileService.genName(normativeFileVo);
			
			deliberationRequestVo.setNormativeFile(normativeFileVo);
			
			String projectPath = getProjectPath(request);
			//读取到草案
			String  protocolPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			String protocolName = WordUtils.getFileNames(protocolPath, SysUtil.STAGE_LEGAL_PROTOCOL);
			deliberationRequestVo.setProtocol(protocolName);

			//读取到法律审查意见书
			String  reviewCommentsPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			String 	reviewCommentsName = WordUtils.getFileNames(reviewCommentsPath,SysUtil.STAGE_LEGAL_REVCOMMENT );
			deliberationRequestVo.setReviewComments(reviewCommentsName);
			
			//读取到起草说明
			String  draftingInstructionName = normativeFileVo.getDraftInstruction();
			deliberationRequestVo.setDraftingInstruction(draftingInstructionName);
			
			//读取征求意见的相关材料
			String requestCommentsPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String requestCommentsName = WordUtils.getFileNames(requestCommentsPath,SysUtil.STAGE_LEGAL_ADOPTCOMMENT);
			deliberationRequestVo.setRequestComments(requestCommentsName);
			
			result.put("vo", deliberationRequestVo);
			result.put("modifiable", true);
			
			return result;
		} else if (SysUtil.SEARCH_TYPE_DELREQUEST.equals(sreachType) && id != null){
			DeliberationRequestVo  deliberationRequestVo = deliberationRequestService.findDeliberationRequestById(id);
			
			//得到联合起草单位名称和负责人名称
			NormativeFileVo normativeFileVo = deliberationRequestVo.getNormativeFile();
			normativeFileService.genName(normativeFileVo);
			deliberationRequestVo.setUnionDrtUnitName(normativeFileVo.getUnionDrtUnitName());
			deliberationRequestVo.setUnionDrtUnitLeaderName(normativeFileVo.getUnionDrtUnitLeaderName());
			
			Stage stage = deliberationRequestVo.getNormativeFile().getStage();
			if(!stage.equals(Stage.DELIBERATION_REQUEST)){
				result.put("modifiable", false);
			}else{
				if( deliberationRequestVo.getDraftingUnit().getId().equals(orgId)){
					result.put("modifiable", true);
				}else{
					result.put("modifiable", false);
				}
			}
			result.put("vo", deliberationRequestVo);
			
			 return result;
		}
		return null;
	}
	
	/**
	 * 保存审议报请
	 * @param request
	 * @param response
	 * @param deliberationRequestVo
	 * @param norId
	 * @param name
	 * @param requestUint
	 * @param requestDate
	 * @return
	 */
	@RequestMapping("deliberationRequest/save")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request, HttpServletResponse response,DeliberationRequestVo deliberationRequestVo) {
		Map<String, Object> result = new HashMap<>();
		String filePath = null;
		String fileName = null;
		NormativeFileVo normativeFileVo = normativeFileService.findById(deliberationRequestVo.getNormativeFile().getId());
		
		deliberationRequestVo.setName(normativeFileVo.getName());
		deliberationRequestVo.setDraftingUnit(normativeFileVo.getDrtUnit());
		deliberationRequestVo.setDraftingUnitClerk(normativeFileVo.getDrtUnitClerk());
		deliberationRequestVo.setDraftingUnitLeader(normativeFileVo.getDrtUnitLeader());
		deliberationRequestVo.setStatus(Status.SUBMIT);
		deliberationRequestVo.setUnionDraUnit(normativeFileVo.getUnionDrtUnit());
		deliberationRequestVo.setUnionDraUnitLeader(normativeFileVo.getUnionDrtUnitLeader());
		deliberationRequestVo.setDraftingInstruction(normativeFileVo.getDraftInstruction());
		
		//生成路径
		String projectPath = getProjectPath(request);
		filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DELIBERATION.toString());
		fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REVIEWINSTRUCTION+ ")" + SysUtil.EXTENSION_NAME;
		
		
		//读取到草案
		String  protocolPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
		String  protocolFilter =  SysUtil.STAGE_LEGAL_PROTOCOL;
		String protocolName = WordUtils.getFileNames(protocolPath,protocolFilter);
		deliberationRequestVo.setProtocol(protocolName);
		
		//读取征求意见的相关材料
		String requestCommentsPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
		String requestCommentsFilter = SysUtil.STAGE_LEGAL_ADOPTCOMMENT;
		String requestCommentsName = WordUtils.getFileNames(requestCommentsPath,requestCommentsFilter);
		deliberationRequestVo.setRequestComments(requestCommentsName);
		
		//读取到法律审查意见书
		String  reviewFilter = SysUtil.STAGE_LEGAL_REVCOMMENT;
		String 	reviewCommentsName = WordUtils.getFileNames(protocolPath,reviewFilter );
		deliberationRequestVo.setReviewComments(reviewCommentsName);

		DeliberationRequestVo  vo =  null;
		try {
			vo = deliberationRequestService.saveOrUpdate(deliberationRequestVo, filePath, fileName);
			result.put("success", true);
			result.put("msg", "保存成功！");
			result.put("vo", vo);
			
		} catch (ServiceException e) {
			log.error(e.getMessage(), e.getCause());
			result.put("success", false);
			result.put("msg", e.getMessage());
		}
		return result;
	}
	
	/**
	 * 删除审议报请
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@RequestMapping("deliberationRequest/delete")
	@ResponseBody
	public String delete(HttpServletRequest request, HttpServletResponse response,Long id) {
		
		String message= "";

		//获取路径
		String projectPath = getProjectPath(request);
			
		try {
			boolean flag = deliberationRequestService.delete(id, projectPath);
			if ( flag ) {
				return SysUtil.JSON_MSG_SUCCESS;
			} else {
				return SysUtil.JSON_MSG_FAIL;
			}
		} catch (ServiceException e) {
			message = "{\"message\":\"" + e.getMessage() + "\"}";
		}
		return message;
	}
	
	/**
	 * 查询审议报请
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping("deliberationRequest/find.do")
	@ResponseBody
	public Map<String, Object> find(String name, PageParam page) {
		
		Set<Long> orgIds = organizationService.findAuthOrgId();
		
		Page<DeliberationRequestVo> filePages = deliberationRequestService.find(page.getPage(),page.getRows(), name, orgIds);
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
	@RequestMapping("deliberationRequest/gainContent")
	public String gainContent(HttpServletRequest request, Long norId, String fileType,String fileName) throws ServiceException {
		
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		
		String content = null;
		String  filePath = null;
		
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			
			if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_PROTOCOL.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
				content = WordUtils.readFile(filePath + File.separator + fileName);
			
			}else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_INSTRUCTION.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
				content = WordUtils.readFile(filePath + File.separator + fileName);
			
			}else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_REVIEWCOMMENT.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
				content = WordUtils.readFile(filePath + File.separator + fileName);
			
			}else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_ADOPTCOMMENT.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
				content = WordUtils.readFile(filePath + File.separator + fileName);
			}
			
			if (!fileName.contains(SysUtil.SEMICOLON)) {
				content = WordUtils.readFile(filePath + File.separator + fileName);	//获取文档内容
			} else {
				content = "";
			}
		}
		return content;
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
	@RequestMapping("deliberationRequest/print")
	public String print(HttpServletRequest request, Long norId) throws ServiceException {
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DELIBERATION.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REVIEWINSTRUCTION + ")"+SysUtil.EXTENSION_NAME;
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
	@RequestMapping("deliberationRequest/export")
	public String export(HttpServletRequest request,HttpServletResponse response, Long norId) {
		boolean downloadFlag = false;
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		if ( normativeFileVo != null) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DELIBERATION.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REVIEWINSTRUCTION + ")"+SysUtil.EXTENSION_NAME;
			//下载说明文档内容
			downloadFlag = this.download(response, filePath + File.separator + fileName, fileName);	
		}
		if (downloadFlag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
	}
	
}
