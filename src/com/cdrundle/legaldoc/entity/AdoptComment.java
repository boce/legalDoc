package com.cdrundle.legaldoc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;

/**
 * 意见征求--反馈意见处理情况
 * @author xiaokui.li
 *
 */
@Entity
@Table(name = "set_adopt_comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AdoptComment  extends LongIdEntity
{

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
	private User draftingUnitLeader;
	
	/**
	 * 主起草单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "dra_unit_clerk", nullable = false)
	private User draftingUnitClerk;
	
	/**
	 * 反馈意见
	 */
	@Column(name = "fb_comments", nullable = false)
	private String feedbackComment;

	/**
	 * 反馈意见处理情况
	 */
	@Column(name = "feedback_process")
	private String feedbackProcess;

	/**
	 * 是否需要修改征求意见稿
	 */
	@Column(name = "is_need_modify", nullable = false)
	private Boolean isNeedModify;
	
	public String getFeedbackProcess() {
		return feedbackProcess;
	}

	public void setFeedbackProcess(String feedbackProcess) {
		this.feedbackProcess = feedbackProcess;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public NormativeFile getNormativeFile()
	{
		return normativeFile;
	}

	public void setNormativeFile(NormativeFile normativeFile)
	{
		this.normativeFile = normativeFile;
	}

	public Organization getDraftingUnit()
	{
		return draftingUnit;
	}

	public void setDraftingUnit(Organization draftingUnit)
	{
		this.draftingUnit = draftingUnit;
	}

	public User getDraftingUnitLeader()
	{
		return draftingUnitLeader;
	}

	public void setDraftingUnitLeader(User draftingUnitLeader)
	{
		this.draftingUnitLeader = draftingUnitLeader;
	}

	public User getDraftingUnitClerk()
	{
		return draftingUnitClerk;
	}

	public void setDraftingUnitClerk(User draftingUnitClerk)
	{
		this.draftingUnitClerk = draftingUnitClerk;
	}

	public String getFeedbackComment()
	{
		return feedbackComment;
	}

	public void setFeedbackComment(String feedbackComment)
	{
		this.feedbackComment = feedbackComment;
	}

	public Boolean getIsNeedModify() {
		return isNeedModify;
	}

	public void setIsNeedModify(Boolean isNeedModify) {
		this.isNeedModify = isNeedModify;
	}

}
