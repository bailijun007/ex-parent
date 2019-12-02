package com.hp.sh.expv3.base;

import java.util.List;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.commons.bean.PageResult;
import com.hp.sh.expv3.commons.page.ExPage;

/**
 * 
 *
 */
public class BaseApiAction {

	protected Page getPage(Integer pageNO) {
		return this.getPage(pageNO, 10);
	}
	
	protected Page getPage(Integer pageNo, Integer pageSize) {
		return this.getPage(pageNo, pageSize, null);
	}
	
	protected Page getPage(Integer pageNo, Integer pageSize, Long total) {
		if(pageNo==null){
			pageNo = 1;
		}
		if(pageSize==null){
			pageSize = 10;
		}
		Page page = new ExPage(pageNo, pageSize, total);
		return page;
	}

	protected <T> PageResult<T> getPageResult(Page page, List<T> list) {
		PageResult<T> result = new PageResult<T>();
		result.setList(list);
		result.setRowTotal(page.getRowTotal());
		result.setPageNo(page.getPageNo());
		result.setPageCount(page.getPageCount());
		return result;
	}

}
