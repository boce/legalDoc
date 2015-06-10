package com.cdrundle.legaldoc.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IRecordReviewDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.Organization;
import com.cdrundle.legaldoc.entity.RecordReview;
import com.cdrundle.legaldoc.enums.OrgType;
import com.cdrundle.legaldoc.enums.ReviewResult;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IRecordReviewService;
import com.cdrundle.legaldoc.util.PinYin;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.RecordReviewVo;
import com.cdrundle.security.WebPlatformUser;

/**
 * @author  XuBao
 *备案审查
 * 2014年6月19日
 */
@Service
public class RecordReviewService implements IRecordReviewService{
	
	private static final String FIRST_RIG_NO = "001";
	@Autowired
	private  IRecordReviewDao  recordReviewDao;
	@Autowired
	private IOrganizationDao    organizationDao;
	@Autowired
	private INormativeFileDao  normativeFileDao;
	@Autowired
	private  IUserDao  userDao;
	
	
	/**
	 * 生成备案号
	 * @return
	 */
	@Override
	@Transactional(readOnly = true)
	public  String gainRegisterCode() {
		String registerCode = "";
		Calendar calendar = Calendar.getInstance();
		//得到当前登录用户信息
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		long orgId = Long.parseLong(userDetail.getOrgId());
		Organization organization = organizationDao.findOne(orgId);
		OrgType orgType = organization.getOrgType();
		if(orgType.name().indexOf("CITY") >= 0){
			registerCode = "C";
		}else if(orgType.name().indexOf("COUNTY") >= 0){
			registerCode = "D";
		}
		String name = organization.getName();
		String str   = registerCode + PinYin.toJianPin(name) + "R" + "-" + calendar.get(Calendar.YEAR) + "-" ;
		String maxRegisterCode  =  normativeFileDao.findRegisterCode(str);
		
		if (StringUtils.isEmpty(maxRegisterCode)) {
			registerCode = str + FIRST_RIG_NO;
		} else {
			int registerCodeIndex = Integer.parseInt(maxRegisterCode.substring(maxRegisterCode.length()-3)) + 1;
			registerCode = str + String.format("%03d", registerCodeIndex);
		}
		return registerCode;
	}
	
	/**
	 * 保存或更新备案查询
	 */
	@Override
	@Transactional
	public RecordReviewVo saveOrUpdate(RecordReviewVo recordReviewVo,String path,String fileName) throws ServiceException{
		
		Long id = recordReviewVo.getId();
		ReviewResult   reviewResult = recordReviewVo.getReviewResult();
		//更新
		if(id != null ){
			//更新备案审查的审查日期，审查结果与存在的问题类型
			RecordReview  recordReview = recordReviewDao.findById(id);
			if( recordReview != null){
				Stage stage = recordReview.getNormativeFile().getStage();
				if(!stage.equals(Stage.RECORD_REVIEW)){
					throw new ServiceException("已存在下游业务，不可修改");
				}
				if( reviewResult != null ){
					NormativeFile  normativeFile = recordReview.getNormativeFile();
					
					normativeFile.setReviewDate(recordReviewVo.getRecordReviewDate());
					recordReview.setRecordReviewDate(recordReviewVo.getRecordReviewDate());
					recordReview.setReviewResult(recordReviewVo.getReviewResult());
					recordReview.setDecUnitOop(recordReviewVo.getDecUnitOop());
					recordReview.setDecProcedureOop(recordReviewVo.getDecProcedureOop());
					recordReview.setContentOop(recordReviewVo.getContentOop());
					recordReview.setDecTechHasDefects(recordReviewVo.getDecTechHasDefects());
					recordReview.setOthers(recordReviewVo.getOthers());
				}else {
					throw new ServiceException("请录入审查结果！");
				}
					WordUtils.htmlToWord(path, fileName, recordReviewVo.getReviewOpinionPaper());
					return  RecordReviewVo.createVo(recordReview);
			}else {
				throw new ServiceException("数据错误，ID为：" + id);
			}
		}
		
		//新增保存
		RecordReview   recordReview = this.coverToRecordReview(recordReviewVo);
		NormativeFile  normativeFile = recordReview.getNormativeFile();
		if( normativeFile != null){
			normativeFile.setRegisterCode(recordReviewVo.getRegisterCode());
			normativeFile.setReviewDate(recordReviewVo.getRecordReviewDate());
			normativeFile.setRegisterDate(recordReviewVo.getRecordDate());
			normativeFile.setStage(Stage.RECORD_REVIEW);
			normativeFile =  normativeFileDao.save(normativeFile);
		}else {
			throw new ServiceException("数据错误，ID为"+ id);
		}
		recordReview = recordReviewDao.save(recordReview);
		WordUtils.htmlToWord(path, fileName, recordReviewVo.getReviewOpinionPaper());
		return  RecordReviewVo.createVo(recordReview);
	}

	/**
	 * 通过Id删除
	 */
	@Override
	@Transactional
	public boolean delete(Long id,String path) throws ServiceException{
		RecordReview  recordReview = recordReviewDao.findById(id);
		if( recordReview != null){ 
			Stage stage = recordReview.getNormativeFile().getStage();
			if(!stage.equals(Stage.RECORD_REVIEW)){
				throw new ServiceException("已存在下游业务，不可删除");
			}
			
			NormativeFile  normativeFile  = recordReview.getNormativeFile();
			if( normativeFile != null){
				normativeFile.setRegisterCode(null);
				normativeFile.setReviewDate(null);
				normativeFile.setRegisterDate(null);
				normativeFile.setStage(Stage.RECORD_REQUEST);
				normativeFile =  normativeFileDao.save(normativeFile);
			}else {
				throw new ServiceException("数据错误，ID为：" + id);
			}
			recordReviewDao.delete(id);
			String filePath = WordUtils.getFilePath(path, NormativeFileVo.createVo(normativeFile), Stage.RECORD.toString());
			String fileName = NormativeFileVo.createVo(normativeFile).getName() + "(" + SysUtil.STAGE_LEGAL_REVIEWOPINIONPAPER + ")" + SysUtil.EXTENSION_NAME;
			filePath = filePath + File.separator + fileName;
			WordUtils.deleteWord(filePath);
			return true;
		}else {
			throw new ServiceException("数据错误，ID为：" + id);
		}
	}

	@Override
	public boolean submit(RecordReviewVo recordReviewVo){

		return false;
	}

	@Override
	public boolean approve(RecordReviewVo recordReviewVo){

		return false;
	}

	@Override
	public boolean unApprove(RecordReviewVo recordReviewVo){

		return false;
	}

	@Override
	public boolean flow(RecordReviewVo recordReviewVo){

		return false;
	}
	
	/**
	 * 查询备案审查
	 * @param page
	 * @param size
	 * @return
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<RecordReviewVo> find(int page, int size, String name, Set<Long> orgIds){ 
		
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "recordReviewDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<RecordReview> pages;
		if(StringUtils.isEmpty(name)){
			pages = recordReviewDao.findAll(orgIds, pageable);
		}else{
			pages = recordReviewDao.findLikeName("%" +name+"%", orgIds, pageable);
		}
		List<RecordReviewVo> volist = RecordReviewVo.createVoList(pages.getContent());
		Page<RecordReviewVo> pageVo = new PageImpl<RecordReviewVo>(volist, pageable, pages.getTotalElements());
		return pageVo;
	}

	/**
	 * 备案登记
	 */
	@Override
	@Transactional
	public RecordReviewVo register(RecordReviewVo recordReviewVo) throws ServiceException{
		Long id = recordReviewVo.getId();
		//更新
		if( id != null ){
			//更新备案审查的审查日期，审查结果与存在的问题类型
			RecordReview  recordReview = recordReviewDao.findById(id);
			if( recordReview != null){
				
				recordReview.setRegisterDate(recordReviewVo.getRegisterDate());
				recordReview.setRegisterCode(recordReviewVo.getRegisterCode());
			}
			
			NormativeFile   normativeFile  =  recordReview.getNormativeFile();
			if( normativeFile != null){
				normativeFile.setRegisterCode(recordReviewVo.getRegisterCode());
				normativeFile.setRegisterDate(recordReviewVo.getRegisterDate());
			}else {
				throw new ServiceException("数据错误，ID为：" + id);
			}
			return   RecordReviewVo.createVo(recordReview);
		}
		
		//新增保存
		RecordReview  recordReview = this.coverToRecordReview(recordReviewVo);
		NormativeFile  normativeFile =  normativeFileDao.findOne(recordReviewVo.getNormativeFile().getId());
		
		if( normativeFile != null){
			normativeFile.setRegisterCode(recordReviewVo.getRegisterCode());
			normativeFile.setRegisterDate(recordReviewVo.getRegisterDate());	
			normativeFile.setStage(Stage.RECORD_REGISTER);
			normativeFileDao.save(normativeFile); 
		}else {
			throw new ServiceException("数据错误，ID为：" + id);
		}
		recordReviewDao.save(recordReview);
		return   RecordReviewVo.createVo(recordReview);
	}

	/**
	 * 报备
	 */
	@Override
	@Transactional
	public RecordReviewVo send(RecordReviewVo recordReviewVo) throws ServiceException{
		Long id = recordReviewVo.getId();
		//更新
		if( id != null ){
			//更新备案审查的审查日期，审查结果与存在的问题类型
			RecordReview  recordReview = recordReviewDao.findById(id);
			if( recordReview != null){
				recordReview.setRecordUnit(organizationDao.findOne(recordReviewVo.getRecordUnit().getId()));
			}
			return   RecordReviewVo.createVo(recordReview);
		}
		
		//新增保存
		RecordReview  recordReview = this.coverToRecordReview(recordReviewVo);
		recordReviewDao.save(recordReview);
		return   RecordReviewVo.createVo(recordReview);
	}

	@Override
	@Transactional(readOnly = true)
	public RecordReviewVo findRecordReviewByName(String name){
		
		return RecordReviewVo.createVo(recordReviewDao.findRecordReviewByName(name));
	}

	@Override
	@Transactional(readOnly = true)
	public RecordReviewVo findById(Long id){
		
		return RecordReviewVo.createVo(recordReviewDao.findById(id));
	}

	@Override
	@Transactional(readOnly = true)
	public RecordReviewVo findByNorId(Long id){ 
		
		return RecordReviewVo.createVo(recordReviewDao.findByNorId(id));
	}

	public RecordReview coverToRecordReview( RecordReviewVo  recordReviewVo) {// 将Vo对象转换为实体对象
		RecordReview recordReview = new RecordReview();
		recordReview.setContentOop(recordReviewVo.getContentOop());
		recordReview.setDecisionMakingUnit(organizationDao.findOne(recordReviewVo.getDecisionMakingUnit().getId()));
		recordReview.setDecisionMakingUnitClerk(userDao.findOne(recordReviewVo.getDecisionMakingUnitClerk().getId()));
		recordReview.setDecisionMakingUnitLeader(userDao.findOne(recordReviewVo.getDecisionMakingUnitLeader().getId()));
		recordReview.setDecProcedureOop(recordReviewVo.getDecProcedureOop());
		recordReview.setDecTechHasDefects(recordReviewVo.getDecTechHasDefects());
		recordReview.setDecUnitOop(recordReviewVo.getDecUnitOop());
		recordReview.setDraftingInstruction(recordReviewVo.getDraftingInstruction());
		recordReview.setId(recordReviewVo.getId());
		recordReview.setLegalBasis(recordReviewVo.getLegalBasis());
		recordReview.setLegalDoc(recordReviewVo.getLegalDoc());
		recordReview.setName(recordReviewVo.getName());
		recordReview.setNormativeFile(normativeFileDao.findOne(recordReviewVo.getNormativeFile().getId()));
		recordReview.setOthers(recordReviewVo.getOthers());
		recordReview.setRecordReport(recordReviewVo.getRecordReport());
		recordReview.setRecordReviewDate(recordReviewVo.getRecordReviewDate());
		
		if( recordReviewVo.getRecordUnit() != null){
			recordReview.setRecordUnit(organizationDao.findOne(recordReviewVo.getRecordUnit().getId()));
		}
		
		recordReview.setRecordRevUnit(organizationDao.findOne(recordReviewVo.getRecordRevUnit().getId()));
		recordReview.setRecordRevUnitClerk(userDao.findOne(recordReviewVo.getRecordRevUnitClerk().getId()));
		recordReview.setRecordRevUnitLeader(userDao.findOne(recordReviewVo.getRecordRevUnitLeader().getId()));
		recordReview.setRegisterDate(recordReviewVo.getRegisterDate());
		recordReview.setRegisterCode(recordReviewVo.getRegisterCode());
		recordReview.setReviewOpinionPaper(recordReviewVo.getReviewOpinionPaper());
		recordReview.setReviewResult(recordReviewVo.getReviewResult());
		recordReview.setStatus(recordReviewVo.getStatus());
		return recordReview;
	}
}
