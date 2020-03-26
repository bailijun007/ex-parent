/**
 * 
 */
package com.hp.sh.expv3.pc.vo;

import java.util.List;

import com.gitee.hupadev.commons.page.Page;

/**
 * DAO分页查询参数
 */
public class ExPageResult<T> extends Page{
	
	private List<T> list;
	
	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "&pageNo=" + super.getPageNo() + "&pageSize=" + super.getPageSize();
	}

}
