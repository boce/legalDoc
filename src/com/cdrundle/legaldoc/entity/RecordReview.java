package com.cdrundle.legaldoc.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;
import com.cdrundle.legaldoc.enums.ReviewResult;
import com.cdrundle.legaldoc.enums.Status;

/**
 * @author XuBao 
 * 备案审查 
 * 2014年6月11日
 */
@Entity
@Table(name = "record_rec_review")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RecordReview extends LongIdEntity {

	/**
			 * 
			 */
	private static final long serialVersionUID = 1L;

	/**
	 * 文件名
	 */
	@Column(length = 200,nullable = false)
	private String name;

	/**
	 * 规范性文件
	 */
	@OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name = "nor_file")
	private NormativeFile normativeFile;

	/**
	 * 状态
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.OPEN;

	/**
	 * 制定单位
	 */
	@ManyToOne
	@JoinColumn(name = "dec_unit",nullable = false)
	private Organization decisionMakingUnit;

	/**
	 * 制定单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "dec_unit_leader",nullable = false)
	private User decisionMakingUnitLeader;

	/**
	 * 制定单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "dec_unit_clerk",nullable = false)
	private User decisionMakingUnitClerk;

	/**
	 * 备案审查单位
	 */
	@ManyToOne
	@JoinColumn(name = "rec_rev_unit",nullable = false)
	private Organization recordRevUnit;

	/**
	 * 备案审查单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "rec_rev_unit_leader",nullable = false)
	private User recordRevUnitLeader;

	/**
	 * 备案审查单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "rec_rev_unit_clerk",nullable = false)
	private User recordRevUnitClerk;
	
	/**
	 * 备案单位
	 */
	@ManyToOne
	@JoinColumn(name = "record_unit",nullable = false)
	private Organization recordUnit;

	/**
	 * 审查日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "rec_rev_date",nullable = false)
	private Date recordReviewDate;
	
	/**
	 * 备案日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "record_date",nullable = false)
	private Date recordDate;

	/**
	 * 备案登记日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "register_date",nullable = false)
	private Date registerDate;
	
	/**
	 * 审查结果
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "review_result",nullable = false)
	private ReviewResult reviewResult = ReviewResult.QUALIFIED ;

	/**
	 * 备案号
	 */
	@Column(name = "register_code",nullable = false,length = 50)
	private String registerCode;

	/**
	 * 制定主体不合规
	 */
	@Column(name = "dec_unit_oop")
	private Boolean decUnitOop;

	/**
	 * 制定程序不合规
	 */
	@Column(name = "dec_procedure_oop")
	private Boolean decProcedureOop;

	/**
	 * 文件内容不合法
	 */
	@Column(name = "content_oop")
	private Boolean contentOop;

	/**
	 * 制定技术有缺陷
	 */
	@Column(name = "dec_tech_has_defects")
	private Boolean decTechHasDefects;

	/**
	 * 其他
	 */
	@Column(name = "others")
	private Boolean others;

	/**
	 * 备案审查意见书
	 */
	@Column(name = "rev_opinion_paper")
	private String reviewOpinionPaper;

	/**
	 * 规范性文件物理路径
	 */
	@Column(name = "legal_doc",nullable = false)
	private String legalDoc;

	/**
	 * 起草说明
	 */
	@Column(name = "dra_instruction",nullable = false)
	private String draftingInstruction;

	/**
	 * 相关依据
	 */
	@Column(name = "legal_basis",nullable = false)
	private String legalBasis;

	/**
	 * 备案报告
	 */
	@Column(name = "record_report",nullable = false)
	private String recordReport;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NormativeFile getNormativeFile() {
		return normativeFile;
	}

	public void setNormativeFile(NormativeFile normativeFile) {
		this.normativeFile = normativeFile;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Organization getDecisionMakingUnit() {
		return decisionMakingUnit;
	}

	public void setDecisionMakingUnit(Organization decisionMakingUnit) {
		this.decisionMakingUnit = decisionMakingUnit;
	}

	public User getDecisionMakingUnitLeader() {
		return decisionMakingUnitLeader;
	}

	public void setDecisionMakingUnitLeader(User decisionMakingUnitLeader) {
		this.decisionMakingUnitLeader = decisionMakingUnitLeader;
	}

	public User getDecisionMakingUnitClerk() {
		return decisionMakingUnitClerk;
	}

	public void setDecisionMakingUnitClerk(User decisionMakingUnitClerk) {
		this.decisionMakingUnitClerk = decisionMakingUnitClerk;
	}

	public Organization getRecordRevUnit()
	{
		return recordRevUnit;
	}

	public void setRecordRevUnit(Organization recordRevUnit)
	{
		this.recordRevUnit = recordRevUnit;
	}

	public User getRecordRevUnitLeader()
	{
		return recordRevUnitLeader;
	}

	public void setRecordRevUnitLeader(User recordRevUnitLeader)
	{
		this.recordRevUnitLeader = recordRevUnitLeader;
	}

	public User getRecordRevUnitClerk()
	{
		return recordRevUnitClerk;
	}

	public void setRecordRevUnitClerk(User recordRevUnitClerk)
	{
		this.recordRevUnitClerk = recordRevUnitClerk;
	}

	public Organization getRecordUnit()
	{
		return recordUnit;
	}

	public void setRecordUnit(Organization recordUnit)
	{
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

	public Date getRecordDate()
	{
		return recordDate;
	}

	public void setRecordDate(Date recordDate)
	{
		this.recordDate = recordDate;
	}

	public Date getRegisterDate()
	{
		return registerDate;
	}

	public void setRegisterDate(Date registerDate)
	{
		this.registerDate = registerDate;
	}
	
	
}
