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
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.enums.StatusEnumEditor;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.service.IRecordReviewService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.RecordReviewVo;
import com.cdrundle.security.WebPlatformUser;

/**
 * @author  XuBao
 *
 * 2014年7月9日
 */
@Controller
public class RecordReviewController extends  BaseController{
	private final Log log = LogFactory.getLog(getClass());
	@Autowired
	private  IRecordReviewService  recordReviewService;
	@Autowired
	private  INormativeFileService normativeFileService;
	@Autowired
	private IOrganizationService organizationService;
	
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
	@RequestMapping("recordReview/load")
	@ResponseBody
	public Map<String, Object> load(HttpServletRequest request, HttpServletResponse response, Long id,String sreachType) {
		
		Map<String, Object>  result = new HashMap<String, Object>();
		// 得到当前登录用户信息
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long orgId = Long.parseLong(userDetail.getOrgId());
		
		if( SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) &&  id != null){
			
			RecordReviewVo recordReviewVo  = recordReviewService.findByNorId(id);
			NormativeFileVo  normativeFileVo  =   normativeFileService.findById(id);
			recordReviewVo.setNormativeFile(normativeFileVo);
			
			String projectPath = getProjectPath(request);
			
			String rc= recordReviewService.gainRegisterCode();
			recordReviewVo.setRegisterCode(rc);
			
			//读取到起草说明
			String  draftingInstructionName = normativeFileVo.getDraftInstruction();
			recordReviewVo.setDraftingInstruction(draftingInstructionName);
			
			//读取规范性文件
			String  legalDocName = normativeFileVo.getLegalDoc();
			recordReviewVo.setLegalDoc(legalDocName);
			 
			//读取相关依据
			String  legalBasisName = normativeFileVo.getLegalBasis();
			recordReviewVo.setLegalBasis(legalBasisName);
			
			//读取备案报告
			String recordReportPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.RECORD.toString());
			String recordReportName = WordUtils.getFileNames(recordReportPath, SysUtil.STAGE_LEGAL_RECORDREQUEST);
			recordReviewVo.setRecordReport(recordReportName);
			
			result.put("vo", recordReviewVo);
			result.put("modifiable", true);
			
			return result;
		} else if (SysUtil.SEARCH_TYPE_RREW.equals(sreachType) && id != null){
			RecordReviewVo recordReviewVo = recordReviewService.findById(id);
			Stage stage = recordReviewVo.getNormativeFile().getStage();
			if(!stage.equals(Stage.RECORD_REVIEW)){
				result.put("modifiable", false);
			}else{
				if( recordReviewVo.getDecisionMakingUnit().getId().equals(orgId) || recordReviewVo.getRecordRevUnit().getId().equals(orgId)){
					result.put("modifiable", true);
				}else{
					result.put("modifiable", false);
				}
			}
			
			result.put("vo", recordReviewVo);
			return result;
		}
		return null;
		
	}
	
	/**
	 * 保存备案审查
	 * @param request
	 * @param response
	 * @param recordReviewVo
	 * @param norId
	 * @param name
	 * @param requestUint
	 * @param requestDate
	 * @return
	 */
	@RequestMapping("recordReview/save")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request, HttpServletResponse response,RecordReviewVo recordReviewVo) {
		Map<String, Object> result = new HashMap<>();
		String filePath = null;
		String fileName = null;
		NormativeFileVo normativeFileVo = normativeFileService.findById(recordReviewVo.getNormativeFile().getId());
		
		recordReviewVo.setName(normativeFileVo.getName());
		recordReviewVo.setDecisionMakingUnit(normativeFileVo.getDecUnit());
		recordReviewVo.setDecisionMakingUnitClerk(normativeFileVo.getDecUnitClerk());
		recordReviewVo.setDecisionMakingUnitLeader(normativeFileVo.getDecUnitLeader());
		recordReviewVo.setRecordRevUnit(normativeFileVo.getRecRevUnit());
		recordReviewVo.setRecordRevUnitClerk(normativeFileVo.getRevUnitClerk());
		recordReviewVo.setRecordRevUnitLeader(normativeFileVo.getRevUnitLeader());
		recordReviewVo.setDraftingInstruction(normativeFileVo.getDraftInstruction());
		recordReviewVo.setLegalBasis(normativeFileVo.getLegalBasis());
		recordReviewVo.setLegalDoc(normativeFileVo.getLegalDoc());
		recordReviewVo.setStatus(Status.OPEN);
		
		//生成路径
		String projectPath = getProjectPath(request);
		filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.RECORD.toString());
		fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REVIEWOPINIONPAPER+ ")" + SysUtil.EXTENSION_NAME;
		
		//读取备案报告
		String recordReportPath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.RECORD.toString());
		String recordReportFilter = SysUtil.STAGE_LEGAL_RECORDREQUEST;
		String recordReportName = WordUtils.getFileNames(recordReportPath,recordReportFilter);
		recordReviewVo.setRecordReport(recordReportName);
		
		RecordReviewVo vo =null;
		
		try {
			vo = recordReviewService.saveOrUpdate(recordReviewVo, filePath, fileName);
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
	 * 备案登记
	 * @param request
	 * @param response
	 * @param recordReviewVo
	 * @param norId
	 * @param name
	 * @param requestUint
	 * @param requestDate
	 * @return
	 */
	@RequestMapping("recordReview/register")
	@ResponseBody
	public RecordReviewVo register(HttpServletRequest request, HttpServletResponse response,RecordReviewVo recordReviewVo) {
		
		NormativeFileVo normativeFileVo = normativeFileService.findById(recordReviewVo.getNormativeFile().getId());
		
		recordReviewVo.setName(normativeFileVo.getName());
		recordReviewVo.setStatus(Status.OPEN);
		
		RecordReviewVo vo =null;
		
		try {
			return recordReviewService.register(recordReviewVo);

		} catch (ServiceException e) {
			return vo;
		}
	}
	
	/**
	 * 报备
	 * @param request
	 * @param response
	 * @param recordReviewVo
	 * @return
	 */
	@RequestMapping("recordReview/send")
	@ResponseBody
	public RecordReviewVo send(HttpServletRequest request, HttpServletResponse response,RecordReviewVo recordReviewVo) {
		
		
		NormativeFileVo normativeFileVo = normativeFileService.findById(recordReviewVo.getNormativeFile().getId());
		
		recordReviewVo.setName(normativeFileVo.getName());
		recordReviewVo.setStatus(Status.OPEN);
		
		RecordReviewVo vo =null;
		
		try {
			return recordReviewService.send(recordReviewVo);

		} catch (ServiceException e) {
			return vo;
		}
	}
	
	/**
	 * 删除备案审查
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@RequestMapping("recordReview/delete")
	@ResponseBody
	public String delete(HttpServletRequest request, HttpServletResponse response,Long id) {
		String message = "";

		//获取路径
		String projectPath = getProjectPath(request);
			
		try {
			boolean flag = recordReviewService.delete(id, projectPath);
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
	 * 查询备案审查
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping("recordReview/find")
	@ResponseBody
	public Map<String, Object> find(String name, PageParam page) {
		Page<RecordReviewVo> filePages = null;
	   Set<Long> orgIds = organizationService.findAuthOrgId();
	   filePages = recordReviewService.find(page.getPage(), page.getRows(), name, orgIds);
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
	@RequestMapping("recordReview/gainContent")
	public String gainContent(HttpServletRequest request, Long norId, String fileType,String fileName) throws ServiceException {
		
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		
		String content = null;
		String  filePath = null;
		
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			
			if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_LEGALBASIS.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.SETUP.toString());
				return WordUtils.readWord(filePath + File.separator + fileName);
			
			}else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_LEGALDOC.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.PUBLISH.toString());
				content = WordUtils.readFile(filePath + File.separator + fileName);
			
			}else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_INSTRUCTION.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
				content = WordUtils.readFile(filePath + File.separator + fileName);
			
			}else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_RECORDREQUEST.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.RECORD.toString());
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
	@RequestMapping("recordReview/print")
	public String print(HttpServletRequest request, Long norId) throws ServiceException {
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.RECORD.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REVIEWOPINIONPAPER+ ")"+SysUtil.EXTENSION_NAME;
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
	@RequestMapping("recordReview/export")
	public String export(HttpServletRequest request,HttpServletResponse response, Long norId) {
		boolean downloadFlag = false;
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		if ( normativeFileVo != null) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.RECORD.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REVIEWOPINIONPAPER + ")"+SysUtil.EXTENSION_NAME;
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
