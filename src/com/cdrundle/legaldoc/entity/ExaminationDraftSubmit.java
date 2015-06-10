
package com.cdrundle.legaldoc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;
import com.cdrundle.legaldoc.enums.Status;

/**
 * @author  gang.li
 *
 * 送审稿报送实体
 */
@Entity
@Table(name = "set_exa_draft_submit")
@Cache(usage =  CacheConcurrencyStrategy.READ_WRITE)
public class ExaminationDraftSubmit  extends  LongIdEntity {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 名称
	 */
	@Column(name = "name", nullable = false)
	private  String  name;
	
	/**
	 * 规范性文件
	 */
	@OneToOne
	@JoinColumn(name = "nor_file", nullable = false)
	private NormativeFile normativeFile; 
	
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
	private  User  draftingUnitLeader;
	
	/**
	 * 主起草单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "dra_unit_clerk", nullable = false)
	private  User  draftingUnitClerk;
	
	/**
	 * 联合起草单位
	 */
	@Column(name = "union_dra_unit")
	private String unionDraftingUnit;
	
	/**
	 * 联合起草单位负责人
	 */
	@Column(name = "union_dra_unit_leader")
	private String unionDraftingUnitLeader;
	
	/**
	 * 联合起草单位经办员
	 */
	@Column(name = "union_dra_unit_clerk")
	private String unionDraftingUnitClerk;

	/**
	 * 状态
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;
	
	
	/**
	 * 审查单位
	 */
	@ManyToOne
	@JoinColumn(name = "rev_unit", nullable = false)
	private Organization reviewUnit;
	
	/**
	 * 审查单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "rev_unit_leader", nullable = false)
	private User reviewUnitLeader;
	
	/**
	 * 审查单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "rev_unit_clerk", nullable = false)
	private User reviewUnitClerk;
	
	/**
	 * 送审稿
	 */
	@Column(name = "exa_draft", nullable = false)
	private String examinationDraft;
	
	/**
	 * 起草说明
	 */
	@Column(name = "dra_instruction", nullable = false)
	private String draftingInstruction;
	
	/**
	 * 相关依据
	 */
	@Column(name = "legal_basises")
	private String legalBasises;

	/**
	 * 相关依据-不带附件
	 */
	@Column(name = "legal_basises_no_atta")
	private String legalBasisesNoAtta;
	
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

	public String getUnionDraftingUnit() {
		return unionDraftingUnit;
	}

	public void setUnionDraftingUnit(String unionDraftingUnit) {
		this.unionDraftingUnit = unionDraftingUnit;
	}

	public String getUnionDraftingUnitLeader() {
		return unionDraftingUnitLeader;
	}

	public void setUnionDraftingUnitLeader(String unionDraftingUnitLeader) {
		this.unionDraftingUnitLeader = unionDraftingUnitLeader;
	}

	public String getUnionDraftingUnitClerk() {
		return unionDraftingUnitClerk;
	}

	public void setUnionDraftingUnitClerk(String unionDraftingUnitClerk) {
		this.unionDraftingUnitClerk = unionDraftingUnitClerk;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Organization getReviewUnit() {
		return reviewUnit;
	}

	public void setReviewUnit(Organization reviewUnit) {
		this.reviewUnit = reviewUnit;
	}

	public User getReviewUnitLeader() {
		return reviewUnitLeader;
	}

	public void setReviewUnitLeader(User reviewUnitLeader) {
		this.reviewUnitLeader = reviewUnitLeader;
	}

	public User getReviewUnitClerk() {
		return reviewUnitClerk;
	}

	public void setReviewUnitClerk(User reviewUnitClerk) {
		this.reviewUnitClerk = reviewUnitClerk;
	}

	public String getExaminationDraft() {
		return examinationDraft;
	}

	public void setExaminationDraft(String examinationDraft) {
		this.examinationDraft = examinationDraft;
	}

	public String getDraftingInstruction() {
		return draftingInstruction;
	}

	public void setDraftingInstruction(String draftingInstruction) {
		this.draftingInstruction = draftingInstruction;
	}

	public String getLegalBasises() {
		return legalBasises;
	}

	public void setLegalBasises(String legalBasises) {
		this.legalBasises = legalBasises;
	}

	public String getLegalBasisesNoAtta() {
		return legalBasisesNoAtta;
	}

	public void setLegalBasisesNoAtta(String legalBasisesNoAtta) {
		this.legalBasisesNoAtta = legalBasisesNoAtta;
	}
	
}
