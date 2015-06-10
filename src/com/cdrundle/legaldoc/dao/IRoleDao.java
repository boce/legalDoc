package com.cdrundle.legaldoc.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.Role;

/**
 * 角色
 * @author xiaokui.li
 *
 */
public interface IRoleDao extends Dao<Role>
{
	/**
	 * 查找角色
	 * @param name
	 * @return
	 */
	@Query("select r from Role r where r.name = :name")
	public Role findByName(@Param("name")String name);

	/**
	 * 模糊查找角色
	 * @param name
	 * @return
	 */
	@Query("select r from Role r where r.name like %:name%")
	public List<Role> findLikeName(@Param("name")String name);
	
	/**
	 * 假删除角色
	 * @param id
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("update Role set isUsed = 'f' where id = :id")
	public void deleteByIdVirtual(@Param("id")long id);
	
	/**
	 * 查找所有启用角色
	 * @param name
	 * @return
	 */
	@Query("select r from Role r where r.isUsed='t'")
	public List<Role> findAllEnable();
	
	/**
	 * 根据id查找角色
	 * @param ids 
	 * @return
	 */
	@Query("select r from Role r where r.id in :ids")
	public List<Role> findRoles(@Param("ids") Set<Long> ids);
}
