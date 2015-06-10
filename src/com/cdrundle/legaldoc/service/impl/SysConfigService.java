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

import com.cdrundle.legaldoc.dao.ISysConfigDao;
import com.cdrundle.legaldoc.entity.SysConfig;
import com.cdrundle.legaldoc.service.ISysConfigService;
import com.cdrundle.legaldoc.vo.SysConfigVo;

@Service
public class SysConfigService implements ISysConfigService {

	@Autowired
	ISysConfigDao sysConfigDao;

	@PersistenceContext
	EntityManager em;

	@Override
	@Transactional
	public SysConfigVo saveOrUpdate(SysConfigVo sysConfigVo) {
		Long id = sysConfigVo.getId();
		SysConfig savedSysConfig;
		if (id == null) {
			SysConfig sysConfig = convertToSysConfig(sysConfigVo);
			savedSysConfig = sysConfigDao.save(sysConfig);
		}
		else{
			SysConfig sysConfig = sysConfigDao.findOne(id);
			sysConfig.setCode(sysConfigVo.getCode());
			sysConfig.setName(sysConfigVo.getName());
			sysConfig.setValue(sysConfigVo.getValue());
			sysConfig.setDescription(sysConfigVo.getDescription());
			sysConfig.setIsUsed(sysConfigVo.getIsUsed());;
			savedSysConfig = sysConfigDao.save(sysConfig);
		}
		return SysConfigVo.createVo(savedSysConfig);
	}

	@Override
	@Transactional
	public boolean delete(SysConfigVo sysConfigVo) {
		sysConfigDao.delete(sysConfigDao.findOne(sysConfigVo.getId()));
		return true;
	}

	@Override
	@Transactional
	public boolean delete(long id) {
		sysConfigDao.delete(id);
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public SysConfigVo findById(long id) {
		return SysConfigVo.createVo(sysConfigDao.findOne(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<SysConfigVo> findAll() {
		return SysConfigVo.createVoList(sysConfigDao.findAll());
	}

	@Override
	@Transactional(readOnly = true)
	public SysConfigVo findByCode(String code) {
		return SysConfigVo.createVo(sysConfigDao.findByCode(code));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<SysConfigVo> findSysConfigRef(String code, String name, int page, int size) {
		Pageable pageable = new PageRequest(page, size);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<SysConfig> cq = cb.createQuery(SysConfig.class);
		Root<SysConfig> root = cq.from(SysConfig.class);
		cq.select(root);
		Predicate where = cb.conjunction();
		where = buildCondition(where, root, code, name, cb);
		cq.where(where);
		TypedQuery<SysConfig> query = em.createQuery(cq);
		query.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber())).setMaxResults(pageable.getPageSize());
		List<SysConfig> rows = query.getResultList();

		int totalCount = getTotalCount(code, name);

		List<SysConfigVo> volist = SysConfigVo.createVoList(rows);
		if(volist == null){
			volist = new ArrayList<>();
		}
		Page<SysConfigVo> pages = new PageImpl<SysConfigVo>(volist, pageable, totalCount);
		return pages;
	}

	private int getTotalCount(String code, String name) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<SysConfig> emp = cq.from(SysConfig.class);
		cq.select(cb.countDistinct(emp));
		Predicate where = cb.conjunction();
		where = buildCondition(where, emp, code, name, cb);
		cq.where(where);

		return em.createQuery(cq).getSingleResult().intValue();

	}

	private Predicate buildCondition(Predicate where, Root<SysConfig> root, String code, String name, CriteriaBuilder cb) {
		EntityType<SysConfig> et = root.getModel();
		if (StringUtils.isNotEmpty(code)) {
			where = cb.and(where, cb.like(root.get(et.getSingularAttribute("code", String.class)), "%" + code + "%"));
		}
		if (StringUtils.isNotEmpty(name)) {
			where = cb.and(where, cb.like(root.get(et.getSingularAttribute("name", String.class)), "%" + name + "%"));
		}
		return where;
	}

	@Override
	public boolean deleteById(long id) {
		sysConfigDao.deleteById(id);
		return true;
	}
	
	public SysConfig convertToSysConfig(SysConfigVo sysConfigVo) {
		SysConfig sysConfig = new SysConfig();
		if (sysConfigVo == null) {
			return null;
		}
		sysConfig.setId(sysConfigVo.getId());
		sysConfig.setCode(sysConfigVo.getCode());
		sysConfig.setName(sysConfigVo.getName());
		sysConfig.setValue(sysConfigVo.getValue());
		sysConfig.setDescription(sysConfigVo.getDescription());
		sysConfig.setIsUsed(sysConfigVo.getIsUsed());
		return sysConfig;
	}

}
