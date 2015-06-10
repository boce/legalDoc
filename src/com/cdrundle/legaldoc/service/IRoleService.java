package com.cdrundle.legaldoc.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.MenuVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.RoleVo;

public interface IRoleService
{
	/**
	 * 保存或者更新角色
	 * @param role
	 * @return
	 */
	public RoleVo saveOrUpdate(RoleVo roleVo);
	
	/**
	 * 根据id删除角色
	 * @param role
	 * @return
	 */
	public boolean deleteById(Long id);
	
	/**
	 * 根据id假删除角色
	 * @param role
	 * @return
	 * @throws ServiceException 
	 */
	public boolean deleteByIdVirtual(Long id) throws ServiceException;
	
	/**
	 * 根据对象删除角色
	 * @param role
	 * @return
	 */
	public void delete(RoleVo roleVo);
	
	/**
	 * 根据名称查询角色
	 * @param name
	 * @return
	 */
	public RoleVo findByName(String name);
	
	/**
	 * 根据名称模糊查询角色
	 * @param name
	 * @return
	 */
	public List<RoleVo> findLikeName(String name);
	
	/**
	 * 查询角色
	 * @param id
	 * @return
	 */
	public RoleVo findById(long id);
	
	/**
	 * 分页查找角色
	 * @param description
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<RoleVo> findRoleRef(String description, int page, int size);
	
	/**
	 * 查找所有启用的角色
	 * @return
	 */
	public List<RoleVo> findAllEnable();
	
	/**
	 * 根据角色id查询该角色对应的授权资源
	 * @param roleId
	 * @return
	 */
	public List<MenuVo> getAllAuthMenu(Long roleId);
	
	/**
	 * 操作授权
	 * @param auths
	 * @param roleId
	 */
	public void optAuthorize(Long[][] auths, Long roleId);
	
	/**
	 * 根据角色id查询所有授权的组织机构
	 * @param roleId
	 * @return
	 */
	public List<OrgShortVo> findAllAuthOrg(Long roleId);
	
	/**
	 * 数据授权
	 * @param orgIds
	 * @param roleId
	 * @return
	 */
	public boolean dataAuthorize(Long[] orgIds, Long roleId);
}
