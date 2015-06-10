package com.cdrundle.legaldoc.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.entity.DeferredAssessment;
import com.cdrundle.legaldoc.entity.FileCleanUp;
import com.cdrundle.legaldoc.enums.Status;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.DeferredAssessmentVo;
import com.cdrundle.legaldoc.vo.ExamDraftReviewVo;
import com.cdrundle.legaldoc.vo.FileCleanupVo;


/**
 * 满期评估服务接口
 * 
 * @author gang.li
 *
 */
public interface IDeferredAssessService
{
	/**
	 * Vo转换成实体
	 * @param daVo
	 * @return DeferredAssessment
	 */
	public DeferredAssessment voToDA(DeferredAssessmentVo daVo);
	
	/**
	 * 实体转换成Vo
	 * @param da
	 * @return DeferredAssessmentVo
	 */
	public DeferredAssessmentVo daToVo(DeferredAssessment da); 
	
	/**
	 * 实体集合转换成VoList
	 * @param daList
	 * @return List<DeferredAssessmentVo>
	 */
	public List<DeferredAssessmentVo> daToVoList(List<DeferredAssessment> daList); 
	
	/**
	 * 保存或者更新
	 * @param daVo
	 * @return DeferredAssessmentVo
	 */
	public DeferredAssessmentVo saveOrUpdate(DeferredAssessmentVo daVo) throws ServiceException;
	
	/**
	 * 删除
	 * @param daVo
	 *
	 */
	public boolean delete(DeferredAssessmentVo daVo) throws ServiceException;

	/**
	 * 
	 * @param cleanupUnit
	 * @param cleanupBegDate
	 * @param cleanupEndDate
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<DeferredAssessmentVo> findAll(String name, Long draftingUnit, Long decisionUnit,
			Status status, Integer page, Integer size) ;
	
	/**
	 * 通过Id进行查询
	 * @param id
	 * @return DeferredAssessmentVo
	 */
	public DeferredAssessmentVo findById(Long id);
	
	/**
	 * 通过规范性文件查询DeferredAssessmentVo
	 * @param id
	 * @return DeferredAssessmentVo
	 */
	public DeferredAssessmentVo findByNorFileId(Long id); 
	
	/**
	 * 通过Name进行查询
	 * @param eferredAssessmentName
	 * @return List<DeferredAssessment>
	 */
	public List<DeferredAssessment> findByName(String deferredAssessmentName);
	
	/**
	 * 提交评估
	 * @param eferredAssessment
	 */
	public void submit(DeferredAssessment deferredAssessment);
	
	/**
	 * 审核
	 * @param eferredAssessment
	 * @return
	 */
	public void approve(DeferredAssessment deferredAssessment);
	
	/**
	 * 弃审
	 * @param eferredAssessment
	 * @return
	 */
	public void unApprove(DeferredAssessment deferredAssessment);
	
	/**
	 * 流程
	 * @param eferredAssessment
	 * @return
	 */
	public void flow(DeferredAssessment deferredAssessment);
	
	/**
	 * 发送规范性文件评估结果到法制网和公众信息网公布
	 * @param message
	 * @return
	 */
	public void publish(String message);
}
