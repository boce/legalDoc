package com.cdrundle.legaldoc.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.ITaskDao;
import com.cdrundle.legaldoc.entity.Task;
import com.cdrundle.legaldoc.service.ITaskService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.vo.TaskVo;
import com.cdrundle.security.WebPlatformUser;

@Service
public class TaskService implements ITaskService{

	@Autowired
	ITaskDao taskDao;
	
	@Override
	@Transactional(readOnly = true)
	public Page<TaskVo> findMyTasks(int page, int size) {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		long orgId = Long.parseLong(userDetail.getOrgId());
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "createDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<Task> pages = taskDao.findOwnTask(orgId, pageable);
		List<TaskVo> voList = TaskVo.createVoList(pages.getContent());
		Page<TaskVo> pageVo = new PageImpl<TaskVo>(voList, pageable, pages.getTotalElements());
		return pageVo;
	}

}
