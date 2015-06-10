package com.cdrundle.legaldoc.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.Role;
import com.cdrundle.legaldoc.entity.User;

public interface IUserDao extends Dao<User>
{
	/**
	 * 查找用户
	 * @param userName
	 * @return
	 */
	@Query("select u from User u where u.userName = :userName")
	public User findByUserName(@Param("userName") String userName);
	
	/**
	 * 查找用户
	 * @param userName
	 * @return
	 */
	@Query(value = "select u from User u where (u.name like %:name%) and (u.organization.id = :orgId)")
	public Page<User> findUserRef(@Param("name")String name, @Param("orgId")Long orgId, Pageable pageable);
	
	/**
	 * @param orgId
	 * @return
	 */
	@Query("select u from User u where u.organization.id = :orgId")
	public List<User> findByOrgId(@Param("orgId") long orgId);
	
	/**
	 * 假删除用户
	 * @param id
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("update User set isUsed = 'f' where id = :id")
	public void deleteById(@Param("id")long id);
	
	/**
	 * 根据用户id查询对应的角色
	 * @param id
	 * @return
	 */
	@Query("select u.roles from User u where u.id = :id")
	public List<Role> findRoleById(@Param("id") Long id);
	
	/**
	 * 根据多个id查找用户
	 * @param ids
	 * @return
	 */
	@Query("select u from User u where u.id in :ids")
	public List<User> findUserByIds(@Param("ids") Set<Long> ids);
	
	/**
	 * 根据组织机构id查找负责人
	 * @param orgId
	 * @return
	 */
	@Query("select u from User u where u.organization.id = :orgId and u.isIncharge='t'")
	public List<User> findChargeByOrg(@Param("orgId") Long orgId);
}
