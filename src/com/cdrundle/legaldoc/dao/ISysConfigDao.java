package com.cdrundle.legaldoc.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.SysConfig;

public interface ISysConfigDao  extends Dao<SysConfig>
{
	/**
	 * 根据编码查询系统参数
	 * @param code
	 * @return
	 */
	@Query("select s from SysConfig s where s.code = ?1")
	public SysConfig findByCode(String code);
	
	/**
	 * 假删除系统参数
	 * @param id
	 * @return
	 */
	@Transactional
	@Modifying
	@Query("update SysConfig set isUsed = 'f' where id = :id")
	public void deleteById(@Param("id")long id);
}
