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
import com.cdrundle.legaldoc.service.IProtocolDeliberationService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.ProtocolDeliberationVo;
import com.cdrundle.security.WebPlatformUser;

/**
 * @author  XuBao
 *
 * 2014年7月4日
 */
@Controller
public class ProtocolDeliberationController extends  BaseController{
	private final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private IProtocolDeliberationService   protocolDeliberationService;
	@Autowired
	private INormativeFileService  normativeFileService;
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
	@RequestMapping("protocolDeliberation/load")
	@ResponseBody
	public Map<String, Object> load(HttpServletRequest request, HttpServletResponse response, Long id,String sreachType) {
		
		Map<String, Object>  result = new HashMap<String, Object>();
		// 得到当前登录用户信息
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long orgId = Long.parseLong(userDetail.getOrgId());
		
		if( SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) &&  id != null){
			ProtocolDeliberationVo protocolDeliberationVo  = protocolDeliberationService.findByNorId(id);
			
			NormativeFileVo  normativeFileVo  =   normativeFileService.findById(id);
			protocolDeliberationVo.setNormativeFile(normativeFileVo);
			String projectPath = getProjectPath(request);
			
			
			
			//读取到草案
			String  protocolPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			String protocolName = WordUtils.getFileNames(protocolPath, SysUtil.STAGE_LEGAL_PROTOCOL);
			protocolDeliberationVo.setProtocol(protocolName);

			//读取到法律审查意见书
			String  reviewCommentsPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			String 	reviewCommentsName = WordUtils.getFileNames(reviewCommentsPath,SysUtil.STAGE_LEGAL_REVCOMMENT );
			protocolDeliberationVo.setReviewComments(reviewCommentsName);
			
			//读取到起草说明
			String  draftingInstructionName = normativeFileVo.getDraftInstruction();
			protocolDeliberationVo.setDraftingInstruction(draftingInstructionName);;
			
			//读取征求意见的相关材料
			String requestCommentsPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String requestCommentsName = WordUtils.getFileNames(requestCommentsPath,SysUtil.STAGE_LEGAL_ADOPTCOMMENT);
			protocolDeliberationVo.setRequestComments(requestCommentsName);
			
			//读取审议请示
			String reviewInstructionPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DELIBERATION.toString());
			String reviewInstructionName = WordUtils.getFileNames(reviewInstructionPath,SysUtil.STAGE_LEGAL_REVIEWINSTRUCTION);
			protocolDeliberationVo.setReviewInstruction(reviewInstructionName);

			result.put("vo", protocolDeliberationVo);
			result.put("modifiable", true);
			
			  return result;
		} else if (SysUtil.SEARCH_TYPE_PDEL.equals(sreachType) && id != null){
			ProtocolDeliberationVo protocolDeliberationVo = protocolDeliberationService.findById(id);
			Stage stage = protocolDeliberationVo.getNormativeFile().getStage();
			if(stage.equals(Stage.DELIBERATION_MODIFY) && !protocolDeliberationVo.getIsNeedModify()){
				result.put("modifiable", true);
			}else if(!stage.equals(Stage.DELIBERATION_PROTOCOL)){
				result.put("modifiable", false);
			}else{
				if(orgId.equals(protocolDeliberationVo.getDraftingUnit().getId()) ){
					result.put("modifiable", true);
				}else{
					result.put("modifiable", false);
				}
			}
			result.put("vo", protocolDeliberationVo);
			 return result ;
		}
		return null;
	}
	
	/**
	 * 保存草案审议
	 * @param request
	 * @param response
	 * @param protocolDeliberationVo
	 * @return
	 */
	@RequestMapping("protocolDeliberation/save")
	@ResponseBody
public Map<String, Object> save(HttpServletRequest request, HttpServletResponse response,ProtocolDeliberationVo protocolDeliberationVo) {
		Map<String, Object> result = new HashMap<>();
		String fileName = null;
		
		NormativeFileVo normativeFileVo = normativeFileService.findById(protocolDeliberationVo.getNormativeFile().getId());
		
		protocolDeliberationVo.setName(normativeFileVo.getName());
		protocolDeliberationVo.setDraftingUnit(normativeFileVo.getDrtUnit());
		protocolDeliberationVo.setDraftingUnitClerk(normativeFileVo.getDrtUnitClerk());
		protocolDeliberationVo.setDraftingUnitLeader(normativeFileVo.getDrtUnitLeader());
		protocolDeliberationVo.setDraftingInstruction(normativeFileVo.getDraftInstruction());
		
		//生成路径
		String projectPath = getProjectPath(request);
		fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_DELIBERATIONCOMMENT+ ")" + SysUtil.EXTENSION_NAME;
		
		//读取到审议请示
		String  reviewInstructionPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DELIBERATION.toString());
		String  reviewInstructionFilter = SysUtil.STAGE_LEGAL_REVIEWINSTRUCTION;
		String reviewInstructionName = WordUtils.getFileNames(reviewInstructionPath,reviewInstructionFilter);
		protocolDeliberationVo.setReviewInstruction(reviewInstructionName);
		
		//读取到草案
		String  protocolPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
		String  protocolFilter = SysUtil.STAGE_LEGAL_PROTOCOL;
		String protocolName = WordUtils.getFileNames(protocolPath,protocolFilter);
		protocolDeliberationVo.setProtocol(protocolName);
		
		//读取征求意见的相关材料
		String requestCommentsPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
		String requestCommentsFilter = SysUtil.STAGE_LEGAL_ADOPTCOMMENT;
		String requestCommentsName = WordUtils.getFileNames(requestCommentsPath,requestCommentsFilter);
		protocolDeliberationVo.setRequestComments(requestCommentsName);;
		
		//读取到法律审查意见书
		String  reviewFilter = SysUtil.STAGE_LEGAL_REVCOMMENT;
		String 	reviewCommentsName = WordUtils.getFileNames(protocolPath,reviewFilter );
		protocolDeliberationVo.setReviewComments(reviewCommentsName);
		
		ProtocolDeliberationVo vo = null;
		try {
			vo = protocolDeliberationService.saveOrUpdate(protocolDeliberationVo, projectPath, fileName);
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
	 * 删除草案审议
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@RequestMapping("protocolDeliberation/delete")
	@ResponseBody
	public String delete(HttpServletRequest request, HttpServletResponse response,Long id) {

		String message = "";
		//获取路径
		String projectPath = getProjectPath(request);
	
			
		try {
			boolean flag = protocolDeliberationService.delete(id, projectPath);
			if ( flag ) {
				return SysUtil.JSON_MSG_SUCCESS;
			} else {
				return SysUtil.JSON_MSG_FAIL;
			}
		} catch (ServiceException e) {
			message  = "{\"message\":\"" +e.getMessage()+"\"}";
		}
		return message;
	}
	
	/**
	 * 查询草案审议
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping("protocolDeliberation/find.do")
	@ResponseBody
	public Map<String, Object> find(String name, PageParam page) {
		Page<ProtocolDeliberationVo> filePages = null;
		
		Set<Long> orgIds = organizationService.findAuthOrgId();
		filePages = protocolDeliberationService.find(page.getPage(), page.getRows(), name, orgIds);
		
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
	@RequestMapping("protocolDeliberation/gainContent")
	public String gainContent(HttpServletRequest request, Long norId,String fileType,String fileName) throws ServiceException {
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		
		String content = null;
		String filePath = null;
		
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
			}else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_REVIEWINSTRUCTION.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DELIBERATION.toString());
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
	@RequestMapping("protocolDeliberation/print")
	public String print(HttpServletRequest request, Long norId) throws ServiceException {
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DELIBERATION.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_DELIBERATIONCOMMENT+ ")"+SysUtil.EXTENSION_NAME;
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
	@RequestMapping("protocolDeliberation/export")
	public String export(HttpServletRequest request,HttpServletResponse response, Long norId) {
		boolean downloadFlag = false;
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		if ( normativeFileVo != null) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.DELIBERATION.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_DELIBERATIONCOMMENT + ")"+SysUtil.EXTENSION_NAME;
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
