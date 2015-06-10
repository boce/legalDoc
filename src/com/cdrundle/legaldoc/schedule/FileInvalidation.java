package com.cdrundle.legaldoc.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.enums.FileStatus;
import com.cdrundle.legaldoc.vo.ScheduleJob;


/**
 * 文件自动失效
 * @author xiaokui.li
 * 
 */
@Component
public class FileInvalidation implements Job
{
	private static final Log log = LogFactory.getLog(FileInvalidation.class);
	
	@Autowired
	INormativeFileDao normativeFileDao;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
        ScheduleJob scheduleJob = (ScheduleJob)context.getMergedJobDataMap().get("scheduleJob");
        log.info("任务名称 = [" + scheduleJob.getJobName() + "]");
        //根据规范性文件id设置文件状态为失效
        normativeFileDao.updateLegalBasisAtta(Long.valueOf(scheduleJob.getParams().get("norId").toString()), FileStatus.INVALID);
        log.info("任务运行成功");
	}
	
}
