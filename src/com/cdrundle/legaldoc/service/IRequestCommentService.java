package com.cdrundle.legaldoc.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.RequestCommentVo;

/**
 * 意见征求--征求意见操作
 * @author xiaokui.li
 *
 */
public interface IRequestCommentService
{
	/**
	 * 保存或者更新征求意见单
	 * @param requestComment
	 * @return
	 * @throws ServiceException 
	 */
	public RequestCommentVo saveOrUpdate(RequestCommentVo requestCommentVo, String rootPath) throws ServiceException;
	
	/**
	 * 根据id删除征求意见单
	 * @param id
	 * @return
	 * @throws ServiceException 
	 */
	public boolean delete(Long id,String rootPath) throws ServiceException;
	
	/**
	 * 根据id查询征求意见单
	 * @param id
	 * @return
	 */
	public RequestCommentVo findById(long id);
	
	/**
	 * 根据名称查询征求意见单
	 * @param name
	 * @return
	 */
	public RequestCommentVo findByName(String name);
	
	/**
	 * 通过名称模糊查找对应页数的征求意见单
	 * @param name
	 * @param orgIds
	 * @param start
	 * @param size
	 * @return
	 */
	public Page<RequestCommentVo> findByName(String name, Set<Long> orgIds, int page, int size);
	
	/**
	 * 通过规范性文件id和反馈单位id查询征求意见单
	 * @param norId
	 * @param reqFromUnitId
	 * @return
	 */
	public RequestCommentVo findByNorFileId(Long norId, Long reqFromUnitId);
	
	/**
	 * 通过规范性文件id查询征求意见单
	 * @param norId
	 * @param reqFromUnitId
	 * @return
	 */
	public List<RequestCommentVo> findByNorFile(Long norId);
	
	/**
	 * 结束反馈意见阶段
	 * @param id
	 * @return
	 * @throws ServiceException 
	 */
	public boolean completeRequestback(Long id) throws ServiceException;
}
