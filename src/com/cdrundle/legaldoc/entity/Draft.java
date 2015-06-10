package com.cdrundle.legaldoc.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;
import com.cdrundle.legaldoc.enums.DraftingMode;
import com.cdrundle.legaldoc.enums.Status;

/**
 * 起草
 * @author xiaokui.li
 *
 */
@Entity
@Table(name = "set_drafting")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Draft extends LongIdEntity
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
	@Column(nullable = false)
	private Status status = Status.OPEN;
	
	/**
	 * 起草开始日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "dra_start_date")
	private Date draftingStartDate;
	
	/**
	 * 起草完成日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "dra_end_date", nullable = false)
	private Date draftingEndDate;
	
	/**
	 * 起草方式
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "dra_mode", nullable = false)
	private DraftingMode draftingMode = DraftingMode.INDEPENDENT_DRAFTING;
	
	/**
	 * 涉及的部门
	 */
	@Column(name = "involved_orges")
	private String involvedOrges;

	/**
	 * 内容
	 */
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

}
