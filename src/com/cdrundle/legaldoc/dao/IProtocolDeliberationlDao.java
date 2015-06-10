package com.cdrundle.legaldoc.dao;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.ProtocolDeliberation;

/**
 * @author  XuBao
 *草案审议
 * 2014年6月10日
 */
public interface IProtocolDeliberationlDao extends  Dao<ProtocolDeliberation> {
	
	/**
	 * 通过文件名模糊查找草案审议
	 * @param name 文件名
	 * @param draftingUnit  主起草单位
	 * @param pageable  
	 * @return  page
	 */
	@Query("select p  from  ProtocolDeliberation  p  where  p.name like :name and p.draftingUnit.id in :id ")
	public Page<ProtocolDeliberation> findLikeName(@Param("name")String name,@Param("id")Set<Long> id,Pageable pageable);
	
	/**
	 * 查找本部门所有的草案审议
	 * @param draftingUnit  主起草单位
	 * @param pageable
	 * @return  page
	 */
	@Query("from ProtocolDeliberation p where p.draftingUnit.id in :id ")
	public Page<ProtocolDeliberation> findAll(@Param("id")Set<Long> id, Pageable pageable);
	
	/**
	 * 通过文件名查找草案审议单
	 * @param name
	 * @return
	 */
	@Query("select p  from  ProtocolDeliberation p where p.name = :name")
	public   ProtocolDeliberation  findProtocolDeliberationByName(@Param("name")String  name);
	
	/**
	 * 通过规范性文件Id查找草案审议
	 * @param id
	 * @return
	 */
	@Query("select p  from ProtocolDeliberation p where  p.normativeFile.id = :id ")
	public  ProtocolDeliberation findByNorId(@Param("id")  Long id);
}
