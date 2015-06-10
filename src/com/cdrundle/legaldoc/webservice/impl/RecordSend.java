package com.cdrundle.legaldoc.webservice.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.springframework.beans.factory.annotation.Autowired;

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IRecordRequestDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.RecordRequest;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.RecordRequestVo;
import com.cdrundle.legaldoc.webservice.IRecordSend;
import com.cdrundle.legaldoc.webservice.pojo.Attachment;

@WebService(endpointInterface = "com.cdrundle.legaldoc.webservice.IRecordSend")
public class RecordSend implements IRecordSend {

	private final Log log = LogFactory.getLog(getClass());
	
	private static final int BUFFER_SIZE = 100000;
	
	@Resource  
    private WebServiceContext context; 
	@Autowired
	IRecordRequestDao recordRequestDao;
	@Autowired
	IOrganizationDao organizationDao;
	@Autowired
	IUserDao userDao;
	@Autowired
	INormativeFileDao normativeFileDao;
	
	@Override
	public boolean saveRecord(RecordRequestVo recordRequestVo, Attachment atta) throws UnsupportedEncodingException {
		String name = recordRequestVo.getName();
		RecordRequest recordRequest = recordRequestDao.findRecordRequestByName(name);
		RecordRequest savedRecordRequest;
		if (recordRequest != null) {
			Organization recordUnit = organizationDao.findOne(recordRequestVo.getRecordUnit().getId());
			User recordUnitClerk = userDao.findOne(recordRequestVo.getRecordUnitClerk().getId());
			User recordUnitLeader = userDao.findOne(recordRequestVo.getRecordUnitLeader().getId());
			NormativeFile normativeFile = recordRequest.getNormativeFile();
			normativeFile.setRecRevUnit(recordUnit);
			normativeFile.setRecRevUnitClerk(recordUnitClerk);
			normativeFile.setRecRevUnitLeader(recordUnitLeader);
			normativeFileDao.save(normativeFile);
			recordRequest.setRecordUnit(recordUnit);
			recordRequest.setRecordUnitClerk(recordUnitClerk);
			recordRequest.setRecordUnitLeader(recordUnitLeader);
			recordRequest.setPhone(recordRequestVo.getPhone());
			recordRequest.setRecordRequestDate(recordRequestVo.getRecordRequestDate());
			savedRecordRequest = recordRequestDao.save(recordRequest);
		} else {
			NormativeFile norFile = convertToNormativeFile(recordRequestVo.getNormativeFile());
			NormativeFile savedNorFile = normativeFileDao.save(norFile);
			RecordRequest rec = converToRecordRequest(recordRequestVo);
			rec.setNormativeFile(savedNorFile);
			savedRecordRequest = recordRequestDao.save(rec);
		}
		// 生成路径
		HttpServletRequest request = (HttpServletRequest)context.getMessageContext().get(AbstractHTTPDestination.HTTP_REQUEST);
		String projectPath = request.getServletContext().getRealPath("/");
		//备案报告
		String recordReportFilePath = WordUtils.getFilePath(projectPath, recordRequestVo.getNormativeFile(), Stage.RECORD.toString());
		recordReportFilePath += File.separator + recordRequestVo.getName() + "(" + SysUtil.STAGE_LEGAL_RECORDREQUEST + ")" + SysUtil.EXTENSION_NAME;
		DataHandler recordReportHandler = atta.getRecordReport();
		writeFile(recordReportHandler, recordReportFilePath);
		//规范性文件
		DataHandler legalDocHandler = atta.getLegalDoc();
		String legalDocfilePath = WordUtils.getFilePath(projectPath, recordRequestVo.getNormativeFile(), Stage.PUBLISH.toString());
		legalDocfilePath +=  File.separator + recordRequestVo.getNormativeFile().getLegalDoc();
		writeFile(legalDocHandler, legalDocfilePath);
		//起草说明
		DataHandler draftingInstructionHandler = atta.getDraftingInstruction();
		String instructionFilePath = WordUtils.getFilePath(projectPath, recordRequestVo.getNormativeFile(), Stage.LEGAL_REVIEW.toString());
		instructionFilePath += File.separator + recordRequestVo.getNormativeFile().getDraftInstruction();
		writeFile(draftingInstructionHandler, instructionFilePath);
		//相关依据
		List<DataHandler> legalBasisHandlers = atta.getLegalBasis();
		if(legalBasisHandlers != null && !legalBasisHandlers.isEmpty()){
			String legalBasisFilePath = WordUtils.getFilePath(projectPath, recordRequestVo.getNormativeFile(), Stage.SETUP.toString());
			for (Iterator<DataHandler> iterator = legalBasisHandlers.iterator(); iterator.hasNext();) {
				DataHandler legalBasisHandler = iterator.next();
				String legalBasisFileName = new String(legalBasisHandler.getName().getBytes("ISO8859-1"), "UTF-8");
				writeFile(legalBasisHandler, legalBasisFilePath + File.separator + legalBasisFileName);
			}
		}
		return savedRecordRequest == null ? false : true;
	}

	/**
	 * 往目标路径写入文件
	 * @param handler
	 * @param outPath
	 */
	private boolean writeFile(DataHandler handler, String outPath){
		boolean success = true;
		try(InputStream is = handler.getInputStream();
			OutputStream os = new FileOutputStream(new File(outPath));) {
			byte[] b = new byte[BUFFER_SIZE];  
	        int bytesRead = 0;  
	        while ((bytesRead = is.read(b)) != -1) {  
	            os.write(b, 0, bytesRead);  
	        }  
	        os.flush();  
		} catch (IOException e) {
			success = false;
			log.error(e);
		}
		return success;
	}
	
	private NormativeFile convertToNormativeFile(NormativeFileVo normativeFileVo) {
		NormativeFile norFile = new NormativeFile();
		norFile.setName(normativeFileVo.getName());
		norFile.setApplyUnit(organizationDao.findOne(normativeFileVo.getApplyUnit().getId()));
		norFile.setDecUnit(organizationDao.findOne(normativeFileVo.getDecUnit().getId()));
		norFile.setDecUnitLeader(userDao.findOne(normativeFileVo.getDecUnitLeader().getId()));
		norFile.setDecUnitClerk(userDao.findOne(normativeFileVo.getDecUnitClerk().getId()));
		norFile.setDrtUnit(organizationDao.findOne(normativeFileVo.getDrtUnit().getId()));
		norFile.setDrtUnitLeader(userDao.findOne(normativeFileVo.getDrtUnitLeader().getId()));
		norFile.setDrtUnitClerk(userDao.findOne(normativeFileVo.getDrtUnitClerk().getId()));
		norFile.setUnionDrtUnit(normativeFileVo.getUnionDrtUnit());
		norFile.setUnionDrtUnitLeader(normativeFileVo.getUnionDrtUnitLeader());
		norFile.setUnionDrtUnitClerk(normativeFileVo.getUnionDrtUnitClerk());
		norFile.setRevUnit(organizationDao.findOne(normativeFileVo.getRevUnit().getId()));
		norFile.setRevUnitLeader(userDao.findOne(normativeFileVo.getRevUnitLeader().getId()));
		norFile.setRevUnitClerk(userDao.findOne(normativeFileVo.getRevUnitClerk().getId()));
		norFile.setInvolvedOrges(normativeFileVo.getInvolvedOrges());
		norFile.setDelUnit(normativeFileVo.getDelUnit());
		norFile.setApplyDate(normativeFileVo.getApplyDate());
		norFile.setDraftDate(normativeFileVo.getDraftDate());
		norFile.setRequestDate(normativeFileVo.getRequestDate());
		norFile.setDelDate(normativeFileVo.getDelDate());
		norFile.setPublishDate(normativeFileVo.getPublishDate());
		norFile.setDocNo(normativeFileVo.getDocNo());
		norFile.setInvalidDate(normativeFileVo.getInvalidDate());
		norFile.setValidDate(normativeFileVo.getValidDate());
		norFile.setPriority(normativeFileVo.getPriority());
		norFile.setStatus(normativeFileVo.getStatus());
		norFile.setStage(normativeFileVo.getStage());
		norFile.setPublishNo(normativeFileVo.getPublishNo());
		norFile.setLegalDoc(normativeFileVo.getLegalDoc());
		norFile.setDraftInstruction(normativeFileVo.getDraftInstruction());
		norFile.setLegalBasis(normativeFileVo.getLegalBasis());
		norFile.setMoreFiles(normativeFileVo.getMoreFiles());
		return norFile;
	}

	private RecordRequest converToRecordRequest(RecordRequestVo recordRequestVo) {// 将Vo对象转换为实体对象
		RecordRequest recordRequest = new RecordRequest();
		recordRequest.setName(recordRequestVo.getName());
		recordRequest.setDecisionMakingUnit(organizationDao.findOne(recordRequestVo.getDecisionMakingUnit().getId()));
		recordRequest.setDecisionMakingUnitClerk(userDao.findOne(recordRequestVo.getDecisionMakingUnitClerk().getId()));
		recordRequest.setDecisionMakingUnitLeader(userDao.findOne(recordRequestVo.getDecisionMakingUnitLeader().getId()));
		recordRequest.setDraftingInstruction(recordRequestVo.getDraftingInstruction());
		recordRequest.setLegalBasis(recordRequestVo.getLegalBasis());
		recordRequest.setLegalDoc(recordRequestVo.getLegalDoc());
		recordRequest.setRecordReport(recordRequestVo.getRecordReport());
		recordRequest.setRecordRequestDate(recordRequestVo.getRecordRequestDate());
		recordRequest.setRecordUnit(organizationDao.findOne(recordRequestVo.getRecordUnit().getId()));
		recordRequest.setRecordUnitClerk(userDao.findOne(recordRequestVo.getRecordUnitClerk().getId()));
		recordRequest.setRecordUnitLeader(userDao.findOne(recordRequestVo.getRecordUnitLeader().getId()));
		recordRequest.setStatus(recordRequestVo.getStatus());
		recordRequest.setPhone(recordRequestVo.getPhone());
		recordRequest.setDraftingUnit(organizationDao.findOne(recordRequestVo.getDraftingUnit().getId()));
		recordRequest.setDraftingUnitClerk(userDao.findOne(recordRequestVo.getDraftingUnitClerk().getId()));
		recordRequest.setDraftingUnitLeader(userDao.findOne(recordRequestVo.getDraftingUnitLeader().getId()));
		return recordRequest;
	}
}
