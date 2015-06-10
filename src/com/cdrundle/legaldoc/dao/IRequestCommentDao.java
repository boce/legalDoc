package com.cdrundle.legaldoc.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.RequestComment;

/**
 * 意见征求--征求意见
 * @author xiaokui.li
 *
 */
public interface IRequestCommentDao extends Dao<RequestComment>
{
	/**
	 * 通过名称查询征求意见单
	 * @param name
	 * @return
	 */
	@Query("select r from RequestComment r where r.name = :name")
	public RequestComment findByName(@Param("name")String name);
	
	/**
	 * 通过名称查找征求意见单
	 * @param name
	 * @return
	 */
	@Query("select d from RequestComment d where d.draftingUnit.id in :orgIds and d.name like :name")
	public Page<RequestComment> findByName(@Param("orgIds")Set<Long> orgIds, @Param("name")String name, Pageable pageable);
	
	@Query("select a from RequestComment a where a.normativeFile.id = :id and a.requestFromUnit.id=:reqFromUnitId")
	public RequestComment findByNorFileId(@Param("id")Long id,@Param("reqFromUnitId")Long reqFromUnitId);
	
	@Query("select a from RequestComment a where a.normativeFile.id = :id")
	public List<RequestComment> findByNorFile(@Param("id")Long id);
	
	@Query("select a from RequestComment a where a.draftingUnit.id in :orgIds ")
	public Page<RequestComment> findAll(@Param("orgIds")Set<Long> orgIds,Pageable pageable);
}
