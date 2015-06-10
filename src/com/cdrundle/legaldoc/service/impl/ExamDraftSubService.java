package com.cdrundle.legaldoc.service.impl;

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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IExamDraftSubDao;
import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.ExaminationDraftSubmit;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IExamDraftSubService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.ExamDraftSubmitVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.UserShortVo;
import com.cdrundle.security.WebPlatformUser;

@Service
public class ExamDraftSubService implements IExamDraftSubService {

	@Autowired
	private IExamDraftSubDao examDraftSubDao;

	@Autowired
	private INormativeFileDao norDao;

	@Autowired
	private IOrganizationDao orgDao;

	@Autowired
	private IUserDao userDao;

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional
	public ExamDraftSubmitVo saveOrUpdate(ExamDraftSubmitVo examDraftSubmitVo, String filePath, String fileName) throws ServiceException {

		NormativeFile normativeFile = norDao.findOne(examDraftSubmitVo.getNormativeFile().getId());
		ExaminationDraftSubmit examSub = null;
		if (normativeFile != null) {
			examSub = examDraftSubDao.findByNorId(normativeFile.getId());
			if (examSub != null && examSub.getId() > 0) { // 更新
				Stage stage = normativeFile.getStage();
				if (!Stage.LEGAL_REVIEW_SUBMIT.equals(stage)) {
					throw new ServiceException("送审稿已经提交,不能保存！");
				}
				examSub.setLegalBasises(examDraftSubmitVo.getLegalBasises());
				examSub.setLegalBasisesNoAtta(examDraftSubmitVo.getLegalBasisesNoAtta());
				examSub.setReviewUnit(orgDao.findOne(examDraftSubmitVo.getReviewUnit().getId()));
				examSub.setReviewUnitClerk(userDao.findOne(examDraftSubmitVo.getReviewUnitClerk().getId()));
				examSub.setReviewUnitLeader(userDao.findOne(examDraftSubmitVo.getReviewUnitClerk().getId()));
				examSub.setDraftingInstruction(examDraftSubmitVo.getDraftingInstruction());
				examSub = examDraftSubDao.save(examSub);
			} else { // 新增
				Stage stage = normativeFile.getStage();
				if(stage.equals(Stage.REQUEST_COMMENT_ADOPT)){
					throw new ServiceException("保存失败，请返回修改征求意见稿！");
				}
				examSub = examDraftSubDao.save(this.voToExamDraftSub(examDraftSubmitVo));
			}
			normativeFile.setRevUnit(orgDao.findOne(examDraftSubmitVo.getReviewUnit().getId()));
			normativeFile.setRevUnitClerk(userDao.findOne(examDraftSubmitVo.getReviewUnitClerk().getId()));
			normativeFile.setRevUnitLeader(userDao.findOne(examDraftSubmitVo.getReviewUnitLeader().getId()));
			normativeFile.setDraftInstruction(fileName);
			normativeFile.setLegalBasis(examDraftSubmitVo.getLegalBasises());
			normativeFile.setLegalBasisNoAtta(examDraftSubmitVo.getLegalBasisesNoAtta());
			normativeFile.setStage(Stage.LEGAL_REVIEW_SUBMIT); // 修改阶段
			norDao.save(normativeFile);
			// 生成起草说明
			WordUtils.htmlToWord(filePath, fileName, examDraftSubmitVo.getDraftingInstruction());
		} else {
			throw new ServiceException("送审稿报送保存错误!");
		}
		return this.examDraftSubToVo(examSub);
	}

	@Override
	@Transactional
	public ExamDraftSubmitVo updateLegalsis(ExamDraftSubmitVo examDraftSubmitVo) throws ServiceException {
		NormativeFile normativeFile = norDao.findOne(examDraftSubmitVo.getNormativeFile().getId());
		normativeFile.setLegalBasis(examDraftSubmitVo.getLegalBasises());
		norDao.save(normativeFile); // 更新规范性文件
		ExaminationDraftSubmit eds = examDraftSubDao.save(this.voToExamDraftSub(examDraftSubmitVo)); // 更新送审稿报送
		return this.examDraftSubToVo(eds);
	}

	@Override
	@Transactional
	public boolean delete(ExamDraftSubmitVo examDraftSubmitVo, String filePath) throws ServiceException {
		boolean deleteFlag = false; // 删除标志
		NormativeFile normativeFile = norDao.findOne(examDraftSubmitVo.getNormativeFile().getId());
		ExaminationDraftSubmit eds = examDraftSubDao.findOne(examDraftSubmitVo.getId());
		if (normativeFile != null && normativeFile.getStage().equals(Stage.LEGAL_REVIEW_SUBMIT)) {
			// 删除规范性文件的审查信息
			normativeFile.setRevUnit(null);
			normativeFile.setRevUnitClerk(null);
			normativeFile.setRevUnitLeader(null);
			normativeFile.setDraftInstruction(null);
			normativeFile.setStage(Stage.REQUEST_COMMENT_MODIFY); // 修改阶段
			norDao.save(normativeFile);

			// 删除存储对应的文件
			WordUtils.deleteWord(filePath);

			// 删除送审稿
			examDraftSubDao.delete(eds);
			deleteFlag = true;
		} else {
			throw new ServiceException("送审稿报送删除错误!");
		}
		return deleteFlag;
	}

	@Override
	@Transactional(readOnly = true)
	public ExamDraftSubmitVo findById(Long id) {
		if (id != null && id != 0l) {
			return this.examDraftSubToVo(examDraftSubDao.findOne(id));
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExaminationDraftSubmit> findByName(String examinationDraftSubmitName) {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		if (userDetail != null) {
			if (userDetail.getOrgId() != null || !userDetail.getOrgId().trim().equals("")) {
				Long orgId = Long.parseLong(userDetail.getOrgId());
				Organization org = orgDao.findOne(orgId);
				return examDraftSubDao.findByName(examinationDraftSubmitName, org);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ExamDraftSubmitVo> findAllByOrgAndUnion(Set<Long> drfOrgIds, int page, int size) {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		if (userDetail != null) {
			if (userDetail.getOrgId() != null || !userDetail.getOrgId().trim().equals("")) {
				String unionDrtUnit = String.valueOf(userDetail.getOrgName());
				List<Order> orders = new ArrayList<Order>();
				Order order = new Order(Direction.DESC, "normativeFile.draftDate");
				orders.add(order);
				Sort sort = new Sort(orders);
				Pageable pageable = new PageRequest(page, size, sort);
				Page<ExaminationDraftSubmit> pages = examDraftSubDao.findAllByUnit(drfOrgIds, "\"" + unionDrtUnit + "\"", pageable);
				List<ExamDraftSubmitVo> volist = this.examDraftSubToVoList(pages.getContent());
				Page<ExamDraftSubmitVo> pageVo = new PageImpl<ExamDraftSubmitVo>(volist, pageable, pages.getTotalElements());
				return pageVo;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ExamDraftSubmitVo> findAll(String name, Set<Long> drfOrgIds, Long reviewUnit, Status status, Integer page, Integer size) {
		Pageable pageable = new PageRequest(page, size);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExaminationDraftSubmit> cq = cb.createQuery(ExaminationDraftSubmit.class);
		Root<ExaminationDraftSubmit> root = cq.from(ExaminationDraftSubmit.class);
		cq.select(root);
		Predicate where = cb.conjunction();
		where = buildCondition(where, root, name, drfOrgIds, reviewUnit, status, cb);
		cq.where(where);
		TypedQuery<ExaminationDraftSubmit> query = em.createQuery(cq);
		query.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber())).setMaxResults(pageable.getPageSize());
		List<ExaminationDraftSubmit> rows = query.getResultList();

		int totalCount = getTotalCount(name, drfOrgIds, reviewUnit, status);

		List<ExamDraftSubmitVo> volist = this.examDraftSubToVoList(rows);
		Page<ExamDraftSubmitVo> pages = new PageImpl<ExamDraftSubmitVo>(volist, pageable, totalCount);
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
		Root<ExaminationDraftSubmit> emp = cq.from(ExaminationDraftSubmit.class);
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
	private Predicate buildCondition(Predicate where, Root<ExaminationDraftSubmit> root, String name, Set<Long> drfOrgIds, Long reviewUnit,
			Status status, CriteriaBuilder cb) {
		EntityType<ExaminationDraftSubmit> et = root.getModel();
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
	@Transactional(readOnly = true)
	public ExaminationDraftSubmit edit(ExamDraftSubmitVo examDraftSubmitVo) {
		ExaminationDraftSubmit eds = this.voToExamDraftSub(examDraftSubmitVo);
		if (eds != null) {
			return examDraftSubDao.save(eds);
		} else {
			return null;
		}
	}

	@Override
	public ExaminationDraftSubmit voToExamDraftSub(ExamDraftSubmitVo examDraftSubmitVo) {
		ExaminationDraftSubmit examinationDraftSubmit = new ExaminationDraftSubmit();
		examinationDraftSubmit.setId(examDraftSubmitVo.getId());
		examinationDraftSubmit.setName(examDraftSubmitVo.getName());
		examinationDraftSubmit.setNormativeFile(norDao.findOne(examDraftSubmitVo.getNormativeFile().getId()));
		examinationDraftSubmit.setDraftingUnit(orgDao.findOne(examDraftSubmitVo.getDraftingUnit().getId()));
		examinationDraftSubmit.setDraftingUnitLeader(userDao.findOne(examDraftSubmitVo.getDraftingUnitLeader().getId()));
		examinationDraftSubmit.setDraftingUnitClerk(userDao.findOne(examDraftSubmitVo.getDraftingUnitClerk().getId()));
		examinationDraftSubmit.setUnionDraftingUnit(examDraftSubmitVo.getUnionDraftingUnit());
		examinationDraftSubmit.setUnionDraftingUnitLeader(examDraftSubmitVo.getUnionDraftingUnitLeader());
		examinationDraftSubmit.setUnionDraftingUnitClerk(examDraftSubmitVo.getUnionDraftingUnitClerk());
		examinationDraftSubmit.setStatus(examDraftSubmitVo.getStatus());
		examinationDraftSubmit.setReviewUnit(orgDao.findOne(examDraftSubmitVo.getReviewUnit().getId()));
		examinationDraftSubmit.setReviewUnitLeader(userDao.findOne(examDraftSubmitVo.getReviewUnitLeader().getId()));
		examinationDraftSubmit.setReviewUnitClerk(userDao.findOne(examDraftSubmitVo.getReviewUnitClerk().getId()));
		examinationDraftSubmit.setExaminationDraft(examDraftSubmitVo.getExaminationDraft());
		examinationDraftSubmit.setDraftingInstruction(examDraftSubmitVo.getDraftingInstruction());
		examinationDraftSubmit.setLegalBasises(examDraftSubmitVo.getLegalBasises());
		examinationDraftSubmit.setLegalBasisesNoAtta(examDraftSubmitVo.getLegalBasisesNoAtta());
		return examinationDraftSubmit;
	}

	@Override
	@Transactional(readOnly = true)
	public ExamDraftSubmitVo examDraftSubToVo(ExaminationDraftSubmit examDraftSubmit) {
		ExamDraftSubmitVo examDraftSubmitVo = new ExamDraftSubmitVo();

		NormativeFileVo normativeFileVo = NormativeFileVo.createVo(norDao.findOne(examDraftSubmit.getNormativeFile().getId()));
		Organization dUnit = orgDao.findOne(examDraftSubmit.getDraftingUnit().getId());
		OrgShortVo draftUnit = OrgShortVo.createVo(dUnit);
		User dUnitLeader = userDao.findOne(examDraftSubmit.getDraftingUnitLeader().getId());
		UserShortVo dLeader = UserShortVo.createVo(dUnitLeader);
		User dUnitClerk = userDao.findOne(Long.valueOf(examDraftSubmit.getDraftingUnitClerk().getId()));
		UserShortVo dClerk = UserShortVo.createVo(dUnitClerk);

		Organization rUnit = orgDao.findOne(examDraftSubmit.getReviewUnit().getId());
		OrgShortVo rdraftUnit = OrgShortVo.createVo(rUnit);
		User rUnitLeader = userDao.findOne(examDraftSubmit.getReviewUnitLeader().getId());
		UserShortVo rLeader = UserShortVo.createVo(rUnitLeader);
		User rUnitClerk = userDao.findOne(Long.valueOf(examDraftSubmit.getReviewUnitClerk().getId()));
		UserShortVo rClerk = UserShortVo.createVo(rUnitClerk);

		examDraftSubmitVo.setId(examDraftSubmit.getId());
		examDraftSubmitVo.setName(examDraftSubmit.getName());
		examDraftSubmitVo.setNormativeFile(normativeFileVo);
		examDraftSubmitVo.setDraftingUnit(draftUnit);
		examDraftSubmitVo.setDraftingUnitLeader(dLeader);
		examDraftSubmitVo.setDraftingUnitClerk(dClerk);
		examDraftSubmitVo.setUnionDraftingUnit(examDraftSubmit.getUnionDraftingUnit());
		examDraftSubmitVo.setUnionDraftingUnitLeader(examDraftSubmit.getUnionDraftingUnitLeader());
		examDraftSubmitVo.setUnionDraftingUnitClerk(examDraftSubmit.getUnionDraftingUnitClerk());
		examDraftSubmitVo.setStatus(examDraftSubmit.getStatus());
		examDraftSubmitVo.setReviewUnit(rdraftUnit);
		examDraftSubmitVo.setReviewUnitLeader(rLeader);
		examDraftSubmitVo.setReviewUnitClerk(rClerk);
		examDraftSubmitVo.setExaminationDraft(examDraftSubmit.getExaminationDraft());
		examDraftSubmitVo.setDraftingInstruction(examDraftSubmit.getDraftingInstruction());
		examDraftSubmitVo.setLegalBasises(examDraftSubmit.getLegalBasises());
		examDraftSubmitVo.setLegalBasisesNoAtta(examDraftSubmit.getLegalBasisesNoAtta());

		return examDraftSubmitVo;
	}

	@Override
	public List<ExamDraftSubmitVo> examDraftSubToVoList(List<ExaminationDraftSubmit> examDraftSubmitList) {
		List<ExamDraftSubmitVo> examVoList = new ArrayList<ExamDraftSubmitVo>();
		for (ExaminationDraftSubmit eds : examDraftSubmitList) {
			examVoList.add(this.examDraftSubToVo(eds));
		}
		return examVoList;
	}

	@Override
	public void submit(ExaminationDraftSubmit examinationDraftSubmit) {
		// TODO Auto-generated method stub

	}

	@Override
	public ExaminationDraftSubmit findByNorId(@Param("norId") long norId) {
		return examDraftSubDao.findByNorId(norId);
	}

	@Override
	public void approve(ExaminationDraftSubmit examinationDraftSubmit) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unApprove(ExaminationDraftSubmit examinationDraftSubmit) {
		// TODO Auto-generated method stub
	}

	@Override
	public void flow(ExaminationDraftSubmit examinationDraftSubmit) {
		// TODO Auto-generated method stub
	}

	@Override
	public void send(String message) {
		// TODO Auto-generated method stub
	}

}
