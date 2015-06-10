package com.cdrundle.legaldoc.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaokui.li 组织机构类型
 * 
 */
public enum OrgType
{
	/**
	 * 省人民政府
	 */
	PROVINCE_GOV("省人民政府"),
	/**
	 * 省工作部门
	 */
	PROVINCE_WORK_DEPART("省工作部门"),
	/**
	 * 市人民政府
	 */
	CITY_GOV("市人民政府"),
	/**
	 * 市工作部门
	 */
	CITY_WORK_DEPART("市工作部门"),
	/**
	 * 县人民政府
	 */
	COUNTY_GOV("县人民政府"),
	/**
	 * 县工作部门
	 */
	COUNTY_WORK_DEPART("县工作部门"),
	/**
	 * 乡人民政府
	 */
	COUNTRY_GOV("乡人民政府"),
	/**
	 * 垂直管理部门
	 */
	VERTICAL_MGT_DEPT("垂直管理部门");
	
    private final String title;
    
	private OrgType(String title) 
	{
		this.title = title;
	}
	
	public String toString() 
    {  
    	 return this.title;  
    }  
    
    public static OrgType get(String str) {  
    	 for (OrgType e : values()) {  
    	     if(e.toString().equals(str)) {  
    	          return e;  
    	     }  
    	 }  
    	 return null;  
    }
    
    public static List<Map<String, Object>> toList()
    {
    	List<Map<String, Object>> list = new ArrayList<>();
    	for (OrgType e : values()) 
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
		for (OrgType e : values()) {
			map.put(e.name(), e.toString());
		}
		return map;
	}
}
