package com.cdrundle.legaldoc.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.District;

/**
 * @author xiaokui.li  
 *
 * 2014年6月17日
 */
public interface IDistrictDao  extends  Dao<District>
{
	
	/**
	 * 查询根节点
	 * @return
	 */
	@Query("select n from District n where n.parent.id is null")
	public List<District> findRoot();
	
	/**
	 * 查询下级区域
	 * @param id
	 * @return
	 */
	@Query("select n from District n where n.parent.id = :id")
	public List<District> findChildren(@Param("id")Long id);
	
	/**
	 * 删除区域
	 * @param id
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("update District set isUsed = 'f' where id = :id")
	public void deleteById(@Param("id")Long id);
	
}
