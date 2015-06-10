package com.cdrundle.legaldoc.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author gang.li 延期结果枚举
 */
public enum AssessResult {

	/**
	 * 直接延期
	 */
	DIRECT_DELAY("直接延期"),
	/**
	 * 修订延期
	 */
	MODIFY_DELAY("修订延期"),

	/**
	 * 撤销
	 */
	REVOKE("撤销");

	private final String title;

	private AssessResult(String title) {
		this.title = title;
	}

	public String toString() {
		return this.title;
	}

	public static AssessResult get(String str) {
		for (AssessResult e : values()) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static AssessResult getByName(String str) {
		for (AssessResult e : values()) {
			if (e.name().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static List<Map<String, Object>> toList() {
		List<Map<String, Object>> list = new ArrayList<>();
		for (AssessResult e : values()) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", e.name());
			map.put("title", e.toString());
			list.add(map);
		}
		return list;
	}

	public static Map<String, String> toMap(){
		Map<String, String> map = new LinkedHashMap<>();
		for (AssessResult e : values()) {
			map.put(e.name(), e.toString());
		}
		return map;
	}
}
