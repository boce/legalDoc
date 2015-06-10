package com.cdrundle.legaldoc.vo;

import java.util.Date;

import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.enums.AssessResult;
import com.cdrundle.legaldoc.enums.Status;

/**
 * @author gang.li
 * 
 * 期满评估VO
 */
public class DeferredAssessmentVo {

	private Long id;
	
	private String name;

	private NormativeFileVo normativeFile;

	private Status status;

	private OrgShortVo decisionUnit;

	private UserShortVo decisionUnitLeader;

	private UserShortVo decisionUnitClerk;

	private OrgShortVo draftingUnit;

	private UserShortVo draftingUnitLeader;

	private UserShortVo draftingUnitClerk;

	private AssessResult assessResult;

	private Date validDate;

	private Date assessDate;

	private String legalDoc;

	private String draftingInstruction;

	private String legalBasis;

	private String assessComment;

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

	public NormativeFileVo getNormativeFile() {
		return normativeFile;
	}

	public void setNormativeFile(NormativeFileVo normativeFile) {
		this.normativeFile = normativeFile;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public OrgShortVo getDecisionUnit() {
		return decisionUnit;
	}

	public void setDecisionUnit(OrgShortVo decisionUnit) {
		this.decisionUnit = decisionUnit;
	}

	public UserShortVo getDecisionUnitLeader() {
		return decisionUnitLeader;
	}

	public void setDecisionUnitLeader(UserShortVo decisionUnitLeader) {
		this.decisionUnitLeader = decisionUnitLeader;
	}

	public UserShortVo getDecisionUnitClerk() {
		return decisionUnitClerk;
	}

	public void setDecisionUnitClerk(UserShortVo decisionUnitClerk) {
		this.decisionUnitClerk = decisionUnitClerk;
	}

	public OrgShortVo getDraftingUnit() {
		return draftingUnit;
	}

	public void setDraftingUnit(OrgShortVo draftingUnit) {
		this.draftingUnit = draftingUnit;
	}

	public UserShortVo getDraftingUnitLeader() {
		return draftingUnitLeader;
	}

	public void setDraftingUnitLeader(UserShortVo draftingUnitLeader) {
		this.draftingUnitLeader = draftingUnitLeader;
	}

	public UserShortVo getDraftingUnitClerk() {
		return draftingUnitClerk;
	}

	public void setDraftingUnitClerk(UserShortVo draftingUnitClerk) {
		this.draftingUnitClerk = draftingUnitClerk;
	}

	public AssessResult getAssessResult() {
		return assessResult;
	}

	public void setAssessResult(AssessResult assessResult) {
		this.assessResult = assessResult;
	}

	public Date getValidDate() {
		return validDate;
	}

	public void setValidDate(Date validDate) {
		this.validDate = validDate;
	}

	public Date getAssessDate() {
		return assessDate;
	}

	public void setAssessDate(Date assessDate) {
		this.assessDate = assessDate;
	}

	public String getLegalDoc() {
		return legalDoc;
	}

	public void setLegalDoc(String legalDoc) {
		this.legalDoc = legalDoc;
	}

	public String getDraftingInstruction() {
		return draftingInstruction;
	}

	public void setDraftingInstruction(String draftingInstruction) {
		this.draftingInstruction = draftingInstruction;
	}

	public String getLegalBasis() {
		return legalBasis;
	}

	public void setLegalBasis(String legalBasis) {
		this.legalBasis = legalBasis;
	}

	public String getAssessComment() {
		return assessComment;
	}

	public void setAssessComment(String assessComment) {
		this.assessComment = assessComment;
	}
	
}
