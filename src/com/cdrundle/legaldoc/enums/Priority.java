package com.cdrundle.legaldoc.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  XuBao
 *优先级枚举
 * 2014年6月17日
 */
public enum Priority
{
	/**
     * 一般
     */
    NORMAL("一般"),
    /**
     * 紧急
     */
    EMERGENCY("紧急"),
    /**
     * 特急
     */
    EXTRA_URGENT("特急"),
    
    /**
     * 特提
     */
    VERSION("特提");
    private final String title;

	private Priority(String title) {
		this.title = title;
	}

	public String toString() {
		return this.title;
	}

	public static Priority get(String str) {
		for (Priority e : values()) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static Priority getByName(String str) {
		for (Priority e : values()) {
			if (e.name().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static List<Map<String, Object>> toList() {
		List<Map<String, Object>> list = new ArrayList<>();
		for (Priority e : values()) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", e.name());
			map.put("title", e.toString());
			list.add(map);
		}
		return list;
	}
    
	public static Map<String, String> toMap(){
		Map<String, String> map = new LinkedHashMap<>();
		for (Priority e : values()) {
			map.put(e.name(), e.toString());
		}
		return map;
	}
}
