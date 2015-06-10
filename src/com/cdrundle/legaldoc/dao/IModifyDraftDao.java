package com.cdrundle.legaldoc.dao;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.ModifyDraft;

/**
 * 意见征求--修改《征求意见稿》
 * @author xiaokui.li
 *
 */
public interface IModifyDraftDao extends Dao<ModifyDraft>
{
	/**
	 * 通过名称查询修改《征求意见稿》
	 * @param name
	 * @return
	 */
	@Query("select a from ModifyDraft a where a.draftingUnit.id in :ids and a.name like %:name% ")
	public Page<ModifyDraft> findByName(@Param("ids")Set<Long> ids,@Param("name")String name,Pageable pageable);
	
	@Query("select a from ModifyDraft a where a.draftingUnit.id in :ids ")
	public Page<ModifyDraft> findAll(@Param("ids")Set<Long> ids,Pageable pageable);
	
	@Query("select a from ModifyDraft a where a.normativeFile.id = :id")
	public ModifyDraft findByNorFileId(@Param("id")long id);
}
