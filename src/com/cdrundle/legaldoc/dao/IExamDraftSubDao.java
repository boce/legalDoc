package com.cdrundle.legaldoc.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.ExaminationDraftSubmit;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.Stage;

/**
 * @author  gang.li
 *
 * 送审稿报送Dao
 */
public interface IExamDraftSubDao extends Dao<ExaminationDraftSubmit> {
	
	/**
	 * 通过名字查找
	 * @param name
	 * @return ExaminationDraftSubmit
	 */
	@Query("select d from ExaminationDraftSubmit d where d.name like %?1 and d.draftingUnit = ?2")
     public List<ExaminationDraftSubmit> findByName(String name, Organization draftingUnit);
	
	/**
	 * 查询全部送审稿通过部门(主起草部门或者是联合部门)
	 * @param id
	 * @param unionDrtUnit
	 * @param pageable
	 */
	@Query("select n from ExaminationDraftSubmit n where n.draftingUnit.id in (:drfOrgIds)  or  n.unionDraftingUnit like %:unionDrtUnit%")
    public  Page<ExaminationDraftSubmit>  findAllByUnit(@Param("drfOrgIds")Set<Long> drfOrgIds, @Param("unionDrtUnit")String unionDrtUnit, Pageable pageable);
	
	/**
	 * 根据norFileId查询送审稿
	 * @param norId
	 * @return ExaminationDraftSubmit
	 */
	@Query("select e from ExaminationDraftSubmit e where e.normativeFile.id = :norId")
	public ExaminationDraftSubmit findByNorId(@Param("norId") long norId);
}  
