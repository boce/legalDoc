package com.cdrundle.legaldoc.service;

import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.AdoptCommentVo;

/**
 * 意见征求--反馈意见处理情况操作
 * @author xiaokui.li
 *
 */
public interface IAdoptCommentService
{
	/**
	 * 保存或者更新反馈意见处理情况
	 * @param adoptComment
	 * @param rootPath
	 * @param isNeedModify
	 * @return
	 * @throws ServiceException 
	 */
	public AdoptCommentVo saveOrUpdate(AdoptCommentVo adoptCommentVo,String rootPath) throws ServiceException;
	
	
	/**
	 * 根据id删除反馈意见处理情况
	 * @param id
	 * @return
	 * @throws ServiceException 
	 */
	public boolean delete(Long id,String rootPath) throws ServiceException;
	
	/**
	 * 根据id查询反馈意见处理情况
	 * @param id
	 * @return
	 */
	public AdoptCommentVo findById(Long id);
	
	/**
	 * 根据名称查询反馈意见处理情况
	 * @param name
	 * @return
	 */
	public Page<AdoptCommentVo> findByName(String name, Set<Long> orgIds,int page,int size);
	
	
	public AdoptCommentVo findByNorFileId(Long id);
}
