package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.List;

import com.cdrundle.legaldoc.entity.ProtocolModify;

/**
 * @author XuBao
 * 
 *         2014年6月10日
 */
public class ProtocolModifyVo {

	private Long id;
	private String name;
	private NormativeFileVo normativeFile;
	private OrgShortVo draftingUnit;
	private UserShortVo draftingUnitLeader;
	private UserShortVo draftingUnitClerk;
	private String deliberationComment;
	private String content;

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

	public String getDeliberationComment() {
		return deliberationComment;
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

	public void setDeliberationComment(String deliberationComment) {
		this.deliberationComment = deliberationComment;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public static ProtocolModifyVo createVo(ProtocolModify protocolModify) {
		ProtocolModifyVo protocolModifyVo = new ProtocolModifyVo();
		if (protocolModify == null) {
			return protocolModifyVo;
		}
		protocolModifyVo.setId(protocolModify.getId());
		protocolModifyVo.setContent(protocolModify.getContent());
		protocolModifyVo.setDeliberationComment(protocolModify.getDeliberationComment());
		protocolModifyVo.setDraftingUnit(OrgShortVo.createVo(protocolModify.getDraftingUnit()));
		protocolModifyVo.setDraftingUnitClerk(UserShortVo.createVo(protocolModify.getDraftingUnitClerk()));
		protocolModifyVo.setDraftingUnitLeader(UserShortVo.createVo(protocolModify.getDraftingUnitLeader()));
		protocolModifyVo.setName(protocolModify.getName());
		protocolModifyVo.setNormativeFile(NormativeFileVo.createVo(protocolModify.getNormativeFile()));
		return protocolModifyVo;
	}

	public static List<ProtocolModifyVo> creatVoList(List<ProtocolModify> protocolModifyList) {
		List<ProtocolModifyVo> protocolModifyVoList = new ArrayList<ProtocolModifyVo>();
		for (ProtocolModify protocolModify : protocolModifyList) {
			protocolModifyVoList.add(ProtocolModifyVo.createVo(protocolModify));
		}
		return protocolModifyVoList;
	}

}
