package com.cdrundle.legaldoc.service;

import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.enums.OrgType;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.RecordReviewVo;

/**
 * 报表服务接口
 * 
 * @author gang.li
 *
 */
public interface IReportService
{
	/**
	 * 根据传入的条件分页查询规范性文件
	 * @param nameList
	 * @param condList
	 * @param valueList
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<NormativeFileVo> findNorFiles(List<String> nameList, List<String> condList, 
			List<String> valueList, Integer page, Integer size);
	
	/**
	 * 根据传入的条件查询规范性文件
	 * @param nameList
	 * @param condList
	 * @param valueList
	 * @return
	 */
	public List<NormativeFileVo> findAllNorFiles(List<String> nameList, List<String> condList, 
			List<String> valueList);
	
	/**
	 * 导出查询出来的规范性文件
	 * @param nameList
	 * @param labelList
	 * @param norList
	 * @return
	 */
	public HSSFWorkbook exportNorFiles(List<String> nameList, List<String> labelList, 
			List<NormativeFileVo> norList)  throws ServiceException;
	
	/**
	 * 分页查询所有的规范性文件
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<NormativeFileVo> findAllPage(Integer page, Integer size);
	
	/**
	 * 查询所有的规范性文件
	 * @return
	 */
	public List<NormativeFileVo> findAll();
	
	/**
	 * 
	 * @param nameList
	 * @param condList
	 * @param valueList
	 * @param page
	 * @param size
	 * @return
	 * @throws ServiceException
	 */
	public Page<RecordReviewVo> findRecReviews(List<String> nameList, List<String> condList, 
			List<String> valueList, Date begDate, Date endDate, Integer page, Integer size)  throws ServiceException;
	
	/**
	 * 查询所有的备案
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<RecordReviewVo> findAllRecRev(Integer page, Integer size);
	
	
	/**
	 * 导出查询出来的备案
	 * @param filePages
	 * @return
	 */
	public HSSFWorkbook exportRecRevs(Page<RecordReviewVo> filePages)  throws ServiceException;
	
	/**
	 * 查询norFile通过组织机构类型和年份
	 * @param orgType
	 * @param year
	 * @return
	 */
	public String searchNorFileNum(Integer year, OrgType orgType)  throws ServiceException;
	
	/**
	 * 导出统计报表
	 * @param year
	 * @param jsonDate
	 * @return
	 */
	public HSSFWorkbook exportNorFileCount(String year, String jsonDate)  throws ServiceException;
}
