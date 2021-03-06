package com.cdrundle.legaldoc.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaokui.li 起草方式
 * 
 */
public enum DraftingMode
{
	/**
	 * 独立起草
	 */
	INDEPENDENT_DRAFTING("独立起草"),
	/**
	 * 联合起草
	 */
	UNION_DRAFTING("联合起草");
	private final String title;

	private DraftingMode(String title) {
		this.title = title;
	}

	public String toString() {
		return this.title;
	}

	public static DraftingMode get(String str) {
		for (DraftingMode e : values()) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static DraftingMode getByName(String str) {
		for (DraftingMode e : values()) {
			if (e.name().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static List<Map<String, Object>> toList() {
		List<Map<String, Object>> list = new ArrayList<>();
		for (DraftingMode e : values()) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", e.name());
			map.put("title", e.toString());
			list.add(map);
		}
		return list;
	}

	public static Map<String, String> toMap(){
		Map<String, String> map = new LinkedHashMap<>();
		for (DraftingMode e : values()) {
			map.put(e.name(), e.toString());
		}
		return map;
	}
}
