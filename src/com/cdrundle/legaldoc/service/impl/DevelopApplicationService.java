package com.cdrundle.legaldoc.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.IDevelopApplicationDao;
import com.cdrundle.legaldoc.dao.IDraftDao;
import com.cdrundle.legaldoc.dao.ILegalBasisDao;
import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.DevelopApplication;
import com.cdrundle.legaldoc.entity.Draft;
import com.cdrundle.legaldoc.entity.LegalBasis;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.enums.LegalBasisType;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IDevelopApplicationService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.DevelopApplicationVo;
import com.cdrundle.legaldoc.vo.LegalBasisVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class DevelopApplicationService implements IDevelopApplicationService {
	
	private static final String FIRST_DOC_NO = "001";
	
	@Autowired
	IDevelopApplicationDao developApplicationDao;

	@Autowired
	INormativeFileDao normativeFileDao;

	@Autowired
	IOrganizationDao organizationDao;

	@Autowired
	IUserDao userDao;

	@Autowired
	IDraftDao draftDao;

	@Autowired
	ILegalBasisDao legalBasisDao;

	@Autowired
	Configuration freemarkerConfiguration;
	
	@Override
	@Transactional
	public DevelopApplicationVo saveOrUpdate(DevelopApplicationVo developApplicationVo, String rootPath, String tempFileId) throws ServiceException,
			IOException, TemplateException {
		Long id = developApplicationVo.getId();
		DevelopApplication savedDevelopApplication;
		if (id != null) {
			DevelopApplication developApplication = developApplicationDao.findOne(id);
			if (developApplication != null) {
				NormativeFile normativeFile = developApplication.getNormativeFile();
				if (normativeFile != null) {
					Stage stage = normativeFile.getStage();
					if (!Stage.SETUP.equals(stage)) {
						throw new ServiceException("已存在下游业务，不允许修改！");
					}
					transToDevelopApplication(developApplication, developApplicationVo);
					normativeFile.setName(developApplication.getName());
					normativeFile.setDrtUnit(developApplication.getApplyOrg());
					normativeFile.setDrtUnitLeader(developApplication.getApplyLeader());
					normativeFile.setDrtUnitClerk(developApplication.getApplyClerk());
					normativeFile.setApplyDate(developApplication.getApplyDate());
					normativeFile.setValidDate(developApplication.getValidDate());
					normativeFile.setPriority(developApplication.getPriority());
					normativeFile.setLegalBasis(developApplication.getLegalBasisAttachment());
					normativeFile.setLegalBasisNoAtta(developApplicationVo.getLegalBasisNoAtta());
					normativeFile.setBasisInvalidDate(developApplication.getBasisInvalidDate());
					normativeFile.setInvolvedOrges(developApplication.getInvolvedOrges());
					normativeFile.setApplyUnit(developApplication.getApplyOrg());
					savedDevelopApplication = developApplicationDao.save(developApplication);
				} else {
					throw new ServiceException("数据错误，制定申请对应规范性文件不存在");
				}
			} else {
				throw new ServiceException("数据错误，该制定申请单不存在");
			}

		} else {
			DevelopApplication developApplicationCheck = developApplicationDao.findByName(developApplicationVo.getName());
			if(developApplicationCheck != null){
				throw new ServiceException("保存失败，文件名称已存在！");
			}
			DevelopApplication developApplication = convertToDevelopApplication(developApplicationVo);
			NormativeFile norFile = new NormativeFile();
			norFile.setName(developApplication.getName());
			norFile.setDecUnit(developApplication.getApplyOrg());
			norFile.setDecUnitLeader(developApplication.getApplyLeader());
			norFile.setDecUnitClerk(developApplication.getApplyClerk());
			norFile.setApplyDate(developApplication.getApplyDate());
			norFile.setValidDate(developApplication.getValidDate());
			norFile.setPriority(developApplication.getPriority());
			norFile.setLegalBasis(developApplication.getLegalBasisAttachment());
			norFile.setLegalBasisNoAtta(developApplicationVo.getLegalBasisNoAtta());
			norFile.setBasisInvalidDate(developApplication.getBasisInvalidDate());
			norFile.setInvolvedOrges(developApplication.getInvolvedOrges());
			norFile.setApplyUnit(developApplication.getApplyOrg());
			norFile.setDocNo(genDocNo());
			norFile.setStage(Stage.SETUP);
			NormativeFile savedNorFile = normativeFileDao.save(norFile);
			developApplication.setNormativeFile(savedNorFile);
			savedDevelopApplication = developApplicationDao.save(developApplication);

		}
		// 把上传文件从临时目录转移到正式目录
		if (StringUtils.isNotEmpty(tempFileId)) {
			String filePath = rootPath + File.separator + SysUtil.FILE_PATH_TEMP + File.separator + tempFileId;
			NormativeFileVo norFileVo = NormativeFileVo.createVo(savedDevelopApplication.getNormativeFile());
			String newfilePath = WordUtils.getFilePath(rootPath, norFileVo, Stage.SETUP.toString());
			boolean success = WordUtils.copyFolder(filePath, newfilePath);
			if (!success) {
				throw new ServiceException("保存出错，附件保存失败！");
			}
			// 删除临时文件目录
			WordUtils.deleteWord(filePath);
		}
		// 保存立项申请文档
		Map<String, Object> root = new HashMap<>();
		root.put("devApp", savedDevelopApplication);
		root.put("legalBasisContent", gainLegalBasisContent(savedDevelopApplication));
		root.put("necLegalRisk", gainNecLegalRisk(savedDevelopApplication));
		root.put("mainProblem", gainMainProblem(savedDevelopApplication));
		root.put("planRegAndMea", gainPlanRegAndMea(savedDevelopApplication));
		root.put("involvedOrges", getInvolvedOrgesName(savedDevelopApplication.getInvolvedOrges()));
		NormativeFileVo norFileVo = NormativeFileVo.createVo(savedDevelopApplication.getNormativeFile());
		String filePath = WordUtils.getFilePath(rootPath, norFileVo, Stage.SETUP.toString());
		String fileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_DEVAPP + ")" + SysUtil.EXTENSION_NAME;
		WordUtils.htmlToWord(filePath, fileName, getTemplateString("devApp.flt", root));
		return DevelopApplicationVo.createVo(savedDevelopApplication);
	}

	/**
	 * 将解析之后的文件内容返回字符串
	 * 
	 * @param name 模板文件名
	 * @param root 数据Map
	 * @return
	 * @throws IOException 
	 * @throws TemplateException 
	 */
	public String getTemplateString(String name, Map<String, Object> root) throws IOException, TemplateException {
		String result = "";
		try(StringWriter out = new StringWriter();) {
			// 通过一个文件输出流，就可以写到相应的文件中
			Template temp = freemarkerConfiguration.getTemplate(name);
			temp.process(root, out);
			result = out.toString();
		} 
		return result;
	}

	/**
	 * 拟确定的制度或措施，以及可行性论证
	 * @param developApplication
	 * @return
	 */
	private String gainPlanRegAndMea(DevelopApplication developApplication) {
		String planRegAndMea = "";
		if (StringUtils.isNotEmpty(developApplication.getPlanRegulationMeasureAndFeasibility())) {
			planRegAndMea = developApplication.getPlanRegulationMeasureAndFeasibility();
		}
		if (StringUtils.isNotEmpty(developApplication.getPlanRegulationMeasureAndFeasibilityAtta())) {
			if (!"".equals(planRegAndMea)) {
				planRegAndMea += "<br/>";
			}
			String planRegAndMeaFileName = developApplication.getPlanRegulationMeasureAndFeasibilityAtta().replaceAll("(\\.docx)|(\\.doc)", "");
			String[] planRegAndMeaFileNames = planRegAndMeaFileName.split(",");
			for (int i = 0; i < planRegAndMeaFileNames.length; i++) {
				if (i > 0) {
					planRegAndMea += "<br/>";
				}
				planRegAndMea += "《" + planRegAndMeaFileNames[i] + "》";
			}
		}
		return planRegAndMea;
	}

	/**
	 * 拟解决的主要问题
	 * @param developApplication
	 * @return
	 */
	private String gainMainProblem(DevelopApplication developApplication) {
		String mainProblem = "";
		if (StringUtils.isNotEmpty(developApplication.getMainProblem())) {
			mainProblem = developApplication.getMainProblem();
		}
		if (StringUtils.isNotEmpty(developApplication.getMainProblemAttachment())) {
			if (!"".equals(mainProblem)) {
				mainProblem += "<br/>";
			}
			String mainProblemFileName = developApplication.getMainProblemAttachment().replaceAll("(\\.docx)|(\\.doc)", "");
			String[] mainProblemFileNames = mainProblemFileName.split(",");
			for (int i = 0; i < mainProblemFileNames.length; i++) {
				if (i > 0) {
					mainProblem += "<br/>";
				}
				mainProblem += "《" + mainProblemFileNames[i] + "》";
			}
		}
		return mainProblem;
	}

	/**
	 * 制定的必要性、合法性，以及社会稳定性风险评估
	 * @param developApplication
	 * @return
	 */
	private String gainNecLegalRisk(DevelopApplication developApplication) {
		String necLegalRisk = "";
		if (StringUtils.isNotEmpty(developApplication.getNecessityLegalAndRisk())) {
			necLegalRisk = developApplication.getNecessityLegalAndRisk();
		}
		if (StringUtils.isNotEmpty(developApplication.getNecessityLegalAndRiskAttachment())) {
			if (!"".equals(necLegalRisk)) {
				necLegalRisk += "<br/>";
			}
			String necLegalRiskFileName = developApplication.getNecessityLegalAndRiskAttachment().replaceAll("(\\.docx)|(\\.doc)", "");
			String[] necLegalRiskFileNames = necLegalRiskFileName.split(",");
			for (int i = 0; i < necLegalRiskFileNames.length; i++) {
				if (i > 0) {
					necLegalRisk += "<br/>";
				}
				necLegalRisk += "《" + necLegalRiskFileNames[i] + "》";
			}
		}
		return necLegalRisk;
	}

	/**
	 * 制定依据
	 * @param developApplication
	 * @return
	 */
	private String gainLegalBasisContent(DevelopApplication developApplication) {
		List<LegalBasis> legalBasises = developApplication.getLegalBasises();
		Map<String, String> legalBasisMap = new LinkedHashMap<>();
		for (LegalBasisType e : LegalBasisType.values()) {
			legalBasisMap.put(e.name(), "");
		}
		for (Iterator<LegalBasis> iterator = legalBasises.iterator(); iterator.hasNext();) {
			LegalBasis legalBasis = iterator.next();
			LegalBasisType legalBasisType = legalBasis.getLegalBasisType();
			String legalBasisStr = legalBasisMap.get(legalBasisType.name());
			if (!"".equals(legalBasisStr)) {
				legalBasisStr += "<br>";
			}
			legalBasisStr += "《" + legalBasis.getName() + "》";
			legalBasisMap.put(legalBasisType.name(), legalBasisStr);
		}
		String legalBasisContent = "";
		Set<Entry<String, String>> legalBasisEntrySet = legalBasisMap.entrySet();
		for (Iterator<Entry<String, String>> iterator = legalBasisEntrySet.iterator(); iterator.hasNext();) {
			Entry<String, String> entry = iterator.next();
			if ("".equals(entry.getValue())) {
				continue;
			}
			if (!"".equals(legalBasisContent)) {
				legalBasisContent += "<br>";
			}
			legalBasisContent += LegalBasisType.getByName(entry.getKey()) + "：<br>" + entry.getValue();
		}
		return legalBasisContent;
	}

	/**
	 * 获取涉及部门名称
	 * 
	 * @param involvedOrges
	 *            涉及部门编码，以逗号分隔
	 * @return
	 */
	private String getInvolvedOrgesName(String involvedOrges) {
		String involvedOrgesName = "";
		if (StringUtils.isNotEmpty(involvedOrges)) {
			involvedOrges = involvedOrges.replaceAll("\"", "");
			Set<Long> orgSet = new LinkedHashSet<>();
			String[] orgArray = involvedOrges.split(",");
			for (int i = 0; i < orgArray.length; i++) {
				orgSet.add(Long.parseLong(orgArray[i]));
			}
			List<Organization> orgList = organizationDao.findOrgByIds(orgSet);
			for (Iterator<Organization> iterator = orgList.iterator(); iterator.hasNext();) {
				Organization org = iterator.next();
				if ("".equals(involvedOrgesName)) {
					involvedOrgesName = org.getName();
				} else {
					involvedOrgesName += ("," + org.getName());
				}
			}
		}
		return involvedOrgesName;
	}

	/**
	 * 生成单据号
	 * 
	 * @return
	 */
	private String genDocNo() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(SysUtil.DATE_FORMAT);
		String dateStr = sdf.format(calendar.getTime());
		String maxDocNo = normativeFileDao.findDocNo(dateStr);
		String docNo;
		if (StringUtils.isEmpty(maxDocNo)) {
			docNo = dateStr + FIRST_DOC_NO;
		} else {
			int docNoIndex = Integer.parseInt(maxDocNo.substring(8)) + 1;
			docNo = dateStr + String.format("%03d", docNoIndex);
		}
		return docNo;
	}

	@Override
	@Transactional
	public boolean delete(DevelopApplicationVo developApplicationVo) {
		developApplicationDao.delete(convertToDevelopApplication(developApplicationVo));
		return true;
	}

	@Override
	@Transactional
	public boolean delete(Long id) {
		developApplicationDao.delete(id);
		return true;
	}

	@Override
	public boolean submit(DevelopApplicationVo developApplicationVo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean approve(DevelopApplicationVo developApplicationVo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean flow() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public DevelopApplicationVo findById(Long id) {
		return DevelopApplicationVo.createVo(developApplicationDao.findOne(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DevelopApplicationVo> findByName(String name, Set<Long> orgIds, int page, int size) {

		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "applyDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<DevelopApplication> pageDevelopApplication;
		if (StringUtils.isNotEmpty(name)) {
			pageDevelopApplication = developApplicationDao.findLikeName(orgIds, "%" + name + "%", pageable);
		} else {
			pageDevelopApplication = developApplicationDao.findAll(orgIds, pageable);
		}

		List<DevelopApplicationVo> volist = DevelopApplicationVo.createVoList(pageDevelopApplication.getContent());
		Page<DevelopApplicationVo> pages = new PageImpl<DevelopApplicationVo>(volist, pageable, pageDevelopApplication.getTotalElements());
		return pages;
	}

	@Override
	@Transactional(readOnly = true)
	public DevelopApplicationVo findByName(String name) {
		return DevelopApplicationVo.createVo(developApplicationDao.findByName(name));
	}

	@Override
	@Transactional(readOnly = true)
	public DevelopApplicationVo findByNorFileId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public void updateLegalBasisAtta(Long id, String legalBasisAtta) {
		developApplicationDao.updateLegalBasisAtta(id, legalBasisAtta);

	}

	@Override
	@Transactional
	public void updateNecLegRiskAtta(Long id, String necLegRiskAtta) {
		developApplicationDao.updatenecLegRiskAtta(id, necLegRiskAtta);

	}

	@Override
	@Transactional
	public void updateMainProblemAtta(Long id, String mainProblemAtta) {
		developApplicationDao.updatemainProblemAtta(id, mainProblemAtta);

	}

	@Override
	@Transactional
	public void updatePlanRegMeaAtta(Long id, String planRegMeaAtta) {
		developApplicationDao.updateplanRegMeaAtta(id, planRegMeaAtta);

	}

	@Override
	@Transactional
	public boolean deleteDevAndFile(DevelopApplicationVo developApplicationVo, String filePath, String fileName) throws ServiceException {
		boolean deleteFlag = false;
		Draft draft = draftDao.findByNorFileId(developApplicationVo.getNormativeFile().getId());
		if (draft != null) {
			throw new ServiceException("已存在下游业务,不允许删除");
		}
		developApplicationDao.delete(developApplicationVo.getId());
		WordUtils.deleteWord(filePath);
		deleteFlag = true;
		return deleteFlag;
	}

	/**
	 * 转为实体对象
	 * 
	 * @return
	 */
	public DevelopApplication convertToDevelopApplication(DevelopApplicationVo developApplicationVo) {
		DevelopApplication developApplication = new DevelopApplication();
		developApplication.setId(developApplicationVo.getId());
		developApplication.setName(developApplicationVo.getName());
		if (developApplicationVo.getNormativeFile() != null && developApplicationVo.getNormativeFile().getId() != null) {
			developApplication.setNormativeFile(normativeFileDao.findOne(developApplicationVo.getNormativeFile().getId()));
		}
		if (developApplicationVo.getApplyOrg() != null) {
			developApplication.setApplyOrg(organizationDao.findOne(developApplicationVo.getApplyOrg().getId()));
		}
		if (developApplicationVo.getApplyLeader() != null) {
			developApplication.setApplyLeader(userDao.findOne(developApplicationVo.getApplyLeader().getId()));
		}
		if (developApplicationVo.getApplyClerk() != null) {
			developApplication.setApplyClerk(userDao.findOne(developApplicationVo.getApplyClerk().getId()));
		}
		if (developApplicationVo.getApprovalLeader() != null) {
			developApplication.setApprovalLeader(userDao.findOne(developApplicationVo.getApprovalLeader().getId()));
		}
		developApplication.setPlanDraftDate(developApplicationVo.getPlanDraftDate());
		developApplication.setPlanReviewDate(developApplicationVo.getPlanReviewDate());
		developApplication.setApplyDate(developApplicationVo.getApplyDate());
		developApplication.setValidDate(developApplicationVo.getValidDate());
		developApplication.setPriority(developApplicationVo.getPriority());
		developApplication.setStatus(developApplicationVo.getStatus());
		developApplication.setBasisInvalidDate(developApplicationVo.getBasisInvalidDate());
		developApplication.setLegalBasisAttachment(developApplicationVo.getLegalBasisAttachment());
		developApplication.setNecessityLegalAndRisk(developApplicationVo.getNecessityLegalAndRisk());
		developApplication.setNecessityLegalAndRiskAttachment(developApplicationVo.getNecessityLegalAndRiskAttachment());
		developApplication.setMainProblem(developApplicationVo.getMainProblem());
		developApplication.setMainProblemAttachment(developApplicationVo.getMainProblemAttachment());
		developApplication.setPlanRegulationMeasureAndFeasibility(developApplicationVo.getPlanRegulationMeasureAndFeasibility());
		developApplication.setPlanRegulationMeasureAndFeasibilityAtta(developApplicationVo.getPlanRegulationMeasureAndFeasibilityAtta());
		developApplication.setInvolvedOrges(developApplicationVo.getInvolvedOrges());
		developApplication.setApplyLeaderComment(developApplicationVo.getApplyLeaderComment());
		developApplication.setApprovalLeaderComment(developApplicationVo.getApprovalLeaderComment());
		developApplication.setRemarks(developApplicationVo.getRemarks());
		List<LegalBasis> legalBasisList = new ArrayList<>();
		List<LegalBasisVo> legalBasises = developApplicationVo.getLegalBasises();
		for (Iterator<LegalBasisVo> iterator = legalBasises.iterator(); iterator.hasNext();) {
			LegalBasisVo legalBasisVo = iterator.next();
			LegalBasis legalBasis = new LegalBasis();
			legalBasis.setName(legalBasisVo.getName());
			legalBasis.setBasisInvalidDate(legalBasisVo.getBasisInvalidDate());
			legalBasis.setLegalBasisType(legalBasisVo.getLegalBasisType());
			legalBasis.setLegalBasisAtta(legalBasisVo.getLegalBasisAtta());
			legalBasis.setDevelopApplication(developApplication);
			legalBasisList.add(legalBasis);
		}
		developApplication.setLegalBasises(legalBasisList);
		return developApplication;
	}

	/**
	 * 把vo对象赋值到实体
	 * 
	 * @param developApplication
	 * @param developApplicationVo
	 */
	public void transToDevelopApplication(DevelopApplication developApplication, DevelopApplicationVo developApplicationVo) {
		developApplication.setName(developApplicationVo.getName());
		if (developApplicationVo.getApplyOrg() != null) {
			developApplication.setApplyOrg(organizationDao.findOne(developApplicationVo.getApplyOrg().getId()));
		}
		if (developApplicationVo.getApplyLeader() != null) {
			developApplication.setApplyLeader(userDao.findOne(developApplicationVo.getApplyLeader().getId()));
		}
		if (developApplicationVo.getApplyClerk() != null) {
			developApplication.setApplyClerk(userDao.findOne(developApplicationVo.getApplyClerk().getId()));
		}
		if (developApplicationVo.getApprovalLeader() != null) {
			developApplication.setApprovalLeader(userDao.findOne(developApplicationVo.getApprovalLeader().getId()));
		}
		developApplication.setPlanDraftDate(developApplicationVo.getPlanDraftDate());
		developApplication.setPlanReviewDate(developApplicationVo.getPlanReviewDate());
		developApplication.setApplyDate(developApplicationVo.getApplyDate());
		developApplication.setValidDate(developApplicationVo.getValidDate());
		developApplication.setPriority(developApplicationVo.getPriority());
		developApplication.setStatus(developApplicationVo.getStatus());
		developApplication.setBasisInvalidDate(developApplicationVo.getBasisInvalidDate());
		developApplication.setLegalBasisAttachment(developApplicationVo.getLegalBasisAttachment());
		developApplication.setNecessityLegalAndRisk(developApplicationVo.getNecessityLegalAndRisk());
		developApplication.setNecessityLegalAndRiskAttachment(developApplicationVo.getNecessityLegalAndRiskAttachment());
		developApplication.setMainProblem(developApplicationVo.getMainProblem());
		developApplication.setMainProblemAttachment(developApplicationVo.getMainProblemAttachment());
		developApplication.setPlanRegulationMeasureAndFeasibility(developApplicationVo.getPlanRegulationMeasureAndFeasibility());
		developApplication.setPlanRegulationMeasureAndFeasibilityAtta(developApplicationVo.getPlanRegulationMeasureAndFeasibilityAtta());
		developApplication.setInvolvedOrges(developApplicationVo.getInvolvedOrges());
		developApplication.setApplyLeaderComment(developApplicationVo.getApplyLeaderComment());
		developApplication.setApprovalLeaderComment(developApplicationVo.getApprovalLeaderComment());
		developApplication.setRemarks(developApplicationVo.getRemarks());
		List<LegalBasis> legalBasises = developApplication.getLegalBasises();
		List<LegalBasisVo> legalBasisesVoList = developApplicationVo.getLegalBasises();
		List<LegalBasisVo> voList = LegalBasisVo.createVoList(legalBasises);
		for (Iterator<LegalBasisVo> iterator = voList.iterator(); iterator.hasNext();) {
			LegalBasisVo legalBasisVo = iterator.next();
			LegalBasis legalBasis = legalBasisDao.findOne(legalBasisVo.getId());
			legalBasises.remove(legalBasis);
			boolean isDelete = true;
			for (Iterator<LegalBasisVo> iterator2 = legalBasisesVoList.iterator(); iterator2.hasNext();) {
				LegalBasisVo legalBasisVo2 = iterator2.next();
				if (legalBasisVo.getId().equals(legalBasisVo2.getId())) {
					isDelete = false;
					break;
				}
			}
			if (isDelete) {
				legalBasisDao.delete(legalBasis);
			}
		}
		for (Iterator<LegalBasisVo> iterator = legalBasisesVoList.iterator(); iterator.hasNext();) {
			LegalBasisVo legalBasisVo = iterator.next();
			LegalBasis legalBasis = new LegalBasis();
			if (legalBasisVo.getId() != null) {
				legalBasis = legalBasisDao.findOne(legalBasisVo.getId());
			} else {
				legalBasis = new LegalBasis();
			}
			legalBasis.setName(legalBasisVo.getName());
			legalBasis.setBasisInvalidDate(legalBasisVo.getBasisInvalidDate());
			legalBasis.setLegalBasisType(legalBasisVo.getLegalBasisType());
			legalBasis.setLegalBasisAtta(legalBasisVo.getLegalBasisAtta());
			legalBasis.setDevelopApplication(developApplication);
			legalBasises.add(legalBasis);
		}
	}

}
