package com.cdrundle.legaldoc.entity;

import java.util.Date;

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
import com.cdrundle.legaldoc.enums.AssessResult;
import com.cdrundle.legaldoc.enums.Status;

/**
 * @author gang.li
 * 
 *         期满评估实体
 */
@Entity
@Table(name = "implmgt_def_assess")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DeferredAssessment extends LongIdEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@Column(name = "name", nullable = false)
	private String name;

	/**
	 * 规范性文件
	 */
	@OneToOne
	@JoinColumn(name = "nor_file", nullable = false)
	private NormativeFile normativeFile;

	/**
	 * 状态
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;

	/**
	 * 制定单位
	 */
	@ManyToOne
	@JoinColumn(name = "dec_unit", nullable = false)
	private Organization decisionUnit;

	/**
	 * 制定单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "dec_unit_leader")
	private User decisionUnitLeader;

	/**
	 * 制定单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "dec_unit_clerk")
	private User decisionUnitClerk;

	/**
	 * 主起草单位
	 */
	@ManyToOne
	@JoinColumn(name = "dra_unit", nullable = false)
	private Organization draftingUnit;

	/**
	 * 主起草单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "dra_unit_leader", nullable = false)
	private User draftingUnitLeader;

	/**
	 * 主起草单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "dra_unit_clerk", nullable = false)
	private User draftingUnitClerk;

	/**
	 * 评估结果
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "assess_result", nullable = false)
	private AssessResult assessResult;

	/**
	 * 有效日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "valid_date")
	private Date validDate;

	/**
	 * 评估日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "assess_date")
	private Date assessDate;

	/**
	 * 规范性文件
	 */
	@Column(name = "legal_doc", nullable = false)
	private String legalDoc;

	/**
	 * 起草说明
	 */
	@Column(name = "dra_instruction", nullable = false)
	private String draftingInstruction;

	/**
	 * 相关依据
	 */
	@Column(name = "legal_basis", nullable = false)
	private String legalBasis;

	/**
	 * 评估意见
	 */
	@Column(name = "assess_comment")
	private String assessComment;

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

	public Organization getDecisionUnit() {
		return decisionUnit;
	}

	public void setDecisionUnit(Organization decisionUnit) {
		this.decisionUnit = decisionUnit;
	}

	public User getDecisionUnitLeader() {
		return decisionUnitLeader;
	}

	public void setDecisionUnitLeader(User decisionUnitLeader) {
		this.decisionUnitLeader = decisionUnitLeader;
	}

	public User getDecisionUnitClerk() {
		return decisionUnitClerk;
	}

	public void setDecisionUnitClerk(User decisionUnitClerk) {
		this.decisionUnitClerk = decisionUnitClerk;
	}

	public Organization getDraftingUnit() {
		return draftingUnit;
	}

	public void setDraftingUnit(Organization draftingUnit) {
		this.draftingUnit = draftingUnit;
	}

	public User getDraftingUnitLeader() {
		return draftingUnitLeader;
	}

	public void setDraftingUnitLeader(User draftingUnitLeader) {
		this.draftingUnitLeader = draftingUnitLeader;
	}

	public User getDraftingUnitClerk() {
		return draftingUnitClerk;
	}

	public void setDraftingUnitClerk(User draftingUnitClerk) {
		this.draftingUnitClerk = draftingUnitClerk;
	}

	public AssessResult getAssessResult() {
		return assessResult;
	}

	public void setAssessResult(AssessResult assessResult) {
		this.assessResult = assessResult;
	}

	public Date getValidDate() {
		return validDate;
	}

	public void setValidDate(Date validDate) {
		this.validDate = validDate;
	}

	public Date getAssessDate() {
		return assessDate;
	}

	public void setAssessDate(Date assessDate) {
		this.assessDate = assessDate;
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

	public String getAssessComment() {
		return assessComment;
	}

	public void setAssessComment(String assessComment) {
		this.assessComment = assessComment;
	}

}
