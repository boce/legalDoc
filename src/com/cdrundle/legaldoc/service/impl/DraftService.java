package com.cdrundle.legaldoc.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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

import com.cdrundle.legaldoc.dao.IAdoptCommentDao;
import com.cdrundle.legaldoc.dao.IDraftDao;
import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IRequestCommentDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.AdoptComment;
import com.cdrundle.legaldoc.entity.Draft;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.RequestComment;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IDraftService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.DraftVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.security.WebPlatformUser;

@Service
public class DraftService implements IDraftService {
	@Autowired
	IDraftDao draftDao;

	@Autowired
	INormativeFileDao normativeFileDao;

	@Autowired
	IOrganizationDao organizationDao;

	@Autowired
	IUserDao userDao;

	@Autowired
	IRequestCommentDao requestCommentDao;
	
	@Autowired
	IAdoptCommentDao adoptCommentDao;
	
	@Override
	@Transactional
	public DraftVo saveOrUpdate(DraftVo draftVo, String rootPath, Boolean isConfirm) throws ServiceException {
		Long id = draftVo.getId();
		Draft savedDraft;
		if (id != null) {
			Draft draft = draftDao.findOne(id);
			if (draft != null) {
				NormativeFile normativeFile = draft.getNormativeFile();
				if (normativeFile != null) {
					if(StringUtils.isEmpty(normativeFile.getInvolvedOrges())){
						AdoptComment adoptComment = adoptCommentDao.findByNorFileId(normativeFile.getId());
						if(adoptComment != null){
							throw new ServiceException("已存在下游业务,不允许修改");
						}
					}else{
						List<RequestComment> requestComments = requestCommentDao.findByNorFile(normativeFile.getId());
						if(requestComments != null && !requestComments.isEmpty()){
							throw new ServiceException("已存在下游业务,不允许修改");
						}
					}
					Organization draftUnit = organizationDao.findOne(draftVo.getDraftingUnit().getId());
					draft.setDraftingUnit(draftUnit);
					User draftUnitLeader = userDao.findOne(draftVo.getDraftingUnitLeader().getId());
					draft.setDraftingUnitLeader(draftUnitLeader);
					User draftUnitClerk = userDao.findOne(draftVo.getDraftingUnitClerk().getId());
					draft.setDraftingUnitClerk(draftUnitClerk);
					draft.setDraftingStartDate(draftVo.getDraftingStartDate());
					draft.setDraftingEndDate(draftVo.getDraftingEndDate());
					draft.setDraftingMode(draftVo.getDraftingMode());
					draft.setUnionDraftingUnit(draftVo.getUnionDraftingUnit());
					draft.setUnionDraftingUnitLeader(draftVo.getUnionDraftingUnitLeader());
					draft.setUnionDraftingUnitClerk(draftVo.getUnionDraftingUnitClerk());
					draft.setContent(draftVo.getContent());
					normativeFile.setDrtUnit(draftUnit);
					normativeFile.setDrtUnitLeader(draftUnitLeader);
					normativeFile.setDrtUnitClerk(draftUnitClerk);
					normativeFile.setUnionDrtUnit(draftVo.getUnionDraftingUnit());
					normativeFile.setUnionDrtUnitLeader(draftVo.getUnionDraftingUnitLeader());
					normativeFile.setUnionDrtUnitClerk(draftVo.getUnionDraftingUnitClerk());
					normativeFile.setDraftDate(draftVo.getDraftingStartDate());
				} else {
					throw new ServiceException("数据错误，起草单对应规范性文件不存在");
				}
				savedDraft = draftDao.save(draft);
			} else {
				throw new ServiceException("数据错误，该起草单不存在");
			}
		} else {
			Draft draft = convertToDraft(draftVo);
			NormativeFile norFile = draft.getNormativeFile();
			draft.setName(norFile.getName());
			draft.setInvolvedOrges(norFile.getInvolvedOrges());
			norFile.setDrtUnit(draft.getDraftingUnit());
			norFile.setDrtUnitLeader(draft.getDraftingUnitLeader());
			norFile.setDrtUnitClerk(draft.getDraftingUnitClerk());
			norFile.setUnionDrtUnit(draft.getUnionDraftingUnit());
			norFile.setUnionDrtUnitLeader(draft.getUnionDraftingUnitLeader());
			norFile.setUnionDrtUnitClerk(draft.getUnionDraftingUnitClerk());
			norFile.setDraftDate(draft.getDraftingStartDate());
			if(StringUtils.isEmpty(norFile.getInvolvedOrges())){
				//如果涉及部门为空，则进入反馈意见处理情况阶段
				norFile.setStage(Stage.REQUEST_COMMENT_FEEDBACK);
			}else{
				norFile.setStage(Stage.DRAFTING);
			}
			savedDraft = draftDao.save(draft);

		}
		NormativeFileVo norFileVo = NormativeFileVo.createVo(savedDraft.getNormativeFile());
		String filePath = WordUtils.getFilePath(rootPath, norFileVo, Stage.DRAFTING.toString());
		String fileName = "";
		if(!isConfirm){
			fileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_DRAFT + ")" + SysUtil.EXTENSION_NAME;
		}else{
			fileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTCOMMENT + ")" + SysUtil.EXTENSION_NAME;
		}
		// 保存起草文档
		WordUtils.htmlToWord(filePath, fileName, savedDraft.getContent());
		return DraftVo.createVo(savedDraft);
	}

	@Override
	@Transactional
	public boolean delete(DraftVo draftVo) {
		draftDao.delete(convertToDraft(draftVo));
		return true;
	}

	@Override
	@Transactional
	public boolean delete(long id) {
		draftDao.delete(id);
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public DraftVo findById(Long id) {
		DraftVo vo = DraftVo.createVo(draftDao.findOne(id));
		vo.setInvolvedOrgNames(getInvolvedOrgesName(vo.getInvolvedOrges()));
		return vo;
	}

	@Override
	@Transactional(readOnly = true)
	public DraftVo findByName(String name) {
		return DraftVo.createVo(draftDao.findByName(name));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DraftVo> findByName(String name, Set<Long> orgIds, int page, int size) {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		String orgId = userDetail.getOrgId();
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "draftingStartDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<Draft> pageDraft = null;
		if (StringUtils.isNotEmpty(name)) {
			pageDraft = draftDao.findByName(orgIds, "%\"" + orgId + "\"%", "%" + name + "%", pageable);
		} else {
			pageDraft = draftDao.findAll(orgIds, "%\"" + orgId + "\"%", pageable);
		}
		List<DraftVo> volist = DraftVo.createVoList(pageDraft.getContent());
		Page<DraftVo> pages = new PageImpl<DraftVo>(volist, pageable, pageDraft.getTotalElements());
		return pages;
	}

	@Override
	@Transactional(readOnly = true)
	public DraftVo findByNorFileId(Long norId) {
		DraftVo vo = new DraftVo();
		if (norId != null) {
			Draft draft = draftDao.findByNorFileId(norId);
			vo = DraftVo.createVo(draft);
			vo.setInvolvedOrgNames(getInvolvedOrgesName(vo.getInvolvedOrges()));
		}
		return vo;
	}

	@Transactional(readOnly = true)
	public String getInvolvedOrgesName(String involvedOrges) {
		String involvedOrgesName = "";
		if (StringUtils.isNotEmpty(involvedOrges)) {
			involvedOrges = involvedOrges.replaceAll("\"", "");
			Set<Long> orgSet = new HashSet<>();
			String[] orgArray = involvedOrges.split(",");
			for (int i = 0; i < orgArray.length; i++) {
				orgSet.add(Long.parseLong(orgArray[i]));
			}
			List<Organization> orgList = organizationDao.findOrgByIds(orgSet);
			for (Iterator<Organization> iterator = orgList.iterator(); iterator.hasNext();) {
				Organization org = iterator.next();
				if ("".equals(involvedOrgesName)) {
					involvedOrgesName = org.getName();
				} else {
					involvedOrgesName += ("," + org.getName());
				}
			}
		}
		return involvedOrgesName;
	}

	@Override
	@Transactional
	public boolean delete(Long id, String rootPath) throws ServiceException {
		if (id != null) {
			Draft draft = draftDao.findOne(id);
			NormativeFile norFile = draft.getNormativeFile();
			if(StringUtils.isEmpty(norFile.getInvolvedOrges())){
				AdoptComment adoptComment = adoptCommentDao.findByNorFileId(norFile.getId());
				if(adoptComment != null){
					throw new ServiceException("已存在下游业务,不允许修改");
				}
			}else{
				List<RequestComment> requestComments = requestCommentDao.findByNorFile(norFile.getId());
				if(requestComments != null && !requestComments.isEmpty()){
					throw new ServiceException("已存在下游业务,不允许修改");
				}
			}
			norFile.setDrtUnit(null);
			norFile.setDrtUnitLeader(null);
			norFile.setDrtUnitClerk(null);
			norFile.setUnionDrtUnit(null);
			norFile.setUnionDrtUnitLeader(null);
			norFile.setUnionDrtUnitClerk(null);
			norFile.setDraftDate(null);
			norFile.setStage(Stage.SETUP);
			normativeFileDao.save(norFile);
			draftDao.delete(id);
			
			NormativeFileVo normativeFileVo = NormativeFileVo.createVo(draft.getNormativeFile());
			String filePath = WordUtils.getFilePath(rootPath, normativeFileVo, Stage.DRAFTING.toString());
			String fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_DRAFT + ")" + SysUtil.EXTENSION_NAME;
			// 删除起草文件初稿
			WordUtils.deleteWord(filePath + File.separator + fileName);
			fileName = normativeFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_REQUESTCOMMENT + ")" + SysUtil.EXTENSION_NAME;
			//删除起草文件征求意见稿
			WordUtils.deleteWord(filePath + File.separator + fileName);
			return true;
		}
		return false;
	}

	public Draft convertToDraft(DraftVo draftVo) {
		Draft draft = new Draft();
		draft.setId(draftVo.getId());
		draft.setName(draftVo.getName());
		if (draftVo.getNormativeFile() != null) {
			draft.setNormativeFile(normativeFileDao.findOne(draftVo.getNormativeFile().getId()));
		}
		if (draftVo.getDraftingUnit() != null) {
			draft.setDraftingUnit(organizationDao.findOne(draftVo.getDraftingUnit().getId()));
		}
		if (draftVo.getDraftingUnitLeader() != null) {
			draft.setDraftingUnitLeader(userDao.findOne(draftVo.getDraftingUnitLeader().getId()));
		}
		if (draftVo.getDraftingUnitClerk() != null) {
			draft.setDraftingUnitClerk(userDao.findOne(draftVo.getDraftingUnitClerk().getId()));
		}
		draft.setUnionDraftingUnit(draftVo.getUnionDraftingUnit());
		draft.setUnionDraftingUnitLeader(draftVo.getUnionDraftingUnitLeader());
		draft.setUnionDraftingUnitClerk(draftVo.getUnionDraftingUnitClerk());
		draft.setStatus(draftVo.getStatus());
		draft.setDraftingStartDate(draftVo.getDraftingStartDate());
		draft.setDraftingEndDate(draftVo.getDraftingEndDate());
		draft.setDraftingMode(draftVo.getDraftingMode());
		draft.setInvolvedOrges(draftVo.getInvolvedOrges());
		draft.setContent(draftVo.getContent());
		return draft;
	}

}
