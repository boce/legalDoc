
package com.cdrundle.legaldoc.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;
import com.cdrundle.legaldoc.enums.FileStatus;

/**
 * @author  gang.li
 *
 * 文件清理实体
 */
@Entity
@Table(name = "implmgt_file_cleanup_line")
@Cache(usage =  CacheConcurrencyStrategy.READ_WRITE)
public class FileCleanUpLine  extends  LongIdEntity {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 名称
	 */
	@Column(name = "name", nullable = false)
	private String name;
	
	/**
	 * 制定单位
	 */
	@ManyToOne
	@JoinColumn(name = "dec_unit", nullable = false)
	private Organization decisionUnit;
	
	/**
	 * 发文号
	 */
	@Column(name = "publish_no", nullable = false)
	private String publishNo;
	
	/**
	 * 发布日期
	 */
	@Column(name = "publish_date", nullable = false)
	private Date publishDate;
	
	/**
	 * 清理结果
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "cleanup_result", nullable = false)
	private FileStatus cleanupResult;
	
	/**
	 * 备注
	 */
	@Column(name = "remark")
	private String remark;
	
	/**
	 * 文件清理文件
	 */
	@ManyToOne
	@JoinColumn(name = "file_cleanup")
	private  FileCleanUp  fileCleanup;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Organization getDecisionUnit() {
		return decisionUnit;
	}

	public void setDecisionUnit(Organization decisionUnit) {
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

	public FileCleanUp getFileCleanup() {
		return fileCleanup;
	}

	public void setFileCleanup(FileCleanUp fileCleanup) {
		this.fileCleanup = fileCleanup;
	}
}
