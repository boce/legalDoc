package com.cdrundle.legaldoc.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.RequestComment;

/**
 * 意见征求--征求意见
 * @author xiaokui.li
 *
 */
public class RequestCommentVo implements  Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 编号
	 */
	private Long id;
	
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
	 * 被征求意见部门
	 */
	private OrgShortVo requestFromUnit;
	
	/**
	 * 最晚反馈时间
	 */
	private Date latestFeedbackDate;

	/**
	 * 征求意见稿
	 */
	private String requestingDraft;
	
	/**
	 * 征求意见函
	 */
	private String content;

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

	public OrgShortVo getRequestFromUnit()
	{
		return requestFromUnit;
	}

	public void setRequestFromUnit(OrgShortVo requestFromUnit)
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
	
	public static RequestCommentVo createVo(RequestComment requestComment)
	{
		RequestCommentVo requestCommentVo = new RequestCommentVo();
		if(requestComment == null)
		{
			return requestCommentVo;
		}
		requestCommentVo.setId(requestComment.getId());
		requestCommentVo.setName(requestComment.getName());
		requestCommentVo.setNormativeFile(NormativeFileVo.createVo(requestComment.getNormativeFile()));
		requestCommentVo.setDraftingUnit(OrgShortVo.createVo(requestComment.getDraftingUnit()));
		requestCommentVo.setDraftingUnitLeader(UserShortVo.createVo(requestComment.getDraftingUnitLeader()));
		requestCommentVo.setDraftingUnitClerk(UserShortVo.createVo(requestComment.getDraftingUnitClerk()));
		requestCommentVo.setRequestFromUnit(OrgShortVo.createVo(requestComment.getRequestFromUnit()));
		requestCommentVo.setLatestFeedbackDate(requestComment.getLatestFeedbackDate());
		requestCommentVo.setRequestingDraft(requestComment.getRequestingDraft());
		requestCommentVo.setContent(requestComment.getContent());
		return requestCommentVo;
	}
	
	public static List<RequestCommentVo> createVoList(List<RequestComment> requestCommentList)
	{
		List<RequestCommentVo> requestCommentVoList = new ArrayList<>();
		if(requestCommentList == null)
		{
			return requestCommentVoList;
		}
		for (Iterator<RequestComment> iterator = requestCommentList.iterator(); iterator.hasNext();)
		{
			requestCommentVoList.add(createVo(iterator.next()));
		}
		return requestCommentVoList;
	}
	
}
