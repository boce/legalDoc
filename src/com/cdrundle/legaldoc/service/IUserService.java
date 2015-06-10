package com.cdrundle.legaldoc.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.vo.RoleVo;
import com.cdrundle.legaldoc.vo.UserShortVo;
import com.cdrundle.legaldoc.vo.UserVo;

public interface IUserService
{
	/**
	 * 保存或者更新用户
	 * @param user
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public UserVo saveOrUpdate(UserVo userVo) throws NoSuchAlgorithmException;
	
	/**
	 * 根据对象删除用户
	 * @param user
	 * @return
	 */
	public void delete(UserVo userVo);
	
	/**
	 * 根据id删除用户
	 * @param id
	 * @return
	 */
	public void delete(long id);
	
	/**
	 * 根据id假删除用户
	 * @param id
	 * @return
	 */
	public boolean deleteById(long id);
	
	/**
	 * 根据id查找用户
	 * @param id
	 * @return
	 */
	public UserVo findById(long id);
	
	/**
	 * 根据id查找用户
	 * @param id
	 * @return
	 */
	public UserShortVo findByIdShort(long id);
	
	/**
	 * 根据多个id查找用户
	 * @param ids
	 * @return
	 */
	public List<UserShortVo> findByIdsShort(Set<Long> ids);
	
	/**
	 * 根据组织机构id查找用户
	 * @param userName
	 * @return
	 */
	public List<UserVo> findByOrgId(Long id);
	
	/**
	 * 根据组织机构id查找对应上级机构用户
	 * @param userName
	 * @return
	 */
	public List<UserVo> findSuperByOrgId(Long id);
	
	/**
	 * 根据登录名查找用户
	 * @param userName
	 * @return
	 */
	public UserVo findByUserName(String userName);
	
	/**
	 * 根据姓名或者组织机构id模糊查询用户
	 * @param name
	 * @param orgId
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<UserVo> findUserRef(String name, Long orgId, int page, int size);
	
	/**
	 * 分页查询所有用户
	 * @param name
	 * @param pageable
	 * @return
	 */
	public Page<UserVo> findByAll(int page, int size);
	
	/**
	 * 赋予用户权限
	 * @param roleIds 角色id
	 * @param userId 用户id
	 * @return
	 */
	public boolean authorize(Set<Long> roleIds, Long userId);

	/**
	 * 根据用户id查询赋予的角色
	 * @param userId
	 * @return
	 */
	public List<RoleVo> findRoleById(Long userId);
	
	/**
	 * 判断密码是否是当前登录用户的密码
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public boolean judgePassword(String password) throws NoSuchAlgorithmException;
	
	/**
	 * 修改当前登录用户密码
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public boolean updatePassword(String password) throws NoSuchAlgorithmException;
	
	/**
	 * 根据组织机构id查询负责人
	 * @param orgId
	 * @return
	 */
	public List<UserVo> findChargeByOrg(Long orgId);
}
