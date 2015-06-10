package com.cdrundle.legaldoc.dao;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.ProtocolModify;

/**
 * @author  XuBao
 *修改草案
 * 2014年6月10日
 */
public interface IProtocolModifyDao extends  Dao<ProtocolModify> {
		
	/**
	 * 通过文件名模糊查找草案修改
	 * @param name 文件名
	 * @param draftingUnit  主起草单位
	 * @param pageable  
	 * @return  page
	 */
	@Query("select p  from  ProtocolModify  p  where  p.name like :name and p.draftingUnit.id in :id ")
	public Page<ProtocolModify> findLikeName(@Param("name")String name,@Param("id")Set<Long>  id ,Pageable pageable);
	
	/**
	 * 查找本部门所有的草案修改
	 * @param draftingUnit  主起草单位
	 * @param pageable
	 * @return  page
	 */
	@Query("from ProtocolModify p where p.draftingUnit.id in :id ")
	public Page<ProtocolModify> findAll(@Param("id")Set<Long> id, Pageable pageable);
	
	/**
	 * 通过文件名查找草案修改单
	 * @param name
	 * @return
	 */
	@Query("from ProtocolModify p where p.name = :name ")
	public  ProtocolModify findProtocolModifyByName(@Param("name")String name);
	
	/**
	 * 通过规范性文件Id查找草案修改
	 * @param id
	 * @return
	 */
	@Query("select p  from ProtocolModify p where  p.normativeFile.id = :id ")
	public  ProtocolModify findByNorId(@Param("id")  Long id);
}
