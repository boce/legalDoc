package com.cdrundle.legaldoc.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaokui.li
 * 制定依据文件类型枚举
 */
public enum LegalBasisType
{
    LAW_AND_REG("法律法规"),
    
    STANDARD("规范标准"),
    
    RELATED_DOC("相关文件"),
    
    REFERENCE("借鉴");
    
    private final String title;

	private LegalBasisType(String title) {
		this.title = title;
	}

	public String toString() {
		return this.title;
	}

	public static LegalBasisType get(String str) {
		for (LegalBasisType e : values()) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static LegalBasisType getByName(String str) {
		for (LegalBasisType e : values()) {
			if (e.name().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static List<Map<String, Object>> toList() {
		List<Map<String, Object>> list = new ArrayList<>();
		for (LegalBasisType e : values()) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", e.name());
			map.put("title", e.toString());
			list.add(map);
		}
		return list;
	}
    
	public static Map<String, String> toMap(){
		Map<String, String> map = new LinkedHashMap<>();
		for (LegalBasisType e : values()) {
			map.put(e.name(), e.toString());
		}
		return map;
	}
}
