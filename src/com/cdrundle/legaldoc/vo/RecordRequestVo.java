package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cdrundle.legaldoc.entity.RecordRequest;
import com.cdrundle.legaldoc.enums.Status;

/**
 * @author XuBao
 * 
 *         2014年6月10日
 */
public class RecordRequestVo{

	private Long id;
	private String name;
	private NormativeFileVo normativeFile;
	private Status status;
	private OrgShortVo decisionMakingUnit;
	private UserShortVo decisionMakingUnitLeader;
	private UserShortVo decisionMakingUnitClerk;
	private OrgShortVo draftingUnit;
	private UserShortVo draftingUnitLeader;
	private UserShortVo draftingUnitClerk;
	private OrgShortVo recordUnit;
	private UserShortVo recordUnitLeader;
	private UserShortVo recordUnitClerk;
	private Date recordRequestDate;
	private String phone;
	private String legalDoc;
	private String draftingInstruction;
	private String legalBasis;
	private String recordReport;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public NormativeFileVo getNormativeFile() {
		return normativeFile;
	}

	public void setNormativeFile(NormativeFileVo normativeFile) {
		this.normativeFile = normativeFile;
	}

	public OrgShortVo getDecisionMakingUnit() {
		return decisionMakingUnit;
	}

	public void setDecisionMakingUnit(OrgShortVo decisionMakingUnit) {
		this.decisionMakingUnit = decisionMakingUnit;
	}

	public UserShortVo getDecisionMakingUnitLeader() {
		return decisionMakingUnitLeader;
	}

	public void setDecisionMakingUnitLeader(UserShortVo decisionMakingUnitLeader) {
		this.decisionMakingUnitLeader = decisionMakingUnitLeader;
	}

	public UserShortVo getDecisionMakingUnitClerk() {
		return decisionMakingUnitClerk;
	}

	public void setDecisionMakingUnitClerk(UserShortVo decisionMakingUnitClerk) {
		this.decisionMakingUnitClerk = decisionMakingUnitClerk;
	}

	public OrgShortVo getRecordUnit() {
		return recordUnit;
	}

	public void setRecordUnit(OrgShortVo recordUnit) {
		this.recordUnit = recordUnit;
	}

	public UserShortVo getRecordUnitLeader() {
		return recordUnitLeader;
	}

	public void setRecordUnitLeader(UserShortVo recordUnitLeader) {
		this.recordUnitLeader = recordUnitLeader;
	}

	public UserShortVo getRecordUnitClerk() {
		return recordUnitClerk;
	}

	public void setRecordUnitClerk(UserShortVo recordUnitClerk) {
		this.recordUnitClerk = recordUnitClerk;
	}

	public Date getRecordRequestDate() {
		return recordRequestDate;
	}

	public void setRecordRequestDate(Date recordRequestDate) {
		this.recordRequestDate = recordRequestDate;
	}

	public String getLegalDoc() {
		return legalDoc;
	}

	public void setLegalDoc(String legalDoc) {
		this.legalDoc = legalDoc;
	}

	public String getDraftingInstruction() {
		return draftingInstruction;
	}

	public void setDraftingInstruction(String draftingInstruction) {
		this.draftingInstruction = draftingInstruction;
	}

	public String getLegalBasis() {
		return legalBasis;
	}

	public void setLegalBasis(String legalBasis) {
		this.legalBasis = legalBasis;
	}

	public String getRecordReport() {
		return recordReport;
	}

	public void setRecordReport(String recordReport) {
		this.recordReport = recordReport;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public OrgShortVo getDraftingUnit() {
		return draftingUnit;
	}

	public void setDraftingUnit(OrgShortVo draftingUnit) {
		this.draftingUnit = draftingUnit;
	}

	public UserShortVo getDraftingUnitLeader() {
		return draftingUnitLeader;
	}

	public void setDraftingUnitLeader(UserShortVo draftingUnitLeader) {
		this.draftingUnitLeader = draftingUnitLeader;
	}

	public UserShortVo getDraftingUnitClerk() {
		return draftingUnitClerk;
	}

	public void setDraftingUnitClerk(UserShortVo draftingUnitClerk) {
		this.draftingUnitClerk = draftingUnitClerk;
	}

	public static RecordRequestVo createVo(RecordRequest recordRequest) {// 将实体对象转换为Vo对象
		RecordRequestVo recordRequestVo = new RecordRequestVo();

		if (recordRequest == null) {
			return recordRequestVo;
		}

		recordRequestVo.setDecisionMakingUnit(OrgShortVo.createVo(recordRequest.getDecisionMakingUnit()));
		recordRequestVo.setDecisionMakingUnitClerk(UserShortVo.createVo(recordRequest.getDecisionMakingUnitClerk()));
		recordRequestVo.setDecisionMakingUnitLeader(UserShortVo.createVo(recordRequest.getDecisionMakingUnitLeader()));
		recordRequestVo.setDraftingInstruction(recordRequest.getDraftingInstruction());
		recordRequestVo.setId(recordRequest.getId());
		recordRequestVo.setLegalBasis(recordRequest.getLegalBasis());
		recordRequestVo.setLegalDoc(recordRequest.getLegalDoc());
		recordRequestVo.setName(recordRequest.getName());
		recordRequestVo.setNormativeFile(NormativeFileVo.createVo(recordRequest.getNormativeFile()));
		recordRequestVo.setRecordReport(recordRequest.getRecordReport());
		recordRequestVo.setRecordRequestDate(recordRequest.getRecordRequestDate());
		recordRequestVo.setRecordUnit(OrgShortVo.createVo(recordRequest.getRecordUnit()));
		recordRequestVo.setRecordUnitClerk(UserShortVo.createVo(recordRequest.getRecordUnitClerk()));
		recordRequestVo.setRecordUnitLeader(UserShortVo.createVo(recordRequest.getRecordUnitLeader()));
		recordRequestVo.setStatus(recordRequest.getStatus());
		recordRequestVo.setPhone(recordRequest.getPhone());
		recordRequestVo.setDraftingUnit(OrgShortVo.createVo(recordRequest.getDraftingUnit()));
		recordRequestVo.setDraftingUnitClerk(UserShortVo.createVo(recordRequest.getDraftingUnitClerk()));
		recordRequestVo.setDraftingUnitLeader(UserShortVo.createVo(recordRequest.getDraftingUnitLeader()));
		return recordRequestVo;

	}

	public static List<RecordRequestVo> createVoList(List<RecordRequest> recordRequestList) {// 将实体集合转换为Vo集合
		List<RecordRequestVo> recordRequestVoList = new ArrayList<RecordRequestVo>();
		for (RecordRequest recordRequest : recordRequestList) {// 将SetDelRequestList集合里面的SetDelRequest一个一个的遍历出来
			recordRequestVoList.add(RecordRequestVo.createVo(recordRequest));// 调用上面的createVo方法，将遍历来的SetDelRequest转换为SetDelRequestVo对象，并添加到SetDelRequestVoList
		}
		return recordRequestVoList;
	}

}
