package com.cdrundle.legaldoc.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.FeedbackCommentVo;

/**
 * 意见征求--反馈意见操作
 * @author xiaokui.li
 *
 */
public interface IFeedbackCommentService
{
	/**
	 * 保存或者更新反馈意见单
	 * @param feedbackComment
	 * @return
	 * @throws ServiceException 
	 */
	public FeedbackCommentVo saveOrUpdate(FeedbackCommentVo feedbackCommentVo,String rootPath) throws ServiceException;
	
	/**
	 * 根据征求意见单对象删除反馈意见单
	 * @param feedbackComment
	 * @return
	 * @throws ServiceException 
	 */
	public boolean delete(Long id,String rootPath) throws ServiceException;
	
	
	/**
	 * 根据id查询反馈意见单
	 * @param id
	 * @return
	 */
	public FeedbackCommentVo findById(long id);
	
	/**
	 * 根据名称查询反馈意见单
	 * @param name
	 * @return
	 */
	public FeedbackCommentVo findByName(String name);
	
	/**
	 * 通过名称模糊查找对应页数的反馈意见单
	 * @param name
	 * @param start
	 * @param size
	 * @return
	 */
	public Page<FeedbackCommentVo> findByName(String name, Set<Long> orgIds, int page, int size);
	
	public FeedbackCommentVo findByNorFileId(Long id, Long feedbackUnit);
	
	/**
	 * 根据规范性文件id查询反馈意见单
	 * @param norId
	 * @return
	 */
	public List<FeedbackCommentVo> findByNorFile(Long norId);
}
