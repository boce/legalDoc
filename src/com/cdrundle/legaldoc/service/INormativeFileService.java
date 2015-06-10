package com.cdrundle.legaldoc.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.enums.OrgType;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.vo.NorFileQueryResultVo;
import com.cdrundle.legaldoc.vo.NorFileQueryVo;
import com.cdrundle.legaldoc.vo.NorFileShortVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;

/**
 * @author XuBao 规范性文件 2014年6月12日
 */
public interface INormativeFileService {

	/**
	 * 查找出本部门所有的规范性文件
	 * 
	 * @param stage
	 *            文件阶段
	 * @param drtUnit主起草部门
	 * @return page
	 */
	public Page<NorFileShortVo> findNorFileForReference(Stage stage, int page, int size);

	/**
	 * 通过文件名查找出本部门的规范性文件
	 * 
	 * @param name
	 * @param stage
	 * @return page
	 */
	public Page<NorFileShortVo> findNorFileByName(String name, Stage stage, int page, int size);

	/**
	 * 动态查询规范性文件
	 * 
	 * @param dto
	 * @param pageable
	 * @return
	 */
	public Page<NorFileQueryResultVo> findAllForAdjustAndCleanup(NorFileQueryVo dto, int page, int size);

	/**
	 * 通过ID查找规范性文件
	 * 
	 * @param id
	 * @return
	 */
	public NormativeFileVo findById(Long id);

	
	/**
	 * 查询登录人员所属部门的规范性文件
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<NorFileShortVo> findNorFileByOwnOrg(String name, int page, int size);
	
	/**
	 * 查询登录人员授权部门的规范性文件
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<NorFileShortVo> findNorFileByOrg(String name, OrgType orgType , int page, int size);
	
	/**
	 * 添加联合起草单位、联合起草单位负责人、涉及部门名称
	 * @param norFile
	 */
	public void genName(NormativeFileVo norFile);
	
	/**
	 * 添加联合起草单位、联合起草单位负责人、涉及部门名称
	 * @param norFile
	 */
	public void genNameList(List<NormativeFileVo> norFiles);
}
