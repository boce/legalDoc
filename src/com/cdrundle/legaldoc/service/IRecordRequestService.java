package com.cdrundle.legaldoc.service;

import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.RecordRequestVo;

/**
 * @author  XuBao
 *备案报送
 * 2014年6月19日
 */
public interface IRecordRequestService{
	
	/**
	 * 备案报送和规范性文件的保存与更新
	 * @param deliberationRequestVo
	 * @param path  文件路径
	 * @param content  写入Word内的内容
	 * @return  DeliberationRequestVo 
	 * @throws ServiceException
	 */
	public   RecordRequestVo  saveOrUpdate(RecordRequestVo recordRequestVo,String path,String fileName) throws ServiceException;
	
	/**
	 * 删除备案报送和规范文件内的备案审查单位，经办员，负责人，联系电话，报送日期，备案报告
	 * @param id
	 * @param path  文件路径
	 * @throws ServiceException
	 */
	public   boolean   delete(Long  id,String path) throws ServiceException;
	
	/**
	 * 文件的提交
	 * @return		boolean
	 */
	public   boolean  submit(RecordRequestVo recordRequestVo);

	/**
	 * 文件的审核
	 * @return		boolean
	 */	
	public   boolean   approve(RecordRequestVo recordRequestVo);

	/**
	 * 文件的弃审
	 * @return		boolean
	 */	
	public   boolean   unApprove(RecordRequestVo recordRequestVo);

	/**
	 * 当前文件的流程
	 * @return		boolean
	 */	
	public   boolean   flow(RecordRequestVo recordRequestVo);
	
	/**
	 * 查找所有的备案报送
	 * @param page  当前页数
	 * @param size		每页的记录条数
	 * @return  page
	 */
	public		Page<RecordRequestVo> find(int page, int size,String name,Set<Long> orgIds);
	
	/**
	 * 文件备案报送
	 * @return
	 */
	public   boolean   send();
	
	/**
	 * 通过文件名查找
	 * @param name
	 * @return
	 */
	public   RecordRequestVo  findRecordRequestByName(String  name);
	
	/**
	 * 通过id查找
	 * @param id
	 * @return
	 */
	public   RecordRequestVo  findById(Long  id);
	
	/**
	 * 通过规范性文件id查找
	 * @param id
	 * @return
	 */
	public  RecordRequestVo findByNorId(Long id);


}
