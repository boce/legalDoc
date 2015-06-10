package com.cdrundle.legaldoc.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;

/**
 * @author XuBao 
 * 签署发布 
 * 2014年6月10日
 */
@Entity
@Table(name = "set_sign_publish")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SignAndPublish extends LongIdEntity {

	/**
				 * 
				 */
	private static final long serialVersionUID = 1L;

	/**
	 * 文件名
	 */
	@Column(length = 200,nullable = false)
	private String name;
	
	/**
	 * 起草单位
	 */
	@ManyToOne
	@JoinColumn(name = "drt_unit")
	private  Organization  draftingUnit;

	/**
	 * 规范性文件
	 */
	@OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name = "nor_file",nullable = false)
	private NormativeFile normativeFile;

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
	 * 签署领导
	 */
	@Column(name = "sign_leaders",nullable = false)
	private String signLeaders;

	/**
	 * 签署日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "sign_date",nullable = false)
	private Date signDate;

	/**
	 * 发文号
	 */
	@Column(name = "publish_no",nullable = false)
	private String publishNo;

	/**
	 * 发布日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "publish_date",nullable = false)
	private Date publishDate;

	/**
	 * 有效期
	 */
	@Column(name = "valid_date",nullable = false)
	private Integer validDate;

	/**
	 * 失效日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "invalid_date",nullable = false)
	private Date invalidDate;

	/**
	 * 规范性文件内容
	 */
	@Column(name = "legal_doc",nullable = false)
	private String legalDoc;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NormativeFile getNormativeFile() {
		return normativeFile;
	}

	public void setNormativeFile(NormativeFile normativeFile) {
		this.normativeFile = normativeFile;
	}

	public Organization getDecisionMakingUnit() {
		return decisionMakingUnit;
	}

	public void setDecisionMakingUnit(Organization decisionMakingUnit) {
		this.decisionMakingUnit = decisionMakingUnit;
	}

	public User getDecisionMakingUnitLeader() {
		return decisionMakingUnitLeader;
	}

	public void setDecisionMakingUnitLeader(User decisionMakingUnitLeader) {
		this.decisionMakingUnitLeader = decisionMakingUnitLeader;
	}

	public User getDecisionMakingUnitClerk() {
		return decisionMakingUnitClerk;
	}

	public void setDecisionMakingUnitClerk(User decisionMakingUnitClerk) {
		this.decisionMakingUnitClerk = decisionMakingUnitClerk;
	}

	public String getSignLeaders() {
		return signLeaders;
	}

	public void setSignLeaders(String signLeaders) {
		this.signLeaders = signLeaders;
	}

	public Date getSignDate() {
		return signDate;
	}

	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}

	public String getPublishNo() {
		return publishNo;
	}

	public void setPublishNo(String publishNo) {
		this.publishNo = publishNo;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}
	
	public Integer getValidDate()
	{
		return validDate;
	}

	public void setValidDate(Integer validDate)
	{
		this.validDate = validDate;
	}

	public Date getInvalidDate() {
		return invalidDate;
	}

	public void setInvalidDate(Date invalidDate) {
		this.invalidDate = invalidDate;
	}

	public String getLegalDoc() {
		return legalDoc;
	}

	public void setLegalDoc(String legalDoc) {
		this.legalDoc = legalDoc;
	}

	public Organization getDraftingUnit()
	{
		return draftingUnit;
	}

	public void setDraftingUnit(Organization draftingUnit)
	{
		this.draftingUnit = draftingUnit;
	}

	
}
