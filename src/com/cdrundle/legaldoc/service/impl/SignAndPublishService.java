package com.cdrundle.legaldoc.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.ISignAndPublishDao;
import com.cdrundle.legaldoc.dao.ITaskDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.SignAndPublish;
import com.cdrundle.legaldoc.entity.Task;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.enums.TaskStatus;
import com.cdrundle.legaldoc.enums.TaskType;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.schedule.FileAdjust;
import com.cdrundle.legaldoc.schedule.FileInvalidation;
import com.cdrundle.legaldoc.service.ISignAndPublishService;
import com.cdrundle.legaldoc.util.ScheduleUtil;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.ScheduleJob;
import com.cdrundle.legaldoc.vo.SignAndPublishVo;

/**
 * @author XuBao 签署发布 2014年6月19日
 */
@Service
public class SignAndPublishService implements ISignAndPublishService {

	@Autowired
	private ISignAndPublishDao signAndPublishDao;
	@Autowired
	private INormativeFileDao normativeFileDao;
	@Autowired
	private IOrganizationDao organizationDao;
	@Autowired
	private IUserDao userDao;
	@Autowired
	private ITaskDao taskDao;
	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	@Override
	@Transactional
	public SignAndPublishVo saveOrUpdate(SignAndPublishVo signAndPublishVo, String path, String fileName) throws ServiceException, SchedulerException {

		Long id = signAndPublishVo.getId();

		// 更新
		if (id != null) {
			SignAndPublish signAndPublish = signAndPublishDao.findOne(id);

			// 更新签署发布单
			if (signAndPublish != null) {
				Stage stage = signAndPublish.getNormativeFile().getStage();
				if (!stage.equals(Stage.PUBLISH)) {
					throw new ServiceException("已存在下游业务，不可修改");
				}
				if (signAndPublishVo.getSignLeaders() != null) {
					signAndPublish.setSignLeaders(signAndPublishVo.getSignLeaders());
					signAndPublish.setSignDate(signAndPublishVo.getSignDate());
					signAndPublish.setDecisionMakingUnit(organizationDao.findOne(signAndPublishVo.getDecisionMakingUnit().getId()));
					signAndPublish.setDecisionMakingUnitClerk(userDao.findOne(signAndPublishVo.getDecisionMakingUnitClerk().getId()));
					signAndPublish.setDecisionMakingUnitLeader(userDao.findOne(signAndPublishVo.getDecisionMakingUnitLeader().getId()));
					signAndPublish.setPublishNo(signAndPublishVo.getPublishNo());
					signAndPublish.setPublishDate(signAndPublishVo.getPublishDate());
					signAndPublish.setValidDate(signAndPublishVo.getValidDate());
					signAndPublish.setInvalidDate(signAndPublishVo.getInvalidDate());
				} else {
					throw new ServiceException("请录入签署领导！");
				}
				NormativeFile normativeFile = signAndPublish.getNormativeFile();
				// 更新规范性文件的制定单位，经办员，领导，发文号，发布日期，有效期，失效期
				if (normativeFile != null) {
					fileName = NormativeFileVo.createVo(normativeFile).getName() + "(" + SysUtil.STAGE_LEGAL_LEGALDOC + ")" + SysUtil.EXTENSION_NAME;
					normativeFile.setDecUnit(organizationDao.findOne(signAndPublishVo.getDecisionMakingUnit().getId()));
					normativeFile.setDecUnitClerk(userDao.findOne(signAndPublishVo.getDecisionMakingUnitClerk().getId()));
					normativeFile.setDecUnitLeader(userDao.findOne(signAndPublishVo.getDecisionMakingUnitLeader().getId()));
					normativeFile.setPublishNo(signAndPublishVo.getPublishNo());
					normativeFile.setPublishDate(signAndPublishVo.getPublishDate());
					normativeFile.setValidDate(signAndPublishVo.getValidDate());
					normativeFile.setInvalidDate(signAndPublishVo.getInvalidDate());
				} else {
					throw new ServiceException("数据错误，ID为：" + id);
				}
				
				Task task = taskDao.findByNorIdAndTaskType(normativeFile.getId(), TaskType.RECORDREMIND);
				if(task != null){
					task.setCreateDate(new Date());
					task.setTaskStatus(TaskStatus.RUNNING);
				}
				
				addFileInvalidJob(signAndPublishVo);
				addFileAdjust(signAndPublishVo);
				
				WordUtils.htmlToWord(path, fileName, signAndPublishVo.getLegalDoc());
				return SignAndPublishVo.createVo(signAndPublish);
			} else {
				throw new ServiceException("数据错误，ID为：" + id);
			}
		}

		// 新增保存
		SignAndPublish signAndPublish = this.coverToSingPublish(signAndPublishVo);
		NormativeFile normativeFile = normativeFileDao.findOne(signAndPublishVo.getNormativeFile().getId());
		Stage stage = normativeFile.getStage();
		if(Stage.DELIBERATION_PROTOCOL.equals(stage)){
			throw new ServiceException("保存失败，请返回草案修改！");
		}
		// 更新规范性文件的制定单位，经办员，领导，发文号，发布日期，有效期，失效期
		if (normativeFile != null) {
			fileName = NormativeFileVo.createVo(normativeFile).getName() + "(" + SysUtil.STAGE_LEGAL_LEGALDOC + ")" + SysUtil.EXTENSION_NAME;
			normativeFile.setDecUnit(organizationDao.findOne(signAndPublishVo.getDecisionMakingUnit().getId()));
			normativeFile.setDecUnitClerk(userDao.findOne(signAndPublishVo.getDecisionMakingUnitClerk().getId()));
			normativeFile.setDecUnitLeader(userDao.findOne(signAndPublishVo.getDecisionMakingUnitLeader().getId()));
			normativeFile.setPublishNo(signAndPublishVo.getPublishNo());
			normativeFile.setPublishDate(signAndPublishVo.getPublishDate());
			normativeFile.setValidDate(signAndPublishVo.getValidDate());
			normativeFile.setInvalidDate(signAndPublishVo.getInvalidDate());
			normativeFile.setLegalDoc(fileName);
			normativeFile.setStage(Stage.PUBLISH);
			normativeFileDao.save(normativeFile);
		}
		signAndPublish = signAndPublishDao.save(signAndPublish);
		
		Task task = new Task();
        task.setTaskName(signAndPublish.getName() + "-备案提醒");
        task.setCreateDate(new Date());
        task.setTaskType(TaskType.RECORDREMIND);
        task.setTaskStatus(TaskStatus.RUNNING);
        task.setNormativeFile(normativeFile);
        taskDao.save(task);
        
        addFileInvalidJob(signAndPublishVo);
		addFileAdjust(signAndPublishVo);
		
		String filePath = WordUtils.getFilePath(path, NormativeFileVo.createVo(normativeFile), Stage.PUBLISH.toString());
		WordUtils.htmlToWord(filePath, fileName, signAndPublishVo.getLegalDoc());
		return SignAndPublishVo.createVo(signAndPublish);
	}

	/**
	 * 添加文件自动失效定时任务
	 * 
	 * @param vo
	 * @throws SchedulerException
	 */
	private void addFileInvalidJob(SignAndPublishVo vo) throws SchedulerException {
		ScheduleJob job = new ScheduleJob();
		job.setJobGroup(TaskType.FILEINVALIDATION.name());
		job.setJobName(vo.getName());
		job.setJobClass(FileInvalidation.class);
		job.setDescription("文件自动失效,名称：" + vo.getName());
		Date invalidDate = vo.getInvalidDate();
		Calendar c = Calendar.getInstance();
		c.setTime(invalidDate);
		// 失效日期+1，从第二天零点开始失效
		c.add(Calendar.DATE, 1);
		String dayStr = String.valueOf(c.get(Calendar.DATE));
		String monthStr = String.valueOf(c.get(Calendar.MONTH) + 1);
		String yearStr = String.valueOf(c.get(Calendar.YEAR));
		job.setCronExpression("0 0 0 " + dayStr + " " + monthStr + " ? " + yearStr);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("norId", vo.getNormativeFile().getId());
		job.setParams(params);
		ScheduleUtil.addOrUpdateJob(schedulerFactoryBean.getScheduler(), job);
	}

	/**
	 * 添加期满评估提醒定时任务
	 * 
	 * @param vo
	 * @throws SchedulerException
	 */
	private void addFileAdjust(SignAndPublishVo vo) throws SchedulerException {
		ScheduleJob job = new ScheduleJob();
		job.setJobGroup(TaskType.FILEADJUST.name());
		job.setJobName(vo.getName());
		job.setJobClass(FileAdjust.class);
		job.setDescription("期满评估提醒,名称：" + vo.getName());
		Date invalidDate = vo.getInvalidDate();
		Calendar c = Calendar.getInstance();
		c.setTime(invalidDate);
		c.add(Calendar.MONTH, -6);
		if(c.getTime().before(new Date())){
			return;
		}
		String dayStr = String.valueOf(c.get(Calendar.DATE));
		String monthStr = String.valueOf(c.get(Calendar.MONTH) + 1);
		String yearStr = String.valueOf(c.get(Calendar.YEAR));
		job.setCronExpression("0 0 0 " + dayStr + " " + monthStr + " ? " + yearStr);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("norId", vo.getNormativeFile().getId());
		params.put("name", vo.getNormativeFile().getName());
		job.setParams(params);
		ScheduleUtil.addOrUpdateJob(schedulerFactoryBean.getScheduler(), job);
	}
	
	@Override
	@Transactional
	public boolean delete(Long id, String path) throws ServiceException, SchedulerException {

		// 删除
		SignAndPublish signAndPublish = signAndPublishDao.findOne(id);
		if (signAndPublish != null) {
			Stage stage = signAndPublish.getNormativeFile().getStage();
			if (!stage.equals(Stage.PUBLISH)) {
				throw new ServiceException("已存在下游业务，不可删除");
			}
			NormativeFile normativeFile = signAndPublish.getNormativeFile();
			// 更新规范性文件的制定单位，经办员，领导，发文号，发布日期，有效期，失效期为空
			if (normativeFile != null) {
				normativeFile.setDecUnit(null);
				normativeFile.setDecUnitClerk(null);
				normativeFile.setDecUnitLeader(null);
				normativeFile.setPublishNo(null);
				normativeFile.setPublishDate(null);
				normativeFile.setInvalidDate(null);
				normativeFile.setStage(Stage.DELIBERATION_MODIFY);
				normativeFileDao.save(normativeFile);
			} else {
				throw new ServiceException("数据错误，ID为：" + id);
			}
			signAndPublishDao.delete(signAndPublish);
			
			Task task = taskDao.findByNorIdAndTaskType(signAndPublish.getNormativeFile().getId(), TaskType.RECORDREMIND);
			taskDao.delete(task);
			
			deleteFileInvalidJob(normativeFile.getName());
			deleteFileAdjustJob(normativeFile.getName());
			
			String filePath = WordUtils.getFilePath(path, NormativeFileVo.createVo(normativeFile), Stage.PUBLISH.toString());
			String fileName = NormativeFileVo.createVo(normativeFile).getName() + "(" + SysUtil.STAGE_LEGAL_LEGALDOC + ")" + SysUtil.EXTENSION_NAME;
			filePath = filePath + File.separator + fileName;
			WordUtils.deleteWord(filePath);
			return true;
		} else {
			throw new ServiceException("数据错误，ID为：" + id);
		}

	}

	/**
	 * 删除文件自动失效定时任务
	 * @param name
	 * @throws ServiceException
	 */
	private void deleteFileInvalidJob(String name) throws SchedulerException{
		ScheduleJob job = new ScheduleJob();
		job.setJobGroup(TaskType.FILEINVALIDATION.name());
		job.setJobName(name);
		ScheduleUtil.deleteJob(schedulerFactoryBean.getScheduler(), job);
	}
	
	/**
	 * 删除期满评估提醒定时任务
	 * @param name
	 * @throws ServiceException
	 */
	private void deleteFileAdjustJob(String name) throws SchedulerException{
		ScheduleJob job = new ScheduleJob();
		job.setJobGroup(TaskType.FILEADJUST.name());
		job.setJobName(name);
		ScheduleUtil.deleteJob(schedulerFactoryBean.getScheduler(), job);
	}
	
	@Override
	public boolean submit(SignAndPublishVo signAndPublishVo) {

		return false;
	}

	@Override
	public boolean approve(SignAndPublishVo signAndPublishVo) {

		return false;
	}

	@Override
	public boolean unApprove(SignAndPublishVo signAndPublishVo) {

		return false;
	}

	@Override
	public boolean flow(SignAndPublishVo signAndPublishVo) {

		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<SignAndPublishVo> find(int page, int size, String name, Set<Long> orgIds) {

		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "signDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<SignAndPublish> pages;
		if (StringUtils.isEmpty(name)) {
			pages = signAndPublishDao.findAll(orgIds, pageable);
		} else {
			pages = signAndPublishDao.findLikeName(name, orgIds, pageable);
		}
		List<SignAndPublishVo> volist = SignAndPublishVo.createVoList(pages.getContent());
		Page<SignAndPublishVo> pageVo = new PageImpl<SignAndPublishVo>(volist, pageable, pages.getTotalElements());
		return pageVo;
	}

	@Override
	public boolean publish(SignAndPublishVo signAndPublishVo) {

		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public SignAndPublishVo findSignAndPublishByName(String name) {

		return SignAndPublishVo.createVo(signAndPublishDao.findSignAndPublishByName(name));
	}

	@Override
	@Transactional(readOnly = true)
	public SignAndPublishVo findById(Long id) {

		return SignAndPublishVo.createVo(signAndPublishDao.findOne(id));
	}

	@Override
	@Transactional(readOnly = true)
	public SignAndPublishVo findByNorId(Long id) {

		return SignAndPublishVo.createVo(signAndPublishDao.findByNorId(id));
	}

	public SignAndPublish coverToSingPublish(SignAndPublishVo signAndPublishVo) {// 将Vo对象转换为实体对象
		SignAndPublish signAndPublish = new SignAndPublish();
		signAndPublish.setDecisionMakingUnit(organizationDao.findOne(signAndPublishVo.getDecisionMakingUnit().getId()));
		signAndPublish.setDecisionMakingUnitClerk(userDao.findOne(signAndPublishVo.getDecisionMakingUnitClerk().getId()));
		signAndPublish.setDecisionMakingUnitLeader(userDao.findOne(signAndPublishVo.getDecisionMakingUnitLeader().getId()));
		signAndPublish.setId(signAndPublishVo.getId());
		signAndPublish.setInvalidDate(signAndPublishVo.getInvalidDate());
		signAndPublish.setLegalDoc(signAndPublishVo.getLegalDoc());
		signAndPublish.setName(signAndPublishVo.getName());
		signAndPublish.setNormativeFile(normativeFileDao.findOne(signAndPublishVo.getNormativeFile().getId()));
		signAndPublish.setPublishDate(signAndPublishVo.getPublishDate());
		signAndPublish.setSignDate(signAndPublishVo.getSignDate());
		signAndPublish.setSignLeaders(signAndPublishVo.getSignLeaders());
		signAndPublish.setValidDate(signAndPublishVo.getValidDate());
		signAndPublish.setPublishNo(signAndPublishVo.getPublishNo());
		signAndPublish.setDraftingUnit(organizationDao.findOne(signAndPublishVo.getDraftingUnit().getId()));
		return signAndPublish;
	}
}
