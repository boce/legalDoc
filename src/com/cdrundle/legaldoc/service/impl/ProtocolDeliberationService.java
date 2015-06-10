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

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IProtocolDeliberationlDao;
import com.cdrundle.legaldoc.dao.IProtocolModifyDao;
import com.cdrundle.legaldoc.dao.ISignAndPublishDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.ProtocolDeliberation;
import com.cdrundle.legaldoc.entity.ProtocolModify;
import com.cdrundle.legaldoc.entity.SignAndPublish;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IProtocolDeliberationService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.ProtocolDeliberationVo;

/**
 * @author XuBao 草案审议 2014年6月19日
 */
@Service
public class ProtocolDeliberationService implements IProtocolDeliberationService {

	@Autowired
	private IProtocolDeliberationlDao protocolDeliberationlDao;
	@Autowired
	private INormativeFileDao normativeFileDao;
	@Autowired
	private IOrganizationDao organizationDao;
	@Autowired
	private IUserDao userDao;
	@Autowired
	private IProtocolModifyDao protocolModifyDao;
	@Autowired
	private ISignAndPublishDao signAndPublishDao;

	/**
	 * 保存或者草案审议单
	 */
	@Override
	@Transactional
	public ProtocolDeliberationVo saveOrUpdate(ProtocolDeliberationVo protocolDeliberationVo, String path, String fileName) throws ServiceException {

		Long id = protocolDeliberationVo.getId();

		// 更新草案审议和规范性文件
		if (id != null) {
			ProtocolDeliberation protocolDeliberation = protocolDeliberationlDao.findOne(id);
			// 更新草案审议的审议日期和审议意见
			if (protocolDeliberation != null) {
				NormativeFile normativeFile = protocolDeliberation.getNormativeFile();
				if(normativeFile == null){
					throw new ServiceException("数据错误，ID为：" + id);
				}
				if(protocolDeliberationVo.getIsNeedModify()){
					ProtocolModify protocolModify = protocolModifyDao.findByNorId(normativeFile.getId());
					if(protocolModify != null){
						throw new ServiceException("已存在下游业务,不允许修改");
					}
				}else{
					SignAndPublish signAndPublish = signAndPublishDao.findByNorId(normativeFile.getId());
					if(signAndPublish != null){
						throw new ServiceException("已存在下游业务,不允许修改");
					}
				}
				// 更新规范性文件的审议日期
				normativeFile.setDelDate(protocolDeliberationVo.getDeliberationDate());
				if(protocolDeliberationVo.getIsNeedModify()){
					normativeFile.setStage(Stage.DELIBERATION_PROTOCOL);
				}else{
					normativeFile.setStage(Stage.DELIBERATION_MODIFY);
				}
				normativeFileDao.save(normativeFile);
				
				protocolDeliberation.setDeliberationDate(protocolDeliberationVo.getDeliberationDate());
				protocolDeliberation.setDeliberationComment(protocolDeliberationVo.getDeliberationComment());
				protocolDeliberation.setIsNeedModify(protocolDeliberationVo.getIsNeedModify());
				protocolDeliberationlDao.save(protocolDeliberation);
				
				WordUtils.htmlToWord(path, fileName, protocolDeliberationVo.getDeliberationComment());
				return ProtocolDeliberationVo.createVo(protocolDeliberation);
			} else {
				throw new ServiceException("数据错误，ID为：" + id);
			}
		}
		// 新增保存
		ProtocolDeliberation protocolDeliberation = this.coverToProtocolDeliberation(protocolDeliberationVo);

		// 保存更新规范性文件的审议日期
		NormativeFile normativeFile = protocolDeliberation.getNormativeFile();
		if (normativeFile != null) {
			normativeFile.setDelDate(protocolDeliberationVo.getDeliberationDate());
			if(protocolDeliberationVo.getIsNeedModify()){
				normativeFile.setStage(Stage.DELIBERATION_PROTOCOL);
			}else{
				normativeFile.setStage(Stage.DELIBERATION_MODIFY);
			}
			normativeFile = normativeFileDao.save(normativeFile);
		} else {
			throw new ServiceException("数据错误，ID为：" + id);
		}
		// 保存草案审议
		protocolDeliberation = protocolDeliberationlDao.save(protocolDeliberation);
		
		//保存审议意见
		NormativeFileVo norFileVo = NormativeFileVo.createVo(normativeFile);
		String delFilePath = WordUtils.getFilePath(path, norFileVo, Stage.DELIBERATION.toString());
		WordUtils.htmlToWord(delFilePath, fileName, protocolDeliberationVo.getDeliberationComment());
		
		if(!protocolDeliberationVo.getIsNeedModify()){
			//从合法性审查目录获取草案
			String legalReviewFilePath = WordUtils.getFilePath(path, norFileVo, Stage.LEGAL_REVIEW.toString());
			String protocolFileName = norFileVo.getName() + "(" + SysUtil.STAGE_LEGAL_PROTOCOL + ")" + SysUtil.EXTENSION_NAME;
			String content = WordUtils.readFile(legalReviewFilePath + File.separator + protocolFileName);	//获取文档内容
			//将草案保存为正式文件
			String norFileName = norFileVo.getName() + SysUtil.EXTENSION_NAME;
			WordUtils.htmlToWord(delFilePath, norFileName, content.replaceFirst("\\(" + SysUtil.STAGE_LEGAL_PROTOCOL + "\\)", ""));
		}else{
			String norFileName = norFileVo.getName() + SysUtil.EXTENSION_NAME;
			File file = new File(delFilePath + File.separator + norFileName);
			if(file.exists()){
				WordUtils.deleteWord(delFilePath + File.separator + norFileName);
			}
		}
		return ProtocolDeliberationVo.createVo(protocolDeliberation);
	}

	/**
	 * 通过Id删除草案审议单
	 */
	@Override
	@Transactional
	public boolean delete(Long id, String path) throws ServiceException {
		// 删除
		ProtocolDeliberation protocolDeliberation = protocolDeliberationlDao.findOne(id);
		if (protocolDeliberation != null) {
			NormativeFile normativeFile = protocolDeliberation.getNormativeFile();
			if(protocolDeliberation.getIsNeedModify()){
				ProtocolModify protocolModify = protocolModifyDao.findByNorId(normativeFile.getId());
				if(protocolModify != null){
					throw new ServiceException("已存在下游业务,不允许修改");
				}
			}else{
				SignAndPublish signAndPublish = signAndPublishDao.findByNorId(normativeFile.getId());
				if(signAndPublish != null){
					throw new ServiceException("已存在下游业务,不允许修改");
				}
			}
			// 更新规范性文件的审议日期
			if (normativeFile != null) {
				normativeFile.setDelDate(null);
				normativeFile.setStage(Stage.DELIBERATION_REQUEST);
				normativeFile = normativeFileDao.save(normativeFile);
			}
			protocolDeliberationlDao.delete(protocolDeliberation);
			String filePath = WordUtils.getFilePath(path, NormativeFileVo.createVo(normativeFile), Stage.DELIBERATION.toString());
			String fileName = NormativeFileVo.createVo(normativeFile).getName() + "(" + SysUtil.STAGE_LEGAL_DELIBERATIONCOMMENT + ")"
					+ SysUtil.EXTENSION_NAME;
			filePath = filePath + File.separator + fileName;
			WordUtils.deleteWord(filePath);
			return true;
		} else {
			throw new ServiceException("数据错误，ID为：" + id);
		}

	}

	/**
	 * 查找草案审议
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ProtocolDeliberationVo> find(int page, int size, String name, Set<Long> orgIds) {

		List<Order> orders = new ArrayList<Order>();
		Order order = new Order(Direction.DESC, "deliberationDate");
		orders.add(order);
		Sort sort = new Sort(orders);
		Pageable pageable = new PageRequest(page, size, sort);
		Page<ProtocolDeliberation> pages;

		if (StringUtils.isEmpty(name)) {
			pages = protocolDeliberationlDao.findAll(orgIds, pageable);
		} else {
			pages = protocolDeliberationlDao.findLikeName("%" + name + "%", orgIds, pageable);
		}

		List<ProtocolDeliberationVo> volist = ProtocolDeliberationVo.createVoList(pages.getContent());
		Page<ProtocolDeliberationVo> pageVo = new PageImpl<ProtocolDeliberationVo>(volist, pageable, pages.getTotalElements());
		return pageVo;
	}

	/**
	 * 通过文件名查找草案审议单
	 */
	@Override
	@Transactional(readOnly = true)
	public ProtocolDeliberationVo findProtocolDeliberationByName(String name) {

		return ProtocolDeliberationVo.createVo(protocolDeliberationlDao.findProtocolDeliberationByName(name));
	}

	/**
	 * 通过Id查找草案审议单
	 */
	@Override
	@Transactional(readOnly = true)
	public ProtocolDeliberationVo findById(Long id) {

		return ProtocolDeliberationVo.createVo(protocolDeliberationlDao.findOne(id));
	}

	/**
	 * 通过规范性文件Id查找草案审议单
	 */
	@Override
	public ProtocolDeliberationVo findByNorId(Long id) {

		return ProtocolDeliberationVo.createVo(protocolDeliberationlDao.findByNorId(id));
	}

	/**
	 * 将Vo对象转换成实体
	 */
	public ProtocolDeliberation coverToProtocolDeliberation(ProtocolDeliberationVo protocolDeliberationVo) {// 将Vo对象转换为实体对象
		ProtocolDeliberation protocolDeliberation = new ProtocolDeliberation();
		protocolDeliberation.setId(protocolDeliberationVo.getId());
		protocolDeliberation.setDeliberationUnit(protocolDeliberationVo.getDeliberationUnit());
		protocolDeliberation.setDraftingInstruction(protocolDeliberationVo.getDraftingInstruction());
		protocolDeliberation.setDraftingUnit(organizationDao.findOne(protocolDeliberationVo.getDraftingUnit().getId()));
		protocolDeliberation.setDraftingUnitClerk(userDao.findOne(protocolDeliberationVo.getDraftingUnitClerk().getId()));
		protocolDeliberation.setDraftingUnitLeader(userDao.findOne(protocolDeliberationVo.getDraftingUnitLeader().getId()));
		protocolDeliberation.setName(protocolDeliberationVo.getName());
		protocolDeliberation.setNormativeFile(normativeFileDao.findOne(protocolDeliberationVo.getNormativeFile().getId()));
		protocolDeliberation.setProtocol(protocolDeliberationVo.getProtocol());
		protocolDeliberation.setRequestComments(protocolDeliberationVo.getRequestComments());
		protocolDeliberation.setReviewInstruction(protocolDeliberationVo.getReviewInstruction());
		protocolDeliberation.setDeliberationDate(protocolDeliberationVo.getDeliberationDate());
		protocolDeliberation.setReviewComments(protocolDeliberationVo.getReviewComments());
		protocolDeliberation.setDeliberationComment(protocolDeliberationVo.getDeliberationComment());
		protocolDeliberation.setIsNeedModify(protocolDeliberationVo.getIsNeedModify());
		return protocolDeliberation;
	}

}
