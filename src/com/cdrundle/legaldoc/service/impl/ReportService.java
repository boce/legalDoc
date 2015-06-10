package com.cdrundle.legaldoc.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IRecordReviewDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.RecordReview;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.FileStatus;
import com.cdrundle.legaldoc.enums.OrgType;
import com.cdrundle.legaldoc.enums.ReviewResult;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IReportService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.RecordReviewVo;
import com.cdrundle.legaldoc.vo.UserShortVo;

@Service
public class ReportService implements IReportService {

	@Autowired 
	private INormativeFileDao norDao;
	
	@Autowired
	private IOrganizationDao orgDao;
	
	@Autowired
	private IUserDao userDao;

	@Autowired
	private IRecordReviewDao recDao;
	
	@PersistenceContext
	EntityManager em;
	
	@Override
	@Transactional(readOnly = true)
	public Page<NormativeFileVo> findNorFiles(List<String> nameList, List<String> condList, 
			List<String> valueList, Integer page, Integer size){
		Pageable pageable = new PageRequest(page, size);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<NormativeFile> cq = cb.createQuery(NormativeFile.class);
		Root<NormativeFile> root = cq.from(NormativeFile.class);
		cq.select(root);
		Predicate where = cb.conjunction();
		where = buildCondition(where, root, nameList, condList, valueList, cb);
		cq.where(where);
		TypedQuery<NormativeFile> query = em.createQuery(cq);
		query.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber())).setMaxResults(pageable.getPageSize());
		List<NormativeFile> rows = query.getResultList();

		int totalCount = getTotalCount(nameList, condList, valueList);

		List<NormativeFileVo> volist = NormativeFileVo.createVoList(rows);
		Page<NormativeFileVo> pages = new PageImpl<NormativeFileVo>(volist, pageable, totalCount);
		return pages;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<NormativeFileVo> findAllNorFiles(List<String> nameList, List<String> condList, 
			List<String> valueList){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<NormativeFile> cq = cb.createQuery(NormativeFile.class);
		Root<NormativeFile> root = cq.from(NormativeFile.class);
		cq.select(root);
		Predicate where = cb.conjunction();
		where = buildCondition(where, root, nameList, condList, valueList, cb);
		cq.where(where);
		TypedQuery<NormativeFile> query = em.createQuery(cq);
		List<NormativeFile> rows = query.getResultList();
		List<NormativeFileVo> volist = NormativeFileVo.createVoList(rows);
		return volist;
	}
	
	@Override
	public HSSFWorkbook exportRecRevs(Page<RecordReviewVo> filePages)  throws ServiceException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("规范性文件");
		HSSFRow row = sheet.createRow((int) 0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		String[] names = {"发文号","文件名称","制定单位","起草 单位","发布日期","备案日期","备案人","备案机关"};
		HSSFCell cell = null;
		for (int index = 0; index < names.length; index++) {	//生成title
			cell = row.createCell(index);
			cell.setCellValue(names[index]);
			cell.setCellStyle(style);
			if (names[index].equals("文件名称")) {
				sheet.setColumnWidth(index, 40*256);	//设置宽度
			} else {
				sheet.setColumnWidth(index, 20*256);	
			}
		}
		
		List <RecordReviewVo> recList = filePages.getContent();
		for (int i = 0; i< recList.size(); i++) {
			RecordReviewVo vo = recList.get(i);
			row = sheet.createRow((int) i + 1);
			cell = row.createCell(0);	//设置文件号的值
			String str = vo.getNormativeFile().getPublishNo();
			if (str != null) {
				cell.setCellValue(str);
			} else {
				cell.setCellValue("");
			}
			cell.setCellStyle(style);
			
			cell = row.createCell(1);	//设置文件名称的值
			String strName = vo.getName();
			if (strName != null) {
				cell.setCellValue(strName);
			} else {
				cell.setCellValue("");
			}
			cell.setCellStyle(style);
			
			cell = row.createCell(2);	//设置制定单位
			OrgShortVo org = vo.getNormativeFile().getDecUnit();
			if (org != null) {
				cell.setCellValue(org.getText());
			} else {
				cell.setCellValue("");
			}
			cell.setCellStyle(style);
			
			cell = row.createCell(3);	//设置起草单位
			OrgShortVo orgDrt = vo.getNormativeFile().getDrtUnit();
			if (orgDrt != null) {
				cell.setCellValue(orgDrt.getText());
			} else {
				cell.setCellValue("");
			}
			cell.setCellStyle(style);
			
			cell = row.createCell(4);	//设置发布日期
			Date pDate = vo.getNormativeFile().getPublishDate();
			if (pDate != null) {
				cell.setCellValue(pDate.toString());
			} else {
				cell.setCellValue("");
			}
			cell.setCellStyle(style);
			
			cell = row.createCell(5);	//设置报备日期
			Date rDate = vo.getRegisterDate();
			if (rDate != null) {
				cell.setCellValue(rDate.toString());
			} else {
				cell.setCellValue("");
			}
			cell.setCellStyle(style);
			
			cell = row.createCell(6);	//设置报备人
			UserShortVo userVo = vo.getRecordRevUnitClerk();
			if (userVo != null) {
				cell.setCellValue(userVo.getName());
			} else {
				cell.setCellValue("");
			}
			cell.setCellStyle(style);
			
			cell = row.createCell(7);	//设置报备机关
			OrgShortVo recRevOrg = vo.getRecordRevUnit();
			if (recRevOrg != null) {
				cell.setCellValue(recRevOrg.getText());
			} else {
				cell.setCellValue("");
			}
			cell.setCellStyle(style);
			
		}
		
		return wb;
	}
	
	@Override
	public HSSFWorkbook exportNorFiles(List<String> nameList, List<String> labelList, 
			List<NormativeFileVo> norList) throws ServiceException{
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("规范性文件");
		HSSFRow row = sheet.createRow((int) 0);
		HSSFCellStyle titleStyle = wb.createCellStyle();
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建标题行格式
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short)11);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		titleStyle.setFont(font);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		
		HSSFCell cell = null;
		for (int index = 0; index < labelList.size(); index++) {	//生成title
			cell = row.createCell(index);
			cell.setCellValue(labelList.get(index));
			cell.setCellStyle(titleStyle);
			if (nameList.get(index).equals("name")) {
				sheet.setColumnWidth(index, 40*256);	//设置宽度
			} else {
				sheet.setColumnWidth(index, 20*256);	//设置宽度
			}
		}
		
		SimpleDateFormat sf = new SimpleDateFormat(SysUtil.DATE_FORMAT_NORMAL);
		Date date = null;
		String str = "";
		OrgShortVo org = null;
		UserShortVo user = null;
		for (int i = 0; i< norList.size(); i++) {
			NormativeFileVo vo = norList.get(i);
			row = sheet.createRow((int) i + 1);
			for(int index = 0; index < labelList.size(); index++) {
				if (nameList.get(index).equals("name")) {
					cell = row.createCell(index);
					str = vo.getName();
					if (!StringUtils.isEmpty(str)) {
						cell.setCellValue(str);
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("unionDrtUnit")) {
					cell = row.createCell(index);
					str = vo.getUnionDrtUnitName();	//处理id获取name
					String [] strArr = str.split(SysUtil.COMMA);
					String strFlag = "";
					str = "";
					if (strArr != null) { 
						for (String s : strArr) {
							s = s.replaceAll("\"", "");
							Organization orgVo = orgDao.findOne(Long.valueOf(s));
							if (orgVo != null) {
								str += strFlag;
								str += orgVo.getName();
								strFlag = SysUtil.COMMA;
							}
						} 
					}
					if (!StringUtils.isEmpty(str)) {
						cell.setCellValue(str);
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("unionDrtUnitLeader")) {
					cell = row.createCell(index);
					str = vo.getUnionDrtUnitLeaderName();	//处理id获取name
					String [] strArr = str.split(SysUtil.COMMA);
					String strFlag = "";
					str = "";
					if (strArr != null) { 
						for (String s : strArr) {
							s = s.replaceAll("\"", "");
							User u = userDao.findOne(Long.valueOf(s));
							if (u != null) {
								str += strFlag;
								str += u.getName();
								strFlag = SysUtil.COMMA;
							}
						} 
					}
					if (!StringUtils.isEmpty(str)) {
						cell.setCellValue(str);
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("unionDrtUnitClerk")) {
					cell = row.createCell(index);
					str = vo.getUnionDrtUnitClerk();	//处理id获取name
					String [] strArr = str.split(SysUtil.COMMA);
					String strFlag = "";
					str = "";
					if (strArr != null) { 
						for (String s : strArr) {
							s = s.replaceAll("\"", "");
							User u = userDao.findOne(Long.valueOf(s));
							if (u != null) {
								str += strFlag;
								str += u.getName();
								strFlag = SysUtil.COMMA;
							}
						} 
					}
					if (!StringUtils.isEmpty(str)) {
						cell.setCellValue(str);
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("involvedOrges")) {
					cell = row.createCell(index);
					str = vo.getInvolvedOrgesName();	//处理id获取name
					String [] strArr = str.split(SysUtil.COMMA);
					String strFlag = "";
					str = "";
					if (strArr != null) { 
						for (String s : strArr) {
							s = s.replaceAll("\"", "");
							Organization orgVo = orgDao.findOne(Long.valueOf(s));
							if (orgVo != null) {
								str += strFlag;
								str += orgVo.getName();
								strFlag = SysUtil.COMMA;
							}
						} 
					}
					if (!StringUtils.isEmpty(str)) {
						cell.setCellValue(str);
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("registerCode")) {
					cell = row.createCell(index);
					str = vo.getRegisterCode();
					if (!StringUtils.isEmpty(str)) {
						cell.setCellValue(str);
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("docNo")) {
					cell = row.createCell(index);
					str = vo.getDocNo();
					if (!StringUtils.isEmpty(str)) {
						cell.setCellValue(str);
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("publishNo")) {
					cell = row.createCell(index);
					str = vo.getPublishNo();
					if (!StringUtils.isEmpty(str)) {
						cell.setCellValue(str);
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("legalDoc")) {
					cell = row.createCell(index);
					str = vo.getLegalDoc();
					if (!StringUtils.isEmpty(str)) {
						cell.setCellValue(str);
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("draftInstruction")) {
					cell = row.createCell(index);
					str = vo.getDraftInstruction();
					if (!StringUtils.isEmpty(str)) {
						cell.setCellValue(str);
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("legalBasis")) {
					cell = row.createCell(index);
					str = vo.getLegalBasis();
					if (!StringUtils.isEmpty(str)) {
						cell.setCellValue(str.replaceAll(".docx|.doc", ""));
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("delUnit")) {
					cell = row.createCell(index);
					str = vo.getDelUnit();
					if (!StringUtils.isEmpty(str)) {
						cell.setCellValue(str);
					} else {
						cell.setCellValue("");
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("applyDate")) {
					cell = row.createCell(index);
					date = vo.getApplyDate();
					if (date == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(sf.format(date));
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("draftDate")) {
					cell = row.createCell(index);
					date = vo.getDraftDate();
					if (date == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(sf.format(date));
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("requestDate")) {
					cell = row.createCell(index);
					date = vo.getRequestDate();
					if (date == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(sf.format(date));
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("delDate")) {
					cell = row.createCell(index);
					date = vo.getDelDate();
					if (date == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(sf.format(date));
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("publishDate")) {
					cell = row.createCell(index);
					date = vo.getPublishDate();
					if (date == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(sf.format(date));
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("reviewDate")) {
					cell = row.createCell(index);
					date = vo.getReviewDate();
					if (date == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(sf.format(date));
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("registerDate")) {
					cell = row.createCell(index);
					date = vo.getRegisterDate();
					if (date == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(sf.format(date));
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("invalidDate")) {
					cell = row.createCell(index);
					date = vo.getInvalidDate();
					if (date == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(sf.format(date));
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("basisInvalidDate")) {
					cell = row.createCell(index);
					date = vo.getBasisInvalidDate();
					if (date == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(sf.format(date));
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("applyUnit")) {
					cell = row.createCell(index);
					org = vo.getApplyUnit();
					if (org == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(org.getText());
					}
					cell.setCellStyle(style);
					continue;
				}  else if(nameList.get(index).equals("drtUnit")) {
					cell = row.createCell(index);
					org = vo.getDrtUnit();
					if (org == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(org.getText());
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("revUnit")) {
					cell = row.createCell(index);
					org = vo.getRevUnit();
					if (org == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(org.getText());
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("decUnit")) {
					cell = row.createCell(index);
					org = vo.getDecUnit();
					if (org == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(org.getText());
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("recRevUnit")) {
					cell = row.createCell(index);
					org = vo.getRecRevUnit();
					if (org == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(org.getText());
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("decUnitLeader")) {
					cell = row.createCell(index);
					user = vo.getDecUnitLeader();
					if (user == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(user.getName());
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("decUnitClerk")) {
					cell = row.createCell(index);
					user = vo.getDecUnitClerk();
					if (user == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(user.getName());
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("drtUnitLeader")) {
					cell = row.createCell(index);
					user = vo.getDrtUnitLeader();
					if (user == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(user.getName());
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("drtUnitClerk")) {
					cell = row.createCell(index);
					user = vo.getDrtUnitClerk();
					if (user == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(user.getName());
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("revUnitLeader")) {
					cell = row.createCell(index);
					user = vo.getRevUnitLeader();
					if (user == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(user.getName());
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("revUnitClerk")) {
					cell = row.createCell(index);
					user = vo.getRevUnitClerk();
					if (user == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(user.getName());
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("recRevUnitLeader")) {
					cell = row.createCell(index);
					user = vo.getRecRevUnitLeader();
					if (user == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(user.getName());
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("validDate")) {
					cell = row.createCell(index);
					Integer indate = vo.getValidDate();
					if (indate == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(indate.intValue());
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("status")) {
					cell = row.createCell(index);
					FileStatus s = vo.getStatus();
					if (s == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(vo.getStatus().toString());
					}
					cell.setCellStyle(style);
					continue;
				} else if(nameList.get(index).equals("stage")) {
					cell = row.createCell(index);
					cell.setCellValue(vo.getStage().toString());
					Stage s = vo.getStage();
					if (s == null) {
						cell.setCellValue("");
					} else {
						cell.setCellValue(vo.getStage().toString());
					}
					cell.setCellStyle(style);
					continue;
				}
				
					
			}
		}
//		try {
//			//FileOutputStream fout = new FileOutputStream("E:/students.xls");
//			//wb.write(fout);
//			//fout.close();
//		}
//		catch (Exception e)
//		{
//			throw new ServiceException("生成execl文件时报错！");
//		}
		return wb;
	}
	
	/**
	 * 获取总数
	 * @param nameList
	 * @param condList
	 * @param valueList
	 * @return
	 */
	private int getTotalCount(List<String> nameList, List<String> condList, 
			List<String> valueList) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<NormativeFile> emp = cq.from(NormativeFile.class);
		cq.select(cb.countDistinct(emp));
		Predicate where = cb.conjunction();
		where = buildCondition(where, emp, nameList, condList, valueList, cb);
		cq.where(where);

		return em.createQuery(cq).getSingleResult().intValue();
	}
	
	/**
	 * 创建条件
	 * @param where
	 * @param root
	 * @param nameList
	 * @param condList
	 * @param valueList
	 * @param cb
	 * @return
	 */
	private Predicate buildCondition(Predicate where, Root<NormativeFile> root, List<String> nameList, List<String> condList, 
			List<String> valueList, CriteriaBuilder cb) {
		EntityType<NormativeFile> et = root.getModel();
		for (int index = 0; index < nameList.size(); index++) {
			String nameStr = nameList.get(index);
			String value = valueList.get(index);
			String condStr = condList.get(index);
			if (value != null && (nameStr.equals("name") || nameStr.equals("unionDrtUnit") || nameStr.equals("unionDrtUnitLeader") || 
					nameStr.equals("unionDrtUnitClerk") || nameStr.equals("involvedOrges") || nameStr.equals("registerCode") || 
					nameStr.equals("docNo") || nameStr.equals("publishNo") || nameStr.equals("legalDoc") || 
					nameStr.equals("draftInstruction") || nameStr.equals("legalBasis") || nameStr.equals("delUnit"))) {
				if (condStr.equals("et")) {
					where = cb.and(where, cb.equal(root.get(et.getSingularAttribute(nameStr, String.class)), value));
				} else if (condStr.equals("ct")) {
					where = cb.and(where, cb.like(root.get(et.getSingularAttribute(nameStr, String.class)), "%" + value + "%"));
				}
			} else if (!StringUtils.isEmpty(value) && (nameStr.equals("applyDate") || nameStr.equals("draftDate") || 
					nameStr.equals("requestDate") || nameStr.equals("delDate") || nameStr.equals("publishDate") || 
					nameStr.equals("reviewDate") || nameStr.equals("registerDate") || nameStr.equals("invalidDate") || 
					nameStr.equals("basisInvalidDate"))) {
				Date date = new Date(value);
				if (condStr.equals("et")) {
					where = cb.and(where, cb.equal(root.get(et.getSingularAttribute(nameStr, Date.class)), date));
				} else if (condStr.equals("lt")) {
					where = cb.and(where, cb.lessThan(root.get(et.getSingularAttribute(nameStr, Date.class)), date));
				} else if (condStr.equals("gt")) {
					where = cb.and(where, cb.greaterThan(root.get(et.getSingularAttribute(nameStr, Date.class)), date));
				}
			} else if (!StringUtils.isEmpty(value) && (nameStr.equals("applyUnit") || nameStr.equals("decUnit") || 
					nameStr.equals("drtUnit") || nameStr.equals("revUnit") || nameStr.equals("recRevUnit"))) {
				Organization org = orgDao.findOne(Long.valueOf(value));
				if (org != null) {
					if (condStr.equals("et")) {
						where = cb.and(where, cb.equal(root.get(et.getSingularAttribute(nameStr, Organization.class)), org));
					}
				}
			} else if (!StringUtils.isEmpty(value) && (nameStr.equals("decUnitLeader") || nameStr.equals("decUnitClerk") || 
					nameStr.equals("drtUnitLeader") || nameStr.equals("drtUnitClerk") || nameStr.equals("revUnitLeader") || 
					nameStr.equals("revUnitClerk") || nameStr.equals("recRevUnitLeader"))) {
				User user = userDao.findOne(Long.valueOf(value));
				if (user != null) {
					if (condStr.equals("et")) {
						where = cb.and(where, cb.equal(root.get(et.getSingularAttribute(nameStr, User.class)), user));
					}
				}
			} else if (!StringUtils.isEmpty(value) && nameStr.equals("status")) {
				FileStatus fileStatus = FileStatus.getByName(value);
				if (fileStatus != null) {
					if (condStr.equals("et")) {
						where = cb.and(where, cb.equal(root.get(et.getSingularAttribute(nameStr, FileStatus.class)), fileStatus));
					}
				}
			} else if (!StringUtils.isEmpty(value) && nameStr.equals("stage")) {
				Stage stage = Stage.getByName(value);
				if (stage != null) {
					if (condStr.equals("et")) {
						where = cb.and(where, cb.equal(root.get(et.getSingularAttribute(nameStr, Stage.class)), stage));
					}
				}
			} else if (!StringUtils.isEmpty(value) && nameStr.equals("validDate")) {
				Long vaild = Long.valueOf(value);
				if (vaild != null) {
					if (condStr.equals("et")) {
						where = cb.and(where, cb.equal(root.get(et.getSingularAttribute(nameStr, Long.class)), vaild));
					} else if (condStr.equals("lt")) {
						where = cb.and(where, cb.lessThan(root.get(et.getSingularAttribute(nameStr, Long.class)), vaild));
					} else if (condStr.equals("gt")) {
						where = cb.and(where, cb.greaterThan(root.get(et.getSingularAttribute(nameStr, Long.class)), vaild));
					}
				}
			}
			
		}
		return where;
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public Page<NormativeFileVo> findAllPage(Integer page, Integer size) {
		Pageable pageable = new PageRequest(page, size);
		Page<NormativeFile> pages = norDao.findAll(pageable);
		List<NormativeFileVo> volist = NormativeFileVo.createVoList(pages.getContent());
		Page<NormativeFileVo> pageVo = new PageImpl<NormativeFileVo>(volist,
				pageable, pages.getTotalElements());
		return pageVo;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<NormativeFileVo> findAll() {
		List<NormativeFile> norFiles = norDao.findAll();
		List<NormativeFileVo> volist = NormativeFileVo.createVoList(norFiles);
		return volist;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<RecordReviewVo> findAllRecRev(Integer page, Integer size) {
		Pageable pageable = new PageRequest(page, size);
		Page<RecordReview> pages = recDao.findAll(pageable);
		List<RecordReviewVo> volist = RecordReviewVo.createVoList(pages.getContent());
		Page<RecordReviewVo> pageVo = new PageImpl<RecordReviewVo>(volist,
				pageable, pages.getTotalElements());
		return pageVo;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<RecordReviewVo> findRecReviews(List<String> nameList, List<String> condList, 
			List<String> valueList, Date begDate, Date endDate, Integer page, Integer size)  throws ServiceException {
		Pageable pageable = new PageRequest(page, size);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RecordReview> cq = cb.createQuery(RecordReview.class);
		Root<RecordReview> root = cq.from(RecordReview.class);
		cq.select(root);
		Predicate where = cb.conjunction();
		where = buildRecRevCondition(where, root, nameList, condList, valueList,begDate, endDate, cb);
		cq.where(where);
		TypedQuery<RecordReview> query = em.createQuery(cq);
		query.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber())).setMaxResults(pageable.getPageSize());
		List<RecordReview> rows = query.getResultList();

		int totalCount = getRecRevTC(nameList, condList, valueList, begDate, endDate);

		List<RecordReviewVo> volist = RecordReviewVo.createVoList(rows);
		Page<RecordReviewVo> pages = new PageImpl<RecordReviewVo>(volist, pageable, totalCount);
		return pages;
	}
	
	/**
	 * 备案查询时获取总数
	 * @param nameList
	 * @param condList
	 * @param valueList
	 * @return
	 */
	private int getRecRevTC(List<String> nameList, List<String> condList, 
			List<String> valueList, Date begDate, Date endDate) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<RecordReview> emp = cq.from(RecordReview.class);
		cq.select(cb.countDistinct(emp));
		Predicate where = cb.conjunction();
		where = buildRecRevCondition(where, emp, nameList, condList, valueList,begDate, endDate, cb);
		cq.where(where);

		return em.createQuery(cq).getSingleResult().intValue();
	}
	
	/**
	 * 创建条件
	 * @param where
	 * @param root
	 * @param nameList
	 * @param condList
	 * @param valueList
	 * @param cb
	 * @return
	 */
	private Predicate buildRecRevCondition(Predicate where, Root<RecordReview> root,List<String> nameList, List<String> condList, 
			List<String> valueList, Date begDate, Date endDate, CriteriaBuilder cb) {
		EntityType<RecordReview> et = root.getModel();
		for (int index = 0; index < nameList.size(); index++) {
			String nameStr = nameList.get(index);
			String value = valueList.get(index);
			String condStr = condList.get(index);
			if (value != null && (nameStr.equals("name") || nameStr.equals("publishNo"))) {
				if (condStr.equals("et")) {
					where = cb.and(where, cb.equal(root.get(et.getSingularAttribute(nameStr, String.class)), value));
				} else if (condStr.equals("ct")) {
					where = cb.and(where, cb.like(root.get(et.getSingularAttribute(nameStr, String.class)), "%" + value + "%"));
				}
			} else if (!StringUtils.isEmpty(value) && (nameStr.equals("decUnit") || nameStr.equals("drtUnit"))) {
				Organization org = orgDao.findOne(Long.valueOf(value));
				if (org != null) {
					if (condStr.equals("et")) {
						if (!nameStr.equals("drtUnit")) {
							where = cb.and(where, cb.equal(root.get(et.getSingularAttribute("decisionMakingUnit", Organization.class)), org));
						} else {
							where = cb.and(where, cb.equal(root.get(et.getSingularAttribute("normativeFile.drtUnit", Organization.class)), org));
						}
					}
				}
			} 
			
		}
		if (begDate != null && endDate != null) {
			where = cb.and(where, cb.between(root.get(et.getSingularAttribute("recordDate", Date.class)), begDate, endDate));
		}
		return where;
	}
	
	/**
	 * 查询统计报表中以制定单位为分组的规范性文件统计
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public String searchNorFileNum(Integer year, OrgType orgType)  throws ServiceException {
		java.sql.Date   begDate=java.sql.Date.valueOf(year + "-1-1");
		java.sql.Date   endDate=java.sql.Date.valueOf(year + "-12-31");
		List<NormativeFile> result = null;
		Query query = null;
		JSONObject json = new JSONObject();
		
		//查询获取规范性文件制定主体数量
		query = em.createQuery("SELECT c FROM NormativeFile c  where ( c.applyDate between ? and ? ) and c.decUnit.orgType=?");  
		query.setParameter(1, begDate);
		query.setParameter(2, endDate);
		query.setParameter(3, orgType);
		result = query.getResultList();
		if (result != null && result.size() > 0) {
			json.put("all", result.size());
		} else {
			json.put("all", 0);
		}
		
		//查询获取制定发布规范性文件数量
		query = em.createQuery("SELECT c FROM NormativeFile c,SignAndPublish s where c.id=s.normativeFile.id and ( c.applyDate between ? and ? ) and c.decUnit.orgType=?");  
		query.setParameter(1, begDate);
		query.setParameter(2, endDate);
		query.setParameter(3, orgType);
//		query.setParameter(4, Stage.PUBLISH);
		result = query.getResultList();
		if (result != null && result.size() > 0) {
			json.put("publish", result.size());
		} else {
			json.put("publish", 0);
		}
		
		//查询获取法制机构进行合法性审查数量
		query = em.createQuery("SELECT c FROM NormativeFile c,ExaminationDraftReview e  where c.id=e.normativeFile.id and ( c.applyDate between ? and ? ) and c.decUnit.orgType=?");  
		query.setParameter(1, begDate);
		query.setParameter(2, endDate);
		query.setParameter(3, orgType);
//		query.setParameter(4, Stage.LEGAL_REVIEW);
		result = query.getResultList();
		if (result != null && result.size() > 0) {
			json.put("review", result.size());
		} else {
			json.put("review", 0);
		}
		
		//查询获取向备案机构报备文件数量
		query = em.createQuery("SELECT c FROM NormativeFile c,RecordRequest r  where c.id=r.normativeFile.id and ( c.applyDate between ? and ? ) and c.decUnit.orgType=?");  
		query.setParameter(1, begDate);
		query.setParameter(2, endDate);
		query.setParameter(3, orgType);
//		query.setParameter(4, Stage.RECORD);
		result = query.getResultList();
		if (result != null && result.size() > 0) {
			json.put("record", result.size());
		} else {
			json.put("record", 0);
		}
				
		//查询获取经备案审查发现问题的数量
		query = em.createQuery("SELECT c FROM NormativeFile c,RecordReview r  where c.id=r.normativeFile.id and ( c.applyDate between ? and ? ) and c.decUnit.orgType=? and("
								+ "r.decUnitOop=true or r.decProcedureOop=true or r.contentOop=true or r.decTechHasDefects=true or r.others=true)");  
		query.setParameter(1, begDate);
		query.setParameter(2, endDate);
		query.setParameter(3, orgType);
		result = query.getResultList();
		if (result != null && result.size() > 0) {
			json.put("deliberation", result.size());
		} else {
			json.put("deliberation", 0);
		}
				
		//查询获取制定不合法数量
		query = em.createQuery("SELECT c FROM NormativeFile c, RecordReview r where ( c.applyDate between ? and ? ) "
				+ "and c.id=r.normativeFile.id and c.decUnit.orgType=? and r.decUnitOop=?");  
		query.setParameter(1, begDate);
		query.setParameter(2, endDate);
		query.setParameter(3, orgType);
		query.setParameter(4, true);
		result = query.getResultList();
		if (result != null && result.size() > 0) {
			json.put("decUnitOop", result.size());
		} else {
			json.put("decUnitOop", 0);
		}
		
		//查询获取制定程序不合规数量
		query = em.createQuery("SELECT c FROM NormativeFile c, RecordReview r where ( c.applyDate between ? and ? ) "
				+ "and c.name=r.name and c.decUnit.orgType=? and r.decProcedureOop=?");  
		query.setParameter(1, begDate);
		query.setParameter(2, endDate);
		query.setParameter(3, orgType);
		query.setParameter(4, true);
		result = query.getResultList();
		if (result != null && result.size() > 0) {
			json.put("decProcedureOop", result.size());
		} else {
			json.put("decProcedureOop", 0);
		}
				
		//查询获取内容不合法数量
		query = em.createQuery("SELECT c FROM NormativeFile c, RecordReview r where ( c.applyDate between ? and ? ) "
				+ "and c.name=r.name and c.decUnit.orgType=? and r.contentOop=?");  
		query.setParameter(1, begDate);
		query.setParameter(2, endDate);
		query.setParameter(3, orgType);
		query.setParameter(4, true);
		result = query.getResultList();
		if (result != null && result.size() > 0) {
			json.put("contentOop", result.size());
		} else {
			json.put("contentOop", 0);
		}
				
		//查询获取制定有缺陷数量
		query = em.createQuery("SELECT c FROM NormativeFile c, RecordReview r where ( c.applyDate between ? and ? ) "
				+ "and c.name=r.name and c.decUnit.orgType=? and r.decTechHasDefects=?");  
		query.setParameter(1, begDate);
		query.setParameter(2, endDate);
		query.setParameter(3, orgType);
		query.setParameter(4, true);
		result = query.getResultList();
		if (result != null && result.size() > 0) {
			json.put("decTechHasDefects", result.size());
		} else {
			json.put("decTechHasDefects", 0);
		}
				
		//查询获取制定不合法其他数量
		query = em.createQuery("SELECT c FROM NormativeFile c, RecordReview r where ( c.applyDate between ? and ? ) "
				+ "and c.name=r.name and c.decUnit.orgType=? and r.others=?");  
		query.setParameter(1, begDate);
		query.setParameter(2, endDate);
		query.setParameter(3, orgType);
		query.setParameter(4, true);
		result = query.getResultList();
		if (result != null && result.size() > 0) {
			json.put("others", result.size());
		} else {
			json.put("others", 0);
		}
				
		//查询获取自行纠正的数量
		query = em.createQuery("SELECT c FROM NormativeFile c, RecordReview r where ( c.applyDate between ? and ? ) "
				+ "and c.name=r.name and c.decUnit.orgType=? and r.reviewResult=?");  
		query.setParameter(1, begDate);
		query.setParameter(2, endDate);
		query.setParameter(3, orgType);
		query.setParameter(4, ReviewResult.SELFCORRECTION);
		result = query.getResultList();
		if (result != null && result.size() > 0) {
			json.put("self", result.size());
		} else {
			json.put("self", 0);
		}
				
		//查询获取撤销的数量
		query = em.createQuery("SELECT c FROM NormativeFile c, RecordReview r where ( c.applyDate between ? and ? ) "
				+ "and c.name=r.name and c.decUnit.orgType=? and r.reviewResult=?");  
		query.setParameter(1, begDate);
		query.setParameter(2, endDate);
		query.setParameter(3, orgType);
		query.setParameter(4, ReviewResult.REVOKE);
		result = query.getResultList();
		if (result != null && result.size() > 0) {
			json.put("revoke", result.size());
		} else {
			json.put("revoke", 0);
		}
		//查询获取正确的数量
		query = em.createQuery("SELECT c FROM NormativeFile c, RecordReview r where ( c.applyDate between ? and ? ) "
				+ "and c.name=r.name and c.decUnit.orgType=? and r.reviewResult=?");  
		query.setParameter(1, begDate);
		query.setParameter(2, endDate);
		query.setParameter(3, orgType);
		query.setParameter(4, ReviewResult.QUALIFIED);
		result = query.getResultList();
		if (result != null && result.size() > 0) {
			json.put("qualified", result.size());
		} else {
			json.put("qualified", 0);
		}
			
		return json.toString();
	}
	
	
	@Override
	public HSSFWorkbook exportNorFileCount(String year, String jsonData)  throws ServiceException {
		JSONArray jArr = new JSONArray(jsonData);
		HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFSheet sheet = wb.createSheet("new sheet");
	    HSSFRow head = sheet.createRow(0);
	    HSSFRow head2 = sheet.createRow(2);
	    HSSFRow head3 = sheet.createRow(4);
	    HSSFRow headNum = sheet.createRow(5);
	    HSSFRow headFoot = sheet.createRow(18);
	    
	    // 第一行大标题
	    createCell(wb, head, 0, year + "年度规范性文件监督管理工作情况统计报告");
	    CellRangeAddress range1 = new CellRangeAddress(0, 1, 0, 14); //合并单元格
	    sheet.addMergedRegion(range1);
	    // 表头第二行为各阶段数量名称
	    createCell(wb, head2, 0, "统计项目");
	    createCell(wb, head2, 1, "规范性文件主体数量");
	    createCell(wb, head2, 2, "制定发布规范性文件数量");
	    createCell(wb, head2, 3, "法制机构进行合法性审核文件数量");
	    createCell(wb, head2, 4, "向备案机构报备的文件数量");
	    createCell(wb, head2, 5, "经备案审查发现存在问题的文件数量");
	    createCell(wb, head2, 6, "规范性文件存在的问题类型");
	    createCell(wb, head2, 11, "处理结果");
	    createCell(wb, head2, 14, "备注");
	    createCell(wb, head3, 6, "制定主体不合法");
	    createCell(wb, head3, 7, "制定程序不合法");
	    createCell(wb, head3, 8, "文件内容不合法");
	    createCell(wb, head3, 9, "制定技术有缺陷");
	    createCell(wb, head3, 10, "其他");
	    createCell(wb, head3, 11, "自行修正");
	    createCell(wb, head3, 12, "废弃撤销");
	    createCell(wb, head3, 13, "其他");
	    
	    CellRangeAddress title0 = new CellRangeAddress(2, 4, 0, 0); 
	    CellRangeAddress title1 = new CellRangeAddress(2, 4, 1, 1); 
	    CellRangeAddress title2 = new CellRangeAddress(2, 4, 2, 2); 
	    CellRangeAddress title3 = new CellRangeAddress(2, 4, 3, 3); 
	    CellRangeAddress title4 = new CellRangeAddress(2, 4, 4, 4);
	    CellRangeAddress title5 = new CellRangeAddress(2, 4, 5, 5); 
	    CellRangeAddress title8 = new CellRangeAddress(2, 3, 14, 14);
	    
	    CellRangeAddress title6 = new CellRangeAddress(2, 3, 6, 10); 
	    CellRangeAddress title7 = new CellRangeAddress(2, 3, 11, 13);
	    
	    sheet.addMergedRegion(title0);
	    sheet.addMergedRegion(title1);
	    sheet.addMergedRegion(title2);
	    sheet.addMergedRegion(title3);
	    sheet.addMergedRegion(title4);
	    sheet.addMergedRegion(title5);
	    sheet.addMergedRegion(title6);
	    sheet.addMergedRegion(title7);
	    sheet.addMergedRegion(title8);
	    
	    // 表头第三行为序列行
	    createCell(wb, headNum, 0, "序号");
	    createCell(wb, headNum, 1, "1");
	    createCell(wb, headNum, 2, "2");
	    createCell(wb, headNum, 3, "3");
	    createCell(wb, headNum, 4, "4");
	    createCell(wb, headNum, 5, "5");
	    createCell(wb, headNum, 6, "6");
	    createCell(wb, headNum, 7, "7");
	    createCell(wb, headNum, 8, "8");
	    createCell(wb, headNum, 9, "9");
	    createCell(wb, headNum, 10, "10");
	    createCell(wb, headNum, 11, "11");
	    createCell(wb, headNum, 12, "12");
	    createCell(wb, headNum, 13, "13");
	    createCell(wb, headNum, 14, "14");
	    
	    int flag = 0;
	    for (int index = 6; index <= 16; index += 2) {
	    	String str = "";
	    	if (index == 6) { //市(州)政府
	    	    str = "市(州)政府";
	    	} else if (index == 8) {
	    		str = "市(州)政府部门";
	    	} else if (index == 10) {
	    		str = "县(市、区)政府";
	    	} else if (index == 12) {
	    		str = "县(市、区)政府部门";
	    	} else if (index == 14) {
	    		str = "乡(镇、街道办)政府";
	    	} else if (index == 16) {
	    		str = "合计";
	    	}
	    	HSSFRow headC = sheet.createRow(index); 
	    	CellRangeAddress tit0 = new CellRangeAddress(index, index + 1, 0, 0);
	    	sheet.addMergedRegion(tit0);
	    	createCell(wb, headC, 0, str);
	    	JSONObject myjObject = jArr.getJSONObject(index - 6 - flag);
	    	createCell(wb, headC, 1, String.valueOf(myjObject.getInt("all")));
	    	CellRangeAddress tit1 = new CellRangeAddress(index, index + 1, 1, 1);
	    	sheet.addMergedRegion(tit1);
	    	createCell(wb, headC, 2, String.valueOf(myjObject.getInt("publish")));
	    	CellRangeAddress tit2 = new CellRangeAddress(index, index + 1, 2, 2);
	    	sheet.addMergedRegion(tit2);
	    	createCell(wb, headC, 3, String.valueOf(myjObject.getInt("review")));
	    	CellRangeAddress tit3 = new CellRangeAddress(index, index + 1, 3, 3);
	    	sheet.addMergedRegion(tit3);
	    	
	    	createCell(wb, headC, 4, String.valueOf(myjObject.getInt("record")));
	    	CellRangeAddress tit4 = new CellRangeAddress(index, index + 1, 4, 4);
	    	sheet.addMergedRegion(tit4);
	    	
	    	createCell(wb, headC, 5, String.valueOf(myjObject.getInt("deliberation")));
	    	CellRangeAddress tit5 = new CellRangeAddress(index, index + 1, 5, 5);
	    	sheet.addMergedRegion(tit5);
	    	
	    	createCell(wb, headC, 6, String.valueOf(myjObject.getInt("decUnitOop")));
	    	CellRangeAddress tit6 = new CellRangeAddress(index, index + 1, 6, 6);
	    	sheet.addMergedRegion(tit6);
	    	
	    	createCell(wb, headC, 7, String.valueOf(myjObject.getInt("decProcedureOop")));
	    	CellRangeAddress tit7 = new CellRangeAddress(index, index + 1, 7, 7);
	    	sheet.addMergedRegion(tit7);
	    	
	    	createCell(wb, headC, 8, String.valueOf(myjObject.getInt("contentOop")));
	    	CellRangeAddress tit8 = new CellRangeAddress(index, index + 1, 8, 8);
	    	sheet.addMergedRegion(tit8);
	    	
	    	createCell(wb, headC, 9, String.valueOf(myjObject.getInt("decTechHasDefects")));
	    	CellRangeAddress tit9 = new CellRangeAddress(index, index + 1, 9, 9);
	    	sheet.addMergedRegion(tit9);
	    	
	    	CellRangeAddress tit10 = new CellRangeAddress(index, index + 1, 10, 10);
	    	sheet.addMergedRegion(tit10);
	    	createCell(wb, headC, 10, String.valueOf(myjObject.getInt("others")));
	    	
	    	CellRangeAddress tit11 = new CellRangeAddress(index, index + 1, 11, 11);
	    	sheet.addMergedRegion(tit11);
	    	createCell(wb, headC, 11, String.valueOf(myjObject.getInt("self")));
	    	
	    	CellRangeAddress tit12 = new CellRangeAddress(index, index + 1, 12, 12);
	    	sheet.addMergedRegion(tit12);
	    	createCell(wb, headC, 12, String.valueOf(myjObject.getInt("revoke")));
	    	
	    	CellRangeAddress tit13 = new CellRangeAddress(index, index + 1, 13, 13);
	    	sheet.addMergedRegion(tit13);
	    	createCell(wb, headC, 13, String.valueOf(myjObject.getInt("qualified")));
	    	
	    	//备注
	    	CellRangeAddress tit14 = new CellRangeAddress(index, index + 1, 14, 14);
	    	sheet.addMergedRegion(tit14);
	    	
	    	flag++;
	    }
	    
	    // 第一行大标题
	    createCell(wb, headFoot, 0, "   填表单位(盖章)：                     填表人：                     审核人：                 联系电话：                         日期：            年     月     日");
	    CellRangeAddress footRange = new CellRangeAddress(18, 18, 0, 14); //合并单元格
	    sheet.addMergedRegion(footRange);
		
		return wb;
	}
	
	private void createCell(HSSFWorkbook wb, HSSFRow row, int col, String val){
		HSSFCell cell = row.createCell(col);
		HSSFCellStyle cellstyle = wb.createCellStyle(); 
		cellstyle.setWrapText(true); //自动换行
		cellstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);//设置水平对齐方式
		cellstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//设置垂直对齐方式
		cell.setCellStyle(cellstyle);
		cell.setCellValue(val);
	}
	
}
