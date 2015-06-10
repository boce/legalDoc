package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.List;

import com.cdrundle.legaldoc.entity.NormativeFile;
import com.cdrundle.legaldoc.enums.FileStatus;

public class NorFileShortVo {

	private long id;
	private String name;
	private FileStatus fileStatus;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FileStatus getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(FileStatus fileStatus) {
		this.fileStatus = fileStatus;
	}

	public static NorFileShortVo createVo(NormativeFile normativeFile) {
		NorFileShortVo norFileShortVo = new NorFileShortVo();
		norFileShortVo.setId(normativeFile.getId());
		norFileShortVo.setName(normativeFile.getName());
		norFileShortVo.setFileStatus(normativeFile.getStatus());
		return norFileShortVo;
	}

	public static List<NorFileShortVo> createVoList(List<NormativeFile> normativeFileList) {// 将实体集合转换为Vo集合
		List<NorFileShortVo> normativeFileVoList = new ArrayList<NorFileShortVo>();
		for (NormativeFile normativeFile : normativeFileList) {
			normativeFileVoList.add(NorFileShortVo.createVo(normativeFile));
		}
		return normativeFileVoList;
	}

}
