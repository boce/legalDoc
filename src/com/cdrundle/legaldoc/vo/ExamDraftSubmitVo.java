package com.cdrundle.legaldoc.vo;

import com.cdrundle.legaldoc.enums.Status;

public class ExamDraftSubmitVo{

	
	private Long id;
	
	private String name;
	
	private NormativeFileVo normativeFile; 
	
	private OrgShortVo draftingUnit;
	
	private UserShortVo draftingUnitLeader;
	
	private UserShortVo draftingUnitClerk;
	
	private String unionDraftingUnit;
	
	private String unionDraftingUnitLeader;
	
	private String unionDraftingUnitClerk;

	private Status status;
	
	private OrgShortVo reviewUnit;
	
	private UserShortVo reviewUnitLeader;
	
	private UserShortVo reviewUnitClerk;
	
	private String examinationDraft;
	
	private String draftingInstruction;
	
	private String legalBasises;
	
	private String legalBasisesNoAtta;
	
	public ExamDraftSubmitVo() {
		
	}
	
	public ExamDraftSubmitVo(Long id, String name, NormativeFileVo normativeFile, 
			OrgShortVo draftingUnit, UserShortVo  draftingUnitLeader, UserShortVo draftingUnitClerk,
	 	String unionDraftingUnit, String unionDraftingUnitLeader, String unionDraftingUnitClerk,
		Status status, OrgShortVo reviewUnit, UserShortVo reviewUnitLeader, UserShortVo reviewUnitClerk,
		String examinationDraft, String draftingInstruction, String legalBasises, String legalBasisesNoAtta){
		
		this.id = id;
		this.name = name;
		this.normativeFile = normativeFile;
		this.draftingUnit = draftingUnit; 
		this.draftingUnitLeader = draftingUnitLeader; 
		this.draftingUnitClerk = draftingUnitClerk;
	 	this.unionDraftingUnit = unionDraftingUnit; 
	 	this.unionDraftingUnitLeader = unionDraftingUnitLeader; 
	 	this.unionDraftingUnitClerk = unionDraftingUnitLeader;
		this.status = status; 
		this.reviewUnit = reviewUnit; 
		this.reviewUnitLeader = reviewUnitLeader;
		this.reviewUnitClerk = reviewUnitClerk;
		this.examinationDraft = examinationDraft;
		this.draftingInstruction = draftingInstruction;
		this.legalBasises = legalBasises;
		this.legalBasisesNoAtta = legalBasisesNoAtta;
	}
	
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

	public String getUnionDraftingUnit() {
		return unionDraftingUnit;
	}

	public void setUnionDraftingUnit(String unionDraftingUnit) {
		this.unionDraftingUnit = unionDraftingUnit;
	}

	public String getUnionDraftingUnitLeader() {
		return unionDraftingUnitLeader;
	}

	public void setUnionDraftingUnitLeader(String unionDraftingUnitLeader) {
		this.unionDraftingUnitLeader = unionDraftingUnitLeader;
	}

	public String getUnionDraftingUnitClerk() {
		return unionDraftingUnitClerk;
	}

	public void setUnionDraftingUnitClerk(String unionDraftingUnitClerk) {
		this.unionDraftingUnitClerk = unionDraftingUnitClerk;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public OrgShortVo getReviewUnit() {
		return reviewUnit;
	}

	public void setReviewUnit(OrgShortVo reviewUnit) {
		this.reviewUnit = reviewUnit;
	}

	public UserShortVo getReviewUnitLeader() {
		return reviewUnitLeader;
	}

	public void setReviewUnitLeader(UserShortVo reviewUnitLeader) {
		this.reviewUnitLeader = reviewUnitLeader;
	}

	public UserShortVo getReviewUnitClerk() {
		return reviewUnitClerk;
	}

	public void setReviewUnitClerk(UserShortVo reviewUnitClerk) {
		this.reviewUnitClerk = reviewUnitClerk;
	}

	public String getExaminationDraft() {
		return examinationDraft;
	}

	public void setExaminationDraft(String examinationDraft) {
		this.examinationDraft = examinationDraft;
	}

	public String getDraftingInstruction() {
		return draftingInstruction;
	}

	public void setDraftingInstruction(String draftingInstruction) {
		this.draftingInstruction = draftingInstruction;
	}

	public String getLegalBasises() {
		return legalBasises;
	}

	public void setLegalBasises(String legalBasises) {
		this.legalBasises = legalBasises;
	}

	public String getLegalBasisesNoAtta() {
		return legalBasisesNoAtta;
	}

	public void setLegalBasisesNoAtta(String legalBasisesNoAtta) {
		this.legalBasisesNoAtta = legalBasisesNoAtta;
	}

}
