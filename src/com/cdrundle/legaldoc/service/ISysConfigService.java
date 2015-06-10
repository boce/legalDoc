package com.cdrundle.legaldoc.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.vo.SysConfigVo;

/**
 * 系统参数操作
 * @author xiaokui.li
 *
 */
public interface ISysConfigService
{

	/**
	 * 新增或者更新系统参数
	 * @param sysConfig
	 * @return
	 */
	public SysConfigVo saveOrUpdate(SysConfigVo sysConfigVo);
	
	/**
	 * 删除系统参数
	 * @param sysConfig
	 * @return
	 */
	public boolean delete(SysConfigVo sysConfigVo);
	
	/**
	 * 删除系统参数
	 * @param id
	 * @return
	 */
	public boolean delete(long id);
	
	/**
	 * 假删除系统参数
	 * @param id
	 * @return
	 */
	public boolean deleteById(long id);
	
	/**
	 * 根据id查找系统参数对象
	 * @param id
	 * @return
	 */
	public SysConfigVo findById(long id);
	
	/**
	 * 根据编码查找系统参数对象
	 * @param code
	 * @return
	 */
	public SysConfigVo findByCode(String code);
	
	/**
	 * 查找所有系统参数对象
	 * @param id
	 * @return
	 */
	public List<SysConfigVo> findAll();
	
	public Page<SysConfigVo> findSysConfigRef(String code, String name, int page, int size);
}
