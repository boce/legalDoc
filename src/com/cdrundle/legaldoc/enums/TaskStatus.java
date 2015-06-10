package com.cdrundle.legaldoc.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务状态
 * @author xiaokui.li
 *
 */
public enum TaskStatus {


    /**
     * 进行中
     */
    RUNNING("进行中"),
    /**
     * 完成
     */
    COMPLETE("完成");
    
    private final String title;

	private TaskStatus(String title) {
		this.title = title;
	}

	public String toString() {
		return this.title;
	}

	public static TaskStatus get(String str) {
		for (TaskStatus e : values()) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static TaskStatus getByName(String str) {
		for (TaskStatus e : values()) {
			if (e.name().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static List<Map<String, Object>> toList() {
		List<Map<String, Object>> list = new ArrayList<>();
		for (TaskStatus e : values()) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", e.name());
			map.put("title", e.toString());
			list.add(map);
		}
		return list;
	}

	public static Map<String, String> toMap(){
		Map<String, String> map = new LinkedHashMap<>();
		for (TaskStatus e : values()) {
			map.put(e.name(), e.toString());
		}
		return map;
	}

}
