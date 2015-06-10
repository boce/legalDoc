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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;
import com.cdrundle.legaldoc.enums.Priority;
import com.cdrundle.legaldoc.enums.Status;

/**
 * @author XuBao 
 * 备案报送 
 * 2014年6月17日
 */
@Entity
@Table(name = "record_rec_request")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RecordRequest extends LongIdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 文件名称
	 */
	@Column(length = 200,nullable = false)
	private String name;

	/**
	 * 规范性文件
	 */
	@OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name = "nor_file",nullable = false)
	private NormativeFile normativeFile;

	/**
	 * 状态
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status = Status.OPEN;

	/**
	 * 制定单位
	 */
	@ManyToOne
	@JoinColumn(name = "dec_unit",nullable = false)
	private Organization decisionMakingUnit;

	/**
	 * 制定单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "dec_unit_leader",nullable = false)
	private User decisionMakingUnitLeader;

	/**
	 * 制定单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "dec_unit_clerk",nullable = false)
	private User decisionMakingUnitClerk;
	

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
	 * 优先级
	 */
	@Enumerated(EnumType.STRING)
	@Column
	private Priority priority = Priority.NORMAL;

	/**
	 * 备案审查单位
	 */
	@ManyToOne
	@JoinColumn(name = "rec_rev_unit",nullable = false)
	private Organization recordUnit;

	/**
	 * 备案审查单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "rec_rev_unit_leader",nullable = false)
	private User recordUnitLeader;

	/**
	 * 备案审查单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "rec_rev_unit_clerk",nullable = false)
	private User recordUnitClerk;

	/**
	 * 报送日期
	 */
	@Column(name = "rec_req_date",nullable = false)
	private Date recordRequestDate;
	
	/**
	 * 联系电话
	 */
	@Column(name = "phone",nullable = false, length = 200)
	private String phone;

	/**
	 * 规范性文件物理地址
	 */
	@Column(name = "legal_doc",nullable = false)
	private String legalDoc;

	/**
	 * 起草说明
	 */
	@Column(name = "dra_instruction",nullable = false)
	private String draftingInstruction;

	/**
	 * 相关依据
	 */
	@Column(name = "legal_basis",nullable = false)
	private String legalBasis;

	/**
	 * 备案报告
	 */
	@Column(name = "rec_report",nullable = false)
	private String recordReport;

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

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public Organization getDecisionMakingUnit()
	{
		return decisionMakingUnit;
	}

	public void setDecisionMakingUnit(Organization decisionMakingUnit)
	{
		this.decisionMakingUnit = decisionMakingUnit;
	}

	public User getDecisionMakingUnitLeader()
	{
		return decisionMakingUnitLeader;
	}

	public void setDecisionMakingUnitLeader(User decisionMakingUnitLeader)
	{
		this.decisionMakingUnitLeader = decisionMakingUnitLeader;
	}

	public User getDecisionMakingUnitClerk()
	{
		return decisionMakingUnitClerk;
	}

	public void setDecisionMakingUnitClerk(User decisionMakingUnitClerk)
	{
		this.decisionMakingUnitClerk = decisionMakingUnitClerk;
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

	public Priority getPriority()
	{
		return priority;
	}

	public void setPriority(Priority priority)
	{
		this.priority = priority;
	}

	public Organization getRecordUnit()
	{
		return recordUnit;
	}

	public void setRecordUnit(Organization recordUnit)
	{
		this.recordUnit = recordUnit;
	}

	public User getRecordUnitLeader()
	{
		return recordUnitLeader;
	}

	public void setRecordUnitLeader(User recordUnitLeader)
	{
		this.recordUnitLeader = recordUnitLeader;
	}

	public User getRecordUnitClerk()
	{
		return recordUnitClerk;
	}

	public void setRecordUnitClerk(User recordUnitClerk)
	{
		this.recordUnitClerk = recordUnitClerk;
	}

	public Date getRecordRequestDate()
	{
		return recordRequestDate;
	}

	public void setRecordRequestDate(Date recordRequestDate)
	{
		this.recordRequestDate = recordRequestDate;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getLegalDoc()
	{
		return legalDoc;
	}

	public void setLegalDoc(String legalDoc)
	{
		this.legalDoc = legalDoc;
	}

	public String getDraftingInstruction()
	{
		return draftingInstruction;
	}

	public void setDraftingInstruction(String draftingInstruction)
	{
		this.draftingInstruction = draftingInstruction;
	}

	public String getLegalBasis()
	{
		return legalBasis;
	}

	public void setLegalBasis(String legalBasis)
	{
		this.legalBasis = legalBasis;
	}

	public String getRecordReport()
	{
		return recordReport;
	}

	public void setRecordReport(String recordReport)
	{
		this.recordReport = recordReport;
	}

	
	
}
