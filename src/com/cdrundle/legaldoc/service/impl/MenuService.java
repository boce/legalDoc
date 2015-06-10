package com.cdrundle.legaldoc.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IEntitlementDao;
import com.cdrundle.legaldoc.dao.IMenuDao;
import com.cdrundle.legaldoc.dao.IPageDao;
import com.cdrundle.legaldoc.dao.IPageSourceDao;
import com.cdrundle.legaldoc.dao.IRoleDao;
import com.cdrundle.legaldoc.entity.Menu;
import com.cdrundle.legaldoc.entity.Page;
import com.cdrundle.legaldoc.entity.PageSource;
import com.cdrundle.legaldoc.entity.Role;
import com.cdrundle.legaldoc.service.IMenuService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.vo.MenuShortVo;
import com.cdrundle.legaldoc.vo.MenuVo;
import com.cdrundle.legaldoc.vo.PageShortVo;
import com.cdrundle.legaldoc.vo.PageSourceShortVo;
import com.cdrundle.legaldoc.vo.PageSourceVo;
import com.cdrundle.legaldoc.vo.PageVo;
import com.cdrundle.security.WebPlatformUser;

@Service
public class MenuService implements IMenuService
{
	@Autowired
	IMenuDao menuDao;
	@Autowired
	IPageDao pageDao;
	@Autowired
	IPageSourceDao pageSourceDao;
	@Autowired
	IEntitlementDao entitlementDao;
	@Autowired
	IRoleDao roleDao;
	
	@Override
	@Transactional
	public boolean saveOrUpdate(MenuVo menuVo)
	{
		Menu save = menuDao.save(convertToMenu(menuVo));
		return save == null ? false : true;
	}

	@Override
	@Transactional
	public boolean delete(MenuVo menuVo)
	{
		menuDao.delete(convertToMenu(menuVo));
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public MenuVo findById(long id)
	{
		return MenuVo.createVo(menuDao.findOne(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuVo> findAll()
	{
		return MenuVo.createVoList(menuDao.findAll());
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuVo> findByLevel(int level)
	{
		return MenuVo.createVoList(menuDao.findByLevel(level));
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuVo> findChildMenus(long id)
	{
		return MenuVo.createVoList(menuDao.findChidMenus(id));
	}

	@Override
	@Transactional(readOnly = true)
	public MenuVo findParentMenu(long id)
	{
		Menu menu = menuDao.findOne(id);
		return MenuVo.createVo(menu.getParentMenu());
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuShortVo> displayMenu()
	{
		List<MenuShortVo> menuVos = new ArrayList<>();
		List<PageShortVo> pageVos = new ArrayList<>();
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		List<GrantedAuthority> authorities = userDetail.getAuthorities();
		Set<Long> roleIds = new HashSet<>();
		for (Iterator<GrantedAuthority> iterator = authorities.iterator(); iterator.hasNext();)
		{
			GrantedAuthority grantedAuthority = iterator.next();
			String authority = grantedAuthority.getAuthority();
			Role role = roleDao.findByName(authority);
			roleIds.add(role.getId());
		}
		List<Menu> menus = entitlementDao.findMenuAuthEnable(roleIds);
		List<Page> pages = entitlementDao.findPageAuthEnable(roleIds);
		List<PageSource> pageSources = entitlementDao.findPageSourceAuthEnable(roleIds);
		for (Iterator<Page> iterator = pages.iterator(); iterator.hasNext();)
		{
			Page page = iterator.next();
			List<PageSourceShortVo> pageSourceVoList = new ArrayList<>();
			for (Iterator<PageSource> iterator2 = pageSources.iterator(); iterator2.hasNext();)
			{
				PageSource pageSource = iterator2.next();
				if(pageSource.getPage() != null && page.getId().equals(pageSource.getPage().getId()))
				{
					pageSourceVoList.add(PageSourceShortVo.createVo(pageSource));
				}
			}
			Collections.sort(pageSourceVoList);
			PageShortVo pageShortVo = PageShortVo.createVo(page);
			pageShortVo.setPageSources(pageSourceVoList);
			pageVos.add(pageShortVo);
		}
		
		for (Iterator<Menu> iterator = menus.iterator(); iterator.hasNext();) {
			Menu menu = iterator.next();
			List<PageShortVo> pageVoList = new ArrayList<>();
			for (Iterator<Page> iterator2 = pages.iterator(); iterator2.hasNext();) {
				Page page = iterator2.next();
				if(page.getMenu() != null && menu.getId().equals(page.getMenu().getId()))
				{
					for (Iterator<PageShortVo> iterator3 = pageVos.iterator(); iterator3.hasNext();) {
						PageShortVo pageShortVo = iterator3.next();
						if(page.getId().equals(pageShortVo.getId())){
							pageVoList.add(pageShortVo);
							break;
						}
					}
				}
			}
			Collections.sort(pageVoList);
			MenuShortVo menuShortVo = MenuShortVo.createVo(menu);
			menuShortVo.setPages(pageVoList);
			menuVos.add(menuShortVo);
		}
		Collections.sort(menuVos);
		return menuVos;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MenuVo> displayAllMenu()
	{
		List<Menu> menus = menuDao.findAll();
		List<MenuVo> menuVoList = MenuVo.createVoList(menus);
		List<Page> pages = pageDao.findAll();
		List<PageVo> pageVoList = PageVo.createVoList(pages);
		List<PageSource> pageSources = pageSourceDao.findAll();
		List<PageSourceVo> pageSourceVoList = PageSourceVo.createVoList(pageSources);
		for (Iterator<PageVo> iterator = pageVoList.iterator(); iterator.hasNext();)
		{
			PageVo pageVo = iterator.next();
			ArrayList<PageSourceVo> pageSourceVos = new ArrayList<PageSourceVo>();
			for (Iterator<PageSourceVo> iterator2 = pageSourceVoList.iterator(); iterator2.hasNext();)
			{
				PageSourceVo pageSourceVo = iterator2.next();
				if(pageSourceVo.getPage() != null && pageVo.getId().equals(pageSourceVo.getPage().getId()))
				{
					pageSourceVos.add(pageSourceVo);
				}
			}
			Collections.sort(pageSourceVos);
			pageVo.setPageSources(pageSourceVos);
			
		}
		for (Iterator<MenuVo> iterator = menuVoList.iterator(); iterator.hasNext();)
		{
			MenuVo menuVo = iterator.next();
			ArrayList<PageVo> pageVos = new ArrayList<PageVo>();
			for (Iterator<PageVo> iterator2 = pageVoList.iterator(); iterator2.hasNext();)
			{
				PageVo pageVo = iterator2.next();
				if(pageVo.getMenu() != null && menuVo.getId().equals(pageVo.getMenu().getId()))
				{
					pageVos.add(pageVo);
				}
			}
			Collections.sort(pageVos);
			menuVo.setPages(pageVos);
		}
		Collections.sort(menuVoList);
		return menuVoList;
	}

	public Menu convertToMenu(MenuVo menuVo)
	{
		Menu menu = new Menu();
		menu.setId(menuVo.getId());
		menu.setName(menuVo.getName());
		menu.setDescription(menuVo.getDescription());
		menu.setParentMenu(menuDao.findOne(menuVo.getParent()));
		menu.setIsLeaf(menuVo.getIsLeaf());
		menu.setLevel(menuVo.getLevel());
		menu.setIsUsed(menuVo.getIsUsed());
		return menu;
	}
}
