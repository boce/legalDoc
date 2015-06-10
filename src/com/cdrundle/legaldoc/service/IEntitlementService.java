package com.cdrundle.legaldoc.service;

import java.util.List;

import com.cdrundle.legaldoc.vo.EntitlmentVo;
import com.cdrundle.legaldoc.vo.MenuVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.PageSourceVo;
import com.cdrundle.legaldoc.vo.PageVo;

/**
 * 赋权
 * @author xiaokui.li
 *
 */
public interface IEntitlementService
{
	/**
	 * 保存或者更新赋权对象
	 * @param entitlment
	 * @return
	 */
	public boolean saveOrUpdate(EntitlmentVo entitlmentVo);
	
	/**
	 * 删除赋权对象
	 * @param entitlment
	 * @return
	 */
	public boolean delete(EntitlmentVo entitlmentVo);
	
	/**
	 * 删除赋权对象
	 * @param id
	 * @return
	 */
	public boolean delete(Long id);
	
	/**
	 * 查找角色对应的所有赋权
	 * @param roleId
	 * @return
	 */
	public List<EntitlmentVo> findByRoleId(Long roleId);
	
	/**
	 * 查找角色对应的赋权菜单
	 * @param roleId
	 * @return
	 */
	public List<MenuVo> findMenuAuth(long roleId);
	
	/**
	 * 查找对应角色的赋权页面
	 * @return
	 */
	public List<PageVo> findPageAuth(long roleId);
	
	/**
	 * 查找对应角色的赋权页面资源
	 * @return
	 */
	public List<PageSourceVo> findPageSourceAuth(long roleId);
	
	/**
	 * 查找对应角色的赋权组织机构
	 * @return
	 */
	public List<OrgShortVo> findOrgAuth(long roleId);
}
