package com.cdrundle.legaldoc.service;

import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.RecordReviewVo;

/**
 * @author  XuBao
 *备案审查
 * 2014年6月19日
 */
public interface IRecordReviewService{
		
	/**
	 * 备案审查和规范性文件的保存与更新
	 * @param deliberationRequestVo
	 * @param path  文件路径
	 * @param content  写入Word内的内容
	 * @return  DeliberationRequestVo 
	 * @throws ServiceException
	 */
	public   RecordReviewVo  saveOrUpdate(RecordReviewVo recordReviewVo,String path,String fileName) throws ServiceException;
	
	/**
	 * 删除备案审查和规范文件内的审查日期，存在的问题类型，审查结果
	 * @param id
	 *  @param path  文件路径
	 * @throws ServiceException
	 */
	public   boolean   delete(Long  id,String path) throws ServiceException;
	
	/**
	 * 文件的提交
	 * @return		boolean
	 */
	public   boolean  submit( RecordReviewVo recordReviewVo);

	/**
	 * 文件的审核
	 * @return		boolean
	 */	
	public   boolean   approve(RecordReviewVo recordReviewVo);

	/**
	 * 文件的弃审
	 * @return		boolean
	 */	
	public   boolean   unApprove(RecordReviewVo recordReviewVo);

	/**
	 * 当前文件的流程
	 * @return		boolean
	 */	
	public   boolean   flow(RecordReviewVo recordReviewVo);
	
	/**
	 * 查找所有的备案审查
	 * @param page  当前页数
	 * @param size		每页的记录条数
	 * @return page
	 */
	public		Page<RecordReviewVo> find(int page, int size,String name,Set<Long> orgIds);
	
	/**
	 * 文件备案审查后向上级报备
	 * @param recordReview
	 * @return
	 * @throws ServiceException 
	 */
	public  RecordReviewVo  send(RecordReviewVo    recordReviewVo) throws ServiceException;
	
	/**
	 * 备案登记
	 * @param recordReviewVo
	 * @return  
	 */
	public  RecordReviewVo register(RecordReviewVo recordReviewVo) throws ServiceException; 
	/**
	 * 通过文件名查找
	 * @param name
	 * @return
	 */
	public   RecordReviewVo  findRecordReviewByName(String  name);
	
	/**
	 * 通过id查找
	 * @param id
	 * @return
	 */
	public   RecordReviewVo  findById(Long   id);
	
	/**
	 * 通过规范性文件id查找
	 * @param id
	 * @return
	 */
	public  RecordReviewVo findByNorId(Long id);
	
	/**
	 * 得到备案登记号
	 * @return
	 */
	public String gainRegisterCode();
}
