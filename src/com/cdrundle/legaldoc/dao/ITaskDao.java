package com.cdrundle.legaldoc.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.base.Dao;
import com.cdrundle.legaldoc.entity.Task;
import com.cdrundle.legaldoc.enums.TaskType;

/**
 * 待办事项
 * @author xiaokui.li
 *
 */
public interface ITaskDao extends Dao<Task>{

	/**
	 * 根据规范性文件id和任务类型查询待办事项
	 * @param norId
	 * @param taskType
	 * @return
	 */
	@Query("select t from Task t where t.normativeFile.id = :norId and t.taskType = :taskType")
	public Task findByNorIdAndTaskType(@Param("norId") Long norId, @Param("taskType") TaskType taskType);
	
	/**
	 * 查询当前组织机构是制定单位或者
	 * @param userName
	 * @return
	 */
	@Query("select t from Task t where t.taskStatus='RUNNING' and t.normativeFile.decUnit.id = :decUnit and (t.taskType = 'FILEADJUST' or t.taskType = 'RECORDREMIND')")
	public Page<Task> findOwnTask(@Param("decUnit") Long decUnit, Pageable pageable);
}
