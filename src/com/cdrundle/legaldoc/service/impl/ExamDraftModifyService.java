package com.cdrundle.legaldoc.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IDeliberationRequestDao;
import com.cdrundle.legaldoc.dao.IExamDraftModifyDao;
import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.DeliberationRequest;
import com.cdrundle.legaldoc.entity.ExaminationDraftModify;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IExamDraftModifyService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.ExamDraftModifyVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.UserShortVo;
import com.cdrundle.security.WebPlatformUser;

@Service
public class ExamDraftModifyService implements IExamDraftModifyService {

	@Autowired
	IExamDraftModifyDao examDraftModifyDao;

	@Autowired
	private IOrganizationDao organizationDao;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private INormativeFileDao normativeFileDao;
	
	@Autowired
	private IDeliberationRequestDao deliberationRequestDao;

	@Override
	public ExaminationDraftModify voToExamDraftModify(ExamDraftModifyVo examDraftModifyVo) {
		ExaminationDraftModify examinationDraftModify = new ExaminationDraftModify();
		examinationDraftModify.setId(examDraftModifyVo.getId());
		examinationDraftModify.setName(examDraftModifyVo.getName());
		examinationDraftModify.setNormativeFile(normativeFileDao.findOne(examDraftModifyVo.getNormativeFile().getId()));
		examinationDraftModify.setDraftingUnit(organizationDao.findOne(examDraftModifyVo.getDraftingUnit().getId()));
		examinationDraftModify.setDraftingUnitLeader(userDao.findOne(examDraftModifyVo.getDraftingUnitLeader().getId()));
		examinationDraftModify.setDraftingUnitClerk(userDao.findOne(examDraftModifyVo.getDraftingUnitClerk().getId()));
		examinationDraftModify.setReviewComment(examDraftModifyVo.getReviewComment());
		examinationDraftModify.setContent(examDraftModifyVo.getContent());
		return examinationDraftModify;
	}

	public ExamDraftModifyVo examDraftModifyToVo(ExaminationDraftModify examDraftModify) {
		ExamDraftModifyVo examDraftModifyVo = new ExamDraftModifyVo();

		NormativeFileVo normativeFileVo = NormativeFileVo.createVo(normativeFileDao.findOne(examDraftModify.getNormativeFile().getId()));
		Organization dUnit = organizationDao.findOne(examDraftModify.getDraftingUnit().getId());
		OrgShortVo draftUnit = OrgShortVo.createVo(dUnit);
		User dUnitLeader = userDao.findOne(examDraftModify.getDraftingUnitLeader().getId());
		UserShortVo dLeader = UserShortVo.createVo(dUnitLeader);
		User dUnitClerk = userDao.findOne(Long.valueOf(examDraftModify.getDraftingUnitClerk().getId()));
		UserShortVo dClerk = UserShortVo.createVo(dUnitClerk);

		examDraftModifyVo.setId(examDraftModify.getId());
		examDraftModifyVo.setName(examDraftModify.getName());
		examDraftModifyVo.setNormativeFile(normativeFileVo);
		examDraftModifyVo.setDraftingUnit(draftUnit);
		examDraftModifyVo.setDraftingUnitLeader(dLeader);
		examDraftModifyVo.setDraftingUnitClerk(dClerk);
		examDraftModifyVo.setReviewComment(examDraftModify.getReviewComment());
		examDraftModifyVo.setContent(examDraftModify.getContent());
		return examDraftModifyVo;
	}

	@Override
	public List<ExamDraftModifyVo> examDraftModifyToVoList(List<ExaminationDraftModify> examDraftModifyList) {
		List<ExamDraftModifyVo> examVoList = new ArrayList<ExamDraftModifyVo>();
		for (ExaminationDraftModify edr : examDraftModifyList) {
			examVoList.add(this.examDraftModifyToVo((edr)));
		}
		return examVoList;
	}

	@Override
	@Transactional
	public ExamDraftModifyVo saveOrUpdate(ExamDraftModifyVo examDraftModifyVo, String filePath, String fileName) throws ServiceException {
		NormativeFile normativeFile = normativeFileDao.findOne(examDraftModifyVo.getNormativeFile().getId());
		ExaminationDraftModify examMod = null;
		if (normativeFile != null) {
			examMod = examDraftModifyDao.findByNorFileId(normativeFile.getId());
			Stage stage = normativeFile.getStage();
			if (examMod != null && examMod.getId() > 0) { // 更新
				if (!Stage.LEGAL_REVIEW_MODIFY.equals(stage)) {
					throw new ServiceException("送审稿已经定稿,不能再修改！");
				}
				examMod.setReviewComment(examDraftModifyVo.getReviewComment());
				examMod.setContent(examDraftModifyVo.getContent());
				examMod = examDraftModifyDao.save(examMod);
			} else { // 新增
				if(Stage.LEGAL_REVIEW_MODIFY.equals(stage)){
					throw new ServiceException("保存失败，该送审稿不需要修改！");
				}
				examMod = examDraftModifyDao.save(this.voToExamDraftModify(examDraftModifyVo));
			}
			WordUtils.htmlToWord(filePath, fileName, examDraftModifyVo.getContent());// 生成文档
			normativeFile.setStage(Stage.LEGAL_REVIEW_MODIFY);
		} else {
			throw new ServiceException("送审稿修改保存错误!");
		}

		return this.examDraftModifyToVo(examMod);
	}

	@Override
	@Transactional
	public boolean delete(ExamDraftModifyVo examDraftModifyVo, String filePath) throws ServiceException {
		NormativeFile normativeFile = normativeFileDao.findOne(examDraftModifyVo.getNormativeFile().getId());
		DeliberationRequest deliberationRequest = deliberationRequestDao.findByNorId(normativeFile.getId());
		if(deliberationRequest != null){
			throw new ServiceException("已存在下游业务,不允许删除");
		}
		ExaminationDraftModify edm = examDraftModifyDao.findOne(examDraftModifyVo.getId());
		boolean returnFlag = false;
		normativeFile.setStage(Stage.LEGAL_REVIEW_REVIEW);
		examDraftModifyDao.delete(edm);
		String fileName = normativeFile.getName() + "(" + SysUtil.STAGE_LEGAL_PROTOCOL + ")" + SysUtil.EXTENSION_NAME;
		WordUtils.deleteWord(filePath + File.separator + fileName);
		fileName = normativeFile.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING_MODIFY + ")" + SysUtil.EXTENSION_NAME;
		WordUtils.deleteWord(filePath + File.separator + fileName);
		returnFlag = true;
		return returnFlag;
	}

	@Override
	@Transactional(readOnly = true)
	public ExamDraftModifyVo findById(Long examDraftModifyId) {
		if (examDraftModifyId != null && examDraftModifyId != 0l) {
			ExaminationDraftModify edm = examDraftModifyDao.findOne(examDraftModifyId);
			return this.examDraftModifyToVo(edm);
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ExamDraftModifyVo findByNorFileId(Long id) {
		if (id != null) {
			ExaminationDraftModify examDraftModify = examDraftModifyDao.findByNorFileId(id);
			if (examDraftModify != null) {
				return this.examDraftModifyToVo(examDraftModify);
			}
			return null;
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ExamDraftModifyVo> findAll(int page, int size) {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		if (userDetail != null) {
			if (userDetail.getOrgId() != null || !userDetail.getOrgId().trim().equals("")) {
				long orgId = Long.parseLong(userDetail.getOrgId());
				Pageable pageable = new PageRequest(page, size);
				Page<ExaminationDraftModify> pages = examDraftModifyDao.findAllByUnit(orgId, pageable);
				List<ExamDraftModifyVo> volist = this.examDraftModifyToVoList(pages.getContent());
				Page<ExamDraftModifyVo> pageVo = new PageImpl<ExamDraftModifyVo>(volist, pageable, pages.getTotalElements());
				return pageVo;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ExamDraftModifyVo> findAllByName(String name, int page, int size) {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		if (userDetail != null) {
			if (userDetail.getOrgId() != null || !userDetail.getOrgId().trim().equals("")) {
				long orgId = Long.parseLong(userDetail.getOrgId());
				Pageable pageable = new PageRequest(page, size);
				Page<ExaminationDraftModify> pages = examDraftModifyDao.findAllByNameAndUnit(name, orgId, pageable);
				List<ExamDraftModifyVo> volist = this.examDraftModifyToVoList(pages.getContent());
				Page<ExamDraftModifyVo> pageVo = new PageImpl<ExamDraftModifyVo>(volist, pageable, pages.getTotalElements());
				return pageVo;
			}
		}
		return null;
	}

	@Override
	public boolean confrim(ExaminationDraftModify examinationDraftModify, String path) {
		return false;
	}
}
