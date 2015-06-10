package com.cdrundle.legaldoc.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cdrundle.legaldoc.vo.NorFileAdjustVo;
import com.cdrundle.legaldoc.vo.NorFileQueryResultVo;
import com.cdrundle.legaldoc.vo.NorFileQueryVo;

public interface INorFileAdjustService {
	
	public List<NorFileAdjustVo> saveOrUpdate(List<NorFileAdjustVo> voList);
	
	public Page<NorFileQueryResultVo> findNorFilesForAdjust(NorFileQueryVo queryVo,int page,int size);

}
