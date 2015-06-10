
package com.cdrundle.legaldoc.vo;

import java.util.Date;

import com.cdrundle.legaldoc.enums.FileStatus;

/**
 * @author  gang.li
 *
 * 子文件清理Vo
 */
public class FileCleanUpLineVo {
	
	private long id;

	private String name;
	
	private OrgShortVo decisionUnit;
	
	private String publishNo;
	
	private Date publishDate;
	
	private FileStatus cleanupResult;
	
	private String remark;
	
	private  FileCleanupVo  fileCleanupVo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OrgShortVo getDecisionUnit() {
		return decisionUnit;
	}

	public void setDecisionUnit(OrgShortVo decisionUnit) {
		this.decisionUnit = decisionUnit;
	}

	public String getPublishNo() {
		return publishNo;
	}

	public void setPublishNo(String publishNo) {
		this.publishNo = publishNo;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public FileStatus getCleanupResult() {
		return cleanupResult;
	}

	public void setCleanupResult(FileStatus cleanupResult) {
		this.cleanupResult = cleanupResult;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public FileCleanupVo getFileCleanupVo() {
		return fileCleanupVo;
	}

	public void setFileCleanupVo(FileCleanupVo fileCleanupVo) {
		this.fileCleanupVo = fileCleanupVo;
	}

	public FileCleanUpLineVo() {
		
	}
	
	public FileCleanUpLineVo(long id, String name, OrgShortVo decisionUnit, String publishNo, 
			Date publishDate, FileStatus cleanupResult, String remark, FileCleanupVo fileCleanupVo) {
		this.id = id; 
		this.name = name;
		this.decisionUnit = decisionUnit;
		this.publishNo = publishNo; 
		this.publishDate = publishDate;
		this.cleanupResult = cleanupResult;
		this.remark = remark;
		this.fileCleanupVo = fileCleanupVo;
	}
	
}
