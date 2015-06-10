
package com.cdrundle.legaldoc.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;

/**
 * @author  gang.li
 *
 * 送审稿修改实体
 */
@Entity
@Table(name = "set_exa_draft_modify")
@Cache(usage =  CacheConcurrencyStrategy.READ_WRITE)
public class ExaminationDraftModify  extends  LongIdEntity {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 名称
	 */
	@Column(name = "name", nullable = false)
	private  String  name;
	
	/**
	 * 规范性文件
	 */
	@OneToOne(cascade =  {CascadeType.MERGE}, fetch = FetchType.LAZY)
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
	 * 审查意见
	 */
	@Column(name = "rev_comment", nullable = false)
	private  String  reviewComment;
	
	/**
	 * 内容
	 */
	@Column(name = "content", nullable = false)
	private  String  content;

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

	public String getReviewComment() {
		return reviewComment;
	}

	public void setReviewComment(String reviewComment) {
		this.reviewComment = reviewComment;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
