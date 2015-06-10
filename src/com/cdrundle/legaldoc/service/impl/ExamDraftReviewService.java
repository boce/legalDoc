package com.cdrundle.legaldoc.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IDeliberationRequestDao;
import com.cdrundle.legaldoc.dao.IExamDraftModifyDao;
import com.cdrundle.legaldoc.dao.IExamDraftReviewDao;
import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.DeliberationRequest;
import com.cdrundle.legaldoc.entity.ExaminationDraftModify;
import com.cdrundle.legaldoc.entity.ExaminationDraftReview;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IExamDraftReviewService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.ExamDraftReviewVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.UserShortVo;
import com.cdrundle.security.WebPlatformUser;

@Service
public class ExamDraftReviewService implements IExamDraftReviewService {

	@Autowired
	private IExamDraftReviewDao examDraftReviewDao;

	@Autowired
	private IOrganizationDao orgDao;

	@Autowired
	private IUserDao userDao;

	@Autowired
	private INormativeFileDao norDao;

	@PersistenceContext
	EntityManager em;
	
	@Autowired
	private IExamDraftModifyDao examDraftModifyDao;
	
	@Autowired
	private IDeliberationRequestDao deliberationRequestDao;

	@Override
	public ExaminationDraftReview voToExamDraftReview(ExamDraftReviewVo examDraftReviewVo) {
		ExaminationDraftReview examinationDraftReview = new ExaminationDraftReview();
		examinationDraftReview.setId(examDraftReviewVo.getId());
		examinationDraftReview.setName(examDraftReviewVo.getName());
		examinationDraftReview.setNormativeFile(norDao.findOne(examDraftReviewVo.getNormativeFile().getId()));
		examinationDraftReview.setDraftingUnit(orgDao.findOne(examDraftReviewVo.getDraftingUnit().getId()));
		examinationDraftReview.setDraftingUnitLeader(userDao.findOne(examDraftReviewVo.getDraftingUnitLeader().getId()));
		examinationDraftReview.setDraftingUnitClerk(userDao.findOne(examDraftReviewVo.getDraftingUnitClerk().getId()));
		examinationDraftReview.setUnionDraftingUnit(examDraftReviewVo.getUnionDraftingUnit());
		examinationDraftReview.setUnionDraftingUnitLeader(examDraftReviewVo.getUnionDraftingUnitLeader());
		examinationDraftReview.setUnionDraftingUnitClerk(examDraftReviewVo.getUnionDraftingUnitClerk());
		examinationDraftReview.setStatus(examDraftReviewVo.getStatus());
		examinationDraftReview.setReviewUnit(orgDao.findOne(examDraftReviewVo.getReviewUnit().getId()));
		examinationDraftReview.setReviewUnitLeader(userDao.findOne(examDraftReviewVo.getReviewUnitLeader().getId()));
		examinationDraftReview.setReviewUnitClerk(userDao.findOne(examDraftReviewVo.getReviewUnitClerk().getId()));
		examinationDraftReview.setExaminationDraft(examDraftReviewVo.getExaminationDraft());
		examinationDraftReview.setDraftingInstruction(examDraftReviewVo.getDraftingInstruction());
		examinationDraftReview.setLegalBasises(examDraftReviewVo.getLegalBasises());
		examinationDraftReview.setReviewDate(examDraftReviewVo.getReviewDate());
		examinationDraftReview.setReviewComment(examDraftReviewVo.getReviewComment());
		examinationDraftReview.setIsNeedModify(examDraftReviewVo.getIsNeedModify());
		return examinationDraftReview;
	}

	public ExamDraftReviewVo examDraftReviewToVo(ExaminationDraftReview examDraftReview) {
		ExamDraftReviewVo examDraftReviewVo = new ExamDraftReviewVo();

		NormativeFileVo normativeFileVo = NormativeFileVo.createVo(norDao.findOne(examDraftReview.getNormativeFile().getId()));
		Organization dUnit = orgDao.findOne(examDraftReview.getDraftingUnit().getId());
		OrgShortVo draftUnit = OrgShortVo.createVo(dUnit);
		User dUnitLeader = userDao.findOne(examDraftReview.getDraftingUnitLeader().getId());
		UserShortVo dLeader = UserShortVo.createVo(dUnitLeader);
		User dUnitClerk = userDao.findOne(Long.valueOf(examDraftReview.getDraftingUnitClerk().getId()));
		UserShortVo dClerk = UserShortVo.createVo(dUnitClerk);

		Organization rUnit = orgDao.findOne(examDraftReview.getReviewUnit().getId());
		OrgShortVo rdraftUnit = OrgShortVo.createVo(rUnit);
		User rUnitLeader = userDao.findOne(examDraftReview.getReviewUnitLeader().getId());
		UserShortVo rLeader = UserShortVo.createVo(rUnitLeader);
		User rUnitClerk = userDao.findOne(Long.valueOf(examDraftReview.getReviewUnitClerk().getId()));
		UserShortVo rClerk = UserShortVo.createVo(rUnitClerk);

		examDraftReviewVo.setId(examDraftReview.getId());
		examDraftReviewVo.setName(examDraftReview.getName());
		examDraftReviewVo.setNormativeFile(normativeFileVo);
		examDraftReviewVo.setDraftingUnit(draftUnit);
		examDraftReviewVo.setDraftingUnitLeader(dLeader);
		examDraftReviewVo.setDraftingUnitClerk(dClerk);
		examDraftReviewVo.setUnionDraftingUnit(examDraftReview.getUnionDraftingUnit());
		examDraftReviewVo.setUnionDraftingUnitLeader(examDraftReview.getUnionDraftingUnitLeader());
		examDraftReviewVo.setUnionDraftingUnitClerk(examDraftReview.getUnionDraftingUnitClerk());
		examDraftReviewVo.setStatus(examDraftReview.getStatus());
		examDraftReviewVo.setReviewUnit(rdraftUnit);
		examDraftReviewVo.setReviewUnitLeader(rLeader);
		examDraftReviewVo.setReviewUnitClerk(rClerk);
		examDraftReviewVo.setExaminationDraft(examDraftReview.getExaminationDraft());
		examDraftReviewVo.setDraftingInstruction(examDraftReview.getDraftingInstruction());
		examDraftReviewVo.setLegalBasises(examDraftReview.getLegalBasises());
		examDraftReviewVo.setReviewComment(examDraftReview.getReviewComment());
		examDraftReviewVo.setReviewDate(examDraftReview.getReviewDate());
		examDraftReviewVo.setIsNeedModify(examDraftReview.getIsNeedModify());

		return examDraftReviewVo;
	}

	@Override
	public List<ExamDraftReviewVo> examDraftReviewToVoList(List<ExaminationDraftReview> examDraftRviewList) {
		List<ExamDraftReviewVo> examVoList = new ArrayList<ExamDraftReviewVo>();
		for (ExaminationDraftReview edr : examDraftRviewList) {
			examVoList.add(this.examDraftReviewToVo((edr)));
		}
		return examVoList;
	}

	@Override
	@Transactional
	public ExamDraftReviewVo saveOrUpdate(ExamDraftReviewVo examDraftReviewVo, String filePath, String fileName) throws ServiceException {
		NormativeFile normativeFile = norDao.findOne(examDraftReviewVo.getNormativeFile().getId());
		ExaminationDraftReview examRev = null;
		if (normativeFile != null) {
			examRev = examDraftReviewDao.findByNorFileId(normativeFile.getId());
			if (examRev != null && examRev.getId() > 0) { // 更新
				if(examDraftReviewVo.getIsNeedModify()){
					ExaminationDraftModify examinationDraftModify = examDraftModifyDao.findByNorFileId(normativeFile.getId());
					if (examinationDraftModify != null) {
						throw new ServiceException("已存在下游业务,不允许修改");
					}
				}else{
					DeliberationRequest deliberationRequest = deliberationRequestDao.findByNorId(normativeFile.getId());
					if (deliberationRequest != null) {
						throw new ServiceException("已存在下游业务,不允许修改");
					}
				}
				examRev.setReviewComment(examDraftReviewVo.getReviewComment());
				examRev.setReviewDate(examDraftReviewVo.getReviewDate());
				examRev.setIsNeedModify(examDraftReviewVo.getIsNeedModify());
				examRev = examDraftReviewDao.save(examRev);
			} else { // 新增
				examRev = examDraftReviewDao.save(this.voToExamDraftReview(examDraftReviewVo));
			}
			normativeFile.setReviewDate(examDraftReviewVo.getReviewDate());
			if(examDraftReviewVo.getIsNeedModify()){
				normativeFile.setStage(Stage.LEGAL_REVIEW_REVIEW);
			}else{
				normativeFile.setStage(Stage.LEGAL_REVIEW_MODIFY);
			}
			norDao.save(normativeFile);
			NormativeFileVo norFileVo = NormativeFileVo.createVo(normativeFile);
			//保存审核意见
			String legalReviewFilePath = WordUtils.getFilePath(filePath, norFileVo, Stage.LEGAL_REVIEW.toString());
			WordUtils.htmlToWord(legalReviewFilePath, fileName, examDraftReviewVo.getReviewComment());// 生成文档
			
			if(!examDraftReviewVo.getIsNeedModify()){
				//从征求意见目录获取送审稿
				String requestCommentFilePath = WordUtils.getFilePath(filePath, norFileVo, Stage.REQUEST_COMMENT.toString());
				String examDraftFileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_EXAMDRAFTING + ")" + SysUtil.EXTENSION_NAME;
				String content = WordUtils.readFile(requestCommentFilePath + File.separator + examDraftFileName);	//获取文档内容
				//将送审稿保存为草案
				String protocolFileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_PROTOCOL + ")" + SysUtil.EXTENSION_NAME;
				WordUtils.htmlToWord(legalReviewFilePath, protocolFileName, content.replaceFirst(SysUtil.STAGE_LEGAL_EXAMDRAFTING, SysUtil.STAGE_LEGAL_PROTOCOL));
			}else{
				String protocolFileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_PROTOCOL + ")" + SysUtil.EXTENSION_NAME;
				File file = new File(legalReviewFilePath + File.separator + protocolFileName);
				if(file.exists()){
					WordUtils.deleteWord(legalReviewFilePath + File.separator + protocolFileName);
				}
			}
		} else {
			throw new ServiceException("送审稿审查保存错误!");
		}

		return this.examDraftReviewToVo(examRev);
	}

	@Override
	@Transactional
	public boolean delete(ExamDraftReviewVo examDraftReviewVo, String filePath) throws ServiceException {
		boolean deleteFlag = false; // 删除标志
		NormativeFile normativeFile = norDao.findOne(examDraftReviewVo.getNormativeFile().getId());
		if(examDraftReviewVo.getIsNeedModify()){
			ExaminationDraftModify examinationDraftModify = examDraftModifyDao.findByNorFileId(normativeFile.getId());
			if(examinationDraftModify != null){
				throw new ServiceException("已存在下游业务,不允许修改");
			}
		}else{
			DeliberationRequest deliberationRequest = deliberationRequestDao.findByNorId(normativeFile.getId());
			if(deliberationRequest != null){
				throw new ServiceException("已存在下游业务,不允许修改");
			}
		}
		ExaminationDraftReview examDraftReview = examDraftReviewDao.findOne(examDraftReviewVo.getId());
		// 删除规范性文件的审查日期信息
		normativeFile.setReviewDate(null);
		normativeFile.setStage(Stage.LEGAL_REVIEW_SUBMIT);
		norDao.save(normativeFile);

		// 删除存储对应的文件
		WordUtils.deleteWord(filePath);
		examDraftReviewDao.delete(examDraftReview);
		// 删除送审稿
		deleteFlag = true;
		return deleteFlag;
	}

	@Override
	@Transactional(readOnly = true)
	public ExamDraftReviewVo findByNorFileId(Long id) {
		if (id != null) {
			ExaminationDraftReview examDraftReview = examDraftReviewDao.findByNorFileId(id);
			if (examDraftReview != null) {
				return this.examDraftReviewToVo(examDraftReview);
			}
			return null;
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public ExamDraftReviewVo findById(Long examDraftReviewId) {
		if (examDraftReviewId != null && examDraftReviewId != 0l) {
			return this.examDraftReviewToVo(examDraftReviewDao.findOne(examDraftReviewId));
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExaminationDraftReview> findByName(String examDraftReviewName) {

		// WebPlatformUser userDetail = SysUtil.getLoginInfo();
		// if (userDetail != null) {
		// if (userDetail.getOrgId() != null ||
		// !userDetail.getOrgId().trim().equals("")) {
		// Long orgId = Long.parseLong(userDetail.getOrgId());
		// Organization org = organizationDao.findOne(orgId);
		// return examDraftReviewDao.findByName(examDraftReviewName, org);
		// } else {
		// return null;
		// }
		// }
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ExamDraftReviewVo> findAllByUnit(Set<Long> drfOrgIds, int page, int size) {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		if (userDetail != null) {
			if (userDetail.getOrgId() != null || !userDetail.getOrgId().trim().equals("")) {
				String unionDrtUnit = String.valueOf(userDetail.getOrgId());
				Pageable pageable = new PageRequest(page, size);
				Page<ExaminationDraftReview> pages = examDraftReviewDao.findAllByUnit(drfOrgIds, "\"" + unionDrtUnit + "\"", pageable);
				List<ExamDraftReviewVo> volist = this.examDraftReviewToVoList(pages.getContent());
				Page<ExamDraftReviewVo> pageVo = new PageImpl<ExamDraftReviewVo>(volist, pageable, pages.getTotalElements());
				return pageVo;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ExamDraftReviewVo> findAll(String name, Set<Long> drfOrgIds, Long reviewUnit, Status status, Integer page, Integer size) {
		Pageable pageable = new PageRequest(page, size);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExaminationDraftReview> cq = cb.createQuery(ExaminationDraftReview.class);
		Root<ExaminationDraftReview> root = cq.from(ExaminationDraftReview.class);
		cq.select(root);
		Predicate where = cb.conjunction();
		where = buildCondition(where, root, name, drfOrgIds, reviewUnit, status, cb);
		cq.where(where);
		TypedQuery<ExaminationDraftReview> query = em.createQuery(cq);
		query.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber())).setMaxResults(pageable.getPageSize());
		List<ExaminationDraftReview> rows = query.getResultList();

		int totalCount = getTotalCount(name, drfOrgIds, reviewUnit, status);

		List<ExamDraftReviewVo> volist = this.examDraftReviewToVoList(rows);
		Page<ExamDraftReviewVo> pages = new PageImpl<ExamDraftReviewVo>(volist, pageable, totalCount);
		return pages;
	}

	/**
	 * 
	 * @param name
	 * @param draftingUnit
	 * @param reviewUnit
	 * @param status
	 * @return
	 */
	private int getTotalCount(String name, Set<Long> drfOrgIds, Long reviewUnit, Status status) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ExaminationDraftReview> emp = cq.from(ExaminationDraftReview.class);
		cq.select(cb.countDistinct(emp));
		Predicate where = cb.conjunction();
		where = buildCondition(where, emp, name, drfOrgIds, reviewUnit, status, cb);
		cq.where(where);

		return em.createQuery(cq).getSingleResult().intValue();

	}

	/**
	 * 组合条件查询
	 * 
	 * @param where
	 * @param root
	 * @param name
	 * @param draftingUnit
	 * @param decisionUnit
	 * @param status
	 * @param cb
	 * @return
	 */
	private Predicate buildCondition(Predicate where, Root<ExaminationDraftReview> root, String name, Set<Long> drfOrgIds, Long reviewUnit,
			Status status, CriteriaBuilder cb) {
		EntityType<ExaminationDraftReview> et = root.getModel();
		Path<Long> path = root.get("draftingUnit");
		In<Long> in = cb.in(path);
		if (drfOrgIds != null && !drfOrgIds.isEmpty()) {
			for (Long org : drfOrgIds) {
				in.value(org);
			}
			where = cb.and(in);
		}
		if (!StringUtils.isEmpty(name)) {
			where = cb.and(where, cb.like(root.get(et.getSingularAttribute("name", String.class)), "%" + name + "%"));
		}
		if (reviewUnit != null) {
			Organization revOrg = orgDao.findOne(reviewUnit);
			if (revOrg != null) {
				where = cb.and(where, cb.equal(root.get(et.getSingularAttribute("reviewUnit", Organization.class)), revOrg));
			}
		}
		if (status != null) {
			where = cb.and(where, cb.equal(root.get(et.getSingularAttribute("status", Status.class)), status));
		}
		return where;
	}

	@Override
	public void submit(ExaminationDraftReview examinationDraftReview) {
		// TODO Auto-generated method stub

	}

	@Override
	public void approve(ExaminationDraftReview examinationDraftReview) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unApprove(ExaminationDraftReview examinationDraftReview) {
		// TODO Auto-generated method stub
	}

	@Override
	public void flow(ExaminationDraftReview examinationDraftReview) {
		// TODO Auto-generated method stub
	}

	@Override
	public void send(String message) {
		// TODO Auto-generated method stub
	}
}
