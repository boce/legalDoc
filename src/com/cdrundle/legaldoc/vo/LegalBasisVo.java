package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.LegalBasis;
import com.cdrundle.legaldoc.enums.LegalBasisType;

/**
 * 制定依据
 * @author xiaokui.li
 *
 */
public class LegalBasisVo{

	private Long id;
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 依据类型
	 */
	private LegalBasisType legalBasisType;
	
	/**
	 * 依据失效日期
	 */
	private Date basisInvalidDate;
	
	/**
	 * 依据类型
	 */
	private String legalBasisAtta;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
	
	public static LegalBasisVo createVo(LegalBasis legalBasis){
		LegalBasisVo legalBasisVo = new LegalBasisVo();
		if(legalBasis == null){
			return null;
		}
		legalBasisVo.setId(legalBasis.getId());
		legalBasisVo.setName(legalBasis.getName());
		legalBasisVo.setBasisInvalidDate(legalBasis.getBasisInvalidDate());
		legalBasisVo.setLegalBasisType(legalBasis.getLegalBasisType());
		legalBasisVo.setLegalBasisAtta(legalBasis.getLegalBasisAtta());
		return legalBasisVo;
	}
	
	public static List<LegalBasisVo> createVoList(List<LegalBasis> legalBasisList){
		List<LegalBasisVo> legalBasisVoList = new ArrayList<>();
		if(legalBasisList == null){
			return null;
		}
		for (Iterator<LegalBasis> iterator = legalBasisList.iterator(); iterator.hasNext();) {
			legalBasisVoList.add(createVo(iterator.next()));
		}
		return legalBasisVoList;
	}
}
