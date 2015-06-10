package com.cdrundle.legaldoc.vo;

import java.util.Date;

public class NorFileQueryVo {
	private String legalBasis;
	private OrgShortVo drtUnit;
	private String name;
	private Date startDate;
	private Date endDate;

	public String getLegalBasis() {
		return legalBasis;
	}

	public void setLegalBasis(String legalBasis) {
		this.legalBasis = legalBasis;
	}

	public OrgShortVo getDrtUnit() {
		return drtUnit;
	}

	public void setDrtUnit(OrgShortVo drtUnit) {
		this.drtUnit = drtUnit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
