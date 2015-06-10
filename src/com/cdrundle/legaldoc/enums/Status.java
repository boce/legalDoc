package com.cdrundle.legaldoc.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  XuBao
 *状态枚举
 * 2014年6月17日
 */
public enum Status
{

    /**
     * 开立
     */
    OPEN("开立"),
    /**
     * 提交
     */
    SUBMIT("提交"),
    /**
     * 审核
     */
    APPROVE("审核"),
    
    /**
     * 弃审
     */
    UNAPPROVE("弃审");
    
    private final String title;

	private Status(String title) {
		this.title = title;
	}

	public String toString() {
		return this.title;
	}

	public static Status get(String str) {
		for (Status e : values()) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static Status getByName(String str) {
		for (Status e : values()) {
			if (e.name().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static List<Map<String, Object>> toList() {
		List<Map<String, Object>> list = new ArrayList<>();
		for (Status e : values()) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", e.name());
			map.put("title", e.toString());
			list.add(map);
		}
		return list;
	}

	public static Map<String, String> toMap(){
		Map<String, String> map = new LinkedHashMap<>();
		for (Status e : values()) {
			map.put(e.name(), e.toString());
		}
		return map;
	}
}
