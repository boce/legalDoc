package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.Task;
import com.cdrundle.legaldoc.enums.TaskStatus;
import com.cdrundle.legaldoc.enums.TaskType;

/**
 * 待办事项
 * @author xiaokui.li
 *
 */
public class TaskVo{

	/**
	 * 编号
	 */
	private Long id;
	
	/**
	 * 任务名称
	 */
	private String taskName;
	
	/**
	 * 创建时间
	 */
	private Date createDate;
	
	/**
	 * 状态
	 */
	private TaskStatus taskStatus;
	
	/**
	 * 任务类型
	 */
	private TaskType taskType;
	
	/**
	 * 规范性文件
	 */
	private NormativeFileVo normativeFile;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public NormativeFileVo getNormativeFile() {
		return normativeFile;
	}

	public void setNormativeFile(NormativeFileVo normativeFile) {
		this.normativeFile = normativeFile;
	}
	
	public static TaskVo createVo(Task task){
		if(task == null){
			return null;
		}
		TaskVo vo = new TaskVo();
		vo.setId(task.getId());
		vo.setTaskName(task.getTaskName());
		vo.setCreateDate(task.getCreateDate());
		vo.setTaskStatus(task.getTaskStatus());
		vo.setTaskType(task.getTaskType());
		vo.setNormativeFile(NormativeFileVo.createVo(task.getNormativeFile()));
		return vo;
	}
	
	public static List<TaskVo> createVoList(List<Task> tasks){
		if(tasks == null){
			return null;
		}
		List<TaskVo> vos = new ArrayList<>();
		for (Iterator<Task> iterator = tasks.iterator(); iterator.hasNext();) {
			vos.add(createVo(iterator.next()));
		}
		return vos;
	}
}
