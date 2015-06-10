package com.cdrundle.legaldoc.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

import com.cdrundle.legaldoc.dao.IEntitlementDao;
import com.cdrundle.legaldoc.dao.IMenuDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IPageDao;
import com.cdrundle.legaldoc.dao.IPageSourceDao;
import com.cdrundle.legaldoc.dao.IRoleDao;
import com.cdrundle.legaldoc.entity.Entitlment;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.Role;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IRoleService;
import com.cdrundle.legaldoc.vo.MenuVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.PageSourceVo;
import com.cdrundle.legaldoc.vo.PageVo;
import com.cdrundle.legaldoc.vo.RoleVo;

@Service
public class RoleService implements IRoleService {
	
	@Autowired
	IRoleDao roleDao;

	@Autowired
	IEntitlementDao entitlementDao;

	@Autowired
	IMenuDao menuDao;

	@Autowired
	IPageDao pageDao;

	@Autowired
	IPageSourceDao pageSourceDao;
	
	@Autowired
	IOrganizationDao orgDao;

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional
	public RoleVo saveOrUpdate(RoleVo role) {
		Role saveRole = roleDao.saveAndFlush(convertToRole(role));
		return RoleVo.createVo(saveRole);
	}

	@Override
	@Transactional
	public void delete(RoleVo role) {
		roleDao.delete(convertToRole(role));
	}

	@Override
	@Transactional(readOnly = true)
	public RoleVo findByName(String name) {
		return RoleVo.createVo(roleDao.findByName(name));
	}

	@Override
	@Transactional(readOnly = true)
	public RoleVo findById(long id) {
		return RoleVo.createVo(roleDao.findOne(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<RoleVo> findRoleRef(String description, int page, int size) {
		Pageable pageable = new PageRequest(page, size);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Role> cq = cb.createQuery(Role.class);
		Root<Role> root = cq.from(Role.class);
		cq.select(root);
		Predicate where = cb.conjunction();
		where = buildCondition(where, root, description, cb);
		cq.where(where);
		TypedQuery<Role> query = em.createQuery(cq);
		query.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber())).setMaxResults(pageable.getPageSize());
		List<Role> rows = query.getResultList();

		int totalCount = getTotalCount(description);

		List<RoleVo> volist = RoleVo.createVoList(rows);
		Page<RoleVo> pages = new PageImpl<RoleVo>(volist, pageable, totalCount);
		return pages;
	}

	/**
	 * 获取总条数
	 * 
	 * @param description
	 * @return
	 */
	private int getTotalCount(String description) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Role> emp = cq.from(Role.class);
		cq.select(cb.countDistinct(emp));
		Predicate where = cb.conjunction();
		where = buildCondition(where, emp, description, cb);
		cq.where(where);

		return em.createQuery(cq).getSingleResult().intValue();

	}

	/**
	 * 组装查询条件
	 * 
	 * @param where
	 * @param root
	 * @param description
	 * @param cb
	 * @return
	 */
	private Predicate buildCondition(Predicate where, Root<Role> root, String description, CriteriaBuilder cb) {
		EntityType<Role> et = root.getModel();
		if (StringUtils.isNotEmpty(description)) {
			where = cb.and(where, cb.like(root.get(et.getSingularAttribute("description", String.class)), "%" + description + "%"));
		}
		return where;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RoleVo> findLikeName(String name) {
		return RoleVo.createVoList(roleDao.findLikeName(name));
	}

	@Override
	@Transactional(readOnly = true)
	public List<RoleVo> findAllEnable() {
		List<Role> roles = roleDao.findAllEnable();
		return RoleVo.createVoList(roles);
	}

	@Override
	@Transactional
	public boolean deleteById(Long id) {
		roleDao.delete(id);
		return true;
	}

	@Override
	@Transactional
	public boolean deleteByIdVirtual(Long id) throws ServiceException {
		if (id == null) {
			throw new ServiceException("id不能为空");
		}
		roleDao.deleteByIdVirtual(id);
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuVo> getAllAuthMenu(Long roleId) {
		List<MenuVo> menuVoList = MenuVo.createVoList(entitlementDao.findMenuAuth(roleId));
		List<PageVo> pageVoList = PageVo.createVoList(entitlementDao.findPageAuth(roleId));
		List<PageSourceVo> pageSourceVoList = PageSourceVo.createVoList(entitlementDao.findPageSourceAuth(roleId));
		for (Iterator<PageVo> iterator = pageVoList.iterator(); iterator.hasNext();) {
			PageVo pageVo = iterator.next();
			ArrayList<PageSourceVo> pageSourceVos = new ArrayList<PageSourceVo>();
			for (Iterator<PageSourceVo> iterator2 = pageSourceVoList.iterator(); iterator2.hasNext();) {
				PageSourceVo pageSourceVo = iterator2.next();
				if (pageSourceVo.getPage() != null && pageVo.getId().equals(pageSourceVo.getPage().getId())) {
					pageSourceVos.add(pageSourceVo);
				}
			}
			Collections.sort(pageSourceVos);
			pageVo.setPageSources(pageSourceVos);

		}
		for (Iterator<MenuVo> iterator = menuVoList.iterator(); iterator.hasNext();) {
			MenuVo menuVo = iterator.next();
			ArrayList<PageVo> pageVos = new ArrayList<PageVo>();
			for (Iterator<PageVo> iterator2 = pageVoList.iterator(); iterator2.hasNext();) {
				PageVo pageVo = iterator2.next();
				if (pageVo.getMenu() != null && menuVo.getId().equals(pageVo.getMenu().getId())) {
					pageVos.add(pageVo);
				}
			}
			Collections.sort(pageVos);
			menuVo.setPages(pageVos);
		}
		Collections.sort(menuVoList);
		return menuVoList;
	}

	@Override
	@Transactional
	public void optAuthorize(Long[][] auths, Long roleId) {
		entitlementDao.deleteByRole(roleId);
		Role role = roleDao.findOne(roleId);
		List<Entitlment> entitlments = new ArrayList<>();
		for (int i = 0, len = auths.length; i < len; i++) {
			Entitlment entitlment = new Entitlment();
			entitlment.setRole(role);
			if (auths[i][0] != null) {
				entitlment.setMenu(menuDao.findOne(auths[i][0]));
			}
			if (auths[i][1] != null) {
				entitlment.setPage(pageDao.findOne(auths[i][1]));
			}
			if (auths[i][2] != null) {
				entitlment.setPageSource(pageSourceDao.findOne(auths[i][2]));
			}
			entitlments.add(entitlment);
		}
		entitlementDao.save(entitlments);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrgShortVo> findAllAuthOrg(Long roleId) {
		List<Organization> orgAuths = entitlementDao.findOrgAuth(roleId);
		return OrgShortVo.createVoListNoChild(orgAuths);
	}
	
	public Role convertToRole(RoleVo roleVo) {
		Role role = new Role();
		role.setId(roleVo.getId());
		role.setName(roleVo.getName());
		role.setDescription(roleVo.getDescription());
		role.setIsUsed(roleVo.getIsUsed());
		return role;
	}

	public List<Role> convertToRoleList(List<RoleVo> roleVos) {
		List<Role> roles = new ArrayList<>();
		if (roleVos == null || roleVos.size() == 0) {
			return roles;
		}
		for (Iterator<RoleVo> iterator = roleVos.iterator(); iterator.hasNext();) {
			roles.add(convertToRole(iterator.next()));
		}
		return roles;
	}

	@Override
	@Transactional
	public boolean dataAuthorize(Long[] orgIds, Long roleId) {
		entitlementDao.deleteDataAuthByRole(roleId);
		if(orgIds != null && orgIds.length > 0){
			Role role = roleDao.findOne(roleId);
			List<Entitlment> entitlments = new ArrayList<>();
			for (int i = 0, len = orgIds.length; i < len; i++) {
				Entitlment entitlment = new Entitlment();
				entitlment.setRole(role);;
				Organization org = orgDao.findOne(orgIds[i]);
				entitlment.setOrganization(org);
				entitlments.add(entitlment);
			}
			entitlementDao.save(entitlments);
		}
		return true;
	}

}
