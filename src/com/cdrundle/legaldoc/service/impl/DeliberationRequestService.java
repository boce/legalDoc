package com.cdrundle.legaldoc.service.impl;

import java.io.File;
import java.util.ArrayList;
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

import com.cdrundle.legaldoc.dao.IDeliberationRequestDao;
import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IProtocolDeliberationlDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.DeliberationRequest;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IDeliberationRequestService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.DeliberationRequestVo;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.security.WebPlatformUser;

/**
 * @author XuBao 审议报请 2014年6月10日
 */
@Service
public class DeliberationRequestService implements IDeliberationRequestService{
	@Autowired
	private IDeliberationRequestDao deliberationRequestDao;
	@Autowired
	private INormativeFileDao normativeFileDao;
	@Autowired
	private IOrganizationDao organizationDao;
	@Autowired
	private IProtocolDeliberationlDao   protocolDeliberationlDao;

	@Autowired
	private IUserDao userDao;
	
	/**
	 * 保存或者更新
	 */
	@Override
	@Transactional
	public DeliberationRequestVo saveOrUpdate(DeliberationRequestVo deliberationRequestVo,
			String path, String fileName) throws ServiceException{
		
		Long id = deliberationRequestVo.getId();
			// 更新
			if (id != null){
				DeliberationRequest deliberationRequest = deliberationRequestDao.findOne(id);

				// 更新审议报请的审议单位、报请日期以及审议请示
				if (deliberationRequest != null){

					Stage  stage = deliberationRequest.getNormativeFile().getStage();
					
					if( !stage.equals(Stage.DELIBERATION_REQUEST)){
						throw new ServiceException("已存在下游业务,不允许修改");
					}
					
					
					String deliberationUnit = deliberationRequestVo.getDeliberationUnit();
					if (deliberationUnit != null){
						deliberationRequest.setDeliberationUnit(deliberationUnit);
						deliberationRequest.setRequestDate(deliberationRequestVo.getRequestDate());
						deliberationRequest.setReviewInstruction(deliberationRequestVo.getReviewInstruction());
					
					} else{
						throw new ServiceException("请录入审议单位！");
					}

					// 更新规范新文件的审议单位和报请日期
					NormativeFile normativeFile = deliberationRequest.getNormativeFile();
					if (normativeFile != null){
						normativeFile.setDelUnit(deliberationRequestVo.getDeliberationUnit());
						normativeFile.setRequestDate(deliberationRequestVo.getRequestDate());
					
					} else{
						throw new ServiceException("数据错误，ID为：" + id);
					}
					WordUtils.htmlToWord(path, fileName, deliberationRequestVo.getReviewInstruction());
					return DeliberationRequestVo.createVo(deliberationRequest);
				
				} else{
					throw new ServiceException("数据错误，ID为：" + id);
				}
			}
			// 新增
			DeliberationRequest deliberationRequest = this.coverToDeliberationRequest(deliberationRequestVo);

			// 更新规范新文件的审议单位和报请日期
			NormativeFile normativeFile = deliberationRequest.getNormativeFile();
			Stage stage = normativeFile.getStage();
			if(Stage.LEGAL_REVIEW_REVIEW.equals(stage)){
				throw new ServiceException("保存失败，请返回送审稿修改！");
			}
			normativeFile.setDelUnit(deliberationRequestVo.getDeliberationUnit());
			normativeFile.setRequestDate(deliberationRequestVo.getRequestDate());
			normativeFile.setStage(Stage.DELIBERATION_REQUEST);
			normativeFile = normativeFileDao.save(normativeFile);
			deliberationRequest = deliberationRequestDao.save(deliberationRequest);
			WordUtils.htmlToWord(path, fileName, deliberationRequestVo.getReviewInstruction());
			return DeliberationRequestVo.createVo(deliberationRequest);
				
	}
	
	/**
	 * 删除审议报请
	 */
	@Override
	@Transactional
	public boolean delete(Long id, String path) throws ServiceException{
		
		DeliberationRequest deliberationRequest = deliberationRequestDao.findOne(id);
		
		// 删除
		if (deliberationRequest != null){
			
			Stage  stage = deliberationRequest.getNormativeFile().getStage();
			
			if( !stage.equals(Stage.DELIBERATION_REQUEST)){
				throw new ServiceException("已存在下游业务,不允许删除");
			}
			
			// 更新规范性文件的审议单位与报请日期为空
			NormativeFile normativeFile = deliberationRequest.getNormativeFile();
			if (normativeFile != null){
				normativeFile.setDelUnit(null);
				normativeFile.setRequestDate(null);
				normativeFile.setStage(Stage.LEGAL_REVIEW_MODIFY);
				normativeFile = normativeFileDao.save(normativeFile);
			
			} else{
				throw new ServiceException("数据错误，ID为：" + id);
			}
			deliberationRequestDao.delete(deliberationRequest);
			String filePath = WordUtils.getFilePath(path, NormativeFileVo.createVo(normativeFile), Stage.DELIBERATION.toString());
			String fileName = NormativeFileVo.createVo(normativeFile).getName() + "(" + SysUtil.STAGE_LEGAL_REVIEWINSTRUCTION + ")" + SysUtil.EXTENSION_NAME;
			filePath = filePath + File.separator + fileName;
			WordUtils.deleteWord(filePath);
			return true;

		} else{
			throw new ServiceException("数据错误，ID为：" + id);
		}
	}

	/**
	 * 查找审议报请
	 */
	@Override
	@Transactional(readOnly=true)
	public Page<DeliberationRequestVo> find(int page, int size,String name,Set<Long>  orgIds){
		
		// 得到当前登录用户信息
		WebPlatformUser userDetail = SysUtil.getLoginInfo();
		String unionDrtUnit = userDetail.getOrgId();
		
		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "requestDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		
		Page<DeliberationRequest> pages ;
		if (StringUtils.isEmpty(name)){
			 pages = deliberationRequestDao.findAll(orgIds,"\"" +unionDrtUnit + "\"", pageable);
		}else{
			 pages = deliberationRequestDao.findLikeName(orgIds, "%\"" +unionDrtUnit + "\"%", "%" + name + "%", pageable);
		}
		
		List<DeliberationRequestVo> volist = DeliberationRequestVo.createVoList(pages.getContent());
		Page<DeliberationRequestVo> pageVo = new PageImpl<DeliberationRequestVo>(volist, pageable, pages.getTotalElements());
		return pageVo;
	}

	@Transactional
	public boolean submit(DeliberationRequestVo deliberationRequestVo)
	{
		return false;

	}

	@Transactional
	public boolean approve(DeliberationRequestVo deliberationRequestVo)
	{
		return false;

	}

	@Transactional
	public boolean unApprove(DeliberationRequestVo deliberationRequestVo)
	{
		return false;

	}

	@Transactional
	public boolean flow(DeliberationRequestVo deliberationRequestVo)
	{
		return false;

	}

	/**
	 * 通过文件名查找审议报请
	 */
	@Override
	@Transactional(readOnly=true)
	public DeliberationRequestVo findDeliberationRequestByName(String name){

		return DeliberationRequestVo.createVo(deliberationRequestDao.findDeliberationRequestByName(name));
	}

	/**
	 * 通过Id查找审议报请
	 */
	@Override
	@Transactional(readOnly=true)
	public DeliberationRequestVo findDeliberationRequestById(Long id){

		return DeliberationRequestVo.createVo(deliberationRequestDao.findOne(id));
	}

	/**
	 * 将Vo对象的值复制到实体中
	 * 
	 * @return DeliberationRequest
	 */
	private DeliberationRequest coverToDeliberationRequest(DeliberationRequestVo deliberationRequestVo){// 将Vo对象转换为实体对象
		
		DeliberationRequest deliberationRequest = new DeliberationRequest();
		deliberationRequest.setId(deliberationRequestVo.getId());
		deliberationRequest.setDeliberationUnit(deliberationRequestVo.getDeliberationUnit());
		deliberationRequest.setDraftingInstruction(deliberationRequestVo.getDraftingInstruction());
		deliberationRequest.setDraftingUnit(organizationDao.findOne(deliberationRequestVo.getDraftingUnit().getId()));
		deliberationRequest.setDraftingUnitClerk(userDao.findOne(deliberationRequestVo.getDraftingUnitClerk().getId()));
		deliberationRequest.setDraftingUnitLeader(userDao.findOne(deliberationRequestVo.getDraftingUnitLeader().getId()));
		deliberationRequest.setName(deliberationRequestVo.getName());
		deliberationRequest.setNormativeFile(normativeFileDao.findOne(deliberationRequestVo.getNormativeFile().getId()));
		deliberationRequest.setProtocol(deliberationRequestVo.getProtocol());
		deliberationRequest.setRequestComments(deliberationRequestVo.getRequestComments());
		deliberationRequest.setReviewInstruction(deliberationRequestVo.getReviewInstruction());
		deliberationRequest.setRequestDate(deliberationRequestVo.getRequestDate());
		deliberationRequest.setReviewComments(deliberationRequestVo.getReviewComments());
		deliberationRequest.setStatus(deliberationRequestVo.getStatus());
		deliberationRequest.setUnionDraUnit(deliberationRequestVo.getUnionDraUnit());
		deliberationRequest.setUnionDraUnitClerk(deliberationRequestVo.getUnionDraUnitClerk());
		deliberationRequest.setUnionDraUnitLeader(deliberationRequestVo.getUnionDraUnitLeader());
		return deliberationRequest;
	}

	/**
	 * 通过规范性文件Id查找审议报请
	 */
	@Override
	@Transactional(readOnly=true)
	public DeliberationRequestVo findByNorId(long id){
		
		return DeliberationRequestVo.createVo(deliberationRequestDao.findByNorId(id));
	}


}
