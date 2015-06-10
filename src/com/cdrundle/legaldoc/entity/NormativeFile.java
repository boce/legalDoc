package com.cdrundle.legaldoc.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;
import com.cdrundle.legaldoc.enums.FileStatus;
import com.cdrundle.legaldoc.enums.Priority;
import com.cdrundle.legaldoc.enums.Stage;

/**
 * @author XuBao 
 * 规范性文件 
 * 2014年6月11日
 */
@Entity
@Table(name = "common_nor_file")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NormativeFile extends LongIdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 文件名称
	 */
	@Column(nullable = false)
	private String name;
	
	/**
	 * 申请单位
	 */
	@ManyToOne	
	@JoinColumn(name = "apply_unit")
	private Organization applyUnit;
	
	/**
	 * 制定单位
	 */
	@ManyToOne
	@JoinColumn(name = "dec_unit")
	private Organization decUnit;

	/**
	 * 制定单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "dec_unit_leader")
	private User decUnitClerk;

	/**
	 * 制定单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "dec_unit_clerk")
	private User decUnitLeader;

	/**
	 * 主起草单位
	 */
	@ManyToOne
	@JoinColumn(name = "dra_unit")
	private Organization drtUnit;

	/**
	 * 主起草单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "dra_unit_clerk")
	private User drtUnitClerk;

	/**
	 * 主起草单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "dra_unit_leader")
	private User drtUnitLeader;

	/**
	 * 联合起草单位
	 */
	@Column(name = "union_dra_unit")
	private String  unionDrtUnit;

	/**
	 * 联合起草单位经办员
	 */
	@Column(name = "union_dra_unit_clerk")
	private String unionDrtUnitClerk;

	/**
	 * 联合起草单位负责人
	 */
	@Column(name = "union_dra_unit_leader")
	private String  unionDrtUnitLeader;

	/**
	 * 审查单位
	 */
	@ManyToOne
	@JoinColumn(name = "rev_unit")
	private Organization revUnit;

	/**
	 * 审查单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "rev_unit_clerk")
	private User revUnitClerk;

	/**
	 * 审查单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "rev_unit_leader")
	private User revUnitLeader;
	
	/**
	 * 涉及部门
	 */
	@Column(name = "involved_orges")
	private  String involvedOrges;

	/**
	 * 审议单位
	 */
	@Column(name = "del_unit")
	private String delUnit;

	/**
	 * 备案审查单位
	 */
	@ManyToOne
	@JoinColumn(name = "rec_rev_unit")
	private Organization recRevUnit;

	/**
	 * 备案审查单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "rec_rev_unit_clerk")
	private User recRevUnitClerk;

	/**
	 * 备案审查单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "rec_rev_unit_leader")
	private User recRevUnitLeader;

	/**
	 * 备案号
	 */
	@Column(name = "register_code")
	private String registerCode;

	/**
	 * 申请日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "apply_date")
	private Date applyDate;

	/**
	 * 起草日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "draft_date")
	private Date draftDate;

	/**
	 * 报请日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "request_date")
	private Date requestDate;

	/**
	 * 审议日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "del_date")
	private Date delDate;

	/**
	 * 发布日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "publish_date")
	private Date publishDate;
	
	/**
	 * 单据号
	 */
	@Column(name = "doc_no")
	private  String  docNo;
	
	/**
	 * 备案审查日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "review_date")
	private Date  reviewDate;
	/**
	 * 备案日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "register_date")
	private Date registerDate;

	/**
	 * 失效日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "invalid_date")
	private Date invalidDate;

	/**
	 * 依据最早失效日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "basis_invalid_date")
	private Date basisInvalidDate;

	/**
	 * 有效期
	 */
	@Column(name = "valid_date")
	private Integer  validDate;

	/**
	 * 优先级
	 */
	@Enumerated(EnumType.STRING)
	@Column
	private Priority priority = Priority.NORMAL;

	/**
	 * 状态
	 */
	@Enumerated(EnumType.STRING)
	@Column
	private FileStatus status = FileStatus.VALID;

	/**
	 * 阶段
	 */
	@Enumerated(EnumType.STRING)
	@Column
	private Stage stage = Stage.SETUP;

	/**
	 * 发文号
	 */
	@Column(name = "publish_no", length = 50)
	private String publishNo;

	/**
	 * 规范性文件
	 */
	@Column(name = "legal_doc", length = 200)
	private String legalDoc;

	/**
	 * 起草说明
	 */
	@Column(name = "draft_instruction", length = 200)
	private String draftInstruction;

	/**
	 * 相关依据
	 */
	@Column(name = "legal_basis", length = 200)
	private String legalBasis;

	/**
	 * 相关依据-不带附件
	 */
	@Column(name = "legal_basis_no_atta", length = 200)
	private String legalBasisNoAtta;
	
	/**
	 * 修订、撤销、废止原因
	 */
	@Column(name = "invalid_reason", length = 200)
	private String invalidReason;

	/**
	 * 更多文件
	 */
	@Column(name = "more_files", length = 200)
	private String moreFiles;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Organization getDecUnit() {
		return decUnit;
	}

	public void setDecUnit(Organization decUnit) {
		this.decUnit = decUnit;
	}

	public User getDecUnitClerk() {
		return decUnitClerk;
	}

	public void setDecUnitClerk(User decUnitClerk) {
		this.decUnitClerk = decUnitClerk;
	}

	public User getDecUnitLeader() {
		return decUnitLeader;
	}

	public void setDecUnitLeader(User decUnitLeader) {
		this.decUnitLeader = decUnitLeader;
	}

	public Organization getDrtUnit() {
		return drtUnit;
	}

	public void setDrtUnit(Organization drtUnit) {
		this.drtUnit = drtUnit;
	}

	public User getDrtUnitClerk() {
		return drtUnitClerk;
	}

	public void setDrtUnitClerk(User drtUnitClerk) {
		this.drtUnitClerk = drtUnitClerk;
	}

	public User getDrtUnitLeader() {
		return drtUnitLeader;
	}

	public void setDrtUnitLeader(User drtUnitLeader) {
		this.drtUnitLeader = drtUnitLeader;
	}

	

	public String getUnionDrtUnit()
	{
		return unionDrtUnit;
	}

	public void setUnionDrtUnit(String unionDrtUnit)
	{
		this.unionDrtUnit = unionDrtUnit;
	}

	public String getUnionDrtUnitClerk()
	{
		return unionDrtUnitClerk;
	}

	public void setUnionDrtUnitClerk(String unionDrtUnitClerk)
	{
		this.unionDrtUnitClerk = unionDrtUnitClerk;
	}

	public String getUnionDrtUnitLeader()
	{
		return unionDrtUnitLeader;
	}

	public void setUnionDrtUnitLeader(String unionDrtUnitLeader)
	{
		this.unionDrtUnitLeader = unionDrtUnitLeader;
	}

	public Organization getRevUnit() {
		return revUnit;
	}

	public void setRevUnit(Organization revUnit) {
		this.revUnit = revUnit;
	}

	public User getRevUnitClerk() {
		return revUnitClerk;
	}

	public void setRevUnitClerk(User revUnitClerk) {
		this.revUnitClerk = revUnitClerk;
	}

	public User getRevUnitLeader() {
		return revUnitLeader;
	}

	public void setRevUnitLeader(User revUnitLeader) {
		this.revUnitLeader = revUnitLeader;
	}

	

	public String getDelUnit()
	{
		return delUnit;
	}

	public void setDelUnit(String delUnit)
	{
		this.delUnit = delUnit;
	}

	public Organization getRecRevUnit() {
		return recRevUnit;
	}

	public void setRecRevUnit(Organization recRevUnit) {
		this.recRevUnit = recRevUnit;
	}

	public User getRecRevUnitClerk() {
		return recRevUnitClerk;
	}

	public void setRecRevUnitClerk(User recRevUnitClerk) {
		this.recRevUnitClerk = recRevUnitClerk;
	}

	public User getRecRevUnitLeader() {
		return recRevUnitLeader;
	}

	public void setRecRevUnitLeader(User recRevUnitLeader) {
		this.recRevUnitLeader = recRevUnitLeader;
	}

	public String getRegisterCode() {
		return registerCode;
	}

	public void setRegisterCode(String registerCode) {
		this.registerCode = registerCode;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public Date getDraftDate() {
		return draftDate;
	}

	public void setDraftDate(Date draftDate) {
		this.draftDate = draftDate;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Date getDelDate() {
		return delDate;
	}

	public void setDelDate(Date delDate) {
		this.delDate = delDate;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public Date getInvalidDate() {
		return invalidDate;
	}

	public void setInvalidDate(Date invalidDate) {
		this.invalidDate = invalidDate;
	}

	public Date getBasisInvalidDate() {
		return basisInvalidDate;
	}

	public void setBasisInvalidDate(Date basisInvalidDate) {
		this.basisInvalidDate = basisInvalidDate;
	}

	public Integer getValidDate()
	{
		return validDate;
	}

	public void setValidDate(Integer validDate)
	{
		this.validDate = validDate;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public FileStatus getStatus() {
		return status;
	}

	public void setStatus(FileStatus status) {
		this.status = status;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public String getPublishNo() {
		return publishNo;
	}

	public void setPublishNo(String publishNo) {
		this.publishNo = publishNo;
	}

	public String getLegalDoc() {
		return legalDoc;
	}

	public void setLegalDoc(String legalDoc) {
		this.legalDoc = legalDoc;
	}

	public String getDraftInstruction() {
		return draftInstruction;
	}

	public void setDraftInstruction(String draftInstruction) {
		this.draftInstruction = draftInstruction;
	}

	public String getLegalBasis() {
		return legalBasis;
	}

	public void setLegalBasis(String legalBasis) {
		this.legalBasis = legalBasis;
	}

	public String getLegalBasisNoAtta() {
		return legalBasisNoAtta;
	}

	public void setLegalBasisNoAtta(String legalBasisNoAtta) {
		this.legalBasisNoAtta = legalBasisNoAtta;
	}

	public String getInvalidReason() {
		return invalidReason;
	}

	public void setInvalidReason(String invalidReason) {
		this.invalidReason = invalidReason;
	}

	public String getMoreFiles() {
		return moreFiles;
	}

	public void setMoreFiles(String moreFiles) {
		this.moreFiles = moreFiles;
	}

	public Date getReviewDate()
	{
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate)
	{
		this.reviewDate = reviewDate;
	}

	public String getInvolvedOrges()
	{
		return involvedOrges;
	}

	public void setInvolvedOrges(String involvedOrges)
	{
		this.involvedOrges = involvedOrges;
	}

	public Organization getApplyUnit()
	{
		return applyUnit;
	}

	public void setApplyUnit(Organization applyUnit)
	{
		this.applyUnit = applyUnit;
	}

	public String getDocNo()
	{
		return docNo;
	}

	public void setDocNo(String docNo)
	{
		this.docNo = docNo;
	}
	
	
}
