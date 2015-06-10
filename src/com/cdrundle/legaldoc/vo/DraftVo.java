package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.Draft;
import com.cdrundle.legaldoc.enums.DraftingMode;
import com.cdrundle.legaldoc.enums.Status;

/**
 * 起草
 * @author xiaokui.li
 *
 */
public class DraftVo
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
	 * 联合起草单位
	 */
	private String unionDraftingUnit;
	
	/**
	 * 联合起草单位负责人
	 */
	private String unionDraftingUnitLeader;
	
	/**
	 * 联合起草单位经办员
	 */
	private String unionDraftingUnitClerk;
	
	/**
	 * 状态
	 */
	private Status status = Status.OPEN;
	
	/**
	 * 起草开始日期
	 */
	private Date draftingStartDate;
	
	/**
	 * 起草完成日期
	 */
	private Date draftingEndDate;
	
	/**
	 * 起草方式
	 */
	private DraftingMode draftingMode = DraftingMode.INDEPENDENT_DRAFTING;
	
	/**
	 * 涉及的部门
	 */
	private String involvedOrges;

	/**
	 * 涉及的部门名称
	 */
	private String involvedOrgNames;
	
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

	public String getUnionDraftingUnit()
	{
		return unionDraftingUnit;
	}

	public void setUnionDraftingUnit(String unionDraftingUnit)
	{
		this.unionDraftingUnit = unionDraftingUnit;
	}

	public String getUnionDraftingUnitLeader()
	{
		return unionDraftingUnitLeader;
	}

	public void setUnionDraftingUnitLeader(String unionDraftingUnitLeader)
	{
		this.unionDraftingUnitLeader = unionDraftingUnitLeader;
	}

	public String getUnionDraftingUnitClerk()
	{
		return unionDraftingUnitClerk;
	}

	public void setUnionDraftingUnitClerk(String unionDraftingUnitClerk)
	{
		this.unionDraftingUnitClerk = unionDraftingUnitClerk;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public Date getDraftingStartDate()
	{
		return draftingStartDate;
	}

	public void setDraftingStartDate(Date draftingStartDate)
	{
		this.draftingStartDate = draftingStartDate;
	}

	public Date getDraftingEndDate()
	{
		return draftingEndDate;
	}

	public void setDraftingEndDate(Date draftingEndDate)
	{
		this.draftingEndDate = draftingEndDate;
	}

	public DraftingMode getDraftingMode()
	{
		return draftingMode;
	}

	public void setDraftingMode(DraftingMode draftingMode)
	{
		this.draftingMode = draftingMode;
	}

	public String getInvolvedOrges()
	{
		return involvedOrges;
	}

	public void setInvolvedOrges(String involvedOrges)
	{
		this.involvedOrges = involvedOrges;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getInvolvedOrgNames() {
		return involvedOrgNames;
	}

	public void setInvolvedOrgNames(String involvedOrgNames) {
		this.involvedOrgNames = involvedOrgNames;
	}

	public static DraftVo createVo(Draft draft)
	{
		DraftVo draftVo = new DraftVo();
		if(draft == null)
		{
			return draftVo;
		}
		draftVo.setId(draft.getId());
		draftVo.setName(draft.getName());
		draftVo.setNormativeFile(NormativeFileVo.createVo(draft.getNormativeFile()));
		draftVo.setDraftingUnit(OrgShortVo.createVo(draft.getDraftingUnit()));
		draftVo.setDraftingUnitLeader(UserShortVo.createVo(draft.getDraftingUnitLeader()));
		draftVo.setDraftingUnitClerk(UserShortVo.createVo(draft.getDraftingUnitClerk()));
		draftVo.setUnionDraftingUnit(draft.getUnionDraftingUnit());
		draftVo.setUnionDraftingUnitLeader(draft.getUnionDraftingUnitLeader());
		draftVo.setUnionDraftingUnitClerk(draft.getUnionDraftingUnitClerk());
		draftVo.setStatus(draft.getStatus());
		draftVo.setDraftingStartDate(draft.getDraftingStartDate());
		draftVo.setDraftingEndDate(draft.getDraftingEndDate());
		draftVo.setDraftingMode(draft.getDraftingMode());
		draftVo.setInvolvedOrges(draft.getInvolvedOrges());
		draftVo.setContent(draft.getContent());
		return draftVo;
	}
	
	public static List<DraftVo> createVoList(List<Draft> draftList)
	{
		List<DraftVo> draftVoList = new ArrayList<>();
		if(draftList == null)
		{
			return draftVoList;
		}
		for (Iterator<Draft> iterator = draftList.iterator(); iterator.hasNext();)
		{
			draftVoList.add(createVo(iterator.next()));
		}
		return draftVoList;
	}
	
}
