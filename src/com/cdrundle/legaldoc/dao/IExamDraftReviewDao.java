package com.cdrundle.legaldoc.dao;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.ExaminationDraftReview;

/**
 * @author  gang.li
 *
 * 送审稿审查Dao
 */
public interface IExamDraftReviewDao extends Dao<ExaminationDraftReview> {
	
	/**
	 * 查询全部送审稿审查通过部门(主起草部门或者是联合部门)
	 * @param id
	 * @param unionDrtUnit
	 * @param pageable
	 */
	@Query("select n from ExaminationDraftReview n where n.draftingUnit.id in (:drfOrgIds) or n.reviewUnit.id in(:drfOrgIds) or  n.unionDraftingUnit like %:unionDrtUnit%")
    public  Page<ExaminationDraftReview>  findAllByUnit(@Param("drfOrgIds")Set<Long> drfOrgIds, @Param("unionDrtUnit")String unionDrtUnit, Pageable pageable);
	
	/**
	 * 查询全部送审稿审查通过部门和name(主起草部门或者是联合部门)
	 * @param id
	 * @param unionDrtUnit
	 * @param pageable
	 */
	@Query("select n from ExaminationDraftReview n where n.name like %:name% and (n.draftingUnit.id = :id  or  n.unionDraftingUnit like %:unionDrtUnit%)")
    public  Page<ExaminationDraftReview>  findAllByNameAndUnit(@Param("name")String name, @Param("id")long id, @Param("unionDrtUnit")String unionDrtUnit, Pageable pageable);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@Query("select a from ExaminationDraftReview a where a.normativeFile.id = :id")
	public ExaminationDraftReview findByNorFileId(@Param("id")long id);

}  
