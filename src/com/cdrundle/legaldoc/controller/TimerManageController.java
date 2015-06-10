package com.cdrundle.legaldoc.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cdrundle.legaldoc.vo.ScheduleJob;

/**
 * 定时管理 
 * Created on 2014-5-20-下午09:58:07
 * @author xiaokui.li
 */
@Controller
@RequestMapping("/timerManage")
public class TimerManageController {
	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	private ScheduleJob getExampleData() {
		ScheduleJob job = new ScheduleJob();
		job.setJobId("10001" + 1);
		job.setJobName("data_import" + 1);
		job.setJobGroup("dataWork");
		job.setJobStatus("1");
		job.setCronExpression("0/5 * * * * ?");
		job.setDescription("数据导入任务");
		return job;
	}

	/**
	 * 添加任务
	 * 
	 * @param request
	 * @param response
	 * @param job
	 * @throws SchedulerException
	 */
	@RequestMapping("/addOrUpdateJob")
	public void addOrUpdateJob(HttpServletRequest request, HttpServletResponse response, ScheduleJob job) throws SchedulerException {
		job = getExampleData();
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		if (null == trigger) {
			JobDetail jobDetail = JobBuilder.newJob(job.getJobClass()).withIdentity(job.getJobName(), job.getJobGroup())
					.storeDurably(job.isDurable()).requestRecovery(job.isRequestsRecovery()).build();
			jobDetail.getJobDataMap().put("scheduleJob", job);
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
	 * @param request
	 * @param response
	 * @param job
	 * @throws SchedulerException
	 */
	@RequestMapping("/startJob")
	public void startJob(HttpServletRequest request, HttpServletResponse response, ScheduleJob job) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		scheduler.scheduleJob(trigger);
	}

	/**
	 * 查询计划中的任务
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws SchedulerException
	 * @throws Exception
	 */
	@RequestMapping("/showJob")
	public String showJob(HttpServletRequest request, HttpServletResponse response) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
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
		request.setAttribute("jobList", jobList);
		return "/showJob.wf";
	}

	/**
	 * 暂停任务
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws SchedulerException
	 */
	@RequestMapping("/pauseJob")
	public void pauseJob(HttpServletRequest request, HttpServletResponse response, ScheduleJob scheduleJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		scheduler.pauseJob(jobKey);
	}

	/**
	 * 恢复任务
	 * 
	 * @param request
	 * @param response
	 * @param scheduleJob
	 * @return
	 * @throws SchedulerException
	 */
	@RequestMapping("/resumeJob")
	public void resumeJob(HttpServletRequest request, HttpServletResponse response, ScheduleJob scheduleJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		scheduler.resumeJob(jobKey);
	}

	/**
	 * 删除任务
	 * 
	 * @param request
	 * @param response
	 * @param scheduleJob
	 * @return
	 * @throws SchedulerException
	 */
	@RequestMapping("/deleteJob")
	public void deleteJob(HttpServletRequest request, HttpServletResponse response, ScheduleJob scheduleJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		scheduler.pauseTrigger(triggerKey);// 停止触发器
		scheduler.unscheduleJob(triggerKey);// 移除触发器
		scheduler.deleteJob(jobKey);// 删除任务
	}

	/**
	 * 立即运行一次
	 * 
	 * @param request
	 * @param response
	 * @param scheduleJob
	 * @return
	 * @throws SchedulerException
	 */
	@RequestMapping("/runOnce")
	public void runOnce(HttpServletRequest request, HttpServletResponse response, ScheduleJob scheduleJob) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
		scheduler.triggerJob(jobKey);
	}
}
