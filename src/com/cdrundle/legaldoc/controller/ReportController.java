package com.cdrundle.legaldoc.controller;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import org.json.JSONArray;
import org.json.JSONObject;
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
import com.cdrundle.legaldoc.enums.FileStatusEnumEditor;
import com.cdrundle.legaldoc.enums.OrgType;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.enums.StageEnumEditor;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IReportService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;
import com.cdrundle.legaldoc.vo.RecordReviewVo;

/**
 * 报表controller
 * @author gang.li
 * 
 */
@Controller
public class ReportController extends BaseController {
	@Autowired
	private IReportService reportService;
	
	@Autowired
	private INormativeFileService norFileService;
	
	private final Log logger = LogFactory.getLog(getClass());
	
	@InitBinder
	protected void initBinder(HttpServletRequest request, 
			ServletRequestDataBinder binder) throws IOException {
		binder.registerCustomEditor(FileStatus.class,
				new FileStatusEnumEditor());
		binder.registerCustomEditor(Stage.class,
				new StageEnumEditor());
		SimpleDateFormat dateFormat = new SimpleDateFormat(SysUtil.DATE_FORMAT_NORMAL);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));
	}
	
	@ResponseBody
	@RequestMapping("/report/getStatus")
	public List<Map<String, Object>> gainStatus(HttpServletRequest req, HttpServletResponse res) {
		return FileStatus.toList();
	}
	
	@ResponseBody
	@RequestMapping("/report/getStage")
	public List<Map<String, Object>> gainStage(HttpServletRequest req, HttpServletResponse res) {
		return Stage.toList();
	}
	
	@ResponseBody
	@RequestMapping("/report/getNorFiles")
	public Map<String, Object> gainNorFiles(HttpServletRequest req, HttpServletResponse res, String idList, 
			String nameList, String condList, String valueList, String labelList, PageParam page) {
		Page<NormativeFileVo> filePages = null;
		Map<String, Object> result = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(valueList) && !StringUtils.isEmpty(condList) && 
				!StringUtils.isEmpty(nameList)) {
			String[] nameArr = nameList.split(SysUtil.COMMA);
			String[] condArr = condList.split(SysUtil.COMMA);
			String[] valueArr = valueList.split(SysUtil.COMMA);
			List<String> nameL = Arrays.asList(nameArr);
			List<String> condL = Arrays.asList(condArr);
			List<String> valueL = Arrays.asList(valueArr);
			filePages = reportService.findNorFiles(nameL, condL, valueL, page.getPage(), page.getRows());
			
			result.put("total", filePages.getTotalElements());
			List <NormativeFileVo> norList = filePages.getContent();
			norFileService.genNameList(norList);
			result.put("rows", norList);
			return result;
		} else {
			filePages = reportService.findAllPage(page.getPage(), page.getRows());
			result.put("total", filePages.getTotalElements());
			List <NormativeFileVo> norList = filePages.getContent();
			norFileService.genNameList(norList);
			result.put("rows", norList);
			return result;
		}
		
	}
	
	@ResponseBody
	@RequestMapping("/report/download")
	public void download(HttpServletRequest req, HttpServletResponse response, String idList, String nameList, 
			String condList, String valueList, String labelList, PageParam page) {
		ServletOutputStream out = null;
		List<NormativeFileVo> norFiles = null;
		try
        {
	        response.reset();
	        response.setContentType("APPLICATION/OCTET-STREAM");
	        String downloadName = "规范性文件查询.xls";
	        downloadName = response.encodeURL(new String(downloadName.getBytes(),"ISO8859_1"));
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadName + "\"");
	       
	        out = response.getOutputStream();
	        String[] nameArr = nameList.split(SysUtil.COMMA);
			String[] condArr = condList.split(SysUtil.COMMA);
			String[] valueArr = valueList.split(SysUtil.COMMA);
			String[] labelArr = labelList.split(SysUtil.COMMA);
			String[] idArr = idList.split(SysUtil.COMMA);
			List<String> labelL = Arrays.asList(labelArr);
			List<String> idL = Arrays.asList(idArr);
			if (!StringUtils.isEmpty(valueList) && !StringUtils.isEmpty(condList) && 
					!StringUtils.isEmpty(nameList)) {
				List<String> nameL = Arrays.asList(nameArr);
				List<String> condL = Arrays.asList(condArr);
				List<String> valueL = Arrays.asList(valueArr);
				norFiles = reportService.findAllNorFiles(nameL, condL, valueL);
			} else {
				norFiles = reportService.findAll();
			}
			//直接把生成workbook加入到response的输出流中
	        HSSFWorkbook wb = reportService.exportNorFiles(idL, labelL, norFiles);
	        wb.write(out);
	        response.setStatus(HttpServletResponse.SC_OK);
		    response.flushBuffer();	//清除缓存
	        out.close();
        } catch (Exception e) {
        	logger.error("下载出错!", e);
        } finally {
        	try {
				out.close();
			} catch (IOException e) {
				logger.error("下载出错!", e);
			}
        }
	}
	
	@ResponseBody
	@RequestMapping("/report/getRecReviews")
	public Map<String, Object> gainRecReviews(HttpServletRequest req, HttpServletResponse res, String nameList, 
			String condList, String valueList, Date begDate, Date endDate, PageParam page) {
		Page<RecordReviewVo> filePages = null;
		Map<String, Object> result = new HashMap<String, Object>();
		if ((!StringUtils.isEmpty(nameList) && !StringUtils.isEmpty(valueList)) || 
				(begDate != null && endDate != null)) {
			 String[] nameArr = nameList.split(SysUtil.COMMA);
			 String[] condArr = condList.split(SysUtil.COMMA);
			 String[] valueArr = valueList.split(SysUtil.COMMA);
			 List<String> nameL = Arrays.asList(nameArr);
			 List<String> condL = Arrays.asList(condArr);
			 List<String> valueL = Arrays.asList(valueArr);
			try {
				filePages = reportService.findRecReviews(nameL, condL, valueL, begDate, endDate, page.getPage(), page.getRows());
			} catch (ServiceException e) {
				return null;
			}
			
			result.put("total", filePages.getTotalElements());
			result.put("rows", filePages.getContent());
			return result;
		} else {
			filePages = reportService.findAllRecRev(page.getPage(), page.getRows());
			result.put("total", filePages.getTotalElements());
			result.put("rows", filePages.getContent());
			return result;
		}
		
	}
	
	
	@ResponseBody
	@RequestMapping("/report/recRevDownload")
	public void recRevDownload(HttpServletRequest req, HttpServletResponse response, String nameList, 
			String condList, String valueList, Date begDate, Date endDate, PageParam page) {
		Page<RecordReviewVo> filePages = null;
		ServletOutputStream out = null;
		try
        {
	        response.reset();
	        response.setContentType("APPLICATION/OCTET-STREAM");
	        String downloadName = "备案台账.xls";
	        downloadName = response.encodeURL(new String(downloadName.getBytes(),"ISO8859_1"));
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadName + "\"");
	       
	        out = response.getOutputStream();
	        String[] nameArr = nameList.split(SysUtil.COMMA);
			String[] condArr = condList.split(SysUtil.COMMA);
			String[] valueArr = valueList.split(SysUtil.COMMA);
			if (!StringUtils.isEmpty(valueList) && !StringUtils.isEmpty(condList) && 
					!StringUtils.isEmpty(nameList)) {
				List<String> nameL = Arrays.asList(nameArr);
				List<String> condL = Arrays.asList(condArr);
				List<String> valueL = Arrays.asList(valueArr);
				filePages = reportService.findRecReviews(nameL, condL, valueL, begDate, endDate, page.getPage(), page.getRows());
			} else if (begDate != null && endDate != null) {
				filePages = reportService.findRecReviews(null, null, null, begDate, endDate, page.getPage(), page.getRows());
			} else {
				filePages = reportService.findAllRecRev(page.getPage(), page.getRows());
			}
			//直接把生成workbook加入到response的输出流中
	        HSSFWorkbook wb = reportService.exportRecRevs(filePages);
	        wb.write(out);
	        response.setStatus(HttpServletResponse.SC_OK);
		    response.flushBuffer();	//清除缓存
	        out.close();
        }
        catch (Exception e) {
        	logger.error("下载出错!", e);
        } finally {
        	try {
				out.close();
			} catch (IOException e) {
				logger.error("下载出错!", e);
			}
        }
		
	}
	
	
	@ResponseBody
	@RequestMapping("/report/gainNorFileNums")
	public String gainNorFileNums(Integer year) {
		StringBuffer strBuffer = new StringBuffer("[");
		String str = "";
		try {
			//市(州)政府
			str = reportService.searchNorFileNum(year, OrgType.CITY_GOV);
			strBuffer.append(str);
			strBuffer.append(SysUtil.COMMA);
			
			//市(州)政府部门
			str = reportService.searchNorFileNum(year, OrgType.CITY_WORK_DEPART);
			strBuffer.append(str);
			strBuffer.append(SysUtil.COMMA);
			
			//县(市、区)政府
			str = reportService.searchNorFileNum(year, OrgType.COUNTY_GOV);
			strBuffer.append(str);
			strBuffer.append(SysUtil.COMMA);
			
			//县(市、区)政府部门
			str = reportService.searchNorFileNum(year, OrgType.COUNTY_WORK_DEPART);
			strBuffer.append(str);
			strBuffer.append(SysUtil.COMMA);
			
			//乡(镇、街道办)政府
			str = reportService.searchNorFileNum(year, OrgType.COUNTRY_GOV);
			strBuffer.append(str);
			
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		strBuffer.append("]");
		//统计合计
		String rStr = strBuffer.toString();
		rStr = amount(rStr);
		return rStr;
	}
	
	
	@ResponseBody
	@RequestMapping("/report/downloadNorFileCount")
	public void downloadNorFileCount(HttpServletRequest req, HttpServletResponse response, Integer year) {
		ServletOutputStream out = null;
		try
        {
	        response.reset();
	        response.setContentType("APPLICATION/OCTET-STREAM");
	        String downloadName = "统计报表.xls";
	        downloadName = response.encodeURL(new String(downloadName.getBytes(),"ISO8859_1"));
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadName + "\"");
	       
	        out = response.getOutputStream();
	        String jsonData = gainNorFileNums(year);
			//直接把生成workbook加入到response的输出流中
	        HSSFWorkbook wb = reportService.exportNorFileCount(String.valueOf(year), jsonData);;
	        wb.write(out);
	        response.setStatus(HttpServletResponse.SC_OK);
		    response.flushBuffer();	//清除缓存
	        out.close();
        }
        catch (Exception e) {
        	logger.error("下载出错!", e);
        } finally {
        	try {
				out.close();
			} catch (IOException e) {
				logger.error("下载出错!", e);
			}
        }
	}
	
	/**
	 * 计算合计
	 * @param jsonString
	 * @return
	 */
	private String amount(String jsonString) {
		JSONArray jArr = new JSONArray(jsonString);
		int allAmount = 0;
		int publishAmount = 0;
		int reviewAmount = 0;
		int recordAmount = 0;
		int deliberationAmount = 0;
		int decUnitOopAmount = 0;
		int decProcedureOopAmount = 0;
		int contentOopAmount = 0;
		int decTechHasDefectsAmount = 0;
		int othersAmount = 0;
		int selfAmount = 0;
		int revokeAmount = 0;
		int qualifiedAmount = 0;
		for(int i=0 ; i < jArr.length() ;i++) {
		    JSONObject myjObject = jArr.getJSONObject(i);
		    allAmount += myjObject.getInt("all");
		    publishAmount += myjObject.getInt("publish");
			reviewAmount += myjObject.getInt("review");
			recordAmount += myjObject.getInt("record");
			deliberationAmount += myjObject.getInt("deliberation");
			decUnitOopAmount += myjObject.getInt("decUnitOop");
			decProcedureOopAmount += myjObject.getInt("decProcedureOop");
			contentOopAmount += myjObject.getInt("contentOop");
			decTechHasDefectsAmount += myjObject.getInt("decTechHasDefects");
			othersAmount += myjObject.getInt("others");
			selfAmount += myjObject.getInt("self");
			revokeAmount += myjObject.getInt("revoke");
			qualifiedAmount += myjObject.getInt("qualified");
	    }
		
		JSONObject jsonO = new JSONObject();
		jsonO.put("all", allAmount);
		jsonO.put("publish", publishAmount);
		jsonO.put("review", reviewAmount);
		jsonO.put("record", recordAmount);
		jsonO.put("deliberation", deliberationAmount);
		jsonO.put("decUnitOop", decUnitOopAmount);
		jsonO.put("decProcedureOop", decProcedureOopAmount);
		jsonO.put("contentOop", contentOopAmount);
		jsonO.put("decTechHasDefects", decTechHasDefectsAmount);
		jsonO.put("others", othersAmount);
		jsonO.put("self", selfAmount);
		jsonO.put("revoke", revokeAmount);
		jsonO.put("qualified", qualifiedAmount);
		
		jsonString = jsonString.substring(0, jsonString.length() - 1);
		jsonString += SysUtil.COMMA;
		jsonString += jsonO.toString();
		jsonString += "]";
		
		return jsonString;
	}
	
}
