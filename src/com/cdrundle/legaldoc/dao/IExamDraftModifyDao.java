package com.cdrundle.legaldoc.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.ExaminationDraftModify;
import com.cdrundle.legaldoc.entity.ExaminationDraftReview;
import com.cdrundle.legaldoc.entity.Organization;

/**
 * @author  gang.li
 *
 * 送审稿修改Dao
 */
public interface IExamDraftModifyDao extends Dao<ExaminationDraftModify> {
	
	/**
	 * 查询全部送审稿修改通过部门(主起草部门或者是联合部门)
	 * @param id
	 * @param unionDrtUnit
	 * @param pageable
	 */
	@Query("select n from ExaminationDraftModify n where n.draftingUnit.id = :id")
    public  Page<ExaminationDraftModify>  findAllByUnit(@Param("id")long id, Pageable pageable);
	
	/**
	 * 查询全部送审稿修改通过部门和name(主起草部门或者是联合部门)
	 * @param id
	 * @param unionDrtUnit
	 * @param pageable
	 */
	@Query("select n from ExaminationDraftModify n where n.name like %:name% and n.draftingUnit.id = :id")
    public  Page<ExaminationDraftModify>  findAllByNameAndUnit(@Param("name")String name, @Param("id")long id, Pageable pageable);
	
	/**
	 * 通过规范性文件ID查询存在的送审稿修改记录
	 * @param id
	 * @return
	 */
	@Query("select a from ExaminationDraftModify a where a.normativeFile.id = :id")
	public ExaminationDraftModify findByNorFileId(@Param("id")long id);
	
}  
