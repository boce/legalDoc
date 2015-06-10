package com.cdrundle.legaldoc.service;

import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.entity.FileCleanUp;
import com.cdrundle.legaldoc.entity.FileCleanUpLine;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.vo.FileCleanUpLineVo;
import com.cdrundle.legaldoc.vo.FileCleanupVo;

/**
 * 
 * @author gang.li
 * 文件清理服务接口
 */
public interface IFileCleanUpService {
	
	/**
	 * Vo转换成实体
	 * @param fileCleanupVo
	 * @return FileCleanUp
	 */
	public FileCleanUp voToFileCleanup(FileCleanupVo fileCleanupVo);
	
	/**
	 * 实体转换成Vo
	 * @param fileCleanUp
	 * @return FileCleanupVo
	 */
	public FileCleanupVo fileCleanupToVo(FileCleanUp fileCleanUp); 
	
	/**
	 * 实体集合转换成VoList
	 * @param fileCleanUpList
	 * @return List<FileCleanupVo>
	 */
	public List<FileCleanupVo> fcutoVoList(List<FileCleanUp> fileCleanUpList); 
	
	/**
	 * 子文件Vo转换成实体
	 * @param fculVo
	 * @return FileCleanUpLine
	 */
	public FileCleanUpLine voToFCULineVo(FileCleanUpLineVo fculVo);
	
	/**
	 * 子文件实体转换成Vo
	 * @param fcul
	 * @return FileCleanUpLineVo
	 */
	public FileCleanUpLineVo fculToVo(FileCleanUpLine fcul); 
	
	/**
	 * 子文件实体集合转换成VoList
	 * @param fculList
	 * @return List<FileCleanUpLineVo>
	 */
	public List<FileCleanUpLineVo> fcultoVoList(List<FileCleanUpLine> fculList);
	
	/**
	 * 保存或者更新
	 * @param fcuVo
	 * @return fculVoList
	 */
	public FileCleanupVo saveOrUpdate(FileCleanupVo fcuVo, List<FileCleanUpLineVo> fculVoList) throws ServiceException;
	
	/**
	 * 保存子节点(规范性文件)
	 * @param norId
	 */
	public FileCleanUpLineVo saveLineFile(FileCleanUpLineVo fculVo);
	
	/**
	 * 删除父节点
	 * @param fcuVo
	 */
	public boolean delete(FileCleanupVo fcuVo)  throws ServiceException;
	
	/**
	 * 删除子节点(规范性文件)
	 * @param norId
	 */
	public boolean deleteLineFile(Long lineId)  throws ServiceException;
	
	/**
	 * 通过ID查询
	 * @param fileCleanUpId
	 * @return 
	 */
	public FileCleanupVo findById(Long fileCleanUpId);
	
	/**
	 * 查询登录部门的所有的文件清理
	 * @param page
	 * @param size
	 * @return 
	 */
	public Page<FileCleanupVo> findAll(int page, int size);
	
	/**
	 * 查询所有子文件
	 * @param page
	 * @param size
	 * @return 
	 */
	public List<FileCleanUpLineVo> findFCULAll();
	
	/**
	 * 查询子清理文件
	 * @param id
	 */
    public  List<FileCleanUpLineVo>  findFCULines(long id);
    
    /**
	 * 分页查找父清理文件
	 * @param cleanupUnit
	 * @param publishDate
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<FileCleanupVo> findByUnitAndDate(Long cleanupUnit, Date cleanupBegDate, Date cleanupEndDate, Integer page, Integer size);
	
	/**
	 * 导出清理清单
	 * @param childList
	 * @param cleanup
	 * @return
	 */
	public HSSFWorkbook download(List<FileCleanUpLineVo> childList, FileCleanupVo cleanup);
	
	/**
	 * 打印清理文件列表
	 * @param childList
	 * @param cleanup
	 * @return
	 */
	public String printList(List<FileCleanUpLineVo> childList, FileCleanupVo cleanup);
	
	/**
	 * 提交送审稿
	 * @param fileCleanUp
	 */
	public void submit(FileCleanUp fileCleanUp);
	
	/**
	 * 审核
	 * @param fileCleanUp
	 * @return
	 */
	public void approve(FileCleanUp fileCleanUp);
	
	/**
	 * 弃审
	 * @param fileCleanUp
	 * @return
	 */
	public void unApprove(FileCleanUp fileCleanUp);
	
	/**
	 * 流程
	 * @param fileCleanUp
	 * @return
	 */
	public void flow(FileCleanUp fileCleanUp);
	
	/**
	 * 清理
	 * @param fileCleanUp
	 * @return
	 */
	public void cleanUp(FileCleanUp fileCleanUp);
	
}
