package com.cdrundle.legaldoc.vo;

import java.io.Serializable;
import java.util.Map;

import org.quartz.Job;

/**
 * 计划任务信息
 * @author xiaokui.li
 *
 */
/**
 * @author xiaokui.li
 *
 */
public class ScheduleJob implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 任务id */
	private String jobId;
	
	/** 任务名称 */
	private String jobName;
	
	/** 任务分组 */
	private String jobGroup;
	
	/**
	 * 触发器状态
	 * None：Trigger已经完成，且不会在执行，或者找不到该触发器，或者Trigger已经被删除
	 * NORMAL:正常状态
	 * PAUSED：暂停状态
	 * COMPLETE：触发器完成，但是任务可能还正在执行中
	 * BLOCKED：线程阻塞状态
	 * ERROR：出现错误
	*/
	private String jobStatus;
	
	/** 任务运行时间表达式 */
	private String cronExpression;
	
	/** 任务描述 */
	private String description;
	
	/**
	 * 是否持久化
	 */
	private boolean durable = true;

	/**
	 * 调度器发生硬停止后，当调度器再次启动的时候是否重新执行
	 */
	private boolean isRequestsRecovery = true;
	
	/**
	 * 定时任务执行的类
	 */
	private Class<? extends Job> jobClass;
	
	/**
	 * 参数
	 */
	private Map<String, Object> params;
	
	public String getJobId()
	{
		return jobId;
	}

	public void setJobId(String jobId)
	{
		this.jobId = jobId;
	}

	public String getJobName()
	{
		return jobName;
	}

	public void setJobName(String jobName)
	{
		this.jobName = jobName;
	}

	public String getJobGroup()
	{
		return jobGroup;
	}

	public void setJobGroup(String jobGroup)
	{
		this.jobGroup = jobGroup;
	}

	public String getJobStatus()
	{
		return jobStatus;
	}

	public void setJobStatus(String jobStatus)
	{
		this.jobStatus = jobStatus;
	}

	public String getCronExpression()
	{
		return cronExpression;
	}

	public void setCronExpression(String cronExpression)
	{
		this.cronExpression = cronExpression;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public boolean isDurable()
	{
		return durable;
	}

	public void setDurable(boolean durable)
	{
		this.durable = durable;
	}

	public boolean isRequestsRecovery() {
		return isRequestsRecovery;
	}

	public void setRequestsRecovery(boolean isRequestsRecovery) {
		this.isRequestsRecovery = isRequestsRecovery;
	}

	public Class<? extends Job> getJobClass() {
		return jobClass;
	}

	public void setJobClass(Class<? extends Job> jobClass) {
		this.jobClass = jobClass;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

}
