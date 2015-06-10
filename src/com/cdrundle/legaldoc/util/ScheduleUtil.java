package com.cdrundle.legaldoc.util;

import java.util.ArrayList;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import com.cdrundle.legaldoc.vo.ScheduleJob;

/**
 * 定时任务工具类
 * 
 * @author xiaokui.li
 * 
 */
public class ScheduleUtil {

	/**
	 * 添加或者修改任务
	 * 
	 * @param scheduler
	 * @param job
	 * @throws SchedulerException
	 */
	public static void addOrUpdateJob(Scheduler scheduler, ScheduleJob job) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		if (null == trigger) {
			JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
			boolean exists = scheduler.checkExists(jobKey);
			if(exists){
				scheduler.deleteJob(jobKey);
			}
			JobDetail jobDetail = JobBuilder.newJob(job.getJobClass()).withIdentity(job.getJobName(), job.getJobGroup())
					.storeDurably(job.isDurable()).requestRecovery(job.isRequestsRecovery()).withDescription(job.getDescription()).build();
			jobDetail.getJobDataMap().put("scheduleJob", job);
			if (job.getParams() != null && !job.getParams().isEmpty()) {
				jobDetail.getJobDataMap().put("params", job.getParams());
			}
			// 表达式调度构建器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
			// 按新的cronExpression表达式构建一个新的trigger
			trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(scheduleBuilder).build();
			scheduler.scheduleJob(jobDetail, trigger);
		} else {
			// Trigger已存在，那么更新相应的定时设置
			// 表达式调度构建器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
			// 按新的cronExpression表达式重新构建trigger
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
			// 按新的trigger重新设置job执行
			scheduler.rescheduleJob(triggerKey, trigger);
		}
	}

	/**
	 * 开始任务
	 * 
	 * @param scheduler
	 * @param job
	 * @throws SchedulerException
	 */
	public static void startJob(Scheduler scheduler, ScheduleJob job) throws SchedulerException {
		TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		scheduler.scheduleJob(trigger);
	}

	/**
	 * 查询计划中的任务
	 * 
	 * @param scheduler
	 * @throws SchedulerException
	 */
	public static List<ScheduleJob> showJob(Scheduler scheduler) throws SchedulerException {
		List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
		for (String groupName : scheduler.getJobGroupNames()) {
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				JobDetail jobDetail = scheduler.getJobDetail(jobKey);
				List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
				for (Trigger trigger : triggers) {
					ScheduleJob job = new ScheduleJob();
					job.setJobName(jobKey.getName());
					job.setJobGroup(jobKey.getGroup());
					job.setDescription(jobDetail.getDescription());
					Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
					job.setJobStatus(triggerState.name());
					if (trigger instanceof CronTrigger) {
						CronTrigger cronTrigger = (CronTrigger) trigger;
						String cronExpression = cronTrigger.getCronExpression();
						job.setCronExpression(cronExpression);
					}
					jobList.add(job);
				}
			}
		}
		return jobList;
	}

	/**
	 * 暂停任务
	 * 
	 * @param scheduler
	 * @param job
	 * @throws SchedulerException
	 */
	public static void pauseJob(Scheduler scheduler, ScheduleJob job) throws SchedulerException {
		JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
		scheduler.pauseJob(jobKey);
	}

	/**
	 * 恢复任务
	 * 
	 * @param job
	 * @param scheduler
	 * @throws SchedulerException
	 */
	public static void resumeJob(Scheduler scheduler, ScheduleJob job) throws SchedulerException {
		JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
		scheduler.resumeJob(jobKey);
	}

	/**
	 * 删除任务
	 * 
	 * @param job
	 * @param scheduler
	 * @throws SchedulerException
	 */
	public static void deleteJob(Scheduler scheduler, ScheduleJob job) throws SchedulerException {
		JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
		TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
		scheduler.pauseTrigger(triggerKey);// 停止触发器
		scheduler.unscheduleJob(triggerKey);// 移除触发器
		scheduler.deleteJob(jobKey);// 删除任务
	}

	/**
	 * 立即运行一次
	 * 
	 * @param job
	 * @param scheduler
	 * @throws SchedulerException
	 */
	public static void runOnce(Scheduler scheduler, ScheduleJob job) throws SchedulerException {
		JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
		scheduler.triggerJob(jobKey);
	}
}
