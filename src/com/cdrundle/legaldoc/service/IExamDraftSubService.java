package com.cdrundle.legaldoc.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cdrundle.legaldoc.entity.ExaminationDraftSubmit;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.DeferredAssessmentVo;
import com.cdrundle.legaldoc.vo.ExamDraftSubmitVo;

/**
 * 送审稿报送服务接口
 * 
 * @author gang.li
 *
 */
public interface IExamDraftSubService
{
	/**
	 * 保存或者更新
	 * @param examinationDraftSubmit
	 * @return ExamDraftSubmitVo
	 */
	public ExamDraftSubmitVo saveOrUpdate(ExamDraftSubmitVo examDraftSubmitVo, String filePath, String fileName) 
			throws ServiceException;
	
	/**
	 * 更新法律依据
	 * @param examDraftSubmitVo
	 * @return ExamDraftSubmitVo
	 */
	public ExamDraftSubmitVo updateLegalsis(ExamDraftSubmitVo examDraftSubmitVo) 
			throws ServiceException;
	
	/**
	 * 删除
	 * @param examDraftSubmitVo
	 * @filePath 删除的文件
	 * 
	 */
	public boolean delete(ExamDraftSubmitVo examDraftSubmitVo, String filePath) 
			throws ServiceException;
	
	/**
	 * 查询
	 * @param id
	 * @return ExamDraftSubmitVo
	 */
	public ExamDraftSubmitVo findById(Long id);
	
	/**
	 * 通过名称查询
	 * @param examinationDraftSubmitName
	 * @return List<ExaminationDraftSubmit>
	 */
	public List<ExaminationDraftSubmit> findByName(String examinationDraftSubmitName);
	
	/**
	 * 查询全部
	 * @param name
	 * @param reviewUnit
	 * @param status
	 * @param page
	 * @param size
	 * @param drfOrgIds
	 * @return Page<ExamDraftSubmitVo>
	 */
	public Page<ExamDraftSubmitVo> findAll(String name, Set<Long> drfOrgIds, Long reviewUnit,
			Status status, Integer page, Integer size) ;
	
	/**
	 * 查询通过名称和部门(开始的默认查询)
	 * @param page
	 * @param size
	 * @param drfOrgIds
	 * @return Page<ExamDraftSubmitVo>
	 */
	public Page<ExamDraftSubmitVo> findAllByOrgAndUnion(Set<Long> drfOrgIds,int page, int size);
	
	/**
	 * Vo转换成实体
	 * @param examDraftSubmitVo
	 * @return
	 */
	public ExaminationDraftSubmit voToExamDraftSub(ExamDraftSubmitVo examDraftSubmitVo);
	
	/**
	 * 实体转换成Vo
	 * @param examDraftSubmit
	 * @return
	 */
	public ExamDraftSubmitVo examDraftSubToVo(ExaminationDraftSubmit examDraftSubmit); 
	
	/**
	 * 实体集合转换成VoList
	 * @param examDraftSubmitList
	 * @return
	 */
	public List<ExamDraftSubmitVo> examDraftSubToVoList(List<ExaminationDraftSubmit> examDraftSubmitList); 
	
	/**
	 * 编辑
	 * @param examinationDraftSubmit
	 * @return 
	 */
	public ExaminationDraftSubmit edit(ExamDraftSubmitVo examDraftSubmitVo); 
	
	/**
	 * 提交送审稿
	 * @param examinationDraftSubmit
	 */
	public void submit(ExaminationDraftSubmit examinationDraftSubmit);
	
	/**
	 * 审核
	 * @param examinationDraftSubmit
	 * @return
	 */
	public void approve(ExaminationDraftSubmit examinationDraftSubmit);
	
	/**
	 * 弃审
	 * @param examinationDraftSubmit
	 * @return
	 */
	public void unApprove(ExaminationDraftSubmit examinationDraftSubmit);
	
	/**
	 * 流程
	 * @param examinationDraftSubmit
	 * @return
	 */
	public void flow(ExaminationDraftSubmit examinationDraftSubmit);
	
	/**
	 * 发送信息给审查经办员
	 * @param message
	 * @return
	 */
	public void send(String message);
	
	/**
	 * 根据norFileId查询送审稿
	 * @param norId
	 * @return ExaminationDraftSubmit
	 */
	public ExaminationDraftSubmit findByNorId(@Param("norId") long norId);
	
}
