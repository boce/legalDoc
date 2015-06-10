package com.cdrundle.legaldoc.dao;


import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.DeliberationRequest;

/**
 * @author XuBao 
 * 审议报请 
 * 2014年6月10日
 */
public interface IDeliberationRequestDao extends Dao<DeliberationRequest>
{

	/**
	 * 通过文件名模糊查找审议报请
	 * @param name 文件名
	 * @param draftingUnit  主起草单位
	 * @param unionDraUnit  联合起草单位
	 * @param pageable  
	 * @return  page
	 */
	@Query("select d  from  DeliberationRequest  d  where (d.draftingUnit.id in :id or d.unionDraUnit like :unionDraUnit) and d.name like :name")
	public Page<DeliberationRequest> findLikeName(@Param("id")Set<Long> id,@Param("unionDraUnit")String unionDrtUnit,@Param("name")String name,Pageable pageable);
	
	/**
	 * 查找本部门所有的审议报请
	 * @param draftingUnit  主起草单位
	 * @param unionDraUnit  联合起草单位
	 * @param pageable
	 * @return  page
	 */
	@Query("from DeliberationRequest d where d.draftingUnit.id in :id or d.unionDraUnit like %:unionDraUnit%  ")
	public Page<DeliberationRequest> findAll(@Param("id")Set<Long> id, @Param("unionDraUnit")String unionDrtUnit,Pageable pageable);
	
	/**
	 * 通过文件名查找
	 * @param name
	 * @return
	 */
	@Query("select d  from  DeliberationRequest d where d.name = :name")
	public  DeliberationRequest findDeliberationRequestByName(@Param("name") String  name);
	
	/**
	 * 通过规范性文件Id查找
	 * @param id
	 * @return
	 */
	@Query("select d  from DeliberationRequest d where  d.normativeFile.id = :id ")
	public  DeliberationRequest findByNorId(@Param("id")  Long id);
}
