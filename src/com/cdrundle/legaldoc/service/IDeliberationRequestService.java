package com.cdrundle.legaldoc.service;

import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.DeliberationRequestVo;

/**
 * @author  XuBao
 *审议报请
 * 2014年6月10日
 */
public interface IDeliberationRequestService {

				
	/**
	 * 审议报请和规范性文件的保存与更新
	 * @param deliberationRequestVo
	 * @param path  文件路径
	 * @param content  写入Word内的内容
	 * @return  DeliberationRequestVo 
	 * @throws ServiceException
	 */
	public   DeliberationRequestVo saveOrUpdate(DeliberationRequestVo deliberationRequestVo,String fileName,String path) throws ServiceException;
	
	/**
	 * 删除审议报请和规范文件内的审议单位以及报请日期
	 * @param id
	 *  @param path  文件路径
	 * @throws ServiceException
	 */
	public   boolean   delete(Long  id,String path) throws ServiceException;
	
	
	/**
	 * 文件的提交
	 * @return		boolean
	 */
	public   boolean  submit(DeliberationRequestVo  deliberationRequestVo);
	
	/**
	 * 文件的审核
	 * @return		boolean
	 */	
	public   boolean   approve(DeliberationRequestVo  deliberationRequestVo);
	
	/**
	 * 文件的弃审
	 * @return		boolean
	 */	
	public   boolean   unApprove(DeliberationRequestVo  deliberationRequestVo);
	
	/**
	 * 当前文件的流程
	 * @return		boolean
	 */	
	public   boolean   flow(DeliberationRequestVo  deliberationRequestVo);
	
	/**
	 * 查找审议报请
	 * @param page  当前页数
	 * @param size		每页的记录条数
	 * @return  	
	 */
	public  Page<DeliberationRequestVo> find(int page, int size,String name,Set<Long>  orgIds);
	
	/**
	 * 通过文件名查找审议报请
	 * @param name
	 * @return
	 */
	public   DeliberationRequestVo  findDeliberationRequestByName(String  name );
	
	/**
	 * 通过id查找
	 * @param id
	 * @return
	 */
	public   DeliberationRequestVo   findDeliberationRequestById(Long  id);
	
	/**
	 * 通过规范性文件Id查找出审议报请
	 * @param id
	 * @return
	 */
	public  DeliberationRequestVo  findByNorId( long id);


}
