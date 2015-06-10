package com.cdrundle.legaldoc.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import org.springframework.util.StringUtils;

import com.cdrundle.legaldoc.dao.IExamDraftSubDao;
import com.cdrundle.legaldoc.dao.IModifyDraftDao;
import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.entity.ExaminationDraftSubmit;
import com.cdrundle.legaldoc.entity.ModifyDraft;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IModifyDraftService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.ModifyDraftVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;

@Service
public class ModifyDraftService implements IModifyDraftService {

	@Autowired
	IModifyDraftDao modifyDraftDao;

	@Autowired
	INormativeFileDao norFileDao;

	@Autowired
	IExamDraftSubDao examDraftSubDao;
	
	@Override
	@Transactional
	public ModifyDraftVo saveOrUpdate(ModifyDraftVo modifyDraftVo, String rootPath, boolean isConfirm) throws ServiceException {
		if (modifyDraftVo != null) {
			ModifyDraft modifyDraft = null;
			ModifyDraft savedModifyDraft = null;
			NormativeFileVo norFileVo = null;
			if (modifyDraftVo.getId() != null && modifyDraftVo.getId() > 0) {
				modifyDraft = modifyDraftDao.findOne(modifyDraftVo.getId());
				NormativeFile normativeFile = modifyDraft.getNormativeFile();
				ExaminationDraftSubmit examinationDraftSubmit = examDraftSubDao.findByNorId(normativeFile.getId());
				if(examinationDraftSubmit != null){
					throw new ServiceException("已存在下游业务,不允许修改");
				}
				modifyDraft.setContent(modifyDraftVo.getContent());
				norFileVo = NormativeFileVo.createVo(normativeFile);

				savedModifyDraft = modifyDraftDao.save(modifyDraft);

			} else {
				modifyDraft = new ModifyDraft();
				NormativeFile norFile = norFileDao.findOne(modifyDraftVo.getNormativeFile().getId());
				Stage stage = norFile.getStage();
				if(stage.equals(Stage.REQUEST_COMMENT_MODIFY)){
					throw new ServiceException("保存失败，该征求意见稿不需要修改！");
				}
				norFile.setStage(Stage.REQUEST_COMMENT_MODIFY);
				norFileDao.save(norFile);
				modifyDraft.setId(modifyDraftVo.getId());
				modifyDraft.setName(norFile.getName());
				modifyDraft.setNormativeFile(norFile);
				modifyDraft.setDraftingUnit(norFile.getDrtUnit());
				modifyDraft.setDraftingUnitLeader(norFile.getDrtUnitLeader());
				modifyDraft.setDraftingUnitClerk(norFile.getDrtUnitClerk());
				if (!StringUtils.isEmpty(modifyDraftVo.getFeedbackProcess())) {
					modifyDraft.setFeedbackProcess(modifyDraftVo.getFeedbackProcess());
				} else {
					modifyDraft.setFeedbackProcess("");
				}
				modifyDraft.setContent(modifyDraftVo.getContent());

				norFileVo = NormativeFileVo.createVo(norFile);

				savedModifyDraft = modifyDraftDao.save(modifyDraft);

			}
			String fileName = "";
			String filePath = WordUtils.getFilePath(rootPath, norFileVo, Stage.REQUEST_COMMENT.toString());
			if (!isConfirm) {
				fileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQCOMMENTREVISE + ")" + SysUtil.EXTENSION_NAME;
			} else {
				fileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING + ")" + SysUtil.EXTENSION_NAME;
			}
			// 保存征求意见修改稿文档
			WordUtils.htmlToWord(filePath, fileName, modifyDraftVo.getContent());
			return ModifyDraftVo.createVo(savedModifyDraft);

		}
		return null;
	}

	@Override
	@Transactional
	public boolean delete(Long id, String rootPath) throws ServiceException {
		if (id != null) {
			NormativeFile normativeFile = modifyDraftDao.findOne(id).getNormativeFile();
			NormativeFileVo normativeFileVo = NormativeFileVo.createVo(normativeFile);

			ExaminationDraftSubmit examinationDraftSubmit = examDraftSubDao.findByNorId(normativeFileVo.getId());
			if(examinationDraftSubmit != null){
				throw new ServiceException("已存在下游业务,不允许删除");
			}
			normativeFile.setStage(Stage.REQUEST_COMMENT_ADOPT);
			norFileDao.save(normativeFile);
			
			String filePath = WordUtils.getFilePath(rootPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQCOMMENTREVISE + ")" + SysUtil.EXTENSION_NAME;

			modifyDraftDao.delete(id);

			// 删除征求意见修改稿文件
			WordUtils.deleteWord(filePath + File.separator + fileName);
			//删除送审稿
			fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING + ")" + SysUtil.EXTENSION_NAME;
			WordUtils.deleteWord(filePath + File.separator + fileName);
			return true;
		}
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public ModifyDraftVo findById(Long id) {
		if (id != null) {
			ModifyDraft modifyDraft = modifyDraftDao.findOne(id);
			return ModifyDraftVo.createVo(modifyDraft);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public ModifyDraftVo findByNorFileId(Long id) {
		if (id != null) {
			ModifyDraft modifyDraft = modifyDraftDao.findByNorFileId(id);
			if (modifyDraft != null) {
				return ModifyDraftVo.createVo(modifyDraft);
			}
			return null;
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ModifyDraftVo> findByName(String name, Set<Long> orgIds, int page, int size) {
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "id");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<ModifyDraft> pages = null;
		if (!StringUtils.isEmpty(name)) {
			pages = modifyDraftDao.findByName(orgIds, name, pageable);
		} else {
			pages = modifyDraftDao.findAll(orgIds, pageable);
		}
		List<ModifyDraftVo> volist = ModifyDraftVo.createVoList(pages.getContent());
		Page<ModifyDraftVo> pageVo = new PageImpl<ModifyDraftVo>(volist, pageable, pages.getTotalElements());
		return pageVo;
	}

}
