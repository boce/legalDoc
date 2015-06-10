package com.cdrundle.legaldoc.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.cdrundle.legaldoc.base.LongIdEntity;
import com.cdrundle.legaldoc.enums.TaskStatus;
import com.cdrundle.legaldoc.enums.TaskType;

/**
 * 待办事项
 * @author xiaokui.li
 *
 */
@Entity
@Table(name = "set_task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Task extends LongIdEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 任务名称
	 */
	@Column(name="task_name", nullable=false)
	private String taskName;
	
	/**
	 * 创建时间
	 */
	@Column(name="create_date", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date createDate;
	
	/**
	 * 状态
	 */
	@Enumerated(EnumType.STRING)
	@Column(name="task_status", nullable=false)
	private TaskStatus taskStatus;
	
	/**
	 * 任务类型
	 */
	@Enumerated(EnumType.STRING)
	@Column(name="task_type", nullable=false)
	private TaskType taskType;
	
	/**
	 * 规范性文件
	 */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "nor_id", nullable = false)
	private NormativeFile normativeFile;
	
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public NormativeFile getNormativeFile() {
		return normativeFile;
	}

	public void setNormativeFile(NormativeFile normativeFile) {
		this.normativeFile = normativeFile;
	}
}
