package com.cdrundle.legaldoc.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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

import com.cdrundle.legaldoc.dao.IDeferredAssessDao;
import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.DeferredAssessment;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.AssessResult;
import com.cdrundle.legaldoc.enums.FileStatus;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IDeferredAssessService;
import com.cdrundle.legaldoc.vo.DeferredAssessmentVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.UserShortVo;

@Service
public class DeferredAssessService implements IDeferredAssessService{

	@Autowired
	private IDeferredAssessDao daDao;
	
	@Autowired
	private IOrganizationDao orgDao;
	
	@Autowired
	private IUserDao userDao;
	
	@Autowired 
	private INormativeFileDao norDao;
	
	@PersistenceContext
	EntityManager em;
	
	@Override
	public DeferredAssessment voToDA(DeferredAssessmentVo daVo) {
		DeferredAssessment da = new DeferredAssessment();
		
		da.setAssessComment(daVo.getAssessComment());
		da.setAssessDate(daVo.getAssessDate());
		da.setAssessResult(daVo.getAssessResult());
		da.setDecisionUnit(orgDao.findOne(daVo.getDecisionUnit().getId()));
		da.setDecisionUnitClerk(userDao.findOne(daVo.getDecisionUnitClerk().getId()));
		da.setDecisionUnitLeader(userDao.findOne(daVo.getDecisionUnitLeader().getId()));
		da.setDraftingInstruction(daVo.getDraftingInstruction());
		da.setDraftingUnit(orgDao.findOne(daVo.getDraftingUnit().getId()));
		da.setDraftingUnitClerk(userDao.findOne(daVo.getDraftingUnitClerk().getId()));
		da.setDraftingUnitLeader(userDao.findOne(daVo.getDraftingUnitLeader().getId()));
		da.setId(daVo.getId());
		da.setLegalBasis(daVo.getLegalBasis());
		da.setLegalDoc(daVo.getLegalDoc());
		da.setName(daVo.getName());
		da.setNormativeFile(norDao.findOne(daVo.getNormativeFile().getId()));
		da.setStatus(daVo.getStatus());
		da.setValidDate(daVo.getValidDate());
		
		return da;
	}
	
	/**
	 * 实体转换成Vo
	 * @param da
	 * @return DeferredAssessmentVo
	 */
	@Override
	public DeferredAssessmentVo daToVo(DeferredAssessment da) {
		DeferredAssessmentVo daVo = new DeferredAssessmentVo();
		
		Organization decUnit = orgDao.findOne(da.getDecisionUnit().getId());
		OrgShortVo dUnit = OrgShortVo.createVo(decUnit);
		User decLeader = userDao.findOne(da.getDecisionUnitLeader().getId());
		UserShortVo dLeader = UserShortVo.createVo(decLeader);
		User decClerk = userDao.findOne(Long.valueOf(da.getDecisionUnitClerk().getId()));
		UserShortVo dClerk = UserShortVo.createVo(decClerk);
		
		Organization draftUnit = orgDao.findOne(da.getDraftingUnit().getId());
		OrgShortVo daUnit = OrgShortVo.createVo(draftUnit);
		User draftLeader = userDao.findOne(da.getDraftingUnitLeader().getId());
		UserShortVo daLeader = UserShortVo.createVo(draftLeader);
		User draftClerk = userDao.findOne(da.getDraftingUnitClerk().getId());
		UserShortVo daClerk = UserShortVo.createVo(draftClerk);
		
		NormativeFileVo norFileVo = NormativeFileVo.createVo(norDao.
				findOne(da.getNormativeFile().getId()));
		
		
		daVo.setAssessComment(da.getAssessComment());
		daVo.setAssessDate(da.getAssessDate());
		daVo.setAssessResult(da.getAssessResult());
		daVo.setDecisionUnit(dUnit);
		daVo.setDecisionUnitClerk(dClerk);
		daVo.setDecisionUnitLeader(dLeader);
		daVo.setDraftingInstruction(da.getDraftingInstruction());
		daVo.setDraftingUnit(daUnit);
		daVo.setDraftingUnitClerk(daClerk);
		daVo.setDraftingUnitLeader(daLeader);
		daVo.setId(da.getId());
		daVo.setLegalBasis(da.getLegalBasis());
		daVo.setLegalDoc(da.getLegalDoc());
		daVo.setName(da.getName());
		daVo.setNormativeFile(norFileVo);
		daVo.setStatus(da.getStatus());
		daVo.setValidDate(da.getValidDate());
		
		return daVo;
	}
	
	/**
	 * 实体集合转换成VoList
	 * @param daList
	 * @return List<DeferredAssessmentVo>
	 */
	@Override
	public List<DeferredAssessmentVo> daToVoList(List<DeferredAssessment> daList) {
		List<DeferredAssessmentVo> daVoList = new ArrayList<DeferredAssessmentVo>();
		for (DeferredAssessment da : daList) {
			daVoList.add(this.daToVo((da)));
		}
		return daVoList;
	}
	
	@Override
	@Transactional
	public DeferredAssessmentVo saveOrUpdate(DeferredAssessmentVo daVo) throws ServiceException{
		NormativeFile norFile = null;
		DeferredAssessment da = null;
		if (daVo != null) {
			norFile = norDao.findOne(daVo.getNormativeFile().getId());
			AssessResult assessResult = daVo.getAssessResult();
			if(AssessResult.MODIFY_DELAY.equals(assessResult)){
				norFile.setStatus(FileStatus.MODIFY);
			}else if(AssessResult.REVOKE.equals(assessResult)){
				norFile.setStatus(FileStatus.REVOKE);
			}
			norDao.save(norFile);
			if (daVo.getId() == null) {	//新增
				Organization decUnit = orgDao.findOne(norFile.getDecUnit().getId());
				OrgShortVo dUnit = OrgShortVo.createVo(decUnit);
				User decLeader = userDao.findOne(norFile.getDecUnitLeader().getId());
				UserShortVo dLeader = UserShortVo.createVo(decLeader);
				User decClerk = userDao.findOne(Long.valueOf(norFile.getDecUnitClerk().getId()));
				UserShortVo dClerk = UserShortVo.createVo(decClerk);
				
				Organization draftUnit = orgDao.findOne(norFile.getDrtUnit().getId());
				OrgShortVo daUnit = OrgShortVo.createVo(draftUnit);
				User draftLeader = userDao.findOne(norFile.getDrtUnitLeader().getId());
				UserShortVo daLeader = UserShortVo.createVo(draftLeader);
				User draftClerk = userDao.findOne(norFile.getDrtUnitClerk().getId());
				UserShortVo daClerk = UserShortVo.createVo(draftClerk);
				
				//从norFile设值给VO
				daVo.setDecisionUnit(dUnit);
				daVo.setDecisionUnitClerk(dClerk);
				daVo.setDecisionUnitLeader(dLeader);
				
				daVo.setDraftingUnit(daUnit);
				daVo.setDraftingUnitClerk(daClerk);
				daVo.setDraftingUnitLeader(daLeader);
				
				daVo.setDraftingInstruction(norFile.getDraftInstruction());
				daVo.setLegalBasis(norFile.getLegalBasis());
				daVo.setLegalDoc(norFile.getLegalDoc());
				daVo.setName(norFile.getName());
				daVo.setStatus(Status.OPEN);
				
				da = daDao.save(this.voToDA(daVo));
				
			} else {	//更新
				DeferredAssessment deferr = daDao.findOne(daVo.getId());
				deferr.setAssessComment(daVo.getAssessComment());
				deferr.setAssessDate(daVo.getAssessDate());
				deferr.setAssessResult(daVo.getAssessResult());
				deferr.setValidDate(daVo.getValidDate());
				da = daDao.save(deferr);
			}
		} else {
			throw new ServiceException("期满评估新增出错!");
		}
		
		return this.daToVo(da);
	}

	@Override
	@Transactional
	public boolean delete(DeferredAssessmentVo daVo) throws ServiceException {
		boolean flag = false;
		if (daVo != null) {
			daDao.delete(this.voToDA(daVo));
			flag = true;
		} else {
			throw new ServiceException("期满评估删除时出错！");
		}
		return flag;
	}

	
	@Override
	@Transactional(readOnly = true)
	public Page<DeferredAssessmentVo> findAll(String name, Long draftingUnit, Long decisionUnit,
			Status status, Integer page, Integer size){
		Pageable pageable = new PageRequest(page, size);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DeferredAssessment> cq = cb.createQuery(DeferredAssessment.class);
		Root<DeferredAssessment> root = cq.from(DeferredAssessment.class);
		cq.select(root);
		Predicate where = cb.conjunction();
		where = buildCondition(where, root, name, draftingUnit, decisionUnit, status, cb);
		cq.where(where);
		TypedQuery<DeferredAssessment> query = em.createQuery(cq);
		query.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber())).setMaxResults(pageable.getPageSize());
		List<DeferredAssessment> rows = query.getResultList();

		int totalCount = getTotalCount(name, draftingUnit, decisionUnit, status);

		List<DeferredAssessmentVo> volist = this.daToVoList(rows);
		Page<DeferredAssessmentVo> pages = new PageImpl<DeferredAssessmentVo>(volist, pageable, totalCount);
		return pages;
	}
	
	/**
	 * 
	 * @param name
	 * @param draftingUnit
	 * @param decisionUnit
	 * @param status
	 * @return
	 */
	private int getTotalCount(String name, Long draftingUnit, Long decisionUnit, Status status) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<DeferredAssessment> emp = cq.from(DeferredAssessment.class);
		cq.select(cb.countDistinct(emp));
		Predicate where = cb.conjunction();
		where = buildCondition(where, emp, name, draftingUnit, decisionUnit, status, cb);
		cq.where(where);

		return em.createQuery(cq).getSingleResult().intValue();

	}
	
	/**
	 * 组合条件查询
	 * @param where
	 * @param root
	 * @param name
	 * @param draftingUnit
	 * @param decisionUnit
	 * @param status
	 * @param cb
	 * @return
	 */
	private Predicate buildCondition(Predicate where, Root<DeferredAssessment> root, String name, Long draftingUnit, Long decisionUnit,
			Status status, CriteriaBuilder cb) {
		EntityType<DeferredAssessment> et = root.getModel();
		if (!StringUtils.isEmpty(name)) {
			where = cb.and(where, cb.like(root.get(et.getSingularAttribute("name", String.class)), name));
		}
		if (draftingUnit != null) {
			Organization draOrg = orgDao.findOne(draftingUnit);
			if (draOrg != null) {
				where = cb.and(where, cb.equal(root.get(et.getSingularAttribute("draftingUnit", Organization.class)), draOrg));
			}
		}
		if (decisionUnit != null) {
			Organization decOrg = orgDao.findOne(decisionUnit);
			if (decOrg != null) {
				where = cb.and(where, cb.equal(root.get(et.getSingularAttribute("decisionUnit", Organization.class)), decOrg));
			}
		}
		if (status != null) {
			where = cb.and(where, cb.equal(root.get(et.getSingularAttribute("status", Status.class)), status));
		}
		return where;
	}
	
	@Override
	@Transactional(readOnly = true)
	public DeferredAssessmentVo findById(Long id) {
		DeferredAssessment da = daDao.findOne(id);
		if (da != null) {
			return this.daToVo(da);
		} else {
			return null;
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public DeferredAssessmentVo findByNorFileId(Long id){
		if (id != null) {
			DeferredAssessment da = daDao.findByNorFileId(id);
			if (da != null) {
				return this.daToVo(da);
			}
		}
		return null;
	}
	
	@Override
	public List<DeferredAssessment> findByName(String deferredAssessmentName) {
		if (deferredAssessmentName != null && !"".equals(deferredAssessmentName)) {
			return daDao.findByName(deferredAssessmentName);
		} else {
			return null;
		}
	}

	@Override
	public void submit(DeferredAssessment deferredAssessment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void approve(DeferredAssessment deferredAssessment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unApprove(DeferredAssessment deferredAssessment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flow(DeferredAssessment deferredAssessment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publish(String message) {
		// TODO Auto-generated method stub
		
	}
}
