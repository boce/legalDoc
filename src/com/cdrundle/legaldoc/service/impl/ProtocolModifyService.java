package com.cdrundle.legaldoc.service.impl;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.dao.IOrganizationDao;
import com.cdrundle.legaldoc.dao.IProtocolModifyDao;
import com.cdrundle.legaldoc.dao.IUserDao;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.entity.ProtocolModify;
import com.cdrundle.legaldoc.enums.Stage;
import com.cdrundle.legaldoc.exception.ServiceException;
import com.cdrundle.legaldoc.service.IProtocolModifyService;
import com.cdrundle.legaldoc.util.SysUtil;
import com.cdrundle.legaldoc.util.WordUtils;
import com.cdrundle.legaldoc.vo.NormativeFileVo;
import com.cdrundle.legaldoc.vo.ProtocolModifyVo;

/**
 * @author XuBao 草案修改 2014年6月19日
 */
@Service
public class ProtocolModifyService implements IProtocolModifyService {

	@Autowired
	private IProtocolModifyDao protocolModifyDao;
	@Autowired
	private IOrganizationDao organizationDao;
	@Autowired
	private IUserDao userDao;
	@Autowired
	private INormativeFileDao normativeFileDao;

	/**
	 * 保存或更新草案修改单
	 */
	@Override
	@Transactional
	public ProtocolModifyVo saveOrUpdate(ProtocolModifyVo protocolModifyVo, String path, String fileName, Boolean isConfirm) throws ServiceException {

		// 更新草案修改的 修改内容
		Long id = protocolModifyVo.getId();
		if (id != null) {
			ProtocolModify protocolModify = protocolModifyDao.findOne(id);

			if (protocolModify != null) {
				Stage stage = protocolModify.getNormativeFile().getStage();
				if (!stage.equals(Stage.DELIBERATION_MODIFY)) {
					throw new ServiceException("已存在下游业务，不可修改");
				}
				protocolModify.setContent(protocolModifyVo.getContent());
			} else {
				throw new ServiceException("数据错误，ID为：" + id);
			}
		} else {
			// 新增保存
			ProtocolModify protocolModify = this.coverToProtocolModify(protocolModifyVo);
			NormativeFile normativeFile = protocolModify.getNormativeFile();
			if (Stage.DELIBERATION_MODIFY.equals(normativeFile.getStage())) {
				throw new ServiceException("保存失败，该草案不需要修改！");
			}
			// 保存更新规范性文件的审议日期
			if (normativeFile != null) {
				normativeFile.setStage(Stage.DELIBERATION_MODIFY);
				normativeFile = normativeFileDao.save(normativeFile);
			}
		}
		ProtocolModify protocolModify = this.coverToProtocolModify(protocolModifyVo);
		NormativeFile normativeFile = protocolModify.getNormativeFile();
		String filePath = WordUtils.getFilePath(path, NormativeFileVo.createVo(normativeFile), Stage.DELIBERATION.toString());
		fileName = "";
		if (!isConfirm) {
			fileName = NormativeFileVo.createVo(normativeFile).getName() + "(" + SysUtil.STAGE_LEGAL_CONTENT + ")" + SysUtil.EXTENSION_NAME;
		} else {
			fileName = NormativeFileVo.createVo(normativeFile).getName() + SysUtil.EXTENSION_NAME;
		}
		protocolModify = protocolModifyDao.save(protocolModify);
		WordUtils.htmlToWord(filePath, fileName, protocolModifyVo.getContent());
		return ProtocolModifyVo.createVo(protocolModify);
	}

	/**
	 * 删除草案修改单
	 */
	@Override
	@Transactional
	public boolean delete(Long id, String path) throws ServiceException {

		// 删除草案修改
		ProtocolModify protocolModify = protocolModifyDao.findOne(id);
		if (protocolModify != null) {
			Stage stage = protocolModify.getNormativeFile().getStage();
			if (!stage.equals(Stage.DELIBERATION_MODIFY)) {
				throw new ServiceException("已存在下游业务，不可删除");
			}
			NormativeFile normativeFile = protocolModify.getNormativeFile();
			if (normativeFile != null) {
				normativeFile.setStage(Stage.DELIBERATION_PROTOCOL);
				normativeFile = normativeFileDao.save(normativeFile);
			} else {
				throw new ServiceException("数据错误，ID为：" + id);
			}
			protocolModifyDao.delete(protocolModify);
			String filePath = WordUtils.getFilePath(path, NormativeFileVo.createVo(normativeFile), Stage.DELIBERATION.toString());
			String fileName = NormativeFileVo.createVo(normativeFile).getName() + "(" + SysUtil.STAGE_LEGAL_CONTENT + ")" + SysUtil.EXTENSION_NAME;
			filePath = filePath + File.separator + fileName;
			WordUtils.deleteWord(filePath);
			return true;
		} else {
			throw new ServiceException("数据错误，ID为：" + id);
		}
	}

	/**
	 * 查找草案修改单
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ProtocolModifyVo> find(int page, int size, String name, Set<Long> orgIds) {

		Pageable pageable = new PageRequest(page, size);
		Page<ProtocolModify> pages;
		if (StringUtils.isEmpty(name)) {
			pages = protocolModifyDao.findAll(orgIds, pageable);
		} else {
			pages = protocolModifyDao.findLikeName("%" + name + "%", orgIds, pageable);
		}
		List<ProtocolModifyVo> volist = ProtocolModifyVo.creatVoList(pages.getContent());
		Page<ProtocolModifyVo> pageVo = new PageImpl<ProtocolModifyVo>(volist, pageable, pages.getTotalElements());
		return pageVo;
	}

	/**
	 * 通过文件名查找草案修改单
	 */
	@Override
	@Transactional(readOnly = true)
	public ProtocolModifyVo findProtocolModifyByName(String name) {

		return ProtocolModifyVo.createVo(protocolModifyDao.findProtocolModifyByName(name));
	}

	/**
	 * 通过id查找草案修改单
	 */
	@Override
	@Transactional(readOnly = true)
	public ProtocolModifyVo findById(Long id) {

		return ProtocolModifyVo.createVo(protocolModifyDao.findOne(id));
	}

	/**
	 * 通过规范性文件id查找草案修改单
	 */
	@Override
	@Transactional(readOnly = true)
	public ProtocolModifyVo findByNorId(Long id) {

		return ProtocolModifyVo.createVo(protocolModifyDao.findByNorId(id));
	}

	public ProtocolModify coverToProtocolModify(ProtocolModifyVo protocolModifyVo) {
		ProtocolModify protocolModify = new ProtocolModify();
		protocolModify.setId(protocolModifyVo.getId());
		protocolModify.setContent(protocolModifyVo.getContent());
		protocolModify.setDeliberationComment(protocolModifyVo.getDeliberationComment());
		protocolModify.setDraftingUnit(organizationDao.findOne(protocolModifyVo.getDraftingUnit().getId()));
		protocolModify.setDraftingUnitClerk(userDao.findOne(protocolModifyVo.getDraftingUnitClerk().getId()));
		protocolModify.setDraftingUnitLeader(userDao.findOne(protocolModifyVo.getDraftingUnitLeader().getId()));
		protocolModify.setName(protocolModifyVo.getName());
		protocolModify.setNormativeFile(normativeFileDao.findOne(protocolModifyVo.getNormativeFile().getId()));
		return protocolModify;
	}

	@Override
	public ProtocolModifyVo confirm(ProtocolModifyVo protocolModifyVo) {

		return null;
	}
}
