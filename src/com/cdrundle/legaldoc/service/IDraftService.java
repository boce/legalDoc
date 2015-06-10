package com.cdrundle.legaldoc.service;

import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.DraftVo;


/**
 * 起草操作
 * @author xiaokui.li
 *
 */
public interface IDraftService
{
	
	/**
	 * 保存或者更新起草单
	 * @param draftVo
	 * @param rootPath
	 * @return
	 * @throws ServiceException
	 */
	public DraftVo saveOrUpdate(DraftVo draftVo, String rootPath, Boolean isConfirm) throws ServiceException;
	
	/**
	 * 根据起草对象删除起草单
	 * @param draft
	 * @return
	 */
	public boolean delete(DraftVo draftVo);
	
	/**
	 * 根据id删除起草单
	 * @param id
	 * @return
	 */
	public boolean delete(long id);
	
	/**
	 * 根据id删除起草单及对象文件
	 * @param id
	 * @param rootPath
	 * @return
	 * @throws ServiceException 
	 */
	public boolean delete(Long id, String rootPath) throws ServiceException;
	
	/**
	 * 根据id查询起草单
	 * @param id
	 * @return
	 */
	public DraftVo findById(Long id);
	
	/**
	 * 根据名称查询起草单
	 * @param name
	 * @return
	 */
	public DraftVo findByName(String name);
	
	/**
	 * 通过名称模糊查找对应页数的起草申请单
	 * @param name
	 * @param orgIds
	 * @param start
	 * @param size
	 * @return
	 */
	public Page<DraftVo> findByName(String name, Set<Long> orgIds, int page, int size);
	
	public DraftVo findByNorFileId(Long norId);
	
	/**
	 * 根据涉及部门id查找名称
	 * @param involvedOrges
	 * @return
	 */
	public String getInvolvedOrgesName(String involvedOrges);
	
}
