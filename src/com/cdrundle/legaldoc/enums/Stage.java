package com.cdrundle.legaldoc.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XuBao 阶段枚举 2014年6月18日
 */
public enum Stage {
	/**
	 * 立项
	 */
	SETUP("立项"),
	/**
	 * 起草
	 */
	DRAFTING("起草"),
	/**
	 * 意见征求
	 */
	REQUEST_COMMENT("意见征求"),

	/**
	 * 意见征求 - 征求意见
	 */
	REQUEST_COMMENT_REQUEST("征求意见"),
	
	/**
	 * 意见征求 - 反馈意见
	 */
	REQUEST_COMMENT_FEEDBACK("反馈意见"),
	
	/**
	 * 意见征求 - 处理情况
	 */
	REQUEST_COMMENT_ADOPT("处理情况"),
	
	/**
	 * 意见征求 - 修改《征求意见稿》
	 */
	REQUEST_COMMENT_MODIFY("修改《征求意见稿》"),
	
	/**
	 * 合法性审查
	 */
	LEGAL_REVIEW("合法性审查"),
	
	/**
	 * 合法性审查-送审稿报送
	 */
	LEGAL_REVIEW_SUBMIT("送审稿报送"),
	
	/**
	 * 合法性审查-送审稿审查
	 */
	LEGAL_REVIEW_REVIEW("送审稿审查"),
	
	/**
	 * 合法性审查-送审稿修改
	 */
	LEGAL_REVIEW_MODIFY("送审稿修改"),

	/**
	 * 审议
	 */
	DELIBERATION("审议"),
	
	/**
	 * 审议-审议报请
	 */
	DELIBERATION_REQUEST("审议报请"),
	
	/**
	 * 审议-草案审议
	 */
	DELIBERATION_PROTOCOL("草案审议"),
	
	/**
	 * 审议-草案修改
	 */
	DELIBERATION_MODIFY("草案修改"),

	/**
	 * 发布
	 */
	PUBLISH("发布"),

	/**
	 * 备案
	 */
	RECORD("备案"),
	
	/**
	 * 备案-备案报送
	 */
	RECORD_REQUEST("备案报送"),
	
	/**
	 * 备案-备案审查
	 */
	RECORD_REVIEW("备案审查"),
	
	/**
	 * 备案-备案登记
	 */
	RECORD_REGISTER("备案登记");
	
	private final String title;

	private Stage(String title) {
		this.title = title;
	}

	public String toString() {
		return this.title;
	}

	public static Stage get(String str) {
		for (Stage e : values()) {
			if (e.toString().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static Stage getByName(String str) {
		for (Stage e : values()) {
			if (e.name().equals(str)) {
				return e;
			}
		}
		return null;
	}

	public static List<Map<String, Object>> toList() {
		List<Map<String, Object>> list = new ArrayList<>();
		for (Stage e : values()) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", e.name());
			map.put("title", e.toString());
			list.add(map);
		}
		return list;
	}
	
	public static Map<String, String> toMap(){
		Map<String, String> map = new LinkedHashMap<>();
		for (Stage e : values()) {
			map.put(e.name(), e.toString());
		}
		return map;
	}
}
