package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.enums.FileStatus;

public class NorFileQueryResultVo{
	
	private Long id;
	
	private OrgShortVo decUnit;

	private OrgShortVo drtUnit;

	private String name;

	private FileStatus status;

	private String invalidReason;
	
	private String publishNo;
	
	private Date publishDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public OrgShortVo getDecUnit() {
		return decUnit;
	}

	public void setDecUnit(OrgShortVo decUnit) {
		this.decUnit = decUnit;
	}

	public OrgShortVo getDrtUnit() {
		return drtUnit;
	}

	public void setDrtUnit(OrgShortVo drtUnit) {
		this.drtUnit = drtUnit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FileStatus getStatus() {
		return status;
	}

	public void setStatus(FileStatus status) {
		this.status = status;
	}

	public String getInvalidReason() {
		return invalidReason;
	}

	public void setInvalidReason(String invalidReason) {
		this.invalidReason = invalidReason;
	}

	public String getPublishNo() {
		return publishNo;
	}

	public void setPublishNo(String publishNo) {
		this.publishNo = publishNo;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public static NorFileQueryResultVo createVo(NormativeFile normativeFile) {
		NorFileQueryResultVo queryResultVo = new NorFileQueryResultVo();
		queryResultVo.setDecUnit(OrgShortVo.createVo(normativeFile
				.getDecUnit()));
		queryResultVo.setDrtUnit(OrgShortVo.createVo(normativeFile
				.getDrtUnit()));
		queryResultVo.setPublishNo(normativeFile.getPublishNo());
		queryResultVo.setId(normativeFile.getId());
		queryResultVo.setInvalidReason(normativeFile.getInvalidReason());
		queryResultVo.setName(normativeFile.getName());
		queryResultVo.setPublishDate(normativeFile.getPublishDate());
		queryResultVo.setStatus(normativeFile.getStatus());
		return queryResultVo;
	}

	public static List<NorFileQueryResultVo> createVoList(
			List<NormativeFile> normativeFileList) {
		// 将实体集合转换为Vo集合
		List<NorFileQueryResultVo> queryResultVoList = new ArrayList<NorFileQueryResultVo>();
		for (NormativeFile normativeFile : normativeFileList) {
			// 将SetDelRequestList集合里面的SetDelRequest一个一个的遍历出来
			queryResultVoList.add(NorFileQueryResultVo.createVo(normativeFile));
			// 调用上面的createVo方法，将遍历来的SetDelRequest转换为SetDelRequestVo对象，并添加到SetDelRequestVoList
		}
		return queryResultVoList;
	}

}