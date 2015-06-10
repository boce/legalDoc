package com.cdrundle.legaldoc.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;

/**
 * @author XuBao 草案修改 2014年6月10日
 */
@Entity
@Table(name = "set_protocol_modify")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProtocolModify extends LongIdEntity {

	/**
					 * 
					 */
	private static final long serialVersionUID = 1L;

	/**
	 * 文件名称
	 */
	@Column(nullable = false,length = 200)
	private String name;

	/**
	 * 规范性文件
	 */
	@OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name = "nor_file",nullable = false)
	private NormativeFile normativeFile;

	/**
	 * 主起草单位
	 */
	@ManyToOne
	@JoinColumn(name = "dra_unit",nullable = false)
	private Organization draftingUnit;

	/**
	 * 主起草单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "dra_unit_leader",nullable = false)
	private User draftingUnitLeader;

	/**
	 * 主起草单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "dra_unit_clerk",nullable = false)
	private User draftingUnitClerk;

	/**
	 * 审议意见
	 */
	@Column(name = "del_comment",nullable = false)
	private String deliberationComment;

	/**
	 * 内容
	 */
	@Column(nullable = false)
	private String content;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NormativeFile getNormativeFile() {
		return normativeFile;
	}

	public void setNormativeFile(NormativeFile normativeFile) {
		this.normativeFile = normativeFile;
	}

	public Organization getDraftingUnit() {
		return draftingUnit;
	}

	public void setDraftingUnit(Organization draftingUnit) {
		this.draftingUnit = draftingUnit;
	}

	public User getDraftingUnitLeader() {
		return draftingUnitLeader;
	}

	public void setDraftingUnitLeader(User draftingUnitLeader) {
		this.draftingUnitLeader = draftingUnitLeader;
	}

	public User getDraftingUnitClerk() {
		return draftingUnitClerk;
	}

	public void setDraftingUnitClerk(User draftingUnitClerk) {
		this.draftingUnitClerk = draftingUnitClerk;
	}

	public String getDeliberationComment() {
		return deliberationComment;
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

}
