package com.cdrundle.legaldoc.service.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdrundle.legaldoc.dao.INormativeFileDao;
import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.service.INorFileAdjustService;
import com.cdrundle.legaldoc.service.INormativeFileService;
import com.cdrundle.legaldoc.vo.NorFileAdjustVo;
import com.cdrundle.legaldoc.vo.NorFileQueryResultVo;
import com.cdrundle.legaldoc.vo.NorFileQueryVo;

@Service
public class NorFileAdjustService implements INorFileAdjustService {
	@Autowired
	private INormativeFileDao normativeFileDao;

	@Autowired
	private INormativeFileService normativeFileService;

	@Override
	@Transactional
	public List<NorFileAdjustVo> saveOrUpdate(List<NorFileAdjustVo> voList) {
		Iterator<NorFileAdjustVo> itr = voList.iterator();
		while (itr.hasNext()) {
			NorFileAdjustVo vo = itr.next();
			NormativeFile nf = normativeFileDao.findOne(vo.getId());

			nf.setStatus(vo.getStatus());
			nf.setInvalidReason(vo.getInvalidReason());

			normativeFileDao.save(nf);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<NorFileQueryResultVo> findNorFilesForAdjust(NorFileQueryVo queryVo, int page, int size) {
		return normativeFileService.findAllForAdjustAndCleanup(queryVo, page, size);
	}

}
