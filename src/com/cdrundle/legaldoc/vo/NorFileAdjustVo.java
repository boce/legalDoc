package com.cdrundle.legaldoc.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.enums.FileStatus;

public class NorFileAdjustVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private OrgShortVo decUnit;
	private UserShortVo decUnitClerk;
	private UserShortVo decUnitLeader;
	private OrgShortVo drtUnit;
	private UserShortVo drtUnitClerk;
	private UserShortVo drtUnitLeader;
	private FileStatus status;
	private String legalDoc;
	private String draftInstruction;
	private String legalBasis;
	private String invalidReason;

	
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

	public FileStatus getStatus() {
		return status;
	}

	public void setStatus(FileStatus status) {
		this.status = status;
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

	public String getInvalidReason() {
		return invalidReason;
	}

	public void setInvalidReason(String invalidReason) {
		this.invalidReason = invalidReason;
	}

	public static NorFileAdjustVo createVo(NormativeFile normativeFile) {
		NorFileAdjustVo norFileAdjustVo = new NorFileAdjustVo();
		norFileAdjustVo.setDecUnit(OrgShortVo.createVoNoChild(normativeFile
				.getDecUnit()));
		norFileAdjustVo.setDecUnitClerk(UserShortVo.createVo(normativeFile
				.getDecUnitClerk()));
		norFileAdjustVo.setDecUnitLeader(UserShortVo.createVo(normativeFile
				.getDecUnitLeader()));
		norFileAdjustVo
				.setDraftInstruction(normativeFile.getDraftInstruction());
		norFileAdjustVo.setDrtUnit(OrgShortVo.createVoNoChild(normativeFile
				.getDrtUnit()));
		norFileAdjustVo.setDrtUnitClerk(UserShortVo.createVo(normativeFile
				.getDrtUnitClerk()));
		norFileAdjustVo.setDrtUnitLeader(UserShortVo.createVo(normativeFile
				.getDrtUnitLeader()));
		norFileAdjustVo.setId(normativeFile.getId());
		norFileAdjustVo.setInvalidReason(normativeFile.getInvalidReason());
		norFileAdjustVo.setLegalBasis(normativeFile.getLegalBasis());
		norFileAdjustVo.setLegalDoc(normativeFile.getLegalDoc());
		norFileAdjustVo.setName(normativeFile.getName());
		norFileAdjustVo.setStatus(normativeFile.getStatus());
		return norFileAdjustVo;
	}

	public static List<NorFileAdjustVo> createVoList(
			List<NormativeFile> normativeFileList) {
		// 将实体集合转换为Vo集合
		List<NorFileAdjustVo> norFileAdjustVoList = new ArrayList<NorFileAdjustVo>();
		for (NormativeFile normativeFile : normativeFileList) {
			// 将SetDelRequestList集合里面的SetDelRequest一个一个的遍历出来
			norFileAdjustVoList.add(NorFileAdjustVo.createVo(normativeFile));
			// 调用上面的createVo方法，将遍历来的SetDelRequest转换为SetDelRequestVo对象，并添加到SetDelRequestVoList
		}
		return norFileAdjustVoList;
	}

}
