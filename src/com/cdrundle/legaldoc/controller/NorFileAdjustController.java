package com.cdrundle.legaldoc.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdrundle.legaldoc.base.BaseController;
import com.cdrundle.legaldoc.enums.FileStatus;
import com.cdrundle.legaldoc.enums.FileStatusEnumEditor;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.INorFileAdjustService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.service.IOrganizationService;
import com.cdrundle.legaldoc.service.IUserService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.NorFileAdjustVo;
import com.cdrundle.legaldoc.vo.NorFileQueryResultVo;
import com.cdrundle.legaldoc.vo.NorFileQueryVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.PageParam;

@Controller
public class NorFileAdjustController extends BaseController{
	@Autowired
	private INorFileAdjustService norFileAdjustService;

	@Autowired
	private INormativeFileService normativeFileService;

	@Autowired
	private IOrganizationService organizationService;

	@Autowired
	private IUserService userService;

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws IOException {
		binder.registerCustomEditor(FileStatus.class, new FileStatusEnumEditor());

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping("/norFileAdjust/getStatus")
	@ResponseBody
	public List<Map<String, Object>> gainStatus(HttpServletRequest req, HttpServletResponse res) {
		return FileStatus.toList();
	}

	@ResponseBody
	@RequestMapping("/norFileAdjust/load")
	public NormativeFileVo load(Long norId) {
		if (norId != null) {
			NormativeFileVo norFile = normativeFileService.findById(norId);

			return norFile;
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping("/norFileAdjust/batchSave")
	public String batchSave(@RequestBody List<Map<String, Object>> vos) {
		if (vos != null) {
			List<NorFileAdjustVo> voList = new ArrayList<NorFileAdjustVo>();
			Iterator itr = vos.iterator();
			while (itr.hasNext()) {
				Map<String, Object> nextObj = (Map<String, Object>) itr.next();
				NorFileAdjustVo vo = new NorFileAdjustVo();
				vo.setId(Long.valueOf(nextObj.get("id").toString()));
				vo.setStatus(FileStatus.getByName((String) nextObj.get("status")));
				vo.setInvalidReason((String) nextObj.get("invalidReason"));

				voList.add(vo);
			}

			norFileAdjustService.saveOrUpdate(voList);
			return "success";
		}
		return null;

	}

	@ResponseBody
	@RequestMapping("/norFileAdjust/save")
	public String save(NorFileAdjustVo vo) {
		if (vo != null) {
			List<NorFileAdjustVo> voList = new ArrayList<NorFileAdjustVo>();
			voList.add(vo);

			norFileAdjustService.saveOrUpdate(voList);
			return "success";
		}
		return null;

	}

	@ResponseBody
	@RequestMapping("/norFileAdjust/query")
	public Map<String, Object> query(NorFileQueryVo queryVo, PageParam page) {
		Page<NorFileQueryResultVo> pages = norFileAdjustService.findNorFilesForAdjust(queryVo, page.getPage(), page.getRows());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", pages.getTotalElements());
		result.put("rows", pages.getContent());
		return result;

	}

	/**
	 * 读取相关文件的内容(包括页面浏览、文档打印的读取文件内容)
	 * 
	 * @param request
	 * @param response
	 * @param norId
	 * @return
	 * @throws ServiceException 
	 */
	@ResponseBody
	@RequestMapping("/norFileAdjust/gainFileContent")
	public String gainFileContent(HttpServletRequest request, HttpServletResponse response, String norId, String fileType, String fileName) throws ServiceException {
		NormativeFileVo normativeFileVo = normativeFileService.findById(Long.valueOf(norId));
		String val = null;
		if (null != normativeFileVo) {
			String projectPath = getProjectPath(request);
			String filePath = "";
			if (SysUtil.FILE_TYPE_LEGALDOC.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.PUBLISH.toString());
			} else if (SysUtil.FILE_TYPE_INSTRUCTION.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.LEGAL_REVIEW.toString());
			} else if (SysUtil.FILE_TYPE_LEGALBASIS.equals(fileType)) {
				filePath = WordUtils.getFilePath(projectPath, normativeFileVo, Stage.SETUP.toString());
			}

			if (!StringUtils.isEmpty(fileName)) {
				if (SysUtil.FILE_TYPE_LEGALBASIS.equals(fileType)) {
					val = WordUtils.readWord(filePath + File.separator + fileName);
				} else {
					val = WordUtils.readFile(filePath + File.separator + fileName); // 获取文档内容
				}
			}
		}
		return val;
	}
}
