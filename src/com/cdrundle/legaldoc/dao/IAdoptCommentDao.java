package com.cdrundle.legaldoc.dao;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.AdoptComment;

/**
 * 意见征求--反馈意见处理情况
 * @author xiaokui.li
 *
 */
public interface IAdoptCommentDao extends Dao<AdoptComment>
{
	/**
	 * 通过名称查询反馈意见处理情况
	 * @param name
	 * @return
	 */
	@Query("select a from AdoptComment a where a.draftingUnit.id in :ids and a.name like %:name% ")
	public Page<AdoptComment> findByName(@Param("ids")Set<Long> ids,@Param("name")String name,Pageable pageable);
	
	@Query("select a from AdoptComment a where a.draftingUnit.id in :ids ")
	public Page<AdoptComment> findAll(@Param("ids")Set<Long> ids,Pageable pageable);
	
	@Query("select a from AdoptComment a where a.normativeFile.id = :id")
	public AdoptComment findByNorFileId(@Param("id")long id);
}
