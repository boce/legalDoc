package com.cdrundle.legaldoc.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.FileCleanUp;
import com.cdrundle.legaldoc.entity.FileCleanUpLine;

public interface IFileCleanUpDao extends Dao<FileCleanUp> {
	/**
	 * 查询本部门全部文件清理
	 * @param id
	 * @param pageable
	 */
	@Query("select n from FileCleanUp n where n.cleanupUnit.id = :id")
    public  Page<FileCleanUp>  findAll(@Param("id")long id, Pageable pageable);
	
	/**
	 * 查询子清理文件
	 * @param id
	 */
	@Query("select n from FileCleanUpLine n where n.fileCleanup.id = :id")
    public  List<FileCleanUpLine>  findFCULines(@Param("id")long id);
	
}
