package com.cdrundle.legaldoc.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务类型
 * @author xiaokui.li
 *
 */
public enum TaskType {


    /**
     * 文件自动失效
     */
	FILEINVALIDATION("文件自动失效"),
    /**
     * 备案提醒
     */
	RECORDREMIND("备案提醒"),
	/**
     * 期满评估
     */
	FILEADJUST("期满评估");
    
    private final String title;

	private TaskType(String title) {
		this.title = title;
	}

	public String toString() {
		return this.title;
	}

	public static TaskType get(String str) {
		for (TaskType e : values()) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static TaskType getByName(String str) {
		for (TaskType e : values()) {
			if (e.name().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static List<Map<String, Object>> toList() {
		List<Map<String, Object>> list = new ArrayList<>();
		for (TaskType e : values()) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", e.name());
			map.put("title", e.toString());
			list.add(map);
		}
		return list;
	}

	public static Map<String, String> toMap(){
		Map<String, String> map = new LinkedHashMap<>();
		for (TaskType e : values()) {
			map.put(e.name(), e.toString());
		}
		return map;
	}

}
