package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.cdrundle.legaldoc.entity.ProtocolDeliberation;

/**
 * @author XuBao
 * 
 *         2014年6月10日
 */
public class ProtocolDeliberationVo {

	/**
	 * 草案审议ID
	 */
	private Long id;

	/**
	 * 草案审议名
	 */
	private String name;

	/**
	 * 规范性文件
	 */
	private NormativeFileVo normativeFile;

	/**
	 * 起草单位
	 */
	private OrgShortVo draftingUnit;

	/**
	 * 起草单位负责人
	 */
	private UserShortVo draftingUnitLeader;

	/**
	 * 起草单位经办员
	 */
	private UserShortVo draftingUnitClerk;

	/**
	 * 审议单位
	 */
	private String deliberationUnit;

	/**
	 * 审议日期
	 */
	private Date deliberationDate;

	/**
	 * 草案
	 */
	private String protocol;

	/**
	 * 起草说明
	 */
	private String draftingInstruction;

	/**
	 * 征求意见
	 */
	private String requestComments;

	/**
	 * 相关法律材料
	 */
	private String reviewComments;

	/**
	 * 审议请示
	 */
	private String reviewInstruction;

	/**
	 * 审议意见
	 */
	private String deliberationComment;

	/**
	 * 是否需要修改草案
	 */
	@Column(name = "is_need_modify", nullable = false)
	private Boolean isNeedModify;
	
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

	public String getDeliberationUnit() {
		return deliberationUnit;
	}

	public void setDeliberationUnit(String deliberationUnit) {
		this.deliberationUnit = deliberationUnit;
	}

	public Date getDeliberationDate() {
		return deliberationDate;
	}

	public void setDeliberationDate(Date deliberationDate) {
		this.deliberationDate = deliberationDate;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getDraftingInstruction() {
		return draftingInstruction;
	}

	public void setDraftingInstruction(String draftingInstruction) {
		this.draftingInstruction = draftingInstruction;
	}

	public String getRequestComments() {
		return requestComments;
	}

	public void setRequestComments(String requestComments) {
		this.requestComments = requestComments;
	}

	public String getReviewComments() {
		return reviewComments;
	}

	public void setReviewComments(String reviewComments) {
		this.reviewComments = reviewComments;
	}

	public String getReviewInstruction() {
		return reviewInstruction;
	}

	public void setReviewInstruction(String reviewInstruction) {
		this.reviewInstruction = reviewInstruction;
	}

	public String getDeliberationComment() {
		return deliberationComment;
	}

	public void setDeliberationComment(String deliberationComment) {
		this.deliberationComment = deliberationComment;
	}

	public Boolean getIsNeedModify() {
		return isNeedModify;
	}

	public void setIsNeedModify(Boolean isNeedModify) {
		this.isNeedModify = isNeedModify;
	}

	public static ProtocolDeliberationVo createVo(ProtocolDeliberation protocolDeliberation) {// 将实体对象转换为Vo对象
		ProtocolDeliberationVo protocolDeliberationVo = new ProtocolDeliberationVo();
		if (protocolDeliberation == null) {
			return protocolDeliberationVo;
		}
		protocolDeliberationVo.setId(protocolDeliberation.getId());
		protocolDeliberationVo.setDeliberationComment(protocolDeliberation.getDeliberationComment());
		protocolDeliberationVo.setDeliberationDate(protocolDeliberation.getDeliberationDate());
		protocolDeliberationVo.setDeliberationUnit(protocolDeliberation.getDeliberationUnit());
		protocolDeliberationVo.setDraftingInstruction(protocolDeliberation.getDraftingInstruction());
		protocolDeliberationVo.setDraftingUnit(OrgShortVo.createVo(protocolDeliberation.getDraftingUnit()));
		protocolDeliberationVo.setDraftingUnitClerk(UserShortVo.createVo(protocolDeliberation.getDraftingUnitClerk()));
		protocolDeliberationVo.setDraftingUnitLeader(UserShortVo.createVo(protocolDeliberation.getDraftingUnitLeader()));
		protocolDeliberationVo.setName(protocolDeliberation.getName());
		protocolDeliberationVo.setNormativeFile(NormativeFileVo.createVo(protocolDeliberation.getNormativeFile()));
		protocolDeliberationVo.setProtocol(protocolDeliberation.getProtocol());
		protocolDeliberationVo.setRequestComments(protocolDeliberation.getRequestComments());
		protocolDeliberationVo.setReviewInstruction(protocolDeliberation.getReviewInstruction());
		protocolDeliberationVo.setReviewComments(protocolDeliberation.getReviewComments());
		protocolDeliberationVo.setIsNeedModify(protocolDeliberation.getIsNeedModify());
		return protocolDeliberationVo;

	}

	public static List<ProtocolDeliberationVo> createVoList(List<ProtocolDeliberation> protocolDeliberationList) {// 将实体集合转换为Vo集合
		List<ProtocolDeliberationVo> protocolDeliberationVoList = new ArrayList<ProtocolDeliberationVo>();
		for (ProtocolDeliberation protocolDeliberation : protocolDeliberationList) {// 将SetDelRequestList集合里面的SetDelRequest一个一个的遍历出来
			protocolDeliberationVoList.add(ProtocolDeliberationVo.createVo(protocolDeliberation));
		}
		return protocolDeliberationVoList;
	}
}
