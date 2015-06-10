package com.cdrundle.legaldoc.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;

/**
 * 意见征求--反馈意见
 * @author xiaokui.li
 *
 */
@Entity
@Table(name = "set_fb_comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FeedbackComment extends LongIdEntity
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
	@ManyToOne
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
	 * 反馈意见部门
	 */
	@ManyToOne
	@JoinColumn(name = "fb_unit", nullable = false)
	private Organization feedbackUnit;
	
	/**
	 * 反馈经办员
	 */
	@ManyToOne
	@JoinColumn(name = "fb_unit_clerk", nullable = false)
	private User feedbackUnitClerk;
	
	/**
	 * 最晚反馈时间
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "latest_fb_date", nullable = false)
	private Date latestFeedbackDate;
	
	/**
	 * 实际反馈时间
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "actual_fb_date", nullable = false)
	private Date actualFeedbackDate;
	
	/**
	 * 征求意见稿
	 */
	@Column(name = "req_draft", nullable = false)
	private String requestingDraft;
	
	/**
	 * 修改意见和建议
	 */
	@Column(name = "modify_opinion", nullable = false)
	private String modifyOpinions;
	
	/**
	 * 反馈单位负责人意见
	 */
	@Column(name = "leader_opinion", nullable = false)
	private String leaderOpinions;
	
	/**
	 * 备注
	 */
	@Column(name = "remark", nullable = false)
	private String remarks;

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

	public Organization getFeedbackUnit()
	{
		return feedbackUnit;
	}

	public void setFeedbackUnit(Organization feedbackUnit)
	{
		this.feedbackUnit = feedbackUnit;
	}

	public User getFeedbackUnitClerk()
	{
		return feedbackUnitClerk;
	}

	public void setFeedbackUnitClerk(User feedbackUnitClerk)
	{
		this.feedbackUnitClerk = feedbackUnitClerk;
	}

	public Date getLatestFeedbackDate()
	{
		return latestFeedbackDate;
	}

	public void setLatestFeedbackDate(Date latestFeedbackDate)
	{
		this.latestFeedbackDate = latestFeedbackDate;
	}

	public Date getActualFeedbackDate()
	{
		return actualFeedbackDate;
	}

	public void setActualFeedbackDate(Date actualFeedbackDate)
	{
		this.actualFeedbackDate = actualFeedbackDate;
	}

	public String getRequestingDraft()
	{
		return requestingDraft;
	}

	public void setRequestingDraft(String requestingDraft)
	{
		this.requestingDraft = requestingDraft;
	}

	public String getModifyOpinions()
	{
		return modifyOpinions;
	}

	public void setModifyOpinions(String modifyOpinions)
	{
		this.modifyOpinions = modifyOpinions;
	}

	public String getLeaderOpinions()
	{
		return leaderOpinions;
	}

	public void setLeaderOpinions(String leaderOpinions)
	{
		this.leaderOpinions = leaderOpinions;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}
	
}
