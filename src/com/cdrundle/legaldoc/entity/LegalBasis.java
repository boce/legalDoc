package com.cdrundle.legaldoc.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;
import com.cdrundle.legaldoc.enums.LegalBasisType;

/**
 * 制定依据
 * @author xiaokui.li
 *
 */
@Entity
@Table(name = "legal_basis")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LegalBasis extends LongIdEntity{

	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@Column(nullable = false)
	private String name;
	
	/**
	 * 依据类型
	 */
	@Enumerated(EnumType.STRING)
	@Column(name="legal_basis_type",nullable = false)
	private LegalBasisType legalBasisType;
	
	/**
	 * 依据失效日期
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "basis_invalid_date")
	private Date basisInvalidDate;
	
	/**
	 * 依据类型
	 */
	@Column(name="legal_basis_atta",nullable = false)
	private String legalBasisAtta;

	/**
	 * 立项申请单
	 */
	@ManyToOne
	@JoinColumn(name="devapp_id")
	private DevelopApplication developApplication;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LegalBasisType getLegalBasisType() {
		return legalBasisType;
	}

	public void setLegalBasisType(LegalBasisType legalBasisType) {
		this.legalBasisType = legalBasisType;
	}

	public Date getBasisInvalidDate() {
		return basisInvalidDate;
	}

	public void setBasisInvalidDate(Date basisInvalidDate) {
		this.basisInvalidDate = basisInvalidDate;
	}

	public String getLegalBasisAtta() {
		return legalBasisAtta;
	}

	public void setLegalBasisAtta(String legalBasisAtta) {
		this.legalBasisAtta = legalBasisAtta;
	}

	public DevelopApplication getDevelopApplication() {
		return developApplication;
	}

	public void setDevelopApplication(DevelopApplication developApplication) {
		this.developApplication = developApplication;
	}
	
}
