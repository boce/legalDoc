package com.cdrundle.legaldoc.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author gang.li
 * 文件状态枚举
 */
public enum FileStatus {
	 
	/**
     * 生效
     */
    VALID("生效"),
    /**
     * 失效
     */
    INVALID("失效"),
    /**
     * 修订
     */
    MODIFY("修订"),
    
    /**
     * 撤销
     */
    REVOKE("撤销"),
    /**
     * 废止
     */
    ABOLISH("废止");
    
private final String title;
    
	private FileStatus(String title) 
	{
		this.title = title;
	}
	
	public String toString() 
    {  
    	 return this.title;  
    }  
    
    public static FileStatus get(String str) {  
    	 for (FileStatus e : values()) {  
    	     if(e.toString().equals(str)) {  
    	          return e;  
    	     }  
    	 }  
    	 return null;  
    }
    
    public static FileStatus getByName(String str) {
		for (FileStatus e : values()) {
			if (e.name().equals(str)) {
				return e;
			}
		}
		return null;
	}
    
    public static List<Map<String, Object>> toList()
    {
    	List<Map<String, Object>> list = new ArrayList<>();
    	for (FileStatus e : values()) 
    	{
    		Map<String, Object> map = new HashMap<>();
    		map.put("name", e.name());
    		map.put("title", e.toString());
    		list.add(map);
    	}
    	return list;
    }
    
    public static Map<String, String> toMap(){
		Map<String, String> map = new LinkedHashMap<>();
		for (FileStatus e : values()) {
			map.put(e.name(), e.toString());
		}
		return map;
	}
}
