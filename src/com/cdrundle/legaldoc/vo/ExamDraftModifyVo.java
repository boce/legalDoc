package com.cdrundle.legaldoc.vo;

public class ExamDraftModifyVo{

	/**
	 * 送审稿修改Vo
	 * 
	 */
	
	private Long id;
	
	private String name;
	
	private NormativeFileVo normativeFile; 
	
	private OrgShortVo draftingUnit;
	
	private UserShortVo draftingUnitLeader;
	
	private UserShortVo draftingUnitClerk;
	
	private  String  reviewComment;
	
	private  String  content;
	
	public ExamDraftModifyVo() {
		
	}
	
	public ExamDraftModifyVo(Long id, String name, NormativeFileVo normativeFile, 
			OrgShortVo draftingUnit, UserShortVo  draftingUnitLeader, UserShortVo draftingUnitClerk,
			String reviewComment, String content){
		
		this.id = id;
		this.name = name;
		this.normativeFile = normativeFile;
		this.draftingUnit = draftingUnit; 
		this.draftingUnitLeader = draftingUnitLeader; 
		this.draftingUnitClerk = draftingUnitClerk;
		this.reviewComment = reviewComment;
		this.content = content;
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

	public String getReviewComment() {
		return reviewComment;
	}

	public void setReviewComment(String reviewComment) {
		this.reviewComment = reviewComment;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
