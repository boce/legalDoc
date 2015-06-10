package com.cdrundle.legaldoc.service;

import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.ProtocolDeliberationVo;

/**
 * @author  XuBao
 *草案审议
 * 2014年6月18日
 */
public interface IProtocolDeliberationService{
		
	/**
	 * 草案审议和规范性文件的保存与更新
	 * @param deliberationRequestVo
	 * @param path  文件路径
	 * @param content  写入Word内的内容
	 * @return  DeliberationRequestVo 
	 * @throws ServiceException
	 */
	public   ProtocolDeliberationVo saveOrUpdate(ProtocolDeliberationVo protocolDeliberationVo,String path,String fileName) throws ServiceException;
	
	/**
	 * 删除草案审议和规范文件内的审议日期和审议意见
	 * @param id
	 * @param path  文件路径
	  * @throws ServiceException
	 */
	public   boolean   delete(Long  id,String path) throws ServiceException;
	
	/**
	 * 查找草案审议
	 * @param page  当前页数
	 * @param size		每页的记录条数
	 * @return  	
	 */
	public		Page<ProtocolDeliberationVo> find(int page, int size,String name,Set<Long> orgIds);
	
	/**
	 * 通过文件名查找
	 * @param name
	 * @return
	 */
	public   ProtocolDeliberationVo  findProtocolDeliberationByName(String  name);
	
	/**
	 * 通过id查找
	 * @param id
	 * @return
	 */
	public   ProtocolDeliberationVo  findById(Long   id);
	
	/**
	 * 通过规范性文件id查找
	 * @param id
	 * @return
	 */
	public  ProtocolDeliberationVo findByNorId(Long id);

}
