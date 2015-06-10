
package com.cdrundle.legaldoc.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;
import com.cdrundle.legaldoc.enums.FileStatus;

/**
 * @author  gang.li
 *
 * 文件清理文件实体
 */
@Entity
@Table(name = "implmgt_file_cleanup")
@Cache(usage =  CacheConcurrencyStrategy.READ_WRITE)
public class FileCleanUp  extends  LongIdEntity {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 状态
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private  FileStatus  status;
	
	/**
	 * 清理单位
	 */
	@ManyToOne
	@JoinColumn(name = "cleanup_unit", nullable = false)
	private Organization cleanupUnit;
	
	/**
	 * 清理单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "cleanup_unit_leader", nullable = false)
	private  User  cleanupUnitLeader;
	
	/**
	 * 清理单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "cleanup_unit_clerk", nullable = false)
	private  User  cleanupUnitClerk;
	
	/**
	 * 审核单位
	 */
	@ManyToOne
	@JoinColumn(name = "approval_unit")
	private Organization approvalUnit;
	
	/**
	 * 审核单位负责人
	 */
	@ManyToOne
	@JoinColumn(name = "approval_unit_leader")
	private  User  approvalUnitLeader;
	
	/**
	 * 审核单位经办员
	 */
	@ManyToOne
	@JoinColumn(name = "approval_unit_clerk")
	private  User  approvalUnitClerk;
	
	/**
	 * 主要审核领导
	 */
	@Column(name = "main_leaders")
	private String mainLeaders;
	
	/**
	 * 清理日期
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "cleanup_date", nullable = false)
	private Date cleanupDate;

	public FileStatus getStatus() {
		return status;
	}

	public void setStatus(FileStatus status) {
		this.status = status;
	}

	public Organization getCleanupUnit() {
		return cleanupUnit;
	}

	public void setCleanupUnit(Organization cleanupUnit) {
		this.cleanupUnit = cleanupUnit;
	}

	public User getCleanupUnitLeader() {
		return cleanupUnitLeader;
	}

	public void setCleanupUnitLeader(User cleanupUnitLeader) {
		this.cleanupUnitLeader = cleanupUnitLeader;
	}

	public User getCleanupUnitClerk() {
		return cleanupUnitClerk;
	}

	public void setCleanupUnitClerk(User cleanupUnitClerk) {
		this.cleanupUnitClerk = cleanupUnitClerk;
	}

	public Organization getApprovalUnit() {
		return approvalUnit;
	}

	public void setApprovalUnit(Organization approvalUnit) {
		this.approvalUnit = approvalUnit;
	}

	public User getApprovalUnitLeader() {
		return approvalUnitLeader;
	}

	public void setApprovalUnitLeader(User approvalUnitLeader) {
		this.approvalUnitLeader = approvalUnitLeader;
	}

	public User getApprovalUnitClerk() {
		return approvalUnitClerk;
	}

	public void setApprovalUnitClerk(User approvalUnitClerk) {
		this.approvalUnitClerk = approvalUnitClerk;
	}

	public String getMainLeaders() {
		return mainLeaders;
	}

	public void setMainLeaders(String mainLeaders) {
		this.mainLeaders = mainLeaders;
	}

	public Date getCleanupDate() {
		return cleanupDate;
	}

	public void setCleanupDate(Date cleanupDate) {
		this.cleanupDate = cleanupDate;
	}
	
}
