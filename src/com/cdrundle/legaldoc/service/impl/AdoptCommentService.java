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

import com.cdrundle.legaldoc.dao.IAdoptCommentDao;
import com.cdrundle.legaldoc.dao.IExamDraftSubDao;
import com.cdrundle.legaldoc.dao.IModifyDraftDao;
import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.entity.AdoptComment;
import com.cdrundle.legaldoc.entity.ExaminationDraftSubmit;
import com.cdrundle.legaldoc.entity.ModifyDraft;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IAdoptCommentService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.AdoptCommentVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;

@Service
public class AdoptCommentService implements IAdoptCommentService {

	@Autowired
	IAdoptCommentDao adoptCommentDao;

	@Autowired
	INormativeFileDao norFileDao;
	
	@Autowired
	IModifyDraftDao modifyDraftDao;
	
	@Autowired
	IExamDraftSubDao examDraftSubDao;

	@Override
	@Transactional
	public AdoptCommentVo saveOrUpdate(AdoptCommentVo adoptCommentVo, String rootPath) throws ServiceException {
		if (adoptCommentVo != null) {
			NormativeFileVo norFileVo = null;
			AdoptComment adoptComment = null;
			AdoptComment savedAdoptComment = null;
			if (adoptCommentVo.getId() != null && adoptCommentVo.getId() > 0) {

				adoptComment = adoptCommentDao.findOne(adoptCommentVo.getId());
				NormativeFile normativeFile = adoptComment.getNormativeFile();
				if(adoptCommentVo.getIsNeedModify()){
					ModifyDraft modifyDraft = modifyDraftDao.findByNorFileId(normativeFile.getId());
					if(modifyDraft != null){
						throw new ServiceException("已存在下游业务,不允许修改");
					}
				}else{
					ExaminationDraftSubmit examinationDraftSubmit = examDraftSubDao.findByNorId(normativeFile.getId());
					if(examinationDraftSubmit != null){
						throw new ServiceException("已存在下游业务,不允许修改");
					}
				}
				adoptComment.setFeedbackProcess(adoptCommentVo.getFeedbackProcess());
				adoptComment.setIsNeedModify(adoptCommentVo.getIsNeedModify());
				norFileVo = NormativeFileVo.createVo(normativeFile);

				if(adoptCommentVo.getIsNeedModify()){
					normativeFile.setStage(Stage.REQUEST_COMMENT_ADOPT);
				}else{
					normativeFile.setStage(Stage.REQUEST_COMMENT_MODIFY);
				}
				norFileDao.save(normativeFile);
				
				savedAdoptComment = adoptCommentDao.save(adoptComment);
			} else {
				adoptComment = new AdoptComment();
				NormativeFile norFile = norFileDao.findOne(adoptCommentVo.getNormativeFile().getId());
				if(adoptCommentVo.getIsNeedModify()){
					norFile.setStage(Stage.REQUEST_COMMENT_ADOPT);
				}else{
					norFile.setStage(Stage.REQUEST_COMMENT_MODIFY);
				}
				norFileDao.save(norFile);
				adoptComment.setId(adoptCommentVo.getId());
				adoptComment.setName(norFile.getName());
				adoptComment.setNormativeFile(norFile);
				adoptComment.setDraftingUnit(norFile.getDrtUnit());
				adoptComment.setDraftingUnitLeader(norFile.getDrtUnitLeader());
				adoptComment.setDraftingUnitClerk(norFile.getDrtUnitClerk());
				if (!StringUtils.isEmpty(adoptCommentVo.getFeedbackComment())) {
					adoptComment.setFeedbackComment(adoptCommentVo.getFeedbackComment());
				} else {
					adoptComment.setFeedbackComment("");
				}
				adoptComment.setFeedbackProcess(adoptCommentVo.getFeedbackProcess());
				adoptComment.setIsNeedModify(adoptCommentVo.getIsNeedModify());

				norFileVo = NormativeFileVo.createVo(norFile);

				savedAdoptComment = adoptCommentDao.save(adoptComment);

			}
			String filePath = WordUtils.getFilePath(rootPath, norFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_ADOPTCOMMENT + ")" + SysUtil.EXTENSION_NAME;
			// 保存反馈处理意见文档
			WordUtils.htmlToWord(filePath, fileName, adoptCommentVo.getFeedbackProcess());
			if(!adoptCommentVo.getIsNeedModify()){
				//从起草目录获取征求意见稿内容
				String draftFilePath = WordUtils.getFilePath(rootPath, norFileVo, Stage.DRAFTING.toString());
				String draftFileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTCOMMENT + ")" + SysUtil.EXTENSION_NAME;
				String content = WordUtils.readFile(draftFilePath + File.separator + draftFileName);	//获取文档内容
				//将征求意见稿保存为送审稿
				String savedFileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING + ")" + SysUtil.EXTENSION_NAME;
				WordUtils.htmlToWord(filePath, savedFileName, content.replaceFirst(SysUtil.STAGE_LEGAL_REQUESTCOMMENT, SysUtil.STAGE_LEGAL_EXAMDRAFTING));
			}else{
				String examDraftFileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING + ")" + SysUtil.EXTENSION_NAME;
				File file = new File(filePath + File.separator + examDraftFileName);
				if(file.exists()){
					WordUtils.deleteWord(filePath + File.separator + examDraftFileName);
				}
			}
			return AdoptCommentVo.createVo(savedAdoptComment);

		}
		return null;
	}

	@Override
	@Transactional
	public boolean delete(Long id, String rootPath) throws ServiceException {
		if (id != null) {
			AdoptComment adoptComment = adoptCommentDao.findOne(id);
			NormativeFile normativeFile = adoptComment.getNormativeFile();
			if(adoptComment.getIsNeedModify()){
				ModifyDraft modifyDraft = modifyDraftDao.findByNorFileId(normativeFile.getId());
				if(modifyDraft != null){
					throw new ServiceException("已存在下游业务,不允许修改");
				}
			}else{
				ExaminationDraftSubmit examinationDraftSubmit = examDraftSubDao.findByNorId(normativeFile.getId());
				if(examinationDraftSubmit != null){
					throw new ServiceException("已存在下游业务,不允许修改");
				}
			}
			normativeFile.setStage(Stage.REQUEST_COMMENT_FEEDBACK);
			norFileDao.save(normativeFile);
			NormativeFileVo normativeFileVo = NormativeFileVo.createVo(normativeFile);

			String filePath = WordUtils.getFilePath(rootPath, normativeFileVo, Stage.REQUEST_COMMENT.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_ADOPTCOMMENT + ")" + SysUtil.EXTENSION_NAME;
			filePath = filePath + File.separator + fileName;

			adoptCommentDao.delete(id);
			// 删除反馈意见处理情况文件
			WordUtils.deleteWord(filePath);
			return true;
		}
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public AdoptCommentVo findById(Long id) {
		if (id != null) {
			AdoptComment adoptComment = adoptCommentDao.findOne(id);
			return AdoptCommentVo.createVo(adoptComment);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public AdoptCommentVo findByNorFileId(Long id) {
		if (id != null) {
			AdoptComment adoptComment = adoptCommentDao.findByNorFileId(id);
			if (adoptComment != null) {
				return AdoptCommentVo.createVo(adoptComment);
			}
			return null;
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<AdoptCommentVo> findByName(String name, Set<Long> orgIds, int page, int size) {
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "id");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<AdoptComment> pages = null;
		if (!StringUtils.isEmpty(name)) {
			pages = adoptCommentDao.findByName(orgIds, name, pageable);
		} else {
			pages = adoptCommentDao.findAll(orgIds, pageable);
		}
		List<AdoptCommentVo> volist = AdoptCommentVo.createVoList(pages.getContent());
		Page<AdoptCommentVo> pageVo = new PageImpl<AdoptCommentVo>(volist, pageable, pages.getTotalElements());
		return pageVo;
	}

}
