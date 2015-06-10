package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.ModifyDraft;

/**
 * 意见征求--修改《征求意见稿》
 * @author xiaokui.li
 *
 */
public class ModifyDraftVo
{

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
	private String feedbackProcess;

	/**
	 * 内容
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


	public String getFeedbackProcess()
	{
		return feedbackProcess;
	}

	public void setFeedbackProcess(String feedbackProcess)
	{
		this.feedbackProcess = feedbackProcess;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}
	
	public static ModifyDraftVo createVo(ModifyDraft modifyDraft)
	{
		ModifyDraftVo modifyDraftVo = new ModifyDraftVo();
		if(modifyDraft == null)
		{
			return modifyDraftVo;
		}
		modifyDraftVo.setId(modifyDraft.getId());
		modifyDraftVo.setName(modifyDraft.getName());
		modifyDraftVo.setNormativeFile(NormativeFileVo.createVo(modifyDraft.getNormativeFile()));
		modifyDraftVo.setDraftingUnit(OrgShortVo.createVo(modifyDraft.getDraftingUnit()));
		modifyDraftVo.setDraftingUnitLeader(UserShortVo.createVo(modifyDraft.getDraftingUnitLeader()));
		modifyDraftVo.setDraftingUnitClerk(UserShortVo.createVo(modifyDraft.getDraftingUnitClerk()));
		modifyDraftVo.setFeedbackProcess(modifyDraft.getFeedbackProcess());
		modifyDraftVo.setContent(modifyDraft.getContent());
		return modifyDraftVo;
	}
	
	public static List<ModifyDraftVo> createVoList(List<ModifyDraft> modifyDraftList)
	{
		List<ModifyDraftVo> modifyDraftVoList = new ArrayList<>();
		if(modifyDraftList == null || modifyDraftList.size() == 0)
		{
			return modifyDraftVoList;
		}
		for (Iterator<ModifyDraft> iterator = modifyDraftList.iterator(); iterator.hasNext();)
		{
			modifyDraftVoList.add(createVo(iterator.next()));
		}
		return modifyDraftVoList;
	}
}
