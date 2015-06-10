package com.cdrundle.legaldoc.entity;

import java.util.Date;

import javax.persistence.CascadeType;
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
 * 意见征求--征求意见
 * @author xiaokui.li
 *
 */
@Entity
@Table(name = "set_req_comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RequestComment extends LongIdEntity
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
	@ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
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
	 * 被征求意见部门
	 */
	@ManyToOne
	@JoinColumn(name = "req_from_unit", nullable = false)
	private Organization requestFromUnit;
	
	/**
	 * 最晚反馈时间
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "latest_feedback_date", nullable = false)
	private Date latestFeedbackDate;

	/**
	 * 征求意见稿
	 */
	@Column(name = "req_draft", nullable = false)
	private String requestingDraft;
	
	/**
	 * 征求意见函
	 */
	@Column(name = "content", nullable = false)
	private String content;

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

	public Organization getRequestFromUnit()
	{
		return requestFromUnit;
	}

	public void setRequestFromUnit(Organization requestFromUnit)
	{
		this.requestFromUnit = requestFromUnit;
	}

	public Date getLatestFeedbackDate()
	{
		return latestFeedbackDate;
	}

	public void setLatestFeedbackDate(Date latestFeedbackDate)
	{
		this.latestFeedbackDate = latestFeedbackDate;
	}

	public String getRequestingDraft()
	{
		return requestingDraft;
	}

	public void setRequestingDraft(String requestingDraft)
	{
		this.requestingDraft = requestingDraft;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}
	
}
