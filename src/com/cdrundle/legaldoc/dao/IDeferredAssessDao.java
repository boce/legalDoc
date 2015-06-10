package com.cdrundle.legaldoc.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.DeferredAssessment;
import com.cdrundle.legaldoc.entity.ExaminationDraftReview;

/**
 * @author  gang.li
 *
 * 满期评估Dao
 */
public interface IDeferredAssessDao extends Dao<DeferredAssessment> {
	
	/**
	 * 通过名字查找
	 * @param name
	 * @return DeferredAssessment
	 */
	@Query("select d from DeferredAssessment d where d.name like %?1")
    public List<DeferredAssessment> findByName(String name);
	
	/**
	 * 通过规范性文件来查询满期评估
	 * @param id
	 * @return
	 */
	@Query("select a from DeferredAssessment a where a.normativeFile.id = :id")
	public DeferredAssessment findByNorFileId(@Param("id")long id);
}  
