package com.cdrundle.legaldoc.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XuBao 审查结果枚举类型 2014年6月17日
 */
public enum ReviewResult {
	/**
	 * 合格
	 */
	QUALIFIED("合格"),
	/**
	 * 自行纠正
	 */
	SELFCORRECTION("自行纠正"),
	/**
	 * 撤销
	 */
	REVOKE("撤销");
	private final String title;

	private ReviewResult(String title) {
		this.title = title;
	}

	public String toString() {
		return this.title;
	}

	public static ReviewResult get(String str) {
		for (ReviewResult e : values()) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static ReviewResult getByName(String str) {
		for (ReviewResult e : values()) {
			if (e.name().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static List<Map<String, Object>> toList() {
		List<Map<String, Object>> list = new ArrayList<>();
		for (ReviewResult e : values()) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", e.name());
			map.put("title", e.toString());
			list.add(map);
		}
		return list;
	}

	public static Map<String, String> toMap(){
		Map<String, String> map = new LinkedHashMap<>();
		for (ReviewResult e : values()) {
			map.put(e.name(), e.toString());
		}
		return map;
	}
}
