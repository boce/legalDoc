package com.cdrundle.legaldoc.vo;

import java.util.List;
import java.util.Map;

public class TreeVo{

	private Long id;
	
	private String text;
	
	private List<TreeVo> children;
	
	private String state = "closed";
	
	private String iconCls;
	
	private Map<String, Object> attributes;
	
	private Integer displayOrder;
	
	public TreeVo() {
	};

	public TreeVo(Long id, String text, List<TreeVo> children, String state, String iconCls, Map<String, Object> attributes, Integer displayOrder) {
		this.id = id;
		this.text = text;
		this.children = children;
		this.state = state;
		this.iconCls = iconCls;
		this.attributes = attributes;
		this.displayOrder = displayOrder;
	};


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<TreeVo> getChildren() {
		return children;
	}

	public void setChildren(List<TreeVo> children) {
		this.children = children;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

}
