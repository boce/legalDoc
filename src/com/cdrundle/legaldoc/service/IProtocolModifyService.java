package com.cdrundle.legaldoc.service;

import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.ProtocolModifyVo;

/**
 * @author  XuBao
 *草案修改
 * 2014年6月18日
 */
public interface IProtocolModifyService{
		
		/**
		 * 草案修改表单的内容和规范性文件的保存与更新
		 * @param protocolModifyVo
		 * @param path  文件路径
		 * @param content  写入Word内的内容
		 * @return	 ProtocolModifyVo
		 * @throws ServiceException 
		 */
		public  ProtocolModifyVo  saveOrUpdate(ProtocolModifyVo	protocolModifyVo,String path,String fileName,Boolean isConfirm) throws ServiceException;
		
		/**
		 * 删除当前草案修改表单以及规范性文件中的修改内容
		 * @param id
		 * @param path  文件路径
		 * @throws ServiceException 
		 */
		public  boolean  delete(Long id,String path) throws ServiceException;
		
		
		
		/**
		 * 查找本部门所有草案修改表单
		 * @param page  当前页数
		 * @param size    每页记录条数
		 * @return  page
		 */
		public		Page<ProtocolModifyVo> find(int page, int size,String name,Set<Long> orgIds);
		
		/**
		 * 确定最终版的规范性文件
		 * @return		ProtocolModifyVo
		 */
		public  ProtocolModifyVo  confirm(ProtocolModifyVo protocolModifyVo);
		
		/**
		 * 通过文件名查找
		 * @param name
		 * @return
		 */
		public    ProtocolModifyVo  findProtocolModifyByName(String name);
		
		/**
		 * 通过Id查找
		 * @param id
		 * @return
		 */
		public    ProtocolModifyVo  findById(Long  id);
		
		/**
		 * 通过规范性文件id查找
		 * @param id
		 * @return
		 */
		public  ProtocolModifyVo findByNorId(Long id);
}
