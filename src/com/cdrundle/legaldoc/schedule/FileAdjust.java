package com.cdrundle.legaldoc.schedule;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.ITaskDao;
import com.cdrundle.legaldoc.entity.Task;
import com.cdrundle.legaldoc.enums.TaskStatus;
import com.cdrundle.legaldoc.enums.TaskType;
import com.cdrundle.legaldoc.vo.ScheduleJob;


/**
 * 期满评估提醒
 * @author xiaokui.li
 * 
 */
@Component
public class FileAdjust implements Job
{
	private static final Log log = LogFactory.getLog(FileAdjust.class);
	
	@Autowired
	INormativeFileDao normativeFileDao;
	@Autowired
	ITaskDao taskDao;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
        ScheduleJob scheduleJob = (ScheduleJob)context.getMergedJobDataMap().get("scheduleJob");
        log.info("任务名称 = [" + scheduleJob.getJobName() + "]");
        Long norId = Long.valueOf(scheduleJob.getParams().get("norId").toString());
        String name = scheduleJob.getParams().get("name").toString();
        Task task = new Task();
        task.setTaskName(name + "-期满评估提醒");
        task.setCreateDate(new Date());
        task.setTaskType(TaskType.FILEADJUST);
        task.setTaskStatus(TaskStatus.RUNNING);
        task.setNormativeFile(normativeFileDao.findOne(norId));
        taskDao.save(task);
        log.info("任务运行成功");
	}
	
}
