package com.cdrundle.legaldoc.dao;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.RecordRequest;

/**
 * @author  XuBao
 *备案报送
 * 2014年6月18日
 */
public interface IRecordRequestDao extends Dao<RecordRequest>{
	
	/**
	 * 通过文件名模糊查询备案报送
	 * @param name
	 * @param decisionMakingUnit  制定单位
	 * @param recordUnit  备案审查单位
	 * @param pageable
	 * @return  page
	 */
	@Query("select r  from  RecordRequest  r  where  r.name like :name and (r.decisionMakingUnit.id in :id or  r. draftingUnit.id in :id)")
	public Page<RecordRequest> findLikeName(@Param("name")String name,@Param("id")Set<Long> id, Pageable pageable);
	
	/**
	 * 查找本部门所有的备案报送
	 * @param decisionMakingUnit  制定单位
	 * @param recordUnit  备案审查单位
	 * @param pageable
	 * @return page
	 */
	@Query("from RecordRequest r where r.decisionMakingUnit.id in :id or  r. draftingUnit.id in :id")
	public Page<RecordRequest> findAll(@Param("id")Set<Long> id, Pageable pageable);
	
	/**
	 * 通过文件名查询备案报送单
	 * @param name
	 * @return
	 */
	@Query("from RecordRequest r  where r.name= :name ")
	public  RecordRequest findRecordRequestByName(@Param("name")String name);
	
	/**
	 * 通过规范性文件Id查找备案报送
	 * @param id
	 * @return
	 */
	@Query("select r  from RecordRequest r where  r.normativeFile.id = :id ")
	public  RecordRequest findByNorId(@Param("id")  Long id);
}
