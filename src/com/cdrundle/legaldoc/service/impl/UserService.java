package com.cdrundle.legaldoc.service.impl;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IRoleDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.Role;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.service.IUserService;
import com.cdrundle.legaldoc.util.MD5Util;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.vo.RoleVo;
import com.cdrundle.legaldoc.vo.UserShortVo;
import com.cdrundle.legaldoc.vo.UserVo;
import com.cdrundle.security.WebPlatformUser;

@Service
public class UserService implements IUserService {

	private static final String DEFAULT_ROLE_NAME = "ROLE_USER";

	@Autowired
	IUserDao userDao;

	@Autowired
	IRoleDao roleDao;

	@Autowired
	IOrganizationDao organizationDao;

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional(readOnly = true)
	public UserVo findByUserName(String userName) {
		return UserVo.createVo(userDao.findByUserName(userName));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserVo> findUserRef(String name, Long orgId, int page, int size) {
		Pageable pageable = new PageRequest(page, size);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> root = cq.from(User.class);
		cq.select(root);
		Predicate where = cb.conjunction();
		where = buildCondition(where, root, name, orgId, cb);
		cq.where(where);
		TypedQuery<User> query = em.createQuery(cq);
		query.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber())).setMaxResults(pageable.getPageSize());
		List<User> rows = query.getResultList();

		int totalCount = getTotalCount(name, orgId);

		List<UserVo> volist = UserVo.createVoList(rows);
		Page<UserVo> pages = new PageImpl<UserVo>(volist, pageable, totalCount);
		return pages;
	}

	private int getTotalCount(String name, Long orgId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> emp = cq.from(User.class);
		cq.select(cb.countDistinct(emp));
		Predicate where = cb.conjunction();
		where = buildCondition(where, emp, name, orgId, cb);
		cq.where(where);

		return em.createQuery(cq).getSingleResult().intValue();

	}

	private Predicate buildCondition(Predicate where, Root<User> root, String name, Long orgId, CriteriaBuilder cb) {
		EntityType<User> et = root.getModel();
		if (StringUtils.isNotEmpty(name)) {
			where = cb.and(where, cb.like(root.get(et.getSingularAttribute("name", String.class)), "%" + name + "%"));
		}
		if (orgId != null) {
			Organization org = organizationDao.findOne(orgId);
			where = cb.and(where, cb.equal(root.get(et.getSingularAttribute("organization", Organization.class)), org));
		}

		return where;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserVo> findByAll(int page, int size) {
		Pageable pageable = new PageRequest(page, size);
		Page<User> userPage = userDao.findAll(pageable);
		List<UserVo> volist = UserVo.createVoList(userPage.getContent());
		Page<UserVo> pages = new PageImpl<UserVo>(volist, pageable, userPage.getTotalElements());
		return pages;
	}

	@Override
	@Transactional
	public UserVo saveOrUpdate(UserVo userVo) throws NoSuchAlgorithmException {
		User savedUser;
		// 如果是新增用户，默认赋予登录用户角色
		if (userVo.getId() == null) {
			User user = convertToUser(userVo);
			Role role = roleDao.findByName(DEFAULT_ROLE_NAME);
			List<Role> roles = new ArrayList<>();
			roles.add(role);
			user.setRoles(roles);

			user.setPassword(MD5Util.getMd5Str(userVo.getPassword()));
			savedUser = userDao.save(user);
		} else {
			User user = userDao.findOne(userVo.getId());
			user.setName(userVo.getName());
			user.setUserName(userVo.getUserName());
			user.setPhone(userVo.getPhone());
			user.setEmail(userVo.getEmail());
			user.setMobile(userVo.getMobile());
			user.setIsIncharge(userVo.getIsIncharge());
			user.setIsUsed(userVo.getIsUsed());
			user.setOrganization(organizationDao.findOne(userVo.getOrganization().getId()));
			savedUser = userDao.save(user);
		}
		return UserVo.createVo(savedUser);
	}

	@Override
	@Transactional
	public void delete(UserVo userVo) {
		userDao.delete(convertToUser(userVo));
	}

	@Override
	@Transactional
	public void delete(long id) {
		userDao.delete(id);
	}

	@Override
	@Transactional
	public boolean deleteById(long id) {
		userDao.deleteById(id);
		return false;
	}

	@Override
	@Transactional
	public boolean authorize(Set<Long> roleIds, Long userId) {
		User user = userDao.findOne(userId);
		if (roleIds != null && roleIds.size() > 0) {
			List<Role> roles = roleDao.findRoles(roleIds);
			user.setRoles(roles);
		} else {
			user.getRoles().clear();
		}
		User savedUser = userDao.save(user);
		return savedUser == null ? false : true;
	}

	@Override
	@Transactional(readOnly = true)
	public UserVo findById(long id) {
		return UserVo.createVo(userDao.findOne(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserVo> findByOrgId(Long orgId) {
		List<User> users;
		if(orgId == null){
			users = userDao.findAll();
		}else{
			users = userDao.findByOrgId(orgId);
		}
		return UserVo.createVoList(users);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserVo> findSuperByOrgId(Long orgId) {
		if(orgId == null){
			return null;
		}
		Organization org = organizationDao.findOne(orgId);
		Organization parentOrg = org.getParentOrganization();
		List<User> users = null;
		if(parentOrg != null){
			users = userDao.findByOrgId(parentOrg.getId());;
		}
		return UserVo.createVoList(users);
	}
	
	@Override
	@Transactional(readOnly = true)
	public UserShortVo findByIdShort(long id) {
		return UserShortVo.createVo(userDao.findOne(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<RoleVo> findRoleById(Long userId) {
		List<Role> roles = userDao.findRoleById(userId);
		return RoleVo.createVoList(roles);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean judgePassword(String password) throws NoSuchAlgorithmException {
		boolean flag = false;
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long userId = Long.parseLong(userDetail.getUserId());
		User user = userDao.findOne(userId);
		if(user.getPassword().equals(MD5Util.getMd5Str(password))){
			flag = true;
		}
		return flag;
	}

	@Override
	@Transactional
	public boolean updatePassword(String password) throws NoSuchAlgorithmException {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		Long userId = Long.parseLong(userDetail.getUserId());
		User user = userDao.findOne(userId);
		user.setPassword(MD5Util.getMd5Str(password));
		User savedUser = userDao.save(user);
		return savedUser == null ? false : true;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<UserShortVo> findByIdsShort(Set<Long> ids) {
		List<User> users = userDao.findUserByIds(ids);
		return UserShortVo.createVoList(users);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<UserVo> findChargeByOrg(Long orgId) {
		return UserVo.createVoList(userDao.findChargeByOrg(orgId));
	}
	
	public User convertToUser(UserVo userVo) {
		User user = new User();
		user.setId(userVo.getId());
		user.setName(userVo.getName());
		user.setUserName(userVo.getUserName());
		user.setPassword(userVo.getPassword());
		user.setPhone(userVo.getPhone());
		user.setEmail(userVo.getEmail());
		user.setMobile(userVo.getMobile());
		user.setIsIncharge(userVo.getIsIncharge());
		user.setIsUsed(userVo.getIsUsed());
		user.setOrganization(organizationDao.findOne(userVo.getOrganization().getId()));
		return user;
	}

	public User convertToUser(UserShortVo userShortVo) {
		User user = new User();
		user.setId(userShortVo.getId());
		user.setName(userShortVo.getName());
		return user;
	}

}
