package com.cdrundle.legaldoc.service;

import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.ModifyDraftVo;

/**
 * 意见征求--修改《征求意见稿》操作
 * 
 * @author xiaokui.li
 * 
 */
public interface IModifyDraftService {
	/**
	 * 保存或者更新修改《征求意见稿》
	 * 
	 * @param modifyDraft
	 * @return
	 * @throws ServiceException 
	 */
	public ModifyDraftVo saveOrUpdate(ModifyDraftVo modifyDraftVo, String rootPath,boolean isConfirm) throws ServiceException;
	
	/**
	 * 根据id删除修改《征求意见稿》
	 * 
	 * @param id
	 * @return
	 * @throws ServiceException 
	 */
	public boolean delete(Long id,String rootPath) throws ServiceException;

	/**
	 * 根据id查询修改《征求意见稿》
	 * 
	 * @param id
	 * @return
	 */
	public ModifyDraftVo findById(Long id);

	/**
	 * 根据名称查询修改《征求意见稿》
	 * 
	 * @param name
	 * @return
	 */
	public Page<ModifyDraftVo> findByName(String name, Set<Long> orgIds, int page, int size);

	public ModifyDraftVo findByNorFileId(Long id);
}
