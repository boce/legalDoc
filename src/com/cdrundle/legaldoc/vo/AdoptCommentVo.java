package com.cdrundle.legaldoc.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.AdoptComment;


/**
 * 意见征求--反馈意见处理情况
 * @author xiaokui.li
 *
 */
public class AdoptCommentVo implements  Serializable
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
	 * 反馈意见
	 */
	private String feedbackComment;

	/**
	 * 反馈意见处理情况
	 */
	private String feedbackProcess;
	
	/**
	 * 是否需要修改征求意见稿
	 */
	private Boolean isNeedModify;

	public String getFeedbackProcess() {
		return feedbackProcess;
	}

	public void setFeedbackProcess(String feedbackProcess) {
		this.feedbackProcess = feedbackProcess;
	}

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

	public static AdoptCommentVo createVo(AdoptComment adoptComment)
	{
		AdoptCommentVo adoptCommentVo = new AdoptCommentVo();
		if(adoptComment == null)
		{
			return adoptCommentVo;
		}
		adoptCommentVo.setId(adoptComment.getId());
		adoptCommentVo.setName(adoptComment.getName());
		adoptCommentVo.setNormativeFile(NormativeFileVo.createVo(adoptComment.getNormativeFile()));
		adoptCommentVo.setDraftingUnit(OrgShortVo.createVoNoChild(adoptComment.getDraftingUnit()));
		adoptCommentVo.setDraftingUnitLeader(UserShortVo.createVo(adoptComment.getDraftingUnitLeader()));
		adoptCommentVo.setDraftingUnitClerk(UserShortVo.createVo(adoptComment.getDraftingUnitClerk()));
		adoptCommentVo.setFeedbackComment(adoptComment.getFeedbackComment());
		adoptCommentVo.setFeedbackProcess(adoptComment.getFeedbackProcess());
		adoptCommentVo.setIsNeedModify(adoptComment.getIsNeedModify());
		return adoptCommentVo;
	}
	
	public static List<AdoptCommentVo> createVoList(List<AdoptComment> adoptCommentList)
	{
		List<AdoptCommentVo> adoptCommentVoList = new ArrayList<AdoptCommentVo>();
		if(adoptCommentList == null || adoptCommentList.size() == 0 )
		{
			return adoptCommentVoList;
		}
		for (Iterator<AdoptComment> iterator = adoptCommentList.iterator(); iterator.hasNext();)
		{
			adoptCommentVoList.add(createVo(iterator.next()));
		}
		return adoptCommentVoList;
	}
	
}
