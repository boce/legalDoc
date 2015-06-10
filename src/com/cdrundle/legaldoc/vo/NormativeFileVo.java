package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.enums.FileStatus;
import com.cdrundle.legaldoc.enums.Priority;
import com.cdrundle.legaldoc.enums.Stage;

/**
 * @author XuBao
 * 
 *         2014年6月12日
 */
public class NormativeFileVo {

	private Long id;
	private String name;
	private OrgShortVo decUnit;
	private UserShortVo decUnitClerk;
	private UserShortVo decUnitLeader;
	private OrgShortVo drtUnit;
	private UserShortVo drtUnitClerk;
	private UserShortVo drtUnitLeader;
	private String unionDrtUnit;
	private String unionDrtUnitClerk;
	private String unionDrtUnitLeader;
	private OrgShortVo revUnit;
	private UserShortVo revUnitClerk;
	private UserShortVo revUnitLeader;
	private String delUnit;
	private OrgShortVo recRevUnit;
	private UserShortVo recRevUnitClerk;
	private UserShortVo recRevUnitLeader;
	private String registerCode;
	private Date applyDate;
	private Date draftDate;
	private Date requestDate;
	private Date delDate;
	private Date publishDate;
	private Date registerDate;
	private Date invalidDate;
	private Date basisInvalidDate;
	private Integer validDate;
	private Priority priority;
	private FileStatus status;
	private Stage stage;
	private String publishNo;
	private String legalDoc;
	private String draftInstruction;
	private String legalBasis;
	private String legalBasisNoAtta;
	private String invalidReason;
	private String moreFiles;
	private Date reviewDate;
	private String involvedOrges;
	private OrgShortVo applyUnit;
	private String docNo;
	/**
	 * 联合起草单位名称
	 */
	private String unionDrtUnitName;

	/**
	 * 联合起草单位负责人名称
	 */
	private String unionDrtUnitLeaderName;

	/**
	 * 涉及部门名称
	 */
	private String involvedOrgesName;

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public String getInvolvedOrges() {
		return involvedOrges;
	}

	public void setInvolvedOrges(String involvedOrges) {
		this.involvedOrges = involvedOrges;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
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

	public String getUnionDrtUnit() {
		return unionDrtUnit;
	}

	public void setUnionDrtUnit(String unionDrtUnit) {
		this.unionDrtUnit = unionDrtUnit;
	}

	public String getUnionDrtUnitClerk() {
		return unionDrtUnitClerk;
	}

	public void setUnionDrtUnitClerk(String unionDrtUnitClerk) {
		this.unionDrtUnitClerk = unionDrtUnitClerk;
	}

	public String getUnionDrtUnitLeader() {
		return unionDrtUnitLeader;
	}

	public void setUnionDrtUnitLeader(String unionDrtUnitLeader) {
		this.unionDrtUnitLeader = unionDrtUnitLeader;
	}

	public String getDelUnit() {
		return delUnit;
	}

	public void setDelUnit(String delUnit) {
		this.delUnit = delUnit;
	}

	public OrgShortVo getDecUnit() {
		return decUnit;
	}

	public void setDecUnit(OrgShortVo decUnit) {
		this.decUnit = decUnit;
	}

	public UserShortVo getDecUnitClerk() {
		return decUnitClerk;
	}

	public void setDecUnitClerk(UserShortVo decUnitClerk) {
		this.decUnitClerk = decUnitClerk;
	}

	public UserShortVo getDecUnitLeader() {
		return decUnitLeader;
	}

	public void setDecUnitLeader(UserShortVo decUnitLeader) {
		this.decUnitLeader = decUnitLeader;
	}

	public OrgShortVo getDrtUnit() {
		return drtUnit;
	}

	public void setDrtUnit(OrgShortVo drtUnit) {
		this.drtUnit = drtUnit;
	}

	public UserShortVo getDrtUnitClerk() {
		return drtUnitClerk;
	}

	public void setDrtUnitClerk(UserShortVo drtUnitClerk) {
		this.drtUnitClerk = drtUnitClerk;
	}

	public UserShortVo getDrtUnitLeader() {
		return drtUnitLeader;
	}

	public void setDrtUnitLeader(UserShortVo drtUnitLeader) {
		this.drtUnitLeader = drtUnitLeader;
	}

	public OrgShortVo getRevUnit() {
		return revUnit;
	}

	public void setRevUnit(OrgShortVo revUnit) {
		this.revUnit = revUnit;
	}

	public UserShortVo getRevUnitClerk() {
		return revUnitClerk;
	}

	public void setRevUnitClerk(UserShortVo revUnitClerk) {
		this.revUnitClerk = revUnitClerk;
	}

	public UserShortVo getRevUnitLeader() {
		return revUnitLeader;
	}

	public void setRevUnitLeader(UserShortVo revUnitLeader) {
		this.revUnitLeader = revUnitLeader;
	}

	public OrgShortVo getRecRevUnit() {
		return recRevUnit;
	}

	public void setRecRevUnit(OrgShortVo recRevUnit) {
		this.recRevUnit = recRevUnit;
	}

	public UserShortVo getRecRevUnitClerk() {
		return recRevUnitClerk;
	}

	public void setRecRevUnitClerk(UserShortVo recRevUnitClerk) {
		this.recRevUnitClerk = recRevUnitClerk;
	}

	public UserShortVo getRecRevUnitLeader() {
		return recRevUnitLeader;
	}

	public void setRecRevUnitLeader(UserShortVo recRevUnitLeader) {
		this.recRevUnitLeader = recRevUnitLeader;
	}

	public String getRegisterCode() {
		return registerCode;
	}

	public void setRegisterCode(String registerCode) {
		this.registerCode = registerCode;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public Date getDraftDate() {
		return draftDate;
	}

	public void setDraftDate(Date draftDate) {
		this.draftDate = draftDate;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Date getDelDate() {
		return delDate;
	}

	public void setDelDate(Date delDate) {
		this.delDate = delDate;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public Date getInvalidDate() {
		return invalidDate;
	}

	public void setInvalidDate(Date invalidDate) {
		this.invalidDate = invalidDate;
	}

	public Date getBasisInvalidDate() {
		return basisInvalidDate;
	}

	public void setBasisInvalidDate(Date basisInvalidDate) {
		this.basisInvalidDate = basisInvalidDate;
	}

	public Integer getValidDate() {
		return validDate;
	}

	public void setValidDate(Integer validDate) {
		this.validDate = validDate;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public FileStatus getStatus() {
		return status;
	}

	public void setStatus(FileStatus status) {
		this.status = status;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public String getPublishNo() {
		return publishNo;
	}

	public void setPublishNo(String publishNo) {
		this.publishNo = publishNo;
	}

	public String getLegalDoc() {
		return legalDoc;
	}

	public void setLegalDoc(String legalDoc) {
		this.legalDoc = legalDoc;
	}

	public String getDraftInstruction() {
		return draftInstruction;
	}

	public void setDraftInstruction(String draftInstruction) {
		this.draftInstruction = draftInstruction;
	}

	public String getLegalBasis() {
		return legalBasis;
	}

	public void setLegalBasis(String legalBasis) {
		this.legalBasis = legalBasis;
	}

	public String getLegalBasisNoAtta() {
		return legalBasisNoAtta;
	}

	public void setLegalBasisNoAtta(String legalBasisNoAtta) {
		this.legalBasisNoAtta = legalBasisNoAtta;
	}

	public String getInvalidReason() {
		return invalidReason;
	}

	public void setInvalidReason(String invalidReason) {
		this.invalidReason = invalidReason;
	}

	public String getMoreFiles() {
		return moreFiles;
	}

	public void setMoreFiles(String moreFiles) {
		this.moreFiles = moreFiles;
	}

	public OrgShortVo getApplyUnit() {
		return applyUnit;
	}

	public void setApplyUnit(OrgShortVo applyUnit) {
		this.applyUnit = applyUnit;
	}

	public String getUnionDrtUnitName() {
		return unionDrtUnitName;
	}

	public void setUnionDrtUnitName(String unionDrtUnitName) {
		this.unionDrtUnitName = unionDrtUnitName;
	}

	public String getUnionDrtUnitLeaderName() {
		return unionDrtUnitLeaderName;
	}

	public void setUnionDrtUnitLeaderName(String unionDrtUnitLeaderName) {
		this.unionDrtUnitLeaderName = unionDrtUnitLeaderName;
	}

	public String getInvolvedOrgesName() {
		return involvedOrgesName;
	}

	public void setInvolvedOrgesName(String involvedOrgesName) {
		this.involvedOrgesName = involvedOrgesName;
	}

	public static NormativeFileVo createVo(NormativeFile normativeFile) {
		NormativeFileVo normativeFileVo = new NormativeFileVo();
		normativeFileVo.setApplyDate(normativeFile.getApplyDate());
		normativeFileVo.setBasisInvalidDate(normativeFile.getBasisInvalidDate());
		normativeFileVo.setDecUnit(OrgShortVo.createVo(normativeFile.getDecUnit()));
		normativeFileVo.setDecUnitClerk(UserShortVo.createVo(normativeFile.getDecUnitClerk()));
		normativeFileVo.setDecUnitLeader(UserShortVo.createVo(normativeFile.getDecUnitLeader()));
		normativeFileVo.setDraftDate(normativeFile.getDraftDate());
		normativeFileVo.setDraftInstruction(normativeFile.getDraftInstruction());
		normativeFileVo.setDrtUnit(OrgShortVo.createVo(normativeFile.getDrtUnit()));
		normativeFileVo.setDrtUnitClerk(UserShortVo.createVo(normativeFile.getDrtUnitClerk()));
		normativeFileVo.setDrtUnitLeader(UserShortVo.createVo(normativeFile.getDrtUnitLeader()));
		normativeFileVo.setPublishNo(normativeFile.getPublishNo());
		normativeFileVo.setId(normativeFile.getId());
		normativeFileVo.setInvolvedOrges(normativeFile.getInvolvedOrges());
		normativeFileVo.setInvalidDate(normativeFile.getInvalidDate());
		normativeFileVo.setInvalidReason(normativeFile.getInvalidReason());
		normativeFileVo.setLegalBasis(normativeFile.getLegalBasis());
		normativeFileVo.setLegalBasisNoAtta(normativeFile.getLegalBasisNoAtta());
		normativeFileVo.setLegalDoc(normativeFile.getLegalDoc());
		normativeFileVo.setMoreFiles(normativeFile.getMoreFiles());
		normativeFileVo.setName(normativeFile.getName());
		normativeFileVo.setPriority(normativeFile.getPriority());
		normativeFileVo.setPublishDate(normativeFile.getPublishDate());
		normativeFileVo.setRecRevUnit(OrgShortVo.createVo(normativeFile.getRecRevUnit()));
		normativeFileVo.setRecRevUnitClerk(UserShortVo.createVo(normativeFile.getRecRevUnitClerk()));
		normativeFileVo.setRecRevUnitLeader(UserShortVo.createVo(normativeFile.getRecRevUnitLeader()));
		normativeFileVo.setRegisterCode(normativeFile.getRegisterCode());
		normativeFileVo.setRegisterDate(normativeFile.getRegisterDate());
		normativeFileVo.setRevUnit(OrgShortVo.createVo(normativeFile.getRevUnit()));
		normativeFileVo.setRevUnitClerk(UserShortVo.createVo(normativeFile.getRevUnitClerk()));
		normativeFileVo.setRevUnitLeader(UserShortVo.createVo(normativeFile.getRevUnitLeader()));
		normativeFileVo.setStage(normativeFile.getStage());
		normativeFileVo.setStatus(normativeFile.getStatus());
		normativeFileVo.setUnionDrtUnit(normativeFile.getUnionDrtUnit());
		normativeFileVo.setUnionDrtUnitClerk(normativeFile.getUnionDrtUnitClerk());
		normativeFileVo.setUnionDrtUnitLeader(normativeFile.getUnionDrtUnitLeader());
		normativeFileVo.setValidDate(normativeFile.getValidDate());
		normativeFileVo.setDelUnit(normativeFile.getDelUnit());
		normativeFileVo.setDelDate(normativeFile.getDelDate());
		normativeFileVo.setRequestDate(normativeFile.getRequestDate());
		normativeFileVo.setApplyUnit(OrgShortVo.createVo(normativeFile.getApplyUnit()));
		normativeFileVo.setDocNo(normativeFile.getDocNo());
		return normativeFileVo;
	}

	public static List<NormativeFileVo> createVoList(List<NormativeFile> normativeFileList) {// 将实体集合转换为Vo集合
		List<NormativeFileVo> normativeFileVoList = new ArrayList<NormativeFileVo>();
		for (NormativeFile normativeFile : normativeFileList) {// 将SetDelRequestList集合里面的SetDelRequest一个一个的遍历出来
			normativeFileVoList.add(NormativeFileVo.createVo(normativeFile));// 调用上面的createVo方法，将遍历来的SetDelRequest转换为SetDelRequestVo对象，并添加到SetDelRequestVoList
		}
		return normativeFileVoList;
	}
}
