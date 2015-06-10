
package com.cdrundle.legaldoc.vo;

import java.util.Date;

import com.cdrundle.legaldoc.enums.FileStatus;

/**
 * @author  gang.li
 *
 * 文件清理文件VO
 */
public class FileCleanupVo {

	private Long id;
	
	private  FileStatus  status;
	
	private OrgShortVo cleanupUnit;
	
	private  UserShortVo  cleanupUnitLeader;
	
	private  UserShortVo  cleanupUnitClerk;
	
	private OrgShortVo approvalUnit;
	
	private  UserShortVo  approvalUnitLeader;
	
	private  UserShortVo  approvalUnitClerk;
	
	private String mainLeaders;
	
	private Date cleanupDate;
	
	public FileCleanupVo(){
		
	}
	
	public FileCleanupVo(Long id, FileStatus  status, OrgShortVo cleanupUnit, UserShortVo  cleanupUnitLeader, 
			UserShortVo  cleanupUnitClerk, OrgShortVo approvalUnit, UserShortVo  approvalUnitLeader, 
			UserShortVo  approvalUnitClerk, String mainLeaders,Date cleanupDate) {
		this.id = id;
		this.status = status;
		this.cleanupUnit = cleanupUnit;
		this.cleanupUnitLeader = cleanupUnitLeader;
		this.cleanupUnitClerk = cleanupUnitClerk;
		this.approvalUnit = approvalUnit;
		this.approvalUnitLeader = approvalUnitLeader;
		this.approvalUnitClerk = approvalUnitClerk;
		this.mainLeaders = mainLeaders;
		this.cleanupDate = cleanupDate;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FileStatus getStatus() {
		return status;
	}

	public void setStatus(FileStatus status) {
		this.status = status;
	}
	public OrgShortVo getCleanupUnit() {
		return cleanupUnit;
	}
	public void setCleanupUnit(OrgShortVo cleanupUnit) {
		this.cleanupUnit = cleanupUnit;
	}
	public UserShortVo getCleanupUnitLeader() {
		return cleanupUnitLeader;
	}
	public void setCleanupUnitLeader(UserShortVo cleanupUnitLeader) {
		this.cleanupUnitLeader = cleanupUnitLeader;
	}
	public UserShortVo getCleanupUnitClerk() {
		return cleanupUnitClerk;
	}
	public void setCleanupUnitClerk(UserShortVo cleanupUnitClerk) {
		this.cleanupUnitClerk = cleanupUnitClerk;
	}
	public OrgShortVo getApprovalUnit() {
		return approvalUnit;
	}
	public void setApprovalUnit(OrgShortVo approvalUnit) {
		this.approvalUnit = approvalUnit;
	}
	public UserShortVo getApprovalUnitLeader() {
		return approvalUnitLeader;
	}
	public void setApprovalUnitLeader(UserShortVo approvalUnitLeader) {
		this.approvalUnitLeader = approvalUnitLeader;
	}
	public UserShortVo getApprovalUnitClerk() {
		return approvalUnitClerk;
	}
	public void setApprovalUnitClerk(UserShortVo approvalUnitClerk) {
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
