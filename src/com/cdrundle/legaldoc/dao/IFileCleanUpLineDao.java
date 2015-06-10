package com.cdrundle.legaldoc.dao;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.FileCleanUpLine;

/**
 * 文件清理子文件Dao
 * @author gang.li
 *
 */
public interface IFileCleanUpLineDao extends Dao<FileCleanUpLine> {
	
	/**
	 * 通过清理文件ID查询子文件集合
	 * @param fileCleanUpId
	 * @return  List<FileCleanUpLine>
	 */
	@Query("select f from FileCleanUpLine f where f.fileCleanup.id = ?1")
	public List<FileCleanUpLine> findByFileCleanUpId(Long fileCleanUpId);
	
	/**
	 * 通过名称查询文件清理子文件
	 * @param fileCleanUpName
	 * @return FileCleanUpLine
	 */
	@Query("select f from FileCleanUpLine f where f.name = :fileCleanUpName")
	public FileCleanUpLine findByFileCleanUpName(@Param("fileCleanUpName")String fileCleanUpName);
	
}
