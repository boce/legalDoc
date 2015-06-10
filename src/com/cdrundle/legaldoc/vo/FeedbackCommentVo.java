package com.cdrundle.legaldoc.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.FeedbackComment;

/**
 * 意见征求--反馈意见
 * @author xiaokui.li
 *
 */
public class FeedbackCommentVo implements  Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 编号
	 */
	private Long id ;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 规范性文件
	 */
	private NormativeFileVo normativeFile;
	
	/**
	 * 主起草单位
	 */
	private OrgShortVo draftingUnit;
	
	/**
	 * 主起草单位负责人
	 */
	private UserShortVo draftingUnitLeader;
	
	/**
	 * 主起草单位经办员
	 */
	private UserShortVo draftingUnitClerk;

	/**
	 * 反馈意见部门
	 */
	private OrgShortVo feedbackUnit;
	
	/**
	 * 反馈经办员
	 */
	private UserShortVo feedbackUnitClerk;
	
	/**
	 * 最晚反馈时间
	 */
	private Date latestFeedbackDate;
	
	/**
	 * 实际反馈时间
	 */
	private Date actualFeedbackDate;
	
	/**
	 * 征求意见稿
	 */
	private String requestingDraft;
	
	/**
	 * 修改意见和建议
	 */
	private String modifyOpinions;
	
	/**
	 * 反馈单位负责人意见
	 */
	private String leaderOpinions;
	
	/**
	 * 备注
	 */
	private String remarks;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public NormativeFileVo getNormativeFile()
	{
		return normativeFile;
	}

	public void setNormativeFile(NormativeFileVo normativeFile)
	{
		this.normativeFile = normativeFile;
	}

	public OrgShortVo getDraftingUnit()
	{
		return draftingUnit;
	}

	public void setDraftingUnit(OrgShortVo draftingUnit)
	{
		this.draftingUnit = draftingUnit;
	}

	public UserShortVo getDraftingUnitLeader()
	{
		return draftingUnitLeader;
	}

	public void setDraftingUnitLeader(UserShortVo draftingUnitLeader)
	{
		this.draftingUnitLeader = draftingUnitLeader;
	}

	public UserShortVo getDraftingUnitClerk()
	{
		return draftingUnitClerk;
	}

	public void setDraftingUnitClerk(UserShortVo draftingUnitClerk)
	{
		this.draftingUnitClerk = draftingUnitClerk;
	}

	public OrgShortVo getFeedbackUnit()
	{
		return feedbackUnit;
	}

	public void setFeedbackUnit(OrgShortVo feedbackUnit)
	{
		this.feedbackUnit = feedbackUnit;
	}

	public UserShortVo getFeedbackUnitClerk()
	{
		return feedbackUnitClerk;
	}

	public void setFeedbackUnitClerk(UserShortVo feedbackUnitClerk)
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
	
	public static FeedbackCommentVo createVo(FeedbackComment feedbackComment)
	{
		FeedbackCommentVo feedbackCommentVo = new FeedbackCommentVo();
		if(feedbackComment == null)
		{
			return feedbackCommentVo;
		}
		feedbackCommentVo.setId(feedbackComment.getId());
		feedbackCommentVo.setName(feedbackComment.getName());
		feedbackCommentVo.setNormativeFile(NormativeFileVo.createVo(feedbackComment.getNormativeFile()));
		feedbackCommentVo.setDraftingUnit(OrgShortVo.createVo(feedbackComment.getDraftingUnit()));
		feedbackCommentVo.setDraftingUnitLeader(UserShortVo.createVo(feedbackComment.getDraftingUnitLeader()));
		feedbackCommentVo.setDraftingUnitClerk(UserShortVo.createVo(feedbackComment.getDraftingUnitClerk()));
		feedbackCommentVo.setFeedbackUnit(OrgShortVo.createVo(feedbackComment.getFeedbackUnit()));
		feedbackCommentVo.setFeedbackUnitClerk(UserShortVo.createVo(feedbackComment.getFeedbackUnitClerk()));
		feedbackCommentVo.setLatestFeedbackDate(feedbackComment.getLatestFeedbackDate());
		feedbackCommentVo.setActualFeedbackDate(feedbackComment.getActualFeedbackDate());
		feedbackCommentVo.setRequestingDraft(feedbackComment.getRequestingDraft());
		feedbackCommentVo.setModifyOpinions(feedbackComment.getModifyOpinions());
		feedbackCommentVo.setLeaderOpinions(feedbackComment.getLeaderOpinions());
		feedbackCommentVo.setRemarks(feedbackComment.getRemarks());
		return feedbackCommentVo;
	}
	
	public static List<FeedbackCommentVo> createVoList(List<FeedbackComment> feedbackCommentList)
	{
		List<FeedbackCommentVo> feedbackCommentVoList = new ArrayList<FeedbackCommentVo>();
		if(feedbackCommentList == null || feedbackCommentList.size() == 0)
		{
			return feedbackCommentVoList;
		}
		for (Iterator<FeedbackComment> iterator = feedbackCommentList.iterator(); iterator.hasNext();)
		{
			feedbackCommentVoList.add(createVo(iterator.next()));
		}
		return feedbackCommentVoList;
	}
	
}
