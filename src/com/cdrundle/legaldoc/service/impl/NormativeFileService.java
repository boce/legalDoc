package com.cdrundle.legaldoc.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IEntitlementDao;
import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IRoleDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.Role;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.OrgType;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.vo.NorFileQueryResultVo;
import com.cdrundle.legaldoc.vo.NorFileQueryVo;
import com.cdrundle.legaldoc.vo.NorFileShortVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.UserShortVo;
import com.cdrundle.security.WebPlatformUser;

/**
 * @author XuBao 规范性文件 2014年6月12日
 */
@Service
public class NormativeFileService implements INormativeFileService {

	@Autowired
	private INormativeFileDao normativeFileDao;
	@Autowired
	private IOrganizationDao organizationDao;
	@Autowired
	private IUserDao userDao;
	@Autowired
	IRoleDao roleDao;
	@Autowired
	IEntitlementDao entitlementDao;
	@PersistenceContext
	private EntityManager em;

	@Override
	@Transactional(readOnly = true)
	public Page<NorFileShortVo> findNorFileByName(String name, Stage stage, int page, int size) {
		// 得到当前登录用户信息
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		long orgId = Long.parseLong(userDetail.getOrgId());
		String unionDrtUnit = userDetail.getOrgId();
		unionDrtUnit = "\"" + unionDrtUnit + "\"";
		String involvedOrges = unionDrtUnit;
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "applyDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<NormativeFile> pages = null;
		if (stage == Stage.SETUP) {
			pages = normativeFileDao.findByNameOnSetup(name, stage, orgId, pageable);
		} else if (stage == Stage.DRAFTING) {
			pages = normativeFileDao.findByNameOnDrafting(name, stage, orgId, pageable);
		} else if (stage == Stage.REQUEST_COMMENT_REQUEST) {
			pages = normativeFileDao.findByNameOnRequestComment(name, stage, involvedOrges, pageable);
		} else if (stage == Stage.REQUEST_COMMENT_FEEDBACK || stage == Stage.REQUEST_COMMENT_ADOPT ||
				stage == Stage.REQUEST_COMMENT_MODIFY || stage == Stage.REQUEST_COMMENT_MODIFY || 
				stage == Stage.LEGAL_REVIEW_REVIEW || stage == Stage.LEGAL_REVIEW_MODIFY) {
			pages = normativeFileDao.findByNameOnRequestComment(name, stage, orgId, pageable);
		} else if(stage == Stage.LEGAL_REVIEW_SUBMIT) {
			pages = normativeFileDao.findByNameOnLegalReview(name, stage, orgId, pageable);
		} else if (stage == Stage.DELIBERATION_REQUEST) {
			pages = normativeFileDao.findByNameOnDeliberationRequest(name, stage, orgId, unionDrtUnit, pageable);
		} else if (stage == Stage.DELIBERATION_PROTOCOL) {
			pages = normativeFileDao.findByNameOnDeliberationProtocol(name, stage, orgId, pageable);
		}else if (stage == Stage.DELIBERATION_MODIFY) {
			pages = normativeFileDao.findByNameOnDeliberationModofily(name, stage, orgId, pageable);
		}else if (stage == Stage.PUBLISH) {
			pages = normativeFileDao.findByNameOnPublish(name, stage, orgId,pageable);
		} else if (stage == Stage.RECORD_REQUEST) {
			pages = normativeFileDao.findByNameOnRecordRequest(name, stage, orgId, pageable);
		} else if (stage == Stage.RECORD_REVIEW) {
			pages = normativeFileDao.findByNameOnRecordReview(name, stage, orgId, pageable);
		} else if (stage == null) {
			pages = normativeFileDao.findByNameForAdjust(name, orgId, unionDrtUnit, pageable);
		}

		List<NorFileShortVo> volist = NorFileShortVo.createVoList(pages.getContent());
		Page<NorFileShortVo> pageVo = new PageImpl<NorFileShortVo>(volist, pageable, pages.getTotalElements());
		return pageVo;
	}

	@Transactional(readOnly = true)
	public Page<NorFileQueryResultVo> findAllForAdjustAndCleanup(NorFileQueryVo queryVo, int page, int size) {
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "publishDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<NormativeFile> cq = cb.createQuery(NormativeFile.class);
		Root<NormativeFile> emp = cq.from(NormativeFile.class);

		cq.select(emp);
		Predicate where = cb.conjunction();
		Predicate condition = buildCondition(where, emp, queryVo, cb);
		cq.where(condition);

		TypedQuery<NormativeFile> query = em.createQuery(cq);
		query.setFirstResult(pageable.getPageSize() * pageable.getPageNumber()).setMaxResults(pageable.getPageSize());
		List<NormativeFile> rows = query.getResultList();
		List<NorFileQueryResultVo> resultVoList = NorFileQueryResultVo.createVoList(rows);
		int totalCount = getTotalCount(queryVo);

		Page<NorFileQueryResultVo> pages = new PageImpl<NorFileQueryResultVo>(resultVoList, pageable, totalCount);

		return pages;
	}

	private int getTotalCount(NorFileQueryVo queryVo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<NormativeFile> emp = cq.from(NormativeFile.class);

		cq.select(cb.countDistinct(emp));
		Predicate where = cb.conjunction();
		Predicate condition = buildCondition(where, emp, queryVo, cb);
		cq.where(condition);

		return em.createQuery(cq).getSingleResult().intValue();

	}

	private Predicate buildCondition(Predicate where, Root<NormativeFile> emp, NorFileQueryVo queryVo, CriteriaBuilder cb) {
		if (queryVo == null)
			return where;
		EntityType<NormativeFile> entityType = emp.getModel();
		List<Predicate> orPredicates = new ArrayList<>();
		Predicate stagePublish = cb.equal(emp.get(entityType.getSingularAttribute("stage", Stage.class)), Stage.PUBLISH);
		Predicate stageReuqest = cb.equal(emp.get(entityType.getSingularAttribute("stage", Stage.class)), Stage.RECORD_REQUEST);
		Predicate stageReview = cb.equal(emp.get(entityType.getSingularAttribute("stage", Stage.class)), Stage.RECORD_REVIEW);
		Predicate stageRegister = cb.equal(emp.get(entityType.getSingularAttribute("stage", Stage.class)), Stage.RECORD_REGISTER);
		orPredicates.add(stagePublish);
		orPredicates.add(stageReuqest);
		orPredicates.add(stageReview);
		orPredicates.add(stageRegister);
		Predicate p = cb.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
		where = cb.and(where, p);
		if (!StringUtils.isEmpty(queryVo.getName())) {
			where = cb.and(where, cb.like(emp.get(entityType.getSingularAttribute("name", String.class)), "%" + queryVo.getName() + "%"));
		}
		if (queryVo.getDrtUnit() != null && queryVo.getDrtUnit().getId() != null) {
			Organization org = organizationDao.findOne(queryVo.getDrtUnit().getId());
			where = cb.and(where, cb.equal(emp.get(entityType.getSingularAttribute("drtUnit", Organization.class)), org));
		}

		if (!StringUtils.isEmpty(queryVo.getLegalBasis())) {
			where = cb.and(where, cb.like(emp.get(entityType.getSingularAttribute("legalBasis", String.class)), "%" + queryVo.getLegalBasis() + "%"));
		}

		if (queryVo.getStartDate() != null && queryVo.getEndDate() != null) {
			where = cb.and(where,
					cb.between(emp.get(entityType.getSingularAttribute("publishDate", Date.class)), queryVo.getStartDate(), queryVo.getEndDate()));
		}

		return where;
	}

	@Override
	@Transactional(readOnly = true)
	public NormativeFileVo findById(Long id) {

		return NormativeFileVo.createVo(normativeFileDao.findOne(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<NorFileShortVo> findNorFileForReference(Stage stage, int page, int size) {
		// 得到当前登录用户信息
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		long orgId = Long.parseLong(userDetail.getOrgId());
		String unionDrtUnit = userDetail.getOrgId();
		unionDrtUnit = "\"" + unionDrtUnit + "\"";
		String involvedOrges = unionDrtUnit;
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "applyDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<NormativeFile> pages = null;
		if (stage == Stage.SETUP) {
			pages = normativeFileDao.findAllOnSetup(stage, orgId, pageable);
		} else if (stage == Stage.DRAFTING) {
			pages = normativeFileDao.findAllOnDrafting(stage, orgId, pageable);
		}else if (stage == Stage.REQUEST_COMMENT_REQUEST) {
			pages = normativeFileDao.findOnRequestComment(stage, involvedOrges, pageable);
		} else if (stage == Stage.REQUEST_COMMENT_FEEDBACK || stage == Stage.REQUEST_COMMENT_ADOPT ||
				stage == Stage.REQUEST_COMMENT_MODIFY || stage == Stage.REQUEST_COMMENT_MODIFY || 
				stage == Stage.LEGAL_REVIEW_REVIEW || stage == Stage.LEGAL_REVIEW_MODIFY) {
			pages = normativeFileDao.findOnRequestComment(stage, orgId, pageable);
		} else if (stage == Stage.LEGAL_REVIEW_SUBMIT) {
			pages = normativeFileDao.findAllOnLegalReview(stage, orgId, pageable);
		} else if (stage == Stage.DELIBERATION_REQUEST) {
			pages = normativeFileDao.findOnDeliberationRequest(stage, orgId, unionDrtUnit, pageable);
		} else if (stage == Stage.DELIBERATION_PROTOCOL) {
			pages = normativeFileDao.findOnDeliberationProtocol(stage, orgId, pageable);
		}else if (stage == Stage.DELIBERATION_MODIFY) {
			pages = normativeFileDao.findOnDeliberationModofily( stage, orgId, pageable);
		}else if (stage == Stage.PUBLISH) {
			pages = normativeFileDao.findOnPublish( stage, orgId,pageable);
		} else if (stage == Stage.RECORD_REQUEST) {
			pages = normativeFileDao.findOnRecordRequest(stage, orgId, pageable);
		} else if (stage == Stage.RECORD_REVIEW) {
			pages = normativeFileDao.findOnRecordReview(stage, orgId, pageable);
		}  else if (stage == null) {
			pages = normativeFileDao.findAllForAdjust(orgId, unionDrtUnit, pageable);
		}

		List<NorFileShortVo> volist = NorFileShortVo.createVoList(pages.getContent());
		Page<NorFileShortVo> pageVo = new PageImpl<NorFileShortVo>(volist, pageable, pages.getTotalElements());
		return pageVo;

	}

	@Override
	@Transactional(readOnly = true)
	public Page<NorFileShortVo> findNorFileByOwnOrg(String name, int page, int size) {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		long orgId = Long.parseLong(userDetail.getOrgId());
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "applyDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<NormativeFile> pages;
		if(StringUtils.isEmpty(name)){
			pages = normativeFileDao.findAllForOwnOrg(orgId, userDetail.getOrgId(), pageable);
		}else{
			pages = normativeFileDao.findAllForOwnOrgByName(name, orgId, userDetail.getOrgId(), pageable);
		}
		List<NorFileShortVo> volist = NorFileShortVo.createVoList(pages.getContent());
		Page<NorFileShortVo> pageVo = new PageImpl<NorFileShortVo>(volist, pageable, pages.getTotalElements());
		return pageVo;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<NorFileShortVo> findNorFileByOrg(String name, OrgType orgType, int page, int size) {
		Set<Long> orgIds = new HashSet<>();
		List<Organization> orgAuths = getOrgAuth();
		for (Iterator<Organization> iterator = orgAuths.iterator(); iterator.hasNext();) {
			Organization organization = iterator.next();
			if(organization.getOrgType().equals(orgType)){
				orgIds.add(organization.getId());
			}
		}
		List<Order> orders = new ArrayList<Order>();
		orders.add(new Order(Direction.DESC, "applyDate"));
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<NormativeFile> pages;
		Page<NorFileShortVo> pageVo = null;
		if(!orgIds.isEmpty()){
			if(StringUtils.isEmpty(name)){
				pages = normativeFileDao.findNorFileOnPublish(orgIds, pageable);
			}else{
				pages = normativeFileDao.findNorFileOnPublishByName("%" + name + "%", orgIds, pageable);
			}
			List<NorFileShortVo> volist = NorFileShortVo.createVoList(pages.getContent());
			pageVo = new PageImpl<NorFileShortVo>(volist, pageable, pages.getTotalElements());
		}
		return pageVo;
	}

	/**
	 * 获取登录用户授权的组织机构
	 * @return
	 */
	private List<Organization> getOrgAuth() {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Set<Long> roleIds = new HashSet<>();
		List<GrantedAuthority> authorities = userDetail.getAuthorities();
		for (Iterator<GrantedAuthority> iterator = authorities.iterator(); iterator.hasNext();) {
			GrantedAuthority grantedAuthority = iterator.next();
			String authority = grantedAuthority.getAuthority();
			Role role = roleDao.findByName(authority);
			roleIds.add(role.getId());
		}
		return entitlementDao.findOrgAuth(roleIds);
	}
	
	public void genName(NormativeFileVo norFile) {
		String unionDrtUnit = norFile.getUnionDrtUnit();
		if(StringUtils.isNotEmpty(unionDrtUnit)){
			String unionDrtUnitName = getOrgNameById(unionDrtUnit);
			norFile.setUnionDrtUnitName(unionDrtUnitName);
		}
		String unionDrtUnitLeader = norFile.getUnionDrtUnitLeader();
		if(StringUtils.isNotEmpty(unionDrtUnitLeader)){
			String unionDrtUnitLeaderName = getUserNameById(unionDrtUnitLeader);
			norFile.setUnionDrtUnitLeaderName(unionDrtUnitLeaderName);
		}
		String involvedOrges = norFile.getInvolvedOrges();
		if(StringUtils.isNotEmpty(involvedOrges)){
			String involvedOrgesName = getOrgNameById(involvedOrges);
			norFile.setInvolvedOrgesName(involvedOrgesName);
		}
	}

	private String getUserNameById(String unionDrtUnitLeader) {
		unionDrtUnitLeader = unionDrtUnitLeader.replaceAll("\"", "");
		String[] unionDrtUnitLeaderArray = unionDrtUnitLeader.split(",");
		Set<Long> unionDrtUnitLeaderSet = new LinkedHashSet<>();
		for (int i = 0; i < unionDrtUnitLeaderArray.length; i++) {
			unionDrtUnitLeaderSet.add(Long.parseLong(unionDrtUnitLeaderArray[i]));
		}
		List<User> users = userDao.findUserByIds(unionDrtUnitLeaderSet);
		List<UserShortVo> userShortVos = UserShortVo.createVoList(users);;
		String unionDrtUnitLeaderName = "";
		for (Iterator<Long> iterator = unionDrtUnitLeaderSet.iterator(); iterator.hasNext();) {
			Long userId = iterator.next();
			for (Iterator<UserShortVo> iterator2 = userShortVos.iterator(); iterator2.hasNext();) {
				UserShortVo userShortVo = iterator2.next();
				if(userId.equals(userShortVo.getId())){
					if("".equals(unionDrtUnitLeaderName)){
						unionDrtUnitLeaderName = userShortVo.getName();
					}else{
						unionDrtUnitLeaderName += ("," + userShortVo.getName());
					}
				}
			}
		}
		return unionDrtUnitLeaderName;
	}

	private String getOrgNameById(String unionDrtUnit) {
		unionDrtUnit = unionDrtUnit.replaceAll("\"", "");
		String[] unionDrtUnitArray = unionDrtUnit.split(",");
		Set<Long> unionDrtUnitSet = new LinkedHashSet<>();
		for (int i = 0; i < unionDrtUnitArray.length; i++) {
			unionDrtUnitSet.add(Long.parseLong(unionDrtUnitArray[i]));
		}
		List<Organization> orgList = organizationDao.findOrgByIds(unionDrtUnitSet);
		List<OrgShortVo> orgShortVos = OrgShortVo.createVoListNoChild(orgList);
		String unionDrtUnitName = "";
		for (Iterator<Long> iterator = unionDrtUnitSet.iterator(); iterator.hasNext();) {
			Long orgId = iterator.next();
			for (Iterator<OrgShortVo> iterator2 = orgShortVos.iterator(); iterator2.hasNext();) {
				OrgShortVo orgShortVo = iterator2.next();
				if(orgId.equals(orgShortVo.getId())){
					if("".equals(unionDrtUnitName)){
						unionDrtUnitName = orgShortVo.getText();
					}else{
						unionDrtUnitName += ("," + orgShortVo.getText());
					}
				}
			}
		}
		return unionDrtUnitName;
	}
	
	@Override
	public void genNameList(List<NormativeFileVo> norFiles) {
		for (Iterator<NormativeFileVo> iterator = norFiles.iterator(); iterator.hasNext();) {
			NormativeFileVo normativeFileVo = iterator.next();
			genName(normativeFileVo);
		}
	}
	
}
