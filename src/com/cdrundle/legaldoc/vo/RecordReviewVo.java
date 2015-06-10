package com.cdrundle.legaldoc.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cdrundle.legaldoc.entity.RecordReview;
import com.cdrundle.legaldoc.enums.ReviewResult;
import com.cdrundle.legaldoc.enums.Status;

/**
 * @author XuBao
 * 
 *         2014年6月11日
 */
public class RecordReviewVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private NormativeFileVo normativeFile;
	private Status status;
	private OrgShortVo decisionMakingUnit;
	private UserShortVo decisionMakingUnitLeader;
	private UserShortVo decisionMakingUnitClerk;
	private OrgShortVo recordRevUnit;
	private UserShortVo recordRevUnitLeader;
	private UserShortVo recordRevUnitClerk;
	private Date registerDate;
	private OrgShortVo recordUnit;
	private Date recordReviewDate;
	private ReviewResult reviewResult;
	private String registerCode;
	private Boolean decUnitOop;
	private Boolean decProcedureOop;
	private Boolean contentOop;
	private Boolean decTechHasDefects;
	private Boolean others;
	private String reviewOpinionPaper;
	private String legalDoc;
	private String draftingInstruction;
	private String legalBasis;
	private String recordReport;
	private Date recordDate;

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
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

	public OrgShortVo getRecordRevUnit() {
		return recordRevUnit;
	}

	public void setRecordRevUnit(OrgShortVo recordRevUnit) {
		this.recordRevUnit = recordRevUnit;
	}

	public UserShortVo getRecordRevUnitLeader() {
		return recordRevUnitLeader;
	}

	public void setRecordRevUnitLeader(UserShortVo recordRevUnitLeader) {
		this.recordRevUnitLeader = recordRevUnitLeader;
	}

	public UserShortVo getRecordRevUnitClerk() {
		return recordRevUnitClerk;
	}

	public void setRecordRevUnitClerk(UserShortVo recordRevUnitClerk) {
		this.recordRevUnitClerk = recordRevUnitClerk;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public OrgShortVo getRecordUnit() {
		return recordUnit;
	}

	public void setRecordUnit(OrgShortVo recordUnit) {
		this.recordUnit = recordUnit;
	}

	public Date getRecordReviewDate() {
		return recordReviewDate;
	}

	public void setRecordReviewDate(Date recordReviewDate) {
		this.recordReviewDate = recordReviewDate;
	}

	public ReviewResult getReviewResult() {
		return reviewResult;
	}

	public void setReviewResult(ReviewResult reviewResult) {
		this.reviewResult = reviewResult;
	}

	public String getRegisterCode() {
		return registerCode;
	}

	public void setRegisterCode(String registerCode) {
		this.registerCode = registerCode;
	}

	public Boolean getDecUnitOop() {
		return decUnitOop;
	}

	public void setDecUnitOop(Boolean decUnitOop) {
		this.decUnitOop = decUnitOop;
	}

	public Boolean getDecProcedureOop() {
		return decProcedureOop;
	}

	public void setDecProcedureOop(Boolean decProcedureOop) {
		this.decProcedureOop = decProcedureOop;
	}

	public Boolean getContentOop() {
		return contentOop;
	}

	public void setContentOop(Boolean contentOop) {
		this.contentOop = contentOop;
	}

	public Boolean getDecTechHasDefects() {
		return decTechHasDefects;
	}

	public void setDecTechHasDefects(Boolean decTechHasDefects) {
		this.decTechHasDefects = decTechHasDefects;
	}

	public Boolean getOthers() {
		return others;
	}

	public void setOthers(Boolean others) {
		this.others = others;
	}

	public String getReviewOpinionPaper() {
		return reviewOpinionPaper;
	}

	public void setReviewOpinionPaper(String reviewOpinionPaper) {
		this.reviewOpinionPaper = reviewOpinionPaper;
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

	public static RecordReviewVo createVo(RecordReview recordReview) {// 将实体对象转换为Vo对象
		RecordReviewVo recordReviewVo = new RecordReviewVo();
		
		if( recordReview == null){
			return recordReviewVo;
		}
		
		recordReviewVo.setContentOop(recordReview.getContentOop());
		recordReviewVo.setDecisionMakingUnit(OrgShortVo.createVo(recordReview.getDecisionMakingUnit()));
		recordReviewVo.setDecisionMakingUnitClerk(UserShortVo.createVo(recordReview.getDecisionMakingUnitClerk()));
		recordReviewVo.setDecisionMakingUnitLeader(UserShortVo.createVo(recordReview.getDecisionMakingUnitLeader()));
		recordReviewVo.setDecProcedureOop(recordReview.getDecProcedureOop());
		recordReviewVo.setDecTechHasDefects(recordReview.getDecTechHasDefects());
		recordReviewVo.setDecUnitOop(recordReview.getDecUnitOop());
		recordReviewVo.setDraftingInstruction(recordReview.getDraftingInstruction());
		recordReviewVo.setId(recordReview.getId());
		recordReviewVo.setLegalBasis(recordReview.getLegalBasis());
		recordReviewVo.setLegalDoc(recordReview.getLegalDoc());
		recordReviewVo.setName(recordReview.getName());
		recordReviewVo.setNormativeFile(NormativeFileVo.createVo(recordReview.getNormativeFile()));
		recordReviewVo.setOthers(recordReview.getOthers());
		recordReviewVo.setRecordReport(recordReview.getRecordReport());
		recordReviewVo.setRecordReviewDate(recordReview.getRecordReviewDate());
		recordReviewVo.setRegisterCode(recordReview.getRegisterCode());
		recordReviewVo.setRecordRevUnit(OrgShortVo.createVo(recordReview.getRecordRevUnit()));
		recordReviewVo.setRecordRevUnitClerk(UserShortVo.createVo(recordReview.getRecordRevUnitClerk()));
		recordReviewVo.setRecordRevUnitLeader(UserShortVo.createVo(recordReview.getRecordRevUnitLeader()));
		recordReviewVo.setRecordUnit(OrgShortVo.createVo(recordReview.getRecordUnit()));
		recordReviewVo.setRegisterDate(recordReview.getRegisterDate());
		recordReviewVo.setReviewOpinionPaper(recordReview.getReviewOpinionPaper());
		recordReviewVo.setReviewResult(recordReview.getReviewResult());
		recordReviewVo.setStatus(recordReview.getStatus());
		return recordReviewVo;

	}

	public static List<RecordReviewVo> createVoList(
			List<RecordReview> recordReviewList) {// 将实体集合转换为Vo集合
		List<RecordReviewVo> recordReviewVoList = new ArrayList<RecordReviewVo>();
		for (RecordReview recordReview : recordReviewList) {// 将SetDelRequestList集合里面的SetDelRequest一个一个的遍历出来
			recordReviewVoList.add(RecordReviewVo.createVo(recordReview));// 调用上面的createVo方法，将遍历来的SetDelRequest转换为SetDelRequestVo对象，并添加到SetDelRequestVoList
		}
		return recordReviewVoList;
	}

	
}
