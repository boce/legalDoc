package com.cdrundle.legaldoc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdrundle.legaldoc.dao.IEntitlementDao;
import com.cdrundle.legaldoc.dao.IMenuDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IPageDao;
import com.cdrundle.legaldoc.dao.IPageSourceDao;
import com.cdrundle.legaldoc.dao.IRoleDao;
import com.cdrundle.legaldoc.entity.Entitlment;
import com.cdrundle.legaldoc.service.IEntitlementService;
import com.cdrundle.legaldoc.vo.EntitlmentVo;
import com.cdrundle.legaldoc.vo.MenuVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.PageSourceVo;
import com.cdrundle.legaldoc.vo.PageVo;

@Service
public class EntitlementService implements IEntitlementService
{
	@Autowired
	IEntitlementDao entitlementDao;
	@Autowired
	IRoleDao roleDao;
	@Autowired
	IMenuDao menuDao;
	@Autowired
	IOrganizationDao organizationDao;
	@Autowired
	IPageDao pageDao;
	@Autowired
	IPageSourceDao pageSourceDao;
	
	@Override
	public List<EntitlmentVo> findByRoleId(Long roleId)
	{
		return EntitlmentVo.createVoList(entitlementDao.findAll(roleId));
	}

	@Override
	public List<MenuVo> findMenuAuth(long roleId)
	{
		return MenuVo.createVoList(entitlementDao.findMenuAuth(roleId));
	}

	@Override
	public List<PageVo> findPageAuth(long roleId)
	{
		return PageVo.createVoList(entitlementDao.findPageAuth(roleId));
	}

	@Override
	public List<PageSourceVo> findPageSourceAuth(long roleId)
	{
		return PageSourceVo.createVoList(entitlementDao.findPageSourceAuth(roleId));
	}

	@Override
	public List<OrgShortVo> findOrgAuth(long roleId)
	{
		return OrgShortVo.createVoListNoChild(entitlementDao.findOrgAuth(roleId));
	}

	@Override
	public boolean saveOrUpdate(EntitlmentVo entitlment)
	{
		Entitlment savedEntitlment = entitlementDao.save(convertToEntitlment(entitlment));
		return savedEntitlment == null ? false : true;
	}

	@Override
	public boolean delete(EntitlmentVo entitlment)
	{
		entitlementDao.delete(convertToEntitlment(entitlment));
		return true;
	}

	@Override
	public boolean delete(Long id)
	{
		entitlementDao.delete(id);
		return true;
	}

	public Entitlment convertToEntitlment(EntitlmentVo entitlmentVo)
	{
		Entitlment entitlment = new Entitlment();
		entitlment.setId(entitlmentVo.getId());
		if(entitlmentVo.getMenu() != null)
		{
			entitlment.setMenu(menuDao.findOne(entitlmentVo.getMenu().getId()));
		}
		if(entitlmentVo.getOrganization() != null)
		{
			entitlment.setOrganization(organizationDao.findOne(entitlmentVo.getOrganization().getId()));
		}
		if(entitlmentVo.getRole() != null)
		{
			entitlment.setRole(roleDao.findOne(entitlmentVo.getRole().getId()));
		}
		if(entitlmentVo.getPage() != null)
		{
			entitlment.setPage(pageDao.findOne(entitlmentVo.getPage().getId()));
		}
		if(entitlmentVo.getPageSource() != null)
		{
			entitlment.setPageSource(pageSourceDao.findOne(entitlmentVo.getPageSource().getId()));
		}
		return entitlment;
	}
}
