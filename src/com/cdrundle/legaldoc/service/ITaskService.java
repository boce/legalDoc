package com.cdrundle.legaldoc.service;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.vo.TaskVo;

/**
 * 待办事项操作
 * @author xiaokui.li
 *
 */
public interface ITaskService {
	public Page<TaskVo> findMyTasks(int page, int size);
}
