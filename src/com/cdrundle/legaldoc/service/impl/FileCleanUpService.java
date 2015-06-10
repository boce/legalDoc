package com.cdrundle.legaldoc.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IFileCleanUpDao;
import com.cdrundle.legaldoc.dao.IFileCleanUpLineDao;
import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.FileCleanUp;
import com.cdrundle.legaldoc.entity.FileCleanUpLine;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.User;
import com.cdrundle.legaldoc.enums.FileStatus;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IFileCleanUpService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.vo.FileCleanUpLineVo;
import com.cdrundle.legaldoc.vo.FileCleanupVo;
import com.cdrundle.legaldoc.vo.OrgShortVo;
import com.cdrundle.legaldoc.vo.UserShortVo;
import com.cdrundle.security.WebPlatformUser;

@Service
public class FileCleanUpService implements IFileCleanUpService {

	@Autowired
	private IFileCleanUpDao fcuDao;

	@Autowired
	private IFileCleanUpLineDao fculDao;

	@Autowired
	private INormativeFileDao norDao;

	@Autowired
	private IOrganizationDao orgDao;

	@Autowired
	private IUserDao userDao;

	@PersistenceContext
	EntityManager em;

	@Override
	public FileCleanUp voToFileCleanup(FileCleanupVo fcuVo) {
		FileCleanUp fcu = new FileCleanUp();
		if(fcuVo.getId() != null){
			fcu = fcuDao.findOne(fcuVo.getId());
			if(fcu == null){
				fcu = new FileCleanUp();
			}
		}
		fcu.setApprovalUnit(orgDao.findOne(fcuVo.getApprovalUnit().getId()));
		fcu.setApprovalUnitClerk(userDao.findOne(fcuVo.getApprovalUnitClerk().getId()));
		fcu.setApprovalUnitLeader(userDao.findOne(fcuVo.getApprovalUnitLeader().getId()));
		fcu.setCleanupDate(fcuVo.getCleanupDate());
		fcu.setCleanupUnit(orgDao.findOne(fcuVo.getCleanupUnit().getId()));
		fcu.setCleanupUnitClerk(userDao.findOne(fcuVo.getCleanupUnitClerk().getId()));
		fcu.setCleanupUnitLeader(userDao.findOne(fcuVo.getCleanupUnitLeader().getId()));
		fcu.setMainLeaders(fcuVo.getMainLeaders());
		fcu.setStatus(fcuVo.getStatus());

		return fcu;
	}

	@Override
	public FileCleanupVo fileCleanupToVo(FileCleanUp fcu) {
		FileCleanupVo fcuVo = new FileCleanupVo();

		Organization aUnit = orgDao.findOne(fcu.getApprovalUnit().getId());
		OrgShortVo appUnit = OrgShortVo.createVo(aUnit);
		User aLeader = userDao.findOne(fcu.getApprovalUnitLeader().getId());
		UserShortVo appLeader = UserShortVo.createVo(aLeader);
		User aClerk = userDao.findOne(Long.valueOf(fcu.getApprovalUnitClerk().getId()));
		UserShortVo appClerk = UserShortVo.createVo(aClerk);

		Organization cUnit = orgDao.findOne(fcu.getCleanupUnit().getId());
		OrgShortVo cleanUnit = OrgShortVo.createVo(cUnit);
		User cLeader = userDao.findOne(fcu.getCleanupUnitLeader().getId());
		UserShortVo cleanLeader = UserShortVo.createVo(cLeader);
		User cClerk = userDao.findOne(Long.valueOf(fcu.getCleanupUnitClerk().getId()));
		UserShortVo cleanClerk = UserShortVo.createVo(cClerk);

		fcuVo.setId(fcu.getId());
		fcuVo.setApprovalUnit(appUnit);
		fcuVo.setApprovalUnitClerk(appClerk);
		fcuVo.setApprovalUnitLeader(appLeader);
		fcuVo.setCleanupDate(fcu.getCleanupDate());
		fcuVo.setCleanupUnit(cleanUnit);
		fcuVo.setCleanupUnitClerk(cleanClerk);
		fcuVo.setCleanupUnitLeader(cleanLeader);
		fcuVo.setMainLeaders(fcu.getMainLeaders());
		fcuVo.setStatus(fcu.getStatus());

		return fcuVo;
	}

	@Override
	public List<FileCleanupVo> fcutoVoList(List<FileCleanUp> fcuList) {
		List<FileCleanupVo> fcuVoList = new ArrayList<FileCleanupVo>();
		for (FileCleanUp fcu : fcuList) {
			fcuVoList.add(this.fileCleanupToVo((fcu)));
		}
		return fcuVoList;
	}

	@Override
	public FileCleanUpLine voToFCULineVo(FileCleanUpLineVo fculVo) {
		FileCleanUpLine fcul = new FileCleanUpLine();
		if(fculVo.getName() != null){
			fcul = fculDao.findByFileCleanUpName(fculVo.getName());
			if(fcul == null){
				fcul = new FileCleanUpLine();
			}
		}
		fcul.setCleanupResult(fculVo.getCleanupResult());
		fcul.setDecisionUnit(orgDao.findOne(fculVo.getDecisionUnit().getId()));
		fcul.setFileCleanup(this.voToFileCleanup(fculVo.getFileCleanupVo()));
		fcul.setName(fculVo.getName());
		fcul.setPublishDate(fculVo.getPublishDate());
		fcul.setPublishNo(fculVo.getPublishNo());
		fcul.setRemark(fculVo.getRemark());

		return fcul;
	}

	@Override
	public FileCleanUpLineVo fculToVo(FileCleanUpLine fcul) {
		Organization dUnit = orgDao.findOne(fcul.getDecisionUnit().getId());
		OrgShortVo decUnit = OrgShortVo.createVo(dUnit);
		FileCleanUpLineVo fculVo = new FileCleanUpLineVo();
		fculVo.setId(fcul.getId());
		fculVo.setCleanupResult(fcul.getCleanupResult());
		fculVo.setDecisionUnit(decUnit);
		fculVo.setFileCleanupVo(this.findById(fcul.getFileCleanup().getId()));
		fculVo.setName(fcul.getName());
		fculVo.setPublishDate(fcul.getPublishDate());
		fculVo.setPublishNo(fcul.getPublishNo());
		fculVo.setRemark(fcul.getRemark());

		return fculVo;
	}

	@Override
	public List<FileCleanUpLineVo> fcultoVoList(List<FileCleanUpLine> fculList) {
		List<FileCleanUpLineVo> fculVoList = new ArrayList<FileCleanUpLineVo>();
		for (FileCleanUpLine fcul : fculList) {
			fculVoList.add(this.fculToVo(fcul));
		}
		return fculVoList;
	}

	@Override
	@Transactional
	public FileCleanupVo saveOrUpdate(FileCleanupVo fcuVo, List<FileCleanUpLineVo> fculVoList) throws ServiceException {
		FileCleanUp fcu = null;
		if (fcuVo != null) {
			if (fcuVo.getId() == null) { // 清理文件的添加
				fcu = fcuDao.save(this.voToFileCleanup(fcuVo));
				if (fculVoList != null && fculVoList.size() > 0) {
					for (int index = 0; index < fculVoList.size(); index++) {
						FileCleanUpLineVo fculVo = fculVoList.get(index);
						if (fculVo != null) {
							fculVo.setFileCleanupVo(this.fileCleanupToVo(fcu));
							fculDao.save(this.voToFCULineVo(fculVo));
						}
					}
				}
			} else { // 清理文件的更新
				fcu = fcuDao.save(this.voToFileCleanup(fcuVo));
				if (fculVoList != null && fculVoList.size() > 0) {
					for (int index = 0; index < fculVoList.size(); index++) {
						FileCleanUpLineVo fculVo = fculVoList.get(index);
						if (fculVo != null) {
							fculVo.setFileCleanupVo(this.fileCleanupToVo(fcu));
							fculDao.save(this.voToFCULineVo(fculVo));
						}
					}
				}
			}
		} else {
			throw new ServiceException("清理文件新增时报错！");
		}
		return this.fileCleanupToVo(fcu);
	}

	@Override
	@Transactional
	public FileCleanUpLineVo saveLineFile(FileCleanUpLineVo fculVo) {
		FileCleanUpLine fcul = fculDao.findOne(fculVo.getId());
		if (fcul != null) { // 更新
			fcul = fculDao.save(fcul);
		} else {// 新增
			fcul = fculDao.save(this.voToFCULineVo(fculVo));
		}
		return this.fculToVo(fcul);
	}

	@Override
	@Transactional
	public boolean delete(FileCleanupVo fcuVo) throws ServiceException {
		boolean flag = false;
		// 获取子文件的集合
		List<FileCleanUpLineVo> childList = this.findFCULines(fcuVo.getId());
		// 获取fileCleanUp
		FileCleanUp fileCleanUp = fcuDao.getOne(fcuVo.getId());
		if (fileCleanUp != null) {
			for (int index = 0; index < childList.size(); index++) { // 逐个删除清理子文件
				FileCleanUpLineVo fculVo = childList.get(index);
				if (fculVo != null) {
					fculDao.delete(this.voToFCULineVo(fculVo)); // 删除子文件
				}
			}
			// 删除清理文件
			fcuDao.delete(fileCleanUp);
			flag = true;
		}
		return flag;
	}

	@Override
	@Transactional
	public boolean deleteLineFile(Long lineId) throws ServiceException {
		FileCleanUpLine fcul = fculDao.findOne(lineId);
		if (fcul != null) {
			fculDao.delete(fcul);
			return true;
		} else {
			throw new ServiceException("删除文件报错！");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public FileCleanupVo findById(Long fileCleanUpId) {
		if (fileCleanUpId != null && fileCleanUpId != 0l) {
			return this.fileCleanupToVo(fcuDao.findOne(fileCleanUpId));
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<FileCleanUpLineVo> findFCULAll() {
		List<FileCleanUpLine> list = fculDao.findAll();
		return this.fcultoVoList(list);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FileCleanupVo> findAll(int page, int size) {
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		if (userDetail != null) {
			if (userDetail.getOrgId() != null || !userDetail.getOrgId().trim().equals("")) {
				long orgId = Long.parseLong(userDetail.getOrgId());
				Pageable pageable = new PageRequest(page, size);
				Page<FileCleanUp> pages = fcuDao.findAll(orgId, pageable);
				List<FileCleanupVo> volist = this.fcutoVoList(pages.getContent());
				Page<FileCleanupVo> pageVo = new PageImpl<FileCleanupVo>(volist, pageable, pages.getTotalElements());
				return pageVo;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FileCleanUpLineVo> findFCULines(long id) {
		return this.fcultoVoList(fcuDao.findFCULines(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FileCleanupVo> findByUnitAndDate(Long cleanupUnit, Date cleanupBegDate, Date cleanupEndDate, Integer page, Integer size) {
		Pageable pageable = new PageRequest(page, size);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<FileCleanUp> cq = cb.createQuery(FileCleanUp.class);
		Root<FileCleanUp> root = cq.from(FileCleanUp.class);
		cq.select(root);
		Predicate where = cb.conjunction();
		where = buildCondition(where, root, cleanupUnit, cleanupBegDate, cleanupEndDate, cb);
		cq.where(where);
		TypedQuery<FileCleanUp> query = em.createQuery(cq);
		query.setFirstResult(pageable.getPageSize() * (pageable.getPageNumber())).setMaxResults(pageable.getPageSize());
		List<FileCleanUp> rows = query.getResultList();

		int totalCount = getTotalCount(cleanupUnit, cleanupBegDate, cleanupEndDate);

		List<FileCleanupVo> volist = this.fcutoVoList(rows);
		Page<FileCleanupVo> pages = new PageImpl<FileCleanupVo>(volist, pageable, totalCount);
		return pages;
	}

	/**
	 * 获取总条数
	 * 
	 * @param cleanupUnit
	 * @param cleanupDate
	 * @return
	 */
	private int getTotalCount(Long cleanupUnit, Date cleanupBegDate, Date cleanupEndDate) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<FileCleanUp> emp = cq.from(FileCleanUp.class);
		cq.select(cb.countDistinct(emp));
		Predicate where = cb.conjunction();
		where = buildCondition(where, emp, cleanupUnit, cleanupBegDate, cleanupEndDate, cb);
		cq.where(where);

		return em.createQuery(cq).getSingleResult().intValue();

	}

	/**
	 * 组装查询条件
	 * 
	 * @param where
	 * @param root
	 * @param cleanupUnit
	 * @param cleanupDate
	 * @param cb
	 * @return
	 */
	private Predicate buildCondition(Predicate where, Root<FileCleanUp> root, Long cleanupUnit, Date cleanupBegDate, Date cleanupEndDate,
			CriteriaBuilder cb) {
		EntityType<FileCleanUp> et = root.getModel();
		if (cleanupUnit != null) {
			Organization org = orgDao.findOne(cleanupUnit);
			if (org != null) {
				where = cb.and(where, cb.equal(root.get(et.getSingularAttribute("cleanupUnit", Organization.class)), org));
			}
		}
		if (cleanupBegDate != null && cleanupEndDate != null) {
			where = cb.and(where, cb.between(root.get(et.getSingularAttribute("cleanupDate", Date.class)), cleanupBegDate, cleanupEndDate));
		}
		return where;
	}

	@Override
	public HSSFWorkbook download(List<FileCleanUpLineVo> childList, FileCleanupVo cleanup) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("清理文件清单");
		HSSFRow row = sheet.createRow((int) 0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		String[] names = { "文件名称", "制定单位", "发文号", "发布日期", "状态", "清理单位" };
		HSSFCell cell = null;
		for (int index = 0; index < names.length; index++) { // 生成title
			cell = row.createCell(index);
			cell.setCellValue(names[index]);
			cell.setCellStyle(style);
		}
		sheet.setColumnWidth(0, 40 * 256); // 设置宽度
		sheet.setColumnWidth(1, 20 * 256);
		sheet.setColumnWidth(2, 20 * 256);
		sheet.setColumnWidth(3, 20 * 256);
		sheet.setColumnWidth(4, 20 * 256);
		sheet.setColumnWidth(5, 20 * 256);
		if (childList != null && childList.size() > 0 && cleanup != null) {
			String str = "";
			OrgShortVo org = null;
			Date date = null;
			SimpleDateFormat sf = new SimpleDateFormat(SysUtil.DATE_FORMAT_NORMAL);
			for (int i = 0; i < childList.size(); i++) {
				FileCleanUpLineVo fileVo = childList.get(i);
				row = sheet.createRow((int) i + 1);

				cell = row.createCell(0); // 文件名称
				str = fileVo.getName();
				if (!StringUtils.isEmpty(str)) {
					cell.setCellValue(str);
				} else {
					cell.setCellValue("");
				}
				cell.setCellStyle(style);

				cell = row.createCell(1); // 制定单位
				org = fileVo.getDecisionUnit();
				if (org != null) {
					cell.setCellValue(org.getText());
				} else {
					cell.setCellValue("");
				}
				cell.setCellStyle(style);

				cell = row.createCell(2); // 发文号
				str = fileVo.getPublishNo();
				if (!StringUtils.isEmpty(str)) {
					cell.setCellValue(str);
				} else {
					cell.setCellValue("");
				}
				cell.setCellStyle(style);

				cell = row.createCell(3); // 发文日期
				date = fileVo.getPublishDate();
				if (date == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(sf.format(date));
				}
				cell.setCellStyle(style);

				cell = row.createCell(4); // 状态
				FileStatus s = fileVo.getCleanupResult();
				if (s == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(fileVo.getCleanupResult().toString());
				}
				cell.setCellStyle(style);

				cell = row.createCell(5); // 清理单位
				org = cleanup.getCleanupUnit();
				if (org != null) {
					cell.setCellValue(org.getText());
				} else {
					cell.setCellValue("");
				}
				cell.setCellStyle(style);

			}
		}

		return wb;
	}

	@Override
	public String printList(List<FileCleanUpLineVo> childList, FileCleanupVo cleanup) {
		StringBuffer resultStr = new StringBuffer("");
		resultStr.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-size:12px;\">");
		resultStr.append("<tr>");
		resultStr.append("<td style=\"width:400px;height:28px;text-align: center;\">文件名称</td>");
		resultStr.append("<td style=\"width:200px;height:28px;text-align: center;\">制定单位</td>");
		resultStr.append("<td style=\"width:120px;height:28px;text-align: center;\">发文号</td>");
		resultStr.append("<td style=\"width:120px;height:28px;text-align: center;\">发布日期</td>");
		resultStr.append("<td style=\"width:120px;height:28px;text-align: center;\">状态</td>");
		resultStr.append("<td style=\"width:200px;height:28px;text-align: center;\">清理单位</td>");
		resultStr.append("</tr>");
		String str = "";
		OrgShortVo org = null;
		Date date = null;
		SimpleDateFormat sf = new SimpleDateFormat(SysUtil.DATE_FORMAT_NORMAL);
		for (int i = 0; i < childList.size(); i++) {
			FileCleanUpLineVo fileVo = childList.get(i);

			resultStr.append("<tr>");
			str = fileVo.getName(); // 文件名称
			if (!StringUtils.isEmpty(str)) {
				resultStr.append("<td style=\"width:400px;height:28px;text-align: center;\">" + str + "</td>");
			} else {
				resultStr.append("<td style=\"width:400px;height:28px;text-align: center;\"></td>");
			}

			org = fileVo.getDecisionUnit(); // 制定单位
			if (org != null) {
				resultStr.append("<td style=\"width:200px;height:28px;text-align: center;\">" + org.getText() + "</td>");
			} else {
				resultStr.append("<td style=\"width:200px;height:28px;text-align: center;\"></td>");
			}

			str = fileVo.getPublishNo(); // 发文号
			if (!StringUtils.isEmpty(str)) {
				resultStr.append("<td style=\"width:120px;height:28px;text-align: center;\">" + str + "</td>");
			} else {
				resultStr.append("<td style=\"width:120px;height:28px;text-align: center;\"></td>");
			}

			date = fileVo.getPublishDate(); // 发布日期
			if (date != null) {
				resultStr.append("<td style=\"width:120px;height:28px;text-align: center;\">" + sf.format(date) + "</td>");
			} else {
				resultStr.append("<td style=\"width:120px;height:28px;text-align: center;\"></td>");
			}

			FileStatus s = fileVo.getCleanupResult(); // 状态
			if (s != null) {
				resultStr.append("<td style=\"width:120px;height:28px;text-align: center;\">" + fileVo.getCleanupResult().toString() + "</td>");
			} else {
				resultStr.append("<td style=\"width:120px;height:28px;text-align: center;\"></td>");
			}

			org = cleanup.getCleanupUnit(); // 清理单位
			if (org != null) {
				resultStr.append("<td style=\"width:200px;height:28px;text-align: center;\">" + org.getText() + "</td>");
			} else {
				resultStr.append("<td style=\"width:200px;height:28px;text-align: center;\"></td>");
			}

			resultStr.append("</tr>");
		}

		resultStr.append("</table>");
		return resultStr.toString();
	}

	@Override
	public void submit(FileCleanUp fileCleanUp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void approve(FileCleanUp fileCleanUp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unApprove(FileCleanUp fileCleanUp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void flow(FileCleanUp fileCleanUp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanUp(FileCleanUp fileCleanUp) {
		// TODO Auto-generated method stub

	}

}
