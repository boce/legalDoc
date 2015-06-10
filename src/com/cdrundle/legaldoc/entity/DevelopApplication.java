package com.cdrundle.legaldoc.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;
import com.cdrundle.legaldoc.enums.Priority;
import com.cdrundle.legaldoc.enums.Status;

/**
 * 立项
 * 
 * @author xiaokui.li
 * 
 */
@Entity
@Table(name = "set_devapp")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DevelopApplication extends LongIdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * 规范性文件
	 */
	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "nor_file", nullable = false)
	private NormativeFile normativeFile;

	/**
	 * 申报单位
	 */
	@ManyToOne
	@JoinColumn(name = "app_unit", nullable = false)
	private Organization applyOrg;

	/**
	 * 申报单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "app_unit_leader", nullable = false)
	private User applyLeader;

	/**
	 * 申报单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "app_unit_clerk", nullable = false)
	private User applyClerk;

	/**
	 * 批准申请领导
	 */
	@ManyToOne
	@JoinColumn(name = "approval_leader", nullable = false)
	private User approvalLeader;

	/**
	 * 拟起草时间
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "plan_draft_date")
	private Date planDraftDate;

	/**
	 * 拟送审时间
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "plan_review_date")
	private Date planReviewDate;

	/**
	 * 申报时间
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "app_date", nullable = false)
	private Date applyDate;

	/**
	 * 有效期
	 */
	@Column(name = "valid_date", nullable = false)
	private int validDate;

	/**
	 * 优先级
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Priority priority = Priority.NORMAL;

	/**
	 * 状态
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.OPEN;

	/**
	 * 依据最早失效日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "basis_invalid_date")
	private Date basisInvalidDate;

	/**
	 * 制定依据附件
	 */
	@Column(name = "legal_basis_atta", nullable = false)
	private String legalBasisAttachment;

	/**
	 * 制定的必要性、合法性，以及社会稳定性风险评估
	 */
	@Column(name = "nec_and_fea", nullable = false)
	private String necessityLegalAndRisk;

	/**
	 * 制定的必要性、合法性，以及社会稳定性风险评估附件
	 */
	@Column(name = "nec_and_fea_atta", nullable = false)
	private String necessityLegalAndRiskAttachment;

	/**
	 * 拟解决的主要问题
	 */
	@Column(name = "main_problem", nullable = false)
	private String mainProblem;

	/**
	 * 拟解决的主要问题附件
	 */
	@Column(name = "main_problem_atta", nullable = false)
	private String mainProblemAttachment;

	/**
	 * 拟确定的制度或措施，以及可行性论证
	 */
	@Column(name = "plan_reg_and_mea", nullable = false)
	private String planRegulationMeasureAndFeasibility;

	/**
	 * 拟确定的制度或措施，以及可行性论证附件
	 */
	@Column(name = "plan_reg_and_mea_atta", nullable = false)
	private String planRegulationMeasureAndFeasibilityAtta;

	/**
	 * 涉及的部门
	 */
	@Column(name = "involved_orges")
	private String involvedOrges;

	/**
	 * 申报单位负责人意见
	 */
	@Column(name = "app_leader_comm", nullable = false)
	private String applyLeaderComment;

	/**
	 * 批准申请领导意见
	 */
	@Column(name = "approval_leader_comm", nullable = false)
	private String approvalLeaderComment;

	/**
	 * 备注
	 */
	private String remarks;

	/**
	 * 制定依据详细信息
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "developApplication")
	private List<LegalBasis> legalBasises;

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

	public Organization getApplyOrg() {
		return applyOrg;
	}

	public void setApplyOrg(Organization applyOrg) {
		this.applyOrg = applyOrg;
	}

	public User getApplyLeader() {
		return applyLeader;
	}

	public void setApplyLeader(User applyLeader) {
		this.applyLeader = applyLeader;
	}

	public User getApplyClerk() {
		return applyClerk;
	}

	public void setApplyClerk(User applyClerk) {
		this.applyClerk = applyClerk;
	}

	public User getApprovalLeader() {
		return approvalLeader;
	}

	public void setApprovalLeader(User approvalLeader) {
		this.approvalLeader = approvalLeader;
	}

	public Date getPlanDraftDate() {
		return planDraftDate;
	}

	public void setPlanDraftDate(Date planDraftDate) {
		this.planDraftDate = planDraftDate;
	}

	public Date getPlanReviewDate() {
		return planReviewDate;
	}

	public void setPlanReviewDate(Date planReviewDate) {
		this.planReviewDate = planReviewDate;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public int getValidDate() {
		return validDate;
	}

	public void setValidDate(int validDate) {
		this.validDate = validDate;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getBasisInvalidDate() {
		return basisInvalidDate;
	}

	public void setBasisInvalidDate(Date basisInvalidDate) {
		this.basisInvalidDate = basisInvalidDate;
	}

	public String getLegalBasisAttachment() {
		return legalBasisAttachment;
	}

	public void setLegalBasisAttachment(String legalBasisAttachment) {
		this.legalBasisAttachment = legalBasisAttachment;
	}

	public String getNecessityLegalAndRisk() {
		return necessityLegalAndRisk;
	}

	public void setNecessityLegalAndRisk(String necessityLegalAndRisk) {
		this.necessityLegalAndRisk = necessityLegalAndRisk;
	}

	public String getNecessityLegalAndRiskAttachment() {
		return necessityLegalAndRiskAttachment;
	}

	public void setNecessityLegalAndRiskAttachment(String necessityLegalAndRiskAttachment) {
		this.necessityLegalAndRiskAttachment = necessityLegalAndRiskAttachment;
	}

	public String getMainProblem() {
		return mainProblem;
	}

	public void setMainProblem(String mainProblem) {
		this.mainProblem = mainProblem;
	}

	public String getMainProblemAttachment() {
		return mainProblemAttachment;
	}

	public void setMainProblemAttachment(String mainProblemAttachment) {
		this.mainProblemAttachment = mainProblemAttachment;
	}

	public String getPlanRegulationMeasureAndFeasibility() {
		return planRegulationMeasureAndFeasibility;
	}

	public void setPlanRegulationMeasureAndFeasibility(String planRegulationMeasureAndFeasibility) {
		this.planRegulationMeasureAndFeasibility = planRegulationMeasureAndFeasibility;
	}

	public String getPlanRegulationMeasureAndFeasibilityAtta() {
		return planRegulationMeasureAndFeasibilityAtta;
	}

	public void setPlanRegulationMeasureAndFeasibilityAtta(String planRegulationMeasureAndFeasibilityAtta) {
		this.planRegulationMeasureAndFeasibilityAtta = planRegulationMeasureAndFeasibilityAtta;
	}

	public String getInvolvedOrges() {
		return involvedOrges;
	}

	public void setInvolvedOrges(String involvedOrges) {
		this.involvedOrges = involvedOrges;
	}

	public String getApplyLeaderComment() {
		return applyLeaderComment;
	}

	public void setApplyLeaderComment(String applyLeaderComment) {
		this.applyLeaderComment = applyLeaderComment;
	}

	public String getApprovalLeaderComment() {
		return approvalLeaderComment;
	}

	public void setApprovalLeaderComment(String approvalLeaderComment) {
		this.approvalLeaderComment = approvalLeaderComment;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<LegalBasis> getLegalBasises() {
		return legalBasises;
	}

	public void setLegalBasises(List<LegalBasis> legalBasises) {
		this.legalBasises = legalBasises;
	}

}
