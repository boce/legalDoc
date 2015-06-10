package com.cdrundle.legaldoc.dao;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.Draft;

/**
 * 起草
 * 
 * @author xiaokui.li
 * 
 */
public interface IDraftDao extends Dao<Draft> {
	/**
	 * 通过名称查询起草单
	 * 
	 * @param name
	 * @return
	 */
	@Query("select d from Draft d where d.name = ?1")
	public Draft findByName(String name);

	/**
	 * 通过名称查找起草单
	 * 
	 * @param name
	 * @return
	 */
	@Query("select d from Draft d where d.name like :name and (d.draftingUnit.id in (:orgIds) or d.unionDraftingUnit like :orgId)")
	public Page<Draft> findByName(@Param("orgIds")Set<Long> orgIds, @Param("orgId") String orgId, @Param("name")String name, Pageable pageable);

	/**
	 * 通过组织机构查找起草单
	 * 
	 * @param name
	 * @return
	 */
	@Query("select d from Draft d where d.draftingUnit.id in (:orgIds) or d.unionDraftingUnit like :orgId")
	public Page<Draft> findAll(@Param("orgIds") Set<Long> orgIds, @Param("orgId") String orgId, Pageable pageable);
	
	/**
	 * 通过规范性文件id查询起草单
	 * 
	 * @param name
	 * @return
	 */
	@Query("select d from Draft d where d.normativeFile.id = :norId")
	public Draft findByNorFileId(@Param("norId")Long norId);
}
