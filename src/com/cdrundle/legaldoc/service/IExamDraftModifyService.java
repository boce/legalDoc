package com.cdrundle.legaldoc.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.entity.ExaminationDraftModify;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.ExamDraftModifyVo;

/**
 * 送审稿修改服务接口
 * 
 * @author gang.li
 *
 */
public interface IExamDraftModifyService
{
	/**
	 * Vo转换成实体
	 * @param examDraftModifyVo
	 * @return
	 */
	public ExaminationDraftModify voToExamDraftModify(ExamDraftModifyVo examDraftModifyVo);
	
	/**
	 * 实体转换成Vo
	 * @param examDraftModify
	 * @return
	 */
	public ExamDraftModifyVo examDraftModifyToVo(ExaminationDraftModify examDraftModify); 
	
	/**
	 * 实体集合转换成VoList
	 * @param examDraftModifyList
	 * @return
	 */
	public List<ExamDraftModifyVo> examDraftModifyToVoList(List<ExaminationDraftModify> examDraftModifyList);
	
	
	/**
	 * 保存或者更新
	 * @param examDraftModifyVo
	 * @param filePath
	 * @param fileName
	 * @return examDraftModifyVo
	 */
	public ExamDraftModifyVo saveOrUpdate(ExamDraftModifyVo examDraftModifyVo, 
			String filePath, String fileName) throws ServiceException;
	
	/**
	 * 删除
	 * @param examinationDraftModifyId
	 * @return
	 */
	public boolean delete(ExamDraftModifyVo examDraftModifyVo, String filePath) throws ServiceException;
	
	/**
	 * 查询
	 * @param examDraftModifyId
	 * @return ExaminationDraftModify
	 */
	public ExamDraftModifyVo findById(Long examDraftModifyId);
	
	/**
	 * 通过规范性文件查询ExamDraftReviewVo
	 * @param id
	 * @return ExamDraftReviewVo
	 */
	public ExamDraftModifyVo findByNorFileId(Long id); 
	
	/**
	 * 查询全部
	 * @param page
	 * @param size
	 * @return Page<ExaminationDraftSubmit>
	 */
	public Page<ExamDraftModifyVo> findAll(int page, int size);
	
	/**
	 * 查询通过名称和部门
	 * @param page
	 * @param size
	 * @return Page<ExaminationDraftSubmit>
	 */
	public Page<ExamDraftModifyVo> findAllByName(String name, int page, int size);
	
	/**
	 * 在服务器指定位置保存草案
	 * @examinationDraftModify 保存文件
	 * @param path 指定的路径
	 * @return 是否保存成功
	 */
	public boolean confrim(ExaminationDraftModify examinationDraftModify, String path);
}
