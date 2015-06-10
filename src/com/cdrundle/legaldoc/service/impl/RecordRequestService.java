package com.cdrundle.legaldoc.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IRecordRequestDao;
import com.cdrundle.legaldoc.dao.ITaskDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.RecordRequest;
import com.cdrundle.legaldoc.entity.Task;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.enums.TaskStatus;
import com.cdrundle.legaldoc.enums.TaskType;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IRecordRequestService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.RecordRequestVo;

/**
 * @author XuBao 备案报送 2014年6月19日
 */
@Service
public class RecordRequestService implements IRecordRequestService {

	@Autowired
	private IRecordRequestDao recordRequestDao;
	@Autowired
	private INormativeFileDao normativeFileDao;
	@Autowired
	private IOrganizationDao organizationDao;
	@Autowired
	private IUserDao userDao;
	@Autowired
	private ITaskDao taskDao;

	@Override
	@Transactional
	public RecordRequestVo saveOrUpdate(RecordRequestVo recordRequestVo, String path, String fileName) throws ServiceException {
		Long id = recordRequestVo.getId();
		Organization recordUnit = organizationDao.findOne(recordRequestVo.getRecordUnit().getId());
		User recordUnitClerk = userDao.findOne(recordRequestVo.getRecordUnitClerk().getId());
		User recordUnitLeader = userDao.findOne(recordRequestVo.getRecordUnitLeader().getId());
		String phone = recordRequestVo.getPhone();
		// 更新
		if (id != null) {
			RecordRequest recordRequest = recordRequestDao.findOne(id);
			// 更新备案报送
			if (recordRequest != null) {
				Stage stage = recordRequest.getNormativeFile().getStage();
				if (!stage.equals(Stage.RECORD_REQUEST)) {
					throw new ServiceException("已存在下游业务，不可修改");
				}
				NormativeFile normativeFile = recordRequest.getNormativeFile();
				if (recordUnit != null && recordUnitClerk != null && recordUnitLeader != null && phone != null && normativeFile != null) {
					recordRequest.setRecordUnit(recordUnit);
					recordRequest.setRecordUnitClerk(recordUnitClerk);
					recordRequest.setRecordUnitLeader(recordUnitLeader);
					recordRequest.setPhone(phone);
					recordRequest.setRecordRequestDate(recordRequestVo.getRecordRequestDate());

					normativeFile.setRecRevUnit(organizationDao.findOne(recordRequestVo.getRecordUnit().getId()));
					normativeFile.setRecRevUnitClerk(userDao.findOne(recordRequestVo.getRecordUnitClerk().getId()));
					normativeFile.setRecRevUnitLeader(userDao.findOne(recordRequestVo.getRecordUnitLeader().getId()));
				} else {
					throw new ServiceException("请录入");
				}
				WordUtils.htmlToWord(path, fileName, recordRequestVo.getRecordReport());
				return RecordRequestVo.createVo(recordRequest);
			} else {
				throw new ServiceException("数据错误，ID为" + id);
			}
		}
		// 新增保存
		RecordRequest recordRequest = this.coverToRecordRequest(recordRequestVo);
		NormativeFile normativeFile = recordRequest.getNormativeFile();

		if (normativeFile != null) {
			normativeFile.setRecRevUnit(organizationDao.findOne(recordRequestVo.getRecordUnit().getId()));
			normativeFile.setRecRevUnitClerk(userDao.findOne(recordRequestVo.getRecordUnitClerk().getId()));
			normativeFile.setRecRevUnitLeader(userDao.findOne(recordRequestVo.getRecordUnitLeader().getId()));
			normativeFile.setStage(Stage.RECORD_REQUEST);
			normativeFile = normativeFileDao.save(normativeFile);
		} else {
			throw new ServiceException("数据错误，ID为" + id);
		}
		recordRequest = recordRequestDao.save(recordRequest);
		
		Task task = taskDao.findByNorIdAndTaskType(normativeFile.getId(), TaskType.RECORDREMIND);
		if(task != null){
			task.setTaskStatus(TaskStatus.COMPLETE);
			taskDao.save(task);
		}
		
		WordUtils.htmlToWord(path, fileName, recordRequestVo.getRecordReport());
		return RecordRequestVo.createVo(recordRequest);
	}

	@Override
	@Transactional
	public boolean delete(Long id, String path) throws ServiceException {
		// 删除
		RecordRequest recordRequest = recordRequestDao.findOne(id);
		if (recordRequest != null) {
			Stage stage = recordRequest.getNormativeFile().getStage();
			if (!stage.equals(Stage.RECORD_REQUEST)) {
				throw new ServiceException("已存在下游业务，不可删除");
			}
			NormativeFile normativeFile = recordRequest.getNormativeFile();
			if (normativeFile != null) {
				normativeFile.setRecRevUnit(null);
				normativeFile.setRecRevUnitClerk(null);
				normativeFile.setRecRevUnitLeader(null);
				normativeFile.setStage(Stage.PUBLISH);
				normativeFile = normativeFileDao.save(normativeFile);
			} else {
				throw new ServiceException("数据错误，ID为：" + id);
			}
			recordRequestDao.delete(recordRequest);
			Task task = taskDao.findByNorIdAndTaskType(normativeFile.getId(), TaskType.RECORDREMIND);
			if(task != null){
				task.setTaskStatus(TaskStatus.RUNNING);
				taskDao.save(task);
			}
			String filePath = WordUtils.getFilePath(path, NormativeFileVo.createVo(normativeFile), Stage.RECORD.toString());
			String fileName = NormativeFileVo.createVo(normativeFile).getName() + "(" + SysUtil.STAGE_LEGAL_RECORDREQUEST + ")"
					+ SysUtil.EXTENSION_NAME;
			filePath = filePath + File.separator + fileName;
			WordUtils.deleteWord(filePath);
			return true;

		} else {
			throw new ServiceException("数据错误，ID为：" + id);
		}
	}

	@Override
	public boolean submit(RecordRequestVo recordRequestVo) {

		return false;
	}

	@Override
	public boolean approve(RecordRequestVo recordRequestVo) {

		return false;
	}

	@Override
	public boolean unApprove(RecordRequestVo recordRequestVo) {

		return false;
	}

	@Override
	public boolean flow(RecordRequestVo recordRequestVo) {

		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<RecordRequestVo> find(int page, int size, String name, Set<Long> orgIds) {

		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "recordRequestDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<RecordRequest> pages;
		if (StringUtils.isEmpty(name)) {
			pages = recordRequestDao.findAll(orgIds, pageable);
		} else {
			pages = recordRequestDao.findLikeName(name, orgIds, pageable);
		}
		List<RecordRequestVo> volist = RecordRequestVo.createVoList(pages.getContent());
		Page<RecordRequestVo> pageVo = new PageImpl<RecordRequestVo>(volist, pageable, pages.getTotalElements());
		return pageVo;
	}

	@Override
	public boolean send() {

		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public RecordRequestVo findRecordRequestByName(String name) {

		return RecordRequestVo.createVo(recordRequestDao.findRecordRequestByName(name));
	}

	@Override
	@Transactional(readOnly = true)
	public RecordRequestVo findById(Long id) {

		return RecordRequestVo.createVo(recordRequestDao.findOne(id));
	}

	@Override
	@Transactional(readOnly = true)
	public RecordRequestVo findByNorId(Long id) {

		return RecordRequestVo.createVo(recordRequestDao.findByNorId(id));
	}

	public RecordRequest coverToRecordRequest(RecordRequestVo recordRequestVo) {// 将Vo对象转换为实体对象
		RecordRequest recordRequest = new RecordRequest();
		recordRequest.setDecisionMakingUnit(organizationDao.findOne(recordRequestVo.getDecisionMakingUnit().getId()));
		recordRequest.setDecisionMakingUnitClerk(userDao.findOne(recordRequestVo.getDecisionMakingUnitClerk().getId()));
		recordRequest.setDecisionMakingUnitLeader(userDao.findOne(recordRequestVo.getDecisionMakingUnitLeader().getId()));
		recordRequest.setDraftingInstruction(recordRequestVo.getDraftingInstruction());
		recordRequest.setId(recordRequestVo.getId());
		recordRequest.setLegalBasis(recordRequestVo.getLegalBasis());
		recordRequest.setLegalDoc(recordRequestVo.getLegalDoc());
		recordRequest.setName(recordRequestVo.getName());
		recordRequest.setNormativeFile(normativeFileDao.findOne(recordRequestVo.getNormativeFile().getId()));
		recordRequest.setRecordReport(recordRequestVo.getRecordReport());
		recordRequest.setRecordRequestDate(recordRequestVo.getRecordRequestDate());
		recordRequest.setRecordUnit(organizationDao.findOne(recordRequestVo.getRecordUnit().getId()));
		recordRequest.setRecordUnitClerk(userDao.findOne(recordRequestVo.getRecordUnitClerk().getId()));
		recordRequest.setRecordUnitLeader(userDao.findOne(recordRequestVo.getRecordUnitLeader().getId()));
		recordRequest.setStatus(recordRequestVo.getStatus());
		recordRequest.setPhone(recordRequestVo.getPhone());
		recordRequest.setDraftingUnit(organizationDao.findOne(recordRequestVo.getDraftingUnit().getId()));
		recordRequest.setDraftingUnitClerk(userDao.findOne(recordRequestVo.getDraftingUnitClerk().getId()));
		recordRequest.setDraftingUnitLeader(userDao.findOne(recordRequestVo.getDraftingUnitLeader().getId()));
		return recordRequest;
	}
}
