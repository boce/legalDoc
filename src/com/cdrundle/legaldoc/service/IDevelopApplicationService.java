package com.cdrundle.legaldoc.service;

import java.io.IOException;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.DevelopApplicationVo;

import freemarker.template.TemplateException;

/**
 * 立项操作
 * @author xiaokui.li
 *
 */
public interface IDevelopApplicationService
{
	/**
	 * 新增或者修改立项申请单
	 * @param developApplication
	 * @return
	 * @throws ServiceException 
	 * @throws IOException 
	 * @throws TemplateException 
	 */
	public DevelopApplicationVo saveOrUpdate(DevelopApplicationVo developApplicationVo, String rootPath, String tempFileId) throws ServiceException, IOException, TemplateException;
	
	/**
	 * 删除立项申请单
	 * @param developApplication
	 * @return
	 */
	public boolean delete(DevelopApplicationVo developApplicationVo);
	
	/**
	 * 删除立项申请单
	 * @param id
	 * @return
	 */
	public boolean delete(Long id);
	
	/**
	 * 提交立项申请单
	 * @param developApplication
	 * @return
	 */
	public boolean submit(DevelopApplicationVo developApplicationVo);
	
	/**
	 * 审批
	 * @param developApplication
	 * @return
	 */
	public boolean approve(DevelopApplicationVo developApplicationVo);
	
	/**
	 * 流程
	 * @return
	 */
	public boolean flow();
	
	/**
	 * 查找立项申请单
	 * @param id
	 * @return
	 */
	public DevelopApplicationVo findById(Long id);
	
	/**
	 * 根据名称查找立项申请单
	 * @param name
	 * @return
	 */
	public DevelopApplicationVo findByName(String name);
	
	/**
	 * 通过名称模糊查找对应页数的立项申请单
	 * @param name
	 * @param start
	 * @param size
	 * @return
	 */
	public Page<DevelopApplicationVo> findByName(String name, Set<Long> orgIds, int page, int size);
	
	/**
	 * 根据规范性文件id查询制定申请单
	 * @param id
	 * @return
	 */
	public DevelopApplicationVo findByNorFileId(Long id);
	
	
	/**
	 * 更新制定依据
	 * @param id
	 * @param LegalBasisAtta
	 */
	public void updateLegalBasisAtta(Long id, String legalBasisAtta);
	
	/**
	 * 更新制定的必要性、合法性，以及社会稳定性风险评估附件
	 * @param id
	 * @param necLegRiskAtta
	 */
	public void updateNecLegRiskAtta(Long id, String necLegRiskAtta);
	
	/**
	 * 更新拟解决的主要问题附件
	 * @param id
	 * @param mainProblemAtta
	 */
	public void updateMainProblemAtta(Long id, String mainProblemAtta);
	
	/**
	 * 更新拟确定的制度或措施，以及可行性论证附件
	 * @param id
	 * @param planRegMeaAtta
	 */
	public void updatePlanRegMeaAtta(Long id, String planRegMeaAtta);
	
	/**
	 * 删除立项申请单和对应上传文件
	 * @param developApplicationVo
	 * @param filePath
	 * @param fileName
	 * @return
	 * @throws ServiceException 
	 */
	public boolean deleteDevAndFile(DevelopApplicationVo developApplicationVo, String filePath, String fileName) throws ServiceException;
}
