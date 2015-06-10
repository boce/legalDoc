package com.cdrundle.legaldoc.entity;

import java.util.Date;

import javax.persistence.CascadeType;
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
import com.cdrundle.legaldoc.enums.Status;

/**
 * @author XuBao 
 * 审议报请 
 * 2014年6月9日
 */
@Entity
@Table(name = "set_del_request")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DeliberationRequest extends LongIdEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 文件名称
	 */
	@Column(nullable = false, length = 200)
	private String name;

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
	private String  unionDraUnit;

	/**
	 * 联合起草单位经办员
	 */
	@Column(name = "union_dra_unit_clerk")
	private String unionDraUnitClerk;

	/**
	 * 联合起草单位负责人
	 */
	@Column(name = "union_dra_unit_leader")
	private String unionDraUnitLeader;

	/**
	 * 状态
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.OPEN;

	/**
	 * 审议单位
	 */
	@Column(name = "del_unit",nullable = false)
	private String deliberationUnit;

	/**
	 * 报请日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "request_date",nullable = false)
	private Date requestDate;

	/**
	 * 草案
	 */
	@Column(nullable = false)
	private String protocol;

	/**
	 * 起草说明
	 */
	@Column(name = "dra_instruction",nullable = false)
	private String draftingInstruction;

	/**
	 * 征求意见的相关材料
	 */
	@Column(name = "req_comments",nullable = false)
	private String requestComments;

	/**
	 * 法律审查意见书
	 */
	@Column(name = "rev_comment",nullable = false)
	private String reviewComments;

	/**
	 * 审议的请示
	 */
	@Column(name = "req_instruction")
	private String reviewInstruction;

	/**
	 * 规范性文件
	 */
	@OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name = "nor_file",nullable = false)
	private NormativeFile normativeFile;
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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

	public String getUnionDraUnit()
	{
		return unionDraUnit;
	}

	public void setUnionDraUnit(String unionDraUnit)
	{
		this.unionDraUnit = unionDraUnit;
	}

	public String getUnionDraUnitClerk()
	{
		return unionDraUnitClerk;
	}

	public void setUnionDraUnitClerk(String unionDraUnitClerk)
	{
		this.unionDraUnitClerk = unionDraUnitClerk;
	}

	public String getUnionDraUnitLeader()
	{
		return unionDraUnitLeader;
	}

	public void setUnionDraUnitLeader(String unionDraUnitLeader)
	{
		this.unionDraUnitLeader = unionDraUnitLeader;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public String getDeliberationUnit()
	{
		return deliberationUnit;
	}

	public void setDeliberationUnit(String deliberationUnit)
	{
		this.deliberationUnit = deliberationUnit;
	}

	public Date getRequestDate()
	{
		return requestDate;
	}

	public void setRequestDate(Date requestDate)
	{
		this.requestDate = requestDate;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public void setProtocol(String protocol)
	{
		this.protocol = protocol;
	}

	public String getDraftingInstruction()
	{
		return draftingInstruction;
	}

	public void setDraftingInstruction(String draftingInstruction)
	{
		this.draftingInstruction = draftingInstruction;
	}

	public String getRequestComments()
	{
		return requestComments;
	}

	public void setRequestComments(String requestComments)
	{
		this.requestComments = requestComments;
	}

	public String getReviewComments()
	{
		return reviewComments;
	}

	public void setReviewComments(String reviewComments)
	{
		this.reviewComments = reviewComments;
	}

	public String getReviewInstruction()
	{
		return reviewInstruction;
	}

	public void setReviewInstruction(String reviewInstruction)
	{
		this.reviewInstruction = reviewInstruction;
	}

	public NormativeFile getNormativeFile()
	{
		return normativeFile;
	}

	public void setNormativeFile(NormativeFile normativeFile)
	{
		this.normativeFile = normativeFile;
	}

}