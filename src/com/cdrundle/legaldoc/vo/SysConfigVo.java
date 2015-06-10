package com.cdrundle.legaldoc.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cdrundle.legaldoc.entity.SysConfig;


/**
 * 系统参数配置信息
 * 
 * @author xiaokui.li
 * 
 */
public class SysConfigVo{

	/**
	 * 编号
	 */
	private Long id;

	/**
	 * 编码
	 */
	private String code;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 值
	 */
	private String value;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 是否启用
	 */
	private Boolean isUsed;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public static SysConfigVo createVo(SysConfig sysConfig){
		SysConfigVo vo = new SysConfigVo();
		if(sysConfig == null){
			return null;
		}
		vo.setId(sysConfig.getId());
		vo.setCode(sysConfig.getCode());
		vo.setName(sysConfig.getName());
		vo.setValue(sysConfig.getValue());
		vo.setDescription(sysConfig.getDescription());
		vo.setIsUsed(sysConfig.getIsUsed());
		return vo;
	}
	
	public static List<SysConfigVo> createVoList(List<SysConfig> sysConfigList){
		List<SysConfigVo> voList = new ArrayList<>();
		if(sysConfigList == null || sysConfigList.isEmpty()){
			return null;
		}
		for (Iterator<SysConfig> iterator = sysConfigList.iterator(); iterator.hasNext();) {
			voList.add(createVo(iterator.next()));
		}
		return voList;
	}
}
