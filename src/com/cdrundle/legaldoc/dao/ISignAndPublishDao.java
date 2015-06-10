package com.cdrundle.legaldoc.dao;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.SignAndPublish;

/**
 * @author  XuBao
 *签署发布
 * 2014年6月18日
 */
public interface ISignAndPublishDao  extends  Dao<SignAndPublish>{
		
	/**
	 * 通过文件名模糊查找签署发布
	 * @param name 文件名
	 * @param decisionMakingUnit  制定单位
	 * @param pageable  
	 * @return  page
	 */
	@Query("select s  from  SignAndPublish  s  where  s.name like :name and (s.decisionMakingUnit.id in (:id) or s.draftingUnit.id in (:id))")
	public Page<SignAndPublish> findLikeName(@Param("name")String name,@Param("id")Set<Long> id,Pageable pageable);
	
	/**
	 * 查找本部门所有的签署发布单
	 * @param decisionMakingUnit  制定单位
	 * @param pageable
	 * @return  page
	 */
	@Query("from SignAndPublish s where s.draftingUnit.id in :id or s.draftingUnit.id in :id ")
	public Page<SignAndPublish> findAll(@Param("id")Set<Long> id, Pageable pageable);
	
	/**
	 * 通过文件名查找签署与发布单
	 * @param name
	 * @return
	 */
	@Query("from SignAndPublish s  where s.name= :name ")
	public  SignAndPublish findSignAndPublishByName(@Param("name")String name);
	
	/**
	 * 通过规范性文件Id查找签署发布
	 * @param id
	 * @return
	 */
	@Query("select s from SignAndPublish s where  s.normativeFile.id = :id ")
	public  SignAndPublish findByNorId(@Param("id")  Long id);
}
