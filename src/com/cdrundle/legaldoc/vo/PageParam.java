package com.cdrundle.legaldoc.vo;

import com.cdrundle.legaldoc.util.SysUtil;

/**
 * 接收easyui grid分页参数
 * @author xiaokui.li
 *
 */
public class PageParam {
	/**
	 * 查询页数
	 */
	private Integer page;
	/**
	 * 每页条数
	 */
	private Integer rows;
	
	/**
	 * 传过来的page从1开始，所以默认减1
	 * @return
	 */
	public Integer getPage() {
		if (page == null || page <= 0) {
			page = SysUtil.PAGE_INDEX;
		} else {
			page = page - 1;
		}
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getRows() {
		if (rows == null || rows <= 0) {
			rows = SysUtil.PAGE_SIZE;
		}
		return rows;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	
}
