package com.cdrundle.legaldoc.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.entity.ExaminationDraftReview;
import com.cdrundle.legaldoc.entity.ExaminationDraftSubmit;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.ExamDraftReviewVo;
import com.cdrundle.legaldoc.vo.ExamDraftSubmitVo;

/**
 * 送审稿审查服务接口
 * 
 * @author gang.li
 *
 */
public interface IExamDraftReviewService
{
	
	/**
	 * Vo转换成实体
	 * @param examDraftReviewVo
	 * @return
	 */
	public ExaminationDraftReview voToExamDraftReview(ExamDraftReviewVo examDraftReviewVo);
	
	/**
	 * 实体转换成Vo
	 * @param examDraftReview
	 * @return
	 */
	public ExamDraftReviewVo examDraftReviewToVo(ExaminationDraftReview examDraftReview); 
	
	/**
	 * 实体集合转换成VoList
	 * @param examDraftReivewList
	 * @return
	 */
	public List<ExamDraftReviewVo> examDraftReviewToVoList(List<ExaminationDraftReview> examDraftReivewList);
	
	/**
	 * 保存或者更新
	 * @param examinationDraftReview
	 * @param filePath
	 * @param content
	 * @return ExaminationDraftReview
	 */
	public ExamDraftReviewVo saveOrUpdate(ExamDraftReviewVo examDraftReviewVo, 
			String filePath, String fileName) throws ServiceException;
	
	/**
	 * 删除
	 * @param examinationDraftReviewId
	 * @return 是否删除成功
	 */
	public boolean delete(ExamDraftReviewVo examDraftReviewVo, String filePath) throws ServiceException;
	
	/**
	 * 查询
	 * @param examDraftReviewId
	 * @return ExaminationDraftReview
	 */
	public ExamDraftReviewVo findById(Long examDraftReviewId);
	
	/**
	 * 查询
	 * @param examDraftReviewName
	 * @return List<ExaminationDraftReview>
	 */
	public List<ExaminationDraftReview> findByName(String examDraftReviewName);
	
	/**
	 * 通过规范性文件查询ExamDraftReviewVo
	 * @param id
	 * @return ExamDraftReviewVo
	 */
	public ExamDraftReviewVo findByNorFileId(Long id); 
	
	/**
	 * 查询全部
	 * @param page
	 * @param size
	 * @return Page<ExamDraftReviewVo>
	 */
	public Page<ExamDraftReviewVo> findAll(String name, Set<Long> drfOrgIds, Long reviewUnit,
			Status status, Integer page, Integer size) ;
	
	/**
	 * 查询全部
	 * @param page
	 * @param size
	 * @param drfOrgIds
	 * @return Page<ExamDraftReviewVo>
	 */
	public Page<ExamDraftReviewVo> findAllByUnit(Set<Long> drfOrgIds, int page, int size);
	
	/**
	 * 提交送审稿
	 * @param examinationDraftReview
	 */
	public void submit(ExaminationDraftReview examinationDraftReview);
	
	/**
	 * 审核
	 * @param examinationDraftReview
	 */
	public void approve(ExaminationDraftReview examinationDraftReview);
	
	/**
	 * 弃审
	 * @param examinationDraftReview
	 */
	public void unApprove(ExaminationDraftReview examinationDraftReview);
	
	/**
	 * 流程
	 * @param examinationDraftReview
	 */
	public void flow(ExaminationDraftReview examinationDraftReview);
	
	/**
	 * 发送信息给审查经办员
	 * @param message
	 */
	public void send(String message);
	
}
