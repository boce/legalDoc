package com.cdrundle.legaldoc.dao;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.RecordReview;

/**
 * @author  XuBao
 *备案审查
 * 2014年6月18日
 */
public interface IRecordReviewDao extends  Dao<RecordReview>{

	/**
	 * 通过文件名模糊查询备案审查
	 * @param name
	 * @param decisionMakingUnit  制定单位
	 * @param recordRevUnit  备案审查单位
	 * @param pageable
	 * @return  page
	 */
	@Query("select r  from  RecordReview  r  where  r.name like :name  and (r.decisionMakingUnit.id in :id or  r. recordRevUnit.id in :id)")
	public Page<RecordReview> findLikeName(@Param("name")String name,@Param("id")Set<Long> id, Pageable pageable);
	
	/**
	 * 查找本部门所有的备案审查
	 * @param decisionMakingUnit  制定单位
	 * @param recordRevUnit  备案审查单位
	 * @param pageable
	 * @return page
	 */
	@Query("from RecordReview r where r.decisionMakingUnit.id in :id or  r. recordRevUnit.id in :id")
	public Page<RecordReview> findAll(@Param("id")Set<Long> id, Pageable pageable);
	
	/**
	 * 通过文件名查找备案审查
	 * @param name
	 * @return
	 */
	@Query("from RecordReview r  where r.name= :name ")
	public  RecordReview findRecordReviewByName(@Param("name")String name);
	
	/**
	 * 通过规范性文件Id查找备案审查
	 * @param id
	 * @return
	 */
	@Query("select r  from RecordReview r where  r.normativeFile.id = :id ")
	public  RecordReview findByNorId(@Param("id")  Long id);
	
	/**
	 * 通过Id查找备案审查
	 * @param id
	 * @return
	 */
	@Query("select r from RecordReview r where r.id = :id ")
	public  RecordReview  findById(@Param("id") Long id);
}
