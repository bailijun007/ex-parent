/**
 * 
 */
package com.hp.sh.expv3.commons.page;

import com.gitee.hupadev.commons.page.Page;

/**
 * DAO分页查询参数
 */
public class ExPage extends Page{

	/**
	 * 
	 */
	public ExPage() {
		super();
	}

	/**
	 * @param pageNo
	 * @param pageSize
	 * @param rowTotal
	 */
	public ExPage(int pageNo, int pageSize, Long rowTotal) {
		super(pageNo, pageSize, rowTotal);
	}

	/**
	 * @param pageNo
	 * @param pageSize
	 */
	public ExPage(int pageNo, int pageSize) {
		super(pageNo, pageSize);
	}
	
	public static ExPage getDefault(Integer pageNo, Integer pageSize){
		if(pageNo==null){
			pageNo = 1;
		}
		if(pageSize==null){
			pageSize = 10;
		}
		return getPage(pageNo, pageSize);
	}
	
	public static ExPage getPage(Integer pageNo, Integer pageSize){
		if(pageNo==null && pageSize==null){
			return null;
		}
		if(pageNo==null){
			pageNo = 1;
		}
		if(pageSize==null){
			pageSize = 10;
		}
		return new ExPage(pageNo, pageSize);
	}

	@Override
	public String toString() {
		return "&pageNo=" + super.getPageNo() + "&pageSize=" + super.getPageSize();
	}

}
