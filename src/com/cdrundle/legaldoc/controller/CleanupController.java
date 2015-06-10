package com.cdrundle.legaldoc.controller;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.base.BaseController;
import com.cdrundle.legaldoc.enums.FileStatus;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IFileCleanUpService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IUserService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.vo.FileCleanUpLineVo;
import com.cdrundle.legaldoc.vo.FileCleanupVo;
import com.cdrundle.legaldoc.vo.NorFileQueryResultVo;
import com.cdrundle.legaldoc.vo.NorFileQueryVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;

/**
 * 文件清理controller
 * @author gang.li
 * 
 */
@Controller
public class CleanupController extends BaseController {
	
	@Autowired
	private IFileCleanUpService fcuService;
	
	@Autowired
	private INormativeFileService norService;

	@Autowired
	private IUserService userService;
	
	private final Log logger = LogFactory.getLog(getClass());

	@InitBinder
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(SysUtil.DATE_FORMAT_TIME);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));
	}
	
	/**
	 * 查询本部门所有的文件清理
	 * @param page
	 * @param size
	 * @return 
	 */
	@ResponseBody
	@RequestMapping("/cleanup/searchAll")
	public String searchAll(Long cleanupUnit, Date cleanupBegDate, Date cleanupEndDate, PageParam page) {
		StringBuffer jsonBuffer = new StringBuffer();
		SimpleDateFormat dateformat = new SimpleDateFormat(SysUtil.DATE_FORMAT);
		Page<FileCleanupVo> filePages = fcuService.findByUnitAndDate(cleanupUnit, cleanupBegDate, cleanupEndDate, page.getPage(), page.getRows());
		//filePages = fcuService.findAll(page, rows);
		//拼接treegrid的json数据格式
		if (filePages.getTotalElements() > 0) {
			List<FileCleanupVo> fcuList = filePages.getContent();
			jsonBuffer.append("{\"total\":" + filePages.getTotalElements() + ",\"rows\":");
			jsonBuffer.append("[");
			boolean fFlag = false;
			for (int index = 0; index < fcuList.size(); index++) {
				FileCleanupVo fcuVo = fcuList.get(index);
				if (fFlag) {
					jsonBuffer.append(SysUtil.COMMA);
				} else {
					fFlag = true;
				}
				jsonBuffer.append("{");
				jsonBuffer.append("\"id\":\"" + fcuVo.getId() + "\"");	//以免id重复
				jsonBuffer.append(SysUtil.COMMA);
				jsonBuffer.append("\"name\":\"" + fcuVo.getCleanupUnit().getText() + "(" + dateformat.format(fcuVo.getCleanupDate()) + ")\"");
				jsonBuffer.append(SysUtil.COMMA);
				jsonBuffer.append("\"state\":\"closed\"");
				jsonBuffer.append(SysUtil.COMMA);
				jsonBuffer.append("\"children\":[]");
				
				jsonBuffer.append("}");
			}
			jsonBuffer.append("]}");
				
		} else {
			jsonBuffer.append("[]");
		}
		return jsonBuffer.toString();
	}
	
	/**
	 * 延迟加载
	 * @param parentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/cleanup/gainChildren")
	public String gainChildren(long parentId) {
		StringBuffer jsonChild = new StringBuffer();
		SimpleDateFormat dateformat = new SimpleDateFormat(SysUtil.DATE_FORMAT_NORMAL);
		List<FileCleanUpLineVo> childList = fcuService.findFCULines(parentId);
		boolean cFlag = false;
		if (childList.size() > 0) {
			jsonChild.append("[");
			for (int chIndex = 0; chIndex < childList.size(); chIndex++) {
				FileCleanUpLineVo fculVo = childList.get(chIndex);
				if (cFlag) {
					jsonChild.append(SysUtil.COMMA);
				} else {
					cFlag = true;
				}
				jsonChild.append("{");
				jsonChild.append("\"id\":\"" + fculVo.getId() + SysUtil.CHILD_NODE + "\"");	//子文件id加上c
				jsonChild.append(SysUtil.COMMA);
				jsonChild.append("\"name\":\"" + fculVo.getName() + "\"");
				jsonChild.append(SysUtil.COMMA);
				jsonChild.append("\"decisionUnit.text\":\"" + fculVo.getDecisionUnit().getText() + "\"");
				jsonChild.append(SysUtil.COMMA);
				jsonChild.append("\"publishNo\":\"" + fculVo.getPublishNo() + "\"");
				jsonChild.append(SysUtil.COMMA);
				jsonChild.append("\"publishDate\":\"" + dateformat.format(fculVo.getPublishDate()) + "\"");
				jsonChild.append(SysUtil.COMMA);
				jsonChild.append("\"status\":\"" + fculVo.getCleanupResult() + "\"");
				jsonChild.append(SysUtil.COMMA);
				String remark = fculVo.getRemark();
				if(StringUtils.isEmpty(remark)){
					remark = "";
				}
				jsonChild.append("\"invalidReason\":\"" + remark + "\"");
				jsonChild.append("}");
			}
			jsonChild.append("]");
		} else {
			jsonChild.append("[]");
		}
		return jsonChild.toString();
	}
	
	/**
	 * 加载父清理文件
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/cleanup/load")
	public FileCleanupVo load(HttpServletRequest request, HttpServletResponse response, String id) {
		FileCleanupVo fileVo = fcuService.findById(Long.valueOf(id));
		if (null != fileVo) {
			return fileVo;
		} else {
			return null;
		}
	}
	
	/**
	 * 加载子清理文件
	 * @param request
	 * @param response
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/cleanup/gainFileLines")
	public List<FileCleanUpLineVo> gainFileLines(HttpServletRequest request, HttpServletResponse response, String fileId) {
		List<FileCleanUpLineVo> childList = new ArrayList<FileCleanUpLineVo>();
		if (!StringUtils.isEmpty(fileId)) {
			childList = fcuService.findFCULines(Long.valueOf(fileId));
		}
		return childList;
	}
	
	/**
	 * 查询子清理文件
	 * @param request
	 * @param response
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/cleanup/searchNorFiles")
	public Map<String, Object> searchNorFiles(NorFileQueryVo queryVo, PageParam page) {
		Page<NorFileQueryResultVo> filePages = norService.findAllForAdjustAndCleanup(queryVo, page.getPage(), page.getRows());
		//过滤规范性文件
		List<NorFileQueryResultVo> norList = filePages.getContent();
		List<NorFileQueryResultVo> realList = new ArrayList<NorFileQueryResultVo>();
		List<FileCleanUpLineVo> lineList = fcuService.findFCULAll();
		int norSize = norList.size();
		for(int norIndex = 0; norIndex < norList.size(); norIndex++) {
			String name = norList.get(norIndex).getName();
			if (!StringUtils.isEmpty(name)) {
				boolean flag = true;
				for(int cleanIndex = 0; cleanIndex < lineList.size(); cleanIndex++) {
					if (name.equals(lineList.get(cleanIndex).getName())) {
						flag = false;
						break;
					}
				}
				if (flag) {
					realList.add(norList.get(norIndex));
				}
			}
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", filePages.getTotalElements() - (norSize - realList.size()));
		result.put("rows", realList);

		return result;
	}
	
	/**
	 * 删除子清理文件
	 * @param fileId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/cleanup/deleteLineFile")
	public String deleteLineFile(long fileId) {
		boolean deleteFlag = false;
		try {
			deleteFlag = fcuService.deleteLineFile(fileId);
		} catch (ServiceException e) {
			deleteFlag = false;
		}
		if (deleteFlag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
	}
	
	/**
	 * 确认时保存子清理文件
	 * @param request
	 * @param response
	 * @param fileId
	 * @return 保存的list
	 */
	@ResponseBody
	@RequestMapping("/cleanup/saveLineFiles")
	public String saveLineFiles(String fileIds, long parentId) {
		FileCleanupVo fcuVo = fcuService.findById(parentId);
		NormativeFileVo norFile = null;
		FileCleanUpLineVo fculVo = null;
		boolean flag = false;
		if (!StringUtils.isEmpty(fileIds)) {
			if (fileIds.contains(SysUtil.COMMA)) {
				String[] ids = fileIds.split(SysUtil.COMMA);
				for (int index = 0; index < ids.length; index++) {
					norFile = norService.findById(Long.valueOf(ids[index]));
					fculVo = new FileCleanUpLineVo();
					fculVo.setId(norFile.getId());
					fculVo.setDecisionUnit(norFile.getDecUnit());
					fculVo.setFileCleanupVo(fcuVo);
					fculVo.setName(norFile.getName());
					fculVo.setPublishDate(norFile.getPublishDate());
					fculVo.setPublishNo(norFile.getPublishNo());
					fculVo.setCleanupResult(FileStatus.VALID);
					fcuService.saveLineFile(fculVo);
				}
				flag = true;
			} else {
				norFile = norService.findById(Long.valueOf(fileIds));
				fculVo = new FileCleanUpLineVo();
				fculVo.setId(norFile.getId());
				fculVo.setDecisionUnit(norFile.getDecUnit());
				fculVo.setFileCleanupVo(fcuVo);
				fculVo.setName(norFile.getName());
				fculVo.setPublishDate(norFile.getPublishDate());
				fculVo.setPublishNo(norFile.getPublishNo());
				fculVo.setCleanupResult(FileStatus.VALID);
				fcuService.saveLineFile(fculVo);
				flag = true;
			}
			
		}
		if (flag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
	}
	
	
	/**
	 * 增加父清理文件
	 * @param request
	 * @param response
	 * @param fcuVo
	 * @param fileIds
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/cleanup/saveCleanup")
	public String saveCleanup(HttpServletRequest request, HttpServletResponse response, 
			FileCleanupVo fcuVo, String fileIds) {
		List<FileCleanUpLineVo> fculVoList = new ArrayList<FileCleanUpLineVo>();
		FileCleanUpLineVo fculVo = null;
		NormativeFileVo norFile = null;
		//FileCleanupVo vo = fcuService.findCleanupByUnitAndDate(fcuVo.getCleanupUnit().getId(), 
		//		fcuVo.getCleanupDate(), fcuVo.getCleanupDate());
		if (!StringUtils.isEmpty(fileIds)) {
			String[] ids = fileIds.split(SysUtil.COMMA);
			for (int index = 0; index < ids.length; index++) {
				norFile = norService.findById(Long.valueOf(ids[index]));
				fculVo = new FileCleanUpLineVo();
				fculVo.setId(norFile.getId());
				fculVo.setDecisionUnit(norFile.getDecUnit());
				fculVo.setFileCleanupVo(fcuVo);
				fculVo.setName(norFile.getName());
				fculVo.setPublishDate(norFile.getPublishDate());
				fculVo.setPublishNo(norFile.getPublishNo());
				fculVo.setCleanupResult(norFile.getStatus());
				fculVoList.add(fculVo);
			}
		}
		try {
			fcuVo.setStatus(FileStatus.INVALID);
			FileCleanupVo fileCleanupVo= fcuService.saveOrUpdate(fcuVo, fculVoList);
			if (fileCleanupVo != null) {
				return "{\"msg\":\"success\", \"cleanupId\":\"" + fileCleanupVo.getId() + "\"}";
			}
		} catch (ServiceException e) {
			return SysUtil.JSON_MSG_FAIL;
		}
		return SysUtil.JSON_MSG_FAIL;
	}
	
	/**
	 * 删除清理文件
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/cleanup/delete")
	public String delete(HttpServletRequest request, HttpServletResponse response, String id) {
		FileCleanupVo fcuVo = fcuService.findById(Long.valueOf(id));
		boolean dFlag = false;
		try {
			dFlag = fcuService.delete(fcuVo);
		} catch (ServiceException e) {
			dFlag = false;
		}
		if (dFlag) {
			return SysUtil.JSON_MSG_SUCCESS;
		} else {
			return SysUtil.JSON_MSG_FAIL;
		}
		
	}
	

	
	/**
	 * 下载清理文件列表
	 * @param request
	 * @param response
	 * @param cleanId
	 * @return
	 */
	@RequestMapping("/cleanup/download")
	public void downloadList(HttpServletRequest request, HttpServletResponse response, Long cleanId) {
		ServletOutputStream out = null;
		List<FileCleanUpLineVo> childList = fcuService.findFCULines(cleanId);
		FileCleanupVo cleanup = fcuService.findById(cleanId);
		if (null != childList) {
			try {
				response.reset();
		        response.setContentType("APPLICATION/OCTET-STREAM");
		        String downloadName = "清理文件列表.xls";
		        downloadName = response.encodeURL(new String(downloadName.getBytes(),"ISO8859_1"));
		        response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadName + "\"");
		        out = response.getOutputStream();
		        HSSFWorkbook wb = fcuService.download(childList, cleanup);
		        wb.write(out);
		        response.setStatus(HttpServletResponse.SC_OK);
			    response.flushBuffer();	//清除缓存
		        out.close();
			}  catch (Exception e) {
				logger.error("下载出错!", e);
	        } finally {
	        	try {
					out.close();
				} catch (IOException e) {
					logger.error("下载出错!", e);
				}
	        }
		}
	}
	
	/**
	 * 打印清理文件
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/cleanup/print")
	public String printCleanup(HttpServletRequest request, HttpServletResponse response, String id) {
		FileCleanupVo fcuVo = fcuService.findById(Long.valueOf(id));
		List<FileCleanUpLineVo> childList = fcuService.findFCULines(fcuVo.getId());
		return fcuService.printList(childList, fcuVo);
	}
	
}
