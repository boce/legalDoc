package com.cdrundle.legaldoc.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
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
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.service.IRecordRequestService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.OrganizationVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.RecordRequestVo;
import com.cdrundle.legaldoc.webservice.IRecordSend;
import com.cdrundle.legaldoc.webservice.pojo.Attachment;
import com.cdrundle.security.WebPlatformUser;

/**
 * @author XuBao
 * 
 *         2014年7月9日
 */
@Controller
public class RecordRequestController extends BaseController {
	private final Log log = LogFactory.getLog(getClass());
	@Autowired
	private IRecordRequestService recordRequestService;
	@Autowired
	private INormativeFileService normativeFileService;
	@Autowired
	private IOrganizationService organizationService;

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
	@RequestMapping("recordRequest/load")
	@ResponseBody
	public Map<String, Object> load(HttpServletRequest request, HttpServletResponse response, Long id, String sreachType) {

		Map<String, Object> result = new HashMap<String, Object>();
		// 得到当前登录用户信息
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long orgId = Long.parseLong(userDetail.getOrgId());

		if (SysUtil.SEARCH_TYPE_NORFILE.equals(sreachType) && id != null) {

			RecordRequestVo recordRequestVo = recordRequestService.findByNorId(id);
			NormativeFileVo normativeFileVo = normativeFileService.findById(id);
			recordRequestVo.setNormativeFile(normativeFileVo);

			// String projectPath =
			// getProjectPath(request);

			// 读取到起草说明
			String draftingInstructionName = normativeFileVo.getDraftInstruction();
			recordRequestVo.setDraftingInstruction(draftingInstructionName);

			// 读取规范性文件
			String legalDocName = normativeFileVo.getLegalDoc();
			recordRequestVo.setLegalDoc(legalDocName);

			// 读取相关依据
			String legalBasisName = normativeFileVo.getLegalBasis();
			recordRequestVo.setLegalBasis(legalBasisName);

			//查询登录人员对应单位的审核单位
			OrganizationVo org = organizationService.findById(orgId);
			recordRequestVo.setRecordUnit(org.getRecordUnit());
			
			result.put("vo", recordRequestVo);
			result.put("modifiable", true);

			return result;
		} else if (SysUtil.SEARCH_TYPE_RREQ.equals(sreachType) && id != null) {
			RecordRequestVo recordRequestVo = recordRequestService.findById(id);
			Stage stage = recordRequestVo.getNormativeFile().getStage();
			if (!stage.equals(Stage.RECORD_REQUEST)) {
				result.put("modifiable", false);
			} else {
				if (recordRequestVo.getDraftingUnit().getId().equals(orgId) || recordRequestVo.getDecisionMakingUnit().getId().equals(orgId)) {
					result.put("modifiable", true);
				} else {
					result.put("modifiable", false);
				}
			}
			result.put("vo", recordRequestVo);
			return result;
		}
		return null;
	}

	/**
	 * 保存备案报送
	 * 
	 * @param request
	 * @param response
	 * @param recordRequestVo
	 * @param norId
	 * @param name
	 * @param requestUint
	 * @param requestDate
	 * @return
	 */
	@RequestMapping("recordRequest/save")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request, HttpServletResponse response, RecordRequestVo recordRequestVo, String recordReport) {
		Map<String, Object> result = new HashMap<>();
		String filePath = null;
		String fileName = null;
		NormativeFileVo normativeFileVo = normativeFileService.findById(recordRequestVo.getNormativeFile().getId());

		recordRequestVo.setName(normativeFileVo.getName());
		recordRequestVo.setDecisionMakingUnit(normativeFileVo.getDecUnit());
		recordRequestVo.setDecisionMakingUnitClerk(normativeFileVo.getDecUnitClerk());
		recordRequestVo.setDecisionMakingUnitLeader(normativeFileVo.getDecUnitLeader());
		recordRequestVo.setDraftingUnit(normativeFileVo.getDrtUnit());
		recordRequestVo.setDraftingUnitClerk(normativeFileVo.getDrtUnitClerk());
		recordRequestVo.setDraftingUnitLeader(normativeFileVo.getDrtUnitClerk());
		recordRequestVo.setDraftingInstruction(normativeFileVo.getDraftInstruction());
		recordRequestVo.setLegalBasis(normativeFileVo.getLegalBasis());
		recordRequestVo.setLegalDoc(normativeFileVo.getLegalDoc());
		recordRequestVo.setStatus(Status.OPEN);

		// 生成路径
		String projectPath = getProjectPath(request);
		filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.RECORD.toString());
		fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_RECORDREQUEST + ")" + SysUtil.EXTENSION_NAME;
		recordRequestVo.setRecordReport(recordReport);

		RecordRequestVo vo = null;

		try {
			vo = recordRequestService.saveOrUpdate(recordRequestVo, filePath, fileName);
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
	 * 删除备案报送
	 * 
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@RequestMapping("recordRequest/delete")
	@ResponseBody
	public String delete(HttpServletRequest request, HttpServletResponse response, Long id) {

		String message = "";

		// 获取路径
		String projectPath = getProjectPath(request);

		try {
			boolean flag = recordRequestService.delete(id, projectPath);
			if (flag) {
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
	 * 查询备案报送
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping("recordRequest/find")
	@ResponseBody
	public Map<String, Object> find(String name, PageParam page) {
		Page<RecordRequestVo> filePages = null;
		Set<Long> orgIds = organizationService.findAuthOrgId();
		filePages = recordRequestService.find(page.getPage(), page.getRows(), name, orgIds);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", filePages.getTotalElements());
		result.put("rows", filePages.getContent());

		return result;
	}

	/**
	 * 获取文件内容
	 * 
	 * @param request
	 * @param norId
	 * @param fileName
	 * @return
	 * @throws ServiceException 
	 */
	@ResponseBody
	@RequestMapping("recordRequest/gainContent")
	public String gainContent(HttpServletRequest request, Long norId, String fileType, String fileName) throws ServiceException {

		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);

		String content = null;
		String filePath = null;

		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);

			if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_LEGALBASIS.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.SETUP.toString());
				return WordUtils.readWord(filePath + File.separator + fileName);

			} else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_LEGALDOC.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.PUBLISH.toString());
				content = WordUtils.readFile(filePath + File.separator + fileName);

			} else if (!StringUtils.isEmpty(fileType) && SysUtil.FILE_TYPE_INSTRUCTION.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
				content = WordUtils.readFile(filePath + File.separator + fileName);

			}

			if (!fileName.contains(SysUtil.SEMICOLON)) {
				content = WordUtils.readFile(filePath + File.separator + fileName); // 获取文档内容
			} else {
				content = "";
			}
		}
		return content;
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
	@RequestMapping("recordRequest/print")
	public String print(HttpServletRequest request, Long norId) throws ServiceException {
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.RECORD.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_RECORDREQUEST + ")" + SysUtil.EXTENSION_NAME;
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
	@RequestMapping("recordRequest/export")
	public String export(HttpServletRequest request, HttpServletResponse response, Long norId) {
		boolean downloadFlag = false;
		NormativeFileVo normativeFileVo = normativeFileService.findById(norId);
		if (normativeFileVo != null) {
			String projectPath = getProjectPath(request);
			String filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.RECORD.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_RECORDREQUEST + ")" + SysUtil.EXTENSION_NAME;
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
	 * 报送
	 * 
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 * @throws ServiceException 
	 */
	@ResponseBody
	@RequestMapping("/recordRequest/send")
	public String send(HttpServletRequest request, Long id) throws ServiceException {
		RecordRequestVo recordRequestVo = recordRequestService.findById(id);
		OrganizationVo organizationVo = organizationService.findById(recordRequestVo.getDecisionMakingUnit().getId());
		String webserviceUrl = organizationVo.getWebserviceUrl();
		if(StringUtils.isEmpty(webserviceUrl)){
			throw new ServiceException("备案审查单位服务地址未填写");
		}
		long startTime = System.currentTimeMillis();
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("mtom-enabled", Boolean.TRUE);
		ClientProxyFactoryBean pf = new ClientProxyFactoryBean();
		pf.setProperties(props);
		pf.setAddress(webserviceUrl);
		pf.getInInterceptors().add(new LoggingInInterceptor());
		pf.getOutInterceptors().add(new LoggingOutInterceptor());
		IRecordSend client = pf.create(IRecordSend.class);
		Attachment atta = new Attachment();
		String projectPath = getProjectPath(request);
		//规范性文件
		String legalDocFilePath = WordUtils.getFilePath(projectPath, recordRequestVo.getNormativeFile(), Stage.PUBLISH.toString());
		legalDocFilePath +=  File.separator + recordRequestVo.getNormativeFile().getLegalDoc();
		DataSource legalDocSource = new FileDataSource(new File(legalDocFilePath));
		atta.setLegalDoc(new DataHandler(legalDocSource));
		//相关依据
		String legalBasisFilePath = WordUtils.getFilePath(projectPath, recordRequestVo.getNormativeFile(), Stage.SETUP.toString());
		String legalBasises = recordRequestVo.getLegalBasis();
		if(StringUtils.isNotEmpty(legalBasises)){
			String[] legalBasisesArr = legalBasises.split(";");
			List<DataHandler> legalBasis = new ArrayList<>();
			for (int i = 0; i < legalBasisesArr.length; i++) {
				DataSource legalBasisSource = new FileDataSource(new File(legalBasisFilePath + File.separator + legalBasisesArr[i]));
				legalBasis.add(new DataHandler(legalBasisSource));
			}
			atta.setLegalBasis(legalBasis);
		}
		//备案报告
		String recordReportFilePath = WordUtils.getFilePath(projectPath, recordRequestVo.getNormativeFile(), Stage.RECORD.toString());
		recordReportFilePath += File.separator + WordUtils.getFileNames(recordReportFilePath, SysUtil.STAGE_LEGAL_RECORDREQUEST);
		DataSource recordReportSource = new FileDataSource(new File(recordReportFilePath));
		atta.setRecordReport(new DataHandler(recordReportSource));
		//起草说明
		String instructionFilePath = WordUtils.getFilePath(projectPath, recordRequestVo.getNormativeFile(), Stage.LEGAL_REVIEW.toString());
		instructionFilePath += File.separator + recordRequestVo.getNormativeFile().getDraftInstruction();
		DataSource instructionSource = new FileDataSource(new File(instructionFilePath));
		atta.setDraftingInstruction(new DataHandler(instructionSource));
		
		boolean success = true;
		try {
			success = client.saveRecord(recordRequestVo, atta);
		} catch (UnsupportedEncodingException e) {
			success = false;
		}catch (Exception e) {
			if(e.getCause() instanceof FileNotFoundException){
				throw new ServiceException("报送附件不存在！");
			}
			success = false;
		}
		long endTime = System.currentTimeMillis();
		log.info("报送耗时：" + (endTime - startTime) + "ms");
		return "{\"success\":" + success + "}";
	}
}
